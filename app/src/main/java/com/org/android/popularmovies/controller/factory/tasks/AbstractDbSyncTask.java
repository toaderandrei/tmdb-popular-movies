package com.org.android.popularmovies.controller.factory.tasks;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.org.android.popularmovies.controller.factory.SyncDbEvent;
import com.org.android.popularmovies.db.adapter.SqliteDatabaseAdapter;
import com.org.android.popularmovies.db.adapter.SqliteDatabaseAdapterImpl;
import com.org.android.popularmovies.synchronization.impl.tasks.AbstractAsyncTask;
import com.org.android.popularmovies.synchronization.interfaces.SyncCallback;

import java.lang.ref.WeakReference;

/**
 * The abstract low layer implementation of the background tasks.
 * Here is the part where the messages are exchanged between
 * background thread and the Ui thread.
 */
public abstract class AbstractDbSyncTask<T, U> extends AbstractAsyncTask {
    public static final String HANDLER_MESSAGE = "message";
    private static final String SYNC_EVENT = "SYNC_EVENT";
    private Integer syncAction;

    protected static final String TAG = AbstractDbSyncTask.class.getSimpleName();
    /**
     * the purpose of this is to just create unique AbstractHttpSyncTask objects.
     */
    protected SyncCallback<T> callback;

    public AbstractDbSyncTask(int syncAction, SyncCallback<T> callback) {
        this.syncAction = syncAction;
        this.callback = callback;
        initHandler();
    }

    public AbstractDbSyncTask(int syncAction) {
        this.syncAction = syncAction;
        initHandler();
    }


    private void initHandler() {
        handler = new AbstractHandler<T>(this);
    }

    protected void onTaskFinished(String message, T data, int event, int result) {
        sendMessage(message, data, event, result);
    }

    protected void sendMessage(String defaultMessage, T task, int event, int result) {
        if (event == -1) {
            return;
        }
        Message completeMessage = getHandler().obtainMessage(event);
        if (task != null) {
            completeMessage.obj = task;
        }
        if (result != SyncDbResult.NONE) {
            Bundle resultBundle = new Bundle();
            resultBundle.putInt(SYNC_EVENT, result);
            completeMessage.setData(resultBundle);
        }
        if (defaultMessage != null && !defaultMessage.isEmpty()) {
            Bundle bundle = new Bundle();
            bundle.putString(HANDLER_MESSAGE, defaultMessage);
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
        private final WeakReference<AbstractDbSyncTask> abstractSyncTaskWeakReference;

        public AbstractHandler(AbstractDbSyncTask abstractSyncTask) {
            super(Looper.myLooper());
            abstractSyncTaskWeakReference = new WeakReference<>(abstractSyncTask);
        }

        @Override
        public void handleMessage(Message msg) {
            AbstractDbSyncTask task = abstractSyncTaskWeakReference.get();
            switch (msg.what) {
                case SyncDbEvent.LOAD_MOVIE_STATUS:
                    sendMessageToUi(msg, task);
                    break;
                case SyncDbEvent.INSERT_MOVIE:
                    sendMessageToUi(msg, task);
                    break;

                case SyncDbEvent.INSERT_OR_UPDATE_MOVIE:
                    sendMessageToUi(msg, task);
                    break;
                case SyncDbEvent.INSERT_MOVIES:
                    sendMessageToUi(msg, task);
                    break;

                case SyncDbEvent.UPDATE_MOVIE:
                    sendMessageToUi(msg, task);
                    break;

                case SyncDbEvent.LOAD_FAVORITE_MOVIES:
                    sendMessageToUi(msg, task);
                    break;

                case SyncDbEvent.LOAD_GENRES:
                    sendMessageToUi(msg, task);
                    break;
                case SyncDbEvent.UPDATE_MOVIES:
                    sendMessageToUi(msg, task);
                    break;
                case SyncDbEvent.DELETE_MOVIE:
                    sendMessageToUi(msg, task);
                    break;

                case SyncDbEvent.DELETE_MOVIES:
                    sendMessageToUi(msg, task);
                    break;

                case SyncDbEvent.INSERT_OR_UPDATE_GENRES:
                    sendMessageToUi(msg, task);
                    break;
            }
        }

        public void sendMessageToUi(Message msg, AbstractDbSyncTask task) {
            T dataLoadMovieStatus = (T) msg.obj;
            Bundle dataLoadMovieBundle = msg.getData();
            int dataLoadMovieStatusEvent = SyncDbResult.NONE;
            if (dataLoadMovieBundle != null) {
                dataLoadMovieStatusEvent = dataLoadMovieBundle.getInt(SYNC_EVENT) == 1 ? SyncDbResult.SUCCESS : SyncDbResult.FAILURE;
            }
            String defaultMsgLoadMovie = "";
            if (msg.getData() != null) {
                defaultMsgLoadMovie = msg.getData().getString(HANDLER_MESSAGE);
            }
            if (task != null && dataLoadMovieStatus != null) {
                task.onUpdate(defaultMsgLoadMovie, dataLoadMovieStatus, dataLoadMovieStatusEvent);
            }
        }
    }

    public int getAction() {
        return syncAction;
    }

    public SqliteDatabaseAdapter getDbAdapter() {
        return SqliteDatabaseAdapterImpl.getInstance();
    }

    /**
     * gets the right sync event. Every database operation has a different
     * sync event
     *
     * @return the sync event(update, insert, delete, etc).
     */
    protected abstract int getSyncEvent();

    /**
     * Database operation using the parameter data.
     *
     * @param data the data to be used for updating the
     *             database.
     * @return true if the operation has succeeded or false otherwise.
     */
    protected abstract boolean commit(U data);
}
