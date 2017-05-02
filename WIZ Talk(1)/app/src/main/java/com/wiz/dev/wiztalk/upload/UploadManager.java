package com.wiz.dev.wiztalk.upload;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;

import com.epic.traverse.push.util.L;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

import cn.finalteam.okhttpfinal.OkHttpFinal;

/**
 * Author: wyouflf
 * Date: 13-11-10
 * Time: 下午8:10
 */
public class UploadManager {
	
	private static final String TAG = "UploadManager";
    private List<UploadInfo> uploadInfoList;
    private int maxUploadThread = 3;
    private Context mContext;
//    private DbUtils db;

    /*package*/ UploadManager(Context appContext) {
//        ColumnConverterFactory.registerColumnConverter(HttpHandler.State.class, new HttpHandlerStateConverter());
        mContext = appContext;
//        db = DbUtils.create(mContext);
//        try {
//            uploadInfoList = db.findAll(Selector.from(UploadInfo.class));
//        } catch (DbException e) {
//            LogUtils.e(e.getMessage(), e);
//        }
        if (uploadInfoList == null) {
            uploadInfoList = new ArrayList<UploadInfo>();
        }
    }

    public int getUploadInfoListCount() {
        return uploadInfoList.size();
    }

    public UploadInfo getUploadInfo(int index) {
        return uploadInfoList.get(index);
    }

    private HashMap<String, UploadInfo> map = new HashMap<String, UploadInfo>();

    
    public UploadInfo getUploadInfo(String clientMsgId) {
    	return map.get(clientMsgId);
    }

    public void addNewUpload(String clientMsgId, String url, String fileName, String target, final RequestCallBack<String> callback) {
    	UploadInfo uploadInfo = this.addNewUpload(url, fileName, target, callback);
    	map.put(clientMsgId, uploadInfo);
    }
    
    public UploadInfo addNewUpload(String url, String fileName, String target, final RequestCallBack<String> callback) /*throws DbException*/ {

        L.d(TAG, "addNewUpload() ");
        L.d(TAG, "addNewUpload() url"+url);
        assert url!=null;
        assert fileName!=null;
        assert target!=null;
    	final UploadInfo uploadInfo = new UploadInfo();
        uploadInfo.setUploadUrl(url);
        uploadInfo.setFileName(fileName);
        uploadInfo.setFileSavePath(target);
        HttpUtils http = new HttpUtils();


        http.configRequestThreadPoolSize(maxUploadThread);
        RequestParams params = new RequestParams();
        params.addBodyParameter("file", new File(target));
//       params.addHeader("http.socket.timeout", "8000");
        HttpHandler<String> handler = http.send(HttpMethod.POST, url, params, new ManagerCallBack(uploadInfo, callback));
        uploadInfo.setHandler(handler);
        uploadInfo.setState(handler.getState());
        uploadInfoList.add(uploadInfo);
        return uploadInfo;
    }


//    public void addNewUpload(String url, String fileName, String target, final RequestCallBack<String> callback) /*throws DbException*/ {
//    	final UploadInfo uploadInfo = new UploadInfo();
//    	uploadInfo.setUploadUrl(url);
//    	uploadInfo.setFileName(fileName);
//    	uploadInfo.setFileSavePath(target);
//    	HttpUtils http = new HttpUtils();
//    	http.configRequestThreadPoolSize(maxUploadThread);
//    	RequestParams params = new RequestParams();
//    	params.addBodyParameter("file", new File(target));
//    	HttpHandler<String> handler = http.send(HttpMethod.POST, url, params, new ManagerCallBack(uploadInfo, callback));
//    	uploadInfo.setHandler(handler);
//    	uploadInfo.setState(handler.getState());
//    	uploadInfoList.add(uploadInfo);
////        db.saveBindingId(uploadInfo);
//    }

    public void removeUpload(int index) /*throws DbException*/ {
        UploadInfo uploadInfo = uploadInfoList.get(index);
        removeUpload(uploadInfo);
    }

    public void removeUpload(UploadInfo uploadInfo) /*throws DbException*/ {
        HttpHandler<String> handler = uploadInfo.getHandler();
        if (handler != null && !handler.isCancelled()) {
            handler.cancel();
        }
        uploadInfoList.remove(uploadInfo);
//        db.delete(uploadInfo);
    }

    public void stopUpload(int index) /*throws DbException*/ {
        UploadInfo uploadInfo = uploadInfoList.get(index);
        stopUpload(uploadInfo);
    }

    public void stopUpload(UploadInfo uploadInfo) /*throws DbException*/ {
        HttpHandler<String> handler = uploadInfo.getHandler();
        if (handler != null && !handler.isCancelled()) {
            handler.cancel();
        } else {
            uploadInfo.setState(HttpHandler.State.CANCELLED);
        }
//        db.saveOrUpdate(uploadInfo);
    }

