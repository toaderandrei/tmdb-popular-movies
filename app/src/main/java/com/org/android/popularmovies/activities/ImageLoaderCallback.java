package com.org.android.popularmovies.activities;

import android.widget.ImageView;

import com.org.android.popularmovies.cache.ImageWorker;
import com.org.android.popularmovies.widgets.ResizedImageView;

/**
 * Loads an image from an URL.
 */
public interface ImageLoaderCallback {

    /**
     * loads image async from a specific url. However, if the image is on the sdcard is fetched from there.
     *
     * @param url       url from where to load the image.
     * @param imageView the image view where to store the loaded image.
     */
    void loadImageAsync(String url, ResizedImageView imageView);

    /**
     * load the image asynchronous from a url using a callback to the UI.
     *
     * @param url       - the url from where to load the image.
     * @param imageView the imageview where to load the Bitmap.
     * @param callback  callback that should notify after the loading has finished.
     */
    void loadImageAsync(String url, ResizedImageView imageView, ImageWorker.Callback callback);

    void cancelLoadingIfRunning(ResizedImageView imageView);
}
