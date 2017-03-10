package a2014011312.ihandy.com.weixiangyu;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import java.util.ArrayList;

/**
 * Created by weixy on 2016/9/2.
 */
public class ListAdapter extends BaseAdapter
{
    private ArrayList<News> mNews = new ArrayList<>();
    private Context mContext;

    public ListAdapter(ArrayList<News> mNews, Context mContext)
    {
        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(mContext)); //初始化ImageLoader

        this.mNews = mNews;
        this.mContext = mContext;
    }

    @Override
    public int getCount()
    {
        return mNews.size();
    }

    @Override
    public Object getItem(int position)
    {
        return null;
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewHolder viewHolder;
        if(convertView == null)
        {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item,parent,false);
            viewHolder = new ViewHolder();
            viewHolder.txt_item_title = (TextView) convertView.findViewById(R.id.txt_item_title);
            viewHolder.item_pic = (ImageView) convertView.findViewById(R.id.item_pic);
            convertView.setTag(viewHolder);
        }
        else
        {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        ImageLoader.getInstance().displayImage(mNews.get(position).getImageUrl(), viewHolder.item_pic, getDisplayOption());

        viewHolder.txt_item_title.setText(mNews.get(position).getTitle());
        return convertView;
    }

    public DisplayImageOptions getDisplayOption() {
        DisplayImageOptions options;
        options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)//设置下载的图片是否缓存在内存中
                .cacheOnDisk(true)//设置下载的图片是否缓存在SD卡中
                .considerExifParams(true) //是否考虑JPEG图像EXIF参数（旋转，翻转）
                .imageScaleType(ImageScaleType.EXACTLY)//设置图片以如何的编码方式显示
                .bitmapConfig(Bitmap.Config.ARGB_8888)//设置图片的解码类型//
                .build();//构建完成
        return options;
    }

    private class ViewHolder
    {
        TextView txt_item_title;
        ImageView item_pic;
    }
}
