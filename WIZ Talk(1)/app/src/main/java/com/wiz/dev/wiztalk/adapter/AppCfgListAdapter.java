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


/**
 * Created by kuang4u on 2015/4/21.
 */

public class AppCfgListAdapter extends BaseAdapter {

    public ArrayList<String> dataArray;
    AppCfgCallBack nCallback;
    boolean isEditMode;


    /** LayoutInflater */
    private LayoutInflater mInflater;
    private Context context;

    public int getCount() {
        int size= dataArray.size();
        return size;
    }

    public interface AppCfgCallBack {
        boolean    isInCommon(String appid);
        void       itemClick(String appid);
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

        String appid =(String)dataArray.get(position);

        if(convertView == null)
        {
            convertView = mInflater.inflate(R.layout.appstore_cell, parent, false);
            holder = new viewHolder();
            holder.appicon = (ImageView) convertView.findViewById(R.id.appiconimage);
            holder.appname = (TextView) convertView.findViewById(R.id.appname);
            holder.apptype = (TextView) convertView.findViewById(R.id.apptype);
            holder.addapp = (TextView) convertView.findViewById(R.id.install);
            holder.apptype.setVisibility(View.INVISIBLE);
            holder.addapp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String appid = (String)view.getTag();
                    nCallback.itemClick(appid);
                }
            });
            convertView.setTag(holder);
        }
        else
        {
            holder = (viewHolder) convertView.getTag();
        }
        holder.appname.setText(PublicTools.getAppName(context,appid));
        holder.appicon.setImageDrawable(PublicTools.getAppIcon(context,appid));
        holder.addapp.setTag(appid);

        if (isEditMode)
        {
            if (nCallback.isInCommon(appid))
            {
                holder.addapp.setText("取消常用");
                holder.addapp.setTextColor(context.getResources().getColor(R.color.captiontxtcolor));
            }
            else
            {
                holder.addapp.setText("添加常用");
                holder.addapp.setTextColor(context.getResources().getColor(R.color.appstoretxtcolor));
            }
        }
        else
        {
            holder.addapp.setText("打开");
            holder.addapp.setTextColor(context.getResources().getColor(R.color.appstoretxtcolor));
        }

        return convertView;
    }

    public void setEditMode(boolean isEditMode)
    {
        if (this.isEditMode != isEditMode)
        {
            this.isEditMode = isEditMode;
            this.notifyDataSetChanged();
        }
    }

    public AppCfgListAdapter(Context context, AppCfgCallBack nCallback, boolean isEditMode)
    {
        this.context   = context   ;
        this.nCallback = nCallback ;
        this.isEditMode = isEditMode;

        dataArray = new ArrayList<String>();
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
        TextView  apptype;
        TextView  addapp;
    }

    public void reload() {

        ArrayList<String> appslist;
        String appid;

        dataArray.clear();
        appslist = PublicTools.getAllInstalledAppsList(context, null);

        for (int i=0 ; appslist != null && i < appslist.size(); i ++)
        {
            appid = appslist.get(i);
            if (!FNTools.emptyString(appid))
            {
                if (!FNTools.appIsInstalled(context, appid))
                    continue;

                String name = PublicTools.getAppName(context, appid);
                if (FNTools.emptyString(name))
                    continue;
                Drawable drawable = PublicTools.getAppIcon(context, appid);
                if (drawable == null)
                    continue;
                dataArray.add(appid);
            }
        }

        this.notifyDataSetChanged();
        return ;
    }
}




