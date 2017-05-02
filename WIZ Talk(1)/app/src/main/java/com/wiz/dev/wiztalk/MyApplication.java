package com.wiz.dev.wiztalk;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.support.multidex.MultiDex;

import com.download.services.MyDownloadManager;
import com.epic.traverse.push.services.XmppService;
import com.epic.traverse.push.util.L;
import com.morgoo.droidplugin.PluginHelper;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.wiz.dev.wiztalk.DB.DaoMaster;
import com.wiz.dev.wiztalk.DB.DaoSession;
import com.wiz.dev.wiztalk.DB.Member;
import com.wiz.dev.wiztalk.activity.LoginActivity_;
import com.wiz.dev.wiztalk.crash.CrashHandler;
import com.wiz.dev.wiztalk.download.DownloadManager;
import com.wiz.dev.wiztalk.download.DownloadService;
import com.wiz.dev.wiztalk.dto.response.LoginResponse;
import com.wiz.dev.wiztalk.preference.CachePrefs_;
import com.wiz.dev.wiztalk.preference.LoginPrefs_;
import com.wiz.dev.wiztalk.public_store.ConstDefine;
import com.wiz.dev.wiztalk.public_store.PublicTools;
import com.wiz.dev.wiztalk.service.writer.MsgService;
import com.wiz.dev.wiztalk.upload.UploadManager;
import com.wiz.dev.wiztalk.upload.UploadService;
import com.wiz.dev.wiztalk.utils.ImagerLoaderOptHelper;
import com.wiz.dev.wiztalk.utils.SmileyParser;

import org.afinal.simplecache.ACache;
import org.androidannotations.annotations.EApplication;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cn.finalteam.filedownloaderfinal.DbUpgradeListener;
import cn.finalteam.filedownloaderfinal.DownloaderManager;
import cn.finalteam.filedownloaderfinal.DownloaderManagerConfiguration;
import cn.finalteam.okhttpfinal.OkHttpFinal;
import cn.finalteam.okhttpfinal.OkHttpFinalConfiguration;
import cn.finalteam.toolsfinal.StorageUtils;

/**
 * Created by ynyxmac on 16/2/4.
 */
@EApplication
public class MyApplication extends Application {

    protected static final String TAG = "MyApplication";

    private static MyApplication mInstance;
    //    private FrameworkInstance frame = null;
    private static DaoMaster daoMaster;
    private static DaoSession daoSession;

    UploadManager mUploadManager;

    MyDownloadManager mDownloadManager;



    private String mUid;

    @Pref
    CachePrefs_ mCachePrefs;

    @Pref
    LoginPrefs_ mLoginPrefs;

    /**
     * 获取全局Application
     *
     * @return
     */
    public static MyApplication getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {

        mInstance = this;
        ImageLoader.getInstance().init(ImagerLoaderOptHelper.getImagLoaderConfig(getApplicationContext()));
//        initAppAdd();
        OkHttpFinalConfiguration.Builder builder = new OkHttpFinalConfiguration.Builder();
        OkHttpFinal.getInstance().init(builder.build());
        mCachePrefs.getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(
                        mOnSharedPreferenceChangeListener);
        SmileyParser.init(this);

        MsgService.getMsgWriter(this);
//        XmppService.startService(this);

        mUploadManager = UploadService.getUploadManager(this);
        mDownloadManager = new MyDownloadManager();

        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(this.getApplicationContext());
        super.onCreate();
        PluginHelper.getInstance().applicationOnCreate(getBaseContext());//这里必须在super.onCreate方法之后，顺序不能变
    }

    @Override
    protected void attachBaseContext(Context base) {
        PluginHelper.getInstance().applicationAttachBaseContext(base);
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    /* public FrameworkInstance getFrame() {
         return frame;
     }

     private void initAppAdd() {
         List<BundleActivator> bundleActivators = new ArrayList<BundleActivator>();
         try {
             frame = FrameworkFactory.getInstance().start(null, this);
             BundleContext context = frame.getSystemBundleContext();
             // InstallBundler 是2.7.0版本内置与框架中的
             InstallBundler ib = new InstallBundler(context);
           *//*  ib.installForAssets("MyBundle.apk", "1.0", null,
                    new installCallback() {
                        @Override
                        public void callback(int arg0, Bundle arg1) {
                            if (arg0 == installCallback.stutas5
                                    || arg0 == installCallback.stutas7) {
                                Log.d("hk",
                                        String.format("插件安装 %s ： %d",
                                                arg1.getName(), arg0));
                                return;
                            } else {
                                Log.d("hk", "插件安装失败 ：%d" + arg0);
                            }
                        }
                    });*//*
            ib.installForAssets("wizplant-release.apk", "1.0", null, null);
            ib.installForAssets("mypdfview-release.apk", "1.0", null, null);
//            ib.installForAssets("mywordview-release.apk", "1.0", null, null);
//            ib.installForAssets("myexcle-release.apk", "1.0", null, null);
//            ib.installForAssets("myppt-release.apk", "1.0", null, null);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.exit(0);
        }
    }
*/
    private SharedPreferences.OnSharedPreferenceChangeListener mOnSharedPreferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(
                SharedPreferences sharedPreferences, String key) {
            L.d(TAG, "onSharedPreferenceChanged() key:" + key);

            String uidOld = mUid;
            String uidNew = mCachePrefs.uid().get();
            mUid = uidNew;
            L.d(TAG, "onSharedPreferenceChanged() uidOld:" + uidOld);
            L.d(TAG, "onSharedPreferenceChanged() uidNew:" + uidNew);
            if (uidNew == null || (uidNew != null && !uidNew.equals(uidOld))) {
                XmppService.startService(getApplicationContext());
            }
        }
    };

