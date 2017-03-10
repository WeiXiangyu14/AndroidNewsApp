package com.ihandy.a2014011312;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

/**
 * Created by weixy on 2016/9/4.
 */
public class AboutActivity extends Activity
{
    private Intent intent;
    private ImageButton backButton;

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aboutme_content);
        intent = getIntent();

        backButton = (ImageButton)findViewById(R.id.aboutme_back);
        //backButton.setImageDrawable(getBaseContext().getResources().getDrawable(R.mipmap.up));
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }
}
