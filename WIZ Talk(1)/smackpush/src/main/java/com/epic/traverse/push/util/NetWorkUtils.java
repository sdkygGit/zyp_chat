package com.epic.traverse.push.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetWorkUtils {

	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		if (connectivity != null) {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null) {// 逐一查找状态为已连接的网络
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		}
		return false;
	}

//	/**
//	 * ISO-8859-1 to UTF-8
//	 * 
//	 * @param str
//	 *            ISO-8859-1
//	 * @return UTF-8
//	 */
//	public static String encode(String str) {
////		try {
////			return new String(str.getBytes("ISO-8859-1"), "UTF-8");
////		} catch (UnsupportedEncodingException e) {
////			e.printStackTrace();
////		}
//		try {
//			return new String(str.getBytes("iso-8859-1"),"GB2312");
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//		}
//		return null;
//	}
}
