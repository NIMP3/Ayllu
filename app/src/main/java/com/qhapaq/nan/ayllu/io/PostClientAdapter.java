package com.qhapaq.nan.ayllu.io;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PostClientAdapter {

    private static AylluApiService API_SERVICE;
    public static Retrofit getNewApiService(String url){


        // set your desired log level
        return  new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
}
