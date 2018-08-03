package com.org.android.popularmovies.db.model;

import android.support.annotation.NonNull;

import com.org.android.popularmovies.db.interfaces.IDataRowType;
import com.org.android.popularmovies.db.interfaces.IDatabaseColumn;

/**
 * Generic column used for the constructing the database rows.
 * Every row has a number of columns, which all can have different
 * types.
 */
public class GenericColumn implements IDatabaseColumn {

    private boolean uniqueField = false;

    private boolean primaryKey = false;

    private String rowName;
    private IDataRowType dataRowType;
    private boolean autoIncremented = false;

    public GenericColumn(boolean primaryKey, boolean uniqueField, boolean autoIncremented, String fieldName, @NonNull IDataRowType type) {
        this.uniqueField = uniqueField;
        this.primaryKey = primaryKey;
        this.dataRowType = type;
        this.rowName = fieldName;
        this.autoIncremented = autoIncremented;
    }

    public GenericColumn(boolean primaryKey, String rowName, @NonNull IDataRowType type) {
        this(primaryKey, false, false, rowName, type);
    }

    public GenericColumn(String rowName, @NonNull IDataRowType type) {
        this(false, false, false, rowName, type);
    }


    public GenericColumn(boolean primaryKey, @NonNull IDataRowType type) {
        this(primaryKey, false, false, null, type);
    }

    @Override
    public boolean isPrimaryKey() {
        return primaryKey;
    }

    @Override
    public String getFieldName() {
        return rowName;
    }

    @Override
    public boolean isUnique() {
        return uniqueField;
    }

    @Override
    public IDataRowType getDataType() {
        return dataRowType;
    }


    @Override
    public String toString() {
        return this.rowName;
    }

    public boolean isAutoIncremented() {
        return autoIncremented;
    }
}
