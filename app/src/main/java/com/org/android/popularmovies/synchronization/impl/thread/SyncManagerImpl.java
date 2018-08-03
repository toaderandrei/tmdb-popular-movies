package com.org.android.popularmovies.synchronization.impl.thread;

import android.util.Log;

import com.org.android.popularmovies.controller.ControllerCallback;
import com.org.android.popularmovies.model.GenreItem;
import com.org.android.popularmovies.model.MovieItem;
import com.org.android.popularmovies.model.MovieReview;
import com.org.android.popularmovies.model.MovieVideo;
import com.org.android.popularmovies.synchronization.event.SyncAction;
import com.org.android.popularmovies.synchronization.impl.tasks.AbstractHttpSyncTask;
import com.org.android.popularmovies.synchronization.impl.tasks.LoadGenresSyncTask;
import com.org.android.popularmovies.synchronization.impl.tasks.UpdateMovieReviewTaskImpl;
import com.org.android.popularmovies.synchronization.impl.tasks.UpdateMovieVideosTaskImpl;
import com.org.android.popularmovies.synchronization.impl.tasks.UpdateMoviesTaskImpl;
import com.org.android.popularmovies.synchronization.interfaces.SyncCallback;
import com.org.android.popularmovies.synchronization.interfaces.SyncListener;
import com.org.android.popularmovies.synchronization.interfaces.SyncManager;
import com.org.android.popularmovies.synchronization.interfaces.WorkerThread;

import java.util.List;

/**
 * Synchronization manager that takes care of adding tasks to the pool of tasks.
 * Each task added to the pool is run the SyncThread.
 */
public class SyncManagerImpl implements SyncManager, SyncListener<AbstractHttpSyncTask> {

    private static final String TAG = SyncManagerImpl.class.getCanonicalName();
    private WorkerThreadPublisherImpl syncThread;
    private int retryCount = 0;
    private static final int MAX_ATTEMPTS = 4;
    private static SyncManagerImpl instance = null;
    private ControllerCallback callback;

    public static SyncManagerImpl getInstance() {
        if (instance == null) {
            instance = new SyncManagerImpl();
        }
        return instance;
    }

    protected SyncManagerImpl() {
        createThread();
    }

    @Override
    public void createThread() {
        syncThread = new WorkerThreadPublisherImpl();
        syncThread.setPriority(3);
    }

    private WorkerThreadPublisherImpl createThread(WorkerThreadPublisherImpl oldInstance) {
        WorkerThreadPublisherImpl newSyncThread = new WorkerThreadPublisherImpl(oldInstance);
        newSyncThread.setPriority(3);
        return newSyncThread;
    }

    @Override
    public void startThread() {
        try {
            if (syncThread == null) {
                createThread();
            }
            switch (syncThread.getState()) {
                case NEW:
                    syncThread.start();
                    break;
                case BLOCKED:
                case WAITING:
                case RUNNABLE:
                case TIMED_WAITING:
                    Log.d(TAG, "Stopping the thread");
                    stopThread();
                    break;
                case TERMINATED:
                    syncThread = createThread(syncThread);
                    syncThread.start();
                    break;
                default:
                    stopThread();
                    startThread();
                    break;
            }
        } catch (IllegalThreadStateException e) {
            Log.e(TAG, "Exceptin starting the worker thread.");
        }

    }


    @Override
    public void stopThread() {
        try {
            if (syncThread != null && syncThread.isAlive()) {
                Log.w(TAG, "stopping the thread");
                syncThread.stopWorker(true);
                syncThread.join();
            }
        } catch (InterruptedException e) {
            Log.e(TAG, "Interrupted exception.message" + e.getMessage());
        }

    }

    @Override
    public WorkerThread getSyncWorker() {
        return syncThread;
    }


    protected void addSyncTask(AbstractHttpSyncTask task) {
        if (syncThread != null) {
            syncThread.addSyncTask(task);
        }
    }

    @Override
    public void getMoviesById(int movieId) {

    }

    @Override
    public void loadMoviesFromServer(SyncCallback<List<MovieItem>> callback, String sortType, String sortOrder, int page) {
        UpdateMoviesTaskImpl task = new UpdateMoviesTaskImpl(SyncAction.GET_MOVIES.getId(), callback, sortType, sortOrder, page);
        addSyncTask(task);
    }

    @Override
    public void updateMovieVideos(String id, SyncCallback<List<MovieVideo>> callback) {
        UpdateMovieVideosTaskImpl task = new UpdateMovieVideosTaskImpl(SyncAction.GET_MOVIE_TRAILER.getId(), callback, id, false);
        addSyncTask(task);
    }

    @Override
    public void loadGenres(SyncCallback<List<GenreItem>> callback) {
        LoadGenresSyncTask loadGenresTask = new LoadGenresSyncTask(SyncAction.GET_GENRES.getId(), callback);
        addSyncTask(loadGenresTask);
    }

    @Override
    public void updateMovieReviews(String id, SyncCallback<List<MovieReview>> callback) {
        UpdateMovieReviewTaskImpl task = new UpdateMovieReviewTaskImpl(SyncAction.GET_GENRES.getId(), callback, id);
        addSyncTask(task);
    }


    @Override
    public void onSuccess() {
        callback.onSuccess();
    }

    @Override
    public void setControllerCallback(ControllerCallback callback) {
        this.callback = callback;
    }

    @Override
    public void onFail(AbstractHttpSyncTask task, boolean retry) {
        if (retry && task != null && retryCount < MAX_ATTEMPTS) {
            retryCount++;
            addSyncTask(task);
        } else {
            callback.onFail("Task:" + task.getAction() + " failed.");
        }
    }
}
