package com.org.android.popularmovies.synchronization.interfaces;

import com.org.android.popularmovies.synchronization.impl.tasks.AbstractHttpSyncTask;

/**
 * SyncListener for notifying listeners
 * the status of the tasks/task.
 */
public interface SyncListener<T> {

    void onSuccess();

    void onFail(T task, boolean retry);
}
