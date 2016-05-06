package com.witskies.manager.helputil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import com.witskies.manager.bean.AppItemInfo;

/**
 * @作者 ch
 * @描述 获取手机所有App信息的工具类（最好就找用户装的）
 * @时间 2015年4月29日 下午2:43:38
 */
public class FindAppUtil {


	private Context mcontext;
	public static List<PackageInfo> systemAndUserApps = new ArrayList<PackageInfo>();// 获取手机内所有应用
	public static List<PackageInfo> UserApps = new ArrayList<PackageInfo>();// 存储用户安装的程序
	private PackageManager pManager;
	public  ArrayList<String> packageNames = new ArrayList<String>();
	public static ArrayList<Map<String, AppItemInfo>> pcknameAndInfo = new ArrayList<Map<String, AppItemInfo>>();
	public static ArrayList<AppItemInfo> UserAppItemInfos;
	public static Map<String, AppItemInfo> pcknameAndInfos;
	


	public FindAppUtil(Context context) {

		this.mcontext = context;
		packageNames.removeAll(packageNames);
		systemAndUserApps.removeAll(systemAndUserApps);
		UserApps.removeAll(UserApps);
		pcknameAndInfo.removeAll(pcknameAndInfo);

		pManager = mcontext.getPackageManager();

		getSystemAndUserApps();
		getUserAppIemInfo();

	}

	/** 获取手机内所有应用,并添加用户 */
	@SuppressWarnings("static-access")
	public void getSystemAndUserApps() {

		systemAndUserApps = pManager.getInstalledPackages(0);
		for (int i = 0; i < systemAndUserApps.size(); i++) {
			PackageInfo pak = systemAndUserApps.get(i);
			// 判断是否为系统预装程序，然后添加用户安装的程序，为0返回用户自己安装列表
			if ((pak.applicationInfo.flags & pak.applicationInfo.FLAG_SYSTEM) == 0) {
				UserApps.add(pak);
			}
		}
	}


	/** 获取手机（用户装的软件）所有应用的相关信息，并添加到类里面 */

	public ArrayList<AppItemInfo> getUserAppIemInfo() {

		UserAppItemInfos = new ArrayList<AppItemInfo>();
		pcknameAndInfos = new HashMap<String, AppItemInfo>();

		for (int i = 0; i < UserApps.size(); i++) {
			PackageInfo pinfo = UserApps.get(i);
			AppItemInfo appItemInfo = new AppItemInfo();
			
			Drawable iconDrawable = pManager
					.getApplicationIcon(pinfo.applicationInfo);

			if (iconDrawable != null) {
				appItemInfo.setIcon(iconDrawable);
			}
			appItemInfo.setVersion(pinfo.versionName);

			appItemInfo.setAppName(pManager.getApplicationLabel(
					pinfo.applicationInfo).toString());

			appItemInfo.setPackageName(pinfo.applicationInfo.packageName);
			packageNames.add(pinfo.applicationInfo.packageName);
			UserAppItemInfos.add(appItemInfo);
			pcknameAndInfos.put(pinfo.applicationInfo.packageName, appItemInfo);

		}
		pcknameAndInfo.add(pcknameAndInfos);
		return UserAppItemInfos;

	}

}
