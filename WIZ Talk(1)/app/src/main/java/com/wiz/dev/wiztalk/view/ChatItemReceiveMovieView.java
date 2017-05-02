package com.wiz.dev.wiztalk.view;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.download.entities.FileInfo;
import com.download.services.DownloadService;
import com.download.services.FileDownloadCallBack;
import com.wiz.dev.wiztalk.DB.MsgInFo;
import com.wiz.dev.wiztalk.DB.XmppDbMessage;
import com.wiz.dev.wiztalk.DB.XmppMessage;
import com.wiz.dev.wiztalk.DB.XmppMessageContentProvider;
import com.wiz.dev.wiztalk.MyApplication;
import com.wiz.dev.wiztalk.R;
import com.wiz.dev.wiztalk.activity.MoviePlayerActivity;
import com.wiz.dev.wiztalk.activity.XchatActivity;
import com.wiz.dev.wiztalk.utils.MyImageLoader;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.LongClick;
import org.androidannotations.annotations.ViewById;

import java.io.File;


/**
 * 显示接收图片的view
 * Created by Dong on 2015/10/13.
 */
@EViewGroup(R.layout.item_chat_received_movie)
public class ChatItemReceiveMovieView extends ChatItemView {

    @ViewById
    public ImageView viv_image;

//    @ViewById
//    public ScalableVideoView video_view;

    @ViewById
    TextView tv_name;

    XchatActivity context;

    @ViewById
    View viv_lay;

    public String url;

    String jsonObject;
    @ViewById
    public RoundProgress roundProgress;

    public File file;
    public boolean isDowning;

    public ChatItemReceiveMovieView(XchatActivity context) {
        super(context);
        this.context = context;
    }

    @Override
    public void bind(XmppMessage message) {
        super.bind(message);
    }

    @Override
    public void bindOther(XmppMessage message) {
        if (XmppDbMessage.isGroupChatMessage(message)) {
            tv_name.setText(message.getExtLocalDisplayName());
        } else {
            tv_name.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(String.valueOf(message.getMold()))) {
            if (message.getMold() == MsgInFo.MOLD_MOVIE) {
                jsonObject = message.getFilePath();
                file = new File(Environment.getExternalStorageDirectory() + File.separator + "wizTalk/RecVideo/" + jsonObject.split("&")[0].substring(jsonObject.split("&")[0].lastIndexOf("/") + 1) + ".mp4");
                if (!file.exists()) {
                    viv_image.setVisibility(VISIBLE);

                    if(context.saveConfig.getStringConfig("httpConfig").equals("true")){
                        MyImageLoader.getInstance(3, MyImageLoader.Type.FIFO).loadImage(jsonObject.split("&")[0], viv_image,false);
                    }else
                        MyImageLoader.getInstance(3, MyImageLoader.Type.FIFO).loadImage(jsonObject.split("&")[1], viv_image,false);

                } else {
                    MyImageLoader.getInstance(3, MyImageLoader.Type.FIFO).loadImage(file.getAbsolutePath(), viv_image,false);
                }
            }
        }

    }


    @Click(R.id.viv_lay)
    void viv_image() {
        if (isDowning)
            return;



        if (!file.exists()) {

            if (MyApplication.getInstance().getDownloadManager().map.size() != 0) {
                Toast.makeText(context, "正在下载其他小视频", Toast.LENGTH_SHORT).show();
                return;
            }

            if(context.saveConfig.getStringConfig("httpConfig").equals("true")){
                url = jsonObject.split("&")[0];
            }else {
                url = jsonObject.split("&")[1];
            }

            FileInfo fileInfo = new FileInfo(message.getId(), url, file.getName(), 0, 0, new FileDownloadCallBack() {

                @Override
                public void onPregress(int progress) {
                    context.mHandler.obtainMessage(0x01, 0, progress, ChatItemReceiveMovieView.this).sendToTarget();
                }

                @Override
                public void onFinish(FileInfo fileInfo) {
                    context.mHandler.obtainMessage(0x02, ChatItemReceiveMovieView.this).sendToTarget();
                }

                @Override
                public void onStart() {
                    context.mHandler.obtainMessage(0x81, ChatItemReceiveMovieView.this).sendToTarget();
                }

                @Override
                public void onNetError() {
                    context.mHandler.obtainMessage(0x82).sendToTarget();
                }

            });
            MyApplication.getInstance().getDownloadManager().map.put(fileInfo.getId(), fileInfo);
            Intent intent = new Intent(mContext, DownloadService.class);
            intent.setAction(DownloadService.ACTION_START);
            intent.putExtra("fileId", fileInfo.getId());
            mContext.startService(intent);
        } else {
            Intent intent = new Intent(context, MoviePlayerActivity.class);
            intent.putExtra("path", file.getAbsolutePath());
            context.startActivity(intent);
//            XchatActivity.mXchatActivity.msg = message;
        }
    }

    @LongClick(R.id.viv_lay)
    public void ivPictureLongClick() {
        Log.d(TAG, "ivPictureLongClick()");
        initPopupWindow();
        showPopuWindow(viv_lay);
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
                    // todo
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
