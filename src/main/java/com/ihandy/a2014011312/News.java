package com.ihandy.a2014011312;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by weixy on 2016/9/2.
 */
public class News
{
    private String news_title;
    private String news_origin;
    private String news_content_url;
    private String Image_url;
    private long news_id;

    public News(){}

    public News(JSONObject dataJson)
    {
        try
        {
            news_title = dataJson.getString("title");
            news_origin = dataJson.getString("origin");
            news_id = dataJson.getLong("news_id");
            JSONObject jsonSource = dataJson.optJSONObject("source");
            news_content_url = jsonSource.getString("url");
            JSONArray jsonArrayImages = dataJson.optJSONArray("imgs");
            if(jsonArrayImages.length() > 0)
            {
                JSONObject jsonImage = jsonArrayImages.getJSONObject(0);
                Image_url = jsonImage.getString("url");
            }
            else
            {
                Image_url = "";
            }
        }
        catch (JSONException e)
        {
            news_title = "JSONException";
            e.printStackTrace();
        }
    }

    public News(String new_title, String new_content)
    {
        this.news_title = new_title;
        this.news_content_url = new_content;
    }

    public String getTitle()
    {
        return news_title;
    }

    public String getOrigin()
    {
        return news_origin;
    }

    public long getId()
    {
        return news_id;
    }

    public String getImageUrl()
    {
        return Image_url;
    }

    public String getContent()
    {
        return news_content_url;
    }

    public void setOrigin(String new_origin)
    {
        this.news_origin = new_origin;
    }

    public void setTitle(String new_title)
    {
        this.news_title = new_title;
    }

    public void setImageUrl(String img_url)
    {
        this.Image_url = img_url;
    }

    public void setId(long id)
    {
        this.news_id = id;
    }

    public void setContent(String new_content)
    {
        this.news_content_url = new_content;
    }
}
