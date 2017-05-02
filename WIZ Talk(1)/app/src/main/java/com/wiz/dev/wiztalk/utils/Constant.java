package com.wiz.dev.wiztalk.utils;

/**
 * Created by Admin on 2015/8/26.
 */
public class Constant {
    public static final int USER_INFO_FROM_CHAT_MY = 0X1001; //从聊天界面跳转,自己
    public static final int USER_INFO_FROM_CHAT_OTHER = 0X1002; //从聊天界面跳转,other
    public static final int USER_INFO_FROM_MYINFO = 0X1003; //从我界面跳转
    public static final int USER_INFO_FROM_NEAR = 0X1004; //从我附近的人界面跳转 ，暂时不做了
    public static final int USER_INFO_FROM_CONTACT = 0X1005; //从联系人跳转

    public static final String IMAGER_FROM_CAMERA = "/sdcard/epic_camera"; //启动相机拍照 保存照片的位置
    public static final String IMAGER_THUMBNAIL = "/sdcard/epic_thumbnail/"; //缩略图位置

    public static final int REQUEST_CODE_TAKE_LOCAL = 10001;
    public static final int REQUEST_CODE_TAKE_CAMERA = 10002;
    public static final int REQUEST_CODE_TAKE_MOVIE = 10003;



}
