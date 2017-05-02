package com.wiz.dev.wiztalk.utils;

import android.content.Context;

import com.wiz.dev.wiztalk.R;
import com.wiz.dev.wiztalk.dto.request.BaseRequest;
import com.wiz.dev.wiztalk.preference.CachePrefs_;

public class CacheUtils {

	public static BaseRequest getBaseRequest(Context context) {
		CachePrefs_ cachePrefs = new CachePrefs_(context);
		
		BaseRequest request = new BaseRequest();
		
//		request.DeviceID = Utils.getDeviceId(context);
		request.DeviceID = Utils.getAndroidDeviceId(context);
		request.Token = cachePrefs.token().get();
		request.Uid = cachePrefs.uid().get();
		request.CustomerId = context.getResources().getString(R.string.customer_id);
		
		return request;
	}
}
