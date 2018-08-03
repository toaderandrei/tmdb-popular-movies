package com.org.android.popularmovies.activities;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.org.android.popularmovies.R;
import com.org.android.popularmovies.activities.adapter.ModeSpinnerAdapter;
import com.org.android.popularmovies.controller.DataController;
import com.org.android.popularmovies.fragments.FavoredMoviesFragment;
import com.org.android.popularmovies.fragments.MoviesDetailsFragment;
import com.org.android.popularmovies.fragments.MoviesFragment;
import com.org.android.popularmovies.fragments.SortedMoviesFragment;
import com.org.android.popularmovies.model.GenreItem;
import com.org.android.popularmovies.model.MovieItem;
import com.org.android.popularmovies.model.SortOrder;
import com.org.android.popularmovies.model.SortType;
import com.org.android.popularmovies.synchronization.interfaces.SyncCallback;

import java.util.List;

public class MainActivity extends BaseActivity {

    private static final String TAG = MainActivity.class.getCanonicalName();
    private MoviesFragment moviesFragment;
    private MoviesDetailsFragment movieFragmentDetails;
    private boolean mTwoPane;
    private static final String THUMBNAILS_FOLDER = "thumbs";
    public static final String MODE_FAVORITES = "favorites";
    public static final String MODE_POPULARITY = "popularity";
    private static final String STATE_MODE = "STATE_MODE";
    private String mMode;

    private ModeSpinnerAdapter mSpinnerAdapter = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTwoPane = findViewById(R.id.movie_details_container) != null;
        mSpinnerAdapter = new ModeSpinnerAdapter(this);
        mMode = savedInstanceState != null ? savedInstanceState.getString(STATE_MODE, SortType.POPULARITY.getSortType()) : MODE_POPULARITY;
        //loadGenresFromServer();
        initModSpinner();
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(STATE_MODE, mMode);
    }

    private void initModSpinner() {
        Toolbar toolbar = getToolbar();
        if (toolbar == null) {
            return;
        }
        mSpinnerAdapter.clear();
        mSpinnerAdapter.addItem(MODE_FAVORITES, getString(R.string.mode_favored), false);
        mSpinnerAdapter.addItem(MODE_POPULARITY, "Popularity", false);
        int itemToSelect = -1;

        if (mMode.equals(MODE_FAVORITES)) {
            itemToSelect = 0;
        } else if (mMode.equals(MODE_POPULARITY)) {
            itemToSelect = 2;
        }
        Spinner spinner = initSpinnerLayoutAndRetrieve(toolbar);
        spinner.setAdapter(mSpinnerAdapter);
        spinner.setOnItemSelectedListener(getSpinnerListener());
        if (itemToSelect >= 0) {
            Log.d(TAG, "Restoring item selection to mode spinner: " + itemToSelect);
            spinner.setSelection(itemToSelect);
        }
    }

    @NonNull
    private AdapterView.OnItemSelectedListener getSpinnerListener() {
        return new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> spinner, View view, int position, long itemId) {
                onModeSelected(mSpinnerAdapter.getMode(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        };
    }

    private Spinner initSpinnerLayoutAndRetrieve(Toolbar toolbar) {
        View spinnerContainer = LayoutInflater.from(this).inflate(R.layout.widget_toolbar_spinner, toolbar, false);
        ActionBar.LayoutParams lp = new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        toolbar.addView(spinnerContainer, lp);

        return (Spinner) spinnerContainer.findViewById(R.id.mode_spinner);
    }

    private void onModeSelected(String mode) {
        if (mode.equals(mMode)) return;
        mMode = mode;

        if (mMode.equals(MODE_FAVORITES)) {
            Fragment fragment = getFragment();
            if (fragment == null || !(fragment instanceof FavoredMoviesFragment)) {
                replaceMoviesFragment(new FavoredMoviesFragment());
            }
        } else {
            Fragment fragment = getFragment();
            if (fragment == null || !(fragment instanceof SortedMoviesFragment)) {
                replaceMoviesFragment(SortedMoviesFragment.newInstance(SortType.POPULARITY.getSortType(), SortOrder.ASC.getSortOrder()));
            }
        }
    }

    private void replaceMoviesFragment(MoviesFragment fragment) {
        moviesFragment = fragment;
        getFragmentManager().beginTransaction()
                .replace(R.id.container, fragment, TAG)
                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                .commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initFragment();
        loader.setExitTaskEarly(false);
    }

    protected void initFragment() {
        Fragment fragment = getFragment();
        if (fragment == null) {
            fragment = SortedMoviesFragment.newInstance(SortType.POPULARITY.getSortType(), SortOrder.DESC.getSortOrder());
            replaceMoviesFragment((MoviesFragment) fragment);
        }
    }

    private Fragment getFragment() {
        return getFragmentManager().findFragmentByTag(TAG);
    }

    @Override
    public boolean onSupportNavigateUp() {
        getSupportFragmentManager().popBackStack();
        return true;
    }

    @Override
    protected void initImageFolder() {
        this.imageFolder = THUMBNAILS_FOLDER;
    }

    @Override
    public void onMovieClickedCallback(MovieItem item) {
        if (mTwoPane) {
            FragmentTransaction ft = null;
            movieFragmentDetails = MoviesDetailsFragment.newInstance(item);
            ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.movie_details_container, movieFragmentDetails);
            ft.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
            ft.commit();
        } else {
            Intent intent = new Intent(this, MovieDetailsActivity.class);
            intent.putExtra(MovieDetailsActivity.EXTRA_MOVIE, item);
            startActivity(intent);
        }
    }


    @Override
    public void clearCaches() {
        if (loader != null) {
            loader.clearCache();
        }
    }

    @Override
    protected void onDestroy() {
        if (loader != null) {
            Log.d(TAG, "onDestroy.closing the cache");
            loader.closeCache();
        }
        DataController.getInstance().clearCaches();
        super.onDestroy();
    }

}
