package com.witskies.manager.image;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio;
import android.provider.MediaStore.Files;
import android.provider.MediaStore.Files.FileColumns;
import android.provider.MediaStore.Images;
import android.provider.MediaStore.Video;
import android.util.Log;

/**
 * 3.0以上的API用这个方法搜索doc,zip,Theme,Apk,Music,Picture等
 * 
 * @author Clance
 * 
 */
public class MediaUtils {
	private final static String TAG = "MediaUtils";
	private static MediaUtils mMediaUtils;

	/**
	 * 查询文件类型的枚举
	 * 
	 * @author Clance
	 * 
	 */
	public enum MediaCategory {
		All, Music, Video, Picture, Theme, Doc, Zip, Apk, Custom, Other
	}

	/**
	 * doc文档的支持类型
	 */
	@SuppressWarnings("serial")
	private static HashSet<String> mDocMimeTypesSet = new HashSet<String>() {
		{
			add("text/plain");// text
			add("text/html");// html
			add("text/xml");// xml
			add("application/pdf");// pdf
			add("application/msword");// doc
			add("application/vnd.ms-excel");// xls
			add("application/rtf");// rtf
			add("application/vnd.ms-powerpoint");// ppt
			add("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");// xlsx
			add("application/vnd.openxmlformats-officedocument.wordprocessingml.document");//docx
			add("application/vnd.openxmlformats-officedocument.presentationml.presentation");// pptx
		}
	};

	public static MediaUtils getInstance() {
		if (mMediaUtils == null) {
			synchronized (MediaUtils.class) {
				if (mMediaUtils == null) {
					mMediaUtils = new MediaUtils();
				}
			}

		}
		return mMediaUtils;
	}

	/**
	 * 获取SD卡上文件的路径(数目)
	 * 
	 * @param context
	 * @return
	 */
	@SuppressLint({ "NewApi" })
	public List<String> getPaths(Context context, MediaCategory category) {
		List<String> paths = new ArrayList<String>();
		try {
			Uri uri = getContentUriByCategory(category);
			String[] columns = new String[] { FileColumns._ID, FileColumns.DATA, FileColumns.SIZE,
					FileColumns.DATE_MODIFIED, FileColumns.TITLE };
			String selection = getSelectionByCategory(category);
			Cursor c = context.getContentResolver().query(uri, columns, selection, null, null);
			if (c == null) {
				return null;
			}

			if (c.moveToFirst()) {
				int pathIndex = c.getColumnIndex(MediaStore.Files.FileColumns.DATA);
				do {
					String path = c.getString(pathIndex); // 获取文件实际路径
					File file = new File(path);
					if (file.length() > 0) {
						//
						paths.add(path);
					}
					// if (!size.equals("0.00B")) {
					//
					// paths.add(path);
					// }

				} while (c.moveToNext()); // 循环获取文件
			}
		} catch (Exception e) {
		}
		return paths;

	}

	@SuppressLint("NewApi")
	private Uri getContentUriByCategory(MediaCategory category) {
		Uri uri;
		// 外部存储
		String volumeName = "external";
		switch (category) {
		case Theme:
			uri = Files.getContentUri(volumeName);
		case Doc:
			uri = Files.getContentUri(volumeName);
		case Zip:
			uri = Files.getContentUri(volumeName);
		case Apk:
			uri = Files.getContentUri(volumeName);
			break;
		case Music:
			uri = Audio.Media.getContentUri(volumeName);
			break;
		case Video:
			uri = Video.Media.getContentUri(volumeName);
			break;
		case Picture:
			uri = Images.Media.getContentUri(volumeName);
			break;
		default:
			uri = null;
		}
		return uri;
	}

	@SuppressLint({ "NewApi" })
	private String getSelectionByCategory(MediaCategory category) {
		String selection = null;
		switch (category) {
		case Theme:
			selection = FileColumns.DATA + " LIKE '%.mtz'";
			break;
		case Doc:
			selection = buildDocSelection();
			break;
		case Zip:
			selection = "(" + FileColumns.MIME_TYPE + " == '" + "application/zip" + "')";
			break;
		case Apk:
			selection = FileColumns.DATA + " LIKE '%.apk'";
			break;
		default:
			selection = null;
		}
		return selection;
	}

	/**
	 * doc过滤器
	 * 
	 * @return
	 */
	@SuppressLint({ "NewApi" })
	private String buildDocSelection() {
		StringBuilder selection = new StringBuilder();
		Iterator<String> iter = mDocMimeTypesSet.iterator();
		while (iter.hasNext()) {
			selection.append("(" + FileColumns.MIME_TYPE + "=='" + iter.next() + "') OR ");
		}
		return selection.substring(0, selection.lastIndexOf(")") + 1);
	}

	/*
	 * 删除文件后通知媒体库更新
	 */
	public void updateGallery(List<String> paths, Context context) {
		if (paths == null) {
			return;
		}
		// 4.4
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			String[] scanPaths = new String[paths.size()];
			for (int i = 0; i < scanPaths.length; i++) {
				scanPaths[i] = new String();
				scanPaths[i] = paths.get(i);
			}
			MediaScannerConnection.scanFile(context, scanPaths, null,
					new MediaScannerConnection.OnScanCompletedListener() {
						public void onScanCompleted(String path, Uri uri) {
							Log.d(TAG, "ok4.4");
						}
					});
		} else {
			Log.d(TAG, "ok4.4以下");
			context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://"
					+ Environment.getExternalStorageDirectory().toString())));
		}

	}
}
