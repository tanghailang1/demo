package com.example.demo.utils;

import okhttp3.OkHttpClient;

import java.util.concurrent.TimeUnit;

public class HttpPool {
    private static OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build();

    private HttpPool() {

    }

    public static OkHttpClient getInstance() {
        return okHttpClient;
    }
}
