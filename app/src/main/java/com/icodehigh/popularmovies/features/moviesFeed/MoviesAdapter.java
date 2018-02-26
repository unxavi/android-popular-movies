package com.icodehigh.popularmovies.features.moviesFeed;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.icodehigh.popularmovies.R;
import com.icodehigh.popularmovies.model.Movie;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * {@link MoviesAdapter} exposes a list of movies
 * from a {@link List<Movie>} to a {@link android.support.v7.widget.RecyclerView}.
 */
public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.ViewHolder> {

    /* The context we use to utility methods, app resources and layout inflaters */
    private final Context context;

    /* List of movies to use with the adapter */
    private final List<Movie> movies;

    /*
    * Below, we've defined an interface to handle clicks on items within this Adapter. In the
    * constructor of our MoviesAdapter, we receive an instance of a class that has implemented
    * said interface. We store that instance in this variable to call the onClick method whenever
    * an item is clicked in the list.
    */
    final private MoviesAdapterOnClickHandler listener;

    /**
     * The interface that receives onMovieClick messages and handle it.
     */
    public interface MoviesAdapterOnClickHandler {
        void onMovieClick(int id);
    }

    /**
     * Creates a MoviesAdapter.
     *
     * @param context  Used to talk to the UI and app resources
     * @param movies   List of movies to show on the adapter
     * @param listener The on-click handler for this adapter. This single handler is called
     *                 when an item is clicked.
     */
    MoviesAdapter(Context context, List<Movie> movies, MoviesAdapterOnClickHandler listener) {
        this.context = context;
        this.movies = movies;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).
                inflate(R.layout.item_movie, parent, false);
        view.setFocusable(true);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Movie movie = movies.get(position);
        String completePosterPath = movie.getCompletePosterPath();
        Picasso.with(context).load(completePosterPath).into(holder.moviesIv);
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    /**
     * Add more movies to show on the recycler view and refresh the list
     *
     * @param newMovies to be added to the recyclerView
     */
    void addMovies(List<Movie> newMovies) {
        int lastOldPosition = this.movies.size();
        this.movies.addAll(newMovies);
        int lastNewPosition = this.movies.size();
        this.notifyItemRangeInserted(lastOldPosition, lastNewPosition);
    }

    /**
     * A ViewHolder is a required part of the pattern for RecyclerViews.
     * this ViewHolder represents a movie view
     */
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView moviesIv;

        ViewHolder(View itemView) {
            super(itemView);
            moviesIv = itemView.findViewById(R.id.movies_iv);
            itemView.setOnClickListener(this);
        }


        /**
         * This gets called by the child views during a click. We fetch the id of the movie
         * that has been selected, and then call the onMovieClick handler registered with this a
         * dapter, passing that id
         *
         * @param view the View that was clicked
         */
        @Override
        public void onClick(View view) {
            Movie movie = movies.get(getAdapterPosition());
            listener.onMovieClick(movie.getId());
        }
    }
}
