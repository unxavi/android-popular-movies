package com.icodehigh.popularmovies.features.movies.feed;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.airbnb.lottie.LottieAnimationView;
import com.hannesdorfmann.mosby3.mvp.MvpActivity;
import com.icodehigh.popularmovies.R;
import com.icodehigh.popularmovies.data.FavoriteMovieContract;
import com.icodehigh.popularmovies.data.MoviesPreferences;
import com.icodehigh.popularmovies.features.movies.detail.MovieDetailActivity;
import com.icodehigh.popularmovies.model.Movie;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.icodehigh.popularmovies.data.FavoriteMovieContract.FavoriteMovieEntry;

public class MoviesFeedActivity extends MvpActivity<MoviesFeedView, MoviesFeedPresenter> implements
        MoviesFeedView,
        MoviesAdapter.MoviesAdapterOnClickHandler,
        AdapterView.OnItemSelectedListener,
        SwipeRefreshLayout.OnRefreshListener,
        LoaderManager.LoaderCallbacks<Cursor> {

    /*
     * how many columns will have the grid
     */
    private static final int SPAN_COUNT_RV = 2;

    /*
     * how many items can we have without loading from the api
     */
    private static final int VISIBLE_THRESHOLD = 10;

    /*
     * This ID will be used to identify the Loader responsible for loading favorites movies.
     * Please note that 77 was chosen arbitrarily. You can use whatever number you like, so long as
     * it is unique and consistent.
     */
    private static final int ID_FAV_MOVIES_LOADER = 77;
    private static final String TAG = "MoviesFeedActivity";

    @BindView(R.id.root_view)
    View rootView;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.contentView)
    SwipeRefreshLayout contentView;

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

    @BindView(R.id.movies_categories_sp)
    Spinner moviesCategoriesSp;


    private MoviesAdapter moviesAdapter;

    private int moviesListModePreference;

    /*
     * The position of the last item visible
     */
    private int lastVisibleItem;
    /*
     * Total amount of items on the RecyclerView
     */
    private int totalItemCount;
    /*
     * Is the presenter loading items from the API
     */
    private boolean isLoadingRV;
    /*
     * Has the presenter returned a page with no movies or last movies meaning that it
     * has no more movies to offer to show
     */
    private boolean isLastPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*
         * Ensures a loader is initialized and active. If the loader doesn't already exist, one is
         * created and (if the activity/fragment is currently started) starts the loader. Otherwise
         * the last created loader is re-used.
         */
        getSupportLoaderManager().initLoader(ID_FAV_MOVIES_LOADER, null, this);

        setContentView(R.layout.activity_movies_feed);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        initMovieSpinner();
        presenter.onViewAttached(moviesListModePreference);
        // Setup SwipeRefreshView
        contentView.setOnRefreshListener(this);
    }

    private void initMovieSpinner() {
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.movies_categories, R.layout.spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        moviesCategoriesSp.setAdapter(adapter);
        // Set listener to respond to events
        moviesCategoriesSp.setOnItemSelectedListener(this);
        // Set the position of the option saved on the spinner
        moviesListModePreference = MoviesPreferences.getMoviesListPreference(this);
        moviesCategoriesSp.setSelection(moviesListModePreference, true);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (moviesListModePreference != position) {
            MoviesPreferences.setMoviesListPreference(this, position);
            moviesListModePreference = position;
            refreshView();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @NonNull
    @Override
    public MoviesFeedPresenter createPresenter() {
        return new MoviesFeedPresenter();
    }

    @Override
    public void isPresenterLoadingData(boolean isLoading) {
        isLoadingRV = isLoading;
        contentView.setRefreshing(isLoading);
    }

    @Override
    public void showInternetError() {
        contentView.setVisibility(View.GONE);
        hideEmptyView();
        hideServerErrorView();
        hideLoadingView();
        noConnectionView.setVisibility(View.VISIBLE);
        noConnectionLottie.playAnimation();
    }

    @Override
    public void showSoftInternetError() {
        Snackbar snackbar = Snackbar.make(rootView, R.string.internet_error, Snackbar.LENGTH_LONG);
        snackbar.setAction(getString(R.string.retry), new RetryApiActionListener(getPresenter()));
        snackbar.show();
    }

    @Override
    public void showServerError() {
        contentView.setVisibility(View.GONE);
        hideEmptyView();
        hideLoadingView();
        hideNoConnectionView();
        serverErrorView.setVisibility(View.VISIBLE);
        serverErrorViewLottie.playAnimation();
    }

    @Override
    public void showSoftServerError() {
        Snackbar snackbar = Snackbar.make(rootView, R.string.server_error, Snackbar.LENGTH_LONG);
        snackbar.setAction(getString(R.string.retry), new RetryApiActionListener(getPresenter()));
        snackbar.show();
    }

    @Override
    public void showEmptyState() {
        contentView.setVisibility(View.GONE);
        hideNoConnectionView();
        hideServerErrorView();
        hideLoadingView();
        emptyView.setVisibility(View.VISIBLE);
        emptyViewLottie.playAnimation();
    }


    @Override
    public void showLoading() {
        contentView.setVisibility(View.GONE);
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
        contentView.setVisibility(View.VISIBLE);

    }

    @Override
    public void setMovieData(List<Movie> movies) {
        if (moviesAdapter == null) {
            moviesAdapter = new MoviesAdapter(this, movies, this);
            final GridLayoutManager gridLayoutManager = new GridLayoutManager(
                    this,
                    SPAN_COUNT_RV
            );
            moviesRv.setLayoutManager(gridLayoutManager);
            moviesRv.setHasFixedSize(true);
            moviesRv.setAdapter(moviesAdapter);
            moviesRv.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    totalItemCount = gridLayoutManager.getItemCount();
                    lastVisibleItem = gridLayoutManager.findLastVisibleItemPosition();
                    if ((!isLoadingRV && !isLastPage && totalItemCount <= (lastVisibleItem + VISIBLE_THRESHOLD))) {
                        moviesRv.post(new Runnable() {
                            @Override
                            public void run() {
                                presenter.getMovies();
                            }

                        });
                    }
                }
            });
        } else {
            moviesAdapter.addMovies(movies);
        }
        showMoviesView();
    }

    @Override
    public void onLastPage() {
        isLastPage = true;
    }

    @Override
    public void onRefresh() {
        refreshView();
    }


    @OnClick({R.id.no_connection_view, R.id.server_error_view})
    public void onViewClicked(View view) {
        presenter.getMovies();
    }

    @Override
    public void onMovieClick(Movie movie) {
        Intent intent = new Intent(this, MovieDetailActivity.class);
        intent.putExtra(Movie.class.getSimpleName(), movie);
        startActivity(intent);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, @Nullable Bundle args) {

        switch (loaderId) {

            case ID_FAV_MOVIES_LOADER:
                /* URI for all rows of favorites movies data */
                Uri uri = FavoriteMovieContract.FavoriteMovieEntry.CONTENT_URI;
                /* Sort order: Ascending by name */
                String sortOrder = FavoriteMovieContract.FavoriteMovieEntry.COLUMN_MOVIE_NAME + " ASC";

                return new CursorLoader(this,
                        uri,
                        null,
                        null,
                        null,
                        sortOrder);
            default:
                throw new RuntimeException("Loader Not Implemented: " + loaderId);
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        ArrayList<Movie> moviesFavList = new ArrayList<>();
        if (data.moveToFirst()) {
            do {
                Movie movie = new Movie();
                movie.setId(data.getInt(data.getColumnIndex(FavoriteMovieEntry.COLUMN_MOVIE_ID)));
                movie.setTitle(data.getString(data.getColumnIndex(FavoriteMovieEntry.COLUMN_MOVIE_NAME)));
                movie.setPosterPath(data.getString(data.getColumnIndex(FavoriteMovieEntry.COLUMN_POSTER_PATH)));
                movie.setReleaseDate(data.getString(data.getColumnIndex(FavoriteMovieEntry.COLUMN_RELEASE_DATE)));
                movie.setVoteAverage(data.getDouble(data.getColumnIndex(FavoriteMovieEntry.COLUMN_VOTE_AVERAGE)));
                movie.setOverview(data.getString(data.getColumnIndex(FavoriteMovieEntry.COLUMN_OVERVIEW)));
                moviesFavList.add(movie);
            } while (data.moveToNext());
        }
        presenter.setMoviesFavList(moviesFavList);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }

    @Override
    public void resetLoader() {
        getSupportLoaderManager().restartLoader(ID_FAV_MOVIES_LOADER, null, this);
    }

    @Override
    public void clearAdapter() {
        if (moviesAdapter != null) {
            moviesAdapter.clear();
        }
    }

    private void refreshView() {
        clearAdapter();
        isLastPage = false;
        presenter.resetPresenter(moviesListModePreference);
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
