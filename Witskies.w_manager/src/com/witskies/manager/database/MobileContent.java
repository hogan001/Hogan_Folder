package com.witskies.manager.database;

import java.io.File;
import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.witskies.manager.bean.AdapterItemBean;
import com.witskies.manager.util.CommentAdapterHelpUtil;

public class MobileContent {
	public static final String TABLE_NAME = "mobile_file";
	public static final String COLUMN_ID = "_id";
	public static final String MOBILE_AUDIOS = "audios";
	public static final String MOBILE_VIDEOS = "videos";
	public static final String MOBILE_DOCUMENTS = "documents";

	private FileServiceOpenHelper dbHelper;
	private SQLiteDatabase db;

	public MobileContent(Context context) {
		dbHelper = FileServiceOpenHelper.getInstance(context);
		db = dbHelper.getWritableDatabase();
	}

	public void insertVideo(String video) {
		ContentValues values = new ContentValues();

		values.put("videos", video);

		db.insert(TABLE_NAME, null, values);

	}

	public void delectVideo(String video) {
		db.delete(TABLE_NAME, "videos=?", new String[] { video });
	}

	public void insertDocument(String document) {
		ContentValues values = new ContentValues();

		values.put("documents", document);

		db.insert(TABLE_NAME, null, values);

	}

	public void delectDocument(String document) {
		db.delete(TABLE_NAME, "documents=?", new String[] { document });
	}

	public void insertAudio(String audio) {
		ContentValues values = new ContentValues();

		values.put("audios", audio);

		db.insert(TABLE_NAME, null, values);

	}

	public void delectAudio(String audio) {
		db.delete(TABLE_NAME, "audios=?", new String[] { audio });
	}

	public ArrayList<AdapterItemBean> select() {
		ArrayList<AdapterItemBean> list = new ArrayList<AdapterItemBean>();

		SQLiteDatabase db = dbHelper.getReadableDatabase();
		if (db.isOpen()) {
			Cursor cursor = db.rawQuery(
					"select * from " + TABLE_NAME /* + " desc" */, null);
			while (cursor.moveToNext()) {
				AdapterItemBean adapterItemBean = new AdapterItemBean();
				String document = cursor.getString(cursor
						.getColumnIndex(MOBILE_DOCUMENTS));
				if (document != null && document.length() > 0) {
					String name = document
							.substring(document.lastIndexOf("/") + 1);
					String data = CommentAdapterHelpUtil.getInstantiation()
							.getDate(document);
					String size = CommentAdapterHelpUtil.getInstantiation()
							.getSize((new File(document)).length());
					if (!size.equals("0.00M")) {
						adapterItemBean.setDocumenCurrPath(document);
						adapterItemBean.setDocumentName(name);
						adapterItemBean.setDocumentSize(size);
						adapterItemBean.setDocumentTime(data);
						list.add(adapterItemBean);
					}
				}
			}
			cursor.close();
		}
		return list;
	}

}
