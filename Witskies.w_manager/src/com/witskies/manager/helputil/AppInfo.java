package com.witskies.manager.helputil;

import android.graphics.drawable.Drawable;

public class AppInfo
{
	// 应用名称
	public String appName = "";
	// 应用包名
	public String packageName = "";
	// 应用版本名称
	public String versionName = "";
	// 应用版本号
	public int versionCode = 0;
	// 应用程序的第一次安装时间
	public long firstInstallTime;

	// 应用程序的最后一次安装时间
	public long lastUpdateTime;

	public Drawable appIcon = null;

	public void print()
	{
	//	Log.v("app", toString());
	}

	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("appName : " + appName + "\n");
		builder.append("packageName : " + packageName + "\n");
		builder.append("versionName : " + versionName + "\n");
		builder.append("firstInstallTime : " + firstInstallTime + "\n");
		builder.append("lastUpdateTime : " + lastUpdateTime + "\n");
		return builder.toString();
	}

}
