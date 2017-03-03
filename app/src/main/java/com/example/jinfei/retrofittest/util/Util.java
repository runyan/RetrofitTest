package com.example.jinfei.retrofittest.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.ImageView;

import com.example.jinfei.retrofittest.entity.Favourite;
import com.example.jinfei.retrofittest.myInterface.NetworkError;
import com.example.jinfei.retrofittest.myInterface.NetworkInterface;
import com.example.jinfei.retrofittest.R;
import com.example.jinfei.retrofittest.myInterface.Service;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
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
        Picasso.with(context).load("http://tnfs.tngou.net/img" + imgSrc).placeholder(context.getDrawable(R.drawable.loading)).into(imageView);
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

    public static List<Favourite> getFavouriteList(Context context) {
        List<Favourite> favouriteList = new ArrayList<>();
        Favourite favourite;
        SQLiteDatabase db = new DBHelper(context, "Menu.db", null, 1).getWritableDatabase();
        Cursor cursor = db.query("Favourite", null, null, null, null, null, null);
        if(cursor.moveToFirst()) {
            do {
                favourite = new Favourite();
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                String nickName = cursor.getString(cursor.getColumnIndex("nick_name"));
                String createDate = cursor.getString(cursor.getColumnIndex("create_date"));
                String imagePath = cursor.getString(cursor.getColumnIndex("image_path"));
                favourite.setId(id);
                favourite.setNickName(nickName);
                favourite.setCreateDate(createDate);
                favourite.setImagePath(imagePath);
                favouriteList.add(favourite);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return favouriteList;
    }

    public static boolean exist(Context context, int id) {
        SQLiteDatabase db = new DBHelper(context, "Menu.db", null, 1).getWritableDatabase();
        Cursor cursor = db.rawQuery("Select * from Favourite where id = ?", new String[]{String.valueOf(id)});
        boolean result = cursor.moveToNext();
        cursor.close();
        return result;
    }

    public static boolean delete(Context context, int id) {
        SQLiteDatabase db = new DBHelper(context, "Menu.db", null, 1).getWritableDatabase();
        int rowAffected = db.delete("Favourite", "id=?", new String[]{String.valueOf(id)});
        return rowAffected > 0;
    }

    public static boolean save(Context context, int id, String nickName, String imgPath) {
        SQLiteDatabase db = new DBHelper(context, "Menu.db", null, 1).getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("id", id);
        values.put("create_date", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA).format(new Date()));
        values.put("nick_name", nickName);
        values.put("image_path", imgPath);
        long rowAffected = db.insert("Favourite", null, values);
        return rowAffected > 0;
    }

}
