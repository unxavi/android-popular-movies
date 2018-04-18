package com.icodehigh.popularmovies.features.movies.detail;

import com.hannesdorfmann.mosby3.mvp.MvpView;
import com.icodehigh.popularmovies.model.ReviewResponse;
import com.icodehigh.popularmovies.model.VideoResponse;

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
     * Method call when the presenter returns a response for trailers
     *
     * @param videoResponse from API
     */
    void setTrailersData(VideoResponse videoResponse);


    /**
     * Method call when the presenter returns a response for reviews
     *
     * @param reviewResponse from API
     */
    void setReviewsData(ReviewResponse reviewResponse);


}
