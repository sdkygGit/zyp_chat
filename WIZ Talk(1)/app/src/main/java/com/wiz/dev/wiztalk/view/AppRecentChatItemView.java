package com.wiz.dev.wiztalk.view;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.morgoo.droidplugin.pm.PluginManager;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.wiz.dev.wiztalk.DB.Member;
import com.wiz.dev.wiztalk.DB.MsgInFo;
import com.wiz.dev.wiztalk.DB.XmppMessage;
import com.wiz.dev.wiztalk.R;
import com.wiz.dev.wiztalk.activity.H5Activity_;
import com.wiz.dev.wiztalk.activity.XchatActivity_;
import com.wiz.dev.wiztalk.apppush.view.AppPushFragment;
import com.wiz.dev.wiztalk.dto.request.BaseRequest;
import com.wiz.dev.wiztalk.dto.request.CheckPlugUpdateRequest;
import com.wiz.dev.wiztalk.dto.request.GetMemberRequest;
import com.wiz.dev.wiztalk.dto.request.GetMemberResponse;
import com.wiz.dev.wiztalk.dto.response.CheckPlugUpdateResponse;
import com.wiz.dev.wiztalk.dto.util.RoundImageView;
import com.wiz.dev.wiztalk.rest.Appmsgsrv8094;
import com.wiz.dev.wiztalk.rest.Appmsgsrv8094Proxy;
import com.wiz.dev.wiztalk.utils.CacheUtils;
import com.wiz.dev.wiztalk.utils.ImagerLoaderOptHelper;
import com.wiz.dev.wiztalk.utils.Utils;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.DimensionPixelOffsetRes;
import org.androidannotations.api.rest.RestErrorHandler;
import org.springframework.core.NestedRuntimeException;

import java.io.File;
import java.util.LinkedHashMap;

import cn.finalteam.okhttpfinal.FileDownloadCallback;
import cn.finalteam.okhttpfinal.HttpRequest;

import static com.morgoo.helper.compat.PackageManagerCompat.INSTALL_FAILED_NOT_SUPPORT_ABI;
import static com.morgoo.helper.compat.PackageManagerCompat.INSTALL_SUCCEEDED;

/**
 * 最近聊天项
 * Created by Dong on 2015/10/13. R.layout.item_conversation
 */
@EViewGroup(R.layout.plugin_item)
public class AppRecentChatItemView extends RelativeLayout {
    private boolean isOpen = false;
    private static final String TAG = "AppRecentChatItemView";
    private String url = "http://112.74.104.234/apk/plugins/wizplant.apk",
            savaPath =
                    Environment.getExternalStorageDirectory().getAbsolutePath() +
                            "/wizTalkPulg/";

    private File file;
    @ViewById
    ImageView iv_recent_avatar;

    @ViewById
    TextView name;

    @ViewById
    TextView desc;

    @ViewById
    LinearLayout asdlayout_left;

    @ViewById
    ImageView plugin_imageView;

    @ViewById
    RelativeLayout asdlayout_right;

    @ViewById
    RoundProgress roundProgress;

    @ViewById
    TextView app_recent_unread;

    private Context mContext;

    public XmppMessage message;


    @DimensionPixelOffsetRes(R.dimen.icon_size_bigger)
    int sizeBigger;

    @DimensionPixelOffsetRes(R.dimen.icon_size_small)
    int sizeSmall;


    public AppRecentChatItemView(Context context) {
        super(context);
        this.mContext = context;
    }

