package com.ihandy.a2014011312;

/**
 * Created by weixy on 2016/9/2.
 */
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Iterator;

public class ViewPagerAdapter extends FragmentStatePagerAdapter
{

    public ArrayList<String> categoryList = new ArrayList<>();
    public ArrayList<String> categoryKeyList = new ArrayList<>();
    private Handler handler = new Handler();

    private SQLiteDatabase db;

    public ViewPagerAdapter(FragmentManager fm,Context mContext)
    {
        super(fm);

        db = DBHelper.getInstance(mContext);
        boolean watchedIsEmpty = false;
        boolean unwatchedIsEmpty = false;

        Cursor c = db.query ("watched",null,null,null,null,null,null);
        if(c.getCount() <= 0)
            watchedIsEmpty = true;
        c = db.query ("unwatched",null,null,null,null,null,null);
        if(c.getCount() <= 0)
            unwatchedIsEmpty = true;

        //db.delete("news",null,null);

        if(watchedIsEmpty && unwatchedIsEmpty)
        {

            Thread getJSONThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    String body = "";
                    try {
                        long timeCurrent = System.currentTimeMillis();
                        URL cs = new URL("http://assignment.crazz.cn/news/en/category?timestamp=" + timeCurrent);
                        URLConnection tc = cs.openConnection();
                        BufferedReader in = new BufferedReader(new InputStreamReader(tc.getInputStream()));
                        String inputLine;
                        while ((inputLine = in.readLine()) != null) {
                            body += inputLine;
                        }
                        in.close();
                    } catch (Exception e) {
                        Log.d("netexpt", "cc");
//                    categoryList.add("BUSINESS");
//                    categoryList.add("ENTERTAINMENT");
//                    categoryList.add("HEALTH");
//                    categoryList.add("MORE TOP STORIES");
//                    categoryList.add("INDIA");
//                    categoryList.add("SCIENCE");
//                    categoryList.add("SPORTS");
//                    categoryList.add("TECHNOLOGY");
//                    categoryList.add("TOP STORIES");
//                    categoryList.add("WORLD");
//                    categoryKeyList.add("business");
//                    categoryKeyList.add("entertainment");
//                    categoryKeyList.add("health");
//                    categoryKeyList.add("more top stories");
//                    categoryKeyList.add("national");
//                    categoryKeyList.add("science");
//                    categoryKeyList.add("sports");
//                    categoryKeyList.add("technology");
//                    categoryKeyList.add("top_stories");
//                    categoryKeyList.add("world");


                        e.printStackTrace();
                    }
                    try {
                        JSONObject jsonObject = new JSONObject(body); //字符串转JSONObject, 但必须catch JSONException
                        Log.d("fuck ", jsonObject.toString());

                        JSONObject jsonObjectData = jsonObject.optJSONObject("data");
                        Log.d("fuckdata ", jsonObjectData.toString());

                        JSONObject jsonObjectCate = jsonObjectData.optJSONObject("categories");
                        Log.d("fuckcate ", jsonObjectCate.toString());

                        Iterator<String> it = jsonObjectCate.keys();

                        while (it.hasNext()) {
                            String tmp = (String) it.next();
                            categoryKeyList.add(tmp);
                            String tmp2 = jsonObjectCate.getString(tmp);
                            categoryList.add(tmp2);
                            //categoryList.add("Tab:"+i);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            getJSONThread.start();
            try
            {
                getJSONThread.join();
            }
            catch (Exception e) {
                e.printStackTrace();
            }

            for(int i=0;i<categoryKeyList.size();i++)
            {
                ContentValues cValue = new ContentValues();

                cValue.put("category",categoryKeyList.get(i));
                cValue.put("categorytext",categoryList.get(i));

                db.insert("watched",null,cValue);
            }
        }
        else
        {
            //categoryKeyList and categoryList设置为数据库中的值
            c = db.query ("watched",null,null,null,null,null,null);
            while(c.moveToNext())
            {
                categoryKeyList.add(c.getString(c.getColumnIndex("category")));
                categoryList.add(c.getString(c.getColumnIndex("categorytext")));
            }

        }
//        categoryList.add("6");
//        categoryList.add("tab7");
//        categoryList.add("tab8");
//        categoryList.add("tab4");
//        categoryList.add("tab5");
//        categoryList.add("tab6");
    }

    //获取显示页的Fragment
    @Override
    public Fragment getItem(int position)
    {
        return TabFragment.getInstance(categoryKeyList.get(position));
    }

    // page个数设置
    @Override
    public int getCount()
    {
        return categoryList.size();
    }

    //设置pageTitle， 我们只需重载此方法即可
    @Override
    public CharSequence getPageTitle(int position)
    {
        return categoryList.get(position);
    }


}
