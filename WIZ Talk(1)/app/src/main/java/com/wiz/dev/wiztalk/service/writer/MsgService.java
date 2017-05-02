package com.wiz.dev.wiztalk.service.writer;

import java.util.List;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

public class MsgService extends Service {

	private static MsgWriter MSG_WRITER;
		
	public static MsgWriter getMsgWriter(Context context) {
		if (!isServiceRunning(context)) {
			Intent intent = new Intent(context, MsgService.class);
			context.startService(intent);
		}
		if (MSG_WRITER == null) {
			MSG_WRITER = new MsgWriter(context);
			MSG_WRITER.startup();
			// TODO: 2015/11/24  
//			List<Message> dbMsgs = DBManager.getInstance(context).getMessagesPendingAndSending(MyApplication.getInstance().getLocalUserName());
//			MSG_WRITER.sendMsgs(dbMsgs);
		}
		return MSG_WRITER;
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		if (MSG_WRITER != null) {
			MSG_WRITER.shutdown();
		}
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	public static boolean isServiceRunning(Context context) {
        boolean isRunning = false;

        ActivityManager activityManager =
                (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList
                = activityManager.getRunningServices(Integer.MAX_VALUE);

        if (serviceList == null || serviceList.size() == 0) {
            return false;
        }

        for (int i = 0; i < serviceList.size(); i++) {
            if (serviceList.get(i).service.getClassName().equals(MsgService.class.getName())) {
                isRunning = true;
                break;
            }
        }
        return isRunning;
    }
	
}
