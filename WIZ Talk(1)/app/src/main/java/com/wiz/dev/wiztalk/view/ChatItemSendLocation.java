package com.wiz.dev.wiztalk.view;

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

import com.epic.traverse.push.util.L;
import com.wiz.dev.wiztalk.DB.MsgInFo;
import com.wiz.dev.wiztalk.DB.XmppMessage;
import com.wiz.dev.wiztalk.DB.XmppMessageContentProvider;
import com.wiz.dev.wiztalk.R;
import com.wiz.dev.wiztalk.activity.XchatActivity;
import com.wiz.dev.wiztalk.public_store.ConstDefine;
import com.wiz.dev.wiztalk.service.writer.MsgService;
import com.wiz.dev.wiztalk.utils.SmileyParser;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.LongClick;
import org.androidannotations.annotations.ViewById;
import org.json.JSONException;
import org.json.JSONObject;

import map.LocationActivity;

/**
 * Created by Dong on 2015/10/13.
 */
@EViewGroup(R.layout.item_chat_sent_location)
public class ChatItemSendLocation extends ChatItemSendView implements View.OnClickListener {

    @ViewById
    TextView tv_message;//文本

    XchatActivity mContext;

    @ViewById
    ImageView img_local;


    @ViewById
    LinearLayout lay_localname;

    JSONObject jsonObject;

    @ViewById
    TextView tv_street;


    public ChatItemSendLocation(XchatActivity context) {
        super(context);
        this.mContext = context;
    }

    @Override
    public void bindOther(XmppMessage message) {
        super.bindOther(message);

        try {
            String msgBody = SmileyParser.getInstance().addSmileySpans(message.getBody()).toString();
            if (msgBody.split("@").length == 2 && msgBody.split("@")[0].equals(ConstDefine.location_pwd)) {

                jsonObject = new JSONObject(msgBody.split("@")[1]);
                tv_message.setText(jsonObject.getString("localName"));
                tv_street.setText(jsonObject.getString("street"));
                img_local.setVisibility(View.VISIBLE);
                img_local.setImageResource(R.drawable.ic_default_location);
                tv_street.setVisibility(View.VISIBLE);

            }
        } catch (Exception e) {
        }
        lay_localname.setOnClickListener(this);
    }

    @LongClick(R.id.lay_localname)
    void tvMessageLongClick() {
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

            tvCopy.setText("重发");

            tvCopy.setOnClickListener(lisenter);
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
                case R.id.tvCopy:
                    try {
                        JSONObject o = new JSONObject();
                        o.put("latitude", jsonObject.getString("latitude"));
                        o.put("longitude", jsonObject.getString("longitude"));
                        o.put("localName", jsonObject.getString("localName"));
                        mContext.sendLocation(ConstDefine.location_pwd + "@" + o.toString(), MsgInFo.MOLD_LOCATION);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
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

    @Click
    void iv_fail_resend(View view) {
        iv_fail_resend.setVisibility(View.GONE);
        JSONObject o = new JSONObject();
        try {
            o.put("latitude", jsonObject.getString("latitude"));
            o.put("longitude", jsonObject.getString("longitude"));
            o.put("localName", jsonObject.getString("localName"));

            MsgService.getMsgWriter(mContext).sendMsg(message);
//            XchatActivity.mXchatActivity.sendLocation(ConstDefine.location_pwd + "@" + o.toString(), MsgInFo.MOLD_LOCATION);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
