package com.icodehigh.popularmovies.features.moviesFeed;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.airbnb.lottie.LottieAnimationView;
import com.hannesdorfmann.mosby3.mvp.MvpActivity;
import com.icodehigh.popularmovies.R;
import com.icodehigh.popularmovies.model.Movie;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MoviesFeedActivity extends MvpActivity<MoviesFeedView, MoviesFeedPresenter> implements
        MoviesFeedView,
        MoviesAdapter.MoviesAdapterOnClickHandler {

    @BindView(R.id.root_view)
    View rootView;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.movies_rv)
    RecyclerView moviesRv;

    @BindView(R.id.loading_view)
    LottieAnimationView loadingView;

    @BindView(R.id.no_connection_view)
    View noConnectionView;

    @BindView(R.id.no_connection_lottie)
    LottieAnimationView noConnectionLottie;

    @BindView(R.id.server_error_view)
    View serverErrorView;

    @BindView(R.id.server_error_lottie)
    LottieAnimationView serverErrorViewLottie;

    @BindView(R.id.empty_view)
    View emptyView;

    @BindView(R.id.empty_view_lottie)
    LottieAnimationView emptyViewLottie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies_feed);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_movies_feed, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @NonNull
    @Override
    public MoviesFeedPresenter createPresenter() {
        return new MoviesFeedPresenter();
    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter.getMovies();
    }

    @Override
    public void showInternetError() {
        moviesRv.setVisibility(View.GONE);
        hideEmptyView();
        hideServerErrorView();
        hideLoadingView();
        noConnectionView.setVisibility(View.VISIBLE);
        noConnectionLottie.playAnimation();
    }


    @Override
    public void showServerError() {
        moviesRv.setVisibility(View.GONE);
        hideEmptyView();
        hideLoadingView();
        hideNoConnectionView();
        serverErrorView.setVisibility(View.VISIBLE);
        serverErrorViewLottie.playAnimation();

    }

    @Override
    public void showEmptyState() {
        moviesRv.setVisibility(View.GONE);
        hideNoConnectionView();
        hideServerErrorView();
        hideLoadingView();
        emptyView.setVisibility(View.VISIBLE);
        emptyViewLottie.playAnimation();

    }


    @Override
    public void showLoading() {
        moviesRv.setVisibility(View.GONE);
        hideEmptyView();
        hideNoConnectionView();
        hideServerErrorView();
        loadingView.setVisibility(View.VISIBLE);
        loadingView.playAnimation();

    }

    @Override
    public void showMoviesView() {
        hideEmptyView();
        hideNoConnectionView();
        hideServerErrorView();
        hideLoadingView();
        moviesRv.setVisibility(View.VISIBLE);

    }

    @Override
    public void showMovieData(List<Movie> movies) {
        LinearLayoutManager layoutManager =
                new GridLayoutManager(
                        this,
                        2
                );
        moviesRv.setLayoutManager(layoutManager);
        moviesRv.setHasFixedSize(true);
        MoviesAdapter moviesAdapter = new MoviesAdapter(this, movies, this);
        moviesRv.setAdapter(moviesAdapter);
        showMoviesView();
    }

    @Override
    public void onMovieClick(int id) {
        // TODO: 2/23/18
    }

    private void hideEmptyView() {
        emptyView.setVisibility(View.GONE);
        if (emptyViewLottie.isAnimating()) {
            emptyViewLottie.pauseAnimation();
        }
    }

    private void hideLoadingView() {
        loadingView.setVisibility(View.GONE);
        if (loadingView.isAnimating()) {
            loadingView.pauseAnimation();
        }
    }

    private void hideNoConnectionView() {
        noConnectionView.setVisibility(View.GONE);
        if (noConnectionLottie.isAnimating()) {
            noConnectionLottie.pauseAnimation();
        }
    }

    private void hideServerErrorView() {
        serverErrorView.setVisibility(View.GONE);
        if (serverErrorViewLottie.isAnimating()) {
            serverErrorViewLottie.pauseAnimation();
        }
    }


}
