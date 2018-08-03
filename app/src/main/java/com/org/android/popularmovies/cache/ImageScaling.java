package com.org.android.popularmovies.cache;

/**
 * Created by toaderandrei on 15/10/15.
 */
public enum ImageScaling {

    HALF(0.5),
    QUARTER(0.25),
    BEST(0.90);

    private Double scaling;

    private ImageScaling(Double scale) {
        this.scaling = scale;
    }

    public Double getScaling() {
        return this.scaling;
    }
}
