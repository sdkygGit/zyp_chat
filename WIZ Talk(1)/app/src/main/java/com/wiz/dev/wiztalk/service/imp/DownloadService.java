package com.wiz.dev.wiztalk.service.imp;

import android.app.IntentService;
import android.content.Intent;
import android.os.Environment;
import android.view.View;
import android.widget.Toast;

import com.wiz.dev.wiztalk.view.ProgressView;

import java.io.File;

import cn.finalteam.okhttpfinal.FileDownloadCallback;
import cn.finalteam.okhttpfinal.HttpRequest;
import cn.finalteam.okhttpfinal.OkHttpFinal;
import cn.finalteam.okhttpfinal.OkHttpFinalConfiguration;

/**
 * Created by zhangyupeng on 2016/4/26.
 */
public class DownloadService extends IntentService {

    public DownloadService() {
        super("DownloadService");
    }

    String filePath;
    public static ProgressView mProgressView;

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            filePath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/fileName.apk";
            OkHttpFinalConfiguration.Builder builder = new OkHttpFinalConfiguration.Builder();
            OkHttpFinal.getInstance().init(builder.build());

            String url = intent.getStringExtra("url");
            final File saveFile = new File(filePath);


            if(saveFile.exists())
                saveFile.delete();
            HttpRequest.download(url, saveFile, new FileDownloadCallback() {
                @Override
                public void onStart() {
                    super.onStart();
                    if (mProgressView != null)
                        mProgressView.setVisibility(View.VISIBLE);
                }

                //下载进度
                @Override
                public void onProgress(int progress, long networkSpeed) {
                    super.onProgress(progress, networkSpeed);
                    if (mProgressView != null)
                        mProgressView.setProgress(progress);
                }

                //下载失败
                @Override
                public void onFailure() {
                    super.onFailure();
                    if (mProgressView != null)
                        mProgressView.setText("下载失败");
                    Toast.makeText(DownloadService.this, "下载失败", Toast.LENGTH_SHORT).show();
                }

                //下载完成（下载成功）
                @Override
                public void onDone() {
                    if (mProgressView != null)
                        mProgressView.setText("下载完成");
                }
            });
        }
    }


}
