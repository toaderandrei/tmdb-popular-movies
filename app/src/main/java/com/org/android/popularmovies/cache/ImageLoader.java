package com.org.android.popularmovies.cache;

import android.content.Context;

/**
 * ImageLoader class that fetches images from the cache.
 */
public class ImageLoader extends ImageResizer {

    public ImageLoader(Context context, int thumbSize) {
        super(context, thumbSize);
    }


    //=======================Start of disk operations===============================//

    @Override
    protected void initDiskCacheInternal() {
        super.initDiskCacheInternal();
    }

    @Override
    protected void closeCacheInternal() {
        super.closeCacheInternal();
    }

    @Override
    protected void clearCacheInternal() {
        super.clearCacheInternal();
    }

    @Override
    protected void flushCacheInternal() {
        super.flushCacheInternal();
    }

    //======================End of disk operations==============================//
}
