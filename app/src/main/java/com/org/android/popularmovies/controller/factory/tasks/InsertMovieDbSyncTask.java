package com.org.android.popularmovies.controller.factory.tasks;

import com.org.android.popularmovies.controller.factory.SyncDbEvent;
import com.org.android.popularmovies.db.model.MovieRow;
import com.org.android.popularmovies.model.MovieItem;
import com.org.android.popularmovies.synchronization.interfaces.SyncCallback;

/**
 * Task that insert a movie into the database.
 */
public class InsertMovieDbSyncTask extends AbstractInsertUpdateDeleteMovieDbSyncTask {


    public InsertMovieDbSyncTask(int syncAction, MovieItem item, SyncCallback<MovieItem> callback) {
        super(syncAction, item, callback);
    }

    @Override
    protected int getSyncEvent() {
        return SyncDbEvent.INSERT_MOVIE;
    }

    @Override
    protected boolean commit(MovieRow row) {
        return getDbAdapter().insertRow(row);
    }
}
