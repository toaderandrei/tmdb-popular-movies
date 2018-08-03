package com.org.android.popularmovies.parser;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * MovieReviews class that contains the transfer data objects from the request to the server.
 */
public class MovieReviewsDTO {

    @Expose
    protected long id;

    @Expose
    protected int page;

    @Expose
    @SerializedName("results")
    protected List<MovieReviewDTO> reviews;

    @Expose
    @SerializedName("total_pages")
    protected int totalPages;

    @Expose
    @SerializedName("total_results")
    protected int totalResults;

    public List<MovieReviewDTO> getReviews() {
        return reviews;
    }

    public int getTotalResults() {
        return totalResults;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public int getPage() {
        return page;
    }

    public long getId() {
        return id;
    }
}
