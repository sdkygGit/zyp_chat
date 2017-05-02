package com.wiz.dev.wiztalk.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.wiz.dev.wiztalk.R;

import java.io.File;

/**
 * Created by hekang on 2016/4/15.
 */
public class SvgActivity extends Activity {
    private String filePath;
    private TextView textView;
    private ImageView imageView_back;
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_svg);
        filePath = getIntent().getStringExtra("filePath");
        initView();
    }

    private void initView() {
        textView = (TextView) findViewById(R.id.activity_svg_textView);
        textView.setText(new File(filePath).getName());
//        textView.setAnimation(TextView);
        imageView_back = (ImageView) findViewById(R.id.activity_svg_imageView_back);
        imageView_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        initWebView();
    }

    private void initWebView() {
        webView = (WebView) findViewById(R.id.svgActivity_webView);
        WebSettings webSettings = webView.getSettings();
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setUseWideViewPort(true);
        webView.getSettings().setBuiltInZoomControls(true);// 会出现放大缩小的按钮
        webSettings.setSupportZoom(true);
        webSettings.setSupportMultipleWindows(true);
//        webView.setInitialScale(75);
        try {
            // SVG图所在路径file:///
            webView.loadUrl("file:///" + filePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
