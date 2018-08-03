package com.org.android.popularmovies.synchronization.interfaces;

import com.org.android.popularmovies.controller.ControllerCallback;
import com.org.android.popularmovies.model.GenreItem;
import com.org.android.popularmovies.model.MovieItem;
import com.org.android.popularmovies.model.MovieReview;
import com.org.android.popularmovies.model.MovieVideo;
import com.org.android.popularmovies.synchronization.impl.tasks.AbstractHttpSyncTask;

import java.util.List;

public interface SyncManager {

    /**
     * after the thread instance is create the thread is started
     *
     * @throws Exception
     */
    public void startThread();

    /**
     * stops the process of the thread
     *
     * @throws Exception
     */
    public void stopThread();

    /**
     * creates the thread that does the job.
     */
    public void createThread();

    /**
     * gets the SyncWorkerPublisher to be able to register listeners for receiving
     * updates regarding the status of the sync.
     *
     * @return the current worker thread.
     */
    public WorkerThread getSyncWorker();

    /**
     * updates a storage which has the id elementId.
     *
     * @param movieId the id of the storage to be updated to the server.
     */
    public void getMoviesById(int movieId);

    /**
     * gets all the movies based on some parameters.
     * If the before is set to true the task is run before the final task.
     *
     * @param page      - current page to load.
     * @param sortType  - the sortype
     * @param sortOrder - the sortorder.
     * @param listener  - the callback listener.
     */
    public void loadMoviesFromServer(SyncCallback<List<MovieItem>> listener, String sortType, String sortOrder, int page);

    void updateMovieVideos(String id, SyncCallback<List<MovieVideo>> callback);

    void updateMovieReviews(String id, SyncCallback<List<MovieReview>> callback);

    void setControllerCallback(ControllerCallback callback);

    void loadGenres(SyncCallback<List<GenreItem>> callback);
}
