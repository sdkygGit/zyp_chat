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
import com.wiz.dev.wiztalk.activity.FileDetialActivity;
import com.wiz.dev.wiztalk.public_store.ConstDefine;
import com.wiz.dev.wiztalk.utils.SmileyParser;
import com.wiz.dev.wiztalk.utils.Utils;

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
@EViewGroup(R.layout.item_chat_received_file)
public class ChatItemReceiveFile extends ChatItemView implements View.OnClickListener {

    @ViewById
    TextView tv_message;

    @ViewById
    TextView tv_name;//昵称

    @ViewById
    ImageView img_file;

    @ViewById
    LinearLayout lay_localname;

    @ViewById
    TextView tv_size;


    public ChatItemReceiveFile(Context context) {
        super(context);
    }

    @Override
    public void bindOther(XmppMessage message) {
        L.d("ChatItemReceiveTextView message:", message.toString());
        if (XmppDbMessage.isGroupChatMessage(message)) {
//    		 TODO 在群里，extlocalDisplayname用来显示群成员的昵称了
//         	tv_name.setText(message.getExtRemoteDisplayName());
            tv_name.setText(message.getExtLocalDisplayName());
        } else {
            tv_name.setVisibility(View.GONE);
        }

        String sub_ext =null;

        try {
            JSONObject jsonObject = new JSONObject(SmileyParser.getInstance().addSmileySpans(message.getBody()).toString());
            sub_ext = jsonObject.getString("filename");
            tv_message .setText(sub_ext);
            sub_ext = sub_ext.substring(sub_ext.lastIndexOf(".") + 1);

            if (sub_ext.equalsIgnoreCase("pdf")) {
                img_file.setImageResource(R.drawable.pdf);

            } else if (sub_ext.equalsIgnoreCase("mp3") || sub_ext.equalsIgnoreCase("wma")
                    || sub_ext.equalsIgnoreCase("m4a") || sub_ext.equalsIgnoreCase("m4p")
                    ||sub_ext.equalsIgnoreCase("amr")) {
                img_file.setImageResource(R.drawable.ic_select_file_music);
            } else if (sub_ext.equalsIgnoreCase("png") || sub_ext.equalsIgnoreCase("jpg")
                    || sub_ext.equalsIgnoreCase("jpeg") || sub_ext.equalsIgnoreCase("gif")
                    || sub_ext.equalsIgnoreCase("tiff")) {
                img_file.setImageResource(R.drawable.image);
            } else if (sub_ext.equalsIgnoreCase("zip") || sub_ext.equalsIgnoreCase("gzip")
                    || sub_ext.equalsIgnoreCase("gz")) {
                img_file.setImageResource(R.drawable.zip);
            } else if (sub_ext.equalsIgnoreCase("m4v") || sub_ext.equalsIgnoreCase("wmv")
                    || sub_ext.equalsIgnoreCase("3gp") || sub_ext.equalsIgnoreCase("mp4")) {
                img_file.setImageResource(R.drawable.movies);
            } else if (sub_ext.equalsIgnoreCase("doc") || sub_ext.equalsIgnoreCase("docx")) {
                img_file.setImageResource(R.drawable.word);
            } else if (sub_ext.equalsIgnoreCase("xls") || sub_ext.equalsIgnoreCase("xlsx")) {
                img_file.setImageResource(R.drawable.excel);
            } else if (sub_ext.equalsIgnoreCase("ppt") || sub_ext.equalsIgnoreCase("pptx")) {
                img_file.setImageResource(R.drawable.ppt);
            } else if (sub_ext.equalsIgnoreCase("html")) {
                img_file.setImageResource(R.drawable.html32);
            } else if (sub_ext.equalsIgnoreCase("xml")) {
                img_file.setImageResource(R.drawable.xml32);
            } else if (sub_ext.equalsIgnoreCase("conf")) {
                img_file.setImageResource(R.drawable.config32);
            } else if (sub_ext.equalsIgnoreCase("apk")) {
                img_file.setImageResource(R.drawable.appicon);
            } else if (sub_ext.equalsIgnoreCase("jar")) {
                img_file.setImageResource(R.drawable.jar32);
            } else {
                img_file.setImageResource(R.drawable.text);
            }
            tv_size.setText(Utils.getFileSizeFromLength(jsonObject.getInt("filesize")));
        } catch (JSONException e) {
            e.printStackTrace();
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
        Intent intent = new Intent(mContext, FileDetialActivity.class);
        intent.putExtra("path",message.getFilePath());
        intent.putExtra("file_name",tv_message.getText().toString());
        intent.putExtra("file_size",tv_size.getText().toString());
        intent.putExtra("fileId",message.getId());
        mContext.startActivity(intent);
    }
}
