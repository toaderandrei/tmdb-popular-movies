package com.org.android.popularmovies.db.interfaces;

import android.database.sqlite.SQLiteDatabase;

import com.org.android.popularmovies.db.table.TablesCreate;

/**
 * Interface that defines the database operations that
 * all the rows support.
 */
public interface IDatabaseRow {

    public void insert(SQLiteDatabase db) throws Exception;

    public void update(SQLiteDatabase db) throws Exception;

    public void delete(SQLiteDatabase db) throws Exception;

    public void select(SQLiteDatabase db) throws Exception;
    
}
