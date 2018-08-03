package com.org.android.popularmovies.widgets;

import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.org.android.popularmovies.fragments.MoviesFragment;

/**
 * Scroll listener for endless loading.
 */
public abstract class EndlessScrollListener extends RecyclerView.OnScrollListener {

    // The minimum amount of items to have below your current scroll position
    // before loading more.
    private int visibleThreshold = 5;
    // The current offset index of data you have loaded
    private int currentPage = 0;
    // The total number of items in the dataset after the last load
    private int previousTotalItemCount = 0;
    // True if we are still waiting for the last set of data to load.
    private boolean loading = true;
    // Sets the starting page index
    private int startingPageIndex = 0;
    private MoviesFragment callback;

    public EndlessScrollListener(int visibleThreshold, int page) {
        this.visibleThreshold = visibleThreshold;
        this.currentPage = page;
    }

    public void setCallback(OnLoadMoreCallback callback) {
        this.mCallback = callback;
    }

    public interface OnLoadMoreCallback {
        void onLoadMore(int page, int totalItemsCount);
    }

    private OnLoadMoreCallback mCallback;

    public EndlessScrollListener() {/* nothing for now. */
    }

    public void onScrolled(int firstItem, int visibleItemCount, int totalItemCount) {

        //first time load, not items count.
        if (loading && totalItemCount < previousTotalItemCount) {
            this.previousTotalItemCount = totalItemCount;
            loading = false;
        }

        //this happens while we are loading and the number of items count has
        //increased above our current previous items count.
        if (loading && totalItemCount > previousTotalItemCount) {
            loading = false;
            previousTotalItemCount = totalItemCount;
            currentPage++;
        }

        // while not loading(from web server) and scrolling if the difference
        //between the total items count and visible threshold is less than
        //first visible item plus the threshold - gridview 6 minimum - then
        //we load more.
        //currentPage+1 means we load the next page.
        if (!loading && (totalItemCount - visibleItemCount) <= (firstItem + visibleThreshold)) {
            loading = true;
            mCallback.onLoadMore(currentPage + 1, totalItemCount);
        }
    }

    public static EndlessScrollListener fromGridLayoutManager(@NonNull final GridLayoutManager layoutManager,
                                                              int visibleThreshold,
                                                              int page) {
        return new EndlessScrollListener(visibleThreshold, page) {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy <= 0) return;

                final int firstVisibleItem = layoutManager.findFirstVisibleItemPosition();
                final int lastVisibleItem = layoutManager.findLastVisibleItemPosition();
                final int totalItemCount = layoutManager.getItemCount();

                onScrolled(firstVisibleItem, lastVisibleItem - firstVisibleItem, totalItemCount);
            }
        };
    }

}
