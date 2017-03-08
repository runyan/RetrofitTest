package com.example.jinfei.retrofittest.myInterface;

import com.example.jinfei.retrofittest.entity.Menu;
import com.example.jinfei.retrofittest.entity.Tngou;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import rx.Observable;

public interface Service {

    @Headers("Cache-Control: public, max-age=3600")
    @GET("/api/{category}/list")
    Call<Tngou> getList(@Path("category") String path, @QueryMap Map<String, Integer> options);
    @Headers("Cache-Control: public, max-age=3600")
    @GET("/api/{category}/list")
    Observable<Tngou> getRxList(@Path("category") String path, @QueryMap Map<String, Integer> options);
    @Headers("Cache-Control: public, max-age=3600")
    @GET("/api/cook/show/")
    Call<Menu> getMenu(@Query("id") int id);
    @Headers("Cache-Control: public, max-age=3600")
    @GET("/api/cook/show/")
    Observable<Menu> getRxMenu(@Query("id") int id);
    @Headers("Cache-Control: public, max-age=3600")
    @GET("/api/cook/name/")
    Call<Tngou> getDishes(@Query("name") String name);
    @Headers("Cache-Control: public, max-age=3600")
    @GET("/api/cook/name/")
    Observable<Tngou> getRxDishes(@Query("name") String name);
}
