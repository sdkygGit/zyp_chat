package com.wiz.dev.wiztalk.view;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.BitmapDrawable;
import android.os.Environment;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.wiz.dev.wiztalk.DB.MsgInFo;
import com.wiz.dev.wiztalk.DB.VoiceDb;
import com.wiz.dev.wiztalk.DB.XmppDbMessage;
import com.wiz.dev.wiztalk.DB.XmppMessage;
import com.wiz.dev.wiztalk.DB.XmppMessageContentProvider;
import com.wiz.dev.wiztalk.MyApplication;
import com.wiz.dev.wiztalk.R;
import com.wiz.dev.wiztalk.activity.XchatActivity;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.LongClick;
import org.androidannotations.annotations.ViewById;

import java.io.File;

import cn.finalteam.filedownloaderfinal.SimpleFileDownloader;
import cn.finalteam.okhttpfinal.FileDownloadCallback;
import cn.finalteam.okhttpfinal.HttpRequest;


/**
 * Created by Dong on 2015/10/13.
 */
@EViewGroup(R.layout.item_chat_received_voice)
public class ChatItemReceiveVoiceView extends ChatItemView implements View.OnClickListener {

    // 语音最外层
    @ViewById
    LinearLayout layout_voice;
    @ViewById
    TextView tv_voice_length;// 语音时长

    @ViewById
    TextView tv_name;// 昵称

//	@ViewById
//	ImageView iv_voice_dot;

    @ViewById
    public ImageView pointIv;

    SQLiteDatabase sd;

    @ViewById
    public ImageView iv_voice;// 动画

    public String filePath;

    File file;

    private int mMinItemWidth;
    private int mMaxItemWidth;


    XchatActivity context;

    public ChatItemReceiveVoiceView(XchatActivity context) {
        super(context);
        // 获取屏幕的宽度
        this.context = context;
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);

        mMaxItemWidth = (int) (outMetrics.widthPixels * 0.7f);
        mMinItemWidth = (int) (outMetrics.widthPixels * 0.2f);
    }

    @Override
    public void bindOther(XmppMessage message) {
        if (XmppDbMessage.isGroupChatMessage(message)) {
//    		 TODO 显示名称
//         	tv_name.setText(message.getExtRemoteDisplayName());
            tv_name.setText(message.getExtLocalDisplayName());
        } else {
            tv_name.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(String.valueOf(message.getMold()))) {
            if (message.getMold() == MsgInFo.MOLD_VOICE) {
                if (message.getVoiceLength() != null) {
                    setVoice(message.getVoiceLength(), message.getFilePath(), message.getCreateTime());
                }
            }
        }
    }

    @LongClick(R.id.layout_voice)
    public void layoutVoice() {
        Log.d(TAG, "layout_voice()");
        initPopupWindow();
        showPopuWindow(layout_voice);
    }

    void setVoice(int length, String fileUrl, long createTime) {
        filePath = fileUrl;
        if (message.getRecVoiceReadStatus() == MsgInFo.READ_FALSE) {
            pointIv.setVisibility(View.VISIBLE);
            MediaManager.currentChatItemReceiveVoiceViews.add(ChatItemReceiveVoiceView.this);
        } else if (message.getRecVoiceReadStatus() == MsgInFo.READ_TRUE) {
            pointIv.setVisibility(View.INVISIBLE);
        }

        // 语音时长 ,需要来个向上取整
        length = length < 1 ? 1 : length;
        tv_voice_length.setText(length + "\"");
        // tv_voice_length.setText(fileUrl);

        ViewGroup.LayoutParams lp = layout_voice.getLayoutParams();

        int lengthWidth = (int) (mMinItemWidth + (mMaxItemWidth / 60f) * length);
        int widthM = Math.min(lengthWidth, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 220, getResources().getDisplayMetrics()));

        lp.width = Math.max(widthM, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, getResources().getDisplayMetrics()));

        layout_voice.setOnClickListener(this);
    }

    /**
     * 播放 接收voice的动画
     *
     * @param filePath
     */
    private void playerVoiceRece(String filePath) {
        if (iv_voice != null) {
            iv_voice.setBackgroundResource(R.drawable.voice_right3);
        }
        MediaManager.isLeft = true;
        MediaManager.itemViewRec = this;
        // 播放音频
        MediaManager.playSound(filePath);
    }


    @Override
    public void onClick(View view) {
        file = new File(Environment.getExternalStorageDirectory() + File.separator + "wizTalk/RecVideo/" + filePath.split("&")[0].substring(filePath.split("&")[0].lastIndexOf("/") + 1) + ".amr");
        if (!file.exists()) {
            String url;
            if (context.saveConfig.getStringConfig("httpConfig").equals("true")) {
                url = filePath.split("&")[0];
            } else {
                url = filePath.split("&")[1];
            }

            HttpRequest.download(url, file, new FileDownloadCallback() {

                public void onFailure() {
                    Toast.makeText(context, "请检查网络", Toast.LENGTH_SHORT).show();
                }

                public void onDone() {
                    playerVoiceRece(file.getAbsolutePath());
                }
            });
        } else {
            if (MediaManager.isStarting()) {
                if (MediaManager.currentPath == file.getAbsolutePath()) {
                    MediaManager.release();
                } else {
                    MediaManager.reset();
                    playerVoiceRece(file.getAbsolutePath());
                }
            } else {
                playerVoiceRece(file.getAbsolutePath());
            }
        }
//
    }

    protected View popView;

    protected void initPopupWindow() {
        if (popWin == null) {
            popView = LayoutInflater.from(mContext).inflate(
                    R.layout.popup_list_item_long_click, null);

            View copyLay = (View) popView.findViewById(R.id.tvCopy);
            copyLay.setVisibility(View.GONE);
            View line = (View) popView.findViewById(R.id.line);
            line.setVisibility(View.GONE);

            TextView tvDel = (TextView) popView.findViewById(R.id.tvDel);
            tvDel.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    messageDao.delete(message);
                    mContext.getContentResolver().notifyChange(
                            XmppMessageContentProvider.CONTENT_URI, null);
                    popWin.dismiss();
                }
            });

            popWin = new PopupWindow(popView,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            popWin.setOutsideTouchable(true);
            popView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    popWin.dismiss();
                }
            });
            // 如果不设置PopupWindow的背景，无论是点击外部区域还是Back键都无法dismiss弹框
            // 我觉得这里是API的一个bug
            popWin.setBackgroundDrawable(new BitmapDrawable());
        }
    }

    protected void showPopuWindow(View iv) {
        popView.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
        int popupWidth = popView.getMeasuredWidth();
        int popupHeight = popView.getMeasuredHeight();
        int[] location = new int[2];
        iv.getLocationOnScreen(location);
        popWin.showAtLocation(iv, Gravity.NO_GRAVITY,
                (location[0] + iv.getWidth() / 2) - popupWidth / 2, location[1]
                        - popupHeight);
    }
}
