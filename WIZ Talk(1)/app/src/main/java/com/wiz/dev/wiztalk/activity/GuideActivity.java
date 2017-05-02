package com.wiz.dev.wiztalk.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.wiz.dev.wiztalk.MyApplication;
import com.wiz.dev.wiztalk.R;
import com.wiz.dev.wiztalk.apppush.view.widget.CircleIndicator;
import com.wiz.dev.wiztalk.public_store.OpenfireConstDefine;
import com.wiz.dev.wiztalk.utils.SaveConfig;
import com.wiz.dev.wiztalk.view.dialog.MyAlertDialog;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.finalteam.okhttpfinal.FileDownloadCallback;
import cn.finalteam.okhttpfinal.HttpRequest;

/**
 * Created by hekang on 2016/5/9.
 */
public class GuideActivity extends Activity {
    private ViewPager viewPager;
    private ArrayList<View> views;
    private int[] pics = {R.drawable.guide1, R.drawable.guide2, R.drawable.guide3};
    private CircleIndicator circleIndicator;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().hide();
        setContentView(R.layout.activity_guide);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("初始化APP配置，请稍候...");
        viewPager = (ViewPager) findViewById(R.id.guide_viewPager);
        circleIndicator = (CircleIndicator) findViewById(R.id.indicator);
        initData();
        List<PackageInfo> packages = getPackageManager().getInstalledPackages(0);
        for (int i = 0; i < packages.size(); i++) {
            PackageInfo packageInfo = packages.get(i);
            if (packageInfo.packageName.equals("com.yxst.epic.unifyplatform")) {


                new MyAlertDialog(GuideActivity.this, "发现旧版本", "请卸载旧版本？", "取消", "确定", new MyAlertDialog.OnMyDialogBtnClickListener() {
                    @Override
                    public void onOkClick() {
                        Uri packageURI = Uri.parse("package:com.yxst.epic.unifyplatform");
                        //创建Intent意图
                        Intent intent = new Intent(Intent.ACTION_DELETE,packageURI);
                        //执行卸载程序
                        startActivity(intent);
                    }

                    @Override
                    public void onCancleClick() {
                    }
                }).showDialog();

                break;
            }
        }
    }

    private void initData() {
        View view1 = LayoutInflater.from(this).inflate(R.layout.guide_item, null);
        view1.findViewById(R.id.guide_layout).setBackgroundResource(pics[0]);
        View view2 = LayoutInflater.from(this).inflate(R.layout.guide_item, null);
        view2.findViewById(R.id.guide_layout).setBackgroundResource(pics[1]);
        View view3 = LayoutInflater.from(this).inflate(R.layout.guide_item, null);
        view3.findViewById(R.id.guide_layout).setBackgroundResource(pics[2]);

        Button guide_button = (Button) view3.findViewById(R.id.guide_button);
        guide_button.setVisibility(View.VISIBLE);
        guide_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                progressDialog.show();
//                initDownPDF();
                Intent intent = new Intent(GuideActivity.this, LoginActivity_.class);
                startActivity(intent);
                SharedPreferences in = getSharedPreferences("in_first", Context.MODE_PRIVATE);
                in.edit().putBoolean("intoAppDelay", true).commit();
                finish();
            }
        });
        views = new ArrayList<View>();
        views.add(view1);
        views.add(view2);
        views.add(view3);

        // 设置数据
        viewPager.setAdapter(pagerAdapter);

        circleIndicator.setViewPager(viewPager);
    }

    private void initDownPDF() {
        String downloadUrl = "http://112.74.104.234/apk/plugins/MyPDF.apk";
        String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/wizTalkPulg/" + "MyPDF.apk";
        final File saveFile = new File(filePath);
        if (!saveFile.exists()) {
            HttpRequest.download(downloadUrl, saveFile, new FileDownloadCallback() {
                @Override
                public void onStart() {
                    super.onStart();
                }

                //下载进度
                @Override
                public void onProgress(int progress, long networkSpeed) {
                    super.onProgress(progress, networkSpeed);
                }

                //下载失败
                @Override
                public void onFailure() {
                    super.onFailure();
                    progressDialog.dismiss();
                    Toast.makeText(GuideActivity.this, "初始化APP配置失败，请重新安装", Toast.LENGTH_SHORT).show();
                    if (saveFile.exists())
                        saveFile.delete();
                }

                //下载完成（下载成功）
                @Override
                public void onDone() {
                    Log.d("hk", "initDownPDF--onDone");
                    Intent intent = new Intent(GuideActivity.this, LoginActivity_.class);
                    startActivity(intent);
                    progressDialog.dismiss();
                    SharedPreferences in = getSharedPreferences("in_first", Context.MODE_PRIVATE);
                    in.edit().putBoolean("intoAppDelay", true).commit();
                    finish();
                }
            });
        } else {//存在就不用再下，直接跳转走
            Intent intent = new Intent(GuideActivity.this, LoginActivity_.class);
            startActivity(intent);
            progressDialog.dismiss();
            SharedPreferences in = getSharedPreferences("in_first", Context.MODE_PRIVATE);
            in.edit().putBoolean("intoAppDelay", true).commit();
            finish();
        }
    }

    private PagerAdapter pagerAdapter = new PagerAdapter() {
        /**
         * 获得当前界面数
         */
        @Override
        public int getCount() {
            if (views != null) {
                return views.size();
            } else return 0;
        }

        /**
         * 判断是否由对象生成界面
         */
        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return (arg0 == arg1);
        }

        /**
         * 销毁position位置的界面
         */
        @Override
        public void destroyItem(View container, int position, Object object) {
            ((ViewPager) container).removeView(views.get(position));
        }

        /**
         * 初始化position位置的界面
         */
        @Override
        public Object instantiateItem(View container, int position) {
            ((ViewPager) container).addView(views.get(position), 0);
            return views.get(position);
        }
    };

}
