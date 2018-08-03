package com.org.android.popularmovies.fragments;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.org.android.popularmovies.R;
import com.org.android.popularmovies.controller.DataController;
import com.org.android.popularmovies.model.GenreItem;
import com.org.android.popularmovies.model.MovieItem;
import com.org.android.popularmovies.model.SortOrder;
import com.org.android.popularmovies.model.SortType;
import com.org.android.popularmovies.synchronization.interfaces.SyncCallback;
import com.org.android.popularmovies.utils.NetworkUtils;

import java.util.List;

/**
 * Fragment that deals with the sorting.
 */
public class SortedMoviesFragment extends MoviesFragment {

    private static final String SORT_TYPE_KEY = "SORT_TYPE";
    private static final String SORT_ORDER_KEY = "SORT_ORDER";
    private String sortType = SortType.POPULARITY.getSortType();
    private String sortOrder = SortOrder.DESC.getSortOrder();

    public static SortedMoviesFragment newInstance(String sortType, String sortOrder) {
        SortedMoviesFragment fragment = new SortedMoviesFragment();
        Bundle bundle = new Bundle();
        bundle.putString(SORT_TYPE_KEY, sortType);
        bundle.putString(SORT_ORDER_KEY, sortOrder);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState != null) {
            String stringSortType = savedInstanceState.getString(SORT_TYPE_KEY);
            String stringsSortOrder = savedInstanceState.getString(SORT_ORDER_KEY);
            sortType = stringSortType;
            sortOrder = stringsSortOrder;
        }
        mViewAnimator.setDisplayedChild(mCurrentPage == 0 ? ANIMATOR_VIEW_LOADING : ANIMATOR_VIEW_CONTENT);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loadGenres();
    }

    public void loadGenres() {
        DataController.getInstance().loadGenresFromDb(mCallbackDbGenres);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void loadMovies() {
        if (isDataCacheEmpty()) {
            loadPage(1);
        } else {
            if (getDataController().getCurrentSortType().equals(sortType) && getDataController().getCurrentSortOrder().equals(sortOrder)) {
                updateAdapter(getMovieList());
            } else {
                loadPage(1);
            }
        }
    }

    @Override
    protected List<MovieItem> getMovieList() {
        return super.getMovieList();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //super.onCreateOptionsMenu(menu);
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.sort_order_popularity_asc) {
            setSortAndUpdateMovieList(SortType.POPULARITY, SortOrder.ASC);
            return true;
        } else if (id == R.id.sort_order_popularity_desc) {
            setSortAndUpdateMovieList(SortType.POPULARITY, SortOrder.DESC);

        } else if (id == R.id.sort_order_release_date_asc) {
            setSortAndUpdateMovieList(SortType.RELEASE_DATE, SortOrder.ASC);
        } else if (id == R.id.sort_order_release_date_desc) {
            setSortAndUpdateMovieList(SortType.RELEASE_DATE, SortOrder.DESC);
        } else if (id == R.id.sort_order_original_title_asc) {
            setSortAndUpdateMovieList(SortType.ORIGINAL_TITLE, SortOrder.ASC);
            return true;
        } else if (id == R.id.sort_order_original_title_desc) {
            setSortAndUpdateMovieList(SortType.ORIGINAL_TITLE, SortOrder.DESC);
            return true;
        } else if (id == R.id.sort_order_average_count_desc) {
            setSortAndUpdateMovieList(SortType.AVERAGE_COUNT, SortOrder.DESC);
            return true;
        } else if (id == R.id.sort_order_average_count_asc) {
            setSortAndUpdateMovieList(SortType.AVERAGE_COUNT, SortOrder.ASC);
            return true;
        } else if (id == R.id.sort_order_vote_count) {
            setSortAndUpdateMovieList(SortType.VOTE_COUNT, SortOrder.DESC);
            return true;
        } else if (id == R.id.clear_caches) {
            clearAndReload();
            return true;
        }
        return true;
    }

    protected void clearAndReload() {
        clearCaches();
        reloadItems();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (sortType != null && sortOrder != null) {
            outState.putString(SORT_TYPE_KEY, sortType);
            outState.putString(SORT_ORDER_KEY, sortOrder);
        }
        super.onSaveInstanceState(outState);
    }

    public void setSortAndUpdateMovieList(SortType sortType, SortOrder sortOrder) {
        this.sortType = sortType.getSortType();
        this.sortOrder = sortOrder.getSortOrder();
        this.clearCaches();
        this.updateMovieListBySortOrder(this.sortType, this.sortOrder, 1);
    }


    @Override
    public void onRefresh() {
        if (sortType != null && sortOrder != null) {
            clearAndReload();
        }
    }

    @Override
    protected void initRecyclerView() {
        super.initRecyclerView();
        reInitializeScrollView(mCurrentPage);
    }

    @Override
    protected void loadPage(int page) {
        updateMovieListBySortOrder(sortType, sortOrder, page);
    }

    protected void updateMovieListBySortOrder(String sortType, String sortOrder, int page) {
        if (NetworkUtils.hasConnectivity()) {
            getDataController().loadMoviesFromServer(mMovieCallback, sortType, sortOrder, page);
        } else {
            mRecyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
            stopRefreshing();
        }
    }

    @Override
    protected void updateUIInternal(List<MovieItem> movieItems, int event) {
        getDataController().setCurrentSortType(sortType);
        getDataController().setCurrentSortOrder(sortOrder);
        super.updateUIInternal(movieItems, event);
    }

    @Override
    public void updateFailed(String message) {
        showMessageInternal(message);
    }

    @Override
    public void updateSuccess(String message) {
        showMessageInternal(message);
    }

    private void showMessageInternal(String message) {
        if (getActivity() != null) {
            Toast.makeText(this.getActivity(), message, Toast.LENGTH_SHORT).show();
        }
    }

    SyncCallback<List<GenreItem>> mCallbackDbGenres = new SyncCallback<List<GenreItem>>() {
        @Override
        public void onUpdateUI(String message, List<GenreItem> genreItems, int event) {
            if (genreItems != null && !genreItems.isEmpty()) {
                getDataController().saveGenresToCache(genreItems);
                loadMovies();
            } else {
                DataController.getInstance().loadGenresFromServer(mCallbackServerGenres);
            }
        }
    };

    SyncCallback<List<GenreItem>> mCallbackServerGenres = new SyncCallback<List<GenreItem>>() {
        @Override
        public void onUpdateUI(String message, List<GenreItem> genreItems, int event) {
            if (genreItems != null) {
                getDataController().saveGenresToCache(genreItems);
                getDataController().saveGenresToDb(genreItems);
                loadMovies();
            } else {
                Toast.makeText(getActivity(), "Genres download failed. App will stop now!!.", Toast.LENGTH_SHORT).show();
            }
        }
    };


    SyncCallback<List<MovieItem>> mMovieCallback = new SyncCallback<List<MovieItem>>() {
        @Override
        public void onUpdateUI(String message, List<MovieItem> movieItems, int event) {
            if (movieItems != null && !movieItems.isEmpty()) {
                updateUIInternal(movieItems, event);
            }
            stopRefreshing();
            if (message != null) {
                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
            }
        }
    };
}
