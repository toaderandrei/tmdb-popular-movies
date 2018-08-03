package com.org.android.popularmovies.controller.factory.tasks;

import android.util.Log;

import com.org.android.popularmovies.controller.factory.SyncDbEvent;
import com.org.android.popularmovies.db.database.DatabaseAdapterUtils;
import com.org.android.popularmovies.db.interfaces.IDatabaseRow;
import com.org.android.popularmovies.model.GenreItem;
import com.org.android.popularmovies.model.MovieItem;
import com.org.android.popularmovies.synchronization.interfaces.SyncCallback;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Load favorite movies task from database.
 */
public class LoadFavoriteMoviesFromDb extends AbstractDbSyncTask<List<MovieItem>, List<MovieItem>> {

    protected List<MovieItem> items;
    protected List<MovieItem> favoriteItems = new ArrayList<>();

    public LoadFavoriteMoviesFromDb(int syncAction, List<MovieItem> item, SyncCallback<List<MovieItem>> callback) {
        super(syncAction, callback);
        this.items = item;
    }

    @Override
    public void synchronize() throws Exception {
        if (items != null) {
            List<IDatabaseRow> rows = getDbAdapter().getMovies(items);
            List<MovieItem> moviesFromDb = DatabaseAdapterUtils.getMovies(rows);
            loadAndSetGenreForMovies(moviesFromDb);
            boolean result = commit(moviesFromDb);
            if (result) {
                Log.d(TAG, "success");
                onTaskFinished(null, favoriteItems, getSyncEvent(), SyncDbResult.SUCCESS);
            } else {
                Log.d(TAG, "failure during retrieval of rows");
                onTaskFinished("Failed to insert some rows", null, getSyncEvent(), SyncDbResult.FAILURE);
            }
        } else {
            //load all of them.
            List<IDatabaseRow> rows = getDbAdapter().getAllMovies();
            List<MovieItem> moviesFromDb = DatabaseAdapterUtils.getMovies(rows);
            loadAndSetGenreForMovies(moviesFromDb);
            boolean result = commit(moviesFromDb);
            if (result) {
                Log.d(TAG, "success");
                onTaskFinished(null, favoriteItems, getSyncEvent(), SyncDbResult.SUCCESS);
            } else {
                Log.d(TAG, "failure during retrieval of rows");
                onTaskFinished("Failed to insert some rows", Collections.EMPTY_LIST, getSyncEvent(), SyncDbResult.FAILURE);
            }
        }
    }

    public void loadAndSetGenreForMovies(List<MovieItem> moviesFromDb) {
        for (MovieItem item : moviesFromDb) {
            List<IDatabaseRow> genreRowItems = getDbAdapter().getGenresByMovieId(String.valueOf(item.getId()));
            List<GenreItem> genreItems = DatabaseAdapterUtils.getGenreItemsFromGenreRows(genreRowItems);
            if (genreItems != null) {
                item.setGenres(genreItems);
            }
        }
    }

    @Override
    protected void onUpdate(String defaultMsg, List<MovieItem> obj, int event) {
        if (callback != null) {
            callback.onUpdateUI(defaultMsg, obj, event);
        }
    }

    @Override
    protected int getSyncEvent() {
        return SyncDbEvent.LOAD_FAVORITE_MOVIES;
    }

    @Override
    protected boolean commit(List<MovieItem> data) {

        for (MovieItem item : data) {
            if (item.isFavored()) {
                favoriteItems.add(item);
            }
        }
        if (favoriteItems != null && !favoriteItems.isEmpty()) {
            return true;
        }
        return false;
    }
}
