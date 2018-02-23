package com.icodehigh.popularmovies.features.moviesFeed;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
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
        Snackbar.make(
                rootView,
                "Check your internet connection and try again",
                Snackbar.LENGTH_LONG)
                .show();
    }

    @Override
    public void showServerError() {
        Snackbar.make(
                rootView,
                "There seems to be an error, try again later",
                Snackbar.LENGTH_LONG)
                .show();
    }

    @Override
    public void showEmptyState() {
        // TODO: 2/23/18
    }

    @Override
    public void showLoading() {
        // TODO: 2/23/18
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

    }

    @Override
    public void onMovieClick(int id) {
        // TODO: 2/23/18
    }
}
