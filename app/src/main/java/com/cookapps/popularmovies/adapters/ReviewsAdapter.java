package com.cookapps.popularmovies.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.cookapps.popularmovies.R;
import com.cookapps.popularmovies.model.MovieReview;

import java.util.List;

/**
 * Created by danielcook on 8/30/16.
 * To display movie reviews
 */
public class ReviewsAdapter extends ArrayAdapter<MovieReview> {

    private final String LOG_TAG = TrailersAdapter.class.getSimpleName();
    private Context mContext;

    public ReviewsAdapter(Context context, List<MovieReview> reviewList) {
        super(context, 0);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Get the MovieTrailer at this position
        MovieReview review = getItem(position);

        // Check if the convertView is being reused, if not inflate it
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_reviews, parent, false);
        }

        TextView reviewAuthor = (TextView) convertView.findViewById(R.id.textView_review_author);
        reviewAuthor.setText(review.getAuthor());

        TextView reviewContent = (TextView) convertView.findViewById(R.id.textView_review_content);
        reviewContent.setText(review.getContent());

        return convertView;
    }
}
