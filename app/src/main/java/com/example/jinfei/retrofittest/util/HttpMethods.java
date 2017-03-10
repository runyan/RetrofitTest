package com.example.jinfei.retrofittest.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

import com.example.jinfei.retrofittest.entity.Cook;
import com.example.jinfei.retrofittest.entity.Menu;
import com.example.jinfei.retrofittest.entity.TngouResponse;
import com.example.jinfei.retrofittest.myInterface.Service;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

public class HttpMethods {

    private static final String BASE_URL = "http://www.tngou.net/";

    private static final int DEFAULT_TIMEOUT = 5;

    private static HttpMethods instance;

    private Service service;

    private HttpMethods(Context context) {
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
        httpClientBuilder.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        File cacheFile = new File(context.getCacheDir().getAbsolutePath(), "HttpCache");
        Log.e("test", context.getCacheDir().getAbsolutePath());
        Cache cache = new Cache(cacheFile, 1024 * 1024 * 100);
        OkHttpClient client = httpClientBuilder.cache(cache).build();
        Retrofit retrofit = new Retrofit.Builder()
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .baseUrl(BASE_URL)
                .build();
        service = retrofit.create(Service.class);
    }

    public static synchronized HttpMethods getInstance(Context context) {
        if(null == instance) {
            instance = new HttpMethods(context.getApplicationContext());
        }
        return instance;
    }

    public void getList(Subscriber<TngouResponse<List<Cook>>> subscriber, final ProgressDialog mDialog, String path, Map<String, Integer> options) {
        service.getRxList(path, options)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        mDialog.show();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

    public void getMenu(Subscriber<Menu> subscriber, final ProgressDialog mDialog, int id) {
        service.getRxMenu(id).subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        mDialog.show();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

    public void getDishes(Subscriber<TngouResponse<List<Cook>>> subscriber, final ProgressDialog mDialog, String name) {
        service.getRxDishes(name).subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        mDialog.show();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

}
