package com.org.android.popularmovies.db.interfaces;

import com.org.android.popularmovies.db.model.GenericColumn;

/**
 * Created by andrei on 12/7/15.
 */
public interface ITablesCreate {

    /**
     * retrieves the array contains all the columns.
     *
     * @return the array of columns.
     */
    GenericColumn[] getColumns();

    /**
     * gets the table name.
     *
     * @return the table name.
     */
    String getName();
}
