package com.org.android.popularmovies.synchronization.impl.tasks;

import com.org.android.popularmovies.model.MovieItem;
import com.org.android.popularmovies.parser.MoviesDTO;
import com.org.android.popularmovies.synchronization.event.SyncEvent;
import com.org.android.popularmovies.synchronization.interfaces.SyncCallback;
import com.org.android.popularmovies.pojo.POJOContentTranslator;

import java.util.List;

/**
 * Update movies task.
 */
public abstract class AbstractUpdateMoviesTask extends AbstractHttpSyncTask<List<MovieItem>> {

    public AbstractUpdateMoviesTask(int syncAction, SyncCallback<List<MovieItem>> callback) {
        super(syncAction, callback);
    }

    @Override
    public void synchronize() throws Exception {

        List<MovieItem> movieItems = null;
        MoviesDTO moviesDTOs = getMoviesDTO();
        if (moviesDTOs != null) {
            movieItems = getMovieItemsFromDTos(moviesDTOs);
        }
        if (movieItems != null) {
            onTaskFinished(movieItems, SyncEvent.SUCCESS);
        } else {
            onTaskFinished("Empty data", null, SyncEvent.SUCCESS);
        }
    }

    private List<MovieItem> getMovieItemsFromDTos(MoviesDTO moviesDTOs) {
        return POJOContentTranslator.convertMoviesDToToModels(moviesDTOs);
    }

    protected MoviesDTO getMoviesDTO() throws Exception{
        return getHttpClient().getMoviesDTO();
    }

    @SuppressWarnings("unchecked")
    protected void onUpdate(String defaultMessage, List<MovieItem> list, int event) {
        if (callback != null) {
            callback.onUpdateUI(defaultMessage, list, event);
        }
    }
}
