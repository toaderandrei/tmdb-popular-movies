package com.org.android.popularmovies.synchronization.interfaces;

import com.org.android.popularmovies.synchronization.impl.tasks.AbstractHttpSyncTask;

/**
 * Created by andrei on 11/27/15.
 */
public interface WorkerPublisher<T> {

    void registerListener(SyncListener<T> listener);

    void unregisterListener(SyncListener<T> listener);

    void clearAllListeners();

    void onFail(T task, boolean retry);

    void onSuccess();
}
