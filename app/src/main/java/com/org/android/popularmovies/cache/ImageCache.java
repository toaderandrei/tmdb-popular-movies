package com.org.android.popularmovies.cache;

import android.annotation.TargetApi;
import android.app.FragmentManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.support.v4.util.LruCache;
import android.util.Log;

import com.org.android.popularmovies.application.PopularMoviesApp;
import com.org.android.popularmovies.utils.ApiVersion;
import com.org.android.popularmovies.utils.FileUtils;
import com.org.android.popularmovies.utils.LogUtils;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.SoftReference;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * ImageFileCache for bitmaps.
 */
public class ImageCache {
    //private File cacheDir;
    private static final String TAG = "ImageCache";
    // Default memory cache size in kilobytes
    private static final int DEFAULT_MEM_CACHE_SIZE = 1024 * 30; // 30MB
    private static final int DISK_CACHE_INDEX = 0;
    // Default disk cache size in bytes
    private static final int DEFAULT_DISK_CACHE_SIZE = 1024 * 1024 * 50; // 50MB

    private ImageCache.ImageCacheParams mCacheParams;
    private boolean mDiskCacheStarting = false;
    private final Object mDiskCacheLock = new Object();
    private DiskLruCache mDiskLruCache;

    // Constants to easily toggle various caches
    private static final boolean DEFAULT_MEM_CACHE_ENABLED = true;
    private static final boolean DEFAULT_DISK_CACHE_ENABLED = true;
    private static final int DEFAULT_COMPRESS_QUALITY = 90;

    private Set<SoftReference<Bitmap>> mReusableBitmaps;
    private LruCache<String, BitmapDrawable> mMemoryCache;


    private ImageCache(ImageCache.ImageCacheParams params) {
        init(params);
    }


    private void init(ImageCache.ImageCacheParams _mImageCacheParams) {
        mCacheParams = _mImageCacheParams;
        if (mCacheParams.memoryCacheEnabled) {
            LogUtils.debug("initializing the memory cache.");
            mReusableBitmaps = Collections.synchronizedSet(new HashSet<SoftReference<Bitmap>>());
            mMemoryCache = new LruCache<String, BitmapDrawable>(mCacheParams.memCacheSize) {
                @Override
                protected void entryRemoved(boolean evicted, String key, BitmapDrawable oldValue, BitmapDrawable newValue) {
                    if (ApiVersion.hasHoneycomb()) {
                        mReusableBitmaps.add(new SoftReference<Bitmap>(oldValue.getBitmap()));
                    }
                }

                @Override
                protected int sizeOf(String key, BitmapDrawable value) {
                    final int bitmapSize = ImageUtils.getBitmapSize(value) / 1024;
                    return bitmapSize == 0 ? 1 : bitmapSize;
                }
            };
        }
    }

    /**
     * Return an {@link ImageCache} instance. A {@link RetainFragment} is used to retain the
     * ImageCache object across configuration changes such as a change in device orientation.
     *
     * @param fragmentManager The fragment manager to use when dealing with the retained fragment.
     * @param cacheParams     The cache parameters to use if the ImageCache needs instantiation.
     * @return An existing retained ImageCache object or a new one if one did not exist
     */
    public static ImageCache getInstance(FragmentManager fragmentManager, ImageCache.ImageCacheParams cacheParams) {

        // Search for, or create an instance of the non-UI RetainFragment
        final RetainFragment mRetainFragment = findOrCreateRetainFragment(fragmentManager);

        // See if we already have an ImageCache stored in RetainFragment
        ImageCache imageCache = (ImageCache) mRetainFragment.getObject();

        // No existing ImageCache, create one and store it in RetainFragment
        if (imageCache == null) {
            imageCache = new ImageCache(cacheParams);
            mRetainFragment.setObject(imageCache);
        }

        return imageCache;
    }

    /**
     * Locate an existing instance of this Fragment or if not found, create and
     * add it using FragmentManager.
     *
     * @param fm The FragmentManager manager to use.
     * @return The existing instance of the Fragment or the new instance if just
     * created.
     */
    private static RetainFragment findOrCreateRetainFragment(FragmentManager fm) {
        //BEGIN_INCLUDE(find_create_retain_fragment)
        // Check to see if we have retained the worker fragment.
        RetainFragment mRetainFragment = (RetainFragment) fm.findFragmentByTag(TAG);

        // If not retained (or first time running), we need to create and add it.
        if (mRetainFragment == null) {
            mRetainFragment = new RetainFragment();
            fm.beginTransaction().add(mRetainFragment, TAG).commitAllowingStateLoss();
        }

        return mRetainFragment;
        //END_INCLUDE(find_create_retain_fragment)
    }

