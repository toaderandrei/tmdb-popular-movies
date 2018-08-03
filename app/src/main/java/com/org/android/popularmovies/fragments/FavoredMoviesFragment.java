package com.org.android.popularmovies.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.org.android.popularmovies.activities.ImageLoaderCallback;
import com.org.android.popularmovies.adapters.FavoriteAdapter;
import com.org.android.popularmovies.adapters.MoviesAdapter;
import com.org.android.popularmovies.model.MovieItem;
import com.org.android.popularmovies.synchronization.interfaces.SyncCallback;

import java.util.Collections;
import java.util.List;

/**
 * Fragment that contains all the favorite movies.
 */
public class FavoredMoviesFragment extends MoviesFragment {


    @Override
    protected void loadPage(int page) {
        //nothing to load as we do not want to load
        //anything what is not in the cache.
    }

    @Override
    public void updateFailed(String message) {

    }

    @Override
    public void updateSuccess(String message) {

    }

    @Override
    public void onRefresh() {
        stopRefreshing();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loadMovies();
    }

    ProgressDialog dialog;

    @Override
    protected List<MovieItem> getMovieList() {

        return Collections.EMPTY_LIST;
    }

    @Override
    protected void loadMovies() {
        updateAdapter(getMovieList());
        showDialog();
        getDataController().loadFavoredMoviesFromDb(mFavoriteMoviesCallback);
    }

    private void showDialog() {
        dialog = new ProgressDialog(getActivity());
        dialog.show();
    }

    @Override
    public void onFavoredMovieItemClicked(MovieItem item, SyncCallback<MovieItem> callback) {
        super.onFavoredMovieItemClicked(item, callback);
    }

    @NonNull
    @Override
    protected MoviesAdapter getMoviesAdapter(ImageLoaderCallback loaderCallback, List<MovieItem> restoredMovies) {
        return new FavoriteAdapter(this.getActivity(), this, loaderCallback, restoredMovies, false);
    }

    SyncCallback<List<MovieItem>> mFavoriteMoviesCallback = new SyncCallback<List<MovieItem>>() {
        @Override
        public void onUpdateUI(String message, List<MovieItem> movieItems, int event) {
            if (movieItems != null && !movieItems.isEmpty()) {
                //we are sure now that all the movies are favorite movies.
                updateAdapter(movieItems);
            } else {
                if (emptyView != null) {
                    emptyView.setVisibility(View.VISIBLE);
                }
                Toast.makeText(getActivity(), "sorry, no favorite movies found!", Toast.LENGTH_SHORT).show();
            }
            if (dialog != null) {
                dialog.cancel();
                dialog = null;
            }
        }
    };
}
