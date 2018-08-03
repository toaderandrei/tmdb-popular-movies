package com.org.android.popularmovies.synchronization.impl.thread;

import com.org.android.popularmovies.synchronization.impl.tasks.AbstractHttpSyncTask;
import com.org.android.popularmovies.synchronization.interfaces.SyncListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Created by andrei on 11/27/15.
 */
public class WorkerThreadPublisherImpl extends WorkerThreadImpl {

    private List<SyncListener> syncListenerList = Collections.synchronizedList(new ArrayList<SyncListener>());

    public WorkerThreadPublisherImpl() {
        super();
    }

    public WorkerThreadPublisherImpl(WorkerThreadPublisherImpl oldInstance) {
        super(oldInstance);
    }


    @Override
    public void registerListener(SyncListener listener) {
        synchronized (syncListenerList) {
            if (listener != null) {
                syncListenerList.add(listener);
            }
        }
    }

    @Override
    public void unregisterListener(SyncListener listener) {
        synchronized (syncListenerList) {
            Iterator<SyncListener> it = syncListenerList.iterator();
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
        for (SyncListener listener : syncListenerList) {
            listener.onSuccess();
        }
    }

    @Override
    public void onFail(AbstractHttpSyncTask task, boolean retry) {
        for (SyncListener listener : syncListenerList) {
            listener.onFail(task, retry);
        }
    }
}
