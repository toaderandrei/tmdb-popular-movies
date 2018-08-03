package com.org.android.popularmovies.httpclient;

import android.content.Context;
import android.graphics.Bitmap;

import com.org.android.popularmovies.parser.MovieGenresDTO;
import com.org.android.popularmovies.parser.MovieReviewsDTO;
import com.org.android.popularmovies.parser.MovieVideosDTO;
import com.org.android.popularmovies.parser.MoviesDTO;

import java.io.IOException;
import java.io.OutputStream;

/**
 * interface that defines the network requests.
 */
public interface IHttpClient {

    /**
     * gets a data transfer object in the form of MoviesDTO. This object is later on  transformed to data objects, models.
     **/
    MoviesDTO getMoviesDTO() throws IOException;

    /**
     * gets a data transfer object in the form of MoviesDTO based on sorting. This object is later on  transformed to data objects, models.
     **/
    MoviesDTO getMoviesDTO(String sortType, String sortOrder, int page) throws IOException;

    Bitmap getBitmapFromServer(int mWidth, int mHeight, Context context, String path) throws Throwable;

    MovieVideosDTO getMovieVideosDTO(String id) throws IOException;

    /**
     * gets the current genres from the server.
     *
     * @return a list of genres.
     */
    MovieGenresDTO getGenresDtos() throws IOException;

    /**
     * loads the movie reviews from the server.
     *
     * @return the list of movie reviews.
     */
    MovieReviewsDTO getMovieReviewsDTO(String movieId) throws IOException;

    /**
     * loads data from a url. The data that is downloaded is then loaded into
     * anoutputstream, provided as a parameter.
     *
     * @param urlString    the representatation of the url as a string.
     * @param outputStream the outstream that contains the data loaded
     *                     from the url.
     * @return true if the operation was successful or false otherwise.
     */
    boolean loadDataToStream(String urlString, OutputStream outputStream);
}
