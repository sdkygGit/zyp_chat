package com.wiz.dev.wiztalk.activity;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.itri.html5webview.HTML5WebView;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebStorage.QuotaUpdater;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.wiz.dev.wiztalk.MyApplication;
import com.wiz.dev.wiztalk.R;
import com.wiz.dev.wiztalk.public_store.PublicTools;


@EActivity
public class H5Activity extends ActionBarActivity implements View.OnClickListener{

    private static final String TAG = "H5Activity";

    @Extra
    String remoteDisplayName;

    @Extra
    String url;

    // @ViewById(R.id.webView)
    HTML5WebView mWebView;

    ProgressBar progressbar;

    ActionBar bar;

    FrameLayout layoutWebView;

    @SuppressLint("NewApi")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_html5);

        progressbar = (ProgressBar) findViewById(R.id.progressBar);
        layoutWebView = (FrameLayout) findViewById(R.id.layoutWebView);

        mWebView = new HTML5WebView(this);
        layoutWebView.addView(mWebView.getLayout());

        WebChromeClient client = new WebChromeClient();
        mWebView.setWebChromeClient(client);
        mWebView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent,
                                        String contentDisposition, String mimetype,
                                        long contentLength) {
                if (url != null && url.startsWith("http://"))
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
            }
        });

        // mWebView.clearCache(true);

        mWebView.getSettings().setDatabaseEnabled(true);
        String databasePath = this.getApplicationContext()
                .getDir("database", Context.MODE_PRIVATE).getPath();
        mWebView.getSettings().setDatabasePath(databasePath);

        mWebView.getSettings().setAppCacheEnabled(true);
        String appCachePath = this.getApplicationContext()
                .getDir("cache", Context.MODE_PRIVATE).getPath();
        mWebView.getSettings().setAppCachePath(appCachePath);

        mWebView.getSettings().setAppCacheMaxSize(1024 * 1024 * 8);
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            mWebView.getSettings().setAllowContentAccess(true);
        }
        mWebView.getSettings().setAllowFileAccess(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mWebView.getSettings().setAllowFileAccessFromFileURLs(true);
            mWebView.getSettings().setAllowUniversalAccessFromFileURLs(true);
        }
        mWebView.getSettings().setSupportZoom(true);
        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.addJavascriptInterface(getHtmlObject(), "hkjsObj");
        if (savedInstanceState != null) {
            mWebView.restoreState(savedInstanceState);
        } else {
            Log.d(TAG, "url:" + url);
            mWebView.loadUrl(url);
            // mWebView.loadUrl("http://3g.163.com/ntes/special/0034073A/wechat_article.html?docid=9N3IMHLJ0001121M&from=singlemessage&isappinstalled=1");
            // mWebView.loadUrl("http://freebsd.csie.nctu.edu.tw/~freedom/html5/");
            // mWebView.loadUrl("file:///data/bbench/index.html");
        }

        initActionBar();
        MyApplication.getInstance().addActivity(this);

    }


    private ValueCallback<Uri> mUploadMessage;
    private final static int FILECHOOSER_RESULTCODE = 1;

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent intent) {
        if (requestCode == FILECHOOSER_RESULTCODE) {
            if (null == mUploadMessage)
                return;
            Uri result = intent == null || resultCode != RESULT_OK ? null
                    : intent.getData();
            mUploadMessage.onReceiveValue(result);
            mUploadMessage = null;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.back:
                finish();
                break;
            case R.id.refresh:
                if (mWebView != null) {
                    mWebView.reload();
                }
                break;
        }
    }

    public class WebChromeClient extends android.webkit.WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if (newProgress == 100) {
                progressbar.setVisibility(View.GONE);
            } else {
                if (progressbar.getVisibility() == View.GONE)
                    progressbar.setVisibility(View.VISIBLE);
                progressbar.setProgress(newProgress);
            }
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
//			if (!TextUtils.isEmpty(title)) {
//				bar.setTitle(title);
//			}
        }


        @Override
        public void onExceededDatabaseQuota(String url,
                                            String databaseIdentifier, long quota,
                                            long estimatedDatabaseSize, long totalQuota,
                                            QuotaUpdater quotaUpdater) {
            // TODO Auto-generated method stub
            quotaUpdater.updateQuota(5 * 1024 * 1024);
            // super.onExceededDatabaseQuota(url, databaseIdentifier, quota,
            // estimatedDatabaseSize, totalQuota, quotaUpdater);
        }

        // 关键代码，以下函数是没有API文档的，所以在Eclipse中会报错，如果添加了@Override关键字在这里的话。

        // For Android 3.0+
        public void openFileChooser(ValueCallback<Uri> uploadMsg) {

            mUploadMessage = uploadMsg;
            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            // i.setType("image/*");
            i.setType("*/*");
            startActivityForResult(Intent.createChooser(i, "File Chooser"),
                    FILECHOOSER_RESULTCODE);

        }

        // For Android 3.0+
        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
            mUploadMessage = uploadMsg;
            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            i.setType("*/*");
            startActivityForResult(Intent.createChooser(i, "File Browser"),
                    FILECHOOSER_RESULTCODE);
        }

        // For Android 4.1
        public void openFileChooser(ValueCallback<Uri> uploadMsg,
                                    String acceptType, String capture) {
            mUploadMessage = uploadMsg;
            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            // i.setType("image/*");
            i.setType("*/*");
            startActivityForResult(Intent.createChooser(i, "File Chooser"),
                    FILECHOOSER_RESULTCODE);
        }
    }

    @Override
    public void finish() {
        ViewGroup viewGroup = (ViewGroup) getWindow().getDecorView();
        viewGroup.removeAllViews();
        super.finish();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, " onDestroy()");

        mWebView.stopLoading();
        mWebView.removeAllViews();
        mWebView.setVisibility(View.GONE);
        mWebView.destroy();
        layoutWebView.removeView(mWebView.getLayout());
        MyApplication.getInstance().removeActivity(this);

        super.onDestroy();
    }

    @Override
    public boolean onSupportNavigateUp() {
        this.finish();
        return true;
    }

    void initActionBar() {
        bar = getSupportActionBar();
        bar.hide();
//        bar.setDisplayOptions(bar.getDisplayOptions() | ActionBar.DISPLAY_HOME_AS_UP, ActionBar.DISPLAY_HOME_AS_UP);
//        bar.setHomeAsUpIndicator(R.drawable.abc_ic_clear);
//        bar.setIcon(null);
//        bar.setDisplayUseLogoEnabled(false);

        View back = findViewById(R.id.back);
        back.setOnClickListener(this);
        View refresh = findViewById(R.id.refresh);
        refresh.setOnClickListener(this);

        TextView title = (TextView) findViewById(R.id.title);
        title.setText(remoteDisplayName);

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mWebView.saveState(outState);
    }

    @Override
    public void onStop() {
        super.onStop();
        mWebView.stopLoading();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mWebView.inCustomView()) {
                mWebView.hideCustomView();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.h5_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_h5_refresh) {
            if (mWebView != null) {
                mWebView.reload();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private Object getHtmlObject() {
        Object insertObj = new Object() {
            @JavascriptInterface
            public void JavacallUserInfo() {//方法名必须是js使用的方法名
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        String nickName = MyApplication.getInstance().getLoginResponse().Member.NickName;
                        String email = MyApplication.getInstance().getLoginResponse().Member.Email;
                        String uid = MyApplication.getInstance().getLoginResponse().Member.UserName;
                        String loginName = PublicTools.loadLocalString(H5Activity.this, "logincode");
                        JSONObject o = new JSONObject();
                        try {
                            o.put("uid",uid);
                            o.put("nickName",nickName);
                            o.put("email",email);
                            o.put("loginName",loginName);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        mWebView.loadUrl("javascript: ShowUserInfoFromAndroid('"+o+"')");
                    }
                });
            }
        };

        return insertObj;
    }
}
