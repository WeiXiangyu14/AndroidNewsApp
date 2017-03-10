package com.ihandy.a2014011312;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;

/**
 * Created by weixy on 2016/9/4.
 */
public class NewsActivity extends Activity
{
    private Intent intent;
    private String newsUrl;
    News thisNews = new News();

    private ImageButton favouriteButton;
    private ImageButton shareButton;
    private WebView wb;

    boolean isFavourite;

    private SQLiteDatabase db;

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_content);
        intent = getIntent();
        newsUrl = intent.getStringExtra("newsUrl");

        thisNews.setContent(newsUrl);
        thisNews.setOrigin(intent.getStringExtra("newsOrigin"));
        thisNews.setTitle(intent.getStringExtra("newsTitle"));
        thisNews.setImageUrl(intent.getStringExtra("newsImage"));
        thisNews.setId(0);

        favouriteButton = (ImageButton)findViewById(R.id.news_favourite);
        shareButton = (ImageButton)findViewById(R.id.news_share);

        db = DBHelper.getInstance(this);

        isFavourite = false;
        Cursor c = db.query ("favourites",null,null,null,null,null,null);
        while(c.moveToNext())
        {
            if(newsUrl.equals(c.getString(c.getColumnIndex("news_content_url"))))
            {
                isFavourite = true;
                break;
            }
        }

        if(isFavourite)
            favouriteButton.setImageDrawable(getResources().getDrawable(R.mipmap.red_heart));
        else
            favouriteButton.setImageDrawable(getResources().getDrawable(R.mipmap.grey_heart));


        favouriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isFavourite)
                {
                    isFavourite = !isFavourite;
                    favouriteButton.setImageDrawable(getResources().getDrawable(R.mipmap.red_heart));
                    ContentValues cValue = new ContentValues();

                    cValue.put("news_title",thisNews.getTitle());
                    cValue.put("news_origin",thisNews.getOrigin());
                    cValue.put("news_content_url",thisNews.getContent());
                    cValue.put("Image_url",thisNews.getImageUrl());
                    cValue.put("news_id",thisNews.getId());

                    db.insert("favourites",null,cValue);
                }
                else
                {
                    isFavourite = !isFavourite;
                    favouriteButton.setImageDrawable(getResources().getDrawable(R.mipmap.grey_heart));
                    db.delete("favourites","news_content_url = ?", new String[]{newsUrl});
                    //Database.getInstance(v.getContext()).update("news",values,"newsId=?",new String[]{newsId});
                }
            }
        });



//        ContentValues cValue = new ContentValues();
//
//        cValue.put("news_title",thisNews.getTitle());
//        cValue.put("news_origin",thisNews.getOrigin());
//        cValue.put("news_content_url",thisNews.getContent());
//        cValue.put("Image_url",thisNews.getImageUrl());
//        cValue.put("news_id",thisNews.getId());
//
//        db.insert("favourites",null,cValue);


        wb = (WebView)findViewById(R.id.news_webview);

        wb.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        wb.loadUrl(newsUrl);
    }
}
