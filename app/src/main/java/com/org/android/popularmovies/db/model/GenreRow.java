package com.org.android.popularmovies.db.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.org.android.popularmovies.db.database.DatabaseAdapterUtils;
import com.org.android.popularmovies.db.table.TablesCreate;

/**
 * Genrerow class that allows the saving of the genres to the db.
 */
public class GenreRow extends AbstractRow {

    public static final String MOVIE_FOREIGN_COLUMN_ID = "_movie_id";
    public static final String GENRE_COLUMN_ID = "_genre_id";
    public static final String GENRE_COLUMN_NAME = "genre_name";

    private String genreName;
    private String movieId;
    private String genreId;

    public GenreRow(Cursor cursor) {
        super(cursor);
    }

    public GenreRow(Integer genreId, String genreName, String movieId) {
        this.genreId = String.valueOf(genreId);
        this.genreName = genreName;
        this.movieId = movieId;
    }

    @Override
    protected ContentValues getContentValues() {
        ContentValues values = new ContentValues();
        values.put(MOVIE_FOREIGN_COLUMN_ID, getMovieId());

        values.put(GENRE_COLUMN_ID, getGenreId());
        values.put(GENRE_COLUMN_NAME, getGenreName());
        return values;
    }

    @Override
    protected String getWhereClause() {
        String whereClause = "";
        whereClause += " " + GENRE_COLUMN_ID + " = " + getGenreId();
        return whereClause;
    }

    @Override
    protected String[] getWhereArgs() {
        return null;
    }

    @Override
    protected void readDataFromCursor(Cursor cursor) throws SQLException {
        int idxGenreColumnId = cursor.getColumnIndexOrThrow(GENRE_COLUMN_ID);
        this.genreId = getStringValueFromCursor(cursor, idxGenreColumnId);


        int idxMovieColumnId = cursor.getColumnIndexOrThrow(MOVIE_FOREIGN_COLUMN_ID);
        this.movieId = getStringValueFromCursor(cursor, idxMovieColumnId);

        int idxGenreColumnName = cursor.getColumnIndexOrThrow(GENRE_COLUMN_NAME);
        this.genreName = getStringValueFromCursor(cursor, idxGenreColumnName);
    }

    @Override
    public TablesCreate getTable() {
        return TablesCreate.GENRE_ROW;
    }

    public String getGenreId() {
        return genreId;
    }

    public String getGenreName() {
        return genreName;
    }

    public static Cursor find(SQLiteDatabase db, String id) {
        TablesCreate table = TablesCreate.GENRE_ROW;
        String queryParam = null;
        String[] param = null;
        if (id != null) {
            queryParam = GENRE_COLUMN_ID + "=?";
            param = new String[]{id};
        }
        return db.query(table.getName(), DatabaseAdapterUtils.getColumnNames(table), queryParam, param, null, null, null, null);
    }

    public static Cursor findGenreByMovieId(SQLiteDatabase db, String id) {
        TablesCreate table = TablesCreate.GENRE_ROW;
        String queryParam = null;
        String[] param = null;
        if (id != null) {
            queryParam = MOVIE_FOREIGN_COLUMN_ID + "=?";
            param = new String[]{id};
        }
        return db.query(table.getName(), DatabaseAdapterUtils.getColumnNames(table), queryParam, param, null, null, null, null);
    }


    public String getMovieId() {
        return movieId;
    }

    public void setMovieId(String movieId) {
        this.movieId = movieId;
    }
}
