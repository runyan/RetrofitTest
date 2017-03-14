package com.example.jinfei.retrofittest.myInterface;

import com.example.jinfei.retrofittest.entity.Cook;
import com.example.jinfei.retrofittest.entity.Menu;
import com.example.jinfei.retrofittest.entity.TngouResponse;

import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;
import rx.Observable;

public interface Service {

    @Headers("Cache-Control: public, max-age=3600")
    @GET("api/cook/list")
    Observable<TngouResponse<List<Cook>>> getRxList(@Query("id") int id, @Query("page") int page, @Query("rows") int rows);
    @Headers("Cache-Control: public, max-age=3600")
    @GET("api/cook/show/")
    Observable<Menu> getRxMenu(@Query("id") int id);
    @Headers("Cache-Control: public, max-age=3600")
    @GET("api/cook/name/")
    Observable<TngouResponse<List<Cook>>> getRxDishes(@Query("name") String name);
}
