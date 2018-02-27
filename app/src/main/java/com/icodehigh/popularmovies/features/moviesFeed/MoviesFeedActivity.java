package com.icodehigh.popularmovies.features.moviesFeed;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
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
import com.icodehigh.popularmovies.data.MoviesPreferences;
import com.icodehigh.popularmovies.model.Movie;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MoviesFeedActivity extends MvpActivity<MoviesFeedView, MoviesFeedPresenter> implements
        MoviesFeedView,
        MoviesAdapter.MoviesAdapterOnClickHandler,
        AdapterView.OnItemSelectedListener,
        SwipeRefreshLayout.OnRefreshListener {

    /*
     * how many columns will have the grid
     */
    private static final int SPAN_COUNT_RV = 2;

    /*
     * how many items can we have without loading from the api
     */
    private static final int VISIBLE_THRESHOLD = 10;

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
     * Has the API returned a page with no movies meaning that it has no more movies
     * to offer to show
     */
    private boolean isApiInLastPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        Snackbar.make(rootView, R.string.internet_error, Snackbar.LENGTH_SHORT).show();
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
        Snackbar.make(rootView, R.string.server_error, Snackbar.LENGTH_SHORT).show();
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
                    if ((!isLoadingRV && !isApiInLastPage && totalItemCount <= (lastVisibleItem + VISIBLE_THRESHOLD))) {
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
    public void onApiLastPage() {
        isApiInLastPage = true;
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
    public void onMovieClick(int id) {
        // TODO: 2/23/18
    }

    private void refreshView() {
        if (moviesAdapter != null) {
            moviesAdapter.clear();
        }
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
