package com.org.android.popularmovies.controller.factory.tasks;

import com.org.android.popularmovies.controller.factory.SyncDbEvent;
import com.org.android.popularmovies.db.database.DatabaseAdapterUtils;
import com.org.android.popularmovies.db.interfaces.IDatabaseRow;
import com.org.android.popularmovies.model.MovieItem;
import com.org.android.popularmovies.synchronization.interfaces.SyncCallback;

import java.util.List;

/**
 * Task for deleting the movies from the database.
 * */
public class DeleteMoviesDbSyncTask extends AbstractInsertUpdateDeleteMoviesDbSyncTask {

    public DeleteMoviesDbSyncTask(int syncAction, List<MovieItem> item, SyncCallback<List<MovieItem>> callback) {
        super(syncAction, item, callback);
    }

    @Override
    protected boolean commit(List<IDatabaseRow> data) {
        List<IDatabaseRow> rows = getDbAdapter().deleteRows(data);
        if (rows == null || rows.isEmpty()) {
            return true;
        }
        failedItems = DatabaseAdapterUtils.getMovies(rows);
        return false;
    }

    @Override
    protected int getSyncEvent() {
        return SyncDbEvent.DELETE_MOVIES;
    }
}