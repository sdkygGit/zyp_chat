package com.fanning.library;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by ynyxmac on 16/1/4.
 */
public class FNTools
{
    public static String appverStr = "";

    //获取客户机android版本
    public static int getAndroidOSVersion()
    {
        int osVersion;
        try
        {
            osVersion = Integer.valueOf(Build.VERSION.SDK_INT);
        }
        catch (NumberFormatException e)
        {
            osVersion = 0;
        }

        return osVersion;
    }

    public static void FLog(String sign, String info)
    {
        Log.d(sign, info);
    }

    public static void FLog(String info)
    {
        FLog("Fanning", info);
    }

    public static boolean emptyString(String str) {
        return emptyString(str, true);
    }

    public static boolean emptyString(String str, boolean trim) {
        if (str == null || str.isEmpty())
            return true;
        if (trim && str.trim().isEmpty())
            return true;
        return false;
    }

    public static String addString(String str, String add)
    {
        return addString(str, add, ",");
    }
    public static String addString(String str, String add, String sep)
    {
        if (str.isEmpty())
            return add;
        return str + sep + add;
    }

    public static String getAppVer(ContextWrapper contextWrapper)
    {
        if (emptyString(appverStr))
        {
            PackageManager pm = contextWrapper.getPackageManager();

            PackageInfo pi = null;//getPackageName()是你当前类的包名，0代表是获取版本信息
            try {
                pi = pm.getPackageInfo(contextWrapper.getPackageName(), 0);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            appverStr = pi.versionName;
        }
        return getAppVer();
    }

    public static String getAppVer()
    {
        return appverStr;
    }

    /**
     * 获取版本号(内部识别号)
     *
     * @param context
     * @return
     */
    public static int getVersionCode(Context context) {
        try {
            PackageInfo pi = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0);
            return pi.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static void showHintInfo(Context context, String AInfo)
    {
        showHintInfo(context, AInfo, Toast.LENGTH_SHORT);
    }

    public static void showHintInfo(Context context, String AInfo, int length)
    {
        Toast.makeText(context, AInfo, length).show();
    }

    public static void showStackBarInfo(Context context, String AInfo)
    {
        showStackBarInfo(context, AInfo, "我知道了", 0, -1, -1, -1);
    }

    public static void showStackBarInfo(Context context, String AInfo,
                                        String title,
                                        int setcolorflag,
                                        int titlecolor,
                                        int btcolor,
                                        int backgroundcolor)
    {
        Activity av = (Activity)context;
        View v = av.getWindow().getDecorView();
        /*final Snackbar bar= Snackbar.make(v, AInfo, Snackbar.LENGTH_LONG);
        Snackbar.SnackbarLayout ve = (Snackbar.SnackbarLayout)bar.getView();

        if ((setcolorflag & 1) !=0)
            ((TextView) ve.findViewById(R.id.snackbar_text)).setTextColor(titlecolor);
        if ((setcolorflag & 2) !=0)
            ((Button) ve.findViewById(R.id.snackbar_action)).setTextColor(btcolor);
        if ((setcolorflag & 4) !=0)
            ve.setBackgroundColor(backgroundcolor);

        bar.show();
        bar.setAction(title, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bar.dismiss();
            }
        });*/
    }

    private static final String AES_SEED = "fanninglibiary19890604";

    //保存本地配置
    public static boolean saveLocalVar(Context AOwner,String Apptag, String AFieldName,String AValue, boolean AES)
    {
        SharedPreferences userInfo = AOwner.getSharedPreferences(Apptag, 0);
        if (AES)
        {
            AValue = AESEncrypt(AES_SEED, AValue);
        }
        return userInfo.edit().putString(AFieldName,AValue).commit();
    }
    //读取本地配置
    public static String loadLocalString(Context AOwner,String Apptag, String AFieldName, Boolean AES)
    {
        SharedPreferences userInfo = AOwner.getSharedPreferences(Apptag, 0);
        String ret = userInfo.getString(AFieldName, "");
        if (AES)
        {
            ret = AESDecrypt(AES_SEED, ret);
        }
        return ret;
    }

    //保存本地配置
    public static boolean saveLocalVar(Context AOwner,String Apptag, String AFieldName,String AValue)
    {
        return saveLocalVar(AOwner, Apptag, AFieldName, AValue, false);
    }
    //读取本地配置
    public static String loadLocalString(Context AOwner,String Apptag, String AFieldName)
    {
        return loadLocalString(AOwner, Apptag, AFieldName, false);
    }

    public static boolean loadLocalBool(Context AOwner,String Apptag, String AFieldName)
    {
        SharedPreferences userInfo = AOwner.getSharedPreferences(Apptag, 0);
        return userInfo.getBoolean(AFieldName, false);
    }

    public static boolean saveLocalBool(Context AOwner,String Apptag, String AFieldName, boolean value)
    {
        SharedPreferences userInfo = AOwner.getSharedPreferences(Apptag, 0);
        return userInfo.edit().putBoolean(AFieldName,value).commit();
    }

    //aes加密，失败返回原字串
    public static String AESEncrypt(String seed, String str2aes)
    {
        if (!emptyString(str2aes))
        {
            try
            {
                str2aes = AESEncryptor.encrypt(seed, str2aes);
            }
            catch (Exception ex)
            {
                FLog("AES加密失败：" + seed + " 原因：" + ex.getMessage());
            }
        }
        return str2aes;
    }

    //aes解密
    public static String AESDecrypt(String seed, String str2dec)
    {
        if (!emptyString(str2dec))
        {
            try
            {
                str2dec = AESEncryptor.decrypt(seed, str2dec);
            }
            catch (Exception ex)
            {
                FLog("AES解密失败：" + str2dec + " 原因：" + ex.getMessage());
            }
        }
        return str2dec;
    }

    public static boolean isInStrings(String[] strlist, String str)
    {
        if (strlist == null || emptyString(str) ) return false;

        for (int i =0 ;i < strlist.length; i ++)
        {
            if (strlist[i].equals(str))
                return true;
        }
        return false;
    }

    //事件回调调方法
    public static abstract class MsgResultCallback{
        abstract public String OnResult(String AVal,Object v);
    }

    //提问对话框,由于Android本身不支持模太模框，因为必须有异步处理方法，需要按上面的步骤进行。
    public static void showAskMessage(Context AOwner,//宿主窗体
                                      final Object tag,//异步回调的参数，会原样传给回调函数
                                      String AContent,//显示的内容
                                      final MsgResultCallback CBMsg//回调事件
    )
    {
        showAskMessage(AOwner, tag, AContent, "提示", CBMsg);
    }

    //提问对话框,由于Android本身不支持模太模框，因为必须有异步处理方法，需要按上面的步骤进行。
    public static void showAskMessage(Context AOwner,//宿主窗体
                                      final Object tag,//异步回调的参数，会原样传给回调函数
                                      String AContent,//显示的内容
                                      String title,
                                      final MsgResultCallback CBMsg//回调事件
    )
    {
        final AlertDialog.Builder builder = new AlertDialog.Builder(AOwner);
        //显示一行文字, 与其它显示相冲突
        //builder.setMessage("这是一个测试");
        builder.setTitle(title);
        builder.setMessage(AContent);
        builder.setPositiveButton("是",
                new android.content.DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if(CBMsg!=null){
                            CBMsg.OnResult("true",tag);
                        }
                    }
                });

        builder.setNegativeButton("否",
                new android.content.DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if(CBMsg!=null){
                            CBMsg.OnResult("false",tag);
                        }
                    }
                });
        builder.create().show();

    }

    public static void showMessageBox(Context AOwner,String AContent)
    {
        showMessageBox(AOwner, AContent, "提示");
    }

    public static void showMessageBox(Context AOwner,String AContent, String title)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(AOwner);
        //显示一行文字, 与其它显示相冲突
        //builder.setMessage("这是一个测试");
        builder.setTitle(title);
        builder.setMessage(AContent);
        //builder.setIcon(R.drawable.logo);
        builder.setPositiveButton("确定",
                new android.content.DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        builder.create().show();
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {// 逐一查找状态为已连接的网络
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static void openActivity(android.content.Context ACurr,java.lang.Class<?> ADst,android.os.Bundle AParms)
    {
        android.content.Intent  intent = new android.content.Intent();
        intent.setClass(ACurr, ADst);
        if (AParms!=null) {
            intent.putExtras(AParms);
        }
        ACurr.startActivity(intent);
    }

    public static String getAndroidDeviceId(Context context) {
        /*String deviceId = getDeviceId(context);
        if (!TextUtils.isEmpty(deviceId)) {
            return deviceId;
        }*/
//        String androidId = getAndroidId(context);
//        if (!TextUtils.isEmpty(androidId)) {
//            return androidId;
//        }
        return "null";
    }

    public static String getAndroidId(Context context) {
        @SuppressWarnings("deprecation")
        String androidId = android.provider.Settings.System.getString(
                context.getContentResolver(),
                android.provider.Settings.System.ANDROID_ID);
        return androidId;
    }

    public static String getDeviceId(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);

        return telephonyManager.getDeviceId();
    }

    public static boolean appIsInstalled(Context context, String packageName){
        boolean ret = false;
        try
        {
            PackageManager pm = context.getPackageManager();
            PackageInfo info = pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);

            if (info == null)
            {
                Intent intent = pm.getLaunchIntentForPackage(packageName);

                if (intent != null)
                    ret = true;
            }
            else
            {
                ret = true;
            }
        }
        catch (PackageManager.NameNotFoundException e)
        {
            ;
        }
        return ret;
    }

    public static String getStandardTime(long timestamp, String format) {
        if (timestamp <= 0) return  "";
        if (emptyString(format))
        {
            format = "yyyy-MM-dd HH:mm:ss";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format , Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        Date date = new Date(timestamp * 1000);
        sdf.format(date);
        return sdf.format(date);
    }


    public static String getStandardTime(long timestamp) {
        return getStandardTime(timestamp, "");
    }
}
