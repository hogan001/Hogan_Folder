package com.witskies.manager.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.witskies.manager.bean.DownloadApk;
import com.witskies.manager.util.WitskieHttpClient;

public class ApkBroadcast extends BroadcastReceiver {

	@Override
	public void onReceive(final Context context, Intent intent) {
		// TODO Auto-generated method stub
		if (Intent.ACTION_PACKAGE_ADDED.equals(intent.getAction())) {
			

			new Thread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					String urlPath = "http://file.witskies.net/api/app/install/"
							+ DownloadApk.getInstant().getPackageName();
					WitskieHttpClient.getInstance(context).justAccessOneNet(urlPath);
				}
			}).start();
		
//			Log.e("广播", "有应用被添加"+DownloadApk.getInstant().getPackageName());
		} else if (Intent.ACTION_PACKAGE_REMOVED.equals(intent.getAction())) {
		}
		/*
		 * else if(Intent.ACTION_PACKAGE_CHANGED.equals(intent.getAction())){
		 * Toast.makeText(context, "有应用被改变", Toast.LENGTH_LONG).show();}
		 */
		else if (Intent.ACTION_PACKAGE_REPLACED.equals(intent.getAction())) {
		}
		/*
		 * else if(Intent.ACTION_PACKAGE_RESTARTED.equals(intent.getAction())){
		 * // Toast.makeText(context, "有应用被替换", Toast.LENGTH_LONG).show(); }
		 */
//		else if (Intent.ACTION_PACKAGE_INSTALL.equals(intent.getAction())) {
//			//Toast.makeText(context, "有应用被安装", Toast.LENGTH_LONG).show();
//		}

	}
}
