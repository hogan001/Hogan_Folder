package com.witskies.manager.util;

import java.util.Timer;
import java.util.TimerTask;

import com.witskies.manager.app.WitskiesApplication;
import com.witskies.w_manager.R;

import android.content.Context;
import android.widget.Toast;
/**
 * @作者 ch
 * @描述   连按二次退出程序
 * @时间 2015年5月19日 上午9:34:03
 */
public class ExitApk {
	private static Boolean isExit = false;

	public static void extiApplication(Context mContext) {

		Timer tExit = null;
		if (isExit == false) {
			isExit = true; // 准备退出
			Toast.makeText(mContext, mContext.getString(R.string.w_apk_exit), Toast.LENGTH_SHORT).show();
			tExit = new Timer();
			tExit.schedule(new TimerTask() {
				@Override
				public void run() {
					isExit = false; // 取消退出
				}
			}, 2000);

		} else {
			
			WitskiesApplication.getInstantiation().exit(mContext);
		}

	}

}
