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
    View loadingView;

    @BindView(R.id.no_connection_view)
    View noConnectionView;

    @BindView(R.id.server_error_view)
    View serverErrorView;

    @BindView(R.id.empty_view)
    View emptyView;

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
        emptyView.setVisibility(View.GONE);
        moviesRv.setVisibility(View.GONE);
        serverErrorView.setVisibility(View.GONE);
        loadingView.setVisibility(View.GONE);
        noConnectionView.setVisibility(View.VISIBLE);
    }

    @Override
    public void showServerError() {
        emptyView.setVisibility(View.GONE);
        moviesRv.setVisibility(View.GONE);
        loadingView.setVisibility(View.GONE);
        noConnectionView.setVisibility(View.GONE);
        serverErrorView.setVisibility(View.VISIBLE);
    }

    @Override
    public void showEmptyState() {
        moviesRv.setVisibility(View.GONE);
        noConnectionView.setVisibility(View.GONE);
        serverErrorView.setVisibility(View.GONE);
        loadingView.setVisibility(View.GONE);
        emptyView.setVisibility(View.VISIBLE);
    }

    @Override
    public void showLoading() {
        emptyView.setVisibility(View.GONE);
        moviesRv.setVisibility(View.GONE);
        noConnectionView.setVisibility(View.GONE);
        serverErrorView.setVisibility(View.GONE);
        loadingView.setVisibility(View.VISIBLE);
    }

    @Override
    public void showMoviesView() {
        emptyView.setVisibility(View.GONE);
        noConnectionView.setVisibility(View.GONE);
        serverErrorView.setVisibility(View.GONE);
        loadingView.setVisibility(View.GONE);
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
}
