package com.org.android.popularmovies.controller.factory;

import com.org.android.popularmovies.controller.factory.interfaces.IDataFactory;
import com.org.android.popularmovies.controller.factory.tasks.AbstractInsertUpdateDeleteMovieDbSyncTask;
import com.org.android.popularmovies.controller.factory.tasks.DeleteMovieDbSyncTask;
import com.org.android.popularmovies.controller.factory.tasks.DeleteMoviesDbSyncTask;
import com.org.android.popularmovies.controller.factory.tasks.InsertMovieDbSyncTask;
import com.org.android.popularmovies.controller.factory.tasks.InsertOrUpdateDbSyncTask;
import com.org.android.popularmovies.controller.factory.tasks.InsertOrUpdateGenresTask;
import com.org.android.popularmovies.controller.factory.tasks.LoadFavoriteMoviesFromDb;
import com.org.android.popularmovies.controller.factory.tasks.LoadGenresSyncTaskDb;
import com.org.android.popularmovies.controller.factory.tasks.LoadMovieStatusDbSyncTask;
import com.org.android.popularmovies.controller.factory.tasks.UpdateMovieDbSyncTask;
import com.org.android.popularmovies.controller.factory.thread.DatabaseThreadObserverImpl;
import com.org.android.popularmovies.model.GenreItem;
import com.org.android.popularmovies.model.MovieItem;
import com.org.android.popularmovies.synchronization.impl.tasks.LoadGenresSyncTask;
import com.org.android.popularmovies.synchronization.interfaces.SyncCallback;

import java.util.List;

/**
 * Layer that is responsible with database interaction.
 * The interaction is in both ways, loading data from db,
 * updating data to the db, etc.
 */
public class DataFactory implements IDataFactory {

    private static final String TAG = DataFactory.class.getCanonicalName();
    private static DataFactory instance;

    private DatabaseThreadObserverImpl dbThread;

    protected DataFactory() {

        dbThread = new DatabaseThreadObserverImpl();
        dbThread.start();
    }

    public static DataFactory getInstance() {
        if (instance == null) {
            instance = new DataFactory();
        }
        return instance;
    }

    @Override
    public void insertMovieToDb(MovieItem item, SyncCallback<MovieItem> callback) {
        AbstractInsertUpdateDeleteMovieDbSyncTask insertMovieTask = new InsertMovieDbSyncTask(SyncDbAction.INSERT_MOVIE.getId(), item, callback);
        dbThread.addDatabasebSyncTask(insertMovieTask);
    }

    @Override
    public void updateMoviesToDb(List<MovieItem> items, SyncCallback<List<MovieItem>> callback) {
        DeleteMoviesDbSyncTask deleteMoviesDbSyncTask = new DeleteMoviesDbSyncTask(SyncDbAction.UPDATE_MOVIES.getId(), items, callback);
        dbThread.addDatabasebSyncTask(deleteMoviesDbSyncTask);
    }

    @Override
    public void deleteMoviesFromDb(List<MovieItem> items, SyncCallback<List<MovieItem>> callback) {
        DeleteMoviesDbSyncTask deleteMoviesDbSyncTask = new DeleteMoviesDbSyncTask(SyncDbAction.DELETE_MOVOIES.getId(), items, callback);
        dbThread.addDatabasebSyncTask(deleteMoviesDbSyncTask);
    }

    @Override
    public void insertOrUpdateMovieToDb(MovieItem item, SyncCallback<MovieItem> callback) {
        AbstractInsertUpdateDeleteMovieDbSyncTask insertUpdateDeleteMovieDbSyncTask = new InsertOrUpdateDbSyncTask(SyncDbAction.INSERT_UPDATE_MOVIE.getId(), item, callback);
        dbThread.addDatabasebSyncTask(insertUpdateDeleteMovieDbSyncTask);
    }

    @Override
    public void loadMovieStatusFromDb(MovieItem item, SyncCallback<MovieItem> mCallback) {

        AbstractInsertUpdateDeleteMovieDbSyncTask insertUpdateDeleteMovieDbSyncTask = new LoadMovieStatusDbSyncTask(SyncDbAction.LOAD_UPDATE_STATUS.getId(), item, mCallback);
        dbThread.addDatabasebSyncTask(insertUpdateDeleteMovieDbSyncTask);
    }

    @Override
    public void loadFavoriteMoviesFromDb(List<MovieItem> items, SyncCallback<List<MovieItem>> mCallback) {
        LoadFavoriteMoviesFromDb loadFavoriteMoviesFromDb = new LoadFavoriteMoviesFromDb(SyncDbAction.LOAD_UPDATE_STATUS.getId(), items, mCallback);
        dbThread.addDatabasebSyncTask(loadFavoriteMoviesFromDb);
    }

    @Override
    public void insertOrUpdateGenresToDb(List<GenreItem> genreItems) {
        InsertOrUpdateGenresTask insertOrUpdateGenresTask = new InsertOrUpdateGenresTask(SyncDbAction.INSERT_UPDATE_GENRE.getId(), genreItems, null);
        dbThread.addDatabasebSyncTask(insertOrUpdateGenresTask);
    }

    @Override
    public void loadGenres(SyncCallback<List<GenreItem>> callback) {
        LoadGenresSyncTaskDb loadGenres = new LoadGenresSyncTaskDb(SyncDbAction.LOAD_GENRES.getId(), callback);
        dbThread.addDatabasebSyncTask(loadGenres);
    }
}
