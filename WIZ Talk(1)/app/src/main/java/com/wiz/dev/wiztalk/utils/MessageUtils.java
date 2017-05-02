package com.wiz.dev.wiztalk.utils;

import android.content.ContentUris;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.epic.traverse.push.util.L;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.wiz.dev.wiztalk.DB.MsgInFo;
import com.wiz.dev.wiztalk.DB.XmppDbManager;
import com.wiz.dev.wiztalk.DB.XmppMessage;
import com.wiz.dev.wiztalk.DB.XmppMessageContentProvider;
import com.wiz.dev.wiztalk.MyApplication;
import com.wiz.dev.wiztalk.dto.response.ResponseUpload;
import com.wiz.dev.wiztalk.service.writer.MsgService;
import com.wiz.dev.wiztalk.view.ChatItemSendImageView;
import com.wiz.dev.wiztalk.view.ChatItemSendVoiceView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.ref.WeakReference;


public class MessageUtils {

    private static final String TAG = "MessageUtils";

    public static void sendMediaMsg(final Context context, final XmppMessage message) {
        message.setToReadStatus(MsgInFo.READ_FALSE);
        message.setRecVoiceReadStatus(MsgInFo.READ_FALSE);
        // TODO: 2015/11/24  
        Long messageId = message.getId();
        message.setStatus(MsgInFo.STATUS_MEDIA_UPLOAD_PENDING);
        if (messageId == null || messageId <= 0) {
            messageId = XmppDbManager.getInstance(context).insertMessage(message);
        }

        String filePath = null;

        String js = Utils.getURLFileServer(context, Utils.URL_FILE_XX);
        if (js != null) {
            try {
                JSONObject jsonObject = new JSONObject(js);
                String path;
                if (jsonObject.getString("httpConfig").equals("true")) {
                    path = "http://" + jsonObject.getString("url") + "/" + jsonObject.getString("fid");
                } else {
                    path = "http://" + jsonObject.getString("publicUrl") + "/" + jsonObject.getString("fid");
                }

                UploadRequestCallBack callback = new UploadRequestCallBack(context,
                        messageId, message, jsonObject);


                filePath = message.getFilePath();
                if (MsgInFo.MOLD_IMG == message.getMold()) {
                    if (filePath.contains("file://")) {
                        filePath = filePath.replace("file://", "");
                    }
                }

                MyApplication
                        .getInstance()
                        .getUploadManager()
                        .addNewUpload(String.valueOf(messageId), path, filePath,
                                filePath, callback);

            } catch (Exception e) {
                e.printStackTrace();
                message.setStatus(MsgInFo.STATUS_MEDIA_UPLOAD_FAILURE);
                XmppDbManager.getInstance(context).updateWithNotify(message);
            }

        }

        // TODO: 2015/11/24
//        String url = Utils.getURLFileServer(context, Utils.URL_FILE_XX);
        //现在是本地的地址
//        filePath = message.getFilePath();
//
//        if (MsgInFo.MOLD_IMG == message.getMold()) {
//            url = Utils.getURLFileServer(context, Utils.URL_FILE_IMAGE);
//            if (filePath.contains("file://")) {
//                filePath = filePath.replace("file://", "");
//            }
//        } else if (MsgInFo.MOLD_VOICE == message.getMold()) {
//            url = Utils.getURLFileServer(context, Utils.URL_FILE_VOICE);
//        } else if (MsgInFo.MOLD_MOVIE == message.getMold()) {
//            url = Utils.getURLFileServer(context, Utils.URL_FILE_VOICE);
//        } else if (MsgInFo.MOLD_FILE == message.getMold()) {
//            url = Utils.getURLFileServer(context, Utils.URL_FILE_VOICE);
//        }
//
//        UploadRequestCallBack callback = new UploadRequestCallBack(context,
//                messageId, message);
//
//        MyApplication
//                .getInstance()
//                .getUploadManager()
//                .addNewUpload(String.valueOf(messageId), url, filePath,
//                        filePath, callback);
    }

    public static class UploadRequestCallBack extends RequestCallBack<String> {

        private Context context;
        private long messageId;
        private XmppMessage message;
        JSONObject js;

        public UploadRequestCallBack(Context context, long messageId,
                                     XmppMessage message, JSONObject js) {
            this.context = context;
            this.messageId = messageId;
            this.message = message;
            this.js = js;
        }

