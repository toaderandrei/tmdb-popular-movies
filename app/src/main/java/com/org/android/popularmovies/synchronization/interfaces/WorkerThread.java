package com.org.android.popularmovies.synchronization.interfaces;

import com.org.android.popularmovies.synchronization.impl.tasks.AbstractHttpSyncTask;

import java.util.List;

/**
 * Interface that describes the worker thread.
 */
public interface WorkerThread extends WorkerPublisher<AbstractHttpSyncTask>{
    /**
     * stops the worker thread.
     *
     * @param cleanList if true clears the queue.
     */
    void stopWorker(boolean cleanList);

    /**
     * adds a new task to the queue.
     *
     * @param syncTask the task to be added.
     */
    void addSyncTask(AbstractHttpSyncTask syncTask);

    /**
     * gets the sync queue.
     *
     * @return the sync queue.
     */
    List<AbstractHttpSyncTask> getSynQueue();

}
