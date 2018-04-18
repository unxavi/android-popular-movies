package com.icodehigh.popularmovies.features.movies.detail;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.icodehigh.popularmovies.R;
import com.icodehigh.popularmovies.model.Review;

import java.util.List;

/**
 * {@link ReviewAdapter} exposes a list of movies
 * from a {@link List<com.icodehigh.popularmovies.model.Review>} to a {@link RecyclerView}.
 */
public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {

    /* The context we use to utility methods, app resources and layout inflaters */
    private final Context context;

    /* List of movies to use with the adapter */
    private final List<Review> reviews;

    /**
     * Creates a TrailerAdapter.
     *
     * @param context Used to talk to the UI and app resources
     * @param reviews List of video reviews to show on the adapter
     */
    ReviewAdapter(Context context, List<Review> reviews) {
        this.context = context;
        this.reviews = reviews;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).
                inflate(R.layout.item_review, parent, false);
        view.setFocusable(true);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Review review = reviews.get(position);
        holder.reviewTv.setText(Html.fromHtml(review.getContent()));
        holder.authorTv.setText(review.getAuthor());
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }


    /**
     * A ViewHolder is a required part of the pattern for RecyclerViews.
     * this ViewHolder represents a movie view
     */
    class ViewHolder extends RecyclerView.ViewHolder {

        TextView reviewTv;
        TextView authorTv;

        ViewHolder(View itemView) {
            super(itemView);
            reviewTv = itemView.findViewById(R.id.review_tv);
            authorTv = itemView.findViewById(R.id.author_tv);
        }


    }
}
