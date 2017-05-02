package com.wiz.dev.wiztalk.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.epic.traverse.push.util.L;
import com.lidroid.xutils.BitmapUtils;
import com.morgoo.droidplugin.pm.PluginManager;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.wiz.dev.wiztalk.DB.Member;
import com.wiz.dev.wiztalk.DB.MsgInFo;
import com.wiz.dev.wiztalk.DB.XmppDbManager;
import com.wiz.dev.wiztalk.DB.XmppMessage;
import com.wiz.dev.wiztalk.MyApplication;
import com.wiz.dev.wiztalk.R;
import com.wiz.dev.wiztalk.dto.request.BaseRequest;
import com.wiz.dev.wiztalk.dto.request.CancelShieldMsgRequest;
import com.wiz.dev.wiztalk.dto.request.CheckPlugUpdateRequest;
import com.wiz.dev.wiztalk.dto.request.FollowAppRequest;
import com.wiz.dev.wiztalk.dto.request.GetMemberRequest;
import com.wiz.dev.wiztalk.dto.request.GetMemberResponse;
import com.wiz.dev.wiztalk.dto.request.IsUserShieldAppRequest;
import com.wiz.dev.wiztalk.dto.request.SetShieldMsgRequest;
import com.wiz.dev.wiztalk.dto.request.UnFollowAppRequest;
import com.wiz.dev.wiztalk.dto.response.BaseResponse;
import com.wiz.dev.wiztalk.dto.response.CancelShieldMsgResponse;
import com.wiz.dev.wiztalk.dto.response.CheckPlugUpdateResponse;
import com.wiz.dev.wiztalk.dto.response.IsUserShieldAppResponse;
import com.wiz.dev.wiztalk.dto.response.Response;
import com.wiz.dev.wiztalk.dto.response.SetShieldMsgResponse;
import com.wiz.dev.wiztalk.rest.Appmsgsrv8093;
import com.wiz.dev.wiztalk.rest.Appmsgsrv8093Proxy;
import com.wiz.dev.wiztalk.rest.Appmsgsrv8094;
import com.wiz.dev.wiztalk.rest.Appmsgsrv8094Proxy;
import com.wiz.dev.wiztalk.utils.CacheUtils;
import com.wiz.dev.wiztalk.utils.ImagerLoaderOptHelper;
import com.wiz.dev.wiztalk.utils.Utils;
import com.wiz.dev.wiztalk.utils.XXBitmapUtils;
import com.wiz.dev.wiztalk.view.ProgressView;
import com.wiz.dev.wiztalk.view.dialog.MyAlertDialog;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.DimensionPixelOffsetRes;
import org.androidannotations.api.rest.RestErrorHandler;
import org.springframework.core.NestedRuntimeException;

import java.io.File;
import java.util.LinkedHashMap;

import cn.finalteam.okhttpfinal.FileDownloadCallback;

import static com.morgoo.helper.compat.PackageManagerCompat.INSTALL_FAILED_NOT_SUPPORT_ABI;
import static com.morgoo.helper.compat.PackageManagerCompat.INSTALL_SUCCEEDED;

@EActivity(R.layout.activity_app_detail)
public class AppDetailActivity extends Activity implements RestErrorHandler {
    private static final String TAG = "AppDetailActivity";
    private String packageName = "com.example.hekang.myapplication";
    private String fileName;
    @Extra
    String remoteUserName;

    @Extra
    String nickName;

    @ViewById
    ImageView ivAvatar;

    @ViewById
    TextView tvDisplayName;

    @ViewById
    TextView tvNickName;

    @ViewById
    TextView tvFunctionDetail;

    @ViewById(R.id.cb_app_receive_app_msg)
    CheckBox cbIsReceiveAppMsg;

    @ViewById
    CheckBox cbFollow;

    @ViewById(R.id.btnEnterApp)
    Button btnEnterApp;

    @Extra
    String follow;

    @Extra
    String fromWhere;

    @ViewById
    ProgressView progressView;

