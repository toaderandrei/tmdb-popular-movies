package com.org.android.popularmovies.controller.factory.tasks;

import android.database.SQLException;

import com.org.android.popularmovies.db.database.DatabaseAdapterUtils;
import com.org.android.popularmovies.db.interfaces.IDatabaseRow;
import com.org.android.popularmovies.db.model.MovieRow;
import com.org.android.popularmovies.model.MovieItem;
import com.org.android.popularmovies.synchronization.interfaces.SyncCallback;

import java.util.List;


/**
 * Task for inserting a movie into the database.
 */
public abstract class AbstractInsertUpdateDeleteMovieDbSyncTask extends AbstractDbSyncTask<MovieItem, MovieRow> {


    protected MovieItem item;

    public AbstractInsertUpdateDeleteMovieDbSyncTask(int syncAction, MovieItem item, SyncCallback<MovieItem> callback) {
        super(syncAction, callback);
        this.item = item;
    }

    @Override
    public void synchronize() throws Exception {
        if (item != null) {
            MovieRow row = DatabaseAdapterUtils.getMovieRowFromMovieItem(item);
            boolean result = commit(row);
            if (result) {
                onTaskFinished(null, item, getSyncEvent(), SyncDbResult.SUCCESS);
            } else {
                onTaskFinished("Failed to insert", item, getSyncEvent(), SyncDbResult.FAILURE);
            }
        } else {
            throw new IllegalArgumentException("Item cannot be null");
        }
    }



    @Override
    protected void onUpdate(String defaultMsg, MovieItem obj, int event) {
        if (callback != null) {
            callback.onUpdateUI(defaultMsg, obj, event);
        }
    }
}
