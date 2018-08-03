package com.org.android.popularmovies.synchronization.impl.tasks;

/**
 * Generic AbstractHttpSyncTask for synchronizing with the server.
 */

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.org.android.popularmovies.application.PopularMoviesApp;
import com.org.android.popularmovies.controller.DataController;
import com.org.android.popularmovies.httpclient.IHttpClient;
import com.org.android.popularmovies.synchronization.event.SyncEvent;
import com.org.android.popularmovies.synchronization.interfaces.SyncCallback;

import java.lang.ref.WeakReference;

public abstract class AbstractHttpSyncTask<T> extends AbstractAsyncTask {
    private Integer syncAction;

    protected static final String TAG = AbstractHttpSyncTask.class.getSimpleName();
    /**
     * the purpose of this is to just create unique AbstractHttpSyncTask objects.
     */
    protected SyncCallback<T> callback;

    public AbstractHttpSyncTask(int syncAction, SyncCallback<T> callback) {
        this.syncAction = syncAction;
        this.callback = callback;
        initHandler();
    }


    private void initHandler() {
        handler = new AbstractHandler<T>(this);
    }

    protected void onTaskFinished(T data, int event) {
        sendMessage(null, data, event);
    }

    protected void onTaskFinished(String defaultMessage, T data, int event) {
        sendMessage(defaultMessage, data, event);
    }

    protected void sendMessage(String defaultMessage, T data, int event) {
        if (event == -1) {
            return;
        }
        Message completeMessage = getHandler().obtainMessage(event);
        if (data != null) {
            completeMessage.obj = data;
        }
        if (defaultMessage != null && !defaultMessage.isEmpty()) {
            Bundle bundle = new Bundle();
            bundle.putString("message", defaultMessage);
            completeMessage.setData(bundle);
        }
        completeMessage.sendToTarget();
    }

    protected AbstractHandler<T> getHandler() {
        return handler;
    }

    /**
     * this handler connects to the UI thread.
     */
    private AbstractHandler<T> handler = null;

    protected abstract void onUpdate(String defaultMsg, T obj, int event);


    private static class AbstractHandler<T> extends Handler {
        private final WeakReference<AbstractHttpSyncTask> abstractSyncTaskWeakReference;

        public AbstractHandler(AbstractHttpSyncTask abstractSyncTask) {
            super(Looper.myLooper());
            abstractSyncTaskWeakReference = new WeakReference<>(abstractSyncTask);
        }

        @Override
        public void handleMessage(Message msg) {
            AbstractHttpSyncTask task = abstractSyncTaskWeakReference.get();
            switch (msg.what) {
                case SyncEvent.SUCCESS:
                    sendMessageToUI(msg, task, SyncEvent.SUCCESS);
                    break;
                case SyncEvent.FAILURE:
                    sendMessageToUI(msg, task, SyncEvent.FAILURE);
                    break;
            }
        }

        public void sendMessageToUI(Message msg, AbstractHttpSyncTask task, int success) {
            T dataSuccess = (T) msg.obj;
            String successMsg = "";
            if (msg.getData() != null) {
                successMsg = msg.getData().getString("message");
            }
            if (task != null && dataSuccess != null) {
                task.onUpdate(successMsg, dataSuccess, success);
            }
        }
    }

    public int getAction() {
        return syncAction;
    }

}
