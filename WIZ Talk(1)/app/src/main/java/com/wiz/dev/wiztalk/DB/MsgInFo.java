package com.wiz.dev.wiztalk.DB;

/**
 * Created by Dong on 2015/11/2.
 */
public interface MsgInFo {

    
    String TYPE_CHAT = "chat";
    String TYPE_GROUPCHAT = "groupchat";
    String TYPE_HEADLINE = "headline";//应用消息
    String TYPE_GROUP_OPERATE = "group_operate";// 与群相关的操作信息,

    //应用后缀名
    String APP_SUFIXX = "@app";
    String ORG_SUFIXX = "@org";
    
    String APP_TYPE_102 = "102";
    String APP_TYPE_103 = "103";
    String APP_INIT = "APP_ENTITY_CREATE";
    
    //groupOperateType   groupoperate的各种操作
    int OPERATE_TIPS = 0; // 其他提示
    int OPERATE_INVITE = 1;  //邀请入群的提示 ,显示一个同意按钮
    int OPERATE_INVITE_STATUS = 2;//已经同意了
    int OPERATE_APPLY = 3; //邀请信息
    int OPERATE_APPLY_STATUS = 4; //已经处理邀请
    
    //0:文本；1：图片；2：语音
    int MOLD_TEXT =0;
    int MOLD_IMG= 1;
    int MOLD_VOICE=2;
    int MOLD_TIPS =3;
    int MOLE_APP_101 = 4;//应用消息
    int MOLE_APP_102 = 102;//应用消息
    int MOLE_APP_103 = 103;//应用消息
    int MOLE_APP_104 = 7;//应用消息
    int MOLD_MOVIE=8;
    int MOLD_FILE=9;

    int MOLD_SHARE_APP = 0x210;
    int MOLD_SHARE_DOC = 0x211;
    int MOLD_LOCATION = 0x212;
    int MOLD_INIT_APP = 0x213;
    
    //message 是收还是发
    int INOUT_IN = 1;
    int INOUT_OUT = 0;

    //已读  
    int READ_TRUE = 1;
    //未读
    int READ_FALSE = 0;

//    消息发送 status: 消息发送的几种状态
    int STATUS_PENDING = 0;  //
    int STATUS_SENDING = 1;
    int STATUS_SUCCESS = 2;
    int STATUS_ERROR = -1;

//    文件上传状态：

    int STATUS_MEDIA_UPLOAD_NORMAL = 99;
    int STATUS_MEDIA_UPLOAD_PENDING = 100;
    int STATUS_MEDIA_UPLOAD_START = 101;
    int STATUS_MEDIA_UPLOAD_CANCEL = 102;
    int STATUS_MEDIA_UPLOAD_SUCCESS = 103;
    int STATUS_MEDIA_UPLOAD_FAILURE = 104;
    int STATUS_MEDIA_UPLOAD_LOADING = 105;

    //下载状态
    int STATUS_MEDIA_DOWNLOAD_PENDING = 150;
    int STATUS_MEDIA_DOWNLOAD_START = 151;
    int STATUS_MEDIA_DOWNLOAD_CANCEL = 152;
    int STATUS_MEDIA_DOWNLOAD_SUCCESS = 153;
    int STATUS_MEDIA_DOWNLOAD_FAILURE = 154;
    int STATUS_MEDIA_DOWNLOAD_LOADING = 155;

}
