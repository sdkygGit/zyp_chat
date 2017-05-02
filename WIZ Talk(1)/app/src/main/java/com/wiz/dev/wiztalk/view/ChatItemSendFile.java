package com.wiz.dev.wiztalk.view;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.epic.traverse.push.util.L;
import com.wiz.dev.wiztalk.DB.MsgInFo;
import com.wiz.dev.wiztalk.DB.XmppMessage;
import com.wiz.dev.wiztalk.DB.XmppMessageContentProvider;
import com.wiz.dev.wiztalk.R;
import com.wiz.dev.wiztalk.activity.XchatActivity;
import com.wiz.dev.wiztalk.public_store.ConstDefine;
import com.wiz.dev.wiztalk.service.writer.MsgService;
import com.wiz.dev.wiztalk.utils.MessageUtils;
import com.wiz.dev.wiztalk.utils.SmileyParser;
import com.wiz.dev.wiztalk.utils.Utils;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.LongClick;
import org.androidannotations.annotations.ViewById;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import map.LocationActivity;

/**
 * Created by Dong on 2015/10/13.
 */
@EViewGroup(R.layout.item_chat_sent_file)
public class ChatItemSendFile extends ChatItemSendView implements View.OnClickListener {

    @ViewById
    TextView tv_message;//文本

    XchatActivity mContext;

    @ViewById
    ImageView img_file;


    @ViewById
    LinearLayout lay_localname;

    @ViewById
    TextView tv_size;

    File file;

    public ChatItemSendFile(XchatActivity context) {
        super(context);
        this.mContext = context;
    }

    @Override
    public void bindOther(XmppMessage message) {
        if (!TextUtils.isEmpty(String.valueOf(message.getMold()))) {
            if (message.getMold() == MsgInFo.MOLD_FILE) {
                String filepath;
                filepath = message.getFilePath();
                if (!TextUtils.isEmpty(message.getExtInternalFilePath())) {
                    filepath = message.getExtInternalFilePath();
                }
                file = new File(filepath);
                tv_message.setText(file.getName());


                String sub_ext = file.getName();

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
                tv_size.setText(Utils.getFileSizeFromLength(message.getVoiceLength()));
            }
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
                    mContext.sendFileMsg(message.getVoiceLength(), file.getAbsolutePath());
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
        String item_ext = file.getName();
        item_ext = item_ext.substring(item_ext.lastIndexOf(".") + 1);
        if (item_ext.equalsIgnoreCase("mp3") ||
                item_ext.equalsIgnoreCase("m4a") ||
                item_ext.equalsIgnoreCase("mp4")||
                item_ext.equalsIgnoreCase("amr")) {

            Intent i = new Intent();
            i.setAction(Intent.ACTION_VIEW);
            i.setDataAndType(Uri.fromFile(file), "audio/*");
            mContext.startActivity(i);
        }

	    	/*photo file selected*/
        else if (item_ext.equalsIgnoreCase("jpeg") ||
                item_ext.equalsIgnoreCase("jpg") ||
                item_ext.equalsIgnoreCase("png") ||
                item_ext.equalsIgnoreCase("gif") ||
                item_ext.equalsIgnoreCase("tiff")) {

            if (file.exists()) {
                Intent picIntent = new Intent();
                picIntent.setAction(Intent.ACTION_VIEW);
                picIntent.setDataAndType(Uri.fromFile(file), "image/*");
                mContext.startActivity(picIntent);
            }
        }

	    	/*video file selected--add more video formats*/
        else if (item_ext.equalsIgnoreCase("m4v") ||
                item_ext.equalsIgnoreCase("3gp") ||
                item_ext.equalsIgnoreCase("wmv") ||
                item_ext.equalsIgnoreCase("mp4") ||
                item_ext.equalsIgnoreCase("ogg") ||
                item_ext.equalsIgnoreCase("wav")) {

            if (file.exists()) {
                Intent movieIntent = new Intent();
                movieIntent.setAction(Intent.ACTION_VIEW);
                movieIntent.setDataAndType(Uri.fromFile(file), "video/*");
                mContext.startActivity(movieIntent);
            }
        }

	    	/*zip file */
        else if (item_ext.equalsIgnoreCase("zip")) {

        }

	    	/* gzip files, this will be implemented later */
        else if (item_ext.equalsIgnoreCase("gzip") ||
                item_ext.equalsIgnoreCase("gz")) {


        }
            /*pdf file selected*/
        else if (item_ext.equalsIgnoreCase("pdf")) {

            if (file.exists()) {

                Intent pdfIntent = new Intent();
                pdfIntent.setAction(Intent.ACTION_VIEW);
                pdfIntent.setDataAndType(Uri.fromFile(file),
                        "application/pdf");
                try {
                    mContext.startActivity(pdfIntent);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(mContext, "不能打开该文件",
                            Toast.LENGTH_SHORT).show();
                }

            }
        }

	    	/*Android application file*/
        else if (item_ext.equalsIgnoreCase("apk")) {

            if (file.exists()) {
                Intent apkIntent = new Intent();
                apkIntent.setAction(Intent.ACTION_VIEW);
                apkIntent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
                mContext.startActivity(apkIntent);
            }
        }

	    	/* HTML file */
        else if (item_ext.equalsIgnoreCase("html")) {

            if (file.exists()) {

                Intent htmlIntent = new Intent();
                htmlIntent.setAction(Intent.ACTION_VIEW);
                htmlIntent.setDataAndType(Uri.fromFile(file), "text/html");

                try {
                    mContext.startActivity(htmlIntent);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(mContext, "不能打开该文件",
                            Toast.LENGTH_SHORT).show();
                }

            }
        }

	    	/* text file*/
        else if (item_ext.equalsIgnoreCase("txt")) {

            if (file.exists()) {

                Intent txtIntent = new Intent();
                txtIntent.setAction(Intent.ACTION_VIEW);
                txtIntent.setDataAndType(Uri.fromFile(file), "text/plain");

                try {
                    mContext.startActivity(txtIntent);
                } catch (ActivityNotFoundException e) {
                    txtIntent.setType("text/*");
                    mContext.startActivity(txtIntent);
                }

            }
        }

	    	/* generic intent */
        else {
            if (file.exists()) {

                Intent generic = new Intent();
                generic.setAction(Intent.ACTION_VIEW);
                generic.setDataAndType(Uri.fromFile(file), "text/plain");

                try {
                    mContext.startActivity(generic);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(mContext, "不能打开该文件" + file.getName(),
                            Toast.LENGTH_SHORT).show();
                }

            }
        }
    }

    @Click
    void iv_fail_resend(View view) {
        iv_fail_resend.setVisibility(View.GONE);
        MessageUtils.sendMediaMsg(mContext, message);
    }

}
