package com.wiz.dev.wiztalk.view;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.epic.traverse.push.util.L;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.wiz.dev.wiztalk.DB.MsgInFo;
import com.wiz.dev.wiztalk.DB.XmppDbManager;
import com.wiz.dev.wiztalk.DB.XmppDbMessage;
import com.wiz.dev.wiztalk.DB.XmppMessage;
import com.wiz.dev.wiztalk.MyApplication;
import com.wiz.dev.wiztalk.R;
import com.wiz.dev.wiztalk.activity.XchatActivity;
import com.wiz.dev.wiztalk.dto.response.ResponseUpload;
import com.wiz.dev.wiztalk.service.writer.MsgService;
import com.wiz.dev.wiztalk.upload.UploadInfo;
import com.wiz.dev.wiztalk.upload.UploadManager;
import com.wiz.dev.wiztalk.utils.MessageUtils;
import com.wiz.dev.wiztalk.utils.Utils;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import java.lang.ref.WeakReference;

/**
 * 聊天发送项
 * Created by Dong on 2015/10/13.
 */
@EViewGroup
public class ChatItemSendView extends ChatItemView {

    public static final String TAG = ChatItemSendView.class.getSimpleName();

    @ViewById
    ImageView iv_fail_resend;//发送失败

    @ViewById
    TextView tv_send_status; //发送状态

    @ViewById
    RoundProgress progress_load;//发送进度条

    public ChatItemSendView(Context context) {
        super(context);
    }

    @Override
    public void bind(XmppMessage message) {
        L.d(TAG, "bind() message :" + message);
        super.bind(message);
//        progress_load.setVisibility(View.GONE);
        iv_fail_resend.setVisibility(View.GONE);
//		tv_send_progress.setVisibility(View.GONE);
        tv_send_status.setVisibility(View.INVISIBLE);
        if (message.getStatus() != null) {

            if (MsgInFo.STATUS_MEDIA_UPLOAD_PENDING == message.getStatus()
                    ||
                    MsgInFo.STATUS_MEDIA_UPLOAD_START == message.getStatus() ||
                    MsgInFo.STATUS_MEDIA_UPLOAD_LOADING == message.getStatus() ||
                    MsgInFo.STATUS_PENDING == message.getStatus() ||
                    MsgInFo.STATUS_SENDING == message.getStatus()
                    ) {
                progress_load.setVisibility(View.VISIBLE);
            } else if (MsgInFo.STATUS_MEDIA_UPLOAD_SUCCESS == message.getStatus() ||
                    MsgInFo.STATUS_SUCCESS == message.getStatus()
                    ) {
                progress_load.setVisibility(View.GONE);
//                tv_send_status.setText("发送成功");
                if (!XmppDbMessage.isGroupChatMessage(message) && !XchatActivity.isTypeApp) {
                    tv_send_status.setVisibility(View.VISIBLE);
                    if (message.getToReadStatus() == XmppDbMessage.READ_TRUE)
                        tv_send_status.setText("已读");
                    else
                        tv_send_status.setText("未读");
                }

            } else if (MsgInFo.STATUS_MEDIA_UPLOAD_FAILURE == message.getStatus() ||
                    MsgInFo.STATUS_ERROR == message.getStatus()
                    ) {
//               tv_send_status.setText("发送失败");
                iv_fail_resend.setVisibility(View.VISIBLE);
                tv_send_status.setVisibility(View.INVISIBLE);
                progress_load.setVisibility(View.GONE);
            }
        }
//        tv_send_status.setText("msg status : "+message.getStatus());
        bindUploadInfo(getContext());
    }

    public void bindUploadInfo(Context context) {
        Log.d(TAG, "bindUploadInfo() ");
        UploadManager uploadManager = MyApplication.getInstance().getUploadManager();
        UploadInfo uploadInfo = uploadManager.getUploadInfo(String.valueOf(message.getId().intValue()));
        if (uploadInfo != null) {
            this.update(uploadInfo);
//			v.refresh();
            HttpHandler<String> handler = uploadInfo.getHandler();
            if (handler != null) {
                RequestCallBack<String> callBack = handler.getRequestCallBack();
                if (callBack instanceof UploadManager.ManagerCallBack) {
                    callBack.setUserTag(new WeakReference<View>(this));
                }
            }
        }
    }

    private UploadInfo uploadInfo;

    public void update(UploadInfo uploadInfo) {
        this.uploadInfo = uploadInfo;
        refresh();
    }

    public void refresh() {
        Log.d(TAG, "refresh() ");
        if (uploadInfo.isUploading()) {
            progress_load.setVisibility(View.VISIBLE);
//			tv_send_progress.setVisibility(View.VISIBLE);
            int progress = (int) (uploadInfo.getProgress() * 100 / uploadInfo.getFileLength());
            L.d(TAG, " progress :" + progress);
//			tv_send_progress.setText(progress + "%");
            progress_load.setProgress(progress);
            XmppDbManager.getInstance(getContext()).updateWithNotify(message);
        } else {
            progress_load.setVisibility(View.GONE);
//			tv_send_progress.setVisibility(View.GONE);
        }
    }

    @Override
    public void bindOther(XmppMessage message) {
    }


}
