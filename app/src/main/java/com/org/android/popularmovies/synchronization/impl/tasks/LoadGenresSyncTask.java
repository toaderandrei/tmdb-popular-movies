package com.org.android.popularmovies.synchronization.impl.tasks;

import com.org.android.popularmovies.model.GenreItem;
import com.org.android.popularmovies.parser.MovieGenresDTO;
import com.org.android.popularmovies.synchronization.event.SyncEvent;
import com.org.android.popularmovies.synchronization.interfaces.SyncCallback;
import com.org.android.popularmovies.pojo.POJOContentTranslator;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * Asynchronous task of loading the genres.
 */
public class LoadGenresSyncTask extends AbstractHttpSyncTask<List<GenreItem>> {


    public LoadGenresSyncTask(int syncEvent, SyncCallback<List<GenreItem>> callback) {
        super(syncEvent, callback);
    }

    @Override
    protected void onUpdate(String defaultMsg, List<GenreItem> obj, int event) {
        if (callback != null) {
            callback.onUpdateUI(defaultMsg, obj, event);
        }
    }

    @Override
    public void synchronize() throws Exception {
        List<GenreItem> genreItems = getGenreItemsFromDtos(getGenreDto());
        //save to the db
        if (genreItems != null) {
            onTaskFinished(genreItems, SyncEvent.SUCCESS);
        } else {
            onTaskFinished("Empty data", null, SyncEvent.SUCCESS);
        }
        // getDataController().saveGenresToCache(genreItems);
        // getDataController().saveGenresToCache(genreItems);

    }

    private List<GenreItem> getGenreItemsFromDtos(MovieGenresDTO genreItemsDto) {
        if (genreItemsDto == null || genreItemsDto.getGenres() == null || genreItemsDto.getGenres().isEmpty()) {
            return Collections.EMPTY_LIST;
        }
        return POJOContentTranslator.convertGenresDtoToGenreItems(genreItemsDto);
    }

    public MovieGenresDTO getGenreDto() throws IOException {
        return getHttpClient().getGenresDtos();
    }
}
