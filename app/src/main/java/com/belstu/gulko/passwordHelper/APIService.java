package com.belstu.gulko.passwordHelper;

import com.belstu.gulko.passwordHelper.model.Base;
import com.belstu.gulko.passwordHelper.model.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface APIService {
    @POST("/ph/saveUser")
    Call<Void> saveUser(@Body User user);

    @POST("/BaseList/saveBase")
    Call<Void> saveBase(@Body Base base);
}
