package com.cookapps.popularmovies.model;

/**
 * Created by danielcook on 8/30/16.
 * To hold movie trailer data
 */
public class MovieTrailer {

    private String name;
    private String site;
    private String key;

    public MovieTrailer() {
    }

    public MovieTrailer(String name, String site, String key) {
        this.name = name;
        this.site = site;
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
