package com.org.android.popularmovies.db.table;

import com.org.android.popularmovies.db.database.SqliteDataType;
import com.org.android.popularmovies.db.interfaces.ITablesCreate;
import com.org.android.popularmovies.db.model.GenericColumn;
import com.org.android.popularmovies.db.model.GenreRow;
import com.org.android.popularmovies.db.model.MovieRow;

/**
 * Defines all the tables.
 */
public enum TablesCreate implements ITablesCreate {

    MOVIE_ROW(new GenericColumn(true, false, true, MovieRow.MOVIE_ID_COLUMN, SqliteDataType.Integer),
            new GenericColumn(MovieRow.MOVIE_FAVORITE_COLUMN, SqliteDataType.Integer),
            new GenericColumn(MovieRow.MOVIE_POSTER_PATH_COLUMN, SqliteDataType.String),
            new GenericColumn(MovieRow.MOVIE_VOTE_AVERAGE_COLUMN, SqliteDataType.Double),
            new GenericColumn(MovieRow.MOVIE_POPULARITY_COLUMN, SqliteDataType.Double),
            new GenericColumn(MovieRow.MOVIE_TITLE_COLUMN, SqliteDataType.String),
            new GenericColumn(MovieRow.MOVIE_ORIGINAL_TITLE_COLUMN, SqliteDataType.String),
            new GenericColumn(MovieRow.MOVIE_RELEASE_DATE_COLUMN, SqliteDataType.String),
            new GenericColumn(MovieRow.MOVIE_OVERVIEW_COLUMN, SqliteDataType.String)),

    GENRE_ROW(new GenericColumn(GenreRow.GENRE_COLUMN_ID, SqliteDataType.String),
            new GenericColumn(GenreRow.MOVIE_FOREIGN_COLUMN_ID, SqliteDataType.String),
            new GenericColumn(GenreRow.GENRE_COLUMN_NAME, SqliteDataType.String));

    private GenericColumn[] columns;

    private TablesCreate(GenericColumn... name) {
        this.columns = name;
    }

    @Override
    public GenericColumn[] getColumns() {
        return columns;
    }

    @Override
    public String getName() {
        return this.name();
    }
}
