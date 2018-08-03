package com.org.android.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Class for videos.
 */
public final class MovieVideo implements Parcelable {

    public static final String SITE_YOUTUBE = "YouTube";
    public static final String TYPE_TRAILER = "Trailer";

    private String id;
    private String iso;
    private String key;
    private String name;

    private String site;

    private int size;
    @Expose
    private String type;

    public MovieVideo() {
    }

    public String getId() {
        return id;
    }

    public MovieVideo setId(String id) {
        this.id = id;
        return this;
    }

    public String getIso() {
        return iso;
    }

    public MovieVideo setIso(String iso) {
        this.iso = iso;
        return this;
    }

    public String getKey() {
        return key;
    }

    public MovieVideo setKey(String key) {
        this.key = key;
        return this;
    }

    public String getName() {
        return name;
    }

    public MovieVideo setName(String name) {
        this.name = name;
        return this;
    }

    public String getSite() {
        return site;
    }

    public MovieVideo setSite(String site) {
        this.site = site;
        return this;
    }

    public int getSize() {
        return size;
    }

    public MovieVideo setSize(int size) {
        this.size = size;
        return this;
    }

    public String getType() {
        return type;
    }

    public MovieVideo setType(String type) {
        this.type = type;
        return this;
    }

    @Override
    public String toString() {
        return "Video{" +
                "key='" + key + '\'' +
                ", name='" + name + '\'' +
                ", site='" + site + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
    // --------------------------------------------------------------------------------------

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.iso);
        dest.writeString(this.key);
        dest.writeString(this.name);
        dest.writeString(this.site);
        dest.writeInt(this.size);
        dest.writeString(this.type);
    }

    protected MovieVideo(Parcel in) {
        this.id = in.readString();
        this.iso = in.readString();
        this.key = in.readString();
        this.name = in.readString();
        this.site = in.readString();
        this.size = in.readInt();
        this.type = in.readString();
    }

    public static final Creator<MovieVideo> CREATOR = new Creator<MovieVideo>() {
        public MovieVideo createFromParcel(Parcel source) {
            return new MovieVideo(source);
        }

        public MovieVideo[] newArray(int size) {
            return new MovieVideo[size];
        }
    };
}