package com.org.android.popularmovies.controller.factory.tasks;

import com.org.android.popularmovies.controller.factory.SyncDbEvent;
import com.org.android.popularmovies.db.model.MovieRow;
import com.org.android.popularmovies.model.MovieItem;
import com.org.android.popularmovies.synchronization.interfaces.SyncCallback;

/**
 * Task that handles a delete operation to the database.
 * */
public class DeleteMovieDbSyncTask extends AbstractInsertUpdateDeleteMovieDbSyncTask {


    public DeleteMovieDbSyncTask(int syncAction, MovieItem item, SyncCallback<MovieItem> callback) {
        super(syncAction, item, callback);
    }

    @Override
    protected boolean commit(MovieRow row) {
        return getDbAdapter().deleteRow(row);
    }

    @Override
    protected int getSyncEvent() {
        return SyncDbEvent.DELETE_MOVIE;
    }
}
