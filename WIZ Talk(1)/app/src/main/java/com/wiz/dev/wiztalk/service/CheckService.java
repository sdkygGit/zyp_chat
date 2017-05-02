package com.wiz.dev.wiztalk.service;//package com.yxst.epic.unifyplatform.service;
//
//import android.app.Service;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.content.pm.PackageManager;
//import android.os.Environment;
//import android.os.IBinder;
//import android.os.RemoteException;
//import android.util.Log;
//import android.widget.Toast;
//
//import com.morgoo.droidplugin.pm.PluginManager;
//
//import static com.morgoo.helper.compat.PackageManagerCompat.INSTALL_FAILED_NOT_SUPPORT_ABI;
//import static com.morgoo.helper.compat.PackageManagerCompat.INSTALL_SUCCEEDED;
//
///**
// * Created by hekang on 2016/5/6.
// */
//public class CheckService extends Service {
//    private static final String openPdf = "openPdf";
//    private static final String openWord = "openWord";
//    private static final String openExcle = "openExcle";
//    private static final String openPpt = "openPpt";
//    private String command, filePath;
//    private SharedPreferences sharedPreferences;
//
//    @Override
//    public IBinder onBind(Intent intent) {
//        return null;
//    }
//
//    @Override
//    public void onCreate() {
//        super.onCreate();
//        sharedPreferences = getSharedPreferences("AllApp", MODE_PRIVATE);
//    }
//
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        Log.d("CheckService", "startService--onStartCommand");
//        new CheckThread().start();
//        return super.onStartCommand(intent, flags, startId);
//    }
//
//    private class CheckThread extends Thread {
//        @Override
//        public void run() {
//            Log.d("CheckService", "startService--CheckThread");
//            command = "openPdf";
//            while (true) {
//                filePath = sharedPreferences.getString("filePath", "");
//                command = sharedPreferences.getString("command", "");
//                // TODO: 2016/5/6 开启解码文件
//                if (command.equals(openPdf)) {
//                    Log.d("CheckService", "startService--CheckThread--command" + command);
//                    Log.d("CheckService", "startService--CheckThread--filePath" + filePath);
//                    sharedPreferences.edit().putString("command", "").apply();
//                    initOpenBundle();
////                    Intent i = new Intent();
////                    i.setClassName(CheckService.this, "com.example.mypdfview.MainActivity");
////                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
////                    i.putExtra("filePath", filePath);
////                    startActivity(i);
//                } /*else if (command.equals(openWord)) {
//                    sharedPreferences.edit().putString("command", "").apply();
//                    Intent i2 = new Intent();//com.example.mywordview.FileChooser
//                    i2.setClassName(CheckService.this, "com.example.mywordview.FileChooser");
//                    i2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    i2.putExtra("filePath", filePath);
//                    startActivity(i2);
//                } else if (command.equals(openExcle)) {
//                    sharedPreferences.edit().putString("command", "").apply();
//                    Intent i3 = new Intent();
//                    i3.setClassName(CheckService.this, "com.example.myexcle.MainActivity");
//                    i3.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    i3.putExtra("filePath", filePath);
//                    startActivity(i3);
//                } else if (command.equals(openPpt)) {
//                    sharedPreferences.edit().putString("command", "").apply();
//                    Intent i4 = new Intent();
//                    i4.setClassName(CheckService.this, "com.example.myppt.MainActivity");
//                    i4.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    i4.putExtra("filePath", filePath);
//                    startActivity(i4);
//                }*/
//            }
//        }
//    }
//
//    private void initOpenBundle() {
////        String packageName = "com.example.hekang.myapplication";
////        String packageName = "com.artifex.mupdfdemo";
//        String packageName = "com.artifex.mupdf";
////        String packageName = "org.geometerplus.zlibrary.ui.android";
////        String fileName = "ChoosePDFActivity.apk";
//        String fileName = "MyPDF.apk";
//        if (!PluginManager.getInstance().isConnected()) {
//            Toast.makeText(this, "插件服务正在初始化，请稍后再试。。。", Toast.LENGTH_SHORT).show();
//        }
//        try {
//            if (PluginManager.getInstance().getPackageInfo(packageName, 0) != null) {
//                PackageManager pm = getPackageManager();
//                Intent intent = pm.getLaunchIntentForPackage(packageName);//应用的包名+application
//                intent.putExtra("filePath", filePath);
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(intent);
//                Log.d("hk", "start");
//            } else {///storage/emulated/0/wizplant-release.apk
//                doInstall(Environment.getExternalStorageDirectory().getAbsolutePath()
//                        + "/wizTalkPulg/" + fileName, packageName);//这个地方是插件存放的地址
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            try {
//                PluginManager.getInstance().installPackage(Environment.getExternalStorageDirectory().getAbsolutePath()
//                        + "/wizTalkPulg/" + fileName, 0);
//            } catch (RemoteException e1) {
//                e1.printStackTrace();
//            }
//        }
//    }
//
//    private synchronized void doInstall(String filePath, String packageName) {//插件保存的地址和插件的包名+application
//        try {
//            final int re = PluginManager.getInstance().installPackage(filePath, 0);
//            switch (re) {
//                case PluginManager.INSTALL_FAILED_NO_REQUESTEDPERMISSION:
//                    Toast.makeText(this, "安装失败，文件请求的权限太多", Toast.LENGTH_SHORT).show();
//                    break;
//                case INSTALL_FAILED_NOT_SUPPORT_ABI:
//                    Toast.makeText(this, "宿主不支持插件的abi环境，可能宿主运行时为64位，但插件只支持32位", Toast.LENGTH_SHORT).show();
//                    break;
//                case INSTALL_SUCCEEDED:
//                    Toast.makeText(this, "安装完成", Toast.LENGTH_SHORT).show();
//                    PackageManager pm = getPackageManager();
//                    Intent intent = pm.getLaunchIntentForPackage(packageName);//应用的包名+application
//                    intent.putExtra("filePath", filePath);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    startActivity(intent);
//                    break;
//            }
//        } catch (RemoteException e) {
//            e.printStackTrace();
//        }
//    }
//}
