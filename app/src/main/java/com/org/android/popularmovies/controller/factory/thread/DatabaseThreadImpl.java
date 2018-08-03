package com.org.android.popularmovies.controller.factory.thread;

import android.database.SQLException;

import com.org.android.popularmovies.controller.factory.tasks.AbstractDbSyncTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Database thread responsible for updating the database.
 */
public abstract class DatabaseThreadImpl extends Thread implements DatabaseThread {


    private boolean stopWorker = false;
    private static final String TAG = DatabaseThreadImpl.class.getSimpleName();
    private List<AbstractDbSyncTask> syncQueue = new ArrayList<AbstractDbSyncTask>();
    private boolean cleanList;

    public DatabaseThreadImpl() {
        this.cleanList = false;
    }

    public DatabaseThreadImpl(DatabaseThreadImpl old) {
        this.syncQueue = old.syncQueue;
    }

    @Override
    public void run() {
        while (!stopWorker) {
            AbstractDbSyncTask task = null;
            synchronized (syncQueue) {
                if (!getSynQueue().isEmpty()) {
                    task = getSynQueue().get(0);
                }
                if (task != null) {
                    try {
                        task.synchronize();
                        if (!getSynQueue().isEmpty()) {
                            getSynQueue().remove(task);
                        }
                    } catch (SQLException sqex) {
                        onFail(task, true);
                    } catch (IllegalArgumentException ex) {
                        onFail(task, false);
                    } catch (Exception ex) {
                        onFail(task, false);
                    }
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

    @Override
    public void stopWorker(boolean cleanList) {
        this.stopWorker = true;
        this.cleanList = cleanList;
    }

    @Override
    public void addDatabasebSyncTask(AbstractDbSyncTask task) {
        synchronized (syncQueue) {
            if (task != null && !getSynQueue().contains(task)) {
                getSynQueue().add(task);
            }
        }
    }


    @Override
    public List<AbstractDbSyncTask> getSynQueue() {
        return this.syncQueue;
    }


    public abstract void onFail(AbstractDbSyncTask task, boolean retry);

    public abstract void onSuccess();
}
