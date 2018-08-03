package com.org.android.popularmovies.parser;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * GenreItem dto used for the gson parser.
 */
public class MovieGenresDTO {

    @Expose
    @SerializedName("genres")
    protected List<MovieGenreItemDTO> genres = new ArrayList<>();

    public List<MovieGenreItemDTO> getGenres() {
        return genres;
    }
}
