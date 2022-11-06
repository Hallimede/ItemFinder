package com.example.t.network;


import org.jetbrains.annotations.NotNull;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public final class ServiceCreator {

    private static final String BASE_URL = "http://124.223.160.213:8080/";
//    private static final String BASE_URL = "http://127.0.0.1:8000/";
    private static final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    @NotNull
    public static final ServiceCreator INSTANCE;

    public final Object create(@NotNull Class serviceClass) {
        return retrofit.create(serviceClass);
    }

    private ServiceCreator() {
    }

    static {
        INSTANCE = new ServiceCreator();
    }

}