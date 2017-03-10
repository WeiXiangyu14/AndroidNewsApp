package com.ihandy.a2014011312;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by weixy on 2016/9/4.
 */
public class FavouriteActivity extends Activity
{
    private Intent intent;

    private SQLiteDatabase db;
    private ListView newslist;
    private ArrayList<News> datas = new ArrayList<>();

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.favourite_content);

        LinearLayout ll = (LinearLayout)findViewById(R.id.favourite_linear);
        intent = getIntent();

        db = DBHelper.getInstance(this);

        Cursor c = db.query ("favourites",null,null,null,null,null,null);
        while(c.moveToNext())
        {
            News newtmp = new News();

            newtmp.setTitle(c.getString(c.getColumnIndex("news_title")));
            newtmp.setOrigin(c.getString(c.getColumnIndex("news_origin")));
            newtmp.setContent(c.getString(c.getColumnIndex("news_content_url")));
            newtmp.setImageUrl(c.getString(c.getColumnIndex("Image_url")));
            newtmp.setId(c.getLong(c.getColumnIndex("news_id")));

            datas.add(newtmp);
        }

        for(int i=0;i<datas.size();i++)
        {
            Log.d("favourite create ",datas.get(i).getTitle());
        }

        newslist = new ListView(getBaseContext());
        ListAdapter myAdapter = new ListAdapter(datas,getBaseContext());
        newslist.setAdapter(myAdapter);

        newslist.setOnItemClickListener(new ListView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Intent intent = new Intent(getBaseContext(),NewsActivity.class);
                intent.putExtra("newsUrl",datas.get(position).getContent());
                intent.putExtra("newsTitle",datas.get(position).getTitle());
                intent.putExtra("newsOrigin",datas.get(position).getOrigin());
                intent.putExtra("newsImage",datas.get(position).getImageUrl());
//        intent.putExtra("newsId",datas.get(position).getId());
                startActivity(intent);
            }
        });

        ll.addView(newslist);

    }

//    @Override
//    protected void onResume()
//    {
//        super.onResume();
//        setContentView(R.layout.favourite_content);
//
//        LinearLayout ll = (LinearLayout)findViewById(R.id.favourite_linear);
//        intent = getIntent();
//
//        db = DBHelper.getInstance(this);
//
//        Cursor c = db.query ("favourites",null,null,null,null,null,null);
//        while(c.moveToNext())
//        {
//            News newtmp = new News();
//
//            newtmp.setTitle(c.getString(c.getColumnIndex("news_title")));
//            newtmp.setOrigin(c.getString(c.getColumnIndex("news_origin")));
//            newtmp.setContent(c.getString(c.getColumnIndex("news_content_url")));
//            newtmp.setImageUrl(c.getString(c.getColumnIndex("Image_url")));
//            newtmp.setId(c.getLong(c.getColumnIndex("news_id")));
//
//            datas.add(newtmp);
//        }
//
//        for(int i=0;i<datas.size();i++)
//        {
//            Log.d("favourite create ",datas.get(i).getTitle());
//        }
//
//        newslist = new ListView(getBaseContext());
//        ListAdapter myAdapter = new ListAdapter(datas,getBaseContext());
//        newslist.setAdapter(myAdapter);
//
//        ll.addView(newslist);
//
//        newslist.setOnItemClickListener(new ListView.OnItemClickListener()
//        {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
//            {
//                Intent intent = new Intent(getBaseContext(),NewsActivity.class);
//                intent.putExtra("newsUrl",datas.get(position).getContent());
//                intent.putExtra("newsTitle",datas.get(position).getTitle());
//                intent.putExtra("newsOrigin",datas.get(position).getOrigin());
//                intent.putExtra("newsImage",datas.get(position).getImageUrl());
////        intent.putExtra("newsId",datas.get(position).getId());
//                startActivity(intent);
//            }
//        });
//    }
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
//    {
//        View view = inflater.inflate(R.layout.favourite_content, container, false);
//        newslist = (ListView) view.findViewById(R.id.list_news);
//        ListAdapter myAdapter = new ListAdapter(datas, this);
//        newslist.setAdapter(myAdapter);
//
//       // newslist.setOnItemClickListener(this);
//        return view;
//    }
}
