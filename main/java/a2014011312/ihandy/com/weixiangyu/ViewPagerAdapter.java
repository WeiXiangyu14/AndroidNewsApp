package a2014011312.ihandy.com.weixiangyu;

/**
 * Created by weixy on 2016/9/2.
 */
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Iterator;

public class ViewPagerAdapter extends FragmentStatePagerAdapter
{

    public ArrayList<String> categoryList = new ArrayList<>();
    public ArrayList<String> categoryKeyList = new ArrayList<>();
    private Handler handler = new Handler();

    public ViewPagerAdapter(FragmentManager fm)
    {
        super(fm);
        Thread getJSONThread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                String body = "";
                try
                {
                    long timeCurrent = System.currentTimeMillis();
                    URL cs = new URL("http://assignment.crazz.cn/news/en/category?timestamp=" + timeCurrent);
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
                    categoryList.add("2");
                    e.printStackTrace();
                }
                catch (Exception e)
                {
                    categoryList.add("1");
                    e.printStackTrace();
                }
                try
                {
                    JSONObject jsonObject = new JSONObject(body); //字符串转JSONObject, 但必须catch JSONException
                    Log.d("fuck ", jsonObject.toString());

                    JSONObject jsonObjectData = jsonObject.optJSONObject("data");
                    Log.d("fuckdata ", jsonObjectData.toString());

                    JSONObject jsonObjectCate = jsonObjectData.optJSONObject("categories");
                    Log.d("fuckcate ", jsonObjectCate.toString());

                    Iterator<String> it = jsonObjectCate.keys();

                    while(it.hasNext())
                    {
                        String tmp = (String)it.next();
                        categoryKeyList.add(tmp);
                        String tmp2 = jsonObjectCate.getString(tmp);
                        categoryList.add(tmp2);
                        //categoryList.add("Tab:"+i);
                    }
                }
                catch (JSONException e)
                {
                    categoryList.add("3");
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
            categoryList.add("5");
            e.printStackTrace();
        }
//        categoryList.add("6");
//        categoryList.add("tab7");
//        categoryList.add("tab8");
//        categoryList.add("tab4");
//        categoryList.add("tab5");
//        categoryList.add("tab6");
    }

    //获取显示页的Fragment
    @Override
    public Fragment getItem(int position)
    {
        return TabFragment.getInstance(categoryKeyList.get(position));
    }

    // page个数设置
    @Override
    public int getCount()
    {
        return categoryList.size();
    }

    //设置pageTitle， 我们只需重载此方法即可
    @Override
    public CharSequence getPageTitle(int position)
    {
        return categoryList.get(position);
    }


}
