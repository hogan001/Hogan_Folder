package com.witskies.manager.multithread;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
	public DatabaseHelper(Context context, String name, CursorFactory factory, int version) {
		super(context, "downloadFile.db", null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Cursor cursor = null;
		try {
			cursor = db.rawQuery("SELECT * FROM sqlite_master WHERE type = ? AND name = ?",
					new String[] { "table", "info" });
			if (cursor.moveToNext()) {
			} else {
				db.execSQL("CREATE TABLE info (path VARCHAR(1024), threadid INTEGER , "
						+ "downloadlength INTEGER , PRIMARY KEY(path,threadid))");
			}
			cursor.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		/*
		 * db.execSQL("CREATE TABLE info (path VARCHAR(1024), threadid INTEGER , "
		 * + "downloadlength INTEGER , PRIMARY KEY(path,threadid))");
		 */
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

}
