package com.org.android.popularmovies.cache;

import android.app.FragmentManager;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.ImageView;

import com.org.android.popularmovies.R;
import com.org.android.popularmovies.application.PopularMoviesApp;
import com.org.android.popularmovies.synchronization.impl.thread.CustomAsyncTask;
import com.org.android.popularmovies.utils.ApiVersion;
import com.org.android.popularmovies.utils.LogUtils;
import com.org.android.popularmovies.widgets.ResizedImageView;
import com.org.android.popularmovies.widgets.scrollable.view.ObservableScrollView;

import java.lang.ref.WeakReference;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * abstract class that deals with processing of the bitmaps in disk cache.
 */
public abstract class ImageWorker {

    private Context context;
    private static final String TAG = "ImageWorker";
    private ImageCache mImageCache;
    private ImageCache.ImageCacheParams mImageCacheParams;
    private Bitmap mLoadingBitmap;
    private Bitmap noImageDrawable;
    protected boolean mPauseWork = false;
    private final Object mPauseWorkLock = new Object();

    protected Resources mResources;
    private static final int MESSAGE_CLEAR = 0;
    private static final int MESSAGE_INIT_DISK_CACHE = 1;
    private static final int MESSAGE_FLUSH = 2;
    private static final int MESSAGE_CLOSE = 3;
    private boolean exitTaskEarly;

