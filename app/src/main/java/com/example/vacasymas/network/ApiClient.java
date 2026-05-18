package com.example.vacasymas.network;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    private static final String BASE_URL = "https://genaxinpowoerttqgaxi.supabase.co/rest/v1/";
    private static final String API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImdlbmF4aW5wb3dvZXJ0dHFnYXhpIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzQ4MDk1MDksImV4cCI6MjA5MDM4NTUwOX0.rbMWzykTO23VE2cBTgV6O0k0y5wYvOASxU2LBvcYgn0";

    private static Retrofit retrofit;

    public static Retrofit get() {
        if (retrofit == null) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            Interceptor headersInterceptor = chain -> {
                Request request = chain.request().newBuilder()
                        .addHeader("apikey", API_KEY)
                        .addHeader("Authorization", "Bearer " + API_KEY)
                        .addHeader("Accept", "application/json")
                        .addHeader("Content-Type", "application/json")
                        .addHeader("Accept-Profile", "public")
                        .addHeader("Content-Profile", "public")
                        .build();
                return chain.proceed(request);
            };

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(headersInterceptor)
                    .addInterceptor(logging)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
