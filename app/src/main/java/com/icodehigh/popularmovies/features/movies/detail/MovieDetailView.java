package com.icodehigh.popularmovies.features.movies.detail;

import com.hannesdorfmann.mosby3.mvp.MvpView;
import com.icodehigh.popularmovies.model.Movie;

import java.util.List;

/**
 * Interface to be implemented to show a Movies List from the API
 */
public interface MovieDetailView extends MvpView {

    /**
     * Method called if the Api Call fails for a device error
     */
    void showSoftInternetError();

    /**
     * Method called if the API call fails for a server error
     */
    void showSoftServerError();

    /**
     * Method call when the presenter returns a list of trailers
     *
     * @param movies list to show on the activity
     */
    void setTrailersData(List<Movie> movies);


    /**
     * Method call when the presenter returns a list of reviews
     *
     * @param movies list to show on the activity
     */
    void setReviewsData(List<Movie> movies);


}
