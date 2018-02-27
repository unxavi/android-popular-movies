package com.icodehigh.popularmovies.features.moviesFeed;

import android.view.View;

class RetryApiActionListener implements View.OnClickListener {

    private MoviesFeedPresenter moviesFeedPresenter;

    public RetryApiActionListener(MoviesFeedPresenter moviesFeedPresenter) {
        this.moviesFeedPresenter = moviesFeedPresenter;
    }

    @Override
    public void onClick(View v) {
        this.moviesFeedPresenter.getMovies();
    }
}
