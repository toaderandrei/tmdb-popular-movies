package com.org.android.popularmovies.parser;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Class that contains the list of all the data transfer object movies.
 */
public final class MoviesDTO {
    @SerializedName("page")
    @Expose
    private long page;
    @SerializedName("results")
    @Expose
    private MovieItemDTO results[];
    @SerializedName("total_pages")
    @Expose
    private long total_pages;
    @SerializedName("total_results")
    @Expose
    private long total_results;

    public long getPage() {
        return page;
    }

    public MovieItemDTO[] getResults() {
        return results;
    }

    public long getTotal_pages() {
        return total_pages;
    }

    public long getTotal_results() {
        return total_results;
    }
}
