package com.example.jinfei.retrofittest.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.example.jinfei.retrofittest.entity.Cook;
import com.example.jinfei.retrofittest.entity.Menu;
import com.example.jinfei.retrofittest.entity.TngouResponse;
import com.example.jinfei.retrofittest.myInterface.Service;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

public class HttpMethods implements Cloneable {

    private static final String BASE_URL = "http://www.tngou.net/";

    private static final int DEFAULT_TIMEOUT = 30;

    private static volatile HttpMethods instance;

    private Service service;

    private HttpMethods(final Context context) {
        if(null != instance) {
            throw new RuntimeException("Cannot construct a singleton more than once");
        }
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
        httpClientBuilder.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        File cacheFile = new File(context.getCacheDir().getAbsolutePath(), "HttpCache");
        Cache cache = new Cache(cacheFile, 1024 * 1024 * 100);
        Interceptor interceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                if(!isNetworkConnected(context)) {
                    request = request.newBuilder()
                            .cacheControl(CacheControl.FORCE_CACHE)
                            .build();
                }
                Response response = chain.proceed(request);
                if(isNetworkConnected(context)) {
                    int maxAge = 60 * 60; // 有网络时 设置缓存超时时间1个小时
                    response.newBuilder()
                            .header("Cache-Control", "public, max-age=" + maxAge)
                            .removeHeader("Pragma")// 清除头信息，因为服务器如果不支持，会返回一些干扰信息，不清除下面无法生效
                            .build();
                } else {
                    int maxStale = 60 * 60 * 24 * 7 * 4;  // 无网络时，设置超时为4周
                    response.newBuilder()
                            .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                            .removeHeader("Pragma")
                            .build();

                }
                return response;
            }
        };
        OkHttpClient client = httpClientBuilder.cache(cache)
                .addInterceptor(interceptor)
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .baseUrl(BASE_URL)
                .build();
        service = retrofit.create(Service.class);
    }

    public static HttpMethods getInstance(Context context) {
        if(null == instance) {
            synchronized (HttpMethods.class) {
                instance = new HttpMethods(context.getApplicationContext());
            }
        }
        return instance;
    }

    public Subscription getList(Subscriber<TngouResponse<List<Cook>>> subscriber, final ProgressDialog mDialog, int id, int pages, int rows) {
        return service.getRxList(id, pages, rows)
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

    public Subscription getMenu(Subscriber<Menu> subscriber, final ProgressDialog mDialog, int id) {
        return service.getRxMenu(id).subscribeOn(Schedulers.io())
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

    public Subscription getDishes(Subscriber<TngouResponse<List<Cook>>> subscriber, final ProgressDialog mDialog, String name) {
        return service.getRxDishes(name).subscribeOn(Schedulers.io())
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

    private boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    @SuppressWarnings("all")
    @Override
    protected Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }
}
