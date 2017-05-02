package com.epic.traverse.push.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.epic.traverse.push.services.XmppService;

public class StaticReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();

		XmppService.startService(context);
	}

}
