package com.org.android.popularmovies.controller.factory;

/**
 * Database sync action.
 */
public enum SyncDbAction {

    INSERT_MOVIE(0),
    INSERT_MOVIES(1),
    UPDATE_MOVIE(2),
    UPDATE_MOVIES(3),
    DELETE_MOVIE(4),
    DELETE_MOVOIES(5),
    INSERT_UPDATE_MOVIE(6),
    LOAD_UPDATE_STATUS(7),
    INSERT_UPDATE_GENRE(8),
    LOAD_GENRES(9),
    LOAD_FAVORITE_STATUS(10);
    private int id;

    private SyncDbAction(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

}
