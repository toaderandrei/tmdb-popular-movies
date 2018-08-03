package com.org.android.popularmovies.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.ViewAnimator;

import com.org.android.popularmovies.R;
import com.org.android.popularmovies.activities.BaseActivityCallback;
import com.org.android.popularmovies.activities.ImageLoaderCallback;
import com.org.android.popularmovies.activities.MovieItemClickedCallback;
import com.org.android.popularmovies.adapters.MoviesAdapter;
import com.org.android.popularmovies.controller.DataController;
import com.org.android.popularmovies.model.MovieItem;
import com.org.android.popularmovies.observer.DataObservable;
import com.org.android.popularmovies.synchronization.interfaces.SyncCallback;
import com.org.android.popularmovies.utils.LogUtils;
import com.org.android.popularmovies.widgets.CustomView;
import com.org.android.popularmovies.widgets.EndlessScrollListener;
import com.org.android.popularmovies.widgets.MultiSwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A placeholder fragment containing a simple view.
 */
public abstract class MoviesFragment extends Fragment implements
        SwipeRefreshLayout.OnRefreshListener,
        MultiSwipeRefreshLayout.CanChildScrollUpCallback,
        MovieItemClickedCallback,
        EndlessScrollListener.OnLoadMoreCallback,
        DataObservable {

    private static final String TAG = MoviesFragment.class.getCanonicalName();
    private static final String STATE_CURRENT_PAGE = "state_current_page";
    private MoviesAdapter adapter;
    @BindView(R.id.multi_swipe_refresh_layout)
    MultiSwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.view_animator_id)
    ViewAnimator mViewAnimator;
    @BindView(R.id.movies_recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.view_empty)
    CustomView emptyView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    protected int mSelectedPos = -1;
    protected int mCurrentPage = 0;
    private static final int VISIBLE_THRESHOLD = 10;
    private GridLayoutManager mGridLayoutManager;
    protected static final String STATE_MOVIES = "STATE_MOVIES";
    protected static final String STATE_SELECTED_POSITION = "STATE_SELECTED_POSITION";
    protected static final int ANIMATOR_VIEW_LOADING = R.id.view_loading;
    protected static final int ANIMATOR_VIEW_CONTENT = R.id.movies_recycler_view;
    private EndlessScrollListener mEndlessScrollListener;
    protected BaseActivityCallback activityCallback;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getDataController().registerListener(this);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, root);
        return root;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(STATE_MOVIES, new ArrayList<>(adapter.getAllItems()));
        outState.putInt(STATE_SELECTED_POSITION, mSelectedPos);
        outState.putInt(STATE_CURRENT_PAGE, mCurrentPage);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mSelectedPos = savedInstanceState != null ? savedInstanceState.getInt(STATE_SELECTED_POSITION, -1) : -1;
        mCurrentPage = savedInstanceState != null ? savedInstanceState.getInt(STATE_CURRENT_PAGE, -1) : 0;
    }

    protected void saveMoviesToCache(List<?> restoredMovies) {
        if (restoredMovies != null) {
            getDataController().addMoviesToCache((List<MovieItem>) restoredMovies);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (activityCallback == null) {
            activityCallback = (BaseActivityCallback) getActivity();
        }
        ImageLoaderCallback loaderCallback = (ImageLoaderCallback) getActivity();
        List<?> restoredMovies = savedInstanceState != null ? savedInstanceState.getParcelableArrayList(STATE_MOVIES) : new ArrayList<>();
        saveMoviesToCache(restoredMovies);
        adapter = getMoviesAdapter(loaderCallback, (List<MovieItem>) restoredMovies);
        initSwipeRefreshLayout();
        initRecyclerView();
        mViewAnimator.setDisplayedChild((mSelectedPos == 0) ? ANIMATOR_VIEW_LOADING : ANIMATOR_VIEW_CONTENT);
    }

    @NonNull
    protected MoviesAdapter getMoviesAdapter(ImageLoaderCallback loaderCallback, List<MovieItem> restoredMovies) {
        return new MoviesAdapter(this.getActivity(), this, loaderCallback, restoredMovies);
    }


    @Override
    public void onResume() {
        super.onResume();
        if (adapter != null) {
            Log.d(TAG, "notify dataset changed from onResume");
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDetach() {
        clearAdapter();
        super.onDetach();
    }


    protected void updateUIInternal(List<MovieItem> movieItems, int event) {
        LogUtils.info("Event id:" + event);
        mCurrentPage++;
        updateAdapter(movieItems);
        mViewAnimator.setDisplayedChild(ANIMATOR_VIEW_CONTENT);
        saveMoviesToCache(movieItems);
    }

    protected void updateAdapter(List<MovieItem> movieItems) {
        if (adapter != null) {
            adapter.addAll(movieItems);
        }
    }

    @Override
    public boolean canSwipeRefreshChildScrollUp() {
        return mRecyclerView != null &&
                mRecyclerView.canScrollVertically(-1) &&
                mViewAnimator != null &&
                mViewAnimator.getDisplayedChild() == ANIMATOR_VIEW_LOADING;
    }

    private void initSwipeRefreshLayout() {
        mSwipeRefreshLayout.setColorSchemeColors(getResources().getIntArray(R.array.swipe_progress_colors));
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setCanChildScrollUpCallback(this);
    }

    protected void initRecyclerView() {
        mGridLayoutManager = new GridLayoutManager(this.getActivity(), getResources().getInteger(R.integer.movie_columns));
        mGridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                int spanSizeCount = mGridLayoutManager.getSpanCount();
                return adapter.isLoadMore(spanSizeCount) ? spanSizeCount : 1;
            }
        });
        mRecyclerView.setLayoutManager(mGridLayoutManager);
        mRecyclerView.setAdapter(adapter);
        if (mSelectedPos != -1) {
            mRecyclerView.scrollToPosition(mSelectedPos);
        }
    }

    @Override
    public void onMovieItemClicked(MovieItem item, int pos) {
        mSelectedPos = pos;
        if (activityCallback != null) {
            activityCallback.onMovieClickedCallback(item);
        }
    }

    @Override
    public void onFavoredMovieItemClicked(MovieItem item, SyncCallback<MovieItem> callback) {
        getDataController().updateMovieToTheDb(item, callback);
    }

    @Override
    public void loadMovieFavoriteStatusIfExistsInDb(MovieItem item, SyncCallback<MovieItem> mCallback) {
        Log.d(TAG, "update the movies to the db.." + item.isFavored());
        getDataController().loadMovieStatusFromDbIfExists(item, mCallback);
    }

    protected void stopRefreshing() {
        mSwipeRefreshLayout.setRefreshing(false);
    }

    protected void clearAdapter() {
        if (adapter != null) {
            adapter.clearAll();
        }
    }

    protected void reInitializeScrollView(int page) {
        if (mEndlessScrollListener != null) {
            mRecyclerView.removeOnScrollListener(mEndlessScrollListener);
        }
        mEndlessScrollListener = EndlessScrollListener.fromGridLayoutManager(mGridLayoutManager, VISIBLE_THRESHOLD, page);

        mEndlessScrollListener.setCallback(this);
        mRecyclerView.addOnScrollListener(mEndlessScrollListener);
    }

    @Override
    public void onLoadMore(int page, int totalItemsCount) {
        if (adapter != null) {
            loadPage(page);
        }
    }

    protected void reloadItems() {
        if (!mSwipeRefreshLayout.isRefreshing()) {
            mViewAnimator.setDisplayedChild(ANIMATOR_VIEW_LOADING);
        }
        mSelectedPos = -1;
        loadPage(1);
        reInitializeScrollView(0);
    }

    protected void clearCaches() {
        clearAdapter();
        getDataController().clearCaches();
        if (activityCallback != null) {
            activityCallback.clearCaches();
        }
    }

    protected abstract void loadPage(int page);

    protected abstract void loadMovies();

    protected List<MovieItem> getMovieList() {
        return DataController.getInstance().getMovieList();
    }

    protected boolean isDataCacheEmpty() {
        List<MovieItem> movieItems = getMovieList();
        return movieItems == null || movieItems.isEmpty();
    }

    protected DataController getDataController() {
        return DataController.getInstance();
    }

    //=================abstract methods============================================================//
}
