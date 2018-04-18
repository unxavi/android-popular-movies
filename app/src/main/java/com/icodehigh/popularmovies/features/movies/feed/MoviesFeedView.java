package com.icodehigh.popularmovies.features.movies.feed;


import com.hannesdorfmann.mosby3.mvp.MvpView;
import com.icodehigh.popularmovies.model.Movie;

import java.util.List;

/**
 * Interface to be implemented to show a Movies List
 */
interface MoviesFeedView extends MvpView {

    /**
     * Method called if the Api Call fails for a device error
     * Using the complete screen, should be called if there are no previous results
     */
    void showInternetError();

    /**
     * Method called if the Api Call fails for a device error and there are previous results
     */
    void showSoftInternetError();

    /**
     * Method called if the API call fails for a server error
     * Using the complete screen, should be called if there are no previous results
     */
    void showServerError();

    /**
     * Method called if the API call fails for a server error and there are previous results
     */
    void showSoftServerError();

    /**
     * Show the loading view when we open the app
     * Using the complete screen, should be called if there are no previous results
     */
    void showLoading();

    /**
     * Show the recycler views with the movies and hide the loading view
     */
    void showMoviesView();

    /**
     * If the API call returns success but there are not movies this method is called
     * Using the complete screen, should be called if there are no previous results
     */
    void showEmptyState();

    /**
     * Method call when the presenter returns a list of movies
     *
     * @param movies list to show on the activity
     */
    void setMovieData(List<Movie> movies);


    /**
     * Called by the presenter when it knows that it can't load more movies
     */
    void onLastPage();


    /**
     * Helper method to inform the view that the presenter is loading data or if it has finish
     *
     * @param isLoading boolean if the presenter is loading or not
     */
    void isPresenterLoadingData(boolean isLoading);

    /**
     * Resets the loader to get again the data from the DB of favorites movies
     */
    void resetLoader();

    /**
     * Clear Recycler View adapter of any possible view
     */
    void clearAdapter();
}
