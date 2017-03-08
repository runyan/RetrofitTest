package com.example.jinfei.retrofittest.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.widget.ImageView;

import com.example.jinfei.retrofittest.R;
import com.example.jinfei.retrofittest.myInterface.NetworkError;
import com.example.jinfei.retrofittest.myInterface.NetworkInterface;
import com.example.jinfei.retrofittest.myInterface.Service;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;

public class Util {

    public static Service getService(Context context) {
        File cacheFile = new File(context.getCacheDir().getAbsolutePath(), "HttpCache");
        Cache cache = new Cache(cacheFile, 1024 * 1024 * 100);
        OkHttpClient client = new OkHttpClient.Builder().cache(cache).connectTimeout(30, TimeUnit.SECONDS).build();
        Retrofit retrofit = new Retrofit.Builder().baseUrl("http://www.tngou.net").client(client)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create()).build();
       return retrofit.create(Service.class);
    }

    public static void setImage(Context context, String imgSrc, ImageView imageView) {
        Picasso.with(context).load("http://tnfs.tngou.net/img" + imgSrc).placeholder(context.getDrawable(R.drawable.loading)).error(R.mipmap.ic_launcher).into(imageView);
    }

    public static void showErrorDialog(Context context, final NetworkInterface network, final NetworkError networkError) {
        AlertDialog dialog = new AlertDialog.Builder(context).create();
        dialog.setTitle(context.getResources().getString(R.string.error));
        dialog.setMessage(context.getResources().getString(R.string.network_error));
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, context.getResources().getString(R.string.reconnect), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                network.call();
            }
        });
        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, context.getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                networkError.onError();
            }
        });
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
    }

    public static void redirect(Context context, Class<? extends Activity> targetClass, Map<String, Object> params) {
        Intent intent = new Intent(context, targetClass);
        if(null != params && !params.isEmpty()) {
            for(Map.Entry<String, Object> entry : params.entrySet()) {
                Object param = entry.getValue();
                if(param instanceof Serializable) {
                    intent.putExtra(entry.getKey(), (Serializable) param);
                }
            }
        }
        context.startActivity(intent);
    }

    public static ProgressDialog getLoadingDialog(Context context) {
        ProgressDialog mDialog = new ProgressDialog(context);
        mDialog.setMessage(context.getResources().getString(R.string.loading));
        mDialog.setCancelable(false);
        mDialog.setCanceledOnTouchOutside(false);
        return mDialog;
    }

}
