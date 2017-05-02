package com.wiz.dev.wiztalk.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.epic.traverse.push.smack.XmppManager;
import com.fanning.library.FNHttp;
import com.fanning.library.FNTools;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wiz.dev.wiztalk.DB.Member;
import com.wiz.dev.wiztalk.MyApplication;
import com.wiz.dev.wiztalk.R;
import com.wiz.dev.wiztalk.dto.response.LoginResponse;
import com.wiz.dev.wiztalk.preference.CachePrefs_;
import com.wiz.dev.wiztalk.preference.LoginPrefs_;
import com.wiz.dev.wiztalk.public_store.ConstDefine;
import com.wiz.dev.wiztalk.public_store.ConstDefineExtranet;
import com.wiz.dev.wiztalk.public_store.OpenfireConstDefine;
import com.wiz.dev.wiztalk.public_store.PublicTools;
import com.wiz.dev.wiztalk.utils.SaveConfig;
import com.wiz.dev.wiztalk.utils.Utils;
import com.wiz.dev.wiztalk.view.dialog.ConfigAddressPopuWindow;
import com.wiz.dev.wiztalk.view.dialog.MyAlertDialog;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.jivesoftware.smack.util.Async;
import org.jivesoftware.smackx.iqregister.AccountManager;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

@EActivity
public class LoginActivity extends Activity {
    @Pref
    CachePrefs_ mCachePrefs;
    @Pref
    LoginPrefs_ mLoginPrefs;

    //private String userName, userCode;
    //private String loginCode;

    EditText ulogincode;
    EditText psdtv;
    CheckBox jzmm, zddl;
    Button loginbt;

    View login_top;
    Dialog dialog;

