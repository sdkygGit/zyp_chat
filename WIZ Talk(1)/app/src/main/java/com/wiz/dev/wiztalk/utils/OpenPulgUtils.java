package com.wiz.dev.wiztalk.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import com.morgoo.droidplugin.pm.PluginManager;

import java.io.File;

import static com.morgoo.helper.compat.PackageManagerCompat.INSTALL_FAILED_NOT_SUPPORT_ABI;
import static com.morgoo.helper.compat.PackageManagerCompat.INSTALL_SUCCEEDED;

/**
 * Created by hekang on 2016/5/14.
 */
public class OpenPulgUtils {
    private Context context;

    public OpenPulgUtils(Context context) {
        this.context = context;
    }

    public void installPulg(String filePath) {//安装插件
        if (!PluginManager.getInstance().isConnected()) {
            Toast.makeText(context, "插件服务正在初始化，请稍后再试。。。", Toast.LENGTH_SHORT).show();
        }
        try {
            final int re = PluginManager.getInstance().installPackage(filePath, 0);
            switch (re) {
                case PluginManager.INSTALL_FAILED_NO_REQUESTEDPERMISSION:
                    Toast.makeText(context, "安装失败，文件请求的权限太多", Toast.LENGTH_SHORT).show();
                    break;
                case INSTALL_FAILED_NOT_SUPPORT_ABI:
                    Toast.makeText(context, "宿主不支持插件的abi环境，可能宿主运行时为64位，但插件只支持32位", Toast.LENGTH_SHORT).show();
                    break;
                case INSTALL_SUCCEEDED:
                    Toast.makeText(context, "安装完成", Toast.LENGTH_SHORT).show();
                    break;
            }
        } catch (RemoteException e) {
            e.printStackTrace();
            Toast.makeText(context, "插件安装失败，请重新安装", Toast.LENGTH_SHORT).show();
        }
    }

    public void uninstallPulg(String packageName) {//卸载插件
        if (!PluginManager.getInstance().isConnected()) {
            Toast.makeText(context, "插件服务正在初始化，请稍后再试。。。", Toast.LENGTH_SHORT).show();
        }
        try {
            if (PluginManager.getInstance().getPackageInfo(packageName, 0) != null) {
                PluginManager.getInstance().deletePackage(packageName, 0);
            } else {
                Toast.makeText(context, "插件还未安装不用卸载", Toast.LENGTH_SHORT).show();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void openPulg(String packageName, String data, int position) {//打开插件
        try {
            if (PluginManager.getInstance().getPackageInfo(packageName, 0) != null) {
                PackageManager pm = context.getPackageManager();
                Intent intent = pm.getLaunchIntentForPackage(packageName);//应用的包名+application
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("openActivity", position);
                intent.putExtra("filePath", data);
                context.startActivity(intent);
                Log.d("hk", "start");
            } else {///storage/emulated/0/wizplant-release.apk
                Toast.makeText(context, "插件还未安装，请先安装", Toast.LENGTH_SHORT).show();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void deletePulg(String packageName, String filePath) {//删除插件,删除前要先卸载
        try {
            if (PluginManager.getInstance().getPackageInfo(packageName, 0) != null) {
                Toast.makeText(context, "请先卸载插件，再删除", Toast.LENGTH_SHORT).show();
            } else {
                if (new File(filePath).delete()) {
                    Toast.makeText(context, "插件删除成功", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