    public void stopAllUpload() throws DbException {
        for (UploadInfo uploadInfo : uploadInfoList) {
            HttpHandler<String> handler = uploadInfo.getHandler();
            if (handler != null && !handler.isCancelled()) {
                handler.cancel();
            } else {
                uploadInfo.setState(HttpHandler.State.CANCELLED);
            }
        }
//        db.saveOrUpdateAll(uploadInfoList);
    }

    public void backupUploadInfoList() throws DbException {
        for (UploadInfo uploadInfo : uploadInfoList) {
            HttpHandler<String> handler = uploadInfo.getHandler();
            if (handler != null) {
                uploadInfo.setState(handler.getState());
            }
        }
//        db.saveOrUpdateAll(uploadInfoList);
    }

    public int getMaxUploadThread() {
        return maxUploadThread;
    }

    public void setMaxUploadThread(int maxUploadThread) {
        this.maxUploadThread = maxUploadThread;
    }

    public class ManagerCallBack extends RequestCallBack<String> {
        private UploadInfo uploadInfo;
        private RequestCallBack<String> baseCallBack;

        public RequestCallBack<String> getBaseCallBack() {
            return baseCallBack;
        }

        public void setBaseCallBack(RequestCallBack<String> baseCallBack) {
            this.baseCallBack = baseCallBack;
        }

        private ManagerCallBack(UploadInfo uploadInfo, RequestCallBack<String> baseCallBack) {
            this.baseCallBack = baseCallBack;
            this.uploadInfo = uploadInfo;
        }

        @Override
        public Object getUserTag() {
            if (baseCallBack == null) return null;
            return baseCallBack.getUserTag();
        }

        @Override
        public void setUserTag(Object userTag) {
            if (baseCallBack == null) return;
            baseCallBack.setUserTag(userTag);
        }

        @Override
        public void onStart() {
            HttpHandler<String> handler = uploadInfo.getHandler();
            if (handler != null) {
                uploadInfo.setState(handler.getState());
            }
//            try {
//                db.saveOrUpdate(uploadInfo);
//            } catch (DbException e) {
//                LogUtils.e(e.getMessage(), e);
//            }
            if (baseCallBack != null) {
                baseCallBack.onStart();
            }
        }

        @Override
        public void onCancelled() {
            HttpHandler<String> handler = uploadInfo.getHandler();
            if (handler != null) {
                uploadInfo.setState(handler.getState());
            }
//            try {
//                db.saveOrUpdate(uploadInfo);
//            } catch (DbException e) {
//                LogUtils.e(e.getMessage(), e);
//            }
            if (baseCallBack != null) {
                baseCallBack.onCancelled();
            }
        }

        @Override
        public void onLoading(long total, long current, boolean isUploading) {
            HttpHandler<String> handler = uploadInfo.getHandler();
            if (handler != null) {
                uploadInfo.setState(handler.getState());
            }
            uploadInfo.setUploading(isUploading);
            uploadInfo.setFileLength(total);
            uploadInfo.setProgress(current);
//            try {
//                db.saveOrUpdate(uploadInfo);
//            } catch (DbException e) {
//                LogUtils.e(e.getMessage(), e);
//            }
            if (baseCallBack != null) {
                baseCallBack.onLoading(total, current, isUploading);
            }
        }

        @Override
        public void onSuccess(ResponseInfo<String> responseInfo) {
            HttpHandler<String> handler = uploadInfo.getHandler();
            if (handler != null) {
                uploadInfo.setState(handler.getState());
            }
//            try {
//                db.saveOrUpdate(uploadInfo);
//            } catch (DbException e) {
//                LogUtils.e(e.getMessage(), e);
//            }
            if (baseCallBack != null) {
                baseCallBack.onSuccess(responseInfo);
            }
        }

        @Override
        public void onFailure(HttpException error, String msg) {
        	L.d(TAG, "onFailure() error: exception:"+error.getMessage());
        	L.d(TAG, "onFailure() error msg :"+msg);
            HttpHandler<String> handler = uploadInfo.getHandler();
            if (handler != null) {
                uploadInfo.setState(handler.getState());
                uploadInfo.setUploading(false);
            }
//            try {
//                db.saveOrUpdate(uploadInfo);
//            } catch (DbException e) {
//                LogUtils.e(e.getMessage(), e);
//            }
            if (baseCallBack != null) {
                baseCallBack.onFailure(error, msg);
            }
        }
    }

//    private class HttpHandlerStateConverter implements ColumnConverter<HttpHandler.State> {
//
//        @Override
//        public HttpHandler.State getFieldValue(Cursor cursor, int index) {
//            return HttpHandler.State.valueOf(cursor.getInt(index));
//        }
//
//        @Override
//        public HttpHandler.State getFieldValue(String fieldStringValue) {
//            if (fieldStringValue == null) return null;
//            return HttpHandler.State.valueOf(fieldStringValue);
//        }
//
//        @Override
//        public Object fieldValue2ColumnValue(HttpHandler.State fieldValue) {
//            return fieldValue.value();
//        }
//
//        @Override
//        public ColumnDbType getColumnDbType() {
//            return ColumnDbType.INTEGER;
//        }
//    }
}
