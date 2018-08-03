package com.org.android.popularmovies.controller.factory.thread;

import com.org.android.popularmovies.controller.factory.tasks.AbstractDbSyncTask;
import com.org.android.popularmovies.synchronization.impl.tasks.AbstractHttpSyncTask;
import com.org.android.popularmovies.synchronization.interfaces.WorkerPublisher;

import java.util.List;

/**
 * Interface that describe the database operations.
 */
public interface DatabaseThread extends WorkerPublisher<AbstractDbSyncTask> {

    void addDatabasebSyncTask(AbstractDbSyncTask task);

    List<AbstractDbSyncTask> getSynQueue();

    /**
     * stops the worker thread.
     *
     * @param cleanList if true clears the queue.
     */
    void stopWorker(boolean cleanList);
}
