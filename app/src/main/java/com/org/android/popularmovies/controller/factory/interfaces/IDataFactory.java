package com.org.android.popularmovies.controller.factory.interfaces;

import com.org.android.popularmovies.model.GenreItem;
import com.org.android.popularmovies.model.MovieItem;
import com.org.android.popularmovies.synchronization.interfaces.SyncCallback;

import java.util.List;

/**
 * interface that describes the database operations.
 */
public interface IDataFactory {
    void insertMovieToDb(MovieItem item, SyncCallback<MovieItem> callback);

    void updateMoviesToDb(List<MovieItem> items, SyncCallback<List<MovieItem>> callback);

    void deleteMoviesFromDb(List<MovieItem> items, SyncCallback<List<MovieItem>> callback);

    void insertOrUpdateMovieToDb(MovieItem item, SyncCallback<MovieItem> callback);

    void loadMovieStatusFromDb(MovieItem item, SyncCallback<MovieItem> mCallback);

    void loadFavoriteMoviesFromDb(List<MovieItem> items, SyncCallback<List<MovieItem>> mCallback);

    void insertOrUpdateGenresToDb(List<GenreItem> genreItems);

    void loadGenres(SyncCallback<List<GenreItem>> callback);
}
