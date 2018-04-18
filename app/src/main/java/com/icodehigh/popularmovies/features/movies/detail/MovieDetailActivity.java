package com.icodehigh.popularmovies.features.movies.detail;

import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
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
        MvpActivity<MovieDetailView, MovieDetailPresenter> implements MovieDetailView, TrailerAdapter.TrailersAdapterOnClickHandler {

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

    private Movie movie;


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
                populateView(movie);
                presenter.onViewAttached(movie.getId());
            } else {
                closeOnError();
            }
        }
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
        ContentValues contentValues = new ContentValues();
        contentValues.put(FavoriteMovieContract.FavoriteMovieEntry.COLUMN_MOVIE_ID, movie.getId());
        contentValues.put(FavoriteMovieContract.FavoriteMovieEntry.COLUMN_MOVIE_NAME, movie.getTitle());
        contentValues.put(FavoriteMovieContract.FavoriteMovieEntry.COLUMN_POSTER_PATH, movie.getPosterPath());
        contentValues.put(FavoriteMovieContract.FavoriteMovieEntry.COLUMN_RELEASE_DATE, movie.getReleaseDate());
        contentValues.put(FavoriteMovieContract.FavoriteMovieEntry.COLUMN_VOTE_AVERAGE, movie.getVoteAverage());
        contentValues.put(FavoriteMovieContract.FavoriteMovieEntry.COLUMN_OVERVIEW, movie.getOverview());
        Uri uri = getContentResolver().insert(FavoriteMovieContract.FavoriteMovieEntry.CONTENT_URI, contentValues);
        if (uri != null) {
            Toast.makeText(this, uri.toString(), Toast.LENGTH_LONG).show();
        }
    }

    @NonNull
    @Override
    public MovieDetailPresenter createPresenter() {
        return new MovieDetailPresenter();
    }

    @Override
    public void showSoftInternetError() {
        // TODO: 4/18/18
    }

    @Override
    public void showSoftServerError() {
        // TODO: 4/18/18
    }

    @Override
    public void setTrailersData(VideoResponse videoResponse) {
        recyclerViewTrailers.setNestedScrollingEnabled(false);
        TrailerAdapter trailerAdapter =
                new TrailerAdapter(this, videoResponse.getResults(), this);
        recyclerViewTrailers.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewTrailers.setHasFixedSize(true);
        recyclerViewTrailers.setAdapter(trailerAdapter);

    }

    @Override
    public void setReviewsData(ReviewResponse reviewResponse) {
        // TODO: 4/18/18
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
                    Uri.parse(String.format("http://www.youtube.com/watch?v=%s", videoId)));
            startActivity(intent);
        }
    }
}
