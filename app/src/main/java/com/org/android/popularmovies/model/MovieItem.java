package com.org.android.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * class that defines the data songs objects.
 */
public final class MovieItem implements Parcelable {


    private long id;
    private String original_title;
    private String poster_path;
    private String overview;
    private String release_date;

    private double popularity;
    private String title;
    private double vote_average;
    boolean favored = false;

    List<GenreItem> genres;

    public MovieItem(long id, boolean favored) {
        this.id = id;
        this.favored = favored;
    }

    public MovieItem(long id, boolean favored, double popularity, double vote_average, String title, String original_title, String overview, String poster_path, String release_date, List<GenreItem> genreItems) {
        this.id = id;
        this.favored = favored;
        this.popularity = popularity;
        this.vote_average = vote_average;
        this.title = title;
        this.overview = overview;
        this.original_title = original_title;
        this.poster_path = poster_path;
        this.release_date = release_date;
        this.genres = genreItems;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeDouble(popularity);
        dest.writeDouble(vote_average);
        dest.writeString(overview);
        dest.writeString(title);
        dest.writeString(original_title);
        dest.writeString(release_date);
        dest.writeString(poster_path);
        dest.writeByte(favored ? (byte) 1 : (byte) 0);
        dest.writeTypedList(genres);
        dest.writeInt(favored ? 1 : 0);
    }

    protected MovieItem(Parcel in) {
        id = in.readLong();
        popularity = in.readDouble();
        vote_average = in.readDouble();
        overview = in.readString();
        title = in.readString();
        original_title = in.readString();
        release_date = in.readString();
        poster_path = in.readString();
        this.favored = in.readByte() != 0;
        this.genres = in.createTypedArrayList(GenreItem.CREATOR);
        this.favored = in.readInt() == 1;
    }

    public static final Creator<MovieItem> CREATOR = new Creator<MovieItem>() {
        @Override
        public MovieItem createFromParcel(Parcel in) {
            return new MovieItem(in);
        }

        @Override
        public MovieItem[] newArray(int size) {
            return new MovieItem[size];
        }
    };


    public String getTitle() {
        return title;
    }

    public String getOriginal_title() {
        return original_title;
    }

    public String getPoster_path() {
        return poster_path;
    }

    public String getOverview() {
        return overview;
    }

    public String getRelease_date() {
        return release_date;
    }


    public long getId() {
        return id;
    }

    public double getPopularity() {
        return popularity;
    }

    public Double getVote_average() {
        return vote_average;
    }

    public List<GenreItem> getGenres() {
        return genres;
    }

    public boolean isFavored() {
        return favored;
    }

    public void setFavored(boolean favored) {
        this.favored = favored;
    }

    public void setGenres(List<GenreItem> genreItems) {
        this.genres = genreItems;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }
}
