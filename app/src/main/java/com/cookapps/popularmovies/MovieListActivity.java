package com.cookapps.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.cookapps.popularmovies.adapters.MovieGridAdapter;
import com.cookapps.popularmovies.model.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * An activity representing a list of Movies. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link MovieDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class MovieListActivity extends AppCompatActivity {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    private Context mContext;

    private MovieGridAdapter mMovieArrayAdapter;
    private String mSortOrder;

    private static final String DETAILFRAGMENT_TAG = "DFTAG";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());


        if (findViewById(R.id.movie_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }

        mContext = this;

        GridView gridView = (GridView) findViewById(R.id.gridView);

        List<Movie> movieList = new ArrayList<>();

        mMovieArrayAdapter = new MovieGridAdapter(this, movieList);
        gridView.setAdapter(mMovieArrayAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Movie movie = mMovieArrayAdapter.getItem(i);

                if(mTwoPane){

                    Bundle arguments = new Bundle();
                    arguments.putParcelable(MovieDetailFragment.MOVIE_PARAM, movie);

                    MovieDetailFragment fragment = new MovieDetailFragment();
                    fragment.setArguments(arguments);
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.movie_detail_container, fragment)
                            .commit();

                } else {
                    Intent intent = new Intent(mContext, MovieDetailActivity.class);
                    intent.putExtra(MovieDetailFragment.MOVIE_PARAM, movie);
                    startActivity(intent);
                }
            }
        });
    }


    @Override
    public void onStart() {
        super.onStart();

        if (isOnline()) {
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
            String sortOrder = sharedPrefs.getString(getString(R.string.pref_sort_key),
                    getString(R.string.pref_sort_default));


            if (mSortOrder == null || !mSortOrder.equals(sortOrder) || mMovieArrayAdapter.getCount() == 0) { // sort order has changed or the movies list is empty
                // fetch the movie list
                mSortOrder = sortOrder;

                if (!sortOrder.equals("favorites")) {
                    QueryMovieDatabase queryMovieDatabaseTask = new QueryMovieDatabase();
                    queryMovieDatabaseTask.execute(sortOrder);
                } else {
                    QueryMovieDatabaseFavorites queryMovieDatabaseFavorites = new QueryMovieDatabaseFavorites();
                    queryMovieDatabaseFavorites.execute("");
                }
            }

        } else {
            Toast.makeText(this, "You must be connected to the internet to use this application."
                    , Toast.LENGTH_LONG).show();
        }
    }


    private class QueryMovieDatabaseFavorites extends AsyncTask<String, String, List<Movie>> {

        @Override
        protected List<Movie> doInBackground(String... strings) {

            // Get all the preferences in SharedPreferences - adapted from StackOverflow
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);
            Map<String,?> keys = sharedPrefs.getAll();

            List<Movie> movieList = new ArrayList<>();

            for(Map.Entry<String,?> entry : keys.entrySet()){

                if(!entry.getKey().equals(getString(R.string.pref_sort_key))){ // If the key is not the sort preference key, it is a movie id that is favorite
                    String jsonString = StaticUtilityClasses.queryMovieDatabaseById(entry.getKey());
                    try {
                        movieList.add(getSingleMovieDataFromJSON(jsonString));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                Log.d("map values",entry.getKey() + ": " +
                        entry.getValue().toString());
            }

            return movieList;
        }

        @Override
        protected void onPostExecute(List<Movie> movieList) {
            if(movieList != null){
                mMovieArrayAdapter.clear();
                for(Movie m : movieList){
                    mMovieArrayAdapter.add(m);
                }
            } else {
                Toast.makeText(mContext, "Something went wrong.  Please check your internet connection and try again."
                        , Toast.LENGTH_LONG).show();
            }
            super.onPostExecute(movieList);
            super.onPostExecute(movieList);
        }

        private Movie getSingleMovieDataFromJSON(String jsonString) throws JSONException{

            String MDB_TITLE = "title";
            String MDB_POSTER_PATH = "poster_path";
            String MDB_OVERVIEW = "overview";
            String MDB_RATING = "vote_average";
            String MDB_DATE = "release_date";
            String MDB_ID = "id";

            JSONObject movieJSON = new JSONObject(jsonString);
            Movie m = new Movie();
            m.setTitle(movieJSON.getString(MDB_TITLE));
            m.setPosterPath(movieJSON.getString(MDB_POSTER_PATH));
            m.setOverview(movieJSON.getString(MDB_OVERVIEW));
            m.setRating(movieJSON.getString(MDB_RATING));
            m.setDate(movieJSON.getString(MDB_DATE));
            m.setId(movieJSON.getString(MDB_ID));

            return m;
        }
    }
    private class QueryMovieDatabase extends AsyncTask<String, String, List<Movie>>{

        private final String LOG_TAG = QueryMovieDatabase.class.getSimpleName();

        @Override
        protected List<Movie> doInBackground(String... sortOrder) {
            // doInBackground method creating following Sunshine Project as a template

            // create url connection
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String moviesString = null;

            try {

                final String BASE_URL = "https://api.themoviedb.org/3/movie";
                final String APIKEY_PARAM = "api_key";

                Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                        .appendPath(sortOrder[0])  // Sort order preference is passed in
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

                moviesString = buffer.toString();

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

            try {
                return getMovieDataFromJSON(moviesString);
            } catch (JSONException e) {
                Log.e(LOG_TAG, "Error caught trying to parse movieJSON string");
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(List<Movie> movieList) {
            if(movieList != null){
                mMovieArrayAdapter.clear();
                for(Movie m : movieList){
                    mMovieArrayAdapter.add(m);
                }
            } else {
                Toast.makeText(mContext, "Something went wrong.  Please check your internet connection and try again."
                        , Toast.LENGTH_LONG).show();
            }
            super.onPostExecute(movieList);
        }


        private List<Movie> getMovieDataFromJSON(String jsonString) throws JSONException{

            String results = "results";
            String MDB_TITLE = "title";
            String MDB_POSTER_PATH = "poster_path";
            String MDB_OVERVIEW = "overview";
            String MDB_RATING = "vote_average";
            String MDB_DATE = "release_date";
            String MDB_ID = "id";

            JSONObject moviesJSON = new JSONObject(jsonString);
            JSONArray moviesJsonArray = moviesJSON.getJSONArray(results);

            List<Movie> moviesList = new ArrayList<>();
            for(int i=0; i <moviesJsonArray.length(); i++){
                JSONObject loopObject = moviesJsonArray.getJSONObject(i);

                Movie m = new Movie();
                m.setTitle(loopObject.getString(MDB_TITLE));
                m.setPosterPath(loopObject.getString(MDB_POSTER_PATH));
                m.setOverview(loopObject.getString(MDB_OVERVIEW));
                m.setRating(loopObject.getString(MDB_RATING));
                m.setDate(loopObject.getString(MDB_DATE));
                m.setId(loopObject.getString(MDB_ID));

                moviesList.add(m);
            }

            return moviesList;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Check network connectivity and return boolean indicating state
     * Adapted from JJD's response at http://stackoverflow.com/questions/1560788/how-to-check-internet-access-on-android-inetaddress-never-times-out
     * @return boolean indicating connectivity
     */
    public boolean isOnline() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }
}
