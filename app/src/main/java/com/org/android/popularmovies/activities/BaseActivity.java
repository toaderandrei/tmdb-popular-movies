package com.org.android.popularmovies.activities;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.org.android.popularmovies.R;
import com.org.android.popularmovies.cache.ImageCache;
import com.org.android.popularmovies.cache.ImageLoader;
import com.org.android.popularmovies.cache.ImageWorker;
import com.org.android.popularmovies.model.MovieItem;
import com.org.android.popularmovies.widgets.ResizedImageView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 *
 * */
public abstract class BaseActivity extends AppCompatActivity implements ImageLoaderCallback, BaseActivityCallback {

    private static final String TAG = BaseActivity.class.getCanonicalName();
    @Nullable
    @BindView(R.id.toolbar)
    protected Toolbar mToolbar;
    protected ImageLoader loader;
    protected String imageFolder = "";
    private static final String IMAGE_FOLDER = "base_asctivity_folder";

    @CallSuper
    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        ButterKnife.bind(this);
        setupToolbar();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initImageFolder();
        initImageLoader();
    }

    protected void initImageFolder() {
        this.imageFolder = IMAGE_FOLDER;
    }

    private void initImageLoader() {
        Log.d(TAG, "initializing the ImageLoader");
        //two image loaders for the same activity
        int imageThumbSize = getResources().getDimensionPixelSize(R.dimen.image_thumbnail_size);
        ImageCache.ImageCacheParams cacheParams = new ImageCache.ImageCacheParams(this, imageFolder);
        loader = new ImageLoader(this, imageThumbSize);

        cacheParams.setMemCacheSizePercent(0.35f); // Set memory cache to 35% of app memory
        loader.addImageCache(this.getFragmentManager(), cacheParams);
    }


    @Override
    protected void onPause() {

        loader.setExitTaskEarly(true);
        loader.flushCache();
        super.onPause();
    }

    @Nullable
    public final Toolbar getToolbar() {
        return mToolbar;
    }

    private void setupToolbar() {
        if (mToolbar == null) {
            Log.w(TAG, "Didn't find a toolbar");
            return;
        }

        ViewCompat.setElevation(mToolbar, getResources().getDimension(R.dimen.toolbar_elevation));
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar == null) {
            return;
        }
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setHomeButtonEnabled(false);
    }

    @Override
    public void loadImageAsync(String url, ResizedImageView imageView) {
        loadImageAsync(url, imageView, null);
    }

    @Override
    public void loadImageAsync(String url, ResizedImageView imageView, ImageWorker.Callback callback) {
        if (loader != null) {
            Log.d(TAG, "loading images from db or server: " + url);
            loader.loadImage(url, imageView, callback);
        }
    }

    @Override
    public void cancelLoadingIfRunning(ResizedImageView imageView) {
        if (loader != null) {
            loader.cancelLoadingIfRunning(imageView);
        }
    }

    @Override
    public void onError() {

    }

    @Override
    public void onSuccess() {

    }

    @Override
    public abstract void onMovieClickedCallback(MovieItem item);

    @Override
    public abstract void clearCaches();
}