    SaveConfig saveConfig;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        login_top = findViewById(R.id.login_top);
        ulogincode = (EditText) findViewById(R.id.etloginCode);
        psdtv = (EditText) findViewById(R.id.etPassword);
        jzmm = (CheckBox) findViewById(R.id.cbRememberPassword);
        zddl = (CheckBox) findViewById(R.id.cbAutoLogin);
        loginbt = (Button) findViewById(R.id.btnLogin);
        saveConfig = new SaveConfig(this);
        TextWatcher tw = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                onTextChange();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };

        ulogincode.addTextChangedListener(tw);
        psdtv.addTextChangedListener(tw);

        ulogincode.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    login();
                    return true;
                }
                return false;
            }
        });
        psdtv.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    login();
                    return true;
                }
                return false;
            }
        });


        zddl.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    jzmm.setChecked(true);
                }
            }
        });

        jzmm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (!b) {
                    zddl.setChecked(false);
                }
            }
        });
        MyApplication.getInstance().addActivity(this);
    }


    @Override
    protected void onResume() {
        super.onResume();
        List<PackageInfo> packages = getPackageManager().getInstalledPackages(0);
        for (int i = 0; i < packages.size(); i++) {
            PackageInfo packageInfo = packages.get(i);
            if (packageInfo.packageName.equals("com.yxst.epic.unifyplatform")) {
                new MyAlertDialog(LoginActivity.this, "发现旧版本", "请卸载旧版本？", "取消", "确定", new MyAlertDialog.OnMyDialogBtnClickListener() {
                    @Override
                    public void onOkClick() {
                        Uri packageURI = Uri.parse("package:com.yxst.epic.unifyplatform");
                        //创建Intent意图
                        Intent intent = new Intent(Intent.ACTION_DELETE, packageURI);
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

        if (Utils.isNetworkAvailable(getBaseContext()) && !isGetConfig) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    getConfig();
                }
            }).start();

        } else {
            Toast.makeText(LoginActivity.this, "当前无网络", Toast.LENGTH_SHORT).show();
        }
    }

    boolean isGetConfig;

    boolean getConfig() {

        boolean isNeiWang =false;
        HttpClient httpclient = new DefaultHttpClient();
        httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 2000);
        HttpPost httppost = new HttpPost("http://" + OpenfireConstDefine.OPENFIRE_SERVER_HOST + "/app/client/app/user/auth");
        try {
            HttpResponse response;
            response = httpclient.execute(httppost);
            int backCode = response.getStatusLine().getStatusCode();
            if (backCode == 200) {
                isNeiWang = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        saveConfig.setStringConfig("httpConfig", isNeiWang +"");
        isGetConfig = true;

        return isNeiWang;
    }

    @Override
    protected void onDestroy() {
        MyApplication.getInstance().removeActivity(this);
        super.onDestroy();

    }

    @Override
    public void onStart() {
        super.onStart();
        ReadLoginInfo();
    }

    void onTextChange() {
        if (FNTools.emptyString(ulogincode.getText().toString())
                || FNTools.emptyString(psdtv.getText().toString())) {
            loginbt.setEnabled(false);
        } else {
            loginbt.setEnabled(true);
        }
    }

    public void onLoginBtnClick(View view) {
        login();
    }

    private void login() {
        if (FNTools.emptyString(ulogincode.getText().toString())) {
            PublicTools.showStackBarInfo(LoginActivity.this, "请输入登录用户！");
            return;
        }

        String psd = psdtv.getText().toString();

        if (FNTools.emptyString(psd)) {
            PublicTools.showStackBarInfo(LoginActivity.this, "请输入密码！");
            return;
        }

        if (true)
            VerifyEntry();
        else {
            FNTools.openActivity(LoginActivity.this, MainActivity_.class, null);
            LoginActivity.this.finish();

        }
    }

    //读取最近使用的信息
    public void ReadLoginInfo() {
        jzmm.setChecked(PublicTools.loadLocalBoolean(LoginActivity.this, "jzmm"));
        zddl.setChecked(PublicTools.loadLocalBoolean(LoginActivity.this, "zddl"));

        String loginCode = PublicTools.loadLocalString(LoginActivity.this, "logincode");

        String psd = PublicTools.loadLocalString(LoginActivity.this, "password", true);

        if (!FNTools.emptyString(loginCode))
            ulogincode.setText(loginCode);

        if (jzmm.isChecked()) {
            psdtv.setText(psd);
            if (zddl.isChecked()) {
                loginbt.performClick();
            }
        }
    }

    //登录验证
    public void VerifyEntry() {
        loginbt.setEnabled(false);

        new AsyncTask<Void,Void,Boolean>(){

            @Override
            protected void onPreExecute() {
                dialog = new Dialog(LoginActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
                dialog.getWindow().setContentView(getLayoutInflater().inflate(R.layout.login_tip_dialog, null));
                DisplayMetrics dm = new DisplayMetrics();
                WindowManager wm = (WindowManager) LoginActivity.this.getSystemService(Context.WINDOW_SERVICE);
                wm.getDefaultDisplay().getMetrics(dm);
                int width = dm.widthPixels;
                dialog.getWindow().setLayout(width * 2 / 5, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }

            @Override
            protected Boolean doInBackground(Void... params) {
                return getConfig();
            }

            @Override
            protected void onPostExecute(Boolean aVoid) {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("userName", ulogincode.getText().toString().trim());
                map.put("password", psdtv.getText().toString());
                if (aVoid)
                    PublicTools.syncHttpPostWithProgress(
                            LoginActivity.this,
                            MsgHwnd,
                            "Login",
                            "/login",
                            map, "正在登录系统，请稍候...");
                else
                    PublicTools.syncHttpPostWithProgress(
                            LoginActivity.this,
                            MsgHwnd,
                            "Login",
                            ConstDefineExtranet.HttpAdress + "/app/client/device" + "/login",
                            map, "正在登录系统，请稍候...");
            }
        }.execute();


    }

    //保存信息
    public void SaveLoginInfo() {
        PublicTools.saveLocalString(this, "logincode", ulogincode.getText().toString());
        PublicTools.saveLocalString(this, "password", psdtv.getText().toString(), true);
        PublicTools.saveLocalBoolean(this, "jzmm", jzmm.isChecked());
        PublicTools.saveLocalBoolean(this, "zddl", zddl.isChecked());
    }

    Handler MsgHwnd = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            if (dialog != null && dialog.isShowing())
                dialog.cancel();
            loginbt.setEnabled(true);
            if (PublicTools.checkHttpResponse(LoginActivity.this, data, "登录失败，请确认用户名或密码是否正确！")) {
                String str = (FNHttp.getRequestResult(data));
                Member member;
                try {
                    JSONObject jsonObj = new JSONObject(str);
                    ConstDefine.uid = jsonObj.getString("uid");
                    ConstDefine.token = jsonObj.getString("token");
                    ConstDefine.PaasBaseUrl = jsonObj.optString("paasBaseUrl");
                    String memberstr = jsonObj.optString("member");
//                    member = new Gson().fromJson(memberstr, Member.class);
//                    FNTools.FLog(member.toString());

                    SaveLoginInfo();

                    JSONObject memberJson = jsonObj.getJSONObject("member");

                    String uid = memberJson.getString("uid");
                    mCachePrefs.uid().put(uid);
                    mCachePrefs.token().put(jsonObj.getString("token"));

                    String jid = uid;
                    if (!TextUtils.isEmpty(uid)) {
                        // TODO: 2016/3/14
                        jid = uid.concat("@").concat(OpenfireConstDefine.OPENFIRE_SERVER_NAME);
                        mLoginPrefs.openfireJid().put(jid);
                        mLoginPrefs.openfirePwd().put("1");
                    }

                    ObjectMapper mapper = new ObjectMapper();
                    LoginResponse loginResponse = mapper.readValue(str, LoginResponse.class);
                    MyApplication.getInstance().putLoginResponse(loginResponse);

                    FNTools.openActivity(LoginActivity.this, MainActivity_.class, null);

                    LoginActivity.this.finish();
                } catch (JSONException e) {

                } catch (JsonParseException e) {
                    e.printStackTrace();
                } catch (JsonMappingException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    private void register(String uname, String upassword) throws Exception {
        AccountManager acc = AccountManager.getInstance(XmppManager.getInstance().getConnection());
        acc.createAccount(uname, upassword);
    }


    public void settings(View view) {
        new ConfigAddressPopuWindow(LoginActivity.this)
                .showPopupWindow(getCurrentFocus());
    }


}
