package com.wiz.dev.wiztalk.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fanning.library.FNTools;
import com.wiz.dev.wiztalk.R;
import com.wiz.dev.wiztalk.public_store.PublicTools;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * Created by kuang4u on 2015/4/21.
 */

public class AppNewMsgAdapter extends BaseAdapter {

    public ArrayList<HashMap<String, Object>> dataArray;


    /** LayoutInflater */
    private LayoutInflater mInflater;
    private Context context;

    public int getCount() {
        int size= dataArray.size();
        return size;
    }

    @Override
    public Object getItem(int position) {
        return dataArray.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        viewHolder  holder = null;

        HashMap<String, Object> map =(HashMap<String, Object>)dataArray.get(position);

        if(convertView == null)
        {
            convertView = mInflater.inflate(R.layout.appnotice_cell, parent, false);
            holder = new viewHolder();
            holder.appicon = (ImageView) convertView.findViewById(R.id.appiconimage);
            holder.appname = (TextView) convertView.findViewById(R.id.appname);
            holder.appmsg = (TextView) convertView.findViewById(R.id.appmessage);
            holder.msgtime = (TextView) convertView.findViewById(R.id.appmessagetime);
            convertView.setTag(holder);
        }
        else
        {
            holder = (viewHolder) convertView.getTag();
        }

        String appid =PublicTools.getMapedString(map, "appid");

        String appname = PublicTools.getAppName(context, appid) ;
        if (FNTools.emptyString(appname))
            appname = "未命名app";

        holder.appname.setText(appname);
        holder.appmsg.setText(map.get("appmsg").toString());


        Drawable drawable = PublicTools.getAppIcon(context, appid );

        if (drawable == null)
        {
            holder.appicon.setImageResource(R.mipmap.appicon);
        }
        else
        {
            holder.appicon.setImageDrawable(drawable);
        }

        if (map.containsKey("msgtime"))
        {
            holder.msgtime.setVisibility(View.VISIBLE);
            holder.msgtime.setText(map.get("msgtime").toString());
        }
        else
        {
            holder.msgtime.setVisibility(View.INVISIBLE);
        }

        return convertView;
    }

    public AppNewMsgAdapter(Context context)
    {
        this.context = context;
        dataArray = new ArrayList<HashMap<String, Object>>();
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public boolean isEmpty()
    {
        return false;
    }

    private class viewHolder{
        ImageView appicon;
        TextView  appname;
        TextView  appmsg;
        TextView  msgtime;
    }
}




