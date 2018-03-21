package com.icodehigh.popularmovies.features.movies.detail;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.icodehigh.popularmovies.R;
import com.icodehigh.popularmovies.model.Movie;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieDetailActivity extends AppCompatActivity {

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
            Movie movie = intent.getExtras().getParcelable(Movie.class.getSimpleName());
            populateView(movie);
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

}