    public ImageWorker(Context context) {
        this.context = context;
        mLoadingBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.icon_loading);
        noImageDrawable = BitmapFactory.decodeResource(getResources(), R.drawable.no_image_found);

    }

    private Resources getResources() {
        if (context != null) {
            return context.getResources();
        }
        return PopularMoviesApp.getInstance().getResources();
    }

    protected Context getContext() {
        return context;
    }

    public void addImageCache(FragmentManager fragmentManager,
                              ImageCache.ImageCacheParams cacheParams) {
        mImageCacheParams = cacheParams;
        mImageCache = ImageCache.getInstance(fragmentManager, mImageCacheParams);
        new CacheAsyncTask().execute(MESSAGE_INIT_DISK_CACHE);
    }


    /**
     * loads the image from either the cache or from the server.
     *
     * @param data      the url from where to get the data.
     * @param imageView the ImageView to populate with the bitmap data.
     */
    public void loadImage(Object data, ResizedImageView imageView) {
        loadImageInternal(data, imageView, null);
    }

    /**
     * loads the image from either the cache or from the server.
     *
     * @param data      the url from where to get the data.
     * @param imageView the ImageView to populate with the bitmap data.
     * @param callback  - callback to notify the status of the async task.
     */
    public void loadImage(Object data, ResizedImageView imageView, Callback callback) {
        loadImageInternal(data, imageView, callback);
    }

    private void loadImageInternal(Object data, ResizedImageView imageView, Callback callback) {
        if (data == null) {
            imageView.setImageBitmap(noImageDrawable);
            return;
        }
        BitmapDrawable bmp = null;
        if (mImageCache != null) {
            bmp = mImageCache.getBitmapFromCache(String.valueOf(data));
        }

        if (bmp != null) {
            setScaleTypeXY(imageView);
            imageView.setImageDrawable(bmp);
            if (callback != null) {
                callback.onSuccess();
            }
        } else {
            loadImageAsync((String) data, imageView, callback);
        }
    }

    private void loadImageAsync(String data, ResizedImageView resizedImageView, Callback callback) {
        if (isPotentialWorkCanceledOrNotStarted(data, resizedImageView)) {
            Log.d(TAG, "task: " + data + " - is starting...");
            final BitmapWorkTask task = new BitmapWorkTask(data, resizedImageView, callback);
            initAndRunAsyncTask(resizedImageView, task);
        }
    }

    private void initAndRunAsyncTask(ResizedImageView imageView, BitmapWorkTask task) {

        AsyncDrawable asyncDrawable = new AsyncDrawable(getResources(), mLoadingBitmap, task);
        //meanwhile while loading....
        setScaleXY(ImageView.ScaleType.FIT_XY, mLoadingBitmap, imageView);
        imageView.setAspectRation(0);

        imageView.setImageDrawable(asyncDrawable);
        //imageView.startAnimation(createAndStartAnimation());
        //run the task
        if (ApiVersion.hasHoneycomb()) {
            task.executeOnExecutor(CustomAsyncTask.DUAL_THREAD_EXECUTOR);
        } else {
            //single execute.
            task.execute();
        }
    }

    private void setScaleXY(ImageView.ScaleType type, Bitmap bmp, ResizedImageView imageView) {
        imageView.setScaleType(type);
        imageView.setImageBitmap(bmp);
    }

    protected boolean isPotentialWorkCanceledOrNotStarted(String data, ResizedImageView imageView) {
        boolean canceled = true;
        BitmapWorkTask bitmapWorkTask = getBitmapWorkTask(imageView);
        if (bitmapWorkTask != null) {
            String mData = bitmapWorkTask.data;
            if (mData == null || !mData.equals(data)) {
                bitmapWorkTask.cancel(true);
                canceled = true;
            } else {
                //same work in progress, just do it!.
                canceled = false;
            }
        }
        return canceled;
    }

    protected BitmapWorkTask getBitmapWorkTask(ResizedImageView resizedImageView) {

        if (resizedImageView != null) {
            Drawable drawable = resizedImageView.getDrawable();
            if (drawable instanceof AsyncDrawable) {
                AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
                return asyncDrawable.getBitmapWorkerTask();
            }
        }
        return null;
    }

    public void setExitTaskEarly(boolean exitTaskEarly) {
        this.exitTaskEarly = exitTaskEarly;
        setPauseWork(false);
    }

    public void cancelLoadingIfRunning(ResizedImageView imageView) {
        BitmapWorkTask task = getBitmapWorkTask(imageView);
        if (task != null) {
            task.cancel(true);
            Log.d(TAG, "task is cancelled by request.");
        }
    }

    /**
     * Drawable that will be attached to the view while there is a work in progress.
     */
    public class AsyncDrawable extends BitmapDrawable {
        private WeakReference<BitmapWorkTask> bitmapWorkTaskWeakReference;

        public AsyncDrawable(Resources res, Bitmap bmp, BitmapWorkTask task) {
            super(res, bmp);
            bitmapWorkTaskWeakReference = new WeakReference<BitmapWorkTask>(task);
        }

        public BitmapWorkTask getBitmapWorkerTask() {
            return bitmapWorkTaskWeakReference.get();
        }
    }

    /**
     * Pause any ongoing background work. This can be used as a temporary
     * measure to improve performance. For example background work could
     * be paused when a ListView or GridView is being scrolled using a
     * {@link android.widget.AbsListView.OnScrollListener} to keep
     * scrolling smooth.
     * <p>
     * If work is paused, be sure setPauseWork(false) is called again
     * before your fragment or activity is destroyed (for example during
     * {@link android.app.Activity#onPause()}), or there is a risk the
     * background thread will never finish.
     */
    protected void setPauseWork(boolean pauseWork) {
        synchronized (mPauseWorkLock) {
            mPauseWork = pauseWork;
            if (!mPauseWork) {
                mPauseWorkLock.notifyAll();
            }
        }
    }

    public boolean isExitTaskEarly() {
        return exitTaskEarly;
    }

    /**
     * AsyncTask that fetches the image either the cache or from an URL.
     * It first tries to fetch it from the disk cache and then, if not found,
     * it tries to load it from the the server based on the data url.
     */
    protected class BitmapWorkTask extends CustomAsyncTask<Void, Void, BitmapDrawable> {

        private String data;
        private Callback callback;
        private WeakReference<ResizedImageView> imageViewWeakReference;

        public BitmapWorkTask(String data, ResizedImageView imageView) {
            this.data = data;
            imageViewWeakReference = new WeakReference<ResizedImageView>(imageView);
        }

        public BitmapWorkTask(String data, ResizedImageView imageView, Callback callback) {
            this.data = data;
            imageViewWeakReference = new WeakReference<ResizedImageView>(imageView);
            this.callback = callback;
        }


        @Override
        protected BitmapDrawable doInBackground(Void... params) {

            BitmapDrawable bmpDrawable = null;
            Bitmap bitmap = null;
            //checkIfPausedBefore();
            synchronized (mPauseWorkLock) {
                while (mPauseWork && !isCancelled()) {
                    try {
                        mPauseWorkLock.wait();
                    } catch (InterruptedException iex) {
                    }
                }
            }
            if (mImageCache != null && !isCancelled() && getAttachedImageView() != null && !isExitTaskEarly()) {
                bitmap = mImageCache.getBitmapFromDiskCache(data);
            }

            if (bitmap == null && !isCancelled() && getAttachedImageView() != null && !isExitTaskEarly()) {
                //bitmap = processBitmap(data); // we handle everything.
                bitmap = processBitmap(data);// - picasso handles everything
            }

            if (bitmap != null) {
                if (ApiVersion.hasHoneycomb()) {
                    bmpDrawable = new BitmapDrawable(mResources, bitmap);
                }
                if (mImageCache != null && bmpDrawable != null) {
                    LogUtils.info("bitmap is in process of being added to the cache");
                    mImageCache.addBitmapToImageCache(data, bmpDrawable);
                    LogUtils.info("bitmap drawable is not null");
                }
            }
            Log.d(TAG, "onDoInBackground task finished.");
            return bmpDrawable;
        }

        @Override
        protected void onPostExecute(BitmapDrawable bitmapDrawable) {
            if (isCancelled() || isExitTaskEarly()) {
                bitmapDrawable = null;
                if (callback != null) {
                    callback.onFail();
                }
            }

            ResizedImageView resImageView = getAttachedImageView();
            if (bitmapDrawable != null && resImageView != null) {
                setScaleTypeXY(resImageView);
                resImageView.setImageDrawable(bitmapDrawable);
                if (callback != null) {
                    callback.onSuccess();
                }
            } else {
                if (callback != null) {
                    callback.onFail();
                }
            }
        }

        @Override
        protected void onCancelled(BitmapDrawable value) {
            super.onCancelled(value);
            synchronized (mPauseWorkLock) {
                mPauseWorkLock.notifyAll();
            }
        }


        //===================getters and setters==================================================//

        /**
         * Returns the ImageView associated with this task as long as the ImageView's task still
         * points to this task as well. Returns null otherwise.
         */
        private ResizedImageView getAttachedImageView() {
            final ResizedImageView resizedImageView = imageViewWeakReference.get();
            final BitmapWorkTask bitmapWorkerTask = getBitmapWorkTask(resizedImageView);

            if (this == bitmapWorkerTask) {
                return resizedImageView;
            }
            return null;
        }
    }

    private void setScaleTypeXY(@NonNull ResizedImageView resImageView) {
        resImageView.setAspectRation(0.675f);
        resImageView.setScaleType(ImageView.ScaleType.FIT_XY);
    }

    protected void initDiskCacheInternal() {
        if (mImageCache != null) {
            mImageCache.initDiskCacheInternal();
        }
    }

    protected void closeCacheInternal() {
        if (mImageCache != null) {
            mImageCache.closeCache();
            mImageCache = null;
        }
    }

    protected void flushCacheInternal() {
        if (mImageCache != null) {
            mImageCache.flushCacheInternal();
        }
    }

    protected void clearCacheInternal() {
        if (mImageCache != null) {
            mImageCache.clearCacheInternal();
        }
    }


    protected class CacheAsyncTask extends CustomAsyncTask<Object, Void, Void> {

        @Override
        protected Void doInBackground(Object... params) {
            switch ((Integer) params[0]) {
                case MESSAGE_CLEAR:
                    clearCacheInternal();
                    break;
                case MESSAGE_INIT_DISK_CACHE:
                    initDiskCacheInternal();
                    break;
                case MESSAGE_FLUSH:
                    flushCacheInternal();
                    break;
                case MESSAGE_CLOSE:
                    closeCacheInternal();
                    break;
            }
            return null;
        }

    }

    protected abstract Bitmap processBitmapWithPicasso(String data);

    protected abstract Bitmap processBitmap(String data);


    public void clearCache() {
        new CacheAsyncTask().execute(MESSAGE_CLEAR);
    }

    public void flushCache() {
        new CacheAsyncTask().execute(MESSAGE_FLUSH);
    }

    public void closeCache() {
        new CacheAsyncTask().execute(MESSAGE_CLOSE);
    }

    protected ImageCache getImageCache() {
        return mImageCache;
    }

    public interface Callback {
        /**
         * notify when success occurs.
         */
        void onSuccess();

        /**
         * notify in case of a failure
         */
        void onFail();
    }
}