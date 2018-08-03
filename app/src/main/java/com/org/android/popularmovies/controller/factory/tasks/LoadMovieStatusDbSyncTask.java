package com.org.android.popularmovies.controller.factory.tasks;

import com.org.android.popularmovies.controller.factory.SyncDbEvent;
import com.org.android.popularmovies.db.interfaces.IDatabaseRow;
import com.org.android.popularmovies.db.model.MovieRow;
import com.org.android.popularmovies.model.MovieItem;
import com.org.android.popularmovies.synchronization.interfaces.SyncCallback;

/**
 * Created by andrei on 12/12/15.
 */
public class LoadMovieStatusDbSyncTask extends AbstractInsertUpdateDeleteMovieDbSyncTask {


    public LoadMovieStatusDbSyncTask(int id, MovieItem item, SyncCallback<MovieItem> mCallback) {
        super(id, item, mCallback);
    }

    @Override
    protected int getSyncEvent() {
        return SyncDbEvent.LOAD_MOVIE_STATUS;
    }

    @Override
    protected boolean commit(MovieRow data) {
        IDatabaseRow row = getDbAdapter().getMovie(String.valueOf(data.getMovieId()));
        if (row != null) {
            boolean isFavored = ((MovieRow) row).getMovieFavorite() == 1;
            item.setFavored(isFavored);
        }
        return row != null;
    }
}
