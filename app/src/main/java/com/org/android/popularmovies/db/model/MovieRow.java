package com.org.android.popularmovies.db.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.org.android.popularmovies.db.database.DatabaseAdapterUtils;
import com.org.android.popularmovies.db.table.TablesCreate;

/**
 * Implementation that defines the database model of the movies.
 * For this we need to implement the internal methods,
 * insert, update, delete, select. The logic for each
 * is defined in the abstract implementation and
 * in the custom implementation we only implement
 * the parts that are different for each custom
 * object.
 */
public class MovieRow extends AbstractRow {

    public static final String MOVIE_ID_COLUMN = "_id";
    public static final String MOVIE_FAVORITE_COLUMN = "movie_favorite";
    public static final String MOVIE_POSTER_PATH_COLUMN = "movie_poster_path";
    public static final String MOVIE_VOTE_AVERAGE_COLUMN = "movie_vote_average";
    public static final String MOVIE_POPULARITY_COLUMN = "movie_popularity";
    public static final String MOVIE_TITLE_COLUMN = "movie_title";
    public static final String MOVIE_ORIGINAL_TITLE_COLUMN = "movie_original_title";
    public static final String MOVIE_RELEASE_DATE_COLUMN = "movie_release_date";
    public static final String MOVIE_OVERVIEW_COLUMN = "movie_overview";

    private long movieId;

    private Integer movieFavorite;

    private double movieVoteAverage;
    private double moviePopularity;
    private String movieReleaseDate;
    private String movieOriginalTitle;
    private String moviePosterPath;
    private String movieTitle;
    private String overview;

    public MovieRow(Cursor cursor) {
        super(cursor);
    }

    public MovieRow(long movieId, boolean movieFavorite) {
        this.movieId = movieId;
        this.movieFavorite = movieFavorite ? 1 : 0;
    }


    public MovieRow(Long movieId,
                    boolean movieFavorite,
                    String movieReleaseDate,
                    String title,
                    String movieOriginalTitle,
                    String moviePosterPath,
                    double movieVoteAverage,
                    double moviePopularity,
                    String overview) {
        this.movieId = movieId;
        this.movieFavorite = movieFavorite ? 1 : 0;
        this.movieReleaseDate = movieReleaseDate;
        this.movieTitle = title;
        this.movieOriginalTitle = movieOriginalTitle;
        this.moviePosterPath = moviePosterPath;
        this.movieVoteAverage = movieVoteAverage;
        this.moviePopularity = moviePopularity;
        this.overview = overview;
    }

    @Override
    public TablesCreate getTable() {
        return TablesCreate.MOVIE_ROW;
    }

    @Override
    protected ContentValues getContentValues() {
        ContentValues values = new ContentValues();
        values.put(MOVIE_ID_COLUMN, getMovieId());
        values.put(MOVIE_FAVORITE_COLUMN, getMovieFavorite());
        values.put(MOVIE_POSTER_PATH_COLUMN, getMoviePosterPath());
        values.put(MOVIE_ORIGINAL_TITLE_COLUMN, getMovieOriginalTitle());
        values.put(MOVIE_POPULARITY_COLUMN, getMoviePopularity());
        values.put(MOVIE_RELEASE_DATE_COLUMN, getMovieReleaseDate());
        values.put(MOVIE_TITLE_COLUMN, getMovieTitle());
        values.put(MOVIE_VOTE_AVERAGE_COLUMN, getMovieVoteAverage());
        values.put(MOVIE_OVERVIEW_COLUMN, getOverview());
        return values;
    }

    @Override
    protected void readDataFromCursor(Cursor cursor) throws SQLException {
        int idColumnIndex = cursor.getColumnIndexOrThrow(MOVIE_ID_COLUMN);
        this.movieId = getLongValueFromCursor(cursor, idColumnIndex);

        int favoriteColumnIndex = cursor.getColumnIndexOrThrow(MOVIE_FAVORITE_COLUMN);
        this.movieFavorite = getIntValueFromCursor(cursor, favoriteColumnIndex);

        int idxReleaseDateColumnIndex = cursor.getColumnIndexOrThrow(MOVIE_RELEASE_DATE_COLUMN);
        this.movieReleaseDate = getStringValueFromCursor(cursor, idxReleaseDateColumnIndex);

        int idxOriginalTitleColumnIndex = cursor.getColumnIndexOrThrow(MOVIE_ORIGINAL_TITLE_COLUMN);
        this.movieOriginalTitle = getStringValueFromCursor(cursor, idxOriginalTitleColumnIndex);

        int idxTitleColumnIndex = cursor.getColumnIndexOrThrow(MOVIE_TITLE_COLUMN);
        this.movieTitle = getStringValueFromCursor(cursor, idxTitleColumnIndex);

        int idxPosterPathColumnIndex = cursor.getColumnIndexOrThrow(MOVIE_POSTER_PATH_COLUMN);
        this.moviePosterPath = getStringValueFromCursor(cursor, idxPosterPathColumnIndex);

        int idxPopularityColumnIndex = cursor.getColumnIndexOrThrow(MOVIE_POPULARITY_COLUMN);
        this.moviePopularity = getDoubleValueFromCursor(cursor, idxPopularityColumnIndex);

        int idxVoteAverageColumnIndex = cursor.getColumnIndexOrThrow(MOVIE_VOTE_AVERAGE_COLUMN);
        this.movieVoteAverage = getDoubleValueFromCursor(cursor, idxVoteAverageColumnIndex);


        int idxOverviewColumnIndex = cursor.getColumnIndexOrThrow(MOVIE_OVERVIEW_COLUMN);
        this.overview = getStringValueFromCursor(cursor, idxOverviewColumnIndex);

    }

    @Override
    protected String[] getWhereArgs() {
        return null;
    }

    @Override
    protected String getWhereClause() {
        String whereClause = "";
        whereClause += " " + MOVIE_ID_COLUMN + " = " + getMovieId();
        return whereClause;
    }

    public static Cursor find(SQLiteDatabase db, String id) {
        TablesCreate table = TablesCreate.MOVIE_ROW;
        String queryParam = null;
        String[] param = null;
        if (id != null) {
            queryParam = MOVIE_ID_COLUMN + "=?";
            param = new String[]{id};
        }
        return db.query(table.getName(), DatabaseAdapterUtils.getColumnNames(table), queryParam, param, null, null, null, null);
    }

    public double getMovieVoteAverage() {
        return movieVoteAverage;
    }

    public double getMoviePopularity() {
        return moviePopularity;
    }

    public String getMovieReleaseDate() {
        return movieReleaseDate;
    }

    public String getMovieOriginalTitle() {
        return movieOriginalTitle;
    }

    public long getMovieId() {
        return movieId;
    }

    public Integer getMovieFavorite() {
        return movieFavorite;
    }

    public String getMoviePosterPath() {
        return moviePosterPath;
    }

    public String getMovieTitle() {
        return movieTitle;
    }

    public String getOverview() {
        return overview;
    }
}
