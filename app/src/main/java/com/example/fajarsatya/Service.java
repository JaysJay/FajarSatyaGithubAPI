package com.example.fajarsatya;

import com.example.fajarsatya.object.Response;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;

public interface Service {

    //?q=pika+in:login
    // yang ini error jadi linknya berubah + dan :
    //@GET("users")
    //Call<Response> getData(@QueryMap Map<String, String> params);

    // encoded true berarti nambah beberapa huruf tertentu
    @GET("users")
    Call<Response> getData(@Query(value = "q", encoded = true) String name,
                           @Query("page") int page);

    // ini berjalan dengan lancar
    //@GET
    //Call<Response> getData(@Url String url);

    //?q=pika+in:login
    //@GET("users?q={name}+in:login")
    //Call<Response> getData(@Path("name") String name);
}
