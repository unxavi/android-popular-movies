package com.icodehigh.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.icodehigh.popularmovies.data.FavoriteMovieContract.FavoriteMovieEntry;

public class MovieDbHelper extends SQLiteOpenHelper {

    /*
    * This is the name of our movies database
    */
    private static final String DATABASE_NAME = "movie.db";

    /*
    * If you change the database schema, you must increment the database version or the onUpgrade
    * method will not be called.
    */
    private static final int DATABASE_VERSION = 1;


    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
         /*
         * This String contain a simple SQL statement that will create a table that will
         * save the user favorite movies
         */
        final String SQL_CREATE_WEATHER_TABLE =

                "CREATE TABLE " + FavoriteMovieEntry.TABLE_NAME + " (" +

                /*
                 * FavoriteMovieEntry implements the interface, "BaseColumns", which does have a field
                 * named "_ID". We use that here to designate our table's primary key.
                 */
                        FavoriteMovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +

                        FavoriteMovieEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +

                        FavoriteMovieEntry.COLUMN_MOVIE_NAME + " TEXT NOT NULL, " +
                /*
                 * To ensure this table can only contain one movie entry per movie_id, we declare
                 * the COLUMN_MOVIE_ID column to be unique. We also specify "ON CONFLICT REPLACE". This tells
                 * SQLite that if we have a movie entry for a certain API ID and we attempt to
                 * insert another movie entry with that API ID, we replace the old movie entry.
                 */
                        " UNIQUE (" + FavoriteMovieEntry.COLUMN_MOVIE_ID + ") ON CONFLICT REPLACE);";

        /*
         * After we've spelled out our SQLite table creation statement above, we actually execute
         * that SQL with the execSQL method of our SQLite database object.
         */
        db.execSQL(SQL_CREATE_WEATHER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
