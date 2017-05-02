package com.wiz.dev.wiztalk.apppush.loader;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.CursorLoader;

import com.wiz.dev.wiztalk.DB.XmppDbManager;

//
public class AppRecentChatLoader extends CursorLoader {

	public String localUserName;

	public AppRecentChatLoader(Context context, String localUserName) {
		super(context);

		this.localUserName = localUserName;
	}

	@Override
	public Cursor loadInBackground() {
		Cursor cursor = XmppDbManager.getInstance(getContext()).getAppRecentMessage(
				localUserName);
		return cursor;
	}

}
