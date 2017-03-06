package com.example.jinfei.retrofittest.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.concurrent.atomic.AtomicInteger;

public class DBHelper extends SQLiteOpenHelper {

    private static DBHelper dbHelper;
    private static SQLiteDatabase database;

    private static AtomicInteger mOpenCounter = new AtomicInteger();

    private static final String CREATE_DB = "create table Favourite(" +
            "id integer primary key," +
            "create_date text," +
            "nick_name text," +
            "image_path text" +
            ")";

    private DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version)  {
        super(context, name, factory, version);
    }

    static synchronized SQLiteDatabase getDB(Context context) {
        if(null == dbHelper) {
            dbHelper = new DBHelper(context, "Menu.db", null, 1);
        }
        if(mOpenCounter.incrementAndGet() == 1) {
            database = dbHelper.getWritableDatabase();
        }
        return database;
    }

    public static void closeDB() {
        if(mOpenCounter.decrementAndGet() == 0) {
            database.close();
            database = null;
        }
        if(mOpenCounter.intValue() < 0) {
            mOpenCounter = new AtomicInteger(0);
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_DB);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
