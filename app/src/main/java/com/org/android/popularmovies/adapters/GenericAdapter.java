package com.org.android.popularmovies.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.org.android.popularmovies.R;
import com.org.android.popularmovies.activities.MainActivity;
import com.org.android.popularmovies.adapters.model.RowView;
import com.org.android.popularmovies.application.PopularMoviesApp;
import com.org.android.popularmovies.model.MovieItem;
import com.org.android.popularmovies.utils.ComparisonUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;


/**
 * Generic adapter used for storing generic objects.
 */
public abstract class GenericAdapter<T, U extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = GenericAdapter.class.getSimpleName();
    private Context context;
    private List<RowView<T>> rowViews = null;

    protected static final int VIEW_TYPE_LOAD_MORE = 1;
    protected static final int VIEW_TYPE_ITEM = 2;


    public GenericAdapter(Context context, List<T> objects) {
        this.context = context;
        rowViews = new ArrayList<RowView<T>>();
        addAll(objects);
    }

    public void addAll(final Collection<? extends T> objects) {

        runAsync(new Runnable() {
            @Override
            public void run() {
                synchronized (rowViews) {
                    addAllInternalAsync(objects);
                }
            }
        });
    }

    protected void removeInternal(T object) {
        boolean isChanged = false;
        synchronized (rowViews) {
            int position = -1;
            if (object != null && getRowViews() != null
                    && !getRowViews().isEmpty()) {
                Iterator<RowView<T>> it = getRowViews().iterator();
                while (it.hasNext()) {
                    position++;
                    RowView<T> obj = it.next();
                    if (ComparisonUtils.areObjectsEqual(obj.getObject(),
                            object)) {
                        it.remove();
                        isChanged = true;
                    }
                }
            }
            if (isChanged) {
                notifyItemRemoved(position);
                notifyDataSetChanged();
            }
        }
    }

    protected void addAllInternalAsync(Collection<? extends T> objects) {
        if (objects == null || objects.isEmpty()) {
            return;
        }
        List<RowView<T>> temp = new ArrayList<RowView<T>>();
        for (T object : objects) {
            temp.add(createRowView(object));
        }
        addAllInternalOnUiThread(temp);
    }

    protected void addAllInternalOnUiThread(final List<? extends RowView<T>> results) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                synchronized (rowViews) {
                    boolean changed = false;
                    Log.d(TAG, "internalAdd.updateFailed listview");
                    int amountInserted = 0;
                    if (results != null && rowViews != null) {
                        List<T> items = getAllItems();
                        int position = -1;
                        for (RowView<T> result : results) {
                            if (result.getObject() instanceof MovieItem) {
                                Log.d("TAG", "result id:" + ((MovieItem) result.getObject()).getId());
                                Log.d("TAG", "poster path:" + ((MovieItem) result.getObject()).getPoster_path());
                            }

                            if (items != null) {
                                for (int k = 0; k < items.size(); k++) {
                                    if (items.get(k) != null && items.get(k).equals(result.getObject())) {
                                        position = k;
                                    }
                                }
                                if (position < 0) {
                                    rowViews.add(result);
                                    changed = true;
                                    amountInserted++;
                                } else {
                                    rowViews.add(position, result);
                                    changed = true;
                                }
                            }

                        }
                        if (changed) {
                            Log.d(TAG, "internalAdd. ");
                            int currentSize = rowViews.size();
                            notifyItemChanged(currentSize, amountInserted);
                            notifyDataSetChanged();
                        }
                    }
                }
            }
        });
    }

    public void clearAll() {
        synchronized (rowViews) {
            boolean isChanged = false;
            if (getRowViews() != null && !getRowViews().isEmpty()) {
                getRowViews().clear();
                isChanged = true;
            }
            if (isChanged) {
                Log.d(TAG, "clearAllInternal.updateFailed listview");
                notifyDataSetChanged();
            }
        }
    }


    protected void runOnUiThread(Runnable runnable) {
        if (runnable == null || context == null) {
            Log.d(TAG, "runnable is null");
            return;
        }
        if (context instanceof MainActivity) {
            Log.d(TAG, "run on ui thread");
            ((MainActivity) context).runOnUiThread(runnable);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    protected void runAsync(Runnable runnable) {
        PopularMoviesApp.getInstance().runAsync(runnable);
    }


    public List<T> getAllItems() {
        List<T> items = new ArrayList<>();
        synchronized (rowViews) {
            for (RowView<T> rowView : rowViews) {
                items.add(rowView.getObject());
            }
        }
        return items;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return viewType == VIEW_TYPE_LOAD_MORE ? getViewHolder(parent) : onCreateItemHolder(parent, viewType);
    }

    @NonNull
    protected RecyclerView.ViewHolder getViewHolder(final ViewGroup parent) {
        return new RecyclerView.ViewHolder(getInflater().inflate(R.layout.item_load_more, parent, false)) {
        };
    }

    protected abstract RecyclerView.ViewHolder onCreateItemHolder(ViewGroup parent, int viewType);

    protected abstract RowView<T> createRowView(T object);

    protected abstract LayoutInflater getInflater();

    protected List<RowView<T>> getRowViews() {
        return rowViews;
    }

    protected Context getContext() {
        return this.context;
    }

}
