package com.org.android.popularmovies.model;

/**
 * Created by toaderandrei on 28/10/15.
 */
public enum SortType {

    POPULARITY("popularity"),
    RELEASE_DATE("release_date"),
    ORIGINAL_TITLE("original_title"),
    AVERAGE_COUNT("vote_average"),
    VOTE_COUNT("vote_count");


    private String sortType;

    private SortType(String sortType) {
        this.sortType = sortType;
    }

    public String getSortType() {
        return sortType;
    }
}
