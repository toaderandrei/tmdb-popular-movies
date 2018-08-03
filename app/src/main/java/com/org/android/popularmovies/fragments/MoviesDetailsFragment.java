package com.org.android.popularmovies.fragments;


import android.app.Fragment;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.org.android.popularmovies.R;
import com.org.android.popularmovies.activities.ImageLoaderCallback;
import com.org.android.popularmovies.cache.ImageWorker;
import com.org.android.popularmovies.controller.DataController;
import com.org.android.popularmovies.model.MovieItem;
import com.org.android.popularmovies.model.MovieReview;
import com.org.android.popularmovies.model.MovieVideo;
import com.org.android.popularmovies.synchronization.interfaces.SyncCallback;
import com.org.android.popularmovies.utils.ApiVersion;
import com.org.android.popularmovies.utils.Lists;
import com.org.android.popularmovies.utils.StringUtils;
import com.org.android.popularmovies.utils.VideoUtils;
import com.org.android.popularmovies.widgets.ResizedImageView;
import com.org.android.popularmovies.widgets.scrollable.callbacks.ObservableScrollViewCallbacks;
import com.org.android.popularmovies.widgets.scrollable.state.ScrollState;
import com.org.android.popularmovies.widgets.scrollable.view.ObservableScrollView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static butterknife.ButterKnife.findById;

/**
 *  Fragment used for showing the details of the Movies.
 */
public class MoviesDetailsFragment extends Fragment implements ObservableScrollViewCallbacks, ImageWorker.Callback {

    @BindView(R.id.movie_scroll_view)
    protected ObservableScrollView mScrollView;

    @BindView(R.id.movie_poster_img)
    protected ResizedImageView imageView;
    protected MovieItem item;

    @BindView(R.id.tv_vote)
    protected TextView tvVote;

    @BindView(R.id.tv_release_date)
    protected TextView tvReleaseDate;

    @BindView(R.id.tv_overview)
    protected TextView tvOverview;

    @BindView(R.id.tv_title)
    protected TextView tvTitle;

    @BindView(R.id.movie_favorite_button)
    protected ImageButton favoriteButton;

    @BindView(R.id.movie_reviews_container)
    ViewGroup mReviewsGroup;
    @BindView(R.id.movie_videos_container)
    ViewGroup mVideosGroup;


    private List<MovieReview> mReviews;
    private List<MovieVideo> mVideos;

    public static final String MOVIE_ITEM = "movie_item";
    private static final String STATE_SCROLL_VIEW = "state_scroll_view";
    private static final String STATE_REVIEWS = "state_reviews";
    private static final String STATE_VIDEOS = "state_trailers";
    private static final String TAG = MoviesDetailsFragment.class.getCanonicalName();
    private LayoutInflater inflater = null;
    private ImageLoaderCallback callback;

    /**
     * Populate image using a url from extras, use the convenience factory method
     * {@link MoviesDetailsFragment#newInstance(MovieItem)} to create this fragment.
     */
    public static MoviesDetailsFragment newInstance(MovieItem movieItem) {
        MoviesDetailsFragment fragment = new MoviesDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(MOVIE_ITEM, movieItem);
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
        readFromArguments();
    }

