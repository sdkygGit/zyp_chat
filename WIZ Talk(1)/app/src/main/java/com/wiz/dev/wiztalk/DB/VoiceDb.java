package com.wiz.dev.wiztalk.DB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class VoiceDb extends SQLiteOpenHelper {

	public static final String DB_NAME = "voice.db";
	public static final String T_NAME = "voice";

	public static final String SEND_NO_READ ="0";//未读
	public static final String SEND_READ ="1";//读过了

	public static final String REC_NO_READ ="2";//未读
	public static final String REC_READ ="3";//读过了

	public static final String TYPE_SEND ="send";//发送的声音
	public static final String TYPE_REC ="receive";//接收的声音


	public VoiceDb(Context context) {
		this(context, DB_NAME, null, 2);
	}

	public VoiceDb(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		try{
			db.execSQL("CREATE TABLE "+T_NAME+"( _id integer primary key autoincrement," +
					" filePath text UNIQUE, type text, create_time INTEGER);");
		}catch (Exception e){}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

}
