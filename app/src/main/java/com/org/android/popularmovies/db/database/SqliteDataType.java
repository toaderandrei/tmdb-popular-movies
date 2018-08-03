package com.org.android.popularmovies.db.database;

import com.org.android.popularmovies.db.interfaces.IDataRowType;

/**
 * Sqlite data types.
 * */
public enum SqliteDataType implements IDataRowType {

    Boolean("BOOLEAN"),
    Integer("INTEGER"),
    Double("REAL"),
    String("TEXT"),
    Long("FLOAT");
    private String dataType;

    private SqliteDataType(String dataType) {
        this.dataType = dataType;
    }

    @Override
    public String getDataTypeName() {
        return this.dataType;
    }

}