    private void readFromArguments() {
        item = getArguments() != null ? (MovieItem) getArguments().getParcelable(MOVIE_ITEM) : null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate and locate the main ImageView
        return inflater.inflate(R.layout.fragnent_movie_details, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        mScrollView.setScrollViewCallbacks(this);
        if (savedInstanceState != null) {
            mVideos = savedInstanceState.getParcelableArrayList(STATE_VIDEOS);
            mReviews = savedInstanceState.getParcelableArrayList(STATE_REVIEWS);
            mScrollView.onRestoreInstanceState(savedInstanceState.getParcelable(STATE_SCROLL_VIEW));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(STATE_SCROLL_VIEW, mScrollView.onSaveInstanceState());
        if (mReviews != null)
            outState.putParcelableArrayList(STATE_REVIEWS, new ArrayList<>(mReviews));
        if (mVideos != null)
            outState.putParcelableArrayList(STATE_VIDEOS, new ArrayList<>(mVideos));
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        callback = ((ImageLoaderCallback) getActivity());
        inflater = LayoutInflater.from(this.getActivity());
        loadViews();
    }

    private void loadViews() {
        if (item != null && item.getPoster_path() != null && !item.getPoster_path().isEmpty()) {
            if (callback != null) {
                callback.loadImageAsync(item.getPoster_path(), imageView, this);
            }
            onMovieLoaded();
            if (mReviews != null) {
                onReviewsLoaded(mReviews);
            } else {
                loadReviews();
            }
            if (mVideos != null) {
                onVideosLoaded(mVideos);
            } else {
                loadVideos();
            }
        } else {
            if (ApiVersion.hasLollipop()) {
                setDrawable(getResources().getDrawable(R.drawable.no_image_found, getActivity().getTheme()));
            } else {
                setDrawable(getResources().getDrawable(R.drawable.no_image_found));
            }
        }
        this.favoriteButton.setOnClickListener(getOnFavoredClickedListener());
        onFavoriteMovieStatusUpdate();

    }

    private void onFavoriteMovieStatusUpdate() {
        DataController.getInstance().loadMovieStatusFromDbIfExists(item, mCallbackMovieFavorite);

    }

    private void loadVideos() {
        if (item != null) {
            DataController.getInstance().loadMovieVideos(item.getId(), mCallbackMovieVideos);
        }
    }

    private void onVideosLoaded(List<MovieVideo> videos) {
        this.mVideos = videos;

        // Remove all existing videos (everything but first two children)
        for (int i = mVideosGroup.getChildCount() - 1; i >= 2; i--) {
            mVideosGroup.removeViewAt(i);
        }

        boolean hasVideos = false;
        if (!Lists.isEmpty(mVideos)) {
            for (MovieVideo video : mVideos) {

                final View videoView = inflater.inflate(R.layout.item_video, mVideosGroup, false);
                final TextView videoName = (TextView) videoView.findViewById(R.id.video_name);

                videoName.setText(video.getSite() + ":" + video.getName());
                videoView.setTag(video);
                videoView.setOnClickListener(getOnClickListener(video));
                mVideosGroup.addView(videoView);
                hasVideos = true;
            }
        }
        mVideosGroup.setVisibility(hasVideos ? View.VISIBLE : View.GONE);
    }

    private View.OnClickListener getOnClickListener(final MovieVideo video) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VideoUtils.playVideo(getActivity(), (MovieVideo) v.getTag());
            }
        };
    }

    private void loadReviews() {
        if (item != null) {
            DataController.getInstance().loadReviews(item.getId(), mCallbackMovieReviews);
        }
    }

    private void onReviewsLoaded(List<MovieReview> mReviews) {
        this.mReviews = mReviews;

        for (int i = mReviewsGroup.getChildCount() - 1; i >= 2; i--) {
            mReviewsGroup.removeViewAt(i);
        }

        boolean hasReviews = false;
        if (!Lists.isEmpty(mReviews)) {
            for (MovieReview review : mReviews) {
                if (TextUtils.isEmpty(review.getAuthor())) {
                    continue;
                }
                final View reviewView = inflater.inflate(R.layout.item_review_detail, mReviewsGroup, false);
                final TextView authorView = (TextView) reviewView.findViewById(R.id.review_author);
                final TextView reviewContent = (TextView) reviewView.findViewById(R.id.review_content);
                authorView.setText(review.getAuthor());
                reviewContent.setText(review.getContent());
                mReviewsGroup.addView(reviewView);
                hasReviews = true;
            }
        }
        mReviewsGroup.setVisibility(hasReviews ? View.VISIBLE : View.GONE);
    }


    private void onMovieLoaded() {
        if (item == null) {
            Log.d(TAG, "onMovieLoaded:");
            return;
        }
        this.tvTitle.setText(getStringValue(null, item.getTitle()));
        String keyReleaseDate = getStringKey(tvReleaseDate);
        Spanned keyReleaseDateSpanned = StringUtils.getSpannedText(keyReleaseDate, item.getRelease_date(), false);
        this.tvReleaseDate.setText(keyReleaseDateSpanned);
        String keyOverview = getStringKey(tvOverview);
        Spanned spannedTextOverview = StringUtils.getSpannedText(keyOverview, item.getOverview(), true);
        this.tvOverview.setText(spannedTextOverview);
        String keyVote = this.getStringKey(tvVote);
        Spanned keyVoteSpanned = StringUtils.getSpannedText(keyVote, item.getVote_average(), false);
        this.tvVote.setText(keyVoteSpanned);
    }

    private View.OnClickListener getOnFavoredClickedListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean oldFavored = item.isFavored();
                v.setSelected(!oldFavored);
                item.setFavored(!oldFavored);
                updateMovieToTheDb(item);
            }
        };
    }

    public void updateMovieToTheDb(MovieItem item) {
        DataController.getInstance().updateMovieToTheDb(item, mCallbackMovieFavorite);
    }

    private void setDrawable(Drawable drawable) {
        imageView.setImageDrawable(drawable);
    }


    private String getStringKey(TextView tv) {
        return tv.getText() != null ? tv.getText().toString() : "";
    }

    @NonNull
    private String getStringValue(String key, String value) {
        return (key != null ? key : "") + value;
    }

    @Override
    public void onDestroy() {
        if (imageView != null) {
            callback.cancelLoadingIfRunning(imageView);
            imageView.setImageBitmap(BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.image_loading));
        }
        super.onDestroy();
    }


    //==========callbacks==========================================================================

    SyncCallback<List<MovieReview>> mCallbackMovieReviews = new SyncCallback<List<MovieReview>>() {
        @Override
        public void onUpdateUI(String message, List<MovieReview> data, int event) {
            if (data != null) {
                onReviewsLoaded(data);
            }
        }
    };

    SyncCallback<List<MovieVideo>> mCallbackMovieVideos = new SyncCallback<List<MovieVideo>>() {
        @Override
        public void onUpdateUI(String message, List<MovieVideo> data, int event) {
            if (data != null) {
                onVideosLoaded(data);
            }
        }
    };

    private SyncCallback<MovieItem> mCallbackMovieFavorite = new SyncCallback<MovieItem>() {
        @Override
        public void onUpdateUI(String message, MovieItem object, int event) {
            favoriteButton.setSelected(object.isFavored());
        }
    };

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
        Log.d(TAG, "onscroll: " + scrollY);
    }

    @Override
    public void onDownMotionEvent() {

    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {

    }

    @Override
    public void onSuccess() {
        Toast.makeText(this.getActivity(), "Image loaded...", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFail() {
        Toast.makeText(this.getActivity(), "Image loading failed...", Toast.LENGTH_SHORT).show();
    }
}
