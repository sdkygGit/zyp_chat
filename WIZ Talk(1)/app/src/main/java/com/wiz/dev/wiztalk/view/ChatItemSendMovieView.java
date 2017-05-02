package com.wiz.dev.wiztalk.view;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.volokh.danylo.video_player_manager.ui.MediaPlayerWrapper;
import com.wiz.dev.wiztalk.DB.MsgInFo;
import com.wiz.dev.wiztalk.DB.XmppMessage;
import com.wiz.dev.wiztalk.DB.XmppMessageContentProvider;
import com.wiz.dev.wiztalk.R;
import com.wiz.dev.wiztalk.activity.MoviePlayerActivity;
import com.wiz.dev.wiztalk.activity.XchatActivity;
import com.wiz.dev.wiztalk.utils.MessageUtils;
import com.wiz.dev.wiztalk.utils.MyImageLoader;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.LongClick;
import org.androidannotations.annotations.ViewById;

import java.io.File;

/**
 * Created by Dong on 2015/10/13.
 */
@EViewGroup(R.layout.item_chat_sent_movie)
public class ChatItemSendMovieView extends ChatItemSendView implements MediaPlayerWrapper.MainThreadMediaPlayerListener {

    @ViewById
    ImageView viv_image;

    XchatActivity context;

    @ViewById
    View pic_lay;

    String filepath;

    public ChatItemSendMovieView(XchatActivity context) {
        super(context);
        this.context = context;
    }

    @Override
    public void bindOther(XmppMessage message) {
        if (!TextUtils.isEmpty(String.valueOf(message.getMold()))) {
            if (message.getMold() == MsgInFo.MOLD_MOVIE) {
                filepath = message.getFilePath();
                if (!TextUtils.isEmpty(message.getExtInternalFilePath())) {
                    filepath = message.getExtInternalFilePath();
                }

                MyImageLoader.getInstance(3, MyImageLoader.Type.FIFO).loadImage(filepath, viv_image,false);
                if (message.getStatus() != null) {
                    if (MsgInFo.STATUS_MEDIA_UPLOAD_PENDING == message.getStatus()||
                            MsgInFo.STATUS_MEDIA_UPLOAD_START == message.getStatus() ||
                            MsgInFo.STATUS_MEDIA_UPLOAD_LOADING == message.getStatus() ||
                            MsgInFo.STATUS_PENDING == message.getStatus() 
                            ) {
                        viv_image.setImageDrawable(new ColorDrawable(0));
                    } else if (MsgInFo.STATUS_MEDIA_UPLOAD_SUCCESS == message.getStatus() ||
                            MsgInFo.STATUS_SUCCESS == message.getStatus()
                            ) {
                        viv_image.setImageResource(R.mipmap.mfc);
                    } else if (MsgInFo.STATUS_MEDIA_UPLOAD_FAILURE == message.getStatus() ||
                            MsgInFo.STATUS_ERROR == message.getStatus()
                            ) {
                        viv_image.setImageResource(R.mipmap.mfc);
                    }
                }
            }
        }
    }


    @Click(R.id.pic_lay)
    void viv_movie() {

        if (new File(filepath).exists()) {
            Intent intent = new Intent(context, MoviePlayerActivity.class);
//        Intent intent = new Intent(context, MediaPlayerActivity.class);
            intent.putExtra("path", filepath);
            context.startActivity(intent);
        } else {
            Toast.makeText(context, "文件已被删除", Toast.LENGTH_SHORT).show();
        }
    }

    @LongClick(R.id.pic_lay)
    void ivPictureLongClick() {
        Log.d(TAG, "ivPictureLongClick()");
        initPopupWindow();
        showPopuWindow(pic_lay);
    }

    protected void initPopupWindow() {
        if (popWin == null) {
            popView = LayoutInflater.from(mContext).inflate(R.layout.popup_list_item_long_click, null);
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
                }
            });
            // 如果不设置PopupWindow的背景，无论是点击外部区域还是Back键都无法dismiss弹框
            // 我觉得这里是API的一个bug
            popWin.setBackgroundDrawable(new BitmapDrawable());
        }
    }

    @Override
    public void onVideoSizeChangedMainThread(int width, int height) {
    }

    @Override
    public void onVideoPreparedMainThread() {
        viv_image.setVisibility(INVISIBLE);
    }


    @Override
    public void onVideoCompletionMainThread() {
//        context.mVideoPlayerManager.playNewVideo(null,video_player,filepath);
//        viv_image.setVisibility(VISIBLE);
//        Toast.makeText(context,"完成",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onErrorMainThread(int what, int extra) {

    }

    @Override
    public void onBufferingUpdateMainThread(int percent) {

    }

    @Override
    public void onVideoStoppedMainThread() {
        viv_image.setVisibility(VISIBLE);
    }

    private class MyPopuwinOnClickLisentener implements OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tvCopy:
                    context.sendMovieMsg(message.getVoiceLength(), filepath);
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

    @Click
    void iv_fail_resend(View view) {
        iv_fail_resend.setVisibility(View.GONE);
        MessageUtils.sendMediaMsg(context, message);
    }
}
