package com.icodehigh.popularmovies.features.moviesFeed;


import com.hannesdorfmann.mosby3.mvp.MvpView;
import com.icodehigh.popularmovies.model.Movie;

import java.util.List;

public interface MoviesFeedView extends MvpView {

    void showInternetError();

    void showLoading();

    void showMovieData(List<Movie> movies);


}
