package com.org.android.popularmovies.synchronization.impl.tasks;

import com.org.android.popularmovies.httpclient.IHttpClient;
import com.org.android.popularmovies.model.MovieVideo;
import com.org.android.popularmovies.parser.MovieVideosDTO;
import com.org.android.popularmovies.synchronization.event.SyncEvent;
import com.org.android.popularmovies.synchronization.interfaces.SyncCallback;
import com.org.android.popularmovies.pojo.POJOContentTranslator;

import java.util.List;

/**
 * Updates the movie trailers
 */
public class UpdateMovieVideosTaskImpl extends AbstractHttpSyncTask<List<MovieVideo>> {


    private String id;
    private boolean before;

    public UpdateMovieVideosTaskImpl(int syncEvent, SyncCallback<List<MovieVideo>> callback, String id, boolean additional) {
        super(syncEvent, callback);
        this.id = id;
        this.before = additional;
    }

    @Override
    public void synchronize() throws Exception {
        List<MovieVideo> movieVideos = null;
        IHttpClient client = getHttpClient();
        MovieVideosDTO movieTrailersDTOs = getMovieVideosDTO(id, client);
        if (movieTrailersDTOs != null) {
            movieVideos = getMovieVideosDTO(movieTrailersDTOs);
        }
        if (movieVideos != null) {
            onTaskFinished(movieVideos, SyncEvent.SUCCESS);
        } else {
            onTaskFinished("Empty data", null, SyncEvent.SUCCESS);
        }
    }


    private List<MovieVideo> getMovieVideosDTO(MovieVideosDTO movieVideosDTO) {
        return POJOContentTranslator.convertMovieVideoDTOsToMovieVideos(movieVideosDTO);
    }

    protected MovieVideosDTO getMovieVideosDTO(String id, IHttpClient client) throws Exception {
        return client.getMovieVideosDTO(id);
    }

    @SuppressWarnings("unchecked")
    protected void onUpdate(String defaultMessage, List<MovieVideo> list, int event) {
        if (callback != null) {
            callback.onUpdateUI(defaultMessage, list, event);
        }
    }
}
