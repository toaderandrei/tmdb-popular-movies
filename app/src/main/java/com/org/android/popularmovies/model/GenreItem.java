package com.org.android.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;

/**
 * Class that defines a genre item.
 */
public class GenreItem implements Parcelable {

    @Expose
    private int id;

    @Expose
    private String name;

    public GenreItem() {
    }

    public GenreItem(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public GenreItem setId(int id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public GenreItem setName(String name) {
        this.name = name;
        return this;
    }

    // --------------------------------------------------------------------------------------

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.name);
    }

    protected GenreItem(Parcel in) {
        this.id = in.readInt();
        this.name = in.readString();
    }

    public static final Creator<GenreItem> CREATOR = new Creator<GenreItem>() {
        public GenreItem createFromParcel(Parcel source) {
            return new GenreItem(source);
        }

        public GenreItem[] newArray(int size) {
            return new GenreItem[size];
        }
    };
}
