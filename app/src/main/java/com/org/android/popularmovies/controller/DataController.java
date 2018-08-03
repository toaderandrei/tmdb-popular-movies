package com.org.android.popularmovies.controller;

import android.support.annotation.NonNull;

import com.org.android.popularmovies.application.PopularMoviesApp;
import com.org.android.popularmovies.controller.factory.DataFactory;
import com.org.android.popularmovies.model.GenreItem;
import com.org.android.popularmovies.model.MovieItem;
import com.org.android.popularmovies.model.MovieReview;
import com.org.android.popularmovies.model.MovieVideo;
import com.org.android.popularmovies.observer.DataObservable;
import com.org.android.popularmovies.observer.DataObserverImpl;
import com.org.android.popularmovies.synchronization.interfaces.SyncCallback;
import com.org.android.popularmovies.synchronization.interfaces.SyncManager;

import java.util.List;

/**
 * DataController class. It takes care of requests from UI(Activities, Fragments, etc)
 * to the server by creating synchronized requests.
 */
public class DataController implements ControllerCallback {


    private static DataController dataControllerInstance;
    private DataCache dataCache = new DataCache();
    private String currentSortType;
    private String currentSortOrder;
    private DataObserverImpl dataObserver;

    private DataController() {
        dataObserver = new DataObserverImpl();
        //optional for now we set the callback.
        //later we can set it when we want.
        setControllerCallback();
    }

    public void setControllerCallback() {
        getSyncManager().setControllerCallback(this);
    }

    public static DataController getInstance() {
        if (dataControllerInstance == null) {
            dataControllerInstance = new DataController();
        }
        return dataControllerInstance;
    }

    public void registerListener(DataObservable observable) {
        if (dataObserver != null) {
            dataObserver.registerListener(observable);
        }
    }


    public void unregisterListener(DataObservable observable) {
        if (dataObserver != null) {
            dataObserver.unregisterListener(observable);
        }
    }

    public void loadMoviesFromServer(SyncCallback<List<MovieItem>> syncCallback, String sortType, String sortOrder, int page) {
        getSyncManager().loadMoviesFromServer(syncCallback, sortType, sortOrder, page);
    }

    private SyncManager getSyncManager() {
        return PopularMoviesApp.getInstance().getSyncManager();
    }

    public void loadMovieVideos(Long id, SyncCallback<List<MovieVideo>> callback) {
        getSyncManager().updateMovieVideos(String.valueOf(id), callback);
    }

    public void loadGenresFromServer(SyncCallback<List<GenreItem>> callback) {
        getSyncManager().loadGenres(callback);
    }


    public void loadGenresFromDb(SyncCallback<List<GenreItem>> callback) {
        getDataFactoryInstance().loadGenres(callback);
    }


    /**
     * Here we updateFailed the status of the movie to the database.
     * In case the movie is favored it will be saved to the database,
     * otherwise the movie will be deleted from the database.
     *
     * @param item to be updated - delete or insert/updateFailed
     */
    public void updateMovieToTheDb(MovieItem item, SyncCallback<MovieItem> callback) {
        getDataFactoryInstance().insertOrUpdateMovieToDb(item, callback);
    }

    private DataFactory getDataFactoryInstance() {
        return DataFactory.getInstance();
    }

    public GenreItem getGenreItem(int id) {
        if (dataCache == null) {
            throw new IllegalStateException("cannot be null");
        }
        return dataCache.getGenreItem(id);
    }

    public List<GenreItem> getGenres() {
        if (dataCache == null) {
            throw new IllegalStateException("cannot be null");
        }
        return dataCache.getGenres();
    }

    public void addMoviesToCache(@NonNull List<MovieItem> movieItems) {
        if (dataCache == null) {
            throw new IllegalStateException("cannot be null");
        }
        dataCache.addMoviesToCache(movieItems);
    }

    public void clearCaches() {
        if (dataCache == null) {
            throw new IllegalStateException("cannot be null");
        }
        dataCache.clearCache();
    }

    public List<MovieItem> getMovieList() {
        if (dataCache == null) {
            throw new IllegalStateException("cannot be null");
        }
        return dataCache.getMovieList();
    }

    public String getCurrentSortType() {
        return currentSortType;
    }

    public String getCurrentSortOrder() {
        return currentSortOrder;
    }

    public void setCurrentSortType(String sortType) {
        this.currentSortType = sortType;
    }

    public void setCurrentSortOrder(String sortOrder) {
        this.currentSortOrder = sortOrder;
    }

    public void loadReviews(Long id, SyncCallback<List<MovieReview>> callback) {
        PopularMoviesApp.getInstance().getSyncManager().updateMovieReviews(String.valueOf(id), callback);
    }

    @Override
    public void onSuccess() {
        if (dataObserver != null) {
            dataObserver.notifyListenersSuccess("Task Finished");
        }
    }

    @Override
    public void onFail(String message) {
        if (dataObserver != null) {
            dataObserver.notifyListenersFail(message);
        }
    }

    public void loadMovieStatusFromDbIfExists(MovieItem item, SyncCallback<MovieItem> mCallback) {
        getDataFactoryInstance().loadMovieStatusFromDb(item, mCallback);
    }

    public void loadFavoredMoviesFromDb(SyncCallback<List<MovieItem>> mCallback) {
        getDataFactoryInstance().loadFavoriteMoviesFromDb(null, mCallback);
    }

    /**
     * saves the genres to the cache and to the db.
     *
     * @param genreItems the list of genres to be saved/stored.
     */
    public void saveGenresToCache(List<GenreItem> genreItems) {
        dataCache.saveGenres(genreItems);
    }

    public void saveGenresToDb(List<GenreItem> genreItems) {
        getDataFactoryInstance().insertOrUpdateGenresToDb(genreItems);
    }
}
