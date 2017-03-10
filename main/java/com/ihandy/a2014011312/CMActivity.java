package com.ihandy.a2014011312;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by weixy on 2016/9/4.
 */
public class CMActivity extends Activity
{
    private Intent intent;
    private ListView cmListView;
    private CMAdapter cmAdapter;

    private ImageButton backButton;

    private ArrayList<String> tmpdatas= new ArrayList<String>();

    private int halving;
    private String[] datas = new String[20];
    private int dataloc = 0;

    private SQLiteDatabase db;

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cm_content);
        intent = getIntent();

        db = DBHelper.getInstance(this);
        datas[dataloc] = "Watched";
        dataloc++;
        Cursor c1 = db.query ("watched",null,null,null,null,null,null);
        while(c1.moveToNext())
        {
            datas[dataloc] = c1.getString(c1.getColumnIndex("categorytext"));
            dataloc++;
        }
        datas[dataloc] = "Unwatched";
        dataloc++;
        Cursor c2 = db.query ("unwatched",null,null,null,null,null,null);
        while(c2.moveToNext())
        {
            datas[dataloc] = c2.getString(c2.getColumnIndex("categorytext"));
            dataloc++;
        }

        halving = c1.getCount() + 1;

        cmListView = (ListView)findViewById(R.id.cm_listview);

        tmpdatas.clear();
        for(int i=0;i<dataloc;i++)
            tmpdatas.add(datas[i]);

        cmAdapter = new CMAdapter(this,tmpdatas);


        cmListView.setOnItemClickListener(new ListView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                if(position == 0 || position == halving)
                {

                }
                else if(position > 0 && position < halving)
                {
                    String loguse = "halving="+halving+" pos="+position;
                    Log.d("wocao watch",loguse);

                    String tmp = datas[position];
                    for(int i=position;i<dataloc-1;i++)
                    {
                        datas[i] = datas[i+1];
                    }
                    datas[dataloc-1]= tmp;
                    halving--;

                    String tmpkey = "";
                    Cursor c3 = db.query ("watched",null,null,null,null,null,null);
                    while(c3.moveToNext())
                    {
                        if(tmp.equals(c3.getString(c3.getColumnIndex("categorytext"))))
                        {
                            tmpkey = c3.getString(c3.getColumnIndex("category"));
                            break;
                        }
                    }

                    ContentValues cValue = new ContentValues();

                    cValue.put("category",tmpkey);
                    cValue.put("categorytext",tmp);

                    db.insert("unwatched",null,cValue);

                    db.delete("watched","categorytext = ?", new String[]{tmp});
                }
                else if(position > halving)
                {
                    String tmp = datas[position];
                    for(int i=position;i>1;i--)
                    {
                        datas[i] = datas[i-1];
                    }
                    datas[1]= tmp;
                    halving++;

                    String tmpkey = "";
                    Cursor c4 = db.query ("unwatched",null,null,null,null,null,null);
                    while(c4.moveToNext())
                    {
                        if(tmp.equals(c4.getString(c4.getColumnIndex("categorytext"))))
                        {
                            tmpkey = c4.getString(c4.getColumnIndex("category"));
                            break;
                        }
                    }

                    ContentValues cValue = new ContentValues();

                    cValue.put("category",tmpkey);
                    cValue.put("categorytext",tmp);

                    db.insert("watched",null,cValue);

                    db.delete("unwatched","categorytext = ?", new String[]{tmp});

                }

                tmpdatas.clear();
                for(int i=0;i<dataloc;i++)
                    tmpdatas.add(datas[i]);

                cmAdapter.notifyDataSetChanged();
//                finish();
//                Intent intent = new Intent(CMActivity.this, CMActivity.class);
//                startActivity(intent);

            }
        });

        backButton = (ImageButton)findViewById(R.id.cm_back);
        //backButton.setImageDrawable(getBaseContext().getResources().getDrawable(R.mipmap.up));
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        cmListView.setAdapter(cmAdapter);
    }
}
