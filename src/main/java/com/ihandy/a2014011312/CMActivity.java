package com.ihandy.a2014011312;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by weixy on 2016/9/4.
 */
public class CMActivity extends Activity
{
    private Intent intent;

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aboutme_content);
        intent = getIntent();

    }
}
