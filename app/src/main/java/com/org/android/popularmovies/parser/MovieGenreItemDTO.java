package com.org.android.popularmovies.parser;

import com.google.gson.annotations.SerializedName;

/**
 * GenreItem dto class.
 */
public class MovieGenreItemDTO {

    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;


    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
