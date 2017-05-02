package com.wiz.dev.wiztalk.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.artifex.mupdfdemo.MuPDFActivity;
import com.lidroid.xutils.util.MimeTypeUtils;
import com.wiz.dev.wiztalk.utils.OpenPulgUtils;

import java.io.File;

/**
 * Created by hekang on 2016/5/14.
 */
public class OpenPulgReservicer extends BroadcastReceiver {
    private String filePath, packageName, fileName;

    @Override
    public void onReceive(Context context, Intent intent) {
        OpenPulgUtils openPulgUtils = new OpenPulgUtils(context);
        String command = intent.getStringExtra("command");
        String filePath = intent.getStringExtra("filePath");
        Uri uri = intent.getData();
        String packageName = intent.getStringExtra("packageName");
        String fileName = intent.getStringExtra("fileName");
        Log.d("OpenPulgReservicer", "onReceive");
        if (command.equals("openPdf")) {//open
            Log.d("CheckService", "onReceive");
            Intent intent1 = new Intent(context, MuPDFActivity.class);
            intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent1.setAction(Intent.ACTION_VIEW);
            intent1.setData(Uri.parse(filePath));
            intent1.putExtra("filePath", filePath);
            context.startActivity(intent1);
          /*  if (new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/wizTalkPulg/" + "MyPDF.apk").exists()) {//判断PDF插件是否已经存在
                openPulgUtils.openPulg("com.artifex.mupdf", filePath, 0);
            } else {
                Toast.makeText(context, "PDF插件不存在，如需使用，请先下载插件", Toast.LENGTH_SHORT).show();
                openFile(context, filePath);
            }*/
        }
    }

    private void openFile(Context context, String filePath) {
        File file = new File(filePath);
        Intent intent2 = new Intent();
        intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//设置intent的Action属性
        intent2.setAction(Intent.ACTION_VIEW);//获取文件file的MIME类型
        String type = MimeTypeUtils.getMimeType(file.getName());//设置intent的data和Type属性。
        intent2.setDataAndType(Uri.fromFile(file), type);
        try {
            context.startActivity(intent2); //这里最好try一下，有可能会报错。 //比如说你的MIME类型是打开邮箱，但是你手机里面没装邮箱客户端，就会报错。
        } catch (Exception e) {
            Toast.makeText(context, "没有安装相应的软件，请安装后打开", Toast.LENGTH_SHORT).show();
        }
    }
}
