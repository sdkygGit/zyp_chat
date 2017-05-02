package com.wiz.dev.wiztalk.utils;

import android.content.Context;
import android.graphics.Bitmap;

import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.bitmap.BitmapCommonUtils;

public class XXBitmapUtils extends BitmapUtils {

//	public long BITMAP_UTIL_DEFAULT_CACHEEXPIRY = 1000L * 60 * 60 * 24 * 1; // 1 day
	public long BITMAP_UTIL_DEFAULT_CACHEEXPIRY = 1000L * 60 * 60 * 1; // 1 h
	
	public XXBitmapUtils(Context context) {
		super(context);
		
//		this.configDefaultLoadingImage(R.drawable.default_avatar);
//		this.configDefaultLoadFailedImage(R.drawable.default_avatar);
		
		this.configDefaultBitmapConfig(Bitmap.Config.RGB_565);
		this.configDefaultBitmapMaxSize(BitmapCommonUtils.getScreenSize(context));
		
		this.configDefaultConnectTimeout(10 * 1000);
		this.configDefaultReadTimeout(10 * 1000);
		
		this.configMemoryCacheEnabled(true);
		this.configDiskCacheEnabled(true);
		
		this.configDefaultCacheExpiry(BITMAP_UTIL_DEFAULT_CACHEEXPIRY);
	}
}
