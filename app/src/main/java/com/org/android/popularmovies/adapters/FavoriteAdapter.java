package com.org.android.popularmovies.adapters;

import android.content.Context;
import android.graphics.Movie;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.org.android.popularmovies.activities.ImageLoaderCallback;
import com.org.android.popularmovies.activities.MovieItemClickedCallback;
import com.org.android.popularmovies.model.MovieItem;

import java.util.List;

/**
 * Created by andrei on 12/14/15.
 */
public class FavoriteAdapter extends MoviesAdapter {


    public FavoriteAdapter(Context context, MovieItemClickedCallback listener, ImageLoaderCallback loaderCallback, List<MovieItem> items, boolean showProgressForLoadMore) {
        super(context, listener, loaderCallback, items, showProgressForLoadMore);
    }

    @NonNull
    @Override
    protected RecyclerView.ViewHolder getViewHolder(ViewGroup parent) {
        return super.getViewHolder(parent);
    }

    @Override
    protected RecyclerView.ViewHolder onCreateItemHolder(ViewGroup parent, int viewType) {
        return new FavoriteViewHolder(super.getView(parent));
    }


    @Override
    protected void bindItem(MovieHolder holder, MovieItem item) {
        super.bindItem(holder, item);
    }

    public class FavoriteViewHolder extends MovieHolder {

        public FavoriteViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void updateFavoriteButton(MovieItem item) {
            super.updateFavoriteButton(item);
            if (!item.isFavored()) {
                removeInternal(item);
            }
        }
    }
}
