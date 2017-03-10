package a2014011312.ihandy.com.weixiangyu;

/**
 * Created by weixy on 2016/9/2.
 */
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Iterator;

public class TabFragment extends Fragment implements AdapterView.OnItemClickListener
{
    private String newsKind;
    private ListView newslist;
    private FragmentManager fManager;
    private ArrayList<News> datas = new ArrayList<>();
    public static final String ARGS_PAGE = "args_page";

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
                    datas.add(new News("2","22"));
                    e.printStackTrace();
                }
                catch (Exception e)
                {
                    datas.add(new News("1","11"));
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
                    datas.add(new News("3","33"));
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
            datas.add(new News("4","44"));
            e.printStackTrace();
        }




        News new1 = new News("title "+newsKind,"content "+newsKind);
        datas.add(new1);
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
        startActivity(intent);
    }
}