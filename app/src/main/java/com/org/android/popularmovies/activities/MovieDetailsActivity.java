package com.org.android.popularmovies.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.org.android.popularmovies.R;
import com.org.android.popularmovies.fragments.MoviesDetailsFragment;
import com.org.android.popularmovies.fragments.MoviesFragment;
import com.org.android.popularmovies.model.MovieItem;

/**
 * Activity that display the details of a Movie.
 */
public class MovieDetailsActivity extends BaseActivity {

    public static final String EXTRA_MOVIE = "com.org.android.popularmovies.extras.EXTRA_MOVIE";

    private static final String MOVIE_FRAGMENT_TAG = "fragment_movie";
    protected static final String FRAGMENT_TAG = "fragment_tag";

    private static final String DETAILS_IMAGE_FOLDER = "DETAILS_IMAGE_FOLDER";

    private MoviesDetailsFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        if (mToolbar != null) {
            ViewCompat.setElevation(mToolbar, getResources().getDimension(R.dimen.toolbar_elevation));
            mToolbar.setNavigationOnClickListener(getOnClickListener());

            ActionBar ab = getSupportActionBar();
            if (ab != null) {
                ab.setDisplayOptions(0, ActionBar.DISPLAY_SHOW_TITLE);
                ab.setDisplayHomeAsUpEnabled(true);
                ab.setDisplayShowHomeEnabled(true);
            }
        }

        MovieItem movie = getIntent().getParcelableExtra(EXTRA_MOVIE);

        if (savedInstanceState == null) {
            fragment = MoviesDetailsFragment.newInstance(movie);
            getFragmentManager().beginTransaction()
                    .replace(R.id.movie_details_container, fragment, MOVIE_FRAGMENT_TAG)
                    .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                    .commit();
        }
    }

    @Override
    protected void initImageFolder() {
        this.imageFolder = DETAILS_IMAGE_FOLDER;
    }

    public View.OnClickListener getOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        };
    }

    @Override
    public void onMovieClickedCallback(MovieItem item) {
        //nothing for now.
    }

    @Override
    public void clearCaches() {
        if (loader != null) {
            loader.clearCache();
        }
    }
}
