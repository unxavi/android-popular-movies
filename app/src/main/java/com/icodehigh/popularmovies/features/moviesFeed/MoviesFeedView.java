package com.icodehigh.popularmovies.features.moviesFeed;


import com.hannesdorfmann.mosby3.mvp.MvpView;
import com.icodehigh.popularmovies.model.Movie;

import java.util.List;

/**
 * Interface to be implemented to show a Movies List from the API
 */
public interface MoviesFeedView extends MvpView {

    /**
     * Method called if the Api Call fails for a device error
     */
    void showInternetError();

    /**
     * Method called if the API call fails for a server error
     */
    void showServerError();

    /**
     * Show the loading view when we open the app
     */
    void showLoading();

    /**
     * Show the recycler views with the movies and hide the loading view
     */
    void showMoviewView();

    /**
     * If the API call returns success but there are not movies this method is called
     */
    void showEmptyState();

    /**
     * Method call when the API rerutn sucess and there is a list of movies to show
     * @param movies list to show on the activity
     */
    void showMovieData(List<Movie> movies);


}
