package com.epic.traverse.push.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.epic.traverse.push.services.XmppService;
import com.epic.traverse.push.util.NetWorkUtils;


public class ConnectivityChangeReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {

		if (NetWorkUtils.isNetworkAvailable(context)) {
//			AlarmReceiver.alarm(context);
			XmppService.startService(context);
		} else {
			//AlarmReceiver.cancel(context);
			XmppService.stopService(context);
		}
	}

}
