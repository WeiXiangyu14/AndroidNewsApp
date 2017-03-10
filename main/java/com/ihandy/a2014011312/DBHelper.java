package com.ihandy.a2014011312;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by weixy on 2016/9/4.
 */
public class DBHelper extends SQLiteOpenHelper
{
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "weixy_news.db";
    private static DBHelper db;

    public static SQLiteDatabase getInstance(Context context)
    {
        if(db == null)
            db = new DBHelper(context);
        return db.getReadableDatabase();
    }

    public DBHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        try
        {
            db.execSQL("CREATE TABLE IF NOT EXISTS news(_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "category TEXT, news_title TEXT, news_origin TEXT, news_content_url TEXT, Image_url TEXT, news_id INTEGER)");

        }
        catch (Exception e)
        {
            Log.d("nani create database","fail");
            e.printStackTrace();
        }
    }



    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {

    }
}
