package com.icodehigh.popularmovies.features.movies.detail;

import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hannesdorfmann.mosby3.mvp.MvpActivity;
import com.icodehigh.popularmovies.R;
import com.icodehigh.popularmovies.data.FavoriteMovieContract;
import com.icodehigh.popularmovies.model.Movie;
import com.icodehigh.popularmovies.model.ReviewResponse;
import com.icodehigh.popularmovies.model.Video;
import com.icodehigh.popularmovies.model.VideoResponse;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MovieDetailActivity extends
        MvpActivity<MovieDetailView, MovieDetailPresenter> implements
        MovieDetailView,
        TrailerAdapter.TrailersAdapterOnClickHandler,
        LoaderManager.LoaderCallbacks<Cursor> {

    /*
     * This ID will be used to identify the Loader responsible for loading one movie to check if
     * it's available on the fav movies database
     */
    private static final int ID_FAV_MOVIE_LOADER = 66;
    public static final String HTTP_WWW_YOUTUBE_COM_WATCH_V_S = "http://www.youtube.com/watch?v=%s";

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.title_tv)
    TextView titleTv;

    @BindView(R.id.movie_poster_iv)
    ImageView moviePosterIv;

    @BindView(R.id.synopsis_tv)
    TextView synopsisTv;

    @BindView(R.id.date_tv)
    TextView dateTv;

    @BindView(R.id.rate_tv)
    TextView rateTv;

    @BindView(R.id.fav_button)
    Button favButton;

    @BindView(R.id.recyclerViewTrailers)
    RecyclerView recyclerViewTrailers;

    @BindView(R.id.recyclerViewReviews)
    RecyclerView recyclerViewReviews;

    @BindView(R.id.root_view)
    CoordinatorLayout rootView;

    private Movie movie;

    private boolean isFavorite;

    private String firstVideoYouTubeKey;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        ButterKnife.bind(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Intent intent = getIntent();
        if (intent == null ||
                intent.getExtras() == null ||
                intent.getExtras().getParcelable(Movie.class.getSimpleName()) == null) {
            closeOnError();
        } else {
            movie = intent.getExtras().getParcelable(Movie.class.getSimpleName());
            if (movie != null) {
                /* This connects our Activity into the loader lifecycle. */
                getSupportLoaderManager().initLoader(ID_FAV_MOVIE_LOADER, null, this);
                presenter.onViewAttached(movie.getId());
                populateView(movie);
            } else {
                closeOnError();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate menu resource file.
        getMenuInflater().inflate(R.menu.detail_movie_menu, menu);
        // Return true to display menu
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (item.getItemId() == R.id.menu_item_share) {
            if (!TextUtils.isEmpty(firstVideoYouTubeKey)) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, String.format(HTTP_WWW_YOUTUBE_COM_WATCH_V_S, firstVideoYouTubeKey));
                sendIntent.setType("text/plain");
                startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.send_to)));
            } else {
                Toast.makeText(this, R.string.youtube_video_not_available, Toast.LENGTH_SHORT).show();

            }
        }
        return super.onOptionsItemSelected(item);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, @Nullable Bundle args) {
        switch (loaderId) {

            case ID_FAV_MOVIE_LOADER:
                /* URI for specific favorite movies data */
                Uri uri = FavoriteMovieContract.FavoriteMovieEntry.buildFavoriteMovieUriWithId(movie.getId());
                return new CursorLoader(this,
                        uri,
                        null,
                        null,
                        null,
                        null);

            default:
                throw new RuntimeException("Loader Not Implemented: " + loaderId);
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        isFavorite = data != null && data.moveToFirst();
        renderTextForFavButton();

    }


    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }

    private void populateView(Movie movie) {
        titleTv.setText(movie.getTitle());
        synopsisTv.setText(movie.getOverview());
        dateTv.setText(movie.getReleaseDate());
        rateTv.setText(String.format(getString(R.string.rate_text), String.valueOf(movie.getVoteAverage())));
        if (TextUtils.isEmpty(movie.getCompletePosterPath())) {
            moviePosterIv.setImageDrawable(
                    getResources().getDrawable(R.drawable.ic_movie_placeholder)
            );
        } else {
            Picasso.with(this)
                    .load(movie.getCompletePosterPath())
                    .placeholder(R.drawable.ic_movie_placeholder)
                    .error(R.drawable.ic_movie_placeholder)
                    .into(moviePosterIv);
        }
    }

    private void closeOnError() {
        finish();
        Toast.makeText(this, R.string.detail_error_message, Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.fav_button)
    public void onViewClicked() {
        if (!isFavorite) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(FavoriteMovieContract.FavoriteMovieEntry.COLUMN_MOVIE_ID, movie.getId());
            contentValues.put(FavoriteMovieContract.FavoriteMovieEntry.COLUMN_MOVIE_NAME, movie.getTitle());
            contentValues.put(FavoriteMovieContract.FavoriteMovieEntry.COLUMN_POSTER_PATH, movie.getPosterPath());
            contentValues.put(FavoriteMovieContract.FavoriteMovieEntry.COLUMN_RELEASE_DATE, movie.getReleaseDate());
            contentValues.put(FavoriteMovieContract.FavoriteMovieEntry.COLUMN_VOTE_AVERAGE, movie.getVoteAverage());
            contentValues.put(FavoriteMovieContract.FavoriteMovieEntry.COLUMN_OVERVIEW, movie.getOverview());
            Uri uri = getContentResolver().insert(FavoriteMovieContract.FavoriteMovieEntry.CONTENT_URI, contentValues);
            if (uri != null) {
                Toast.makeText(this, R.string.movie_add_favorite, Toast.LENGTH_LONG).show();
                isFavorite = true;
            }
        } else {
            /* URI for specific favorite movies data */
            Uri uri = FavoriteMovieContract.FavoriteMovieEntry.buildFavoriteMovieUriWithId(movie.getId());
            int delete = getContentResolver().delete(uri, null, null);
            if (delete > 0) {
                Toast.makeText(this, R.string.movie_deleted_from_fav, Toast.LENGTH_LONG).show();
                isFavorite = false;
            }
        }
        renderTextForFavButton();
    }

    private void renderTextForFavButton() {
        if (isFavorite) {
            favButton.setText(R.string.delete_from_favorite);
        } else {
            favButton.setText(R.string.add_to_favorite);
        }
    }

    @NonNull
    @Override
    public MovieDetailPresenter createPresenter() {
        return new MovieDetailPresenter();
    }

    @Override
    public void showSoftInternetError() {
        Snackbar snackbar = Snackbar.make(rootView, R.string.internet_error, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    @Override
    public void showSoftServerError() {
        Snackbar snackbar = Snackbar.make(rootView, R.string.server_error, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    @Override
    public void setTrailersData(VideoResponse videoResponse) {
        if (videoResponse != null && videoResponse.getResults() != null && !videoResponse.getResults().isEmpty()) {
            Video video = videoResponse.getResults().get(0);
            firstVideoYouTubeKey = video.getKey();
        }
        recyclerViewTrailers.setNestedScrollingEnabled(false);
        TrailerAdapter trailerAdapter =
                new TrailerAdapter(this, videoResponse.getResults(), this);
        recyclerViewTrailers.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewTrailers.setHasFixedSize(true);
        recyclerViewTrailers.setAdapter(trailerAdapter);

    }

    @Override
    public void setReviewsData(ReviewResponse reviewResponse) {
        recyclerViewReviews.setNestedScrollingEnabled(false);
        ReviewAdapter reviewAdapter = new ReviewAdapter(this, reviewResponse.getResults());
        recyclerViewReviews.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewReviews.setHasFixedSize(true);
        recyclerViewReviews.setAdapter(reviewAdapter);
    }

    @Override
    public void onTrailerClick(Video video) {
        openYoutubeVideo(video.getKey());
    }

    @Override
    public void openYoutubeVideo(String videoId) {
        Intent youtubeIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse(String.format("vnd.youtube:%s", videoId)));
        try {
            startActivity(youtubeIntent);
        } catch (ActivityNotFoundException ex) {
            Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse(String.format(HTTP_WWW_YOUTUBE_COM_WATCH_V_S, videoId)));
            startActivity(intent);
        }
    }
}
