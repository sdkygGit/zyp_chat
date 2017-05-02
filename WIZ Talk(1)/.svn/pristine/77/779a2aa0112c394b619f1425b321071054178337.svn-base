package com.fanning.library;


import android.content.Context;
import android.content.Intent;

import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;


import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.BinaryHttpResponseHandler;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.loopj.android.http.RequestHandle;

import org.apache.http.Header;

import java.io.File;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

/**
 * Created by ynyxmac on 16/1/4.
 */
public class FNFile {

    /**
     * 删除指定目录下文件及目录
     * @param deleteThisPath
     * @param filepath
     * @return
     */
    public static void deleteFolderFile(String filePath, boolean deleteThisPath) {
        if (!TextUtils.isEmpty(filePath)) {
            try {

                File file = new File(filePath);
                if (file.isDirectory()) {// 处理目录
                    File files[] = file.listFiles();
                    for (int i = 0; i < files.length; i++) {
                        deleteFolderFile(files[i].getAbsolutePath(), true);
                    }
                }
                if (deleteThisPath) {
                    if (!file.isDirectory()) {// 如果是文件，删除
                        file.delete();
                    } else {// 目录
                        if (file.listFiles().length == 0) {// 目录下没有文件或者目录，删除
                            file.delete();
                        }
                    }
                }
            } catch (Exception e) {
                FNTools.FLog("删除文件夹/文件错误:" + e.getMessage());
                e.printStackTrace();
            }
        }
    }
    /**
     * 格式化单位
     * @param size
     * @return
     */
    public static String getFormatSize(double size) {
        double kiloByte = size/1024;
        if(kiloByte < 1) {
            return size + "Byte(s)";
        }

        double megaByte = kiloByte/1024;
        if(megaByte < 1) {
            BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));
            return result1.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "KB";
        }

        double gigaByte = megaByte/1024;
        if(gigaByte < 1) {
            BigDecimal result2  = new BigDecimal(Double.toString(megaByte));
            return result2.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "MB";
        }

        double teraBytes = gigaByte/1024;
        if(teraBytes < 1) {
            BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));
            return result3.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "GB";
        }
        BigDecimal result4 = new BigDecimal(teraBytes);
        return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "TB";
    }

    public static boolean isIntentAvailable(Context context, Intent intent) {
        final PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent,
                PackageManager.GET_ACTIVITIES);
        return list.size() > 0;
    }

    // android获取一个用于打开HTML文件的intent
    public static Intent getHtmlFileIntent(String param) {
        Uri uri = Uri.parse(param).buildUpon().encodedAuthority("com.android.htmlfileprovider").scheme("content").encodedPath(param).build();
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.setDataAndType(uri, "text/html");
        return intent;
    }

    // android获取一个用于打开图片文件的intent
    public static Intent getImageFileIntent(String param) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "image/*");
        return intent;
    }

    // android获取一个用于打开PDF文件的intent
    public static Intent getPdfFileIntent(String param) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "application/pdf");
        return intent;
    }

    // android获取一个用于打开文本文件的intent
    public static Intent getTextFileIntent(String param, boolean paramBoolean) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (paramBoolean) {
            Uri uri1 = Uri.parse(param);
            intent.setDataAndType(uri1, "text/plain");
        } else {
            Uri uri2 = Uri.fromFile(new File(param));
            intent.setDataAndType(uri2, "text/plain");
        }
        return intent;
    }

    // android获取一个用于打开音频文件的intent
    public static Intent getAudioFileIntent(String param) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("oneshot", 0);
        intent.putExtra("configchange", 0);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "audio/*");
        return intent;
    }

    // android获取一个用于打开视频文件的intent
    public static Intent getVideoFileIntent(String param) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("oneshot", 0);
        intent.putExtra("configchange", 0);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "video/*");
        return intent;
    }

    // android获取一个用于打开CHM文件的intent
    public static Intent getChmFileIntent(String param) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "application/x-chm");
        return intent;
    }

    // android获取一个用于打开Word文件的intent
    public static Intent getWordFileIntent(String param) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "application/msword");
        return intent;
    }

    // android获取一个用于打开Excel文件的intent
    public static Intent getExcelFileIntent(String param) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "application/vnd.ms-excel");
        return intent;
    }

    // android获取一个用于打开PPT文件的intent
    public static Intent getPptFileIntent(String param) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
        return intent;
    }

    //文件是否存在
    //AFile= "/storage/sdcard0/Manual/test.pdf"
    public static boolean fileIsExists(String AFile)
    {
        boolean ret = false;
        try {
            File f = new File(AFile);
            ret = f.exists();
        } catch (Exception e) {
            // TODO: handle exception
            ret = false;
        }
        return ret;
    }

    public static void openFile(Context context, String path)
    {
        if (!fileIsExists(path))
        {
            FNTools.showMessageBox(context, "文件不存在！");
            return;
        }
        String ext = getFileExtName(path).toLowerCase();

        if (ext.equals("pdf"))
        {
            Intent it = getPdfFileIntent(path);
            if (it != null)
                context.startActivity(it);
        }
        else if (ext.equals("jpg") || ext.equals("jpeg") || ext.equals("png")
                || ext.equals("bmp"))
        {
            Intent it = getImageFileIntent(path);
            if (it != null)
                context.startActivity(it);
        }
        else if (ext.equals("htm") || ext.equals("html") )
        {
            Intent it = getHtmlFileIntent(path);
            if (it != null)
                context.startActivity(it);
        }
        else if ( ext.equals("doc") || ext.equals("docx"))
        {
            Intent it = getWordFileIntent(path);
            if (it != null)
                context.startActivity(it);
        }
        else if (ext.equals("ppt") || ext.equals("pptx") )
        {
            Intent it = getPptFileIntent(path);
            if (it != null)
                context.startActivity(it);
        }
        else if (ext.equals("xls") || ext.equals("xlsx"))
        {
            Intent it = getExcelFileIntent(path);
            if (it != null)
                context.startActivity(it);
        }
        else if (ext.equals("mp3"))
        {
            Intent it = getAudioFileIntent(path);
            if (it != null)
                context.startActivity(it);
        }
        else if (ext.equals("txt"))
        {
            Intent it = getTextFileIntent(path, false);
            if (it != null)
                context.startActivity(it);
        }
        else if (ext.equals("chm"))
        {
            Intent it = getChmFileIntent(path);
            if (it != null)
                context.startActivity(it);
        }
        else if (ext.equals("mp4") || ext.equals("avi") || ext.equals("mpg")
                || ext.equals("mpeg") || ext.equals("rm") || ext.equals("rmvb"))
        {
            Intent it = getVideoFileIntent(path);
            if (it != null)
                context.startActivity(it);
        }
    }

    /**
     * 获取文件夹大小
     * @param file File实例
     * @return long
     */
    public static long getFolderSize(java.io.File file)
    {

        long size = 0;
        try {
            java.io.File[] fileList = file.listFiles();
            for (int i = 0; i < fileList.length; i++)
            {
                if (fileList[i].isDirectory())
                {
                    size = size + getFolderSize(fileList[i]);

                }else{
                    size = size + fileList[i].length();

                }
            }
        } catch (Exception e) {
            FNTools.FLog("获取文件夹大小失败：" + e.getMessage());
            e.printStackTrace();
        }
        //return size/1048576;
        return size;
    }

    //获得缓存路径
    public static String getCachePath(Context context)
    {
        File f = context.getExternalCacheDir();
        if (f == null)
        {
            f = context.getCacheDir();
        }
        return f.getPath() + "/";
    }

    /*
     * Java文件操作 获取文件扩展名
     *
     */
    public static String getFileExtName(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot >-1) && (dot < (filename.length() - 1))) {
                return filename.substring(dot + 1);
            }
        }
        return filename;
    }

    /*
     * Java文件操作 获取不带扩展名的文件名
     */
    public static String getFileNameWithoutExt(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot >-1) && (dot < (filename.length()))) {
                return filename.substring(0, dot);
            }
        }
        return filename;
    }

    //从URL中得到文件名
    public static String pickFileNameFromUrl(String AUrl) {
        return AUrl.substring(AUrl.lastIndexOf("/") + 1);
    }

    public static void createFolder(String APath) {
        File pt = new File(APath);
        if (!pt.exists()) {
            pt.mkdirs();
        }
    }

    //删除文件
    public static void deleteFile(String AName) {
        File F = new File(AName);
        if (F.exists()) { // 判断文件是否存在
            if (F.isFile()) { // 判断是否是文件
                F.delete(); // delete()方法 你应该知道 是删除的意思;
            }
        } else {

        }
    }

    //删除文件夹
    public static void deleteFolder(String AName) {
        File F = new File(AName);
        if (F.exists()) { // 判断文件是否存在
            if (F.isDirectory()) { // 否则如果它是一个目录
                File files[] = F.listFiles(); // 声明目录下所有的文件 files[];
                for (int i = 0; i < files.length; i++) { // 遍历目录下所有的文件
                    deleteFolder(files[i].getName()); // 把每个文件 用这个方法进行迭代
                }
            }
            F.delete();
        }
    }


    private static class DownloadData implements Serializable {

        public String  DownUrl = "";//下载URL
        public String  SavePath = "";//保存路径
        public String  SaveFileName = "";//保存文件名
        public Handler Progresshwnd = null;//进度条回调句柄

        //以下是运行期数据，上面是运行前数据。
        public boolean Complete = false;//是否完成
        public int     Percent = 0;//完成百分比例
        public boolean canceled = false;
        public long     fileSize = 0;
        public long     writesize = 0;
        public Boolean isSucc = false;
        DownfileCallBack downfileCallBack = null;
    }

    private static void sendMsg(DownloadData Info) {
        DownloadData objData = new DownloadData();
        Message msg = new Message();
        Bundle bd = new Bundle();
        bd.putString("savepath", Info.SavePath);
        bd.putBoolean("iscomplete", Info.Complete);
        bd.putInt("percent", Info.Percent);
        bd.putLong("filesize", Info.fileSize);
        bd.putLong("writesize", Info.writesize);
        bd.putBoolean("issucc", Info.isSucc);
        bd.putBoolean("canceled", Info.canceled);
        bd.putString("downurl", Info.DownUrl);
        bd.putString("filename", Info.SaveFileName);

        msg.setData(bd);
        Info.Progresshwnd.sendMessage(msg);
    }


    public interface DownfileCallBack {
        boolean   isInterrept();
    }

    public static  void downLoadFile(String ADownUrl, String ASavePath, String AFileName, Handler AProgressHwnd)
    {
        downLoadFile(null , ADownUrl, ASavePath, AFileName, AProgressHwnd, null);
    }

    public static  void downLoadFile(Context context, String ADownUrl, String ASavePath, String AFileName, Handler AProgressHwnd)
    {
        downLoadFile(context , ADownUrl, ASavePath, AFileName, AProgressHwnd, null);
    }

    public static  void downLoadFile(Context context, String ADownUrl, String ASavePath, String AFileName, Handler AProgressHwnd, DownfileCallBack ncallback) {
        final DownloadData info = new DownloadData();
        createFolder(ASavePath);
        info.DownUrl = ADownUrl;
        info.SavePath = ASavePath;
        info.downfileCallBack = ncallback;
        if (!info.SavePath.endsWith("/")) {
            info.SavePath = info.SavePath + "/";
        }
        info.SaveFileName = AFileName;
        info.Progresshwnd = AProgressHwnd;

        final AsyncHttpClient httpClient = new AsyncHttpClient();
        File file = new File(info.SavePath, info.SaveFileName);

        final RequestHandle  request = httpClient.get(context, ADownUrl, new FileAsyncHttpResponseHandler(file)
            {
                @Override
                public void onSuccess(int var1, Header[] var2, File var3) {
                    info.isSucc = true;
                    info.Complete = true;
                    info.Percent = 100;
                    sendMsg(info);
                }

                @Override
                public void onFailure(int var1, Header[] var2, Throwable var3, File var4) {
                    info.isSucc = false;
                    info.Complete = true;
                    info.Percent = 100;
                    sendMsg(info);
                }

                @Override
                public void onProgress(long bytesWritten, long totalSize) {
                    double val = (bytesWritten * 100) / totalSize;
                    info.Percent = (int) val;
                    info.fileSize = totalSize;

                    info.writesize = bytesWritten;

                    if (info.downfileCallBack != null && info.downfileCallBack.isInterrept())
                    {
                        info.isSucc = false;
                        info.Complete = true;
                        info.canceled = true;
                        httpClient.cancelAllRequests(true);
                    }
                    sendMsg(info);
                }
        });
    }

}
