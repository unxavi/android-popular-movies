package com.icodehigh.popularmovies.model;



import com.google.gson.annotations.SerializedName;

public class MovieResponse {

    @SerializedName("page")
    private int page;

    @SerializedName("total_results")
    private int totalResults;

    @SerializedName("total_pages")
    private int totalPages;

    @SerializedName("results")
    private Movie results;


}
