package com.witskies.manager.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class FileServiceOpenHelper extends SQLiteOpenHelper {
	private static final int DATABASE_VERSION = 1;
	private static FileServiceOpenHelper instance;

	private static final String FILE_SERVICE = "CREATE TABLE "
			+ MobileContent.TABLE_NAME + " (" + MobileContent.COLUMN_ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT,"
			+ MobileContent.MOBILE_AUDIOS + " TEXT, "
			+ MobileContent.MOBILE_VIDEOS + " TEXT, "
			+ MobileContent.MOBILE_DOCUMENTS + " TEXT); ";
//			+ MobileContent.MOBILE_DOCUMENTS + " TEXT, "
//			+ " UNIQUE " +MobileContent.MOBILE_AUDIOS +" )";

	private FileServiceOpenHelper(Context context) {
		super(context, getUserDatabaseName(), null, DATABASE_VERSION);
	}
	

	public static FileServiceOpenHelper getInstance(Context context) {
		if (instance == null) {
			instance = new FileServiceOpenHelper(
					context.getApplicationContext());
		}
		return instance;
	}

	private static String getUserDatabaseName() {
		/**
		 * Format(String, Object)
		 **/
		return String.format("fileSerivce_ch.db");
	
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL(FILE_SERVICE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

	public void closeDB() {
		if (instance != null) {
			try {
				SQLiteDatabase db = instance.getWritableDatabase();
				db.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			instance = null;
		}
	}

}
