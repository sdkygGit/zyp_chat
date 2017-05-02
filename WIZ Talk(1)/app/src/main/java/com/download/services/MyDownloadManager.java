package com.download.services;

import com.download.entities.FileInfo;
import com.wiz.dev.wiztalk.upload.UploadInfo;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/7/1.
 */
public class MyDownloadManager {

    public Map<Long,FileInfo> map = new HashMap<Long, FileInfo>();


    public FileInfo getUploadInfo(long clientMsgId) {
        return map.get(clientMsgId);
    }




    public void addDownloadTask(long id, FileInfo fileInfo){


        map.put(id,fileInfo);
    }
}
