package com.ihandy.a2014011312;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by weixy on 2016/9/7.
 */
public class CleanActivity extends Activity
{
    private Intent intent;
    private ImageButton cleanButton;
    private ImageButton backButton;
    private SQLiteDatabase db;

    private Toast t;


    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.clean_content);
        intent = getIntent();

        db = DBHelper.getInstance(getBaseContext());

        t = new Toast(this);
        t.setDuration(Toast.LENGTH_SHORT);
        TextView txv = new TextView(this);
        txv.setText("缓存已清除");
        txv.setTextSize(24.0f);
        t.setView(txv);
//        t.setText("缓存已清除");

        cleanButton = (ImageButton)findViewById(R.id.clean_button);
        cleanButton.setImageDrawable(getBaseContext().getResources().getDrawable(R.mipmap.recycle));
        cleanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                db.delete("news",null,null);

                t.show();
             //   finish();
            }
        });
        backButton = (ImageButton)findViewById(R.id.clean_back);
        //backButton.setImageDrawable(getBaseContext().getResources().getDrawable(R.mipmap.up));
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}
