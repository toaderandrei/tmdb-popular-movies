package com.org.android.popularmovies.pojo;

import com.org.android.popularmovies.controller.DataController;
import com.org.android.popularmovies.model.GenreItem;
import com.org.android.popularmovies.model.MovieItem;
import com.org.android.popularmovies.model.MovieReview;
import com.org.android.popularmovies.model.MovieVideo;
import com.org.android.popularmovies.parser.MovieGenresDTO;
import com.org.android.popularmovies.parser.MovieGenreItemDTO;
import com.org.android.popularmovies.parser.MovieItemDTO;
import com.org.android.popularmovies.parser.MovieReviewDTO;
import com.org.android.popularmovies.parser.MovieReviewsDTO;
import com.org.android.popularmovies.parser.MovieVideoDTO;
import com.org.android.popularmovies.parser.MovieVideosDTO;
import com.org.android.popularmovies.parser.MoviesDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * Class that allows converting o the movie data transfer objects to movie data model.
 */
public class POJOContentTranslator {

    public static List<MovieItem> convertMoviesDToToModels(MoviesDTO moviesDTO) {
        List<MovieItem> movieItems = new ArrayList<>();
        for (MovieItemDTO dto : moviesDTO.getResults()) {
            List<GenreItem> genreItems = new ArrayList<>();
            long id = dto.getId();
            String title = getStringValue(dto.getTitle());
            String original_title = getStringValue(dto.getOriginal_title());
            String overview = getStringValue(dto.getOverview());
            String posterPath = getStringValue(dto.getPoster_path());
            String releaseDate = getStringValue(dto.getRelease_date());
            double popularity = getDoubleValue(dto.getPopularity());
            double voteAverage = getDoubleValue(dto.getVoteAverage());
            for (Integer genreId : dto.getGenre_ids()) {
                GenreItem item = DataController.getInstance().getGenreItem(genreId);
                if (item != null) {
                    genreItems.add(item);
                }
            }
            MovieItem movieItem = new MovieItem(id, false, popularity, voteAverage, title, original_title, overview, posterPath, releaseDate, genreItems);
            movieItems.add(movieItem);
        }
        return movieItems;
    }

    public static List<MovieVideo> convertMovieVideoDTOsToMovieVideos(MovieVideosDTO moveiVideosDTO) {
        List<MovieVideo> movieVideos = new ArrayList<>();

        for (MovieVideoDTO dto : moveiVideosDTO.getResults()) {
            MovieVideo video = new MovieVideo();
            video.setId(getStringValue(dto.getId()));
            video.setIso(getStringValue(dto.getIso_639_1()));
            video.setKey(getStringValue(dto.getKey()));
            video.setName(getStringValue(dto.getName()));
            video.setSize(getIntValue(dto.getSize()));
            video.setType(getStringValue(dto.getType()));
            video.setSite(getStringValue(dto.getSite()));
            movieVideos.add(video);
        }
        return movieVideos;
    }

    public static List<MovieReview> convertMovieReviewsDToToMovieReviews(MovieReviewsDTO moveiVideosDTO) {
        List<MovieReview> movieReviews = new ArrayList<>();

        for (MovieReviewDTO dto : moveiVideosDTO.getReviews()) {
            MovieReview review = new MovieReview();
            review.setId(getStringValue(dto.getId()));
            review.setAuthor(getStringValue(dto.getAuthor()));
            review.setUrl(getStringValue(dto.getUrl()));
            review.setContent(getStringValue(dto.getContent()));
            movieReviews.add(review);
        }
        return movieReviews;
    }

    public static List<GenreItem> convertGenresDtoToGenreItems(MovieGenresDTO genreDtos) {
        List<GenreItem> genreItems = new ArrayList<>();
        for (MovieGenreItemDTO dto : genreDtos.getGenres()) {
            if (dto.getId() < 0) {
                continue;
            }
            GenreItem genreItem = new GenreItem();
            genreItem.setId(dto.getId());
            if (dto.getName() != null && !dto.getName().isEmpty()) {
                genreItem.setName(dto.getName());
            }
            genreItems.add(genreItem);
        }
        return genreItems;
    }

    //===============data manipulation part======================================================//

    private static String getStringValue(String initialValue) {
        if (initialValue == null || initialValue.equals("")) {
            return "";
        }
        return initialValue;
    }

    private static Integer getIntValue(Integer initialValue) {
        if (initialValue == null || initialValue < 0) {
            return -1;
        }
        return initialValue;
    }

    private static Double getDoubleValue(Double id) {
        if (id == null || id < 0) {
            return -1d;
        }
        return id;
    }

    private static Long getLongValue(Long id) {
        if (id == null || id < 0) {
            return -1L;
        }
        return id;
    }

    private static Long getLongValue(long id) {
        if (id < 0) {
            return -1L;
        }
        return id;
    }
}