    public void bind(XmppMessage msg, int unRead) {
        this.message = msg;

        if (msg.getType().toString().equals(MsgInFo.TYPE_HEADLINE)) {
            //应用消息
            desc.setText(msg.getBody());
            name.setText(msg.getExtRemoteDisplayName());
        }

        if (unRead > 0) {
            app_recent_unread.setText(unRead + "");
            app_recent_unread.setVisibility(View.VISIBLE);
        } else {
            app_recent_unread.setVisibility(View.GONE);
        }
        String username = null;
        if (msg.getType().equalsIgnoreCase(MsgInFo.TYPE_CHAT)) {
            username = msg.getExtRemoteUserName().split("@")[0];
            username = username.concat(Member.SUFFIX_USER);
        } else {
//            群没有头像
            //3@group.skysea.com/8a34f8c9491f3d8101493bb934bb34c2
            username = msg.getFrom_();
            if (username.contains("/")) {
                username = username.substring(username.lastIndexOf("/") + 1, username.length
                        ()).toLowerCase();
                username = username.concat(Member.SUFFIX_USER);
            }
        }
//        setIcon(username);

        ImageLoader.getInstance().displayImage(Utils.getAvataUrl(msg.getExtRemoteUserName(), sizeBigger), iv_recent_avatar,
                ImagerLoaderOptHelper.getAppAvatarOpt());

        asdlayout_left.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!AppPushFragment.isOpenMenu) {
                   /* Intent i = new Intent();
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.setClassName(mContext, "com.example.hekang.wizplant.activity.Activity_seach");
                    mContext.startActivity(i);*/
                    //先检查是否存在插件,然后在安装打开
                    new AsyncTask<Void, Void, CheckPlugUpdateResponse>() {

                        String type = "";
                        Appmsgsrv8094 mAppmsgsrv8094;

                        @Override
                        protected void onPreExecute() {
                            mAppmsgsrv8094 = Appmsgsrv8094Proxy.create(5 * 1000);
                            mAppmsgsrv8094.setRestErrorHandler(new RestErrorHandler() {
                                @Override
                                public void onRestClientExceptionThrown(NestedRuntimeException e) {
                                    Toast.makeText(mContext, "访问失败", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        @Override
                        protected CheckPlugUpdateResponse doInBackground(Void... params) {

                            try {
                                BaseRequest baseRequest = CacheUtils.getBaseRequest(mContext);
                                GetMemberRequest request = new GetMemberRequest();
                                request.BaseRequest = baseRequest;
                                request.UserName = message.getExtRemoteUserName().split("@")[0].concat(Member.SUFFIX_APP);
                                GetMemberResponse response = mAppmsgsrv8094.getMember(request);
                                type = response.Member.PYInitial;

//                            BaseRequest baseRequest2 = CacheUtils.getBaseRequest(mContext);
                                CheckPlugUpdateRequest request2 = new CheckPlugUpdateRequest();
                                request2.BaseRequest = baseRequest;
                                request2.VersionCode = 1;//这里的版本号和版本名字以及APPID应该是获取的
                                request2.VersionName = "1.0";
                                request2.Appid = response.Member.Uid;
                                if (type.equalsIgnoreCase("h5")) {
                                    request2.Type = "h5";
                                }
                                CheckPlugUpdateResponse response2 = mAppmsgsrv8094.checkPlugUpdate(request2);


                                return response2;
                            } catch (Exception e) {

                            }

                            return null;
                        }

                        @Override
                        protected void onPostExecute(CheckPlugUpdateResponse response) {

                            if (response == null) {
                                Toast.makeText(mContext, "插件不存在", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            if (TextUtils.isEmpty(response.BaseResponse.ErrMsg) && response.isUpdate) {
                                LinkedHashMap<String, String> oj = (LinkedHashMap<String, String>) response.msg.getObjectContent();
                                final String downloadUrl = oj.get("downloadUrl");

                                if (type.equalsIgnoreCase("h5")) {
                                    H5Activity_.intent(mContext).remoteDisplayName(message.getExtRemoteDisplayName())
                                            .url(downloadUrl).start();
                                } else {
                                    file = new File(savaPath + oj.get("fileName"));
                                    isOpen = false;//判断是否打开消息界面
                                    if (!file.exists()) {
                                        initDownBundle();
                                    } else {
                                        initOpenBundle();
                                    }
                                }

                            } else {//如果不是最新版本就直接打开
//                                Toast.makeText(mContext, "已经是最新版本", Toast.LENGTH_SHORT).show();
                                LinkedHashMap<String, String> oj = (LinkedHashMap<String, String>) response.msg.getObjectContent();
                                final String downloadUrl = oj.get("downloadUrl");

                                if (type.equalsIgnoreCase("h5")) {
                                    H5Activity_.intent(mContext).remoteDisplayName(message.getExtRemoteDisplayName())
                                            .url(downloadUrl).start();
                                } else {
                                    file = new File(savaPath + oj.get("fileName"));
                                    isOpen = false;//判断是否打开消息界面
                                    if (!file.exists()) {
                                        initDownBundle();
                                    } else {
                                        initOpenBundle();
                                    }
                                }
                            }
                        }
                    }.execute();

                } else {
                    AppPushFragment.mListView.closeSlidedItem();
                    AppPushFragment.isOpenMenu = false;
                }
            }
        });

        asdlayout_right.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                isOpen = true;
                if (!AppPushFragment.isOpenMenu) {
//                    if (file.exists())
                    XchatActivity_.intent(mContext).remoteUserName(message.getExtRemoteUserName())
                            .remoteUserNick(message.getExtRemoteDisplayName())
                            .chatType(MsgInFo.TYPE_CHAT).start();
//                    else
//                        initDownBundle();
                } else {
                    AppPushFragment.mListView.closeSlidedItem();
                    AppPushFragment.isOpenMenu = false;
                }
            }
        });
    }

