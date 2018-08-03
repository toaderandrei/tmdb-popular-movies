package com.org.android.popularmovies.controller.factory.thread;

import com.org.android.popularmovies.controller.factory.tasks.AbstractDbSyncTask;
import com.org.android.popularmovies.synchronization.impl.tasks.AbstractHttpSyncTask;
import com.org.android.popularmovies.synchronization.interfaces.SyncListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Thread that handles background operations to the database.
 * */
public class DatabaseThreadObserverImpl extends DatabaseThreadImpl {


    private List<SyncListener<AbstractDbSyncTask>> syncListenerList = Collections.synchronizedList(new ArrayList<SyncListener<AbstractDbSyncTask>>());

    public DatabaseThreadObserverImpl() {
        super();
    }

    public DatabaseThreadObserverImpl(DatabaseThreadObserverImpl oldInstance) {
        super(oldInstance);
    }

    @Override
    public void registerListener(SyncListener<AbstractDbSyncTask> listener) {
        synchronized (syncListenerList) {
            if (listener != null) {
                syncListenerList.add(listener);
            }
        }
    }

    @Override
    public void unregisterListener(SyncListener<AbstractDbSyncTask> listener) {
        synchronized (syncListenerList) {
            Iterator<SyncListener<AbstractDbSyncTask>> it = syncListenerList.iterator();
            while (it.hasNext()) {
                SyncListener syncListener = it.next();
                if (listener == syncListener) {
                    it.remove();
                }
            }
        }
    }

    @Override
    public void clearAllListeners() {
        if (syncListenerList != null) {
            syncListenerList.clear();
        }
    }

    @Override
    public void onSuccess() {
        for (SyncListener<AbstractDbSyncTask> listener : syncListenerList) {
            listener.onSuccess();
        }
    }

    @Override
    public void onFail(AbstractDbSyncTask task, boolean retry) {
        for (SyncListener<AbstractDbSyncTask> listener : syncListenerList) {
            listener.onFail(task, retry);
        }
    }
}