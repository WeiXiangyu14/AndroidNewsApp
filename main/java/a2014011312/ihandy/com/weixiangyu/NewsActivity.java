package a2014011312.ihandy.com.weixiangyu;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by weixy on 2016/9/4.
 */
public class NewsActivity extends Activity
{
    private Intent intent;
    private String newsUrl;

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_content);
        intent = getIntent();
        newsUrl = intent.getStringExtra("newsUrl");
        WebView wb = (WebView)findViewById(R.id.news_webview);

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
