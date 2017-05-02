package com.wiz.dev.wiztalk.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.GridView;
import android.widget.Toast;

import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.wiz.dev.wiztalk.R;
import com.wiz.dev.wiztalk.dto.model.MediaModel;
import com.wiz.dev.wiztalk.utils.ImagerLoaderOptHelper;

public class ChildAdapter extends BaseAdapter {
    /**
     * 用来存储图片的选中情况
     */
    private HashMap<Integer, Boolean> mSelectMap = new HashMap<Integer, Boolean>();
    private List<String> list;
    protected LayoutInflater mInflater;
    Map<Integer, String> map;
    Context context;

    public boolean addSelect(int key, String value) {
        if (map.containsKey(key)) {
            map.remove(key);
            notifyDataSetChanged();
        } else {
            if (map.size() < 1) {
                map.put(key, value);
                notifyDataSetChanged();
            } else {
                Toast.makeText(context, "最多选择一个文件", Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        return true;
    }

    public Map<Integer, String> getSelectImagePath() {
        if (map != null && map.size() > 0)
            return map;
        return null;
    }


    public ChildAdapter(Context context, List<String> list, GridView mGridView) {
        this.list = list;
        this.context = context;
        mInflater = LayoutInflater.from(context);
        map = new HashMap<Integer, String>();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public String getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        String path = list.get(position);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.grid_child_item, null);
            viewHolder = new ViewHolder();
            viewHolder.mImageView = (ImageView) convertView.findViewById(R.id.child_image);
            viewHolder.mCheckBox = (CheckBox) convertView.findViewById(R.id.child_checkbox);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.mImageView.setTag(path);
        if (map.containsKey(position))
            viewHolder.mCheckBox.setChecked(true);
        else
            viewHolder.mCheckBox.setChecked(false);

        viewHolder.mCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!addSelect(position, getItem(position))) {
                    ((CheckBox) v).setChecked(false);
                }
            }
        });

        com.wiz.dev.wiztalk.utils.ImageLoader.getInstance(3, com.wiz.dev.wiztalk.utils.ImageLoader.Type.FIFO).loadImage(path,viewHolder.mImageView);

        return convertView;
    }
    /**
     * 给CheckBox加点击动画，利用开源库nineoldandroids设置动画
     *
     * @param view
     */
    private void addAnimation(View view) {
        float[] vaules = new float[]{0.5f, 0.6f, 0.7f, 0.8f, 0.9f, 1.0f, 1.1f, 1.2f, 1.3f, 1.25f, 1.2f, 1.15f, 1.1f,
                1.0f};
        AnimatorSet set = new AnimatorSet();
        set.playTogether(ObjectAnimator.ofFloat(view, "scaleX", vaules),
                ObjectAnimator.ofFloat(view, "scaleY", vaules));
        set.setDuration(150);
        set.start();
    }

    /**
     * 获取选中的Item的position
     *
     * @return
     */
    public List<Integer> getSelectItems() {
        List<Integer> list = new ArrayList<Integer>();
        for (Iterator<Map.Entry<Integer, Boolean>> it = mSelectMap.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<Integer, Boolean> entry = it.next();
            if (entry.getValue()) {
                list.add(entry.getKey());
            }
        }

        return list;
    }

    public static class ViewHolder {
        public ImageView mImageView;
        public CheckBox mCheckBox;
    }

}
