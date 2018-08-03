package com.org.android.popularmovies.parser;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * POJO class for MovieTrailers
 */

public final class MovieVideosDTO {
    @Expose
    @SerializedName("id")
    private long id;
    @Expose
    @SerializedName("results")
    private MovieVideoDTO results[];

    public MovieVideosDTO(long id, MovieVideoDTO[] results) {
        this.id = id;
        this.results = results;
    }

    public MovieVideoDTO[] getResults() {
        return results;
    }
}
