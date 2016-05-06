package com.witskies.manager.multithread;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DatabaseUtil {
	private DatabaseHelper helper;
	public static final String lock = "访问";

	public DatabaseUtil(Context context) {
		helper = new DatabaseHelper(context, "downloadFile.db", null, 1);
	}

	// 添加记录
	public void insert(String path, int threadid, int downloadlength) {
		SQLiteDatabase db = helper.getWritableDatabase();
		db.execSQL("INSERT INTO info(path,threadid,downloadlength) VALUES (?,?,?)", new Object[] {
				path, threadid, downloadlength });
		print("成功添加");
		db.close();
	}

	// 删除记录
	public void delete(String path, int threadid) {
		SQLiteDatabase db = helper.getWritableDatabase();
		db.execSQL("DELETE FROM info WHERE path = ? AND threadid = ?", new Object[] { path,
				threadid });
		print("成功删除");
		db.close();
	}

	// 更新记录
	public void update(String path, int threadid, int downloadlength) {
		SQLiteDatabase db = helper.getWritableDatabase();
		// print("准备更新1");
		db.execSQL("UPDATE info SET downloadlength = ? WHERE path = ? AND threadid = ?",
				new Object[] { downloadlength, path, threadid });
		// print("成功更新2");
		db.close();
	}

	// 查询线程是否存在
	public ItemRecord query(String path, int threadid) {
		SQLiteDatabase db = helper.getWritableDatabase();
		Cursor c = db.rawQuery(
				"SELECT path,threadid,downloadlength FROM info WHERE path = ? AND threadid = ?",
				new String[] { path, Integer.toString(threadid) });
		ItemRecord record = null;
		if (c.moveToNext()) {
			record = new ItemRecord(c.getString(0), c.getInt(1), c.getInt(2));
		}
		c.close();
		db.close();
		return record;
	}

	// 查询未完成任务
	public List<String> query(String path) {
		print("List<String> query 开始");
		SQLiteDatabase db = helper.getWritableDatabase();
		Cursor c = db.rawQuery("SELECT DISTINCT path FROM info", new String[] { path });
		List<String> arrayList = new ArrayList<String>();
		while (c.moveToNext()) {
			arrayList.add(c.getString(0));
		}
		c.close();
		db.close();
		print("List<String> query 结束");
		return arrayList;
	}

	// public void deleteDB(String dbName){
	// helper.d
	// }
	// 调试信息输出
	private void print(String msg) {
		Log.d("DatabaseUtil", msg);
	}
}
