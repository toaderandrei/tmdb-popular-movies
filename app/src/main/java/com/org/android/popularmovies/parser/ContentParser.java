package com.org.android.popularmovies.parser;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;

import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Parser for the data.
 */
public class ContentParser {
    private static final String TAG = ContentParser.class.getCanonicalName();
    private static ContentParser instance;
    private Gson gson;


    protected ContentParser() {
        gson = new Gson();
    }

    public static ContentParser getInstance() {
        if (instance == null) {
            instance = new ContentParser();
        }
        return instance;
    }

    public MoviesDTO parseMovies(InputStream stream) {

        MoviesDTO response = null;
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(stream);
            response = gson.fromJson(inputStreamReader, MoviesDTO.class);
        } catch (JsonIOException ex) {
            Log.e(TAG, ex.getMessage(), ex);
        }
        return response;
    }

    public MovieVideosDTO parseMovieVideos(InputStream stream) {
        MovieVideosDTO response = null;
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(stream);
            response = gson.fromJson(inputStreamReader, MovieVideosDTO.class);
        } catch (JsonIOException ex) {
            Log.e(TAG, ex.getMessage(), ex);
        }
        return response;
    }


    public MovieReviewsDTO parseMovieReviewsDTO(InputStream stream) {
        MovieReviewsDTO response = null;
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(stream);
            response = gson.fromJson(inputStreamReader, MovieReviewsDTO.class);
        } catch (JsonIOException ex) {
            Log.e(TAG, ex.getMessage(), ex);
        }
        return response;
    }

    public MovieGenresDTO parseGenres(InputStream stream) {
        MovieGenresDTO response = null;
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(stream);
            response = gson.fromJson(inputStreamReader, MovieGenresDTO.class);

        } catch (JsonIOException ex) {
            Log.e(TAG, ex.getMessage(), ex);
        }
        return response;
    }
}
