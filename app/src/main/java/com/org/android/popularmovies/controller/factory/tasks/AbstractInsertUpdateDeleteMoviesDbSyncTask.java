package com.org.android.popularmovies.controller.factory.tasks;

import android.database.SQLException;
import android.util.Log;

import com.org.android.popularmovies.controller.factory.SyncDbEvent;
import com.org.android.popularmovies.db.database.DatabaseAdapterUtils;
import com.org.android.popularmovies.db.interfaces.IDatabaseRow;
import com.org.android.popularmovies.db.model.MovieRow;
import com.org.android.popularmovies.model.MovieItem;
import com.org.android.popularmovies.synchronization.interfaces.SyncCallback;

import java.util.List;

/**
 * Task that deals with inserting, updating or deleting movies to the database.
 * The right operation is defined on an upper layer.
 */
public abstract class AbstractInsertUpdateDeleteMoviesDbSyncTask extends AbstractDbSyncTask<List<MovieItem>, List<IDatabaseRow>> {

    protected List<MovieItem> items;
    protected List<MovieItem> failedItems = null;

    public AbstractInsertUpdateDeleteMoviesDbSyncTask(int syncAction, List<MovieItem> item, SyncCallback<List<MovieItem>> callback) {
        super(syncAction, callback);
        this.items = item;
    }

    @Override
    public void synchronize() throws Exception {
        if (items != null) {
            List<IDatabaseRow> rows = DatabaseAdapterUtils.getMovieRows(items);
            boolean result = commit(rows);
            if (result) {
                Log.d(TAG, "success");
                onTaskFinished(null, items, getSyncEvent(), SyncDbResult.SUCCESS);
            } else {
                Log.d(TAG, "failed to insert some rows");
                onTaskFinished("Failed to insert some rows", failedItems, getSyncEvent(), SyncDbResult.FAILURE);
            }
        } else {
            throw new IllegalArgumentException("Item cannot be null");
        }
    }

    @Override
    protected void onUpdate(String defaultMsg, List<MovieItem> obj, int event) {
        if (callback != null) {
            callback.onUpdateUI(defaultMsg, obj, event);
        }
    }
}
