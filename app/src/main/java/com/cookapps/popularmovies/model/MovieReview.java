package com.cookapps.popularmovies.model;

/**
 * Created by danielcook on 8/30/16.
 * To hold movie review data
 */
public class MovieReview {

    private String author;
    private String content;
    private String url;

    public MovieReview() {
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}


