package com.wiz.dev.wiztalk.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import com.autonavi.amap.mapcore.ConnectionManager;
import com.wiz.dev.wiztalk.R;
import com.wiz.dev.wiztalk.public_store.OpenfireConstDefine;
import com.wiz.dev.wiztalk.utils.SaveConfig;
import com.wiz.dev.wiztalk.utils.Utils;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;

import java.io.IOException;

public class WelcomeActivity extends Activity {

    private static final int MSG_INTO_APP = 0x0001;
    private static final int MSG_HEAD = 0x1111;
    private static final long DELAYMILLIS_DEFALUT = 1 * 1000;
    private boolean intoAppDelay = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        //第一次在这里下载内置的PDF解析插件

        SharedPreferences in = getSharedPreferences("in_first", Context.MODE_PRIVATE);
        intoAppDelay = in.getBoolean("intoAppDelay", false);
        if (intoAppDelay)
            mHander.sendEmptyMessageDelayed(MSG_INTO_APP, DELAYMILLIS_DEFALUT);
        else
            mHander.sendEmptyMessage(MSG_HEAD);
    }


    private Handler mHander = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case MSG_INTO_APP:
                    Intent intent = new Intent(WelcomeActivity.this, LoginActivity_.class);
                    WelcomeActivity.this.startActivity(intent);
                    WelcomeActivity.this.finish();
                    break;
                case MSG_HEAD:
                    Intent intent2 = new Intent(WelcomeActivity.this, GuideActivity.class);
                    WelcomeActivity.this.startActivity(intent2);
                    WelcomeActivity.this.finish();
                    break;
            }
        }
    };

}
