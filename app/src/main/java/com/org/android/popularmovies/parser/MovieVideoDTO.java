package com.org.android.popularmovies.parser;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Movie Video Item DTO.
 */
public class MovieVideoDTO {

    @Expose
    @SerializedName("id")
    protected String id;
    @Expose
    @SerializedName("iso_639_1")
    protected String iso_639_1;
    @Expose
    @SerializedName("key")
    protected String key;
    @Expose
    @SerializedName("name")
    protected String name;
    @Expose
    @SerializedName("site")
    protected String site;
    @Expose
    @SerializedName("size")
    protected Integer size;
    @Expose
    @SerializedName("type")
    protected String type;

    public MovieVideoDTO(String id, String iso_639_1, String key, String name, String site, Integer size, String type) {
        this.id = id;
        this.iso_639_1 = iso_639_1;
        this.key = key;
        this.name = name;
        this.site = site;
        this.size = size;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public String getIso_639_1() {
        return iso_639_1;
    }

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public String getSite() {
        return site;
    }

    public Integer getSize() {
        return size;
    }

    public String getType() {
        return type;
    }
}

