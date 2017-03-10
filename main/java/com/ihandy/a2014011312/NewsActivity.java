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
import android.widget.TextView;

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
    private  ImageButton backButton;
    private WebView wb;
    private TextView tb;

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
        backButton = (ImageButton)findViewById(R.id.news_back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        db = DBHelper.getInstance(this);

        isFavourite = false;
        Cursor c = db.query ("favourites",null,null,null,null,null,null);
        while(c.moveToNext())
        {
            if(thisNews.getTitle().equals(c.getString(c.getColumnIndex("news_title"))))
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
                    //favouriteButton.setBackground(getResources().getDrawable(R.color.no));
                    ContentValues cValue = new ContentValues();

                    cValue.put("news_title",thisNews.getTitle());
                    cValue.put("news_origin",thisNews.getOrigin());
                    cValue.put("news_content_url",thisNews.getContent());
                    cValue.put("Image_url",thisNews.getImageUrl());
                    cValue.put("news_id",thisNews.getId());

                    db.insert("favourites",null,cValue);

                    FavouriteActivity.refreshHelper = 0;
                }
                else
                {
                    isFavourite = !isFavourite;
                    favouriteButton.setImageDrawable(getResources().getDrawable(R.mipmap.grey_heart));
                    db.delete("favourites","news_title = ?", new String[]{thisNews.getTitle()});
                    FavouriteActivity.refreshHelper = 0;
                    //Database.getInstance(v.getContext()).update("news",values,"newsId=?",new String[]{newsId});
                }
            }
        });


        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                String shareinfo = thisNews.getTitle() + "\n\nImage : " + thisNews.getImageUrl() + "\n\nURL : " + thisNews.getContent() + "\n\nFrom WeiXiangyu's News App";
                shareIntent.putExtra(Intent.EXTRA_TEXT,shareinfo);
                shareIntent.setType("text/plain");
                startActivity(Intent.createChooser(shareIntent,"Share"));

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

        if(newsUrl != null)
        {

            wb.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    view.loadUrl(url);
                    return true;
                }
            });
            wb.loadUrl(newsUrl);
        }
        else
        {
            tb = (TextView)findViewById(R.id.inv_url);
            tb.setVisibility(View.VISIBLE);
        }
    }
}
