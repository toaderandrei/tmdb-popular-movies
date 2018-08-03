package com.org.android.popularmovies.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.NonNull;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.org.android.popularmovies.R;
import com.org.android.popularmovies.activities.ImageLoaderCallback;
import com.org.android.popularmovies.activities.MovieItemClickedCallback;
import com.org.android.popularmovies.adapters.model.RowView;
import com.org.android.popularmovies.cache.ImageWorker;
import com.org.android.popularmovies.model.MovieItem;
import com.org.android.popularmovies.synchronization.interfaces.SyncCallback;
import com.org.android.popularmovies.utils.StringUtils;
import com.org.android.popularmovies.widgets.ResizedImageView;

import java.util.Comparator;
import java.util.List;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Adapter used for displaying the songs into a list.
 */
public class MoviesAdapter extends GenericAdapter<MovieItem, MoviesAdapter.MovieHolder> {

    private static final String TAG = MoviesAdapter.class.getCanonicalName();
    private MovieItemClickedCallback mListener;
    private ImageLoaderCallback callback;
    private Comparator<? super RowView<MovieItem>> comparator = null;
    private boolean showProgressForLoadMore = true;

    public MoviesAdapter(Context context, MovieItemClickedCallback listener, ImageLoaderCallback loaderCallback, List<MovieItem> items, boolean showProgressForLoadMore) {
        super(context, items);
        this.mListener = listener;
        setHasStableIds(true);
        this.callback = loaderCallback;
        this.showProgressForLoadMore = showProgressForLoadMore;
    }

    public MoviesAdapter(Context context, MovieItemClickedCallback listener, ImageLoaderCallback loaderCallback, List<MovieItem> items) {
        super(context, items);
        this.mListener = listener;
        setHasStableIds(true);
        this.callback = loaderCallback;
    }


    @Override
    protected LayoutInflater getInflater() {
        return LayoutInflater.from(getContext());
    }

    @Override
    protected RecyclerView.ViewHolder onCreateItemHolder(ViewGroup parent, int viewType) {
        return new MovieHolder(getView(parent));
    }

    protected View getView(ViewGroup parent) {
        return getInflater().inflate(R.layout.item_movie, parent, false);
    }

