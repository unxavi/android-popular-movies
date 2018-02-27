package com.icodehigh.popularmovies.model;


import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Movie {

    private static final String BASE_POSTER_PATH = "http://image.tmdb.org/t/p/w185";

    @SerializedName("vote_count")
    private int voteCount;

    @SerializedName("id")
    private int id;

    @SerializedName("video")
    private boolean video;

    @SerializedName("vote_average")
    private double voteAverage;

    @SerializedName("title")
    private String title;

    @SerializedName("popularity")
    private double popularity;

    @SerializedName("poster_path")
    private String posterPath;

    @SerializedName("original_language")
    private String originalLanguage;

    @SerializedName("original_title")
    private String originalTitle;

    @SerializedName("genre_ids")
    private List<Integer> genreIds = null;

    @SerializedName("backdrop_path")
    private String backdropPath;

    @SerializedName("adult")
    private boolean adult;

    @SerializedName("overview")
    private String overview;

    @SerializedName("release_date")
    private String releaseDate;

    public int getId() {
        return id;
    }

    public String getCompletePosterPath() {
        if (TextUtils.isEmpty(posterPath)) {
            return null;
        } else {
            return String.format("%s%s", BASE_POSTER_PATH, posterPath);
        }
    }


}