    protected void initDiskCacheInternal() {
        synchronized (mDiskCacheLock) {
            if (mDiskLruCache == null || mDiskLruCache.isClosed()) {
                File diskCacheDir = mCacheParams.diskCacheDir;
                if (diskCacheDir != null && mCacheParams.diskCacheEnabled) {
                    if (!diskCacheDir.exists()) {
                        diskCacheDir.mkdirs();
                    }
                    if (FileUtils.getUsableSpace(diskCacheDir) > mCacheParams.diskCacheSize) {
                        try {
                            mDiskLruCache = DiskLruCache.open(diskCacheDir, 1, 1, mCacheParams.diskCacheSize);
                        } catch (IOException ioex) {
                            mCacheParams.diskCacheDir = null;
                            LogUtils.exception("exception in setting up the cache dir.");
                        }
                    }
                }
            }
            mDiskCacheStarting = false;
            mDiskCacheLock.notifyAll();
        }
    }

    public BitmapDrawable getBitmapFromCache(String data) {
        BitmapDrawable bmp = null;
        LogUtils.info("getting the image from cache.");
        if (mMemoryCache != null) {
            bmp = mMemoryCache.get(data);
        }
        return bmp;
    }

    public Bitmap getBitmapFromDiskCache(String data) {
        Bitmap bmp = null;
        String hashKey = FileUtils.hashKeyForDisk(data);
        synchronized (mDiskCacheLock) {
            while (mDiskCacheStarting) {
                try {
                    mDiskCacheLock.wait();
                } catch (InterruptedException e) {
                    LogUtils.exception("interrupted exception with the message:" + e.getMessage());
                }
            }
            if (mDiskLruCache != null) {
                InputStream inputStream = null;
                try {
                    DiskLruCache.Snapshot snapshot = mDiskLruCache.get(hashKey);
                    if (snapshot != null) {
                        inputStream = snapshot.getInputStream(DISK_CACHE_INDEX);
                        if (inputStream != null) {
                            FileDescriptor fd = ((FileInputStream) inputStream).getFD();
                            try {
                                bmp = ImageUtils.decodeBitmapFromFileDescriptor(fd, Integer.MAX_VALUE, Integer.MAX_VALUE, this);
                            } catch (IllegalArgumentException illex) {
                                Log.e(TAG, "failed to decode the bitmap - ", illex);
                                bmp = null;
                            }
                        }
                    }
                } catch (IOException exc) {
                    LogUtils.exception("exception in: getting the file:" + exc.getMessage());
                }
            }
        }
        return bmp;
    }

