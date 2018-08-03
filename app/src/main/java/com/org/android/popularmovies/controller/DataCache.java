package com.org.android.popularmovies.controller;

import android.graphics.Movie;
import android.support.annotation.NonNull;

import com.org.android.popularmovies.model.GenreItem;
import com.org.android.popularmovies.model.MovieItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Movie data cache.
 */
public class DataCache {

    private static DataCache instance;

    private List<MovieItem> movieList = Collections.synchronizedList(new ArrayList<MovieItem>());
    private List<GenreItem> genreItemList = Collections.synchronizedList(new ArrayList<GenreItem>());
    private List<MovieItem> favoriteItemList = Collections.synchronizedList(new ArrayList<MovieItem>());

    protected DataCache() {/* nothing */}

    public void addMoviesToCache(@NonNull List<MovieItem> movieItems) {
        synchronized (movieList) {
            for (MovieItem item : movieItems) {
                if (!movieList.contains(item)) {
                    movieList.add(item);
                }
            }
        }
    }

    public List<MovieItem> getMovieList() {
        return new ArrayList<>(movieList);
    }

    public void clearCache() {
        synchronized (movieList) {
            if (movieList != null && !movieList.isEmpty()) {
                movieList.clear();
            }
        }
    }

    public void saveGenres(List<GenreItem> items) {
        for (GenreItem item : items) {
            if (!genreItemList.contains(item)) {
                genreItemList.add(item);
            }
        }
    }

    public GenreItem getGenreItem(int id) {
        synchronized (genreItemList) {
            for (GenreItem item : genreItemList) {
                if (item.getId() == id) {
                    return item;
                }
            }
        }
        return null;
    }

    public List<GenreItem> getGenres() {
        synchronized (genreItemList) {
            return genreItemList;
        }
    }


    public List<MovieItem> getFavorites() {
        synchronized (favoriteItemList) {
            return favoriteItemList;
        }
    }

    public void saveFavorites(List<MovieItem> items) {
        for (MovieItem item : items) {
            if (!favoriteItemList.contains(item)) {
                favoriteItemList.add(item);
            }
        }
    }


    public void saveFavorite(MovieItem item) {
        synchronized (favoriteItemList) {
            if (!favoriteItemList.contains(item)) {
                favoriteItemList.add(item);
            }
        }
    }
}
