package com.org.android.popularmovies.cache;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.util.Log;

import com.org.android.popularmovies.httpclient.HttpClient;
import com.org.android.popularmovies.utils.FileUtils;
import com.org.android.popularmovies.utils.LogUtils;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Class that deals with image resizing and also loading the bitmap with picasso
 * from a given URL.
 */
public class ImageResizer extends ImageWorker {

    private static final String TAG = "ImageResizer";
    protected int mImageWidth;
    protected int mImageHeight;
    private DisplayMetrics displayMetrics;

    private DiskLruCache mHttpDiskLruCache;
    private File mHttpCacheDir;
    private final Object mHttpDiskCacheLock = new Object();

    private static final int HTTP_CACHE_SIZE = 20 * 1024 * 1024; // 20MB
    private static final String HTTP_CACHE_DIR = "http_cache";
    private static final int IO_BUFFER_SIZE = 8 * 1024;
    private boolean mHttpDiskCacheStarting = true;
    private static final int DISK_CACHE_INDEX = 0;

    public ImageResizer(Context context, int mSize) {
        super(context);
        displayMetrics = getDisplayMetrics();
        this.setImageSize(mSize);
        mHttpCacheDir = FileUtils.getDiskCacheDir(context, HTTP_CACHE_DIR);
    }

    private int getDisplayMetricsWidth() {
        if (displayMetrics == null) {
            throw new IllegalStateException("It cannot be null");
        }
        return displayMetrics.widthPixels;
    }

    private int getDisplayMetricsHeight() {
        if (displayMetrics == null) {
            throw new IllegalStateException("it cannot be null");
        }
        return displayMetrics.heightPixels;
    }

    @NonNull
    private DisplayMetrics getDisplayMetrics() {
        final DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics;
    }

    @Override
    protected void initDiskCacheInternal() {
        super.initDiskCacheInternal();
        initHttpCacheInternal();
    }

    @Override
    protected void clearCacheInternal() {
        super.clearCacheInternal();
        synchronized (mHttpDiskCacheLock) {
            if (mHttpDiskLruCache != null && !mHttpDiskLruCache.isClosed()) {
                try {
                    mHttpDiskLruCache.delete();
                    Log.d(TAG, "delete successful");
                } catch (IOException ioex) {
                    Log.e(TAG, "error when deleting the cache dir", ioex);
                }
                mHttpDiskLruCache = null;
                mHttpDiskCacheStarting = true;
                initHttpCacheInternal();
            }
        }
    }

    @Override
    protected void flushCacheInternal() {
        super.flushCacheInternal();
        synchronized (mHttpDiskCacheLock) {
            if (mHttpDiskLruCache != null) {
                try {
                    mHttpDiskLruCache.flush();
                } catch (IOException ioex) {
                    Log.e(TAG, "http ioex when flushing the cache", ioex);
                }
            }
        }
    }

    @Override
    protected void closeCacheInternal() {
        super.closeCacheInternal();
        synchronized (mHttpDiskCacheLock) {
            if (mHttpDiskLruCache != null && !mHttpDiskLruCache.isClosed()) {
                try {
                    mHttpDiskLruCache.close();
                    mHttpDiskLruCache = null;
                    Log.d(TAG, "delete successful.");
                } catch (IOException ioex) {
                    Log.e(TAG, "error when deleting the cache dir", ioex);
                }
            }
        }
    }

    protected void initHttpCacheInternal() {
        if (!mHttpCacheDir.exists()) {
            mHttpCacheDir.mkdirs();
        }
        synchronized (mHttpDiskCacheLock) {
            if (FileUtils.getUsableSpace(mHttpCacheDir) > HTTP_CACHE_SIZE) {
                try {
                    mHttpDiskLruCache = DiskLruCache.open(mHttpCacheDir, 1, 1, HTTP_CACHE_SIZE);
                } catch (IOException ioex) {
                    Log.e(TAG, "exception when trying to initialize the http cache dir");
                    mHttpDiskLruCache = null;
                }
            }
            mHttpDiskCacheStarting = false;
            mHttpDiskCacheLock.notifyAll();
        }

    }

    /**
     * processes the bitmap from the url.
     *
     * @param data string representation of the url.
     * @return the bitmap loaded from url.
     */
    @Override
    protected Bitmap processBitmap(@NonNull String data) {

        String key = FileUtils.hashKeyForDisk(data);

        FileDescriptor fileDescriptor = null;
        FileInputStream fileInputStream = null;
        Bitmap bitmap = null;
        synchronized (mHttpDiskCacheLock) {
            while (mHttpDiskCacheStarting) {
                try {
                    mHttpDiskCacheLock.wait();
                } catch (InterruptedException iex) {
                }
            }

            if (mHttpDiskLruCache != null) {
                DiskLruCache.Snapshot snapshot;
                try {
                    snapshot = mHttpDiskLruCache.get(key);
                    if (snapshot == null) {
                        DiskLruCache.Editor editor = mHttpDiskLruCache.edit(key);
                        if (editor != null) {
                            OutputStream outputStream = editor.newOutputStream(DISK_CACHE_INDEX);
                            if (downloadUrlToStream(data, outputStream)) {
                                Log.d(TAG, "download successful. edit. committing...");
                                editor.commit();
                            } else {
                                Log.d(TAG, "download unsuccessful. could not edit. abort.");
                                //else we abort the editing.
                                editor.abort();
                            }
                        }
                        snapshot = mHttpDiskLruCache.get(key);
                    }
                    //if the snapshot is not null, therefore, have something saved.
                    if (snapshot != null) {
                        fileInputStream = (FileInputStream) snapshot.getInputStream(DISK_CACHE_INDEX);
                        fileDescriptor = fileInputStream.getFD();
                    }
                } catch (IOException ioex) {
                    Log.e(TAG, "IOException");
                } finally {
                    if (fileDescriptor == null && fileInputStream != null) {
                        closeFileInputStreamInternal(fileInputStream);
                    }
                }
            }
        }
        if (fileDescriptor != null) {
            try {
                bitmap = ImageUtils.decodeBitmapFromFileDescriptor(fileDescriptor, mImageWidth, mImageHeight, getImageCache());
            } catch (IllegalArgumentException illex) {
                Log.e(TAG, "error decoding bitmap:", illex);
                bitmap = null;
            }
        }
        if (fileInputStream != null) {
            closeFileInputStreamInternal(fileInputStream);
        }
        Log.d(TAG, "return bitmap");
        return bitmap;
    }

    private void closeFileInputStreamInternal(FileInputStream fileInputStream) {
        try {
            fileInputStream.close();
        } catch (IOException ioex) {
            Log.e(TAG, "error when closing the input stream.");
        }
    }

    /**
     * Download a bitmap from a URL and write the content to an output stream.
     *
     * @param urlString The URL to fetch
     * @return true if successful, false otherwise
     */
    public boolean downloadUrlToStream(String urlString, OutputStream outputStream) {
        return HttpClient.getInstance().loadDataToStream(urlString, outputStream);
    }

    @Override
    protected Bitmap processBitmapWithPicasso(String data) {
        try {
            return HttpClient.getInstance().getBitmapFromServer(mImageWidth, mImageHeight, getContext(), data);

        } catch (Throwable ex) {
            LogUtils.exception("exception in loading image with picasso from Url:" + data);
            if (ex instanceof OutOfMemoryError) {
                super.clearCache();
            }
            return null;
        }
    }

    public void setImageSize(int imageSize) {
        this.mImageWidth = imageSize;
        this.mImageHeight = imageSize;
    }
}
