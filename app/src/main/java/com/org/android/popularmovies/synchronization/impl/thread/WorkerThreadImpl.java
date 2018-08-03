package com.org.android.popularmovies.synchronization.impl.thread;

import com.org.android.popularmovies.synchronization.impl.tasks.AbstractHttpSyncTask;
import com.org.android.popularmovies.synchronization.interfaces.WorkerThread;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * WorkerThreadImpl that adds/runs the tasks added by the SyncManagerImpl
 */
public abstract class WorkerThreadImpl extends Thread implements WorkerThread {
    public boolean cleanList = false;
    private boolean stopWorker = false;
    private static final String TAG = WorkerThreadImpl.class.getSimpleName();
    private List<AbstractHttpSyncTask> syncQueue = new ArrayList<AbstractHttpSyncTask>();
    public WorkerThreadImpl() {
    }


    public WorkerThreadImpl(WorkerThreadImpl copyCtr) {
        this.syncQueue = copyCtr.syncQueue;
    }

    @Override
    public void stopWorker(boolean cleanList) {
        this.cleanList = cleanList;
        this.stopWorker = true;
    }

    @Override
    public void addSyncTask(AbstractHttpSyncTask syncTask) {
        synchronized (syncQueue) {
            if (syncTask != null && !getSynQueue().contains(syncTask)) {
                getSynQueue().add(syncTask);
            }
        }
    }

    @Override
    public List<AbstractHttpSyncTask> getSynQueue() {
        return this.syncQueue;
    }

    @Override
    public void run() {
        while (!stopWorker) {
            AbstractHttpSyncTask syncTask = null;
            synchronized (syncQueue) {
                if (!getSynQueue().isEmpty()) {
                    syncTask = getSynQueue().get(0);
                }
            }
            if (syncTask != null) {
                try {
                    syncTask.synchronize();
                    synchronized (syncQueue) {
                        if (!getSynQueue().isEmpty()) {
                            getSynQueue().remove(syncTask);
                        }
                    }
                } catch (IOException ioex) {
                    onFail(syncTask, true);
                } catch (Exception ex) {
                    onFail(syncTask, false);
                }
            }
        }
        cleanUp();
    }

    private void cleanUp() {
        if (cleanList) {
            getSynQueue().clear();
        }
    }
}
