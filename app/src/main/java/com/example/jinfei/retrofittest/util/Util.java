package com.example.jinfei.retrofittest.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.jinfei.retrofittest.myInterface.NetworkError;
import com.example.jinfei.retrofittest.myInterface.NetworkInterface;
import com.example.jinfei.retrofittest.R;
import com.example.jinfei.retrofittest.myInterface.Service;

import java.io.File;
import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Util {

    public static Service getService(Context context) {
        File cacheFile = new File(context.getCacheDir().getAbsolutePath(), "HttpCache");
        Cache cache = new Cache(cacheFile, 1024 * 1024 * 100);
        OkHttpClient client = new OkHttpClient.Builder().cache(cache).connectTimeout(1, TimeUnit.MINUTES).build();
        Retrofit retrofit = new Retrofit.Builder().baseUrl("http://www.tngou.net").client(client)
                .addConverterFactory(GsonConverterFactory.create()).build();
       return retrofit.create(Service.class);
    }

    public static void setImage(Context context, String imgSrc, ImageView imageView) {
        Glide.with(context).load("http://tnfs.tngou.net/img" + imgSrc).placeholder(context.getDrawable(R.drawable.loading)).into(imageView);
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

}
