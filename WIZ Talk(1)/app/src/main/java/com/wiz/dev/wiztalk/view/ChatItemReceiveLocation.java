package com.wiz.dev.wiztalk.view;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.epic.traverse.push.util.L;
import com.wiz.dev.wiztalk.DB.XmppDbMessage;
import com.wiz.dev.wiztalk.DB.XmppMessage;
import com.wiz.dev.wiztalk.DB.XmppMessageContentProvider;
import com.wiz.dev.wiztalk.R;
import com.wiz.dev.wiztalk.public_store.ConstDefine;
import com.wiz.dev.wiztalk.utils.SmileyParser;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.LongClick;
import org.androidannotations.annotations.ViewById;
import org.json.JSONException;
import org.json.JSONObject;

import map.LocationActivity;

/**
 * 接收的文本消息
 * Created by Dong on 2015/10/13.
 */
@EViewGroup(R.layout.item_chat_received_location)
public class ChatItemReceiveLocation extends ChatItemView implements View.OnClickListener {

    @ViewById
    TextView tv_message;

    @ViewById
    TextView tv_name;//昵称

    @ViewById
    ImageView img_local;

    @ViewById
    LinearLayout lay_localname;

    JSONObject jsonObject;

    @ViewById
    TextView tv_street;


    public ChatItemReceiveLocation(Context context) {
        super(context);
    }

    @Override
    public void bindOther(XmppMessage message) {
        L.d("ChatItemReceiveTextView message:", message.toString());
        if (XmppDbMessage.isGroupChatMessage(message)) {
//    		 TODO 在群里，extlocalDisplayname用来显示群成员的昵称了
            tv_name.setText(message.getExtLocalDisplayName());
        } else {
            tv_name.setVisibility(View.GONE);
        }

        try {
            String msgBody = SmileyParser.getInstance().addSmileySpans(message.getBody()).toString();
            if (msgBody.split("@")[0].equals(ConstDefine.location_pwd)) {

                jsonObject = new JSONObject(msgBody.split("@")[1]);
                tv_message.setText(jsonObject.getString("localName"));
                tv_street.setText(jsonObject.getString("street"));
                img_local.setImageResource(R.drawable.ic_default_location);
                img_local.setVisibility(View.VISIBLE);
                tv_street.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
        }
        lay_localname.setOnClickListener(this);
    }

    @LongClick(R.id.lay_localname)
    public void tv_message() {
        L.d(TAG, "tv_message()");
        initPopupWindow();
        showPopuWindow(lay_localname);
    }

    protected void initPopupWindow() {
        if (popWin == null) {
            popView = LayoutInflater.from(mContext).inflate(
                    R.layout.popup_list_item_long_click, null);
            MyPopuwinOnClickLisentener lisenter = new MyPopuwinOnClickLisentener();

            TextView tvCopy = (TextView) popView.findViewById(R.id.tvCopy);
            tvCopy.setVisibility(View.GONE);
            View line = popView.findViewById(R.id.line);
            line.setVisibility(View.GONE);

            TextView tvDel = (TextView) popView.findViewById(R.id.tvDel);
            tvDel.setOnClickListener(lisenter);

            popWin = new PopupWindow(popView,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            popWin.setOutsideTouchable(true);
            popView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    popWin.dismiss();
                    // todo
                }
            });
            // 如果不设置PopupWindow的背景，无论是点击外部区域还是Back键都无法dismiss弹框
            // 我觉得这里是API的一个bug
            popWin.setBackgroundDrawable(new BitmapDrawable());
        }
    }

    private class MyPopuwinOnClickLisentener implements OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {


                case R.id.tvDel:
                    messageDao.delete(message);
                    mContext.getContentResolver().notifyChange(
                            XmppMessageContentProvider.CONTENT_URI, null);
                    break;
            }
            popWin.dismiss();
        }
    }

    public void onClick(View view) {
        if (jsonObject != null) {

            try {
                Intent intent = new Intent(mContext, LocationActivity.class);
                intent.putExtra("latitude", jsonObject.getString("latitude"));
                intent.putExtra("longitude", jsonObject.getString("longitude"));
                intent.putExtra("localName", jsonObject.getString("localName"));
                mContext.startActivity(intent);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

}
