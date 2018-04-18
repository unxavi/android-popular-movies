package com.icodehigh.popularmovies.features.movies.detail;

import android.support.annotation.NonNull;
import android.util.Log;

import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter;
import com.icodehigh.popularmovies.BuildConfig;
import com.icodehigh.popularmovies.model.ReviewResponse;
import com.icodehigh.popularmovies.model.VideoResponse;
import com.icodehigh.popularmovies.rest.ServiceGenerator;
import com.icodehigh.popularmovies.rest.service.ApiService;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Interface to be implemented to show a Movies List from the API
 */
class MovieDetailPresenter extends MvpBasePresenter<MovieDetailView> {

    private static final String TAG = "MovieDetailPresenter";
    /*
     * Trailer response from API
     */
    private VideoResponse videoResponse;

    /*
     * Reviews response from API
     */
    private ReviewResponse reviewResponse;

    /*
     * Api Service for making request to the API
     */
    private ApiService service;

    /*
     * Network request to get trailers
     */
    private Call<VideoResponse> videoResponseCall;


    /**
     * Called when the view is ready, if the presenter has result from before, it will just
     * serve it to the view without making network calls
     */
    void onViewAttached(int movieId) {
        this.service = ServiceGenerator.createService(ApiService.class);
        if (videoResponse != null) {
            ifViewAttached(new ViewAction<MovieDetailView>() {
                @Override
                public void run(@NonNull MovieDetailView view) {
                    view.setTrailersData(videoResponse);
                }
            });
        } else {
            getTrailersFromAPI(movieId);
        }

        if (reviewResponse != null) {
            ifViewAttached(new ViewAction<MovieDetailView>() {
                @Override
                public void run(@NonNull MovieDetailView view) {
                    view.setReviewsData(reviewResponse);
                }
            });
        } else {
            // TODO: 4/18/18
        }
    }

    private void getTrailersFromAPI(int movieId) {
        videoResponseCall =
                service.getMovieVideos(movieId, BuildConfig.API_KEY_MOVIES);
        videoResponseCall.enqueue(new Callback<VideoResponse>() {
            @Override
            public void onResponse(@NonNull Call<VideoResponse> call, @NonNull Response<VideoResponse> response) {
                final VideoResponse body = response.body();
                if (response.isSuccessful() && body != null) {
                    videoResponse = body;
                    ifViewAttached(new ViewAction<MovieDetailView>() {
                        @Override
                        public void run(@NonNull MovieDetailView view) {
                            view.setTrailersData(videoResponse);
                        }
                    });
                } else {
                    showServerError();
                }
            }

            @Override
            public void onFailure(@NonNull Call<VideoResponse> call, @NonNull Throwable t) {
                // if the response has a failure could be for many reasons, could be the lack
                // of connection and might need to retry later or it could fail due to some
                // mismatch of the response and Java models
                if (t instanceof IOException) {
                    showInternetError();
                } else {
                    // mismatch of the response and Java models probably
                    showServerError();
                    Log.e(TAG, "onFailure: ", t);
                }
            }
        });

    }

    private void showServerError() {
        ifViewAttached(new ViewAction<MovieDetailView>() {
            @Override
            public void run(@NonNull MovieDetailView view) {
                view.showSoftServerError();
            }
        });
    }

    private void showInternetError() {
        ifViewAttached(new ViewAction<MovieDetailView>() {
            @Override
            public void run(@NonNull MovieDetailView view) {
                view.showSoftInternetError();
            }
        });
    }

    /**
     * If the presenter gets destroy, cancel the network request if any
     */
    @Override
    public void destroy() {
        super.destroy();
        if (videoResponseCall != null) {
            videoResponseCall.cancel();
            videoResponseCall = null;
        }
    }
}
