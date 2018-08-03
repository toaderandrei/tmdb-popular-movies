package com.org.android.popularmovies.controller.factory.tasks;

import com.org.android.popularmovies.controller.factory.SyncDbEvent;
import com.org.android.popularmovies.db.database.DatabaseAdapterUtils;
import com.org.android.popularmovies.db.interfaces.IDatabaseRow;
import com.org.android.popularmovies.model.GenreItem;
import com.org.android.popularmovies.synchronization.interfaces.SyncCallback;

import java.util.Collections;
import java.util.List;

/**
 * Created by andrei on 12/14/15.
 */
public class LoadGenresSyncTaskDb extends AbstractDbSyncTask<List<GenreItem>, Void> {


    private List<IDatabaseRow> genreRows;

    public LoadGenresSyncTaskDb(int syncAction, SyncCallback<List<GenreItem>> callback) {
        super(syncAction, callback);
    }

    @Override
    protected void onUpdate(String defaultMsg, List<GenreItem> obj, int event) {
        if (callback != null) {
            callback.onUpdateUI(defaultMsg, obj, event);
        }
    }

    @Override
    protected int getSyncEvent() {
        return SyncDbEvent.LOAD_GENRES;
    }

    @Override
    protected boolean commit(Void v) {
        genreRows = getDbAdapter().getGenres();
        return genreRows != null && !genreRows.isEmpty();
    }

    @Override
    public void synchronize() throws Exception {
        boolean result = commit(null);
        if (result) {
            if (genreRows != null && !genreRows.isEmpty()) {
                List<GenreItem> genreItems = DatabaseAdapterUtils.getGenreItemsFromGenreRows(genreRows);
                onTaskFinished(null, genreItems, getSyncEvent(), SyncDbResult.SUCCESS);
            }
        } else {
            onTaskFinished(null, Collections.EMPTY_LIST, getSyncEvent(), SyncDbResult.FAILURE);
        }
    }
}
