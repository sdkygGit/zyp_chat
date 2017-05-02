package com.wiz.dev.wiztalk.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.epic.traverse.push.services.XmppService;
import com.epic.traverse.push.smack.XmppManager;
import com.epic.traverse.push.util.L;
import com.fanning.library.TabFragmentHost;
import com.readystatesoftware.viewbadger.BadgeView;
import com.wiz.dev.wiztalk.DB.Member;
import com.wiz.dev.wiztalk.DB.MsgInFo;
import com.wiz.dev.wiztalk.DB.XmppDbManager;
import com.wiz.dev.wiztalk.DB.XmppMessage;
import com.wiz.dev.wiztalk.DB.XmppMessageContentProvider;
import com.wiz.dev.wiztalk.MyApplication;
import com.wiz.dev.wiztalk.R;
import com.wiz.dev.wiztalk.apppush.view.AppPushFragment_;
import com.wiz.dev.wiztalk.dto.request.BaseRequest;
import com.wiz.dev.wiztalk.dto.request.GetApplicationListRequest;
import com.wiz.dev.wiztalk.dto.response.BaseResponse;
import com.wiz.dev.wiztalk.dto.response.GetApplicationListResponse;
import com.wiz.dev.wiztalk.fragment.ContactListFragment_;
import com.wiz.dev.wiztalk.fragment.FindFragment_;
import com.wiz.dev.wiztalk.fragment.IMFragment_;
import com.wiz.dev.wiztalk.fragment.PersonalCenterFragment_;
import com.wiz.dev.wiztalk.preference.LoginPrefs_;
import com.wiz.dev.wiztalk.public_store.OpenfireConstDefine;
import com.wiz.dev.wiztalk.rest.Appmsgsrv8094;
import com.wiz.dev.wiztalk.rest.Appmsgsrv8094Proxy;
import com.wiz.dev.wiztalk.utils.CacheUtils;
import com.wiz.dev.wiztalk.utils.ImagerLoaderOptHelper;
import com.wiz.dev.wiztalk.utils.SaveConfig;
import com.wiz.dev.wiztalk.utils.Utils;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.androidannotations.api.rest.RestErrorHandler;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.springframework.core.NestedRuntimeException;

import java.io.IOException;
import java.util.List;

@EActivity
public class MainActivity extends FragmentActivity {

    private static final String TAG = "MainActivity";
    private BadgeView chatHint;
    private BadgeView shopHint;
    private BadgeView appHint;
    public static long lastMsgTime = 0;
    @Pref
    LoginPrefs_ mLoginPrefs;

    public SaveConfig saveConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        LayoutInflater layoutInflater = LayoutInflater.from(this);
//        OpenPulgUtils openPulgUtils=new OpenPulgUtils(this);
//        openPulgUtils.installPulg(Environment.getExternalStorageDirectory().getAbsolutePath() + "/wizTalkPulg/"
//                + "MyPDF.apk");
      /*  try {//启动pdf插件
            int installState = PluginManager.getInstance().installPackage(Environment.getExternalStorageDirectory().getAbsolutePath() + "/wizTalkPulg/"
                    + "MyPDF.apk", 0);//pdf插件的位置
//            int installState = PluginManager.getInstance().installPackage("file:///android_asset/"
//            +"MyPDF.apk", 0);//pdf插件的位置
            Log.d("hk", "installState:" + installState);
            if (installState != INSTALL_SUCCEEDED && installState != INSTALL_FAILED_ALREADY_EXISTS) {//判断是否安装成功
                Toast.makeText(this, "pdf插件启动失败，请手动启动", Toast.LENGTH_SHORT).show();
            }
        } catch (RemoteException e) {
            Toast.makeText(this, "pdf插件启动失败，请手动启动", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }*/

        TabFragmentHost mTabHost = (TabFragmentHost) findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
        mTabHost.getTabWidget().setDividerDrawable(null);
        mTabHost.getTabWidget().setBackgroundColor(Color.parseColor("#FFFFFF"));
        View vChat = getTabItemView(layoutInflater, R.drawable.main_tab_xx, "消息");
        chatHint = new BadgeView(this, vChat.findViewById(R.id.imageview));
        chatHint.setTextSize(10);
        chatHint.setVisibility(View.INVISIBLE);

