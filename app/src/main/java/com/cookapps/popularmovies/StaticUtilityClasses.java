package com.cookapps.popularmovies;

import android.net.Uri;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


/**
 * Created by danielcook on 8/13/16.
 * To house static utility classes that will appear across activities
 */
public class StaticUtilityClasses {

    private static final String LOG_TAG = StaticUtilityClasses.class.getSimpleName();

    public static Uri buildYoutubeURI(String trailerKey){

        String baseString = "http://www.youtube.com/watch?v=";
        String fullString = baseString + trailerKey;
        return Uri.parse(fullString);
    }

    public static URL buildPosterURL(String posterPath, String size){

        // Construct the url to the poster
        final String BASE_URL = "http://image.tmdb.org/t/p/";

        Uri builtUri = Uri.parse(BASE_URL).buildUpon().
                appendPath(size).
                appendEncodedPath(posterPath).build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
            Log.d(LOG_TAG, "URL: " + url);

        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error", e);
            e.printStackTrace();
            return null;
        }

        return url;
    }

    public static String queryMovieDatabaseById(String movieId){

        // create url connection
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String moviesString = null;

        try {

            final String BASE_URL = "https://api.themoviedb.org/3/movie";
            final String APIKEY_PARAM = "api_key";

            Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                    .appendPath(movieId)  // Movie Id passed in
                    .appendQueryParameter(APIKEY_PARAM, "put api key here") // TODO: in order to work, an api key must go here
                    .build();

            URL url = new URL(builtUri.toString());

            Log.v(LOG_TAG, "URL: " + url);

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                return null;
            }

            return buffer.toString();

        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }
    }

    public static String queryMovieDatabase(String movieId, String secondParameter){

        // create url connection
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String moviesString = null;

        try {

            final String BASE_URL = "https://api.themoviedb.org/3/movie";
            final String APIKEY_PARAM = "api_key";

            Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                    .appendPath(movieId)  // Movie Id passed in
                    .appendPath(secondParameter)  // Query parameter for "videos"
                    .appendQueryParameter(APIKEY_PARAM, "52f1d3aeb2aab547b2a8f42b70c2fc4a")
                    .build();

            URL url = new URL(builtUri.toString());

            Log.v(LOG_TAG, "URL: " + url);

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                return null;
            }

            return buffer.toString();

        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }
    }
}
