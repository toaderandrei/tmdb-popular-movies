package com.org.android.popularmovies.httpclient;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.org.android.popularmovies.model.MovieImageSize;
import com.org.android.popularmovies.parser.ContentParser;
import com.org.android.popularmovies.parser.MovieGenresDTO;
import com.org.android.popularmovies.parser.MovieReviewsDTO;
import com.org.android.popularmovies.parser.MovieVideosDTO;
import com.org.android.popularmovies.parser.MoviesDTO;
import com.squareup.picasso.Picasso;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * class used for making http requests.
 */
public class HttpClient implements IHttpClient {

    public static final String MESSAGE_IS = "Message is:";
    public static final String GENERAL_IOEXCEPTION = "General IOException";
    private OkHttpClient okHttpClient;
    private static HttpClient instance = null;
    private static final int IO_BUFFER_SIZE = 8 * 1024;

    private static final String TAG = HttpClient.class.getSimpleName();

    public static IHttpClient getInstance() {
        if (instance == null) {
            instance = new HttpClient();
        }
        return instance;
    }

    private HttpClient() {
        okHttpClient = new OkHttpClient();
    }


    private void initTimeout() {
        if (okHttpClient != null) {
            OkHttpClient.Builder builder = okHttpClient.newBuilder();
            builder.connectTimeout(15, TimeUnit.SECONDS);
            builder.readTimeout(15, TimeUnit.SECONDS);
            okHttpClient = builder.build();
        }
    }


    @Override
    public MoviesDTO getMoviesDTO(String sortType, String sortOrder, int page) throws IOException {
        MoviesDTO response;
        initTimeout();
        String url = getURL(sortType, sortOrder, page);
        response = executeRequestAndParse(url);
        return response;
    }

    @Nullable
    private MoviesDTO executeRequestAndParse(String url) throws IOException {
        MoviesDTO response = null;
        try {
            Request request = new Request.Builder().url(url).get().build();

            Response resp = okHttpClient.newCall(request).execute();
            if (resp != null) {
                switch (resp.code()) {
                    case 200:
                        final ResponseBody responseBody = resp.body();
                        final InputStream stream = responseBody.byteStream();
                        if (stream != null) {
                            response = getContentParser().parseMovies(stream);
                        }
                        break;
                    default:
                        response = null;
                        Log.w(TAG, MESSAGE_IS + resp.code());
                        break;
                }
            }
        } catch (IOException ex) {
            throw new IOException(GENERAL_IOEXCEPTION);
        }
        return response;
    }

    @Override
    public Bitmap getBitmapFromServer(int mImageWidth, int mImageHeight, Context context, String data) throws Throwable {
        if (mImageHeight != 0 && mImageWidth != 0) {
            return Picasso.get().load(getPostImageURLPath(data)).resize(mImageWidth, mImageHeight).get();
        }
        return Picasso.get().load(data).get();
    }

    @Override
    public MovieVideosDTO getMovieVideosDTO(String id) throws IOException {
        MovieVideosDTO response = null;
        try {
            //TODO - use HttpUrl parse everywhere.
            Request request = new Request.Builder().url(getMovieVideosURL(id)).get().build();

            Response resp = okHttpClient.newCall(request).execute();
            if (resp != null) {
                switch (resp.code()) {
                    case 200:
                        final ResponseBody responseBody = resp.body();
                        final InputStream stream = responseBody.byteStream();
                        if (stream != null) {
                            response = getContentParser().parseMovieVideos(stream);
                        }
                        break;
                    default:
                        response = null;
                        Log.w(TAG, MESSAGE_IS + resp.code());
                        break;
                }
            }
        } catch (IOException ex) {
            throw new IOException(GENERAL_IOEXCEPTION);
        }
        return response;
    }


