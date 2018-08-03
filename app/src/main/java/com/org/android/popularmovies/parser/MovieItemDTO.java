package com.org.android.popularmovies.parser;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by toaderandrei on 11/10/15.
 */
public class MovieItemDTO {

    @SerializedName("adult")
    @Expose
    private boolean adult;
    @SerializedName("backdrop_path")
    @Expose
    private String backdrop_path;
    @SerializedName("genre_ids")
    @Expose
    private List<Integer> genre_ids = new ArrayList<Integer>();
    @SerializedName("id")
    @Expose
    private long id;
    @SerializedName("original_language")
    @Expose
    private String original_lang;
    @SerializedName("original_title")
    @Expose
    private String original_title;
    @SerializedName("overview")
    @Expose
    private String overview;
    @SerializedName("release_date")
    @Expose
    private String release_date;
    @SerializedName("poster_path")
    @Expose
    private String poster_path;
    @SerializedName("popularity")
    @Expose
    private double popularity;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("video")
    @Expose
    private boolean video;
    @SerializedName("vote_average")
    @Expose
    private double vote_avg;
    @SerializedName("vote_count")
    @Expose
    private double vote_count;

    public boolean isAdult() {
        return adult;
    }

    public String getBackdrop_path() {
        return backdrop_path;
    }

    public List<Integer> getGenre_ids() {
        return genre_ids;
    }

    public long getId() {
        return id;
    }

    public String getOriginal_lang() {
        return original_lang;
    }

    public String getOriginal_title() {
        return original_title;
    }

    public String getOverview() {
        return overview;
    }

    public String getRelease_date() {
        return release_date;
    }

    public String getPoster_path() {
        return poster_path;
    }

    public double getPopularity() {
        return popularity;
    }

    public String getTitle() {
        return title;
    }

    public boolean isVideo() {
        return video;
    }

    public double getVoteAverage() {
        return vote_avg;
    }

    public double getVote_count() {
        return vote_count;
    }
}