        @Override
        public void onSuccess(ResponseInfo<String> responseInfo) {
            refreshListItem();
//            ResponseUpload responseUpload = Utils.readValue(responseInfo.result, ResponseUpload.class);

//            if (message.getMold() == MsgInFo.MOLD_IMG) {
//                message.setFilePath("http://" + responseUpload.fileUrl);
//            } else if (message.getMold() == MsgInFo.MOLD_VOICE) {
//                message.setFilePath("http://" + responseUpload.fileUrl);
//            } else if (message.getMold() == MsgInFo.MOLD_MOVIE) {
//                message.setFilePath("http://" + responseUpload.fileUrl);
//            } else if (message.getMold() == MsgInFo.MOLD_FILE) {
//                message.setFilePath("http://" + responseUpload.fileUrl);
//            }


            try {
                String path = "http://" + js.getString("url") + "/" + js.getString("fid");
                String path2 = "http://" + js.getString("publicUrl") + "/" + js.getString("fid");

                message.setFilePath(path + "&" +path2);

                MsgService.getMsgWriter(context).sendMsg(message);
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }

        @Override
        public void onFailure(HttpException error, String msg) {
            L.d(TAG, " onFailure() error:" + error.getMessage());
            L.d(TAG, " onFailure() msg :" + msg);
            refreshListItem();
            message.setStatus(MsgInFo.STATUS_MEDIA_UPLOAD_FAILURE);
            XmppDbManager.getInstance(context).updateWithNotify(message);
        }

        @Override
        public void onLoading(long total, long current, boolean isUploading) {
            // message.setExtStatus(DBMessage.STATUS_MEDIA_UPLOAD_LOADING);
            // DBManager.getInstance(context).update(message);
            L.d(TAG, "onLoading() total:" + total);
            L.d(TAG, "onLoading() current:" + current);
            L.d(TAG, "onLoading() isUploading:" + isUploading);
            refreshListItem();
        }

        @Override
        public void onStart() {
            refreshListItem();
            message.setStatus(MsgInFo.STATUS_MEDIA_UPLOAD_START);
            XmppDbManager.getInstance(context).updateWithNotify(message);
        }

        @Override
        public void onCancelled() {
            refreshListItem();
            message.setStatus(MsgInFo.STATUS_MEDIA_UPLOAD_CANCEL);
            XmppDbManager.getInstance(context).updateWithNotify(message);
        }

        @SuppressWarnings("unchecked")
        private void refreshListItem() {
            if (userTag == null)
                return;
            WeakReference<View> tag = (WeakReference<View>) userTag;
            View view = tag.get();
            // if (view != null) {
            // view.refresh();
            // }
            if (view instanceof ChatItemSendImageView) {
                ChatItemSendImageView v = (ChatItemSendImageView) view;
                v.refresh();
            } else if (view instanceof ChatItemSendVoiceView) {
                ChatItemSendVoiceView v = (ChatItemSendVoiceView) view;
                v.refresh();
            }
        }
    }


    public static void receiveMediaMsg(Context context, XmppMessage message) {
//		群聊信息没有sid
        if (message == null /*|| TextUtils.isEmpty(message.getSid())*/) {
            return;
        }
        //这个时候已经是插入到数据库里的了
        Long messageId = message.getId();

        String url = message.getFilePath();
        String filePath = null;

        if (MsgInFo.MOLD_IMG == message.getMold()) {

            File dirFile = Utils
                    .ensureIMSubDir(
                            context,
                            message.getDirect() == MsgInFo.INOUT_IN ? Utils
                                    .FILE_PATH_SUB_DIR_SOUND_IN
                                    : Utils.FILE_PATH_SUB_DIR_IMAGE_OUT);
            // 	TODO: 2015/11/27 将图片格式写死了，后期需要修改 
            File file = new File(dirFile, System.currentTimeMillis() + ".jpg");
            filePath = file.getAbsolutePath();
        } else if (MsgInFo.MOLD_VOICE == message.getMold()) {

            File dirFile = Utils
                    .ensureIMSubDir(
                            context,
                            message.getDirect() == MsgInFo.INOUT_IN ? Utils
                                    .FILE_PATH_SUB_DIR_SOUND_IN
                                    : Utils.FILE_PATH_SUB_DIR_SOUND_OUT);
            File file = new File(dirFile, System.currentTimeMillis() + ".amr");
            filePath = file.getAbsolutePath();
        }

        L.d(TAG, "sendMediaMsg() url:" + url);
        L.d(TAG, "sendMediaMsg() filePath:" + filePath);

        DownloadRequestCallBack callback = new DownloadRequestCallBack(context,
                messageId, message);
//        MyApplication
//                .getInstance()
//                .getDownloadManager()
//                .addNewDownload(url, filePath, filePath, false, false, callback);
    }

    public static class DownloadRequestCallBack extends RequestCallBack<File> {

        private Context context;
        private XmppMessage xmppMessage;

        public DownloadRequestCallBack(Context context, long messageId,
                                       XmppMessage message) {
            this.context = context;
            this.xmppMessage = message;
        }

        @Override
        public void onSuccess(ResponseInfo<File> responseInfo) {
            refreshListItem();
            Log.e(TAG, " onResponse() : file://" + responseInfo);
            xmppMessage.setStatus(MsgInFo.STATUS_MEDIA_DOWNLOAD_SUCCESS);
            xmppMessage.setFilePath("file://" + responseInfo.result.getPath());
            XmppDbManager.getInstance(context).updateWithNotify(xmppMessage);

        }

        @Override
        public void onFailure(HttpException error, String msg) {
            refreshListItem();
            xmppMessage.setStatus(MsgInFo.STATUS_MEDIA_DOWNLOAD_FAILURE);
            XmppDbManager.getInstance(context).updateWithNotify(xmppMessage);
        }

        @Override
        public void onLoading(long total, long current, boolean isUploading) {
            // message.setExtStatus(DBMessage.STATUS_MEDIA_UPLOAD_LOADING);
            // DBManager.getInstance(context).update(message);
            L.d(TAG, "onLoading() total:" + total);
            L.d(TAG, "onLoading() current:" + current);
            L.d(TAG, "onLoading() isUploading:" + isUploading);
            refreshListItem();
        }

        @Override
        public void onStart() {
            refreshListItem();
            xmppMessage.setStatus(MsgInFo.STATUS_MEDIA_DOWNLOAD_START);
            XmppDbManager.getInstance(context).updateWithNotify(xmppMessage);
        }

        @Override
        public void onCancelled() {
            refreshListItem();
            xmppMessage.setStatus(MsgInFo.STATUS_MEDIA_DOWNLOAD_CANCEL);
            XmppDbManager.getInstance(context).updateWithNotify(xmppMessage);
        }

        @SuppressWarnings("unchecked")
        private void refreshListItem() {
//			if (userTag == null)
//				return;
//			WeakReference<View> tag = (WeakReference<View>) userTag;
//			View view = tag.get();

//			if (view instanceof ChatItem) {
//				ChatItem v = (ChatItem) view;
//				v.refresh();
//			}
        }
    }
}
