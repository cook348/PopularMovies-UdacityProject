package com.cookapps.popularmovies.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.cookapps.popularmovies.R;
import com.cookapps.popularmovies.StaticUtilityClasses;
import com.cookapps.popularmovies.model.MovieTrailer;

import java.util.List;

/**
 * Created by danielcook on 8/30/16.
 * To display movie trailers
 */
public class TrailersAdapter extends ArrayAdapter<MovieTrailer> {

    private final String LOG_TAG = TrailersAdapter.class.getSimpleName();
    private Context mContext;

    public TrailersAdapter(Context context, List<MovieTrailer> trailerList) {
        super(context, 0);
        mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Get the MovieTrailer at this position
        MovieTrailer trailer = getItem(position);

        // Check if the convertView is being reused, if not inflate it
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_trailer, parent, false);
        }

        TextView nameTextView = (TextView) convertView.findViewById(R.id.textView_trailerName);
        nameTextView.setText(trailer.getName());

        ImageButton playButton = (ImageButton) convertView.findViewById(R.id.imageButtonPlay);
        playButton.setTag(position);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int p = (Integer) view.getTag();
                MovieTrailer t = getItem(p);

                //  open youtube video
                // Adapted from StackOverflow: http://stackoverflow.com/questions/4654878/how-to-play-youtube-video-in-my-android-application
                mContext.startActivity(new Intent(Intent.ACTION_VIEW, StaticUtilityClasses.buildYoutubeURI(t.getKey())));
            }
        });

        return convertView;
    }
}
