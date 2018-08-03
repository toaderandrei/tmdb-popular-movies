package com.org.android.popularmovies.db.adapter;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.org.android.popularmovies.application.PopularMoviesApp;
import com.org.android.popularmovies.db.database.SqliteDatabaseOpenHelper;
import com.org.android.popularmovies.db.interfaces.IDatabaseRow;
import com.org.android.popularmovies.db.model.GenreRow;
import com.org.android.popularmovies.db.model.MovieRow;
import com.org.android.popularmovies.model.MovieItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter for retrieving data from the database.
 * This class is responsible for all the queries to the database, select, delete, insert, update.
 */
public class SqliteDatabaseAdapterImpl implements SqliteDatabaseAdapter {

    private static final String TAG = SqliteDatabaseAdapterImpl.class.getCanonicalName();

    private final Object dbLock = new Object();
    private static SqliteDatabaseAdapterImpl instance = null;
    private SQLiteDatabase db;
    private boolean isDatabaseOpened;

    protected SqliteDatabaseAdapterImpl() {
        isDatabaseOpened = false;
        checkIfIsDbOpen();
    }

    protected boolean checkIfIsDbOpen() {
        if (!isDatabaseOpened) {
            openAccessDb(true);
        }
        return isDatabaseOpened;
    }

    private void openAccessDb(boolean write) throws SQLException {

        SqliteDatabaseOpenHelper openHelper;
        openHelper = new SqliteDatabaseOpenHelper(PopularMoviesApp.getInstance().getApplicationContext());
        if (db == null) {
            if (write) {
                db = openHelper.getWritableDatabase();
            } else {
                db = openHelper.getReadableDatabase();
            }
        }
        if (db != null) {
            if (db.isOpen()) {
                isDatabaseOpened = true;
            }
        }
    }

    public static SqliteDatabaseAdapterImpl getInstance() {
        if (instance == null) {
            instance = new SqliteDatabaseAdapterImpl();
        }
        return instance;
    }

    @Override
    public boolean insertRow(IDatabaseRow row) {
        boolean insert;
        checkIfIsDbOpen();
        try {
            row.insert(getDb());
            insert = true;
        } catch (Exception sqex) {
            Log.e(TAG, "exception when inserting row:" + row);
            insert = false;
        }
        return insert;
    }

    @Override
    public boolean updateRow(IDatabaseRow row) {
        boolean update;
        checkIfIsDbOpen();
        synchronized (dbLock) {
            try {
                row.update(getDb());
                update = true;
            } catch (Exception sqex) {
                Log.e(TAG, "exception when inserting row:" + row);
                update = false;
            }
        }
        return update;
    }


    @Override
    public List<IDatabaseRow> getMovies(List<MovieItem> movies) {
        checkIfIsDbOpen();
        List<IDatabaseRow> rows = new ArrayList<>();
        List<IDatabaseRow> failedRows = new ArrayList<>();

        if (movies != null) {
            Cursor cursor = null;
            synchronized (dbLock) {
                getDb().beginTransaction();
                for (MovieItem movie : movies) {
                    MovieRow row = null;
                    try {
                        cursor = MovieRow.find(getDb(), String.valueOf(movie.getId()));
                        while (!cursor.isAfterLast()) {
                            row = new MovieRow(cursor);
                            rows.add(row);
                        }
                    } catch (Exception sqex) {
                        Log.e(TAG, "exception when inserting row:" + movie);
                        Log.e(TAG, "Nr of failed rows:" + failedRows.size());
                        failedRows.add(row);
                    }
                }
                getDb().setTransactionSuccessful();
                getDb().endTransaction();
            }
        }
        return rows;
    }


    @Override
    public List<IDatabaseRow> getAllMovies() {
        checkIfIsDbOpen();
        List<IDatabaseRow> rows = new ArrayList<>();
        Cursor cursor = null;
        synchronized (dbLock) {
            getDb().beginTransaction();
            try {
                cursor = MovieRow.find(getDb(), null);
                while (!cursor.isAfterLast()) {
                    MovieRow row = new MovieRow(cursor);
                    rows.add(row);
                }
                getDb().setTransactionSuccessful();
            } catch (Exception sqex) {
                Log.e(TAG, "exception when loading all movies:");
            }
        }
        getDb().endTransaction();
        return rows;
    }

    @Override
    public List<IDatabaseRow> getGenres() {
        checkIfIsDbOpen();
        List<IDatabaseRow> rows = new ArrayList<>();
        Cursor cursor = null;
        synchronized (dbLock) {
            getDb().beginTransaction();
            try {
                cursor = GenreRow.find(getDb(), null);
                while (!cursor.isAfterLast()) {
                    GenreRow row = new GenreRow(cursor);
                    rows.add(row);
                }
                getDb().setTransactionSuccessful();
            } catch (Exception sqex) {
                Log.e(TAG, "exception when loading all movies:");
            }
        }
        getDb().endTransaction();
        return rows;
    }


