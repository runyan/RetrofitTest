package com.example.jinfei.retrofittest.myInterface;

import com.example.jinfei.retrofittest.entity.Menu;
import com.example.jinfei.retrofittest.entity.Tngou;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface Service {

    @Headers("Cache-Control: public, max-age=3600")
    @GET("/api/{category}/list")
    Call<Tngou> getList(@Path("category") String path, @Query("id") int id, @Query("page") int page, @Query("rows") int rows);
    @Headers("Cache-Control: public, max-age=3600")
    @GET("/api/cook/show/")
    Call<Menu> getMenu(@Query("id") int id);
    @Headers("Cache-Control: public, max-age=3600")
    @GET("/api/cook/name/")
    Call<Tngou> getDishes(@Query("name") String name);
}
