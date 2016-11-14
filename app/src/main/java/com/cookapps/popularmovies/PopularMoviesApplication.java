package com.cookapps.popularmovies;

import android.app.Application;

import com.facebook.stetho.Stetho;

/**
 * Created by danielcook on 8/30/16.
 *
 */
public class PopularMoviesApplication extends Application {
    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);
    }

}