    @NonNull
    private View.OnClickListener getOnClickListener(final MovieItem item, final int position) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onMovieItemClicked(item, position);
                }
            }
        };
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == VIEW_TYPE_ITEM) {
            MovieItem item = getRowViews().get(position).getObject();
            // if (isItemValid(item) && !isImageLoadingInProgress(item)) {
            bindItem((MovieHolder) holder, item);
        }
    }

    protected void bindItem(MovieHolder holder, MovieItem item) {
        holder.bind(item);
    }

    private boolean isItemValid(MovieItem item) {
        return item != null;
    }

    @Override
    public long getItemId(int position) {
        return (!isLoadMore(position)) ? getRowViews().get(position).getObject().getId() : -1L;
    }

    @Override
    public int getItemViewType(int position) {
        return isLoadMore(position) ? VIEW_TYPE_LOAD_MORE : VIEW_TYPE_ITEM;
    }

    @Override
    public int getItemCount() {
        return this.getAllItems().size();
    }

    /**
     * the total number of elements is getCount() - 1 but
     * we account for the last position where the itme
     * is view more.
     *
     * @param position the position where we decide to load
     *                 more.
     * @return true or false.
     */
    public boolean isLoadMore(int position) {
        return position == getItemCount();
    }

    @Override
    protected RowView<MovieItem> createRowView(MovieItem object) {
        return new RowView<MovieItem>(object);
    }

    public class MovieHolder extends RecyclerView.ViewHolder implements ImageWorker.Callback {

        @BindView(R.id.movie_item_container)
        View mContentContainer;
        @BindView(R.id.movie_item_image)
        ResizedImageView mImageView;
        @BindView(R.id.movie_item_title)
        TextView mTitleView;
        @BindView(R.id.movie_item_genres)
        TextView mGenresView;
        @BindView(R.id.movie_item_btn_favorite)
        ImageButton mFavoriteButton;

        @BindColor(R.color.theme_color)
        int mColorBackground;
        @BindColor(R.color.white)
        int mColorTitle;
        @BindColor(R.color.material_white)
        int mColorSubtitle;
        private long mMovieId;
        private MovieItem item;
        private int retry = 5;

        private final StringBuilder mBuilder = new StringBuilder(30);

        public MovieHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(@NonNull final MovieItem item) {
            this.item = item;
            mContentContainer.setOnClickListener(getOnClickListener(item, getAdapterPosition()));
            mFavoriteButton.setSelected(item.isFavored());
            mFavoriteButton.setOnClickListener(getOnFavoredClickedListener(item));
            mTitleView.setText(item.getTitle());
            mGenresView.setText(StringUtils.joinGenres(item.getGenres(), ",", mBuilder));
            if (mMovieId != item.getId()) {
                resetColors();
                mMovieId = item.getId();
            }
            String image_url = item.getPoster_path();
            if (!TextUtils.isEmpty(image_url)) {
                setAnimatioOnImageView(createAndStartAnimation());
                loadImageAsync(image_url);
            } else {
                callback.loadImageAsync(null, mImageView);
            }
            loadMovieFavoriteStatusIfExistsInDb();
        }

        private void loadMovieFavoriteStatusIfExistsInDb() {
            mListener.loadMovieFavoriteStatusIfExistsInDb(item, mCallback);
        }

        protected SyncCallback<MovieItem> mCallback = new SyncCallback<MovieItem>() {
            @Override
            public void onUpdateUI(String message, MovieItem object, int event) {
                updateFavoriteButton(object);
            }
        };

        protected void updateFavoriteButton(MovieItem object) {
            mFavoriteButton.setSelected(object.isFavored());
        }

        private void setAnimatioOnImageView(Animation andStartAnimation) {
            mImageView.setAnimation(andStartAnimation);
        }

        private void loadImageAsync(String image_url) {
            callback.loadImageAsync(image_url, mImageView, this);
        }


        protected Animation createAndStartAnimation() {
            RotateAnimation anim = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            anim.setRepeatMode(Animation.RESTART);
            anim.setInterpolator(new LinearInterpolator());
            anim.setRepeatCount(Animation.INFINITE);
            anim.setDuration(800);
            return anim;
        }


        private void resetColors() {
            mTitleView.setTextColor(mColorTitle);
            mGenresView.setTextColor(mColorSubtitle);
            mFavoriteButton.setColorFilter(mColorTitle, PorterDuff.Mode.MULTIPLY);
        }

        @Override
        public void onSuccess() {
            if (mImageView != null && mImageView.getDrawable() instanceof BitmapDrawable) {
                setAnimatioOnImageView(null);
                BitmapDrawable bmpDrawable = (BitmapDrawable) mImageView.getDrawable();
                Palette.Builder builder = Palette.from(bmpDrawable.getBitmap());
                Palette.Swatch swatch = builder.generate().getVibrantSwatch();
                if (swatch != null) {
                    mTitleView.setTextColor(swatch.getBodyTextColor());
                    mGenresView.setTextColor(swatch.getTitleTextColor());
                    mFavoriteButton.setColorFilter(swatch.getBodyTextColor(), PorterDuff.Mode.MULTIPLY);
                }
            }
        }

        @Override
        public void onFail() {
            if (getContext() instanceof Activity) {
                Log.d(TAG, "fail to load the image:" + item.getTitle());
                Toast.makeText(getContext(), "Fail to load the " + item.getTitle() + "!", Toast.LENGTH_SHORT).show();
            }
            resetAnimationOnImageView();
            if (retry >= 0) {
                //TODO implement a better approach.
                Log.d(TAG, "retry the loading: " + retry + " for " + item.getTitle());
                retry--;
                if (item != null && item.getPoster_path() != null) {
                    Log.d(TAG, "load the image " + item.getPoster_path() + " for movie " + item.getTitle());
                    loadImageAsync(item.getPoster_path());
                }
            }
        }

        private void resetAnimationOnImageView() {
            setAnimatioOnImageView(null);
            setAnimatioOnImageView(createAndStartAnimation());
        }

        private View.OnClickListener getOnFavoredClickedListener(final MovieItem item) {
            return new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean oldFavored = item.isFavored();
                    item.setFavored(!oldFavored);
                    mListener.onFavoredMovieItemClicked(item, mCallback);
                    v.setSelected(!oldFavored);
                }
            };
        }
    }

    @NonNull
    @Override
    protected RecyclerView.ViewHolder getViewHolder(ViewGroup parent) {
        return super.getViewHolder(parent);
    }
}
