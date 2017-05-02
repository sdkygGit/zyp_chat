package com.wiz.dev.wiztalk.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.epic.traverse.push.services.XmppService;
import com.epic.traverse.push.smack.XmppManager;
import com.wiz.dev.wiztalk.MyApplication;
import com.wiz.dev.wiztalk.utils.ImagerLoaderOptHelper;

/**
 * Created by Administrator on 2016/5/25.
 */
public class LoginConflictReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("com.yxst.epic.unifyplatform.conflict")) {
            XmppManager.getInstance().exit();
            ImagerLoaderOptHelper.cleanCache();
            MyApplication.getInstance().onReLogin();

//            XmppService.stopService(this);
            Toast.makeText(context,"你的账号在其他地方登陆，被挤下线",Toast.LENGTH_SHORT).show();
        }
    }

}
