package com.org.android.popularmovies.db.adapter;

import android.database.sqlite.SQLiteDatabase;

import com.org.android.popularmovies.db.interfaces.IDatabaseRow;
import com.org.android.popularmovies.db.model.GenreRow;
import com.org.android.popularmovies.model.MovieItem;

import java.util.List;

/**
 * Interface that defines all the requests.
 */
public interface SqliteDatabaseAdapter {


    /**
     * inserts a movie into the database.
     *
     * @param row the row to be inserted.
     */
    boolean insertRow(IDatabaseRow row);


    /**
     * updates a movie from the database.
     *
     * @param row the row to be inserted.
     */
    boolean updateRow(IDatabaseRow row);


    /**
     * Searches a movie into the database and returns the result.
     * The operation is transactional.
     *
     * @param id the list of movies to be searched.
     * @return the list of movies found in the database.
     */
    IDatabaseRow getMovie(String id);

    List<IDatabaseRow> getMovies(List<MovieItem> movies);

    IDatabaseRow getGenre(String id);

    /**
     * insert movies into the database in a transactional way.
     * The operation succeeds if all the rows are inserted successfully.
     * returns the list of the failed inserted rows.
     *
     * @param rows the rows to be inserted.
     * @return the list of failed rows.
     */
    List<IDatabaseRow> insertRows(List<IDatabaseRow> rows);

    /**
     * updates movies to the database.
     * The operation is transactional.
     * The operation succeeds if all the rows are updated.
     * returns a list of failed rows.
     *
     * @param rows the rows to be updated.
     * @return the list of failed rows.
     */
    List<IDatabaseRow> updateRows(List<IDatabaseRow> rows);

    /**
     * Deletes a list of movies from the database.
     * The operation is transactional.
     * The operation succeeds if all the rows are deleted.
     * returns a list of failed rows.
     *
     * @param rows the rows to be updated.
     * @return the list of failed rows.
     */
    List<IDatabaseRow> deleteRows(List<IDatabaseRow> rows);

    /**
     * deletes a movie from the database.
     *
     * @param row the row to be inserted.
     */
    boolean deleteRow(IDatabaseRow row);

    List<IDatabaseRow> getAllMovies();

    List<IDatabaseRow> getGenresByMovieId(String id);

    List<IDatabaseRow> getGenres();
}