    @Override
    public IDatabaseRow getMovie(String id) {
        checkIfIsDbOpen();
        IDatabaseRow row = null;
        if (id != null) {
            Cursor cursor = null;
            synchronized (dbLock) {
                getDb().beginTransaction();
                try {
                    cursor = MovieRow.find(getDb(), id);
                    while (!cursor.isAfterLast()) {
                        row = new MovieRow(cursor);
                    }
                    getDb().setTransactionSuccessful();
                } catch (Exception sqex) {
                    Log.e(TAG, "exception when inserting row:" + id);
                } finally {
                    if (cursor != null && !cursor.isClosed()) {
                        cursor.close();
                    }
                }
                getDb().endTransaction();
            }
        }
        return row;
    }

    @Override
    public IDatabaseRow getGenre(String id) {
        checkIfIsDbOpen();
        IDatabaseRow row = null;
        if (id != null) {
            Cursor cursor = null;
            synchronized (dbLock) {
                getDb().beginTransaction();
                try {
                    cursor = GenreRow.find(getDb(), id);
                    while (!cursor.isAfterLast()) {
                        row = new GenreRow(cursor);
                    }
                    getDb().setTransactionSuccessful();
                } catch (Exception sqex) {
                    Log.e(TAG, "exception when retrieving the  row:" + id);
                } finally {
                    if (cursor != null && !cursor.isClosed()) {
                        cursor.close();
                    }
                }
                getDb().endTransaction();
            }
        }
        return row;
    }


    @Override
    public List<IDatabaseRow> getGenresByMovieId(String id) {
        checkIfIsDbOpen();
        List<IDatabaseRow> rows = new ArrayList<>();
        if (id != null) {
            Cursor cursor = null;
            synchronized (dbLock) {
                getDb().beginTransaction();
                try {
                    cursor = GenreRow.findGenreByMovieId(getDb(), id);
                    while (!cursor.isAfterLast()) {
                        GenreRow row = new GenreRow(cursor);
                        rows.add(row);
                    }
                    getDb().setTransactionSuccessful();
                } catch (Exception sqex) {
                    Log.e(TAG, "exception when retrieving the  rows:" + id);
                } finally {
                    if (cursor != null && !cursor.isClosed()) {
                        cursor.close();
                    }
                }
                getDb().endTransaction();
            }
        }
        return rows;
    }

    @Override
    public List<IDatabaseRow> insertRows(List<IDatabaseRow> rows) {
        checkIfIsDbOpen();
        List<IDatabaseRow> rowsFailed = new ArrayList<>();
        if (rows != null) {
            synchronized (dbLock) {
                getDb().beginTransaction();
                for (IDatabaseRow row : rows) {
                    try {
                        row.insert(getDb());
                        getDb().setTransactionSuccessful();
                    } catch (Exception sqex) {
                        Log.e(TAG, "exception when inserting row:" + row);
                        rowsFailed.add(row);
                    }
                }
                getDb().endTransaction();
            }
        }
        return rowsFailed;
    }

    @Override
    public List<IDatabaseRow> updateRows(List<IDatabaseRow> rows) {
        checkIfIsDbOpen();
        List<IDatabaseRow> rowsFailed = new ArrayList<>();
        if (rows != null) {
            synchronized (dbLock) {
                getDb().beginTransaction();
                for (IDatabaseRow row : rows) {
                    try {
                        row.update(getDb());
                    } catch (Exception sqex) {
                        Log.e(TAG, "exception when inserting row:" + row);
                        rowsFailed.add(row);
                    }
                }
                getDb().setTransactionSuccessful();
                getDb().endTransaction();
            }
        }
        return rowsFailed;
    }

    @Override
    public List<IDatabaseRow> deleteRows(List<IDatabaseRow> rows) {
        checkIfIsDbOpen();
        List<IDatabaseRow> rowsFailed = new ArrayList<>();
        if (rows != null) {
            synchronized (dbLock) {
                getDb().beginTransaction();
                for (IDatabaseRow row : rows) {
                    try {
                        row.update(getDb());
                        getDb().setTransactionSuccessful();
                    } catch (Exception sqex) {
                        Log.e(TAG, "exception when inserting row:" + row);
                        rowsFailed.add(row);
                    }
                }
                getDb().endTransaction();
            }
        }
        return rowsFailed;
    }


    @Override
    public boolean deleteRow(IDatabaseRow row) {
        boolean delete;
        checkIfIsDbOpen();
        synchronized (dbLock) {
            try {
                row.delete(getDb());
                delete = true;
            } catch (Exception sqex) {
                Log.e(TAG, "exception when inserting row:" + row);
                delete = false;
            }
        }
        return delete;
    }

    private SQLiteDatabase getDb() {
        return db;
    }
}
