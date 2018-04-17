package com.icodehigh.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class MoviesContentProvider extends ContentProvider {

    /*
     * These constant will be used to match URIs with the data they are looking for. We will take
     * advantage of the UriMatcher class to make that matching MUCH easier than doing something
     * ourselves, such as using regular expressions.
     */
    public static final int CODE_FAVORITE_MOVIES = 100;
    public static final int CODE_FAVORITE_MOVIES_WITH_ID = 101;

    /*
     * The URI Matcher used by this content provider.
     */
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private MovieDbHelper movieDbHelper;

    /**
     * Creates the UriMatcher that will match each URI to the CODE_FAVORITE_MOVIES and
     * CODE_FAVORITE_MOVIES_WITH_ID constants defined above.
     *
     * @return A UriMatcher that correctly matches the constants for CODE_FAVORITE_MOVIES
     * and CODE_FAVORITE_MOVIES_WITH_ID
     */
    public static UriMatcher buildUriMatcher() {

        /*
         * All paths added to the UriMatcher have a corresponding code to return when a match is
         * found. The code passed into the constructor of UriMatcher here represents the code to
         * return for the root URI. It's common to use NO_MATCH as the code for this case.
         */
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = FavoriteMovieContract.CONTENT_AUTHORITY;

        /* This URI is content://com.icodehigh.popularmovies/favoritemovies/ */
        matcher.addURI(authority, FavoriteMovieContract.PATH_FAVORITE_MOVIES, CODE_FAVORITE_MOVIES);

        /*
         * This URI would look something like content://com.icodehigh.popularmovies/favoritemovies/214172
         */
        matcher.addURI(authority, FavoriteMovieContract.PATH_FAVORITE_MOVIES + "/#", CODE_FAVORITE_MOVIES_WITH_ID);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        movieDbHelper = new MovieDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Cursor cursor;

        final SQLiteDatabase db = movieDbHelper.getReadableDatabase();
        int match = sUriMatcher.match(uri);

        switch (match) {
            case CODE_FAVORITE_MOVIES:
                cursor = db.query(
                        FavoriteMovieContract.FavoriteMovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);

                break;
            case CODE_FAVORITE_MOVIES_WITH_ID:
                /*
                 * In order to determine the id associated with this URI, we look at the last
                 * path segment. In the comment above, the last path segment is 214172 and
                 * represents the id of the movie to select
                 */
                String normalizedIdString = uri.getLastPathSegment();

                /*
                 * The query method accepts a string array of arguments, as there may be more
                 * than one "?" in the selection statement. Even though in our case, we only have
                 * one "?", we have to create a string array that only contains one element
                 * because this method signature accepts a string array.
                 */
                String[] selectionArguments = new String[]{normalizedIdString};
                cursor = db.query(
                        /* Table we are going to query */
                        FavoriteMovieContract.FavoriteMovieEntry.TABLE_NAME,
                        projection,
                        FavoriteMovieContract.FavoriteMovieEntry.COLUMN_MOVIE_ID + " = ? ",
                        selectionArguments,
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknow uri: " + uri);
        }
        if (getContext() != null) {
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
        }

        return cursor;
    }

    /**
     * We aren't going to do anything with this method. However, we are required to
     * override it.
     *
     * @param uri the URI to query.
     * @return nothing in Sunshine, but normally a MIME type string, or null if there is no type.
     */
    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        throw new RuntimeException("Not implemented");
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final SQLiteDatabase db = movieDbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);

        Uri returnedUri;
        switch (match) {
            case CODE_FAVORITE_MOVIES:
                long id = db.insert(FavoriteMovieContract.FavoriteMovieEntry.TABLE_NAME, null, values);
                if (id > 0) {
                    returnedUri = ContentUris.withAppendedId(FavoriteMovieContract.FavoriteMovieEntry.CONTENT_URI, id);
                } else {
                    throw new SQLException("Failed to insert row into " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (getContext() != null) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return returnedUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = movieDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);

        /* Users of the delete method will expect the number of rows deleted to be returned. */
        int numRowsDeleted;

        switch (match) {
            case CODE_FAVORITE_MOVIES:
                numRowsDeleted = db.delete(
                        FavoriteMovieContract.FavoriteMovieEntry.TABLE_NAME,
                        selection,
                        selectionArgs);

                break;
            case CODE_FAVORITE_MOVIES_WITH_ID:
                // Get the task ID from the URI path
                String normalizedIdString = uri.getLastPathSegment();
                // Use selections/selectionArgs to filter for this ID
                numRowsDeleted = db.delete(
                        FavoriteMovieContract.FavoriteMovieEntry.TABLE_NAME,
                        FavoriteMovieContract.FavoriteMovieEntry.COLUMN_MOVIE_ID + " = ? ",
                        new String[]{normalizedIdString});
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (getContext() != null && numRowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }


        return 0;
    }

    /**
     * We aren't going to do anything with this method. However, we are required to
     * override it.
     *
     * @param uri       The URI to query. This can potentially have a record ID if this
     *                  is an update request for a specific record.
     * @param values    A set of column_name/value pairs to update in the database.
     *                  This must not be {@code null}.
     * @param selection An optional filter to match rows to update.
     * @return the number of rows affected.
     */
    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        throw new RuntimeException("Not implemented");
    }
}
