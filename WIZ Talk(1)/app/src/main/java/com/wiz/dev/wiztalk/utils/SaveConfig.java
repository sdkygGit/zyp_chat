package com.wiz.dev.wiztalk.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SaveConfig {
	private SharedPreferences mShare;

	public SaveConfig(Context context) {
		this.mShare = context.getSharedPreferences(
				SaveConfig.class.getSimpleName(), Context.MODE_PRIVATE);
	}

	public void setStringConfig(String key, String value) {
		mShare.edit().putString(key, value).commit();
	}
	
	public String getStringConfig(String key){
		return mShare.getString(key, null);
	}

	public static final int LOGIN_REQ_CODE = 0x19;
	
	//获取异步任务代码key
	public static final String TASK_CODE_KET = "task_code";
	
	//异步登陆代码
	public static final int TASK_CODE_LOGIN = 0x110;

	public static final String SHARE_PWD ="2016_05_11_16_06";
	public static final String FILE_PWD ="2016_07_09";

	public static final  String UPDATA_TO_READ_PWD = "fdsgsgbdgdaaafafwafafafsfsgasfewfasbgsagewgsega3t4awgagva5463210g2sa1g4e";
	
	public static final String IP_Z ="(25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]?\\d)){3}";
	public static final String PORT_Z ="[0-9]|[1-9][0-9]{1,3}|[1-6][0-5][0-5][0-3][0-5]";
}
