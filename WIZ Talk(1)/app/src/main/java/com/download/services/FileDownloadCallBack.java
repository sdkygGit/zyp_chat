package com.download.services;

import com.download.entities.FileInfo;

/**
 * Created by Administrator on 2016/7/1.
 */
public abstract class FileDownloadCallBack {

    protected java.lang.Object userTag;

    public void setUserTag(Object userTag){this.userTag = userTag;}

    public abstract void onStart();

    public abstract void onPregress(int progress);

    public abstract void onFinish(FileInfo fileInfo);

    public abstract void onNetError();
}
