package com.cookapps.popularmovies;

import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.cookapps.popularmovies.adapters.ReviewsAdapter;
import com.cookapps.popularmovies.adapters.TrailersAdapter;
import com.cookapps.popularmovies.databinding.MovieDetailBinding;
import com.cookapps.popularmovies.model.Movie;
import com.cookapps.popularmovies.model.MovieReview;
import com.cookapps.popularmovies.model.MovieTrailer;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a single Movie detail screen.
 * This fragment is either contained in a {@link MovieListActivity}
 * in two-pane mode (on tablets) or a {@link MovieDetailActivity}
 * on handsets.
 */
public class MovieDetailFragment extends Fragment {

    public static final String MOVIE_PARAM = "movie";

    private Movie mMovie;

    private TrailersAdapter mTrailersAdapter;
    private ListView mTrailersListView;

    private ReviewsAdapter mReviewsAdapter;
    private ListView mReviewsListView;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MovieDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(MOVIE_PARAM)) {
            mMovie = getArguments().getParcelable(MOVIE_PARAM);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

//        View v = inflater.inflate(R.layout.movie_detail, container, false);

        // TODO fix the data binding - why isn't the data coming out?
        MovieDetailBinding binding = DataBindingUtil.inflate(inflater, R.layout.movie_detail,
                container, false);

        View v = binding.getRoot();

        if(mMovie != null) {

//            mMovie.setRating(mMovie.getRating() + getString(R.string.out_of_10));

            binding.setMovie(mMovie);

            // Set up the imageView
            ImageView imageView = (ImageView) v.findViewById(R.id.imageView_detail);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(0, 0, 0, 0);

            URL url = StaticUtilityClasses.buildPosterURL(mMovie.getPosterPath(), "w185");

            if (url != null) {
                // Use picasso to fill the image View with the poster
                Picasso.with(getActivity()).load(url.toString()).into(imageView);
            }

            // Pre fill mTrailersListView with empty list of MovieTrailers
            mTrailersListView = binding.listViewTrailers;
            List<MovieTrailer> trailerList = new ArrayList<>();
            mTrailersAdapter = new TrailersAdapter(getActivity(), trailerList);
            mTrailersListView.setAdapter(mTrailersAdapter);

            // Pre fill mReviewsListView with empty list of MovieReviews
            mReviewsListView = binding.listViewReviews;
            List<MovieReview> reviewList = new ArrayList<>();
            mReviewsAdapter = new ReviewsAdapter(getActivity(), reviewList);
            mReviewsListView.setAdapter(mReviewsAdapter);


            // query the web api for trailers for the movie: /movie/id/videos
            QueryTrailersTask queryTrailersTask = new QueryTrailersTask();
            queryTrailersTask.execute(mMovie.getId());

            // query the web api for reviews for the movie: /movie/id/reviews
            QueryReviewsTask queryReviewsTask = new QueryReviewsTask();
            queryReviewsTask.execute(mMovie.getId());


            // retrieve the favorite flag from preferences, and if favorite, set the button background to indicate that it is selected.
            CheckBox favoritesCheckBox = binding.checkBoxFavorite;
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            final SharedPreferences.Editor editor = sharedPrefs.edit();

            boolean favorite = sharedPrefs.getBoolean(mMovie.getId(), false);
            favoritesCheckBox.setChecked(favorite);

            favoritesCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    // Respond to CheckBox Change
                    if (b) {
                        editor.putBoolean(mMovie.getId(), true);
                        editor.apply();
                    } else {
                        editor.remove(mMovie.getId());
                        editor.apply();
                    }
                }
            });
        }

        return v;
    }

    private class QueryTrailersTask extends AsyncTask<String, String, List<MovieTrailer>> {

        private final String LOG_TAG = QueryTrailersTask.class.getSimpleName();

        @Override
        protected List<MovieTrailer> doInBackground(String... args) {
            // doInBackground method creating following Sunshine Project as a template

            String moviesString = StaticUtilityClasses.queryMovieDatabase(args[0], "videos");

            try {
                return getTrailerDataFromJSON(moviesString);
            } catch (JSONException e) {
                Log.e(LOG_TAG, "Error caught trying to parse movieJSON string");
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(List<MovieTrailer> movieTrailerList) {
            if(movieTrailerList != null){
                mTrailersAdapter.clear();
                for(MovieTrailer m : movieTrailerList){
                    mTrailersAdapter.add(m);
                }
            } else {
                Toast.makeText(getActivity(), "Something went wrong.  Please check your internet connection and try again."
                        , Toast.LENGTH_LONG).show();
            }
            super.onPostExecute(movieTrailerList);
        }

    }

    private List<MovieTrailer> getTrailerDataFromJSON(String jsonString) throws JSONException{

        String results = "results";
        String MDB_NAME= "name";
        String MDB_KEY= "key";
        String MDB_SITE = "site";

        JSONObject moviesJSON = new JSONObject(jsonString);
        JSONArray moviesJsonArray = moviesJSON.getJSONArray(results);

        List<MovieTrailer> moviesList = new ArrayList<>();
        for(int i=0; i <moviesJsonArray.length(); i++){
            JSONObject loopObject = moviesJsonArray.getJSONObject(i);

            MovieTrailer m = new MovieTrailer();
            m.setName(loopObject.getString(MDB_NAME));
            m.setKey(loopObject.getString(MDB_KEY));
            m.setSite(loopObject.getString(MDB_SITE));
            moviesList.add(m);
        }

        return moviesList;
    }

    private class QueryReviewsTask extends AsyncTask<String, String, List<MovieReview>> {

        private final String LOG_TAG = QueryReviewsTask.class.getSimpleName();

        @Override
        protected List<MovieReview> doInBackground(String... args) {
            // doInBackground method creating following Sunshine Project as a template

            String reviewsString = StaticUtilityClasses.queryMovieDatabase(args[0], "reviews");

            try {
                return getReviewDataFromJSON(reviewsString);
            } catch (JSONException e) {
                Log.e(LOG_TAG, "Error caught trying to parse movieJSON string");
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(List<MovieReview> movieReviewList) {
            if(movieReviewList != null){
                mReviewsAdapter.clear();
                for(MovieReview m : movieReviewList){
                    mReviewsAdapter.add(m);
                }
            } else {
                Toast.makeText(getActivity(), "Something went wrong.  Please check your internet connection and try again."
                        , Toast.LENGTH_LONG).show();
            }
            super.onPostExecute(movieReviewList);
        }

    }

    private List<MovieReview> getReviewDataFromJSON(String jsonString) throws JSONException{

        String results = "results";
        String MDB_AUTHOR = "author";
        String MDB_CONTENT= "content";
        String MDB_URL = "url";

        JSONObject moviesJSON = new JSONObject(jsonString);
        JSONArray moviesJsonArray = moviesJSON.getJSONArray(results);

        List<MovieReview> moviesList = new ArrayList<>();
        for(int i=0; i <moviesJsonArray.length(); i++){
            JSONObject loopObject = moviesJsonArray.getJSONObject(i);

            MovieReview m = new MovieReview();
            m.setAuthor(loopObject.getString(MDB_AUTHOR));
            m.setContent(loopObject.getString(MDB_CONTENT));
            m.setUrl(loopObject.getString(MDB_URL));
            moviesList.add(m);
        }

        return moviesList;
    }

}
