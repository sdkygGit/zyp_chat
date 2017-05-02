package com.wiz.dev.wiztalk.crash;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.os.Process;
import android.util.Log;
import android.widget.Toast;

import com.epic.traverse.push.util.L;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 通用的异常记录、上报类，用于替换系统默认的异常捕获
 * 在application中初始化即可，
 * 其中，log的目录为应用安装的cache目录，会随着应用被卸载而删除
 * //sdcard/Android/data/<application package>/cache 
 * Created by Dong on 2015/12/17.
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {
    
    private static final String TAG = CrashHandler.class.getSimpleName();
    private static final boolean DEBUG = true;

    //放在缓存目录更好，卸载程序时，会一并删除
//    private static final String PATH = 
//            Environment.getExternalStorageDirectory().getPath() + "/EPIC/log/";
    private static final String FILE_NAME = "crash";

    
    //log文件的后缀名  
    private static final String FILE_NAME_SUFFIX = ".txt";
    
    //系统默认的异常处理
    private Thread.UncaughtExceptionHandler mUncaughtExceptionHandler;

    private CrashHandler(){};

    private static CrashHandler sInstance = new CrashHandler();

    public static CrashHandler getInstance() {
        return sInstance;
    }

    private Context mContext;

    /**
     * 初始化
     * @param context
     */
    public void init(Context context){
        //获取系统默认的异常实例
        mUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
        mContext = context;
        
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        
        dumpException2SDCard(ex);
        //考虑异常上报的时机。弹出对话框，询问用户上报异常信息
        dumpException2NetServers(ex);
        // TODO: 2016/3/16  
//        showToast(mContext,"程序遭遇异常，即将退出!");
        showToast(mContext,ex.getMessage());

        try {
            Thread.sleep(1000*2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //如下的退出方式只会结束TASK中的栈顶activity，可以考虑通过回调在application中finish所有的activity
        if (mUncaughtExceptionHandler != null) {
            mUncaughtExceptionHandler.uncaughtException(thread, ex);
        } else {
            Process.killProcess(Process.myPid());
            System.exit(1);
        }
    }

    private void showToast(final Context context,final String message) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                Looper.loop();
            }
        }).start();
    }

    /**
     * save the exception to sd files
     * @param ex
     */
    private void dumpException2SDCard(Throwable ex) {
        //如果SD卡不存在或无法使用，则无法把异常信息写入SD卡  
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            if (DEBUG) {
                Log.w(TAG, "sdcard unmounted,skip dump exception");
                return;
            }
        }
        //获取 cache目录中的log目录
        String filePath = getDiskCacheDir(mContext,"log");
        File dir = new File(filePath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        long current = System.currentTimeMillis();
        String time = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date(current));
        //以当前时间创建log文件  
        File file = new File(filePath +"/"+ FILE_NAME + time + FILE_NAME_SUFFIX);
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(new BufferedWriter(new FileWriter(file)));
            //导出发生异常的时间  
            pw.println(time);

            //导出手机信息  
            dumpPhoneInfo(pw);

            pw.println();
            //导出异常的调用栈信息  
            ex.printStackTrace(pw);
           
        } catch (Exception e) {
            Log.e(TAG, "dump crash info failed");
            e.printStackTrace();
        }finally {
            if (pw!=null){
                pw.close();
            }
           
        }
    }

    /**
     * 
     * @param context
     * @param fileName 文件夹的名字，将bitmap和log之类分开存放
     * @return
     */
    private String getDiskCacheDir(Context context,String fileName){
        String cachePath;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            ///sdcard/Android/data/<application package>/cache 
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            ///data/data/<application package>/cache
            cachePath = context.getCacheDir().getPath();
        }
        return cachePath+"/"+fileName;
    }
    
    private void dumpPhoneInfo(PrintWriter pw) throws PackageManager.NameNotFoundException {
        //应用的版本名称和版本号  
        PackageManager pm = mContext.getPackageManager();
        PackageInfo pi = pm.getPackageInfo(mContext.getPackageName(), PackageManager.GET_ACTIVITIES);
        pw.print("App Version: ");
        pw.print(pi.versionName);
        pw.print('_');
        pw.println(pi.versionCode);

        //android版本号  
        pw.print("OS Version: ");
        pw.print(Build.VERSION.RELEASE);
        pw.print("_");
        pw.println(Build.VERSION.SDK_INT);

        //手机制造商  
        pw.print("Vendor: ");
        pw.println(Build.MANUFACTURER);

        //手机型号  
        pw.print("Model: ");
        pw.println(Build.MODEL);

        //cpu架构  
        pw.print("CPU ABI: ");
        pw.println(Build.CPU_ABI);//consider os version
    }

    /**
     * upload the ex to servers;
     * @param ex
     */
    private void dumpException2NetServers(Throwable ex){
        L.d(TAG, "dumpException2NetServers()");
    }
}
