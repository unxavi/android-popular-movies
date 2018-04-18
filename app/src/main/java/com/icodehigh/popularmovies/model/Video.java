package com.icodehigh.popularmovies.model;

import com.google.gson.annotations.SerializedName;

public class Video {

    @SerializedName("vote_count")
    private String id;

    @SerializedName("iso_639_1")
    private String iso6391;

    @SerializedName("iso_3166_1")
    private String iso31661;

    @SerializedName("key")
    private String key;

    @SerializedName("name")
    private String name;

    @SerializedName("site")
    private String site;

    @SerializedName("size")
    private int size;

    @SerializedName("type")
    private String type;

    public String getKey() {
        return key;
    }
}
