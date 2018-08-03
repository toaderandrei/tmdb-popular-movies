package com.org.android.popularmovies.controller.factory.tasks;

import android.util.Log;

import com.org.android.popularmovies.controller.factory.SyncDbEvent;
import com.org.android.popularmovies.db.database.DatabaseAdapterUtils;
import com.org.android.popularmovies.db.interfaces.IDatabaseRow;
import com.org.android.popularmovies.db.model.GenreRow;
import com.org.android.popularmovies.model.GenreItem;
import com.org.android.popularmovies.synchronization.interfaces.SyncCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * Task that either updates or inserts a genre row to the db.
 */
public class InsertOrUpdateGenresTask extends AbstractDbSyncTask<List<GenreItem>, List<IDatabaseRow>> {


    private List<GenreItem> items;

    public InsertOrUpdateGenresTask(int syncAction, List<GenreItem> items, SyncCallback<List<GenreItem>> callback) {
        super(syncAction, callback);
        this.items = items;
    }


    @Override
    public void synchronize() throws Exception {
        if (items != null) {
            List<IDatabaseRow> rows = DatabaseAdapterUtils.getGenreRowsFromGenreItems(items);
            boolean result = commit(rows);
            if (result) {
                onTaskFinished(null, items, getSyncEvent(), SyncDbResult.SUCCESS);
            } else {
                onTaskFinished("Failed to insert", items, getSyncEvent(), SyncDbResult.FAILURE);
            }
        } else {
            throw new IllegalArgumentException("Item cannot be null");
        }
    }

    @Override
    protected void onUpdate(String defaultMsg, List<GenreItem> obj, int event) {
        if (callback != null) {
            callback.onUpdateUI(defaultMsg, obj, event);
        }
    }

    @Override
    protected int getSyncEvent() {
        return SyncDbEvent.INSERT_OR_UPDATE_GENRES;
    }

    @Override
    protected boolean commit(List<IDatabaseRow> data) {

        List<IDatabaseRow> rowsToInsert = new ArrayList<>();
        for (IDatabaseRow row : data) {
            IDatabaseRow dbRow = getDbAdapter().getGenre(((GenreRow) row).getGenreId());
            if (dbRow == null) {
                rowsToInsert.add(row);
            }
        }
        if (!rowsToInsert.isEmpty()) {
            Log.d(TAG, "inserted genre rows:" + rowsToInsert.size());
            getDbAdapter().insertRows(rowsToInsert);
            //TODO - this needs to be checkout.
            return true;
        }
        return false;
    }
}
