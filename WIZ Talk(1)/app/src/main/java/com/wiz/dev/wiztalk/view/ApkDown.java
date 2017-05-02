package com.wiz.dev.wiztalk.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fanning.library.FNFile;
import com.fanning.library.FNPopUpWindow;
import com.fanning.library.FNTools;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.wiz.dev.wiztalk.R;
import com.wiz.dev.wiztalk.public_store.PublicTools;

import java.io.File;

/**
 * Created by ynyxmac on 16/3/14.
 */
public class ApkDown {

    private  FNPopUpWindow pwin;
    private  TextView tvtitle ,tvSize;
    ProgressBar progressBar;
    Context context;
    Fragment fragment;
    Activity activity;
    String appname;
    int requestCode ;
    boolean isStop = false;

    public   void StartDownApk(Context context, Fragment fragment, View startView, String appname, String downUrl,
                               String filename, String iconUrl,
                               int requestCode)
    {

        this.activity = (Activity)context;
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int height = dm.heightPixels;
        this.context = context;
        this.fragment = fragment;
        this.appname = appname;
        this.requestCode = requestCode;

        String downpath = PublicTools.getSDCardPath(context) + "/apk/";

//        if (FNFile.fileIsExists(downpath + filename))
//        {
//            // 通过Intent安装APK文件
//            Intent i = new Intent(Intent.ACTION_VIEW);
//            i.setDataAndType(Uri.parse("file://" + downpath + filename),
//                    "application/vnd.android.package-archive");
//            if (fragment !=null)
//                fragment.startActivityForResult(i, requestCode);
//            else
//                activity.startActivityForResult(i, requestCode);
//            return;
//        }

        pwin = new FNPopUpWindow(context ,R.layout.popupdownwin, dm.widthPixels ,height,true ,false);

//        LinearLayout linearLayout = (LinearLayout)pwin.MainWindow.findViewById(R.id.popwin_mainwin);
//        linearLayout.setFocusable(true);
//        linearLayout.setFocusableInTouchMode(true);
        FrameLayout cancellayout = (FrameLayout)pwin.MainWindow.findViewById(R.id.popdown_cancel);




        tvtitle = (TextView)pwin.MainWindow.findViewById(R.id.popdown_title);
        tvtitle.setText("正在下载" + appname);
        tvtitle.setFocusableInTouchMode(true);
        tvtitle.setFocusable(true);
        tvtitle.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_BACK) {
                    ShowAskDialog();
                    return true;
                }
                return false;
            }
        });


        tvSize = (TextView)pwin.MainWindow.findViewById(R.id.popdown_size);

        if (FNTools.emptyString(iconUrl))
        {
            ImageView iv = (ImageView) pwin.MainWindow.findViewById(R.id.iv_icon);

            DisplayImageOptions options = new DisplayImageOptions.Builder()
                    .showImageOnLoading(R.mipmap.appicon)
                    .showImageForEmptyUri(R.mipmap.appicon)
                    .showImageOnFail(R.mipmap.appicon)
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .displayer(new SimpleBitmapDisplayer())
                    .build();
            ImageLoader.getInstance().displayImage(iconUrl, iv, options);
        }

        progressBar = (ProgressBar)pwin.MainWindow.findViewById(R.id.popdown_progress);
        progressBar.setProgress(0);

        cancellayout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ShowAskDialog();
            }
        });

        pwin.ShowWindow(startView);

        FNFile.DownfileCallBack downfileCallBack = new FNFile.DownfileCallBack() {
            @Override
            public boolean isInterrept() {
                return isStop;
            }
        };

        FNFile.downLoadFile(context, downUrl, downpath,
                filename,
                DownFileMsgDown, downfileCallBack);
    }

    void ShowAskDialog()
    {
        FNTools.showAskMessage(ApkDown.this.context, ApkDown.this.appname, "确定终止：" + ApkDown.this.appname + "下载?",
                new FNTools.MsgResultCallback() {
                    @Override
                    public String OnResult(String AVal, Object v) {
                        String packagename = (String) v;
                        if (FNTools.emptyString(packagename)) return null;
                        if (AVal.toLowerCase().equals("true")) {
                            isStop = true;
                        }
                        return null;
                    }
                });
    }

    private Handler DownFileMsgDown=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            Bundle bd=msg.getData();
            String SavePath= bd.getString("savepath");
            boolean isComplete= bd.getBoolean("iscomplete");
            int Percert= bd.getInt("percent");
            boolean isSucc= bd.getBoolean("issucc");
            boolean canceled= bd.getBoolean("canceled");
            long fileSize = bd.getLong("filesize");
            long writesize = bd.getLong("writesize");

            String saveFilename=bd.getString("filename");

            if (canceled)
            {
                pwin.close();
            }
            else if (!isComplete)
            {
                if (progressBar!=null)
                {
                    progressBar.setProgress(Percert);
                    progressBar.setSecondaryProgress(Percert);
                }
                tvSize.setText(FNFile.getFormatSize(writesize) + "/" + FNFile.getFormatSize(fileSize));
            }
            else
            {
                if (isSucc)
                {
                    if (progressBar!=null)
                    {
                        progressBar.setProgress(100);
                        progressBar.setSecondaryProgress(100);
                    }
                    tvSize.setText(FNFile.getFormatSize(fileSize) + "/" + FNFile.getFormatSize(fileSize));
                    pwin.close();

                    File apkfile = new File(SavePath+saveFilename);

                    if (!apkfile.exists()) {
                        return;
                    }
                    // 通过Intent安装APK文件
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setDataAndType(Uri.fromFile(apkfile),
                            "application/vnd.android.package-archive");
                    if (fragment !=null)
                        fragment.startActivityForResult(i, requestCode);
                    else
                        activity.startActivityForResult(i, requestCode);

                }
            }
            super.handleMessage(msg);
        }
    };
}
