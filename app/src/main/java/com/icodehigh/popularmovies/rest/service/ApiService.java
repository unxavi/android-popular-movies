package com.icodehigh.popularmovies.rest.service;


import android.support.annotation.NonNull;

import com.icodehigh.popularmovies.model.MovieResponse;
import com.icodehigh.popularmovies.model.ReviewResponse;
import com.icodehigh.popularmovies.model.VideoResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    int FIRST_PAGE_API = 1;

    /* API PATHS */
    String MOVIE_TOP_RATED_PATH = "movie/top_rated";
    String MOVIE_POPULAR_PATH = "movie/popular";
    String MOVIE_REVIEWS_PATH = "movie/{id}/reviews";
    String MOVIE_VIDEOS_PATH = "movie/{id}/videos";

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

    /**
     * Get movie trailer videos
     *
     * @param id     of the video to get the videos trailers for
     * @param apiKey of the service
     * @return response {@linkplain retrofit2.Call } with movie trailers of the API
     * with a  {@linkplain com.icodehigh.popularmovies.model.VideoResponse } object
     */
    @NonNull
    @GET(MOVIE_VIDEOS_PATH)
    Call<VideoResponse> getMovieVideos(
            @Path("id") int id,
            @Query(API_KEY_PARAM) String apiKey
    );

    /**
     * Get trailer reviews for the movie
     *
     * @param id     of the video to get the reviews for
     * @param apiKey of the service
     * @return response {@linkplain retrofit2.Call } with movie trailers of the API
     * with a  {@linkplain com.icodehigh.popularmovies.model.ReviewResponse } object
     */
    @NonNull
    @GET(MOVIE_REVIEWS_PATH)
    Call<ReviewResponse> getMovieReviews(
            @Path("id") int id,
            @Query(API_KEY_PARAM) String apiKey
    );

}