    @Override
    public void onTerminate() {
        mCachePrefs.getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(
                        mOnSharedPreferenceChangeListener);

        super.onTerminate();
    }

    public void noNetLogout(){
        XmppService.stopService(this);
        // TODO: 2016/3/16
        // TODO: 2016/4/3
    }

    public void onReLogin() {
        XmppService.stopService(this);
        mCachePrefs.edit()
                .uid().put(null)
                .userName().put(null)
                .token().put(null)
                .apply();
        mLoginPrefs.edit()
                .openfireJid().put(null)
                .openfirePwd().put(null)
                .isLogout().put(true)
                .isAutoLogin().put(false)
                .apply();
        // TODO: 2016/3/16  
        PublicTools.saveLocalBoolean(this, "zddl", false);
        ACache.get(this).clear();
        ConstDefine.token = "";
        ConstDefine.uid = "";
        // TODO: 2016/4/3  
        ImagerLoaderOptHelper.cleanCache();

        LoginActivity_.intent(this).flags(Intent.FLAG_ACTIVITY_NEW_TASK).start();
        for (Iterator<Activity> it = mActivityList.iterator(); it.hasNext(); ) {
            Activity activity = it.next();
            try {
                activity.finish();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public String getToken() {
        return mCachePrefs.token().get();
    }

    public String getUid() {
        return mCachePrefs.uid().get();
    }

    public String getLocalUserName() {
        return mCachePrefs.userName().get();
    }

    /**
     * 取得DaoMaster
     *
     * @param context
     * @return
     */
    public static DaoMaster getDaoMaster(Context context) {
        if (daoMaster == null) {
            DaoMaster.OpenHelper helper = new DaoMaster.DevOpenHelper(context,
                    "message-db", null);
            daoMaster = new DaoMaster(helper.getWritableDatabase());
        }
        return daoMaster;
    }

    /**
     * 取得DaoSession
     *
     * @param context
     * @return
     */
    public static DaoSession getDaoSession(Context context) {
        if (daoSession == null) {
            if (daoMaster == null) {
                daoMaster = getDaoMaster(context);
            }
            daoSession = daoMaster.newSession();
        }
        return daoSession;
    }

    public Member getLocalMember() {
        LoginResponse response = (LoginResponse) ACache.get(this).getAsObject("LoginResponse");
        if (response != null) {
            return response.Member;
        }
        return null;
    }

    public void putLocalMember(Member member) {
        if (member != null) {
            LoginResponse response = getLoginResponse();
            response.Member = member;
            putLoginResponse(response);
        }
    }

    public void putLoginResponse(LoginResponse response) {
        //已经登录成功了
        XmppService.startService(this);
        ACache.get(this).put("LoginResponse", response);

        if (response != null && response.Member != null) {
            Member member = response.Member;
            mCachePrefs.edit()
                    .uid().put(member.Uid)
                    .userName().put(member.UserName)
                    .token().put(response.Token)
                    .paasBaseUrl().put(response.PaasBaseUrl)
                    .apply();
        }
    }

    public String getPaasBaseUrl() {
        return mCachePrefs.paasBaseUrl().get();
    }

    public LoginResponse getLoginResponse() {
        return (LoginResponse) ACache.get(this).getAsObject("LoginResponse");
    }

    /**
     * @return 12adasa23@admin-pc
     */
    public String getOpenfireJid() {
        return mLoginPrefs.openfireJid().get().toLowerCase();
    }

    public String getOpenfirePwd() {
        return mLoginPrefs.openfirePwd().get();
    }

    public UploadManager getUploadManager() {
        return mUploadManager;
    }

    public MyDownloadManager getDownloadManager() {
        return mDownloadManager;
    }

    /**
     * 返回true才是手动
     *
     * @return
     */
    public boolean isLogout() {
        return mLoginPrefs.isLogout().getOr(false);
    }

    private List<Activity> mActivityList = new ArrayList<Activity>();

    public void addActivity(Activity activity) {
        mActivityList.add(activity);
    }

    public void removeActivity(Activity activity) {
        mActivityList.remove(activity);
    }

    /**
     * 更新数据库监听
     */
    private DbUpgradeListener dbUpgradeListener = new DbUpgradeListener() {
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    };
}
