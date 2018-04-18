package com.icodehigh.popularmovies.features.movies.detail;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.icodehigh.popularmovies.R;
import com.icodehigh.popularmovies.model.Video;

import java.util.List;

/**
 * {@link TrailerAdapter} exposes a list of movies
 * from a {@link List<Video>} to a {@link RecyclerView}.
 */
public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.ViewHolder> {

    /* The context we use to utility methods, app resources and layout inflaters */
    private final Context context;

    /* List of movies to use with the adapter */
    private final List<Video> trailers;

    /*
     * Below, we've defined an interface to handle clicks on items within this Adapter. In the
     * constructor of our MoviesAdapter, we receive an instance of a class that has implemented
     * said interface. We store that instance in this variable to call the onClick method whenever
     * an item is clicked in the list.
     */
    final private TrailersAdapterOnClickHandler listener;

    /**
     * The interface that receives onTrailerClick messages and handle it.
     */
    public interface TrailersAdapterOnClickHandler {
        void onTrailerClick(Video video);
    }

    /**
     * Creates a TrailerAdapter.
     *
     * @param context  Used to talk to the UI and app resources
     * @param trailers List of video trailers to show on the adapter to be able to click and watch
     * @param listener The on-click handler for this adapter. This single handler is called
     *                 when an item is clicked.
     */
    TrailerAdapter(Context context, List<Video> trailers, TrailersAdapterOnClickHandler listener) {
        this.context = context;
        this.trailers = trailers;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).
                inflate(R.layout.item_trailer, parent, false);
        view.setFocusable(true);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int trailerNumber = position + 1;
        String text = context.getString(R.string.trailer) + " " + trailerNumber;
        holder.trailerTV.setText(text);
    }

    @Override
    public int getItemCount() {
        return trailers.size();
    }


    /**
     * A ViewHolder is a required part of the pattern for RecyclerViews.
     * this ViewHolder represents a movie view
     */
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView trailerTV;

        ViewHolder(View itemView) {
            super(itemView);
            trailerTV = itemView.findViewById(R.id.trailer_tv);
            itemView.setOnClickListener(this);
        }


        /**
         * This gets called by the child views during a click.
         *
         * @param view the View that was clicked
         */
        @Override
        public void onClick(View view) {
            Video video = trailers.get(getAdapterPosition());
            listener.onTrailerClick(video);
        }
    }
}
