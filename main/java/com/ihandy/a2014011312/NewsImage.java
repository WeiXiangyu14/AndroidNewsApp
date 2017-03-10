package com.ihandy.a2014011312;

import org.json.JSONObject;

/**
 * Created by weixy on 2016/9/3.
 */
public class NewsImage
{
    private String url;
    private String copyright;

    public NewsImage(JSONObject dataJson)
    {
        url = dataJson.optString("url");
        copyright = dataJson.optString("copyright");
    }

    public String getUrl()
    {
        return url;
    }

    public String getCopyright()
    {
        return copyright;
    }
}
