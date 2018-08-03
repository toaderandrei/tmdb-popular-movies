package com.org.android.popularmovies.synchronization.impl.tasks;

import com.org.android.popularmovies.model.MovieItem;
import com.org.android.popularmovies.parser.MoviesDTO;
import com.org.android.popularmovies.synchronization.interfaces.SyncCallback;

import java.util.List;

/**
 * Update movies task.
 */
public class UpdateMoviesTaskImpl extends AbstractUpdateMoviesTask {
    private String sortType;
    private String order;
    private int page;

    public UpdateMoviesTaskImpl(int syncAction, SyncCallback<List<MovieItem>> callback, String sortType, String order, int page) {
        super(syncAction, callback);
        this.sortType = sortType;
        this.order = order;
        this.page = page;
    }

    @Override
    protected MoviesDTO getMoviesDTO() throws Exception {
        return getHttpClient().getMoviesDTO(sortType, order, page);
    }
}
