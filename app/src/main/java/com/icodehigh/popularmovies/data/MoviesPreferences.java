/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.icodehigh.popularmovies.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public final class MoviesPreferences {

    // Declare the constants for movies list types
    public static final int POPULAR_MOVIES_LIST = 0;
    public static final int TOP_RATED_MOVIES_LIST = 1;
    public static final int FAVORITES_MOVIES_LIST = 2;

    /*
     * key to store in share preferences what movie list to load
     */
    private static final String MOVIE_LIST_PREFERENCE = "movie_list_preference";

    // Define the list of accepted constants and declare the MoviesListMode annotation
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({POPULAR_MOVIES_LIST, TOP_RATED_MOVIES_LIST, FAVORITES_MOVIES_LIST})
    public @interface MoviesListMode {
    }

    /**
     * Helper to get the default shared preferences
     *
     * @param context used to access SharedPreferences
     * @return SharedPreferences default
     */
    private static SharedPreferences getSharedPreferences(@NonNull Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    /**
     * Return the user preference for the movies list
     *
     * @param context used to access SharedPreferences
     * @return an int identifying the movie type according to {@link MoviesListMode}
     */
    @MoviesListMode
    public static int getMoviesListPreference(Context context) {
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        return sharedPreferences.getInt(MOVIE_LIST_PREFERENCE, POPULAR_MOVIES_LIST);
    }

    /**
     * Sets on sharedPreferences the the movie list type that the user wants to save
     *
     * @param context        used to access SharedPreferences
     * @param moviesListMode identifying the movie type according to {@link MoviesListMode}
     */
    public static void setMoviesListPreference(Context context, @MoviesListMode int moviesListMode) {
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(MOVIE_LIST_PREFERENCE, moviesListMode);
        editor.apply();
    }
}