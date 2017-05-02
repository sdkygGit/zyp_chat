package com.wiz.dev.wiztalk.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.wiz.dev.wiztalk.DB.XmppDbManager;

public class RefreshUtils {

	boolean hasMore = false;
	
	int DELAY_TIME = 2 * 1000;
	long mLastCallTime = 0l;
	
	Context mContext;
	Uri	mUri;
	
	@SuppressLint("HandlerLeak") 
	Handler mHandler = new Handler(Looper.getMainLooper()){
		@Override
		public void handleMessage(Message msg) {
			mContext.getContentResolver().notifyChange(mUri, null);
			XmppDbManager.getInstance(mContext).notification(mContext);
			
			super.handleMessage(msg);
		}
		
	};
	
	final int MSG_REFRESH = 0;
	
	public void INeedNotify(Context aContext,Uri aUri){
		mUri = aUri;
		mContext = aContext;
		
		long currentTimeMillis = System.currentTimeMillis();
		
		if( currentTimeMillis - mLastCallTime < DELAY_TIME ){
			mHandler.removeMessages(MSG_REFRESH);
			mHandler.sendEmptyMessageDelayed(MSG_REFRESH, DELAY_TIME);
		}else{
			mLastCallTime = System.currentTimeMillis();
			mHandler.removeMessages(MSG_REFRESH);
			mHandler.sendEmptyMessage(MSG_REFRESH);
		}
	}
}
