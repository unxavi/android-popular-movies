package com.icodehigh.popularmovies.rest;


import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.icodehigh.popularmovies.BuildConfig;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Class to handle the Retrofit object and the service class
 * writing more methos createService could handle different authetication methods
 */
public class ServiceGenerator {

    /**
     * Base url of the movies database API
     */
    private static final String BASE_URL = "https://api.themoviedb.org/3/";

    /**
     * Handle dates from the API to convert to the correct {@linkplain java.util.Date} oject
     */
    @NonNull
    private static Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();

    /**
     * builder object for retrofit
     */
    @NonNull
    private static Retrofit.Builder builder =
            new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson));

    /**
     * retrofit object, if not in debug is the final retrofit object
     */
    @NonNull
    private static Retrofit retrofit = builder.build();

    /**
     * logging interceptor for debug the app when the build type is debug
     */
    @NonNull
    private static HttpLoggingInterceptor logging =
            new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY);

    /**
     * http client for the request, could add interceptors to add headers to the request
     */
    @NonNull
    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

    /**
     * Create Service class base on a retrofit service interface
     *
     * @param serviceClass example {@linkplain com.icodehigh.popularmovies.rest.service.ApiService}
     * @return serviceClass to call the API
     */
    public static <S> S createService(@NonNull Class<S> serviceClass) {
        return create(serviceClass);
    }

    /**
     * Create Service class base on a retrofit service interface, add debug information if the app
     * if build on debug mode
     *
     * @param serviceClass example {@linkplain com.icodehigh.popularmovies.rest.service.ApiService}
     * @return serviceClass to call the API
     */
    private static <S> S create(@NonNull Class<S> serviceClass) {
        if (BuildConfig.DEBUG && !httpClient.interceptors().contains(logging)) {
            httpClient.addInterceptor(logging);
            builder.client(httpClient.build());
            retrofit = builder.build();
        }

        return retrofit.create(serviceClass);
    }
}
