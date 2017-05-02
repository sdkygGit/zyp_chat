package com.wiz.dev.wiztalk.view;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.epic.traverse.push.util.L;
import com.wiz.dev.wiztalk.DB.MsgInFo;
import com.wiz.dev.wiztalk.DB.VoiceDb;
import com.wiz.dev.wiztalk.DB.XmppMessage;
import com.wiz.dev.wiztalk.DB.XmppMessageContentProvider;
import com.wiz.dev.wiztalk.MyApplication;
import com.wiz.dev.wiztalk.R;
import com.wiz.dev.wiztalk.activity.XchatActivity;
import com.wiz.dev.wiztalk.utils.MessageUtils;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.LongClick;
import org.androidannotations.annotations.ViewById;

/**
 * Created by Dong on 2015/10/13.
 */
@EViewGroup(R.layout.item_chat_sent_voice)
public class ChatItemSendVoiceView extends ChatItemSendView implements View.OnClickListener {

    //语音最外层
    @ViewById
    LinearLayout layout_voice;
    @ViewById
    TextView tv_voice_length;//语音时长

    private int mMinItemWidth;
    private int mMaxItemWidth;

    @ViewById
    public ImageView pointIv;

    SQLiteDatabase sd;


    @ViewById
    public ImageView iv_voice;// 动画

    public String filePath;

    XchatActivity mContext;

    public ChatItemSendVoiceView(XchatActivity context) {
        super(context);
        //获取屏幕的宽度
        mContext = context;
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);

        mMaxItemWidth = (int) (outMetrics.widthPixels * 0.7f);
        mMinItemWidth = (int) (outMetrics.widthPixels * 0.2f);

    }


    @Override
    public void bindOther(XmppMessage message) {
        if (!TextUtils.isEmpty(String.valueOf(message.getMold()))) {
            if (message.getMold() == MsgInFo.MOLD_VOICE) {
                setVoice(message.getVoiceLength(), message.getExtInternalFilePath());
            }
        }
    }

    void setVoice(int length, String path) {
        //语音时长
        tv_voice_length.setText(length + "\"");

        ViewGroup.LayoutParams lp = layout_voice.getLayoutParams();

        int lengthWidth = (int) (mMinItemWidth + (mMaxItemWidth / 60f) * length);

        int widthM = Math.min(lengthWidth, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 220, getResources().getDisplayMetrics()));

        lp.width = Math.max(widthM, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, getResources().getDisplayMetrics()));



        // 设置播放动画
        //设置播放动画
        sd = new VoiceDb(MyApplication.getInstance()).getWritableDatabase();
        Cursor cursor = sd.query("voice", null, "filePath=?", new String[]{path}, null, null, null);
        if (cursor.moveToFirst()) {
            String type = cursor.getString(2);
            if (type.equals(VoiceDb.SEND_NO_READ)) {
                pointIv.setVisibility(View.VISIBLE);
                MediaManager.currentChatItemSendVoiceViews.add(ChatItemSendVoiceView.this);
            } else if (type.equals(VoiceDb.SEND_READ)) {
                pointIv.setVisibility(View.INVISIBLE);
            }
        }
        //发送语音提示，连续播放，取消此注释即可
//        else{
//            pointIv.setVisibility(View.VISIBLE);
//            MediaManager.currentChatItemSendVoiceViews.add(ChatItemSendVoiceView.this);
//            sd.execSQL("INSERT INTO voice('filePath','type') VALUES ('" + path + "','" + VoiceDb.SEND_NO_READ + "');");
//        }
        cursor.close();
        sd.close();
        this.filePath = path;
        layout_voice.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (MediaManager.isStarting()) {
            if (MediaManager.currentPath == filePath) {
                MediaManager.release();
            } else {
                MediaManager.reset();
                playerVoiceSend(view, filePath);
            }
        } else {
            playerVoiceSend(view, filePath);
        }
    }

    private void playerVoiceSend(View view, String filePath) {
        if (iv_voice != null) {
            iv_voice.setBackgroundResource(R.drawable.voice_left3);
        }
        MediaManager.isLeft = false;
        MediaManager.itemViewSend = this;
        //播放音频
        MediaManager.playSound(filePath);
    }

    @LongClick(R.id.layout_voice)
    void ivPictureLongClick() {
        L.d(TAG, "layout_voice()");
        initPopupWindow();
        showPopuWindow(layout_voice);
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
                    if (message != null)
                        mContext.sendVoiceMsg(message.getVoiceLength(), message.getExtInternalFilePath());

                    break;
                case R.id.tvDel:
                    messageDao.delete(message);
                    mContext.getContentResolver().notifyChange(
                            XmppMessageContentProvider.CONTENT_URI, null);
                    break;
                default:
                    break;
            }
            popWin.dismiss();
        }

    }

    @Click
    void iv_fail_resend(View view){
        iv_fail_resend.setVisibility(View.GONE);
//        if (message != null)
            MessageUtils.sendMediaMsg(mContext, message);
//        message.setStatus(MsgInFo.STATUS_MEDIA_UPLOAD_PENDING);
//        sendMsg();
    }
}
