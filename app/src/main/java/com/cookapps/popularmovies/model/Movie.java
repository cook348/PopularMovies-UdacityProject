package com.cookapps.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by danielcook on 8/12/16.
 * A Movie Class to hold the appropriate data associated with a movie
 */
public class Movie implements Parcelable {

    private String title;
    private String posterPath;
    private String overview;
    private String rating;
    private String date;
    private String id;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int i) {
        out.writeString(title);
        out.writeString(posterPath);
        out.writeString(overview);
        out.writeString(rating);
        out.writeString(date);
        out.writeString(id);
    }

    // Private constructor for Parcelable
    private Movie(Parcel in) {
        title = in.readString();
        posterPath = in.readString();
        overview = in.readString();
        rating = in.readString();
        date = in.readString();
        id = in.readString();
    }

    // After implementing the `Parcelable` interface, we need to create the
    // `Parcelable.Creator<MyParcelable> CREATOR` constant for our class;
    // Notice how it has our class specified as its type.
    public static final Parcelable.Creator<Movie> CREATOR
            = new Parcelable.Creator<Movie>() {

        // This simply calls our new constructor (typically private) and
        // passes along the unmarshalled `Parcel`, and then returns the new object!
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        // We just need to copy this and change the type to match our class.
        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    public Movie() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


}
