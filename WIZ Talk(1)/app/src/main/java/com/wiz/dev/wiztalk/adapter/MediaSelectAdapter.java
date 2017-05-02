/*
 * Copyright 2013 - learnNcode (learnncode@gmail.com)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */


package com.wiz.dev.wiztalk.adapter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.wiz.dev.wiztalk.R;
import com.wiz.dev.wiztalk.dto.model.MediaModel;
import com.wiz.dev.wiztalk.utils.MyImageLoader;
import com.wiz.dev.wiztalk.utils.Utils;


public class MediaSelectAdapter extends BaseAdapter {

    private Context mContext;
    private List<MediaModel> mGalleryModelList;
    LayoutInflater viewInflater;
    Map<Integer, MediaModel> map;

    public MediaSelectAdapter(Context context, List<MediaModel> categories) {
        mGalleryModelList = categories;
        mContext = context;
        viewInflater = LayoutInflater.from(mContext);
        map = new HashMap<Integer, MediaModel>();
    }

    public int getCount() {
        return mGalleryModelList.size();
    }

    public boolean addSelect(int key, MediaModel value) {
        if (map.containsKey(key)) {
            map.remove(key);
            notifyDataSetChanged();
        } else {
            if (map.size() < 1) {
                map.put(key, value);
                notifyDataSetChanged();
            } else {
                Toast.makeText(mContext, "最多选择一个文件", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }

    public Map<Integer, MediaModel> getSelectMediaModel() {
        if (map != null && map.size() > 0)
            return map;
        return null;
    }

    @Override
    public MediaModel getItem(int position) {
        return mGalleryModelList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = viewInflater.inflate(R.layout.media_chooser_item, null);
            holder = new ViewHolder();
            holder.media_name = (TextView) convertView.findViewById(R.id.media_name);
            holder.media_add_time = (TextView) convertView.findViewById(R.id.media_add_time);
            holder.media_size = (TextView) convertView.findViewById(R.id.media_size);
            holder.imageView = (ImageView) convertView.findViewById(R.id.imageview);
            holder.checkbox = (CheckBox) convertView.findViewById(R.id.checkbox);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (!getItem(position).isVideo) {
            holder.imageView.setImageResource(R.drawable.ic_select_file_music);
        } else {
            MyImageLoader.getInstance(3, MyImageLoader.Type.FIFO).loadImage(getItem(position).url, holder.imageView, true);
        }

        if (map.containsKey(position))
            holder.checkbox.setChecked(true);
        else
            holder.checkbox.setChecked(false);

        holder.checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!addSelect(position, getItem(position))) {
                    ((CheckBox) v).setChecked(false);
                }
            }
        });

        holder.media_name.setText(getItem(position).displayName);
        holder.media_add_time.setText(dateFormat.format(new Date(getItem(position).addTime * 1000)));
        holder.media_size.setText(Utils.getFileSizeFromLength(getItem(position).size));
        return convertView;
    }

    class ViewHolder {
        ImageView imageView;
        TextView media_name;
        TextView media_add_time;
        TextView media_size;
        CheckBox checkbox;
    }

    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
}
