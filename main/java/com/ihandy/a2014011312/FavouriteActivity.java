package com.ihandy.a2014011312;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
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
    private ImageButton backButton;
//    private LinearLayout favouriteLinearLayout;
    private ArrayList<News> datas = new ArrayList<>();

    public static int refreshHelper = 0;

    protected void onCreate(Bundle savedInstanceState)
    {
        refreshHelper = 1;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.favourite_content);

        LinearLayout favouriteLinearLayout = (LinearLayout)findViewById(R.id.favourite_linear);
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
                startActivity(intent);
            }
        });

        favouriteLinearLayout.addView(newslist);

        backButton = (ImageButton)findViewById(R.id.favourite_back);
        //backButton.setImageDrawable(getBaseContext().getResources().getDrawable(R.mipmap.up));
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
    protected void onResume()
    {
        super.onResume();
        if(refreshHelper == 0)
        {
            finish();
            Intent intent = new Intent(FavouriteActivity.this, FavouriteActivity.class);
            startActivity(intent);
        }

    }



}
