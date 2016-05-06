package com.witskies.manager.helputil;

import android.util.Log;

/**
 * 日志打印辅助类
 * 
 * @author Administrator
 * 
 */
public class LogUtils {
	/**
	 * 是否开启打印
	 */
	private final static boolean isDebug = true;
	private static final String TAG = "w_manager";

	public static void i(String tag, String msg) {
		if (isDebug) {

			Log.i(tag, msg);
		}

	}

	public static void e(String tag, String msg) {
		if (isDebug) {

			Log.e(tag, msg);
		}

	}

	public static void v(String tag, String msg) {
		if (isDebug) {

			Log.v(tag, msg);
		}

	}

	public static void d(String tag, String msg) {
		if (isDebug) {

			Log.d(tag, msg);
		}

	}

	// 下面四个是默认tag的函数
	public static void i(String msg) {
		if (isDebug)
			Log.i(TAG, msg);
	}

	public static void d(String msg) {
		if (isDebug)
			Log.d(TAG, msg);
	}

	public static void e(String msg) {
		if (isDebug)
			Log.e(TAG, msg);
	}

	public static void v(String msg) {
		if (isDebug)
			Log.v(TAG, msg);
	}
}
