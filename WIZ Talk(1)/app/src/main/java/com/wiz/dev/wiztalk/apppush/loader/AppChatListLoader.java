package com.wiz.dev.wiztalk.apppush.loader;

import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;

import com.wiz.dev.wiztalk.DB.XmppDbManager;
public class AppChatListLoader extends CursorLoader {

	public String localUserName;
	public String appId;

	public AppChatListLoader(Context context, String localUserName, String appId) {
		super(context);

		this.localUserName = localUserName;
		this.appId = appId;
	}

	@Override
	public Cursor loadInBackground() {
		Cursor cursor = XmppDbManager.getInstance(getContext())
				.getAppPushChatList(localUserName,appId);
	
		return cursor;
	}

}
