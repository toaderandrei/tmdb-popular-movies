package com.org.android.popularmovies.controller.factory.tasks;

import com.org.android.popularmovies.controller.factory.SyncDbEvent;
import com.org.android.popularmovies.db.database.DatabaseAdapterUtils;
import com.org.android.popularmovies.db.interfaces.IDatabaseRow;
import com.org.android.popularmovies.db.model.GenreRow;
import com.org.android.popularmovies.db.model.MovieRow;
import com.org.android.popularmovies.model.GenreItem;
import com.org.android.popularmovies.model.MovieItem;
import com.org.android.popularmovies.synchronization.interfaces.SyncCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * Task that deals with either inserting or updating the database,
 * depending on the existence of the record.
 */
public class InsertOrUpdateDbSyncTask extends AbstractInsertUpdateDeleteMovieDbSyncTask {


    public InsertOrUpdateDbSyncTask(int syncAction, MovieItem item, SyncCallback<MovieItem> callback) {
        super(syncAction, item, callback);
    }

    @Override
    protected int getSyncEvent() {
        return SyncDbEvent.INSERT_OR_UPDATE_MOVIE;
    }

    @Override
    protected boolean commit(MovieRow data) {
        if (data == null) {
            return false;
        }
        List<IDatabaseRow> rowsToInsertOrUpdate = new ArrayList<>();
        List<GenreItem> genreItemList = item.getGenres();
        List<IDatabaseRow> genreRowList = new ArrayList<>();
        for (GenreItem genreItem : genreItemList) {
            IDatabaseRow genreRow = getDbAdapter().getGenre(String.valueOf(genreItem.getId()));
            if (genreRow != null && genreRow instanceof GenreRow) {
                ((GenreRow) genreRow).setMovieId(String.valueOf(item.getId()));
                genreRowList.add(genreRow);
            }
        }
        updateGenreRows(genreRowList);
        return updateOrInsertRow(data, rowsToInsertOrUpdate);
    }

    private boolean updateOrInsertRow(MovieRow data, List<IDatabaseRow> rowsToInsertOrUpdate) {
        IDatabaseRow row = getDbAdapter().getMovie(String.valueOf(data.getMovieId()));
        rowsToInsertOrUpdate.add(row);
        if (row != null) {
            return getDbAdapter().updateRow(data);
        } else {
            return getDbAdapter().insertRow(data);
        }
    }

    private void updateGenreRows(List<IDatabaseRow> genreRowList) {
        List<IDatabaseRow> genreDatabaseRows = getDbAdapter().getGenresByMovieId(String.valueOf(item.getId()));
        for (IDatabaseRow mGenreRow : genreRowList) {
            if (!genreDatabaseRows.isEmpty() && genreDatabaseRows.contains(mGenreRow)) {
                getDbAdapter().updateRow(mGenreRow);

            } else {
                getDbAdapter().insertRow(mGenreRow);
            }
        }
    }
}
