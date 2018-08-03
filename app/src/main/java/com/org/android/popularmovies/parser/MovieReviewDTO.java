package com.org.android.popularmovies.parser;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * MovieReview Data Transfer object.
 */
public class MovieReviewDTO {

    @Expose
    @SerializedName("id")
    protected String id;
    @Expose
    @SerializedName("author")
    protected String author;
    @Expose
    @SerializedName("content")
    protected String content;
    @Expose
    @SerializedName("url")
    private String url;

    public String getId() {
        return id;
    }

    public String getAuthor() {
        return author;
    }

    public String getUrl() {
        return url;
    }

    public String getContent() {
        return content;
    }
}
