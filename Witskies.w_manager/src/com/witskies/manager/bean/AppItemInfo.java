package com.witskies.manager.bean;

import android.content.Intent;
import android.graphics.drawable.Drawable;

/**
 * @作者 ch
 * @描述 自定认的一个Bean,用来存储应用程序(app)的相关信息
 * @时间 2015年4月29日 下午2:41:00
 */

public class AppItemInfo {
	private Drawable icon;// 图标
	private String version;// 版本

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	private String appName;// 存放应用程序名
	private String packageName;// 存放应用程序包名
	private Intent intent; // 启动应用程序的Intent

	public Intent getIntent() {
		return intent;
	}

	public void setIntent(Intent intent) {
		this.intent = intent;
	}

	public Drawable getIcon() {
		return icon;
	}

	public void setIcon(Drawable icon) {
		this.icon = icon;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

}