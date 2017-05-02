package com.wiz.dev.wiztalk.public_store;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;

import com.fanning.library.FNFile;
import com.fanning.library.FNHttp;
import com.fanning.library.FNTools;
import com.wiz.dev.wiztalk.utils.SaveConfig;

import org.afinal.simplecache.ACache;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ynyxmac on 16/2/4.
 */
public class PublicTools {
    private static final String appTag = "Unify_Platform";
    public final static String HttpResponseSucc = "##@##";
    private static HashMap<String, Object> appsInfo;

    //读取本地配置
    public static String loadLocalString(Context AOwner, String AFieldName, Boolean AES) {
        return FNTools.loadLocalString(AOwner, appTag, AFieldName, AES);
    }

    public static boolean saveLocalString(Context AOwner, String AFieldName, String value, Boolean AES) {
        return FNTools.saveLocalVar(AOwner, appTag, AFieldName, value, AES);
    }


    //读取本地配置
    public static String loadLocalString(Context AOwner, String AFieldName) {
        return FNTools.loadLocalString(AOwner, appTag, AFieldName);
    }

    public static boolean saveLocalString(Context AOwner, String AFieldName, String value) {
        return FNTools.saveLocalVar(AOwner, appTag, AFieldName, value, false);
    }

    //读取本地配置
    public static boolean loadLocalBoolean(Context AOwner, String AFieldName) {
        return FNTools.loadLocalBool(AOwner, appTag, AFieldName);
    }

    //save本地配置
    public static boolean saveLocalBoolean(Context AOwner, String AFieldName, boolean value) {
        return FNTools.saveLocalBool(AOwner, appTag, AFieldName, value);
    }

    public static void showStackBarInfo(Context context, String AInfo) {
        FNTools.showStackBarInfo(context, AInfo, "我知道了", 1 | 2 | 4, Color.BLACK, Color.BLUE, Color.WHITE);

    }

    private static JSONObject addinfo(Context context, Map<String, String> map) {
        if (map == null) {
            map = new HashMap<String, String>();
        }

        JSONObject baseJson = new JSONObject();

        try {
            baseJson.put("deviceType", "android");
            baseJson.put("customer_id", ConstDefine.customer_id);
            baseJson.put("deviceID", FNTools.getAndroidDeviceId(context));

            String token = ConstDefine.token;
//            if (!FNTools.emptyString(token))
//                token = "D82BC1146DCA46CEA37ED3FC71CBD5FF_yxdx3kn3cbom1o7wzishnj0nt";
            baseJson.put("token", token);
            String uid = ConstDefine.uid;
//            if (!FNTools.emptyString(uid))
//                uid = "D82BC1146DCA46CEA37ED3FC71CBD5FF";
            baseJson.put("uid", uid);
        } catch (JSONException e) {
            return null;
        }

        String t;
        JSONObject jsonObject = new JSONObject(map);
        try {
            jsonObject.put("baseRequest", baseJson);
            t = jsonObject.toString();
            FNTools.FLog(t);
        } catch (JSONException e) {
            jsonObject = null;
        }

        return jsonObject;
    }

    public static void syncHttpPostWithProgress(
            Context AOwner,
            android.os.Handler AMsgHwnd,
            String msgTag,
            String url,
            Map<String, String> map,
            String msg) {

        JSONObject jsonObject = addinfo(AOwner, map);

        if (jsonObject == null) return;

        if(!url.startsWith("http"))
            url = ConstDefine.HttpAdress + "/app/client/device" + url;
        FNHttp.syncHttpPostWithProgress(AOwner, AMsgHwnd, msgTag, url, jsonObject, msg);
    }

    public static void syncHttpGetWithProgress(
            Context AOwner,
            android.os.Handler AMsgHwnd,
            String msgTag,
            String url,
            String msg) {
        if (!url.startsWith("http"))
            url = ConstDefine.HttpAdress + url;
        FNHttp.syncHttpGetWithProgress(AOwner, AMsgHwnd, msgTag, url, msg);
    }

    public static void syncHttpGetWithoutProgress(
            Context AOwner,
            android.os.Handler AMsgHwnd,
            String msgTag,
            String url) {
        if (!url.startsWith("http"))
            url = ConstDefine.HttpAdress + url;
        FNHttp.syncHttpGetWithoutProgress(AOwner, AMsgHwnd, msgTag, url);
    }

    public static void syncHttpPostWithoutProgress(
            Context AOwner,
            android.os.Handler AMsgHwnd,
            String msgTag,
            String url,
            Map<String, String> map) {
        JSONObject jsonObject = addinfo(AOwner, map);

        if (jsonObject == null) return;

        if (!url.startsWith("http"))
            url = ConstDefine.HttpAdress + "/app/client/device" + url;
        FNHttp.syncHttpPostWithoutProgress(AOwner, AMsgHwnd, msgTag, url, jsonObject);
    }

    public static boolean getFailMessage(final Context context, String jsonStr) {

        boolean ret = false;
        if (FNTools.emptyString(jsonStr)) return ret;

        try {
            JSONObject jsonObject;
            JSONObject rootobject = new JSONObject(jsonStr);
            if (rootobject != null) {
                jsonObject = rootobject.optJSONObject("baseResponse");
                if (jsonObject != null) {
                    if (jsonObject.getInt("ret") == 0) {
                        ret = true;
                    }
                } else {
                    ret = rootobject.getBoolean("succeed");
                }

            }
        } catch (JSONException e) {

        }

        return ret;
    }

    public static void DLog(String info) {
        FNTools.FLog("UnifyPlatform", info);
    }

    public static boolean checkHttpResponse(Context context, Bundle data, String netErrorMsg, String optErrorMessage) {
        if (data == null) return false;
        boolean ret = true;

        FNHttp.closeRequestProgress(data);

        if (!FNHttp.isRequestSuccess(data)) {
            ret = false;
            PublicTools.showToast(context, netErrorMsg);
        } else {
            String valueStr = FNHttp.getRequestResult(data);
            if (!PublicTools.getFailMessage(context, valueStr)) {
                ret = false;
                PublicTools.showToast(context, optErrorMessage);
            }
        }
        return ret;
    }

    public static boolean checkHttpResponse(Context context, Bundle data) {
        return checkHttpResponse(context, data, "操作失败，请确认网络连接！", "操作失败，请稍候再试！");
    }

    public static boolean checkHttpResponse(Context context, Bundle data, String opterrormsg) {
        return checkHttpResponse(context, data, "操作失败，请确认网络连接！", opterrormsg);
    }

    public static void showToast(Context context, String info) {
        FNTools.showHintInfo(context, info);
    }

    //按位
    public static final int APP_NOINSTALLED = 1;  //未安装
    public static final int APP_INSTALLED = 2;  //已经安装
    public static final int APP_UPDATED = 4;  //已经安装，可更新

    public static int getappState(Context context, String packagename, int curintversion) {
        if (!FNTools.appIsInstalled(context, packagename)) {
            return APP_NOINSTALLED;
        }
        if (curintversion < 0) {
            return APP_INSTALLED;
        }

        int ret = APP_INSTALLED;
        HashMap<String, Object> mapinfo = getAppsInfo(context, packagename);
        if (mapinfo != null && mapinfo.containsKey("versioncode")) {
            if (curintversion > (Integer) mapinfo.get("versioncode")) {
                ret |= APP_UPDATED;
            }
        }
        return ret;
    }

    public static String getAppStateCaption(Context context, String packagename, int curintversion) {
        int appstate = getappState(context, packagename, curintversion);
        String ret = "";
        if ((appstate & PublicTools.APP_UPDATED) > 0) {
            ret = "更 新";
        } else if ((appstate & PublicTools.APP_INSTALLED) > 0) {
            ret = "卸 载";
        } else if ((appstate & PublicTools.APP_NOINSTALLED) > 0) {
            ret = "安 装";
        }
        return ret;
    }

    public static void putCache(Context context, String key, Serializable value) {
        ACache mACache = ACache.get(context);
        mACache.put(key, value, ACache.MAX_COUNT);
    }

    public static ArrayList<String> getAllInstalledAppsList(Context context, ArrayList<String> savelist) {
        String filename = context.getFilesDir() + "/UnifyInstalledApps";
        return getStringListFromFile(filename, context, savelist);
    }


    private static ArrayList<String> getStringListFromFile(String filename, Context context, ArrayList<String> savelist) {
        //保存
        if (savelist != null) {
            try {
                File f = new File(filename);
                ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(f));
                oos.writeObject(savelist);
                oos.flush();
                oos.close();
            } catch (Exception e) {
                PublicTools.DLog("保存" + filename + "失败：" + e.getMessage());
            }
        } else //读取
        {
            //FileInputStream stream;
            try {
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename));
                return (ArrayList<String>) ois.readObject();
            } catch (Exception e) {
                PublicTools.DLog("读取" + filename + "失败：" + e.getMessage());
            }
        }
        return null;
    }

    public static ArrayList<String> getCommonAppsList(Context context, ArrayList<String> savelist) {
        String filename = context.getFilesDir() + "/UnifyCommonApps";
        return getStringListFromFile(filename, context, savelist);
    }

    public static void clearAppInfoCache(Context context) {
        ACache mACache = ACache.get(context);
        mACache.remove("appsinfomap");
        if (appsInfo != null)
            appsInfo.clear();
    }


    private static HashMap<String, Object> getAppsInfo(Context context, String packagename) {
        if (FNTools.emptyString(packagename)) {
            return null;
        }

        if (appsInfo == null) {
            appsInfo = new HashMap<String, Object>();

            //读取缓存
            HashMap<String, Object> map = (HashMap<String, Object>) ACache.get(
                    context).getAsObject("appsinfomap");
            if (map != null) {
                appsInfo.putAll(map);
            }
        }

        if (appsInfo.containsKey(packagename)) {
            return (HashMap<String, Object>) appsInfo.get(packagename);
        }

        PackageInfo pi = null;

        try {
            PackageManager pm = context.getPackageManager();

//          List<PackageInfo> list = pm.getInstalledPackages(0);
//          PublicTools.DLog(list.toString());
            pi = pm.getPackageInfo(packagename, 0);
            String packageName = pi.packageName;
            if (FNTools.emptyString(packageName)) {
                return null;
            }

            String appname = pi.applicationInfo.loadLabel(pm).toString();
            if (FNTools.emptyString(appname)) {
                return null;
            }

            HashMap<String, Object> mapinfo = new HashMap<String, Object>();

            mapinfo.put("packageName", packagename);
            mapinfo.put("appName", appname.trim());
            mapinfo.put("appIcon", pi.applicationInfo.loadIcon(context.getPackageManager()));
            mapinfo.put("versioncode", pi.versionCode);
            mapinfo.put("versionName", pi.versionName);

            appsInfo.put(packagename, mapinfo);

            //写入缓存
            PublicTools.putCache(context, "appsinfomap", mapinfo);
            return mapinfo;

        } catch (Exception e) {
            PublicTools.DLog("读取app信息失败：packagename(" + packagename + ") " + e.getMessage());
            return null;
        }
    }

    public static String getAppName(Context context, String packagename) {
        HashMap<String, Object> mapinfo = getAppsInfo(context, packagename);
        if (mapinfo != null && mapinfo.containsKey("appName")) {
            return mapinfo.get("appName").toString();
        }
        return "";
    }

    public static Drawable getAppIcon(Context context, String packagename) {
        HashMap<String, Object> mapinfo = getAppsInfo(context, packagename);
        if (mapinfo != null && mapinfo.containsKey("appName")) {
            return (Drawable) mapinfo.get("appIcon");
        }
        return null;
    }

    public static int getAppVersion(Context context, String packagename) {
        HashMap<String, Object> mapinfo = getAppsInfo(context, packagename);
        if (mapinfo != null && mapinfo.containsKey("versioncode")) {
            return (Integer) mapinfo.get("versioncode");
        }
        return -1;
    }

    public static boolean updateAppsList(Context context) {
        boolean ret = false;
        String packagename;
        ArrayList<String> list = null;
        ArrayList<String> listnew = null;

        for (int i = 0; i < 2; i++) {
            if (i == 0) {
                list = getAllInstalledAppsList(context, null);
            } else {
                list = getCommonAppsList(context, null);
            }

            if (list == null) continue;

            listnew = new ArrayList<String>();

            for (int j = 0; j < list.size(); j++) {
                packagename = list.get(j);
                if (FNTools.appIsInstalled(context, packagename)) {
                    listnew.add(packagename);
                }
            }
            if (listnew.size() != list.size()) {
                ret = true;
                if (i == 0) {
                    getAllInstalledAppsList(context, listnew);
                } else {
                    getCommonAppsList(context, listnew);
                }
            }
        }
        return ret;
    }

    public static String getMapedString(HashMap<String, Object> map, String key) {
        String ret = "";
        if (map != null && !FNTools.emptyString(key) && map.containsKey(key)) {
            ret = map.get(key).toString();
        }
        return ret;
    }

    public static int getMapedInt(HashMap<String, Object> map, String key) {
        int ret = -1;
        if (map != null && !FNTools.emptyString(key) && map.containsKey(key)) {
            ret = (Integer) map.get(key);
        }
        return ret;
    }

    public static String getDownloadUrl(String id) {
        if (FNTools.emptyString(id)) return "";
        //   return ConstDefine.HttpAdress + "/app/client/device/login/download?fileId=" + id;
        return "http://10.180.128.100:8080/app/client/device/login/download?fileId=" + id;
    }

    public static String getSDCardPath(Context context) {
        File sdcardDir = null;
        //判断SDCard是否存在
        boolean sdcardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        if (sdcardExist) {
            sdcardDir = Environment.getExternalStorageDirectory();
            return sdcardDir.toString() + "/yxPlatform";
        }
        return FNFile.getCachePath(context) + "/yxPlatform";
    }


}
