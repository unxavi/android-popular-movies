package com.icodehigh.popularmovies.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Defines table and column names for the favorite moviees database.
 */
public class FavoriteMovieContract {

    /*
     * The "Content authority" is the name for the entire content provider
     */
    public static final String CONTENT_AUTHORITY = "com.icodehigh.popularmovies";

    /*
     * Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
     * the content provider for movies.
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /*
     * Possible paths that can be appended to BASE_CONTENT_URI to form valid URI's that the app
     * can handle. For instance,
     *
     *     content://com.icodehigh.popularmovies/movies/
     *     [           BASE_CONTENT_URI         ][ PATH_WEATHER ]
     *
     * is a valid path for looking at favorite movies data.
     *
     *      content://com.icodehigh.popularmovies/something/
     *
     * will fail, as the ContentProvider hasn't been given any information on what to do with
     * "something".
     */
    public static final String PATH_FAVORITE_MOVIES = "favoritemovies";

    /**
     * Private constructor to avoid to make an instance of this class by accident
     */
    private FavoriteMovieContract() {
    }

    public static final class FavoriteMovieEntry implements BaseColumns {

        /* The base CONTENT_URI used to query the fav movies table from the content provider */
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_FAVORITE_MOVIES)
                .build();

        /* Used internally as the name of our favorite movies table. */
        public static final String TABLE_NAME = "favorite_movie";

        /* Movie ID as returned by API, used to identify within the API*/
        public static final String COLUMN_MOVIE_ID = "movie_id";

        /* Movie title as returned by API in English*/
        public static final String COLUMN_MOVIE_NAME = "movie_name";

        /* Movie poster path */
        public static final String COLUMN_POSTER_PATH = "poster_path";

        /* Movie release date */
        public static final String COLUMN_RELEASE_DATE = "release_date";

        /* Movie vote average */
        public static final String COLUMN_VOTE_AVERAGE = "vote_average";

        /* Movie overview */
        public static final String COLUMN_OVERVIEW = "overview";


        /**
         * Builds a URI that adds the id of the movie to the end of the fav movies content URI path.
         * This is used to query details about a single fav movie entry by movie id.

         *
         * @param movieId Normalized date in milliseconds
         * @return Uri to query details about a single weather entry
         */
        public static Uri buildFavoriteMovieUriWithId(int movieId) {
            return CONTENT_URI.buildUpon()
                    .appendPath(Integer.toString(movieId))
                    .build();
        }

    }
}
