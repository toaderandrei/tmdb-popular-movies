package com.org.android.popularmovies.db.database;

import android.support.annotation.NonNull;

import com.org.android.popularmovies.db.interfaces.IDatabaseRow;
import com.org.android.popularmovies.db.model.GenericColumn;
import com.org.android.popularmovies.db.model.GenreRow;
import com.org.android.popularmovies.db.model.MovieRow;
import com.org.android.popularmovies.db.table.TablesCreate;
import com.org.android.popularmovies.model.GenreItem;
import com.org.android.popularmovies.model.MovieItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class that converts movie items to movie rows and vice-versa.
 */
public class DatabaseAdapterUtils {
    public static String[] getColumnNames(TablesCreate table) {
        List<String> listColumns = new ArrayList<>();
        GenericColumn[] columns = table.getColumns();
        for (GenericColumn column : columns) {
            listColumns.add(column.getFieldName());
        }
        return listColumns.toArray(new String[listColumns.size()]);
    }

    public static List<MovieItem> getMovies(List<IDatabaseRow> movieRows) {
        List<MovieItem> items = new ArrayList<>();
        for (IDatabaseRow row : movieRows) {
            MovieItem item = getMovieItemInternal((MovieRow) row);
            items.add(item);
        }
        return items;
    }

    @NonNull
    public static MovieItem getMovieItemFromMovieRow(MovieRow row) {
        return getMovieItemInternal(row);
    }

    @NonNull
    public synchronized static MovieItem getMovieItemInternal(MovieRow row) {
        return new MovieItem(Long.valueOf(row.getMovieId()), row.getMovieFavorite() == 1, row.getMoviePopularity(), row.getMovieVoteAverage(), row.getMovieTitle(), row.getMovieOriginalTitle(), row.getOverview(), row.getMoviePosterPath(), row.getMovieReleaseDate(), null);
    }


    public static List<IDatabaseRow> getMovieRows(List<MovieItem> movieItems) {
        List<IDatabaseRow> rows = new ArrayList<>();
        for (MovieItem item : movieItems) {
            MovieRow row = getMovieRowInternal(item);
            rows.add(row);
        }
        return rows;
    }

    @NonNull
    public static MovieRow getMovieRowFromMovieItem(MovieItem item) {
        return getMovieRowInternal(item);
    }

    @NonNull
    private synchronized static MovieRow getMovieRowInternal(MovieItem item) {
        return new MovieRow(item.getId(),
                item.isFavored(),
                item.getRelease_date(),
                item.getTitle(),
                item.getOriginal_title(),
                item.getPoster_path(),
                item.getVote_average(),
                item.getPopularity(),
                item.getOverview());
    }

    public static List<IDatabaseRow> getGenreRowsFromGenreItems(List<GenreItem> items) {
        List<IDatabaseRow> genreRows = new ArrayList<>();
        for (GenreItem itemGenreRow : items) {
            GenreRow genreRow = new GenreRow(itemGenreRow.getId(), itemGenreRow.getName(), null);
            genreRows.add(genreRow);
        }
        return genreRows;
    }

    public static List<GenreItem> getGenreItemsFromGenreRows(List<IDatabaseRow> genreRowItems) {
        List<GenreItem> items = new ArrayList<>();
        for (IDatabaseRow row : genreRowItems) {
            GenreRow genreRow = (GenreRow) row;
            GenreItem item = new GenreItem(Integer.valueOf(genreRow.getGenreId()), genreRow.getGenreName());
            items.add(item);
        }
        return items;
    }
}
