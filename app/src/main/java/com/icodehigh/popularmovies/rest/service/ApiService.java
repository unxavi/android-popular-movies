package com.icodehigh.popularmovies.rest.service;


import android.support.annotation.NonNull;

import com.icodehigh.popularmovies.model.MovieResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {

   int FIRST_PAGE_API = 1;

    /* API PATHS */
    String MOVIE_TOP_RATED_PATH = "movie/top_rated";
    String MOVIE_POPULAR_PATH = "movie/popular";

    /* API PARAMS */
    String API_KEY_PARAM = "api_key";
    String PAGE_PARAM = "page";


    /**
     * Get movies top rated
     *
     * @param apiKey of the service
     * @return response {@linkplain retrofit2.Call } with top rated movies of the API
     * with a  {@linkplain com.icodehigh.popularmovies.model.MovieResponse } object
     */
    @NonNull
    @GET(MOVIE_TOP_RATED_PATH)
    Call<MovieResponse> getTopRatedMovies(
            @Query(API_KEY_PARAM) String apiKey,
            @Query(PAGE_PARAM) int page
    );

    /**
     * Get movies by most popular
     *
     * @param apiKey of the service
     * @return response {@linkplain retrofit2.Call } with popular movies of the API
     * with a  {@linkplain com.icodehigh.popularmovies.model.MovieResponse } object
     */
    @NonNull
    @GET(MOVIE_POPULAR_PATH)
    Call<MovieResponse> getPopularMovies(
            @Query(API_KEY_PARAM) String apiKey,
            @Query(PAGE_PARAM) int page
    );
}
