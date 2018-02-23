package com.icodehigh.popularmovies.features.moviesFeed;


import android.support.annotation.NonNull;

import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter;
import com.icodehigh.popularmovies.BuildConfig;
import com.icodehigh.popularmovies.model.Movie;
import com.icodehigh.popularmovies.model.MovieResponse;
import com.icodehigh.popularmovies.rest.ServiceGenerator;
import com.icodehigh.popularmovies.rest.service.ApiService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


class MoviesFeedPresenter extends MvpBasePresenter<MoviesFeedView> {


    /**
     * Get movies from API and return them to the view if any, otherwise return the corresponding
     * error to show the user what has happened.
     */
    void getMovies() {
        // Show to the user that the movies are being loaded
        ifViewAttached(new ViewAction<MoviesFeedView>() {
            @Override
            public void run(@NonNull MoviesFeedView view) {
                view.showLoading();
            }
        });
        // Create the ApiService object and enque the call
        ApiService service = ServiceGenerator.createService(ApiService.class);
        Call<MovieResponse> popularMoviesCall =
                service.getPopularMovies(BuildConfig.API_KEY_MOVIES);
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
                } else {
                    // if the response is not successful show that there
                    // was a server error to the user and should try it later
                    ifViewAttached(new ViewAction<MoviesFeedView>() {
                        @Override
                        public void run(@NonNull MoviesFeedView view) {
                            view.showServerError();
                        }
                    });
                }
            }

            @Override
            public void onFailure(@NonNull Call<MovieResponse> call, @NonNull Throwable t) {
                // if the response has a failure could be for many reasons, could be the lack
                // of connection and might need to retry later
                ifViewAttached(new ViewAction<MoviesFeedView>() {
                    @Override
                    public void run(@NonNull MoviesFeedView view) {
                        view.showInternetError();
                    }
                });
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
}
