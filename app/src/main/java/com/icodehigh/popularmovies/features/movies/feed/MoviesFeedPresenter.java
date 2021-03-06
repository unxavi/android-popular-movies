package com.icodehigh.popularmovies.features.movies.feed;


import android.support.annotation.NonNull;
import android.util.Log;

import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter;
import com.icodehigh.popularmovies.BuildConfig;
import com.icodehigh.popularmovies.data.MoviesPreferences;
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


    /*
     * Current page to load from the API
     */
    private int page = ApiService.FIRST_PAGE_API;

    /*
     * Movie list fetched from the API
     */
    private List<Movie> moviesList = new ArrayList<>();

    /*
     * Movie list fetched from the DB
     */
    private List<Movie> moviesFavList = new ArrayList<>();

    /*
     * API Call to enqueue on Retrofit
     */
    private Call<MovieResponse> popularMoviesCall;

    /*
     * Movies list type to query the API
     */
    @MoviesPreferences.MoviesListMode
    private int moviesListMode;

    /*
     * API Service to make Retrofit calls
     */
    private ApiService service;


    /**
     * Called if there is a need to reset the presenter, like switching from one movie list mode
     * to another or just refresh over the API
     *
     * @param movieListMode {@link MoviesPreferences.MoviesListMode} to load from API
     */
    void resetPresenter(@MoviesPreferences.MoviesListMode int movieListMode) {
        this.moviesListMode = movieListMode;
        this.moviesList.clear();
        this.page = ApiService.FIRST_PAGE_API;
        if (moviesListMode == MoviesPreferences.POPULAR_MOVIES_LIST || moviesListMode == MoviesPreferences.TOP_RATED_MOVIES_LIST) {
            getMovies();
        } else if (moviesListMode == MoviesPreferences.FAVORITES_MOVIES_LIST) {
            ifViewAttached(new ViewAction<MoviesFeedView>() {
                @Override
                public void run(@NonNull MoviesFeedView view) {
                    view.resetLoader();
                }
            });
        }
    }

    /**
     * Called when the view is ready, if the presenter has result from before, it will just
     * serve it to the view without making a network call
     */
    void onViewAttached(@MoviesPreferences.MoviesListMode int movieListMode) {
        this.moviesListMode = movieListMode;
        this.service = ServiceGenerator.createService(ApiService.class);
        if (moviesListMode == MoviesPreferences.POPULAR_MOVIES_LIST || moviesListMode == MoviesPreferences.TOP_RATED_MOVIES_LIST) {
            if (moviesList == null || moviesList.isEmpty()) {
                this.page = ApiService.FIRST_PAGE_API;
                getMovies();
            } else {
                ifViewAttached(new ViewAction<MoviesFeedView>() {
                    @Override
                    public void run(@NonNull MoviesFeedView view) {
                        view.setMovieData(moviesList);
                    }
                });
            }
        }
    }

    /**
     * Set the favorites movies from the db and if selected in the view mode show them
     *
     * @param moviesFavList array list
     */
    public void setMoviesFavList(final List<Movie> moviesFavList) {
        this.moviesFavList = moviesFavList;
        if (moviesListMode == MoviesPreferences.FAVORITES_MOVIES_LIST) {
            showFavMoviesList();
        }
    }

    private void showFavMoviesList() {
        ifViewAttached(new ViewAction<MoviesFeedView>() {
            @Override
            public void run(@NonNull MoviesFeedView view) {
                view.clearAdapter();
                view.isPresenterLoadingData(false);
                if (moviesFavList.isEmpty()) {
                    view.showEmptyState();
                } else {
                    view.setMovieData(new ArrayList<>(moviesFavList));
                }
                view.onLastPage();
            }
        });
    }

    /**
     * Get movies from API and return them to the view if any, otherwise return the corresponding
     * error to show the user what has happened.
     */
    void getMovies() {
        // TODO: 4/17/18 getting movies should be handle by a repository, to get from the DB or API
        if (moviesListMode == MoviesPreferences.POPULAR_MOVIES_LIST || moviesListMode == MoviesPreferences.TOP_RATED_MOVIES_LIST) {
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
            setApiCallForMovieList();
            if (popularMoviesCall != null) {
                // Inform the view that the presenter is loading data
                ifViewAttached(new ViewAction<MoviesFeedView>() {
                    @Override
                    public void run(@NonNull MoviesFeedView view) {
                        view.isPresenterLoadingData(true);
                    }
                });
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
        }
    }

    /**
     * Helper method to set the API Call base on the movie list that they user wants to load
     */
    private void setApiCallForMovieList() {
        if (MoviesPreferences.POPULAR_MOVIES_LIST == moviesListMode) {
            popularMoviesCall =
                    service.getPopularMovies(BuildConfig.API_KEY_MOVIES, page);
        } else if (MoviesPreferences.TOP_RATED_MOVIES_LIST == moviesListMode) {
            popularMoviesCall =
                    service.getTopRatedMovies(BuildConfig.API_KEY_MOVIES, page);
        }
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
                    view.isPresenterLoadingData(false);
                    if (page == ApiService.FIRST_PAGE_API) {
                        view.showEmptyState();
                    } else {
                        view.onLastPage();
                    }
                }
            });
        } else {
            // if there are objects on the respond show a view that there are no movies available
            ifViewAttached(new ViewAction<MoviesFeedView>() {
                @Override
                public void run(@NonNull MoviesFeedView view) {
                    view.isPresenterLoadingData(false);
                    view.setMovieData(movies);
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
                view.isPresenterLoadingData(false);
                if (page == ApiService.FIRST_PAGE_API) {
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
                view.isPresenterLoadingData(false);
                if (page == ApiService.FIRST_PAGE_API) {
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
