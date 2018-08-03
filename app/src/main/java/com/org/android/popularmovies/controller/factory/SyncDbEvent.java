package com.org.android.popularmovies.controller.factory;

/**
 * Created by andrei on 12/11/15.
 */
public class SyncDbEvent {

    public static final int UPDATE_MOVIE = 0;
    public static final int UPDATE_MOVIES = 1;
    public static final int INSERT_MOVIE = 2;
    public static final int INSERT_MOVIES = 3;
    public static final int DELETE_MOVIE = 4;
    public static final int DELETE_MOVIES = 5;
    public static final int INSERT_OR_UPDATE_MOVIE = 6;
    public static final int LOAD_MOVIE_STATUS = 7;

    public static final int INSERT_OR_UPDATE_GENRES = 8;
    public static final int LOAD_GENRES = 9;
    public static final int LOAD_FAVORITE_MOVIES = 10;

}