    private void initOpenBundle() {//packageName和filePath应该是传递过来的
        String packageName = "com.example.hekang.myapplication";
        if (!PluginManager.getInstance().isConnected()) {
            Toast.makeText(mContext, "插件服务正在初始化，请稍后再试。。。", Toast.LENGTH_SHORT).show();
        }
        try {
            if (PluginManager.getInstance().getPackageInfo(packageName, 0) != null) {
//                Toast.makeText(mContext, "已经安装了，不能再安装", Toast.LENGTH_SHORT).show();
                PackageManager pm = mContext.getPackageManager();
                Intent intent = pm.getLaunchIntentForPackage(packageName);//应用的包名+application
                intent.putExtra("openSelected", 0);//这个地方是用来判断启动插件的哪个界面
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
                Log.d("hk", "start");
            } else {///storage/emulated/0/wizplant-release.apk
                doInstall(file.getAbsolutePath(), packageName);//这个地方是插件存放的地址
            }
        } catch (Exception e) {
            e.printStackTrace();
            try {
                PluginManager.getInstance().installPackage(file.getAbsolutePath(), 0);
            } catch (RemoteException e1) {
                e1.printStackTrace();
            }
        }
    }

    private synchronized void doInstall(String filePath, String packageName) {//插件保存的地址和插件的包名+application
        try {
            final int re = PluginManager.getInstance().installPackage(filePath, 0);
            switch (re) {
                case PluginManager.INSTALL_FAILED_NO_REQUESTEDPERMISSION:
                    Toast.makeText(mContext, "安装失败，文件请求的权限太多", Toast.LENGTH_SHORT).show();
                    break;
                case INSTALL_FAILED_NOT_SUPPORT_ABI:
                    Toast.makeText(mContext, "宿主不支持插件的abi环境，可能宿主运行时为64位，但插件只支持32位", Toast.LENGTH_SHORT).show();
                    break;
                case INSTALL_SUCCEEDED:
                    Toast.makeText(mContext, "安装完成", Toast.LENGTH_SHORT).show();
                    PackageManager pm = mContext.getPackageManager();
                    Intent intent = pm.getLaunchIntentForPackage(packageName);//应用的包名+application
                    intent.putExtra("openSelected", 0);//这个地方是用来判断启动插件的哪个界面
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(intent);
                    break;
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void initDownBundle() {
        HttpRequest.download(url, file, new FileDownloadCallback() {
            //开始下载
            @Override
            public void onStart() {
                super.onStart();
                roundProgress.setVisibility(View.VISIBLE);
                plugin_imageView.setVisibility(View.INVISIBLE);
            }

            //下载进度
            @Override
            public void onProgress(int progress, long networkSpeed) {
                super.onProgress(progress, networkSpeed);
                roundProgress.setProgress(progress);
            }

            //下载失败
            @Override
            public void onFailure() {
                super.onFailure();
                Toast.makeText(mContext, "下载失败", Toast.LENGTH_SHORT).show();
                roundProgress.setVisibility(View.INVISIBLE);
                plugin_imageView.setVisibility(View.VISIBLE);
            }

            //下载完成（下载成功）
            @Override
            public void onDone() {
                super.onDone();
                // TODO: 2016/5/16 下载完插件，应该把插件的Appid，VersionName，VersionCode保存起来，为后来更新插件做参考
                roundProgress.setVisibility(View.INVISIBLE);
                plugin_imageView.setVisibility(View.VISIBLE);
                if (isOpen)//判断是否进入消息界面
                    XchatActivity_.intent(mContext).remoteUserName(message.getExtRemoteUserName())
                            .remoteUserNick(message.getExtRemoteDisplayName())
                            .chatType(MsgInFo.TYPE_CHAT).start();
                else
                    initOpenBundle();//如果不进入消息界面，肯定就是进入插件
            }
        });
    }


}
