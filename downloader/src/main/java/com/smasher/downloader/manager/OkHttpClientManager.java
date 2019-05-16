package com.smasher.downloader.manager;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

public class OkHttpClientManager {


    public static final int SOCKET_CONNECT_TIMEOUT = 20 * 1000;
    public static final int SOCKET_READ_TIME_TIMEOUT = 20 * 1000;


    private static OkHttpClientManager instance;

    private OkHttpClientManager() {
    }

    public static OkHttpClientManager getInstance() {
        if (instance == null) {
            instance = new OkHttpClientManager();
        }
        return instance;
    }


    public OkHttpClient getClient() {
        return new OkHttpClient.Builder()
                .connectTimeout(SOCKET_CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)
                .readTimeout(SOCKET_CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)
                .build();
    }

}