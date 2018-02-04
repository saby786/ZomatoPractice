package com.example.saby7.zomatopractise;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

/**
 * Created by saby7 on 03-02-2018.
 */

public interface InterfaceApi {
@GET("geocode?")
    Call<Zomato> getZomato(@Query("lat") String latitude ,@Query("lon") String longitude, @Header("user-key") String apiKey);
}
