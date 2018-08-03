package com.org.android.popularmovies.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by toaderandrei on 11/10/15.
 */
public enum MovieImageSize {

    W154("w154"),
    W92("w92"),
    W185("w185"),
    W300("w300"),
    W342("w342"),
    W500("w500"),
    W780("w780"),
    W1280("w1280"),
    ORIGINAL("original");

    private String img_size;

    private MovieImageSize(String imgSize) {
        this.img_size = imgSize;
    }

    public String getImgSize() {
        return img_size;
    }

    public List<String> getValues() {
        MovieImageSize[] list = MovieImageSize.values();
        List<String> movieList = new ArrayList<>();

        for (MovieImageSize posterSize : list) {
            movieList.add(posterSize.getImgSize());
        }
        return movieList;
    }
}
