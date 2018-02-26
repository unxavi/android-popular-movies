package com.icodehigh.popularmovies.features.moviesFeed;


import android.support.annotation.NonNull;
import android.util.Log;

import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter;
import com.icodehigh.popularmovies.BuildConfig;
import com.icodehigh.popularmovies.model.Movie;
import com.icodehigh.popularmovies.model.MovieResponse;
import com.icodehigh.popularmovies.rest.ServiceGenerator;
import com.icodehigh.popularmovies.rest.service.ApiService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


class MoviesFeedPresenter extends MvpBasePresenter<MoviesFeedView> {


    private static final String TAG = "MoviesFeedPresenter";

    private int page = 1;

    private List<Movie> moviesList = new ArrayList<>();

    private Call<MovieResponse> popularMoviesCall;

    /**
     * Called when the view is ready, if the presenter has result from before, it will just
     * serve it to the view without making a network call
     */
    void onViewAttached() {
        if (moviesList == null || moviesList.isEmpty()) {
            getMovies();
            page = 1;
        } else {
            ifViewAttached(new ViewAction<MoviesFeedView>() {
                @Override
                public void run(@NonNull MoviesFeedView view) {
                    view.showMovieData(moviesList);
                }
            });
        }
    }


    /**
     * Get movies from API and return them to the view if any, otherwise return the corresponding
     * error to show the user what has happened.
     */
    void getMovies() {
        // Show to the user that the movies are being loaded
        if (page == 1) {
            ifViewAttached(new ViewAction<MoviesFeedView>() {
                @Override
                public void run(@NonNull MoviesFeedView view) {
                    view.showLoading();
                }
            });
        }
        // Create the ApiService object and enqueue the call
        ApiService service = ServiceGenerator.createService(ApiService.class);
        popularMoviesCall =
                service.getPopularMovies(BuildConfig.API_KEY_MOVIES, page);
        popularMoviesCall.enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(
                    @NonNull Call<MovieResponse> call,
                    @NonNull Response<MovieResponse> response) {
                MovieResponse movieResponse = response.body();
                if (response.isSuccessful() && movieResponse != null) {
                    // if the response is successful and the call
                    // contains data, show it to the user
                    onResponseSuccess(movieResponse);
                    // Increment the page number, so next time the next page is fetch
                    page++;
                } else {
                    // if the response is not successful show that there
                    // was a server error to the user and should try it later
                    showViewServerError();
                }
            }

            @Override
            public void onFailure(@NonNull Call<MovieResponse> call, @NonNull final Throwable t) {
                // if the response has a failure could be for many reasons, could be the lack
                // of connection and might need to retry later or it could fail due to some
                // mismatch of the response and Java models
                if (t instanceof IOException) {
                    showViewInternetError();
                } else {
                    // mismatch of the response and Java models probably
                    showViewServerError();
                    Log.e(TAG, "onFailure: ", t);
                }
            }
        });
    }


    /**
     * If response is success decide if should show the empty view or the list of movies
     *
     * @param movieResponse {@link MovieResponse} object with the response information.
     */
    private void onResponseSuccess(MovieResponse movieResponse) {
        final List<Movie> movies = movieResponse.getResults();
        moviesList.addAll(movies);
        if (movies.isEmpty()) {
            // if there are no objects on the respond show a view that
            // there are no movies available
            ifViewAttached(new ViewAction<MoviesFeedView>() {
                @Override
                public void run(@NonNull MoviesFeedView view) {
                    view.showEmptyState();
                }
            });
        } else {
            // if there are objects on the respond show a view that there are no movies available
            ifViewAttached(new ViewAction<MoviesFeedView>() {
                @Override
                public void run(@NonNull MoviesFeedView view) {
                    view.showMovieData(movies);
                }
            });

        }
    }

    /**
     * If the first page of the API is being fetch then show full screen error if not
     * Show a soft error to let the user know what has happen with the server
     */
    private void showViewServerError() {
        ifViewAttached(new ViewAction<MoviesFeedView>() {
            @Override
            public void run(@NonNull MoviesFeedView view) {
                if (page == 1) {
                    view.showServerError();
                } else {
                    view.showSoftServerError();
                }
            }
        });
    }

    /**
     * If the first page of the API is being fetch then show full screen error if not
     * Show a soft error to let the user know what has happen with the internet connection
     */
    private void showViewInternetError() {
        ifViewAttached(new ViewAction<MoviesFeedView>() {
            @Override
            public void run(@NonNull MoviesFeedView view) {
                if (page == 1) {
                    view.showInternetError();
                } else {
                    view.showSoftInternetError();
                }
            }
        });
    }


    /**
     * If the presenter gets destroy, cancel the network request if any
     */
    @Override
    public void destroy() {
        super.destroy();
        if (popularMoviesCall != null) {
            popularMoviesCall.cancel();
            popularMoviesCall = null;
        }
    }
}
