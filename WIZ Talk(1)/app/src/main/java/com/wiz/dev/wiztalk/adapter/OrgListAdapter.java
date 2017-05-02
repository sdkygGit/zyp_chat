package com.wiz.dev.wiztalk.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.wiz.dev.wiztalk.R;

import java.util.ArrayList;
import java.util.HashMap;



/**
 * Created by WangKun on 2014/10/21.
 */
public class OrgListAdapter extends BaseAdapter {
    final int VIEW_TYPE = 3;
    final int TYPE_Head = 0;
    final int TYPE_Org = 1;
    final int TYPE_man = 2;



    public ArrayList<HashMap<String, Object>> dataArray;

    //每个convert view都会调用此方法，获得当前所需要的view样式
    @Override
    public int getItemViewType(int position)
    {
        HashMap<String, Object> map =(HashMap<String, Object>)dataArray.get(position);
        String prefix = map.get("celltype").toString();
        if (prefix.equals("head"))
        {
            return TYPE_Head;
        }
        if (prefix.equals("org"))
        {
            return TYPE_Org;
        }
        return TYPE_man;
    }

    @Override
    public int getViewTypeCount() {
        return VIEW_TYPE;
    }

    /** LayoutInflater */
    private LayoutInflater mInflater;
    private Context        context;

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

        int type = getItemViewType(position);

        HashMap<String, Object> map =(HashMap<String, Object>)dataArray.get(position);

        //无convertView，需要new出各个控件
        if(convertView == null)
        {
            //按当前所需的样式，确定new的布局
            switch(type)
            {
                case TYPE_Head:
                    convertView = mInflater.inflate(R.layout.contact_head, parent, false);
                    holder = new viewHolder();
                    holder.caption = (TextView) convertView.findViewById(R.id.contacthead_caption);
                    holder.sepline  = (View)convertView.findViewById(R.id.contacthead_liner);
                    holder.tailimage   = (ImageView)convertView.findViewById(R.id.contacthead_updown);
                    convertView.setTag(holder);
                    break;
                case TYPE_man:
                case TYPE_Org:
                    convertView = mInflater.inflate(R.layout.contact_cell, parent, false);
                    holder = new viewHolder();
                    holder.caption     = (TextView)convertView.findViewById(R.id.contactcell_caption);
                    holder.headimage   = (ImageView)convertView.findViewById(R.id.contactcell_avator);
                    holder.tailimage   = (ImageView)convertView.findViewById(R.id.contactcell_go);
                    holder.sepline  = (View)convertView.findViewById(R.id.contactcell_liner);
                    convertView.setTag(holder);
                    break;
            }
        }
        else
        {
            holder = (viewHolder) convertView.getTag();
        }


        //设置资源
        switch(type)
        {
            case TYPE_Head:
                holder.caption.setText(map.get("caption").toString());
                holder.tailimage.setVisibility(View.GONE);
                holder.sepline.setVisibility(View.VISIBLE);
                break;
            case TYPE_man:
                holder.headimage.setVisibility(View.VISIBLE);
                holder.caption.setText(map.get("caption").toString());
                holder.tailimage.setVisibility(View.GONE);
                break;
            case TYPE_Org:
                holder.headimage.setVisibility(View.GONE);
                holder.caption.setText(map.get("caption").toString());
                holder.tailimage.setVisibility(View.VISIBLE);
                break;
        }

        if (type != TYPE_Head)
        {
            boolean f,l ;

            f = map.get("isFirst").toString() == "Y";
            l = map.get("isLast").toString() == "Y" ;

            if (!f && !l)
                holder.sepline.setVisibility(View.VISIBLE);
            else if (f && l)
                holder.sepline.setVisibility(View.INVISIBLE);
            else if (f)
                holder.sepline.setVisibility(View.VISIBLE);
            else if (l)
                holder.sepline.setVisibility(View.INVISIBLE);
            else
                holder.sepline.setVisibility(View.VISIBLE);
        }
        return convertView;
    }

    public OrgListAdapter(Context context)
    {
        this.context = context;
        dataArray = new ArrayList<HashMap<String, Object>>();
        this.mInflater = LayoutInflater.from(context);
    }


    private class viewHolder{
        TextView  caption;
        ImageView tailimage;
        ImageView headimage;
        View      sepline;
    }
}



