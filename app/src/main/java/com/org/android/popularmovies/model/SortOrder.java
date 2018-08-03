package com.org.android.popularmovies.model;

/**
 * Created by toaderandrei on 28/10/15.
 */
public enum SortOrder {
    ASC("asc"),
    DESC("desc");

    private String sortOrder;

    private SortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
    }

    public String getSortOrder() {
        return sortOrder;
    }
}