    public static final String FROM_WHERE_IS_CHAT = "FROM_WHERE_IS_CHAT";

    @DimensionPixelOffsetRes(R.dimen.icon_size_bigger)
    int size;

    private Appmsgsrv8093 mAppmsgsrv8093;
    private Appmsgsrv8094 mAppmsgsrv8094;

//	private boolean mIsChecked;

    private BitmapUtils bitmapUtils;


    @AfterViews
    void afterViews() {
        bitmapUtils = new XXBitmapUtils(this);

        bitmapUtils.configDefaultLoadingImage(R.drawable.ic_default_app_mini);
        bitmapUtils.configDefaultLoadFailedImage(R.drawable.ic_default_app_mini);
        tvDisplayName.setText(nickName);
//        tvNickName.setText(remoteUserName);
        if (follow.equals("1")) {
            cbFollow.setChecked(true);
        } else {
            cbFollow.setChecked(false);
//            btnEnterApp.setText("关注");
        }
        doInBackgroundGetMemberInfo();
    }

    private OnCheckedChangeListener checkedChangeListener = new OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                cancelShieldMsgBackground(isChecked);
            } else {
                setShieldMsgBackground(isChecked);
            }
        }
    };

    @AfterInject
    void afterInject() {
        mAppmsgsrv8093 = Appmsgsrv8093Proxy.create();
        mAppmsgsrv8093.setRestErrorHandler(this);
        mAppmsgsrv8094 = Appmsgsrv8094Proxy.create(5 * 1000);
        mAppmsgsrv8094.setRestErrorHandler(this);
    }

    // 初始化是否接收消息
    @Background
    void isUserShieldBackground() {
        onPreExecute();

        String sourceId = getSourceId();
        String appId = getTargetId();

        IsUserShieldAppRequest isUserShieldAppRequest = new IsUserShieldAppRequest();
        isUserShieldAppRequest.BaseRequest = CacheUtils.getBaseRequest(this);
        isUserShieldAppRequest.sourceId = sourceId;
        isUserShieldAppRequest.targetId = appId;

        mAppmsgsrv8093.setRestErrorHandler(this);
        L.d(TAG, "IsUserShieldAppRequest:" + isUserShieldAppRequest);
        IsUserShieldAppResponse isUserShieldAppResponse = mAppmsgsrv8093.isUserShieldApp(isUserShieldAppRequest);
        L.d(TAG, "IsUserShieldAppResponse:" + isUserShieldAppResponse);
        isUserShieldOnPostExecute(isUserShieldAppResponse);
    }

    private String getSourceId() {
        return MyApplication.getInstance().getLocalMember().Uid;
    }

    private String getTargetId() {
        if (!TextUtils.isEmpty(remoteUserName)) {
            int end = remoteUserName.indexOf("@");
            return remoteUserName.substring(0, end);
        }
        return null;
    }

    @UiThread
    void isUserShieldOnPostExecute(IsUserShieldAppResponse response) {
        L.d(TAG, "isUserShieldOnPostExecute() response:" + response);
        dismissProgressDialog();
        if (response == null) {
            return;
        }
        if (response.BaseResponse.Ret != BaseResponse.RET_SUCCESS) {
            Toast.makeText(this, response.BaseResponse.ErrMsg,
                    Toast.LENGTH_SHORT).show();
            return;
        }
        cbIsReceiveAppMsgSetOnCheckedChangeListener(!response.isShield);
    }

    public Member mAppMember;


    @Background
    void doInBackgroundGetMemberInfo() {
        onPreExecute();

        BaseRequest baseRequest = CacheUtils.getBaseRequest(this);
        GetMemberRequest request = new GetMemberRequest();
        request.BaseRequest = baseRequest;
        request.UserName = remoteUserName;
        L.d(TAG, "getMemberInfo() request:" + request);
        mAppmsgsrv8094.setRestErrorHandler(this);
        L.d(TAG, "getMemberInfo() mAppmsgsrv8094.getRootUrl():" + mAppmsgsrv8094.getRootUrl());
        GetMemberResponse response = mAppmsgsrv8094.getMember(request);
        onPostExecuteGetMember(response);
    }

    @UiThread
    void onPostExecuteGetMember(GetMemberResponse response) {
        L.d(TAG, "getMemberInfoResponsse() response:" + response);
        dismissProgressDialog();
        if (response == null) {
            dismissProgressDialog();
            return;
        }

        if (response.BaseResponse.Ret != BaseResponse.RET_SUCCESS) {
            Toast.makeText(this, response.BaseResponse.ErrMsg,
                    Toast.LENGTH_SHORT).show();
            dismissProgressDialog();
            return;
        }

        if (response.Member == null) {
            Toast.makeText(this, "应用信息为空",
                    Toast.LENGTH_SHORT).show();
            dismissProgressDialog();
            return;
        }

        if (response.Member != null) {
            mAppMember = response.Member;

            boolean isFollow = "1".equals(mAppMember.Follow);
//            btnEnterApp.setText(isFollow ? "进入应用服务号" : "关注");

            tvDisplayName.setText(response.Member.NickName);
            tvFunctionDetail.setText(response.Member.Description);
            cbFollowSetOnCheckedChangeListener("1".equals(response.Member.Follow));
            DisplayImageOptions options = ImagerLoaderOptHelper.getAppAvatarOpt();
            ImageLoader.getInstance().displayImage(Utils.getImgUrl(response.Member.Avatar, size), ivAvatar, options);
        }

        // TODO: 2016/3/28  
//        isUserShieldBackground();
    }

    private OnCheckedChangeListener cbFollowOnCheckedChangeListener = new OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            doInBackgroundFollow(isChecked);
        }
    };

    @Background
    void doInBackgroundFollow(final boolean isChecked) {
        onPreExecute();

        Response response = null;

        if (isChecked) {
            FollowAppRequest request = new FollowAppRequest();
            request.BaseRequest = CacheUtils.getBaseRequest(this);
            request.appname = remoteUserName;

            mAppmsgsrv8094.setRestErrorHandler(new RestErrorHandler() {

                @Override
                public void onRestClientExceptionThrown(NestedRuntimeException arg0) {
                    showToast("访问失败！");
                    cbFollowSetOnCheckedChangeListener(!isChecked);
                }
            });
            L.d(TAG, "FollowAppRequest:" + request);
            response = mAppmsgsrv8094.followApp(request);
            L.d(TAG, "FollowAppResponse:" + response);
        } else {
            UnFollowAppRequest request = new UnFollowAppRequest();
            request.BaseRequest = CacheUtils.getBaseRequest(this);
            request.appname = remoteUserName;

            mAppmsgsrv8094.setRestErrorHandler(new RestErrorHandler() {
                @Override
                public void onRestClientExceptionThrown(NestedRuntimeException arg0) {
                    showToast("访问失败！");
                    cbFollowSetOnCheckedChangeListener(!isChecked);
                }
            });
            L.d(TAG, "UnFollowAppRequest:" + request);
            response = mAppmsgsrv8094.unFollowApp(request);
            L.d(TAG, "UnFollowAppResponse:" + response);
        }
        onPostExecuteFollow(remoteUserName, isChecked, response);
    }

    @UiThread
    void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    @UiThread
    void cbFollowSetOnCheckedChangeListener(boolean isChecked) {
        cbFollow.setOnCheckedChangeListener(null);
        cbFollow.setChecked(isChecked);
        cbFollow.setOnCheckedChangeListener(cbFollowOnCheckedChangeListener);
    }

    @UiThread
    void onPostExecuteFollow(String appname, boolean isChecked, Response response) {
        dismissProgressDialog();
        if (response == null) {
            return;
        }

        if (response.BaseResponse.Ret != BaseResponse.RET_SUCCESS) {
            Toast.makeText(this, response.BaseResponse.ErrMsg,
                    Toast.LENGTH_SHORT).show();
            cbFollowSetOnCheckedChangeListener(!isChecked);
            return;
        }

        cbFollowSetOnCheckedChangeListener(isChecked);
        Toast.makeText(this, "设置成功！", Toast.LENGTH_SHORT).show();

        mAppMember.Follow = isChecked ? "1" : "0";
        boolean isFollow = "1".equals(mAppMember.Follow);
//        btnEnterApp.setText(isFollow ? "进入应用服务号" : "关注");
        cbFollow.setChecked(isFollow);

        if (isFollow) {
            doInbackgroundInsert(mAppMember);
        } else {
            XmppDbManager.getInstance(AppDetailActivity.this).deleteChat(MyApplication.getInstance().getOpenfireJid(), mAppMember.UserName);
        }

        if (!isChecked) {
            String localUserName = MyApplication.getInstance().getLocalUserName();
            XmppDbManager.getInstance(this).deleteChat(localUserName, remoteUserName);
        }

        Intent data = new Intent();
        Bundle extras = new Bundle();
        extras.putString("appname", appname);
        extras.putString("follow", isChecked ? "1" : "0");
        extras.putBoolean("isChecked", isChecked);
        data.putExtras(extras);
        setResult(RESULT_OK, data);
    }

    @Background
    void doInbackgroundInsert(Member member) {
        Cursor cursor = XmppDbManager.getInstance(AppDetailActivity.this).getChatList(MyApplication.getInstance().getOpenfireJid(),
                member.Uid.concat(Member.SUFFIX_APP), MsgInFo.TYPE_CHAT);
        if (cursor.moveToFirst()){
            XmppMessage msg = MyApplication.getDaoSession(AppDetailActivity.this).getXmppMessageDao().readEntity(cursor,0);
            msg.setExtLocalDisplayName(member.NickName);
            msg.setExtRemoteUserName(member.UserName);//根据appid分类
            msg.setExtRemoteDisplayName(member.NickName);
            msg.setPushToken(member.NickName);
            msg.setPushContent(member.Description);

            msg.setPushObjectContentAppId(member.Uid);
            msg.setBody(member.Description);
            XmppDbManager.getInstance(AppDetailActivity.this).updateWithNotify(msg);
            return;
        }
        XmppMessage msg = new XmppMessage();
        String localUserJid = MyApplication.getInstance().getOpenfireJid();
        msg.setType(MsgInFo.TYPE_HEADLINE);
        msg.setTo_(localUserJid);
        msg.setFrom_("应用推送@skysea.com");
        msg.setExtLocalDisplayName(member.NickName); // NickName对应display
        msg.setExtLocalUserName(localUserJid);
        msg.setExtRemoteUserName(member.UserName);//根据appid分类
        msg.setExtRemoteDisplayName(member.NickName);
        msg.setDirect(MsgInFo.INOUT_IN);
        msg.setStatus(MsgInFo.STATUS_SUCCESS);
        msg.setReadStatus(MsgInFo.READ_TRUE);
        msg.setCreateTime(System.currentTimeMillis());
        msg.setPushToken(member.NickName);
        msg.setPushContent(member.Description);
        msg.setPushMsgType(MsgInFo.MOLD_INIT_APP);
        msg.setPushStatusId("9999");
        msg.setPushtoUserNames(localUserJid);
        msg.setPushObjectContentAppId(member.Uid);
        msg.setPushObjectContentBody("");
        msg.setPushObjectContentOperations("");
        msg.setPushObjectContentPubTime(System.currentTimeMillis());
        msg.setPushSessions("[all]");
        msg.setPushexpire(3600000L);
        msg.setShowTime(System.currentTimeMillis());
        //设置
        msg.setBody(member.Description);
        msg.setMold(MsgInFo.MOLD_INIT_APP);//MsgInFo.MOLE_APP_102
        XmppDbManager.getInstance(AppDetailActivity.this).insertMessageWithNotify(msg);
    }

    // 取消接收消息
    @Background
    void setShieldMsgBackground(final boolean isChecked) {
        onPreExecute();

        String sourceId = getSourceId();
        String appId = getTargetId();

        BaseRequest baseRequest = CacheUtils.getBaseRequest(this);
        SetShieldMsgRequest request = new SetShieldMsgRequest();
        request.BaseRequest = baseRequest;
        request.sourceId = sourceId;
        request.targetId = appId;

        mAppmsgsrv8093.setRestErrorHandler(new RestErrorHandler() {

            @Override
            public void onRestClientExceptionThrown(NestedRuntimeException arg0) {
                Log.d(TAG, "NestedRuntimeException:" + arg0);
                showToast("访问失败！");
                cbIsReceiveAppMsgSetOnCheckedChangeListener(!isChecked);
            }
        });

        L.d(TAG, "setShieldMsg() request:" + request);
        SetShieldMsgResponse response = mAppmsgsrv8093.setShieldMsg(request);
        setShieldMsgOnPostExecute(response);
    }

    @UiThread
    void setShieldMsgOnPostExecute(SetShieldMsgResponse response) {
        L.d(TAG, "setShieldMsgOnPostExecute() response:" + response);
        dismissProgressDialog();
        if (response == null) {
            cbIsReceiveAppMsgSetOnCheckedChangeListener(true);
            return;
        }

        if (response.BaseResponse.Ret != BaseResponse.RET_SUCCESS) {
            Toast.makeText(this, response.BaseResponse.ErrMsg,
                    Toast.LENGTH_SHORT).show();
            cbIsReceiveAppMsgSetOnCheckedChangeListener(true);
            return;
        }
        cbIsReceiveAppMsgSetOnCheckedChangeListener(false);
        Toast.makeText(this, "设置成功！", Toast.LENGTH_SHORT).show();
    }

    // 设置接收消息
    @Background
    void cancelShieldMsgBackground(final boolean isChecked) {
        onPreExecute();

        String sourceId = getSourceId();
        String appId = getTargetId();

        BaseRequest baseRequest = CacheUtils.getBaseRequest(this);
        CancelShieldMsgRequest request = new CancelShieldMsgRequest();
        request.BaseRequest = baseRequest;
        request.sourceId = sourceId;
        request.targetId = appId;

        mAppmsgsrv8093.setRestErrorHandler(new RestErrorHandler() {

            @Override
            public void onRestClientExceptionThrown(NestedRuntimeException arg0) {
                Log.d(TAG, "NestedRuntimeException:" + arg0);
                showToast("访问失败！");
                cbIsReceiveAppMsgSetOnCheckedChangeListener(!isChecked);
            }
        });

        L.d(TAG, "cancelShieldMsgBackground() request:" + request);
        CancelShieldMsgResponse response = mAppmsgsrv8093.cancelShieldMsg(request);
        cancelShieldMsgOnPostExecute(response);
    }

    @UiThread
    void cancelShieldMsgOnPostExecute(CancelShieldMsgResponse response) {
        L.d(TAG, "cancelShieldMsgOnPostExecute() response:" + response);
        dismissProgressDialog();
        if (response == null) {
            return;
        }

        if (response.BaseResponse.Ret != BaseResponse.RET_SUCCESS) {
            Toast.makeText(this, response.BaseResponse.ErrMsg,
                    Toast.LENGTH_SHORT).show();
            cbIsReceiveAppMsgSetOnCheckedChangeListener(false);
            return;
        }
        cbIsReceiveAppMsgSetOnCheckedChangeListener(true);
        Toast.makeText(this, "设置成功！", Toast.LENGTH_SHORT).show();
    }

    @UiThread
    void cbIsReceiveAppMsgSetOnCheckedChangeListener(boolean isChecked) {
        cbIsReceiveAppMsg.setOnCheckedChangeListener(null);

        cbIsReceiveAppMsg.setChecked(isChecked);
        cbIsReceiveAppMsg.setOnCheckedChangeListener(checkedChangeListener);
    }


    public void onSendMsgBtnClick(View v) {
        // ChatActivity.actionChat(this, FakeData.getChatList().get(2));
    }

    @UiThread
    @Override
    public void onRestClientExceptionThrown(NestedRuntimeException arg0) {
        dismissProgressDialog();
        Log.d(TAG, "NestedRuntimeException:" + arg0);
        Toast.makeText(this, "访问失败", Toast.LENGTH_SHORT).show();
    }


    @UiThread
    void onPreExecute() {
        if (mProgressDialog == null || !mProgressDialog.isShowing()) {
            mProgressDialog = ProgressDialog.show(this, null, "加载数据");
        }
    }

    private void dismissProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    public static final int RESULT_FINISH = 2;


    @Click(R.id.back)
    void back() {
        finish();
    }

    @Click(R.id.share)
    void share() {
        Intent intent = new Intent("com.yxst.epic.unifyplatform..activity.ShareContactSelectActivity");
        intent.putExtra("share_type", "app");
        intent.putExtra("share_member", mAppMember);
        startActivity(intent);
    }

    @Click(R.id.btnEnterApp)
    void onClickEnterApp() {
        getPluginUrl();
    }

    private ProgressDialog mProgressDialog;

    @UiThread
    void onPreGetPluginUrl() {
        mProgressDialog = ProgressDialog.show(this, "提示", "检查更新");
    }

    @Background
    void getPluginUrl() {
        onPreGetPluginUrl();//检查更新进度提示框显示
        BaseRequest baseRequest = CacheUtils.getBaseRequest(AppDetailActivity.this);
        CheckPlugUpdateRequest request = new CheckPlugUpdateRequest();
        request.BaseRequest = baseRequest;
        request.VersionCode = 1;//这里的版本号和版本名字以及APPID应该是获取的
        request.VersionName = "1.0";
        request.Appid = mAppMember.Uid;
        if(mAppMember.PYInitial.equalsIgnoreCase("h5")){
            request.Type = "h5";
        }
        L.d(TAG, "checkUpdateDoInBackground() request:" + request);
        CheckPlugUpdateResponse response = mAppmsgsrv8094.checkPlugUpdate(request);
        L.d(TAG, "checkUpdateDoInBackground() response:" + response);
        onPostGetPluginUrl(response);
    }

    @UiThread
    void onPostGetPluginUrl(CheckPlugUpdateResponse response) {
        dismissProgressDialog();
        if (response == null) {
            return;
        }
        if (TextUtils.isEmpty(response.BaseResponse.ErrMsg) && response.isUpdate) {
            LinkedHashMap<String, String> oj = (LinkedHashMap<String, String>) response.msg.getObjectContent();
            final String downloadUrl = oj.get("downloadUrl");

            if (mAppMember.PYInitial.equalsIgnoreCase("h5")) {
                H5Activity_.intent(this).remoteDisplayName(nickName)
                        .url(downloadUrl).start();
            } else {
                fileName = oj.get("fileName");
                new MyAlertDialog(this, "发现新版本", "是否下载更新？", "取消", "下载", new MyAlertDialog.OnMyDialogBtnClickListener() {
                    @Override
                    public void onOkClick() {
                        downFile(downloadUrl, fileName);
                    }

                    @Override
                    public void onCancleClick() {
                        initOpenBundle(packageName, fileName);
                    }
                }).showDialog();
            }

        } else {//如果不是最新版本就直接打开

            LinkedHashMap<String, String> oj = (LinkedHashMap<String, String>) response.msg.getObjectContent();
            final String downloadUrl = oj.get("downloadUrl");

            if (mAppMember.PYInitial.equalsIgnoreCase("h5")) {
                H5Activity_.intent(this).remoteDisplayName(nickName)
                        .url(downloadUrl).start();
            }else{
                Toast.makeText(this, "已经是最新版本", Toast.LENGTH_SHORT).show();
                initOpenBundle(packageName, fileName);
            }
        }

    }

    private void downFile(String downloadUrl, final String fileName) {
        // TODO: 2016/5/16  packageName和fileName是获取的
//        String packageName = "com.artifex.mupdfdemo";
//        String packageName = "com.artifex.mupdf";
//        String packageName = "org.geometerplus.zlibrary.ui.android";
//        String fileName = "ChoosePDFActivity.apk";
//        String fileName = "MyPDF.apk";
        final File saveFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                + "/wizTalkPulg/" + fileName);

        cn.finalteam.okhttpfinal.HttpRequest.download(downloadUrl, saveFile, new FileDownloadCallback() {
            @Override
            public void onStart() {
                super.onStart();
                progressView.setVisibility(View.VISIBLE);
                btnEnterApp.setVisibility(View.INVISIBLE);
                progressView.setText("加载中...");
            }

            //下载进度
            @Override
            public void onProgress(int progress, long networkSpeed) {
                super.onProgress(progress, networkSpeed);
                progressView.setProgress(progress);
            }

            //下载失败
            @Override
            public void onFailure() {
                super.onFailure();
                progressView.setText("加载失败");
            }

            //下载完成（下载成功）
            @Override
            public void onDone() {
                progressView.setText("加载完成");
                initUninstall(packageName, fileName);
            }
        });
    }

    private void initUninstall(String packageName, String fileName) {
        try {
            PluginManager.getInstance().deletePackage(packageName, 0);//先卸载原来的插件
            initOpenBundle(packageName, fileName);
        } catch (RemoteException e) {
            Toast.makeText(this, "卸载旧版本插件失败,请先手动卸载", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            return;
        }
    }

    private void initOpenBundle(String packageName, String fileName) {//更新的插件packageName和fileName应该是获取的
        if (!PluginManager.getInstance().isConnected()) {
            Toast.makeText(this, "插件服务正在初始化，请稍后再试。。。", Toast.LENGTH_SHORT).show();
        }
        try {
            if (PluginManager.getInstance().getPackageInfo(packageName, 0) != null) {
//                Toast.makeText(this, "已经安装了，不能再安装", Toast.LENGTH_SHORT).show();
                PackageManager pm = getPackageManager();
                Intent intent = pm.getLaunchIntentForPackage(packageName);//应用的包名+application
                intent.putExtra("openSelected", 0);//这个地方是用来判断启动插件的哪个界面
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                Log.d("hk", "start");
            } else {///storage/emulated/0/wizplant-release.apk
                doInstall(Environment.getExternalStorageDirectory().getAbsolutePath()
                        + "/wizTalkPulg/" + fileName, packageName);//这个地方是插件存放的地址
            }
        } catch (Exception e) {
            e.printStackTrace();
            try {
                PluginManager.getInstance().installPackage(Environment.getExternalStorageDirectory().getAbsolutePath()
                        + "/wizTalkPulg/" + fileName, 0);
            } catch (RemoteException e1) {
                e1.printStackTrace();
            }
        }
    }

    private synchronized void doInstall(String filePath, final String packageName) {//插件保存的地址和插件的包名+application
        try {
            final int re = PluginManager.getInstance().installPackage(filePath, 0);
            switch (re) {
                case PluginManager.INSTALL_FAILED_NO_REQUESTEDPERMISSION:
                    Toast.makeText(this, "安装失败，文件请求的权限太多", Toast.LENGTH_SHORT).show();
                    break;
                case INSTALL_FAILED_NOT_SUPPORT_ABI:
                    Toast.makeText(this, "宿主不支持插件的abi环境，可能宿主运行时为64位，但插件只支持32位", Toast.LENGTH_SHORT).show();
                    break;
                case INSTALL_SUCCEEDED:
                    Toast.makeText(this, "安装完成", Toast.LENGTH_SHORT).show();
                    progressView.setText("安装完成");

                    progressView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            PackageManager pm = getPackageManager();
                            Intent intent = pm.getLaunchIntentForPackage(packageName);//应用的包名+application
                            intent.putExtra("openSelected", 0);//这个地方是用来判断启动插件的哪个界面
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                    });
                    PackageManager pm = getPackageManager();
                    Intent intent = pm.getLaunchIntentForPackage(packageName);//应用的包名+application
                    intent.putExtra("openSelected", 0);//这个地方是用来判断启动插件的哪个界面
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    break;
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


}
