package com.org.android.popularmovies.adapters.model;

import com.org.android.popularmovies.utils.ComparisonUtils;

/**
 * Created by toaderandrei on 10/10/15.
 */
public class RowView<T> {
    private T object;

    public RowView(T object) {
        this.object = object;
    }

    public T getObject() {
        return this.object;
    }
}
