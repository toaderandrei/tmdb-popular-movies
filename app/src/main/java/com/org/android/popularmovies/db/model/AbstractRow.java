package com.org.android.popularmovies.db.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.org.android.popularmovies.db.constants.DatabaseErrors;
import com.org.android.popularmovies.db.database.DatabaseAdapterUtils;
import com.org.android.popularmovies.db.interfaces.IDatabaseRow;
import com.org.android.popularmovies.db.table.TablesCreate;

/**
 * Abstract implementation of all the database rows operations.
 */
public abstract class AbstractRow implements IDatabaseRow {


    public AbstractRow() {

    }

    public AbstractRow(Cursor cursor) {
        moveCursorToTheNextRow(cursor);
    }

    @Override
    public void insert(SQLiteDatabase db) throws Exception {
        if (db != null && db.isOpen() && !db.isReadOnly()) {
            internalInsert(db);
        } else {
            throw new SQLException(DatabaseErrors.DATABASE_NULL_OR_CLOSED);
        }
    }

    @Override
    public void update(SQLiteDatabase db) throws Exception {
        if (db != null && db.isOpen() && !db.isReadOnly()) {
            internalUpdate(db);
        } else {
            throw new SQLException(DatabaseErrors.DATABASE_NULL_OR_CLOSED);
        }
    }

    @Override
    public void delete(SQLiteDatabase db) throws Exception {
        if (db != null && db.isOpen() && !db.isReadOnly()) {
            internalDelete(db);
        } else {
            throw new SQLException(DatabaseErrors.DATABASE_NULL_OR_CLOSED);
        }
    }


    @Override
    public void select(SQLiteDatabase db) throws android.database.SQLException {
        Cursor cursor = db.query(getTable().getName(), DatabaseAdapterUtils.getColumnNames(getTable()),
                getWhereClause(), null, null, null, null);
        moveCursorToTheNextRow(cursor);
        cursor.close();
    }

    private void moveCursorToTheNextRow(Cursor cursor) {
        if (cursor != null && !cursor.isClosed()) {
            if (cursor.isBeforeFirst()) {
                cursor.moveToNext();
            }
            readDataFromCursor(cursor);
            cursor.moveToNext();
        } else {
            if (cursor != null) {
                cursor.close();
            }
            throw new SQLException(DatabaseErrors.CURSOR_NULL_OR_CLOSED);
        }
    }

    //============================abstract methods=============================================//

    /**
     * abstract method for inserting the data into the database.
     * Every custom row that extends this class must implement
     * its own getContentValues.
     *
     * @param db the Sqlitedatabase used for inserting the data.
     */
    protected void internalInsert(SQLiteDatabase db) {
        //Statement stm = db.compileStatement(sql);
        long result = db.insertOrThrow(getTable().getName(), null, getContentValues());
        if (result < 0) {
            throw new IllegalArgumentException(DatabaseErrors.DATABASE_ROW_INSERTION_FAILED);
        }
    }

    /**
     * abstract method for deleting the data from the database.
     * Every custom row that extends this class must implement
     * its own delete.
     *
     * @param db the Sqlitedatabase used for inserting the data.
     */
    protected void internalDelete(SQLiteDatabase db) {
        long result = db.delete(getTable().getName(), getWhereClause(), getWhereArgs());
        if (result < 0) {
            throw new IllegalArgumentException(DatabaseErrors.DATABASE_ROW_DELETION_FAILED);
        }
    }

    /**
     * abstract method for updating the data into the database.
     * Every custom row that extends this class must implement
     * its own update method.
     *
     * @param db the Sqlitedatabase used for inserting the data.
     */
    protected void internalUpdate(SQLiteDatabase db) {
        long result = db.update(getTable().getName(), getContentValues(), getWhereClause(), getWhereArgs());
        if (result < 0) {
            throw new IllegalArgumentException(DatabaseErrors.DATABASE_ROW_UPDATE_FAILED);
        }
    }


    protected String getStringValueFromCursor(Cursor cursor, int columnIndex) {
        if (cursor.isNull(columnIndex)) {
            return null;
        } else
            return cursor.getString(columnIndex);
    }


    /**
     * gets the Double value at the columnIndex from the cursor.
     * If the value at the index is null returns null.
     *
     * @param cursor      from which the value is retrieved.
     * @param columnIndex the index of the colum.
     * @return the value at the columnIndex.
     */
    protected Double getDoubleValueFromCursor(Cursor cursor, int columnIndex) {
        if (cursor.isNull(columnIndex)) {
            return null;
        } else
            return cursor.getDouble(columnIndex);
    }

    /**
     * gets the Double value at the columnIndex from the cursor.
     * If the value at the index is null returns null.
     *
     * @param cursor      from which the value is retrieved.
     * @param columnIndex the index of the colum.
     * @return the value at the columnIndex.
     */
    protected Long getLongValueFromCursor(Cursor cursor, int columnIndex) {
        if (cursor.isNull(columnIndex)) {
            return null;
        } else
            return cursor.getLong(columnIndex);
    }


    /**
     * gets the value at the columnindex from the cursor.
     * returns null if the value at the columnIndex is null.
     *
     * @param cursor      from which the value is retrieved.
     * @param columnIndex the index of the colum.
     * @return the value at the columnIndex.
     */
    protected Integer getIntValueFromCursor(Cursor cursor, int columnIndex) {
        if (cursor.isNull(columnIndex)) {
            return null;
        } else
            return cursor.getInt(columnIndex);
    }

    protected Integer getBoolValue(Cursor cursor, int columnIndex) {
        if (cursor.isNull(columnIndex)) {
            return null;
        } else {
            return cursor.getInt(columnIndex);
        }
    }

    //============================start of abstract methods=============================================//

    /**
     * gets the contentvalues object for each specific operation
     *
     * @return the ContentValues object.
     */
    protected abstract ContentValues getContentValues();

    protected abstract String getWhereClause();

    /**
     * the where arguments.
     *
     * @return the string array containing the whereArgs.
     */
    protected abstract String[] getWhereArgs();

    protected abstract void readDataFromCursor(Cursor cursor) throws SQLException;

    public abstract TablesCreate getTable();

    //============================end of abstract methods=============================================//
}