    public Bitmap getBitmapFromReusableSet(BitmapFactory.Options options) {
        Bitmap bmp = null;

        if (mReusableBitmaps != null && !mReusableBitmaps.isEmpty()) {
            synchronized (mReusableBitmaps) {
                final Iterator<SoftReference<Bitmap>> it = mReusableBitmaps.iterator();
                while (it.hasNext()) {
                    Bitmap item = it.next().get();
                    if (item != null && item.isMutable()) {
                        if (canUseForInBitmap(item, options)) {
                            bmp = item;
                            it.remove();
                            break;
                        }
                    } else {
                        it.remove();
                    }
                }
            }
        }
        return bmp;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private boolean canUseForInBitmap(Bitmap candidate, BitmapFactory.Options options) {
        boolean canUseForInBitmap = false;
        if (!ApiVersion.hasKitKat()) {
            return candidate.getWidth() == options.outWidth &&
                    candidate.getHeight() == options.outHeight &&
                    options.inSampleSize == 1;
        }

        //if we have kitkat
        int widthRatio = options.outWidth / options.inSampleSize;
        int heightRatio = options.outHeight / options.inSampleSize;
        int byteCount = widthRatio * heightRatio * ImageUtils.getBytesPerPixel(candidate.getConfig());
        if (byteCount <= candidate.getAllocationByteCount()) {
            canUseForInBitmap = true;
        }
        return canUseForInBitmap;
    }


    //==============================cache recycling============================================//

    public void flushCacheInternal() {
        synchronized (mDiskCacheLock) {
            if (mDiskLruCache != null) {
                try {
                    mDiskLruCache.flush();
                    LogUtils.info("Internal Disk cache flushed.");
                } catch (IOException ioex) {
                }
            }
        }
    }

    public void closeCache() {
        synchronized (mDiskCacheLock) {
            if (mDiskLruCache != null && !mDiskLruCache.isClosed()) {
                try {
                    mDiskLruCache.close();
                    LogUtils.info("Internal Disk cache closed.");
                } catch (IOException ioex) {
                }
            }
        }
    }

    /**
     * clear all the caches and initializes at the end the disk cache.
     */
    public void clearCacheInternal() {
        if (mMemoryCache != null) {
            mMemoryCache.evictAll();
            Log.d(TAG, "internal clearing the memory cache.");
        }

        synchronized (mDiskCacheLock) {
            mDiskCacheStarting = true;
            if (mDiskLruCache != null && !mDiskLruCache.isClosed()) {
                try {
                    mDiskLruCache.delete();
                } catch (IOException ioex) {

                }
            }
            mDiskLruCache = null;
            initDiskCacheInternal();
        }
    }

    //==================================End of cache recycling====================================

    public void addBitmapToImageCache(String data, BitmapDrawable bmpDrawable) {
        if (bmpDrawable == null || data == null) {
            LogUtils.debug("drawable is null or data string.");
            return;
        }
        if (mMemoryCache != null) {
            mMemoryCache.put(data, bmpDrawable);
        }

        synchronized (mDiskCacheLock) {
            if (mDiskLruCache != null) {
                String hashKey = FileUtils.hashKeyForDisk(data);
                OutputStream out = null;
                try {
                    DiskLruCache.Snapshot snapshot = mDiskLruCache.get(hashKey);
                    if (snapshot == null) {
                        DiskLruCache.Editor editor = mDiskLruCache.edit(hashKey);
                        if (editor != null) {
                            out = editor.newOutputStream(DISK_CACHE_INDEX);
                            bmpDrawable.getBitmap().compress(Bitmap.CompressFormat.PNG, mCacheParams.compressQuality, out);
                            editor.commit();
                            out.close();
                        }
                    } else {
                        snapshot.getInputStream(DISK_CACHE_INDEX).close();
                    }
                } catch (IOException ioex) {

                } finally {
                    if (out != null) {
                        try {
                            out.close();
                        } catch (IOException ioex) {
                            LogUtils.exception("exception in closing the outputstream.");
                        }
                    }
                }
            }
        }
    }

    //==============================end of cache recycling============================================//


    //==============================Image caching params============================================//

    /**
     * A holder class that contains cache parameters.
     */
    public static class ImageCacheParams {
        protected int memCacheSize = DEFAULT_MEM_CACHE_SIZE;
        protected int diskCacheSize = DEFAULT_DISK_CACHE_SIZE;

        protected int compressQuality = DEFAULT_COMPRESS_QUALITY;
        protected boolean memoryCacheEnabled = DEFAULT_MEM_CACHE_ENABLED;
        protected boolean diskCacheEnabled = DEFAULT_DISK_CACHE_ENABLED;
        protected File diskCacheDir;

        /**
         * Create a set of image cache parameters that can be provided to
         * {@link ImageCache#getInstance(android.app.FragmentManager, com.org.android.popularmovies.cache.ImageCache.ImageCacheParams)} or
         * {@link ImageWorker#addImageCache(android.app.FragmentManager, com.org.android.popularmovies.cache.ImageCache.ImageCacheParams)}.
         *
         * @param context                A context to use.
         * @param diskCacheDirectoryName A unique subdirectory name that will be appended to the
         *                               application cache directory. Usually "cache" or "images"
         *                               is sufficient.
         */
        public ImageCacheParams(Context context, String diskCacheDirectoryName) {
            diskCacheDir = FileUtils.getDiskCacheDir(context, diskCacheDirectoryName);
        }

        /**
         * Sets the memory cache size based on a percentage of the max available VM memory.
         * Eg. setting percent to 0.2 would set the memory cache to one fifth of the available
         * memory. Throws {@link IllegalArgumentException} if percent is < 0.01 or > .8.
         * memCacheSize is stored in kilobytes instead of bytes as this will eventually be passed
         * to construct a LruCache which takes an int in its constructor.
         * <p>
         * This value should be chosen carefully based on a number of factors
         * Refer to the corresponding Android Training class for more discussion:
         * http://developer.android.com/training/displaying-bitmaps/
         *
         * @param percent Percent of available app memory to use to size memory cache
         */
        public void setMemCacheSizePercent(float percent) {
            if (percent < 0.01f || percent > 0.8f) {
                throw new IllegalArgumentException("setMemCacheSizePercent - percent must be "
                        + "between 0.01 and 0.8 (inclusive)");
            }
            memCacheSize = Math.round(percent * Runtime.getRuntime().maxMemory() / 1024);
        }
    }


    private static PopularMoviesApp getApp() {
        return PopularMoviesApp.getInstance();
    }
}
