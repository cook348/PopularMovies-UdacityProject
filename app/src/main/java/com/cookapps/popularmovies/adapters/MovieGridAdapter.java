package com.cookapps.popularmovies.adapters;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.cookapps.popularmovies.StaticUtilityClasses;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.List;

/**
 * Created by danielcook on 8/12/16.
 * Created to fill the GridView
 */
public class MovieGridAdapter extends ArrayAdapter<com.cookapps.popularmovies.model.Movie> {

    private final String LOG_TAG = MovieGridAdapter.class.getSimpleName();
    private Context mContext;
    private int mScreenWidth;

    public MovieGridAdapter(Context context, List<com.cookapps.popularmovies.model.Movie> movieList) {
        super(context, 0);
        mContext = context;

        DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
        mScreenWidth = displayMetrics.widthPixels;

    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        com.cookapps.popularmovies.model.Movie movie = getItem(position);

        ImageView imageView;
        if (convertView == null) {
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            imageView.setAdjustViewBounds(true);
//            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(0, 0, 0, 0);
        } else {
            imageView = (ImageView) convertView;
        }

        URL posterURL = StaticUtilityClasses.buildPosterURL(movie.getPosterPath(), "w185");

        if(posterURL != null){
            // Use picasso to fill the image View with the poster
            Picasso.with(getContext()).load(posterURL.toString()).into(imageView);
        }

        return imageView;
    }
}