        View vShop = getTabItemView(layoutInflater, R.drawable.main_tab_shop, "应用商店");
        shopHint = new BadgeView(this, vShop.findViewById(R.id.imageview));
        shopHint.setTextSize(10);
        shopHint.setVisibility(View.INVISIBLE);

        Bundle bundle = new Bundle();
        bundle.putString("localUserName", mLoginPrefs.userName().get());
        bundle.putBoolean("isSelectMode", false);

        mTabHost.addTab(mTabHost.newTabSpec("消息").setIndicator(vChat),
                IMFragment_.class, null);

        View vApp = getTabItemView(layoutInflater, R.drawable.main_tab_shop, "应用");
        appHint = new BadgeView(this, vApp.findViewById(R.id.imageview));
        appHint.setTextSize(10);
        appHint.setVisibility(View.INVISIBLE);
        mTabHost.addTab(mTabHost.newTabSpec("应用").setIndicator(vApp),
                AppPushFragment_.class, null);

        mTabHost.addTab(mTabHost.newTabSpec("联系人").setIndicator(getTabItemView(layoutInflater, R.drawable.main_tab_txl, "联系人")),
                ContactListFragment_.class, bundle);

//        mTabHost.addTab(mTabHost.newTabSpec("应用商店").setIndicator(vShop), AppStoreFragment.class, null);

        mTabHost.addTab(mTabHost.newTabSpec("发现").setIndicator(getTabItemView(layoutInflater, R
                .drawable.main_tab_find, "发现")), FindFragment_.class, null);

        mTabHost.addTab(mTabHost.newTabSpec("我").setIndicator(getTabItemView(layoutInflater, R
                        .drawable.main_tab_me, "我")), PersonalCenterFragment_.class,
                null);

        MyApplication.getInstance().addActivity(this);

        new AsyncTask<Void, Void, List<Member>>() {
            Appmsgsrv8094 mIMInterfaceProxy;

            @Override
            protected void onPreExecute() {
                mIMInterfaceProxy = Appmsgsrv8094Proxy.create();
                mIMInterfaceProxy.setRestErrorHandler(new RestErrorHandler() {
                    @Override
                    public void onRestClientExceptionThrown(NestedRuntimeException e) {
                    }
                });
            }

            @Override
            protected List<Member> doInBackground(Void... params) {
                BaseRequest baseRequest = CacheUtils.getBaseRequest(MainActivity.this);
                GetApplicationListRequest request = new GetApplicationListRequest();
                request.BaseRequest = baseRequest;
                GetApplicationListResponse response = mIMInterfaceProxy
                        .getApplicationList(request);
                if (response == null) {
                    return null;
                }
                if (response.BaseResponse.Ret != BaseResponse.RET_SUCCESS) {
                    Toast.makeText(MainActivity.this, response.BaseResponse.ErrMsg,
                            Toast.LENGTH_SHORT).show();
                    return null;
                }
                return response.memberList;
            }

            @Override
            protected void onPostExecute(List<Member> members) {
                if (members != null && members.size() > 0)
                    for (Member member : members) {
                        doInbackgroundInsert(member);
                    }
            }
        }.execute();
        registerReceiver(wifiStateReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        saveConfig = new SaveConfig(this);
    }

    @Override
    protected void onDestroy() {
        MyApplication.getInstance().removeActivity(this);
        unregisterReceiver(wifiStateReceiver);
        super.onDestroy();
    }

    /**
     * 给Tab按钮设置图标和文字
     */
    private View getTabItemView(LayoutInflater layoutInflater, int picResid, String caption) {
        View view = layoutInflater.inflate(R.layout.tab_indicator, null);
        ImageView imageView = (ImageView) view.findViewById(R.id.imageview);
        imageView.setImageResource(picResid);
        TextView textView = (TextView) view.findViewById(R.id.textview);
        textView.setText(caption);
        return view;
    }

    @Override
    protected void onResume() {
        Utils.cancelNotification(this);
        doInBackground();
        getContentResolver().registerContentObserver(
                XmppMessageContentProvider.CONTENT_URI, true, mContentObserver);

        super.onResume();
    }

    @Override
    protected void onPause() {
        getContentResolver().unregisterContentObserver(mContentObserver);
        super.onPause();
    }


