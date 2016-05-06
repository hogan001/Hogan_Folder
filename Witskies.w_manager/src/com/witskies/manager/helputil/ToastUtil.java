package com.witskies.manager.helputil;

import android.content.Context;
import android.widget.Toast;

public class ToastUtil {
	/**
	 * toast工具类
	 * @param context
	 * @param message
	 * @param duration 0 短 1长
	 */
	public static void showToast(Context context, String message, int duration) {
		if (duration == 1) {

			Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
		}
		if (duration == 2) {

			Toast.makeText(context, message, Toast.LENGTH_LONG).show();
		}

	}
}
