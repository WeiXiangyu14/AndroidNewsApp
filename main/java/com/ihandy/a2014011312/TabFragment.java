package com.ihandy.a2014011312;

/**
 * Created by weixy on 2016/9/2.
 */
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class TabFragment extends Fragment implements AdapterView.OnItemClickListener
{
    private String newsKind;
    private PullToRefreshListView newslist;
 //   private DBHelper helper;
    private ArrayList<News> datas = new ArrayList<>();
    public static final String ARGS_PAGE = "args_page";

    private SQLiteDatabase db;

    private Comparator<News> comparator;

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

        comparator = new Comparator<News>(){
            public int compare(News s1, News s2)
            {
                if(s1.getId() > s2.getId())
                    return -1;
                else if (s1.getId() == s2.getId())
                    return 0;
                else
                    return 1;
            }
        };

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
                    Log.d("fff  ","io excp in start "+newsKind);
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
            Log.d("now news num = ",newsKind+" "+c.getCount());
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
                Cursor c = db.query ("news",null,null,null,null,null,null);
                boolean alreadyInDB = false;
                while(c.moveToNext())
                {
                    if(datas.get(i).getTitle().equals(c.getString(c.getColumnIndex("news_title"))))
                    {
                        alreadyInDB = true;
                        break;
                    }
                }
                if(alreadyInDB)
                    continue;

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

        Collections.sort(datas,comparator);

//        News new1 = new News("title "+newsKind,"content "+newsKind);
//        datas.add(new1);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_tab, container, false);
        newslist = (PullToRefreshListView) view.findViewById(R.id.list_news);
        newslist.setEmptyView(view.findViewById(R.id.empty));
        final ListAdapter myAdapter = new ListAdapter(datas, getActivity());
        newslist.setAdapter(myAdapter);

        newslist.setOnItemClickListener(this);
        newslist.setMode(PullToRefreshBase.Mode.BOTH);
 //       newslist.setOnPullEventListener(new PullToRefreshBase.OnPullEventListener<ListView>() {

   //         @Override
//            public void onPullEvent(PullToRefreshBase<ListView> refreshView, PullToRefreshBase.State state,
//                                    PullToRefreshBase.Mode direction) {
//                Log.d("puref", "onPullEvent() : " + state.name());
//
//                if (state.equals(PullToRefreshBase.State.PULL_TO_REFRESH)) {
////                    refreshView.getLoadingLayoutProxy().setPullLabel(getString(R.string.pull_to_refresh));
////                    refreshView.getLoadingLayoutProxy().setReleaseLabel(getString(R.string.release_to_refresh));
////                    refreshView.getLoadingLayoutProxy().setRefreshingLabel(getString(R.string.loading));
//
////                    String label = DateUtils.formatDateTime(getApplicationContext(),
////                            System.currentTimeMillis(),
////                            DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
//                    // Update the LastUpdatedLabel
//                    refreshView.getLoadingLayoutProxy().setLastUpdatedLabel("interrrrrrr");
////                            getString(R.string.updated) + " : " + label);
//                }
//            }
//        });

        newslist.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>()
        {
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView)
            {
                datas.clear();
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
                //        datas.add(new News("from internet",""));
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
                    Log.d("now news num = ",newsKind+" "+c.getCount());
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
                        Cursor c = db.query ("news",null,null,null,null,null,null);
                        boolean alreadyInDB = false;
                        while(c.moveToNext())
                        {
                            if(datas.get(i).getTitle().equals(c.getString(c.getColumnIndex("news_title"))))
                            {
                                alreadyInDB = true;
                                break;
                            }
                        }
                        if(alreadyInDB)
                            continue;

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
                Collections.sort(datas,comparator);

                myAdapter.notifyDataSetChanged();
                new FinishRefresh().execute();
            }

            // 上拉Pulling Up
            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView)
            {
                // 上拉的时候添加选项
                int sz = datas.size();
                if(sz == 0)
                {
                    new FinishRefresh().execute();
                    return ;
                }

                final long oldestId = datas.get(sz-1).getId() - 1;

                Log.d("hhhhh ","oldestid = "+oldestId);

                Thread getJSONThread = new Thread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        String body = "";
                        try
                        {
                            String oldurl = "http://assignment.crazz.cn/news/query?locale=en&category=" + newsKind + "&max_news_id=" + oldestId;
                            Log.d("ggg  ","url is " + oldurl);
                            URL cs = new URL(oldurl);
                            URLConnection tc = cs.openConnection();
                            BufferedReader in = new BufferedReader(new InputStreamReader(tc.getInputStream()));
                            String inputLine;
                            while ((inputLine = in.readLine()) != null)
                            {
                                body += inputLine;
                            }
                            in.close();
                            Log.d("ggg body ",body);
                        }
                        catch (IOException e)
                        {
                            Log.d("ggg  ","io wrong");
                            e.printStackTrace();
                        }
                        catch (Exception e)
                        {
                            Log.d("ggg  ","net wrong");
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
                                Log.d("ggg  ","pull up get num"+jsonArrayNews.length());
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
                            Log.d("ggg  ","json wrong");
                            e.printStackTrace();
                        }
                        //        datas.add(new News("from internet",""));
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
                    Log.d("now news num = ",newsKind+" "+c.getCount());
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
                        Cursor c = db.query ("news",null,null,null,null,null,null);
                        boolean alreadyInDB = false;
                        while(c.moveToNext())
                        {
                            if(datas.get(i).getTitle().equals(c.getString(c.getColumnIndex("news_title"))))
                            {
                                alreadyInDB = true;
                                break;
                            }
                        }
                        if(alreadyInDB)
                            continue;

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
                Collections.sort(datas,comparator);

                myAdapter.notifyDataSetChanged();

                new FinishRefresh().execute();
            }
        });
        return view;
    }

    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        position--;
        if(position>=0)
        {
            Intent intent = new Intent(getActivity(), NewsActivity.class);
            intent.putExtra("newsUrl", datas.get(position).getContent());
            intent.putExtra("newsTitle", datas.get(position).getTitle());
            intent.putExtra("newsOrigin", datas.get(position).getOrigin());
            intent.putExtra("newsImage", datas.get(position).getImageUrl());
//        intent.putExtra("newsId",datas.get(position).getId());
            startActivity(intent);
        }
    }

    private class FinishRefresh extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            return null;
        }

        @Override
        protected void onPostExecute(Void result){
            newslist.onRefreshComplete();
        }
    }
}