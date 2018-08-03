package com.org.android.popularmovies.synchronization.impl.tasks;

import com.org.android.popularmovies.model.MovieReview;
import com.org.android.popularmovies.parser.MovieReviewsDTO;
import com.org.android.popularmovies.synchronization.event.SyncEvent;
import com.org.android.popularmovies.synchronization.interfaces.SyncCallback;
import com.org.android.popularmovies.pojo.POJOContentTranslator;

import java.io.IOException;
import java.util.List;

/**
 * Task that loads the movie reviews for a movie.
 */
public class UpdateMovieReviewTaskImpl extends AbstractHttpSyncTask<List<MovieReview>> {

    private String id;

    public UpdateMovieReviewTaskImpl(int syncEvent, SyncCallback<List<MovieReview>> callback, String id) {
        super(syncEvent, callback);
        this.id = id;
    }

    @Override
    protected void onUpdate(String defaultMsg, List<MovieReview> list, int event) {
        if (callback != null) {
            callback.onUpdateUI(defaultMsg, list, event);
        }
    }

    @Override
    public void synchronize() throws Exception {

        List<MovieReview> movieReviews = null;
        MovieReviewsDTO movieReviewsDTO = getMovieReviewsDTO(id);
        if (movieReviewsDTO != null) {
            movieReviews = getMovieReviews(movieReviewsDTO);
        }
        if (movieReviews != null) {
            onTaskFinished(movieReviews, SyncEvent.SUCCESS);
        } else {
            onTaskFinished("Empty data", null, SyncEvent.SUCCESS);
        }
    }


    protected MovieReviewsDTO getMovieReviewsDTO(String id) throws IOException {
        return getHttpClient().getMovieReviewsDTO(id);
    }

    protected List<MovieReview> getMovieReviews(MovieReviewsDTO dtos) {
        return POJOContentTranslator.convertMovieReviewsDToToMovieReviews(dtos);
    }
}
