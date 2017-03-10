package com.ihandy.a2014011312;

/**
 * Created by weixy on 2016/9/2.
 */
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class TabFragment extends Fragment implements AdapterView.OnItemClickListener
{
    private String newsKind;
    private ListView newslist;
 //   private DBHelper helper;
    private ArrayList<News> datas = new ArrayList<>();
    public static final String ARGS_PAGE = "args_page";

    private SQLiteDatabase db;

    public static TabFragment getInstance(String k)
    {
        TabFragment fragment = new TabFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARGS_PAGE, k);
        fragment.setArguments(bundle);

        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        newsKind = getArguments().getString(ARGS_PAGE);

        db = DBHelper.getInstance(getActivity());

        try
        {
            db.execSQL("CREATE TABLE IF NOT EXISTS favourites(_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "category TEXT, news_title TEXT, news_origin TEXT, news_content_url TEXT, Image_url TEXT, news_id INTEGER)");

        }
        catch (Exception e)
        {
            Log.d("nani create database","fail");
            e.printStackTrace();
        }

        Thread getJSONThread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                String body = "";
                try
                {
                    URL cs = new URL("http://assignment.crazz.cn/news/query?locale=en&category=" + newsKind);
                    URLConnection tc = cs.openConnection();
                    BufferedReader in = new BufferedReader(new InputStreamReader(tc.getInputStream()));
                    String inputLine;
                    while ((inputLine = in.readLine()) != null)
                    {
                        body += inputLine;
                    }
                    in.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                try
                {
                    JSONObject jsonObject = new JSONObject(body); //字符串转JSONObject, 但必须catch JSONException
                    Log.d("nani ", jsonObject.toString());

                    JSONObject jsonObjectData = jsonObject.optJSONObject("data");
                    //Log.d("nanidata ", jsonObjectData.toString());

                    if(jsonObjectData != null)
                    {
                        Log.d("nanidata ", jsonObjectData.toString());
                        JSONArray jsonArrayNews = jsonObjectData.optJSONArray("news");
                        Log.d("naninews ", jsonArrayNews.toString());

//                        if (jsonArrayNews != null)
//                        {
                            Log.d("alele ",newsKind);
                            for (int i = 0; i < jsonArrayNews.length(); i++)
                            {
//                                Log.d("conan ", jsonArrayNews.getJSONObject(i).toString());
                                datas.add(new News(jsonArrayNews.getJSONObject(i)));
                            }
                     //   }
                    }

                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        });
        getJSONThread.start();
        try
        {
            getJSONThread.join();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        if(datas.size() == 0)
        {
            //从数据库中获取新闻
            Cursor c = db.query ("news",null,null,null,null,null,null);
            while(c.moveToNext())
            {
                if(newsKind.equals(c.getString(c.getColumnIndex("category"))))
                {
                    News newtmp = new News();

                    newtmp.setTitle(c.getString(c.getColumnIndex("news_title")));
                    newtmp.setOrigin(c.getString(c.getColumnIndex("news_origin")));
                    newtmp.setContent(c.getString(c.getColumnIndex("news_content_url")));
                    newtmp.setImageUrl(c.getString(c.getColumnIndex("Image_url")));
                    newtmp.setId(c.getLong(c.getColumnIndex("news_id")));

                    datas.add(newtmp);
                }
            }
        }
        else
        {
            //存入数据库
            for(int i=0;i<datas.size();i++)
            {
                ContentValues cValue = new ContentValues();

                cValue.put("category",newsKind);
                cValue.put("news_title",datas.get(i).getTitle());
                cValue.put("news_origin",datas.get(i).getOrigin());
                cValue.put("news_content_url",datas.get(i).getContent());
                cValue.put("Image_url",datas.get(i).getImageUrl());
                cValue.put("news_id",datas.get(i).getId());

                db.insert("news",null,cValue);
            }
        }


//        News new1 = new News("title "+newsKind,"content "+newsKind);
//        datas.add(new1);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_tab, container, false);
        newslist = (ListView) view.findViewById(R.id.list_news);
        ListAdapter myAdapter = new ListAdapter(datas, getActivity());
        newslist.setAdapter(myAdapter);

        newslist.setOnItemClickListener(this);
        return view;
    }
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
//        FragmentTransaction fTransaction = fManager.beginTransaction();
//        NewContentFragment ncFragment = new NewContentFragment();
//        Bundle bd = new Bundle();
//        bd.putString("content", datas.get(position).getContent());
//        ncFragment.setArguments(bd);
//        //获取Activity的控件
//        TextView txt_title = (TextView) getActivity().findViewById(R.id.txt_title);
//        txt_title.setText(datas.get(position).getContent());
//        //加上Fragment替换动画
//        fTransaction.setCustomAnimations(R.anim.fragment_slide_left_enter, R.anim.fragment_slide_left_exit);
//        fTransaction.replace(R.id.tabs, ncFragment);
//        //调用addToBackStack将Fragment添加到栈中
//        fTransaction.addToBackStack(null);
//        fTransaction.commit();

        Intent intent = new Intent(getActivity(),NewsActivity.class);
        intent.putExtra("newsUrl",datas.get(position).getContent());
        intent.putExtra("newsTitle",datas.get(position).getTitle());
        intent.putExtra("newsOrigin",datas.get(position).getOrigin());
        intent.putExtra("newsImage",datas.get(position).getImageUrl());
//        intent.putExtra("newsId",datas.get(position).getId());
        startActivity(intent);
    }
}