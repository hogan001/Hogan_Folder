package com.witskies.manager.bean;

import android.graphics.drawable.Drawable;

/**
 * @作者 ch
 * @描述 下载的apk类的相关信息
 * @时间 2015-4-2 下午3:51:43
 */
public class DownloadApk {
	private String apkName;
	private String packageName;
	private String url_apk;
	private String serviceName;

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getUrl_apk() {
		return url_apk;
	}

	public void setUrl_apk(String url_apk) {
		this.url_apk = url_apk;
	}

	private Drawable icon;
	private static DownloadApk loadApk;

	public static DownloadApk getInstant() {
		if (loadApk == null) {
			loadApk = new DownloadApk();
		}
		return loadApk;
	}

	public String getApkName() {
		return apkName;
	}

	public void setApkName(String apkName) {
		this.apkName = apkName;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public Drawable getIcon() {
		return icon;
	}

	public void setIcon(Drawable icon) {
		this.icon = icon;
	}
}
