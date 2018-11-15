package com.belstu.gulko.passwordHelper;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Client {

    public static final String BaseURL = "http://172.20.10.11:8081";

    private static Retrofit getRetrofit() {
        return new Retrofit.Builder()
                .baseUrl(BaseURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static APIService getService() {
        return getRetrofit().create(APIService.class);
    }
}