    @Background
    void doInBackground() {
        String localUserName = MyApplication.getInstance().getOpenfireJid();
        long unreadChatCount = XmppDbManager.getInstance(this).getChatListUnreadCount
                (localUserName);
        long unreadAppCount = XmppDbManager.getInstance(this).getAppPushChatListUnreadCount
                (localUserName);
        onPostExecute(unreadChatCount, unreadAppCount);
    }


    @UiThread
    void onPostExecute(long chatListUnreadCount, long unreadAppCount) {
        if (chatListUnreadCount > 0) {
            chatHint.setVisibility(View.VISIBLE);
            chatHint.setText(String.valueOf(chatListUnreadCount));
            chatHint.show();
            chatHint.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
        } else {
            chatHint.setVisibility(View.INVISIBLE);
        }

        if (unreadAppCount > 0) {
            appHint.setVisibility(View.VISIBLE);
            appHint.setText(String.valueOf(unreadAppCount));
            appHint.show();
            appHint.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
        } else {
            appHint.setVisibility(View.INVISIBLE);
        }
    }


    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 200:
                    if (!saveConfig.getStringConfig("httpConfig").equals("true")) {
                        saveConfig.setStringConfig("httpConfig", "true");
                        Toast.makeText(MainActivity.this,"切换内网",Toast.LENGTH_SHORT).show();
                    }
                    break;
                default:
                    if (saveConfig.getStringConfig("httpConfig").equals("true")) {
                        saveConfig.setStringConfig("httpConfig", "false");
                        Toast.makeText(MainActivity.this,"切换外网",Toast.LENGTH_SHORT).show();
//                        XmppManager.getInstance().exit();
//                        ImagerLoaderOptHelper.cleanCache();
//                        MyApplication.getInstance().onReLogin();
                    }
                    break;
            }
        }
    };

    private ContentObserver mContentObserver = new ContentObserver(
            mHandler) {
        public void onChange(boolean selfChange) {
            L.d(TAG, "onChange(boolean)");
            lastMsgTime = 0;
            doInBackground();
        }
    };

    private long mExitTime;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                mExitTime = System.currentTimeMillis();
            } else {
                //按返回键退出程序但是不销毁，程序后台，运行，再点击app启动图标时，进入最后退出的那个activity，
                // 类似点击了home键
                moveTaskToBack(true);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Background
    public void doInbackgroundInsert(Member member) {
        Cursor cursor = XmppDbManager.getInstance(MainActivity.this).getChatList(MyApplication.getInstance().getOpenfireJid(),
                member.Uid.concat(Member.SUFFIX_APP), MsgInFo.TYPE_CHAT);
        if (cursor.moveToFirst()) {
            XmppMessage msg = MyApplication.getDaoSession(MainActivity.this).getXmppMessageDao().readEntity(cursor, 0);
            msg.setExtLocalDisplayName(member.NickName);
            msg.setExtRemoteUserName(member.UserName);//根据appid分类
            msg.setExtRemoteDisplayName(member.NickName);
            msg.setPushToken(member.NickName);
            msg.setPushContent(member.Description);

            msg.setPushObjectContentAppId(member.Uid);
            msg.setBody(member.Description);
            XmppDbManager.getInstance(MainActivity.this).updateWithNotify(msg);
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
//            msg.setCreateTime(me.getCreateTime());
        XmppDbManager.getInstance(MainActivity.this).insertMessageWithNotify(msg);
    }


    BroadcastReceiver wifiStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Utils.isNetworkAvailable(getBaseContext())) {
                getConfig();
            } else {
//                Toast.makeText(MainActivity.this, "网络已断开", Toast.LENGTH_SHORT).show();
            }
        }
    };

    boolean isGeting;

    void getConfig() {
        if (isGeting)
            return;
        isGeting = true;
        new Thread(new Runnable() {
            public void run() {
                HttpClient httpclient = new DefaultHttpClient();
                httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 2000);
                HttpPost httppost = new HttpPost("http://" + OpenfireConstDefine.OPENFIRE_SERVER_HOST + "/app/client/app/user/auth");
                try {
                    HttpResponse response;
                    response = httpclient.execute(httppost);
                    int backCode = response.getStatusLine().getStatusCode();

                    mHandler.sendEmptyMessage(backCode);
                } catch (IOException e) {
                    e.printStackTrace();
                    mHandler.sendEmptyMessage(-200);
                }
                isGeting = false;
            }
        }).start();
    }


}