    @Override
    public MovieReviewsDTO getMovieReviewsDTO(String movieId) {
        MovieReviewsDTO response = null;
        try {
            HttpUrl httpUrl = HttpUrl.parse(getMovieReviewsURL(movieId));
            if (httpUrl != null) {
                Request request = new Request.Builder().url(httpUrl).get().build();

                Response resp = okHttpClient.newCall(request).execute();
                if (resp != null) {
                    switch (resp.code()) {
                        case 200:
                            final ResponseBody responseBody = resp.body();
                            final InputStream stream = responseBody.byteStream();
                            if (stream != null) {
                                response = getContentParser().parseMovieReviewsDTO(stream);
                            }
                            break;
                        default:
                            response = null;
                            Log.w(TAG, MESSAGE_IS + resp.code());
                            break;
                    }
                }
            } else {
                Log.d(TAG, "malformed url.");
            }
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage(), ex);
        }
        return response;
    }

    @Override
    public boolean loadDataToStream(String urlString, OutputStream outputStream) {
        BufferedOutputStream out = null;
        BufferedInputStream in = null;
        boolean loaded = false;
        try {
            HttpUrl httpUrl = HttpUrl.parse(getPostImageURLPath(urlString));
            if (httpUrl != null) {
                Request request = new Request.Builder().url(httpUrl).get().build();
                Response response = okHttpClient.newCall(request).execute();
                if (response != null) {
                    switch (response.code()) {
                        case 200:
                            final ResponseBody responseBody = response.body();
                            Log.d(TAG, "return the data");
                            in = new BufferedInputStream(responseBody.byteStream(), IO_BUFFER_SIZE);
                            out = new BufferedOutputStream(outputStream, IO_BUFFER_SIZE);
                            int b;
                            while ((b = in.read()) != -1) {
                                out.write(b);
                            }
                            loaded = true;
                            break;
                        default:
                            loaded = false;
                            Log.e(TAG, MESSAGE_IS + response.code());
                            break;
                    }
                }
            } else {
                Log.d(TAG, "malformed url:" + urlString);
            }
        } catch (final IOException e) {
            Log.e(TAG, "Error in downloadBitmap - " + e);
            loaded = false;
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                Log.e(TAG, "IOException.problem when closing the BufferedOutPutStream");
            }
        }
        return loaded;
    }

    @Override
    public MovieGenresDTO getGenresDtos() {
        MovieGenresDTO response = null;
        try {
            Request request = new Request.Builder().url(getGenresUrl()).get().build();

            Response resp = okHttpClient.newCall(request).execute();
            if (resp != null) {
                switch (resp.code()) {
                    case 200:
                        final ResponseBody responseBody = resp.body();
                        final InputStream stream = responseBody.byteStream();
                        if (stream != null) {
                            response = getContentParser().parseGenres(stream);
                        }
                        break;
                    default:
                        response = null;
                        Log.w(TAG, MESSAGE_IS + resp.code());
                        break;
                }
            }
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage(), ex);
        }
        return response;
    }

    @Override
    public MoviesDTO getMoviesDTO() throws IOException {
        MoviesDTO response = null;
        initTimeout();
        String url = getURL();
        response = executeRequestAndParse(url);
        return response;
    }

    public ContentParser getContentParser() {
        return ContentParser.getInstance();
    }

    private String getURL() {
        return getURLInternal();
    }

    private String getURL(String type, String order, int page) {
        String mPage = HttpConstants.PAGE_PARAM + page + HttpConstants.AND_CHARACTER;
        return getURLInternal(mPage, HttpConstants.SORT_ORDER, type, HttpConstants.POINT_CHARACTER, order);
    }

    private String getMovieVideosURL(String... params) {
        StringBuilder builder = new StringBuilder();
        builder.append(HttpConstants.API_BASE_PATH);
        builder.append(HttpConstants.SLASH_CHARACTER);
        builder.append(HttpConstants.MOVIE_CHARACTER);
        builder.append(HttpConstants.SLASH_CHARACTER);
        if (params != null && params.length > 0) {
            for (String param : params) {
                builder.append(param);
                builder.append(HttpConstants.SLASH_CHARACTER);
            }
        }
        builder.append(HttpConstants.VIDEOS_CHARACTER);
        builder.append(HttpConstants.QUESTION_CHARACTER);
        builder.append(HttpConstants.AND_CHARACTER);
        builder.append(HttpConstants.API_KEY_PARAM_KEY);
        builder.append(HttpConstants.API_KEY_PARAM_KEY_VALUE);
        return builder.toString();
    }

    private String getMovieReviewsURL(String... params) {
        StringBuilder builder = new StringBuilder();
        builder.append(HttpConstants.API_BASE_PATH);
        builder.append(HttpConstants.SLASH_CHARACTER);
        builder.append(HttpConstants.MOVIE_CHARACTER);
        builder.append(HttpConstants.SLASH_CHARACTER);
        if (params != null && params.length > 0) {
            for (String param : params) {
                builder.append(param);
                builder.append(HttpConstants.SLASH_CHARACTER);
            }
        }
        builder.append(HttpConstants.REVIEWS_CHARACTER);
        builder.append(HttpConstants.QUESTION_CHARACTER);
        builder.append(HttpConstants.AND_CHARACTER);
        builder.append(HttpConstants.API_KEY_PARAM_KEY);
        builder.append(HttpConstants.API_KEY_PARAM_KEY_VALUE);
        return builder.toString();
    }


    public String getPostImageURLPath(String data) {
        StringBuilder builder = new StringBuilder();
        builder.append(HttpConstants.API_POST_IMAGE_URL);
        builder.append(HttpConstants.SLASH_CHARACTER);
        builder.append(MovieImageSize.ORIGINAL.getImgSize());
        builder.append(data);
        return builder.toString();
    }

    public String getGenresUrl() {
        StringBuilder builder = new StringBuilder();
        builder.append(HttpConstants.API_BASE_PATH);
        builder.append(HttpConstants.SLASH_CHARACTER);
        builder.append(HttpConstants.GENRE_CHARACTER);
        builder.append(HttpConstants.SLASH_CHARACTER);
        builder.append(HttpConstants.MOVIE_CHARACTER);
        builder.append(HttpConstants.SLASH_CHARACTER);
        builder.append(HttpConstants.LIST_CHARACTER);
        builder.append(HttpConstants.QUESTION_CHARACTER);
        builder.append(HttpConstants.API_KEY_PARAM_KEY);
        builder.append(HttpConstants.API_KEY_PARAM_KEY_VALUE);
        return builder.toString();
    }

    @NonNull
    private String getURLInternal(String... params) {
        StringBuilder builder = new StringBuilder();
        builder.append(HttpConstants.API_BASE_PATH);
        builder.append(HttpConstants.SLASH_CHARACTER);
        builder.append(HttpConstants.DISCOVER_MOVIE);
        if (params != null && params.length > 0) {
            for (String param : params) {
                builder.append(param);
            }
        }
        builder.append(HttpConstants.AND_CHARACTER);
        builder.append(HttpConstants.API_KEY_PARAM_KEY);
        builder.append(HttpConstants.API_KEY_PARAM_KEY_VALUE);
        return builder.toString();
    }

}
