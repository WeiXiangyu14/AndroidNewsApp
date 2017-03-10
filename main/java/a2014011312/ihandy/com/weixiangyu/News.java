package a2014011312.ihandy.com.weixiangyu;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by weixy on 2016/9/2.
 */
public class News
{
    private String news_title;
    private String news_content_url;
    private String Image_url;

    public News(){}

    public News(JSONObject dataJson)
    {
        try
        {
            news_title = dataJson.getString("title");
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

    public String getImageUrl()
    {
        return Image_url;
    }

    public String getContent()
    {
        return news_content_url;
    }

    public void setTitle(String new_title)
    {
        this.news_title = new_title;
    }

    public void setContent(String new_content)
    {
        this.news_content_url = new_content;
    }
}
