package com.wiz.dev.wiztalk.adapter;

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
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.skysea.group.MemberInfo;
import com.wiz.dev.wiztalk.R;
import com.wiz.dev.wiztalk.dto.util.RoundImageView;
import com.wiz.dev.wiztalk.utils.ImagerLoaderOptHelper;
import com.wiz.dev.wiztalk.utils.Utils;
import com.wiz.dev.wiztalk.view.XChatDetialItem_;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

import java.util.List;

@EBean
public class QunMemberListAdapter extends BaseAdapter {

    @RootContext
    Context context;

    private List<MemberInfo> list;


    public QunMemberListAdapter(Context context) {

    }

    public void changeData(List<MemberInfo> list) {
        this.list = list;
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if(list != null && list.size() >0){
            return list.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder mViewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.qun_member_item,null);

            mViewHolder = new ViewHolder();
            mViewHolder.user_avata = (ImageView) convertView.findViewById(R.id.icon_avata);
            mViewHolder.user_name = (TextView) convertView.findViewById(R.id.user_name);
            convertView.setTag(mViewHolder);

        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }
        final MemberInfo memberInfo = (MemberInfo) getItem(position);

        String avataUrl = Utils.getAvataUrl(memberInfo.getUserName().concat("@user"), 40);

        Bitmap bitmapFromCache = ImagerLoaderOptHelper.getBitmapFromCache(avataUrl, new
                ImageSize(40, 40));
        if (bitmapFromCache != null) {
            mViewHolder.user_avata.setImageBitmap(RoundImageView.toRoundBitmap(bitmapFromCache));
        }else{
            DisplayImageOptions options = ImagerLoaderOptHelper.getUserLeftAvatarOpt();
            ImageLoader.getInstance().displayImage(avataUrl, mViewHolder.user_avata,
                    options, null);
        }
        mViewHolder.user_name.setText(memberInfo.getNickname());
        return convertView;
    }


    class ViewHolder{
        TextView user_name;
        ImageView user_avata;
    }

}
