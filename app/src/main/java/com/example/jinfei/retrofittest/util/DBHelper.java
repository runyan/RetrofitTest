package com.example.jinfei.retrofittest.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

class DBHelper extends SQLiteOpenHelper {

    private static final String CREATE_DB = "create table Favourite(" +
            "id integer primary key," +
            "create_date text," +
            "nick_name text," +
            "image_path text" +
            ")";

    DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version)  {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_DB);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
