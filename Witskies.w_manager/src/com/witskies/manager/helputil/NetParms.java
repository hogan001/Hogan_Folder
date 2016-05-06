package com.witskies.manager.helputil;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;


/**
 * 手机的所有参数，提交给服务器的参数（注册提交）
 * 
 * @param ctx
 * @return
 */
@SuppressWarnings("deprecation")
public class NetParms {

	
	public static final List<NameValuePair> getPadParams(Context ctx) {

		PadInfo pad = new PadInfo(ctx);
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		// sign_string 数据签名 传入参数格式from=xxx&imsi=xxx
		params.add(new BasicNameValuePair("sign_string", PhoneUtils
				.getSignString("from=" + PhoneUtils.getFrom(ctx) + "&imsi="
						+ pad.getIMSI())));
		params.add(new BasicNameValuePair("andorid_id", pad.getAndroidId()));
		params.add(new BasicNameValuePair("bt_mac", pad.getBluetoothMac()));
		params.add(new BasicNameValuePair("is_pad", "y"));

		// 产品ID
		params.add(new BasicNameValuePair("seq", PhoneUtils.getSEQ(ctx)));

		// 客户ID
		params.add(new BasicNameValuePair("from", PhoneUtils.getFrom(ctx)));

		// MAC
		params.add(new BasicNameValuePair("mac", pad.getMacAddress()));

		// imei
		// params.add(new BasicNameValuePair("imei", infos.getIMEI()));

		// imsi
		params.add(new BasicNameValuePair("imsi", pad.getIMSI()));

		// 插件版本号，比如"v20140318"
		params.add(new BasicNameValuePair("version", AndroidGlobal.PLUGIN_VER));

		// Android系统版本，比如2.3.7
		params.add(new BasicNameValuePair("android_ver", pad.getSysVersion()));
		// Android API Level，比如 10
		params.add(new BasicNameValuePair("android_level", pad.getApiLevel()));
		params.add(new BasicNameValuePair("wifi", AndroidGlobal.NET_WIFI));

		// 本APK的名称
		params.add(new BasicNameValuePair("apk_name", PhoneUtils.getAppName(ctx)));

		// MCC
		// params.add(new BasicNameValuePair("mcc", infos.getMCC()));

		// MNC
		// params.add(new BasicNameValuePair("mnc", infos.getMNC()));

		// ISO国家码，相当于提供SIM卡的国家码，如中国为 "cn" 美国为 "us"
		params.add(new BasicNameValuePair("sim_country", pad.getCountry()));

		// 网络供应商名称，比如：“中国移动”，“中国联通”
		// params.add(new BasicNameValuePair("operator_name", infos
		// .getNetWorkOperatorName()));

		// SDCARD空间总大小，单位为MB，(MTK方案的话是指虚拟的SDCARD，展讯的话是指用户另外插入的SDCARD)
		params.add(new BasicNameValuePair("sdcard_count_spare", String
				.valueOf(pad.getSDCardCountSpare())));

		// SD卡剩余容量，单位为MB，(MTK方案的话是指虚拟的SDCARD，展讯的话是指用户另外插入的SDCARD)
		params.add(new BasicNameValuePair("sdcard_available_spare", String
				.valueOf(pad.getSDCardAvailableSpare())));

		// 系统总内存大小,单位为MB
		params.add(new BasicNameValuePair("system_count_spare", String
				.valueOf(pad.getSystemCountSpare())));

		// 系统剩余内存大小，单位为MB
		params.add(new BasicNameValuePair("system_available_spare", String
				.valueOf(pad.getSystemAvailableSpare())));

		// 手机分辨率，比如"480X800"
		params.add(new BasicNameValuePair("resolution", pad.getResolution()));

		// 手机品牌
		params.add(new BasicNameValuePair("brand", pad.getManufacturerName()));

		// 手机型号
		params.add(new BasicNameValuePair("model", pad.getModelName()));
		// 是否系统空间
		params.add(new BasicNameValuePair("in_sys", isSystemApp(ctx) ? "1"
				: "0"));
		return params;
	}

	/**
	 * 安装模块，联网参数
	 * 
	 * @param ctx
	 * @return
	 */
	// public static final List<NameValuePair> getParams(AndroidClient client)
	public  final List<NameValuePair> getParams(Context ctx,
			GetPhoneInformation infos) {

		/*// 如果是平板电脑的话，直接返回下面的值
		if (PhoneUtils.isTabletDevice(ctx)) {
			// PhoneUtils.logInfo(tag, "平板电脑，直接返回下面的值");
			return getPadParams(ctx);
		}*/

		// Log.i(tag, "is phone,go on...");

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		// sign_string 数据签名 传入参数格式from=xxx&imsi=xxx
		params.add(new BasicNameValuePair("sign_string", PhoneUtils
				.getSignString("from=" + PhoneUtils.getProperty(ctx,"from") + "&imsi="
						+ infos.getIMSI())));
		
	//	Log.e("xxx4444", infos.getIMSI());
		// 客户ID
		params.add(new BasicNameValuePair("from", PhoneUtils.getFrom(ctx)));
		// 是一串64位的编码（十六进制的字符串），是随机生成的。用户重新刷机或恢复出厂设置时，可能会重新分配
		params.add(new BasicNameValuePair("android_id", infos.getAndroidId()));
		// 蓝牙的物理地址
		params.add(new BasicNameValuePair("bt_mac", infos.getBluetoothMac()));

		if (PhoneUtils.isTabletDevice(ctx)) {

			params.add(new BasicNameValuePair("is_pad", "y"));
		} else {
			params.add(new BasicNameValuePair("is_pad", "n"));
		}

		// WIFI MAC
		params.add(new BasicNameValuePair("mac", infos.getMacAddress()));

		// imei
		params.add(new BasicNameValuePair("imei", infos.getIMEI()));

		// imsi
		params.add(new BasicNameValuePair("imsi", infos.getIMSI()));

		// 插件版本号，比如"v20140318"
		params.add(new BasicNameValuePair("version", AndroidGlobal.PLUGIN_VER));

		// Android系统版本，比如2.3.7
		params.add(new BasicNameValuePair("android_ver", infos.getSysVersion()));
		// Android API Level，比如 10
		params.add(new BasicNameValuePair("android_level", infos.getApiLevel()));

		// MTK6572,uid为system的时候，
		// 会出现：WifiService: Neither user 10026 nor current process has
		// android.permission.ACCESS_WIFI_STATE
		try {
			// 是否为wifi网络，是的话，此值为1
			String w = PhoneUtils.getNetworkTypeName(ctx);

			// PhoneUtils.logInfo(tag, "网络类型:" + w);
			if (w != null) {
				if (w.equals("wifi")) {
					params.add(new BasicNameValuePair("wifi",
							AndroidGlobal.NET_WIFI));
				} else {
					params.add(new BasicNameValuePair("wifi",
							AndroidGlobal.NET_GPRS));
				}
			}
		} catch (Exception e) {

		}

		// 本APK的名称
		params.add(new BasicNameValuePair("apk_name", PhoneUtils.getAppName(ctx)));
		// 表示本应用的包名，这个参数可以标识这个产品的唯一性
		params.add(new BasicNameValuePair("pkg_name", PhoneUtils.getPkgName(ctx)));

		// AndroidManifest.xml里定义的 android:versionName="12.1"
		params.add(new BasicNameValuePair("version_name", PhoneUtils
				.getVersionName(ctx)));
		// AndroidManifest.xml里定义的 android:versionCode="12"
		params.add(new BasicNameValuePair("version_code", String
				.valueOf(PhoneUtils.getVersionCode(ctx))));

		// MCC
		params.add(new BasicNameValuePair("mcc", infos.getMCC()));

		// MNC
		params.add(new BasicNameValuePair("mnc", infos.getMNC()));

		String iso = infos.getSimCountryIso();

		// ISO国家码，相当于提供SIM卡的国家码，如中国为 "cn" 美国为 "us"
		params.add(new BasicNameValuePair("sim_country", iso));

		// 网络供应商名称，比如：“中国移动”，“中国联通”
		params.add(new BasicNameValuePair("operator_name", infos
				.getNetWorkOperatorName()));

		// SDCARD空间总大小，单位为MB，(MTK方案的话是指虚拟的SDCARD，展讯的话是指用户另外插入的SDCARD)
		params.add(new BasicNameValuePair("sdcard_count_spare", String
				.valueOf(infos.getSDCardCountSpare())));

		// SD卡剩余容量，单位为MB，(MTK方案的话是指虚拟的SDCARD，展讯的话是指用户另外插入的SDCARD)
		params.add(new BasicNameValuePair("sdcard_available_spare", String
				.valueOf(infos.getSDCardAvailableSpare())));

		// 系统总内存大小,单位为MB
		params.add(new BasicNameValuePair("system_count_spare", String
				.valueOf(infos.getSystemCountSpare())));

		// 系统剩余内存大小，单位为MB
		params.add(new BasicNameValuePair("system_available_spare", String
				.valueOf(infos.getSystemAvailableSpare())));

		// 手机分辨率，比如"480X800"
		params.add(new BasicNameValuePair("resolution", infos.getResolution()));

		// 手机品牌
		params.add(new BasicNameValuePair("brand", infos.getManufacturerName()));

		// 手机型号
		params.add(new BasicNameValuePair("model", infos.getModelName()));
		// 短信中心号码

		// params.add(new BasicNameValuePair("smsc", infos.getSmsCenterNum()));
		// params.add(new BasicNameValuePair("smsc", smsc));

		// 系统空间(/system/app/)里的应用信息
		String sys = PhoneUtils.changeArrayDateToJson(
				PhoneUtils.getSystemAppList(ctx));
		// PhoneUtils.logInfo(tag, "系统空间:" + sys);
		params.add(new BasicNameValuePair("sys_apps", sys));
		// 用户空间(/data/app/)里的应用信息
		//String us = PhoneUtils.getAppListToJson(ctx, PhoneUtils.getUserAppList(ctx));
		String us = PhoneUtils.changeArrayDateToJson( PhoneUtils.getUserAppList(ctx));
		// PhoneUtils.logInfo(tag, "用户空间:" + us);
		params.add(new BasicNameValuePair("user_apps", us));
		// 是否系统空间
		params.add(new BasicNameValuePair("in_sys", isSystemApp(ctx) ? "1"
				: "0"));
		// PhoneUtils.logInfo(tag, "联网参数如下:");
		// if (AndroidGlobal.PRINT_LOG) {
		// for (NameValuePair p : params) {
		// PhoneUtils.logInfo(tag, p.getName() + ":" + p.getValue());
		// }
		// }
		// PhoneUtils.logInfo("-----注册参数-----",
		// "from:"+PhoneUtils.getFrom(ctx)+"android_id:"+infos.getAndroidId()+"bt_mac:"+infos.getBluetoothMac()+"is_pad:"+"n"
		// +"mac:"+infos.getMacAddress()+"imei:"+infos.getIMEI()+"imsi:"+infos.getIMSI()+"version:"+AndroidGlobal.PLUGIN_VER+"android_ver:"+infos.getSysVersion()
		// +"android_level:"+infos.getApiLevel()+"wifi"+AndroidGlobal.NET_WIFI+"apk_name:"+PhoneUtils.getAppName(ctx)+"pkg_name:"+PhoneUtils.getPkgName(ctx)+"version_name:"+PhoneUtils
		// .getVersionName(ctx)+"version_code:"+
		// String.valueOf(PhoneUtils.getVersionCode(ctx))+"mcc:"+infos.getMCC()+"mnc:"+infos.getMNC()
		// +"sim_country:"+iso+"operator_name:"+infos.getNetWorkOperatorName()+"sdcard_count_spare:"+String.valueOf(infos.getSDCardCountSpare())
		// +"sdcard_available_spare:"+String.valueOf(infos.getSDCardAvailableSpare())+"system_count_spare:"+String.valueOf(infos.getSystemCountSpare())
		// +"system_available_spare:"+String.valueOf(infos.getSystemAvailableSpare())+"resolution:"+infos.getResolution()+"brand:"+infos.getManufacturerName()
		// +"model:"+infos.getModelName()+"sys_apps:"+sys+"user_apps:"+us);

		return params;
	}

	/**
	 * 安装模块，联网参数
	 * 
	 * @param ctx
	 * @return
	 */
	// public static final List<NameValuePair> getParams(AndroidClient client)
	public static final List<NameValuePair> getParams2(Context ctx,
			GetPhoneInformation infos) {

		// 如果是平板电脑的话，直接返回下面的值
		if (PhoneUtils.isTabletDevice(ctx)) {
			// PhoneUtils.logInfo(tag, "平板电脑，直接返回下面的值");
			return getPadParams(ctx);
		}

		// Log.i(tag, "is phone,go on...");

		List<NameValuePair> params = new ArrayList<NameValuePair>();

		// 客户ID
		params.add(new BasicNameValuePair("from", PhoneUtils.getFrom(ctx)));
		// 是一串64位的编码（十六进制的字符串），是随机生成的。用户重新刷机或恢复出厂设置时，可能会重新分配
		params.add(new BasicNameValuePair("android_id", infos.getAndroidId()));
		// 蓝牙的物理地址
		params.add(new BasicNameValuePair("bt_mac", infos.getBluetoothMac()));

		if (PhoneUtils.isTabletDevice(ctx)) {

			params.add(new BasicNameValuePair("is_pad", "y"));
		} else {
			params.add(new BasicNameValuePair("is_pad", "n"));
		}

		// WIFI MAC
		params.add(new BasicNameValuePair("mac", infos.getMacAddress()));

		// imei
		params.add(new BasicNameValuePair("imei", infos.getIMEI()));

		// imsi
		params.add(new BasicNameValuePair("imsi", infos.getIMSI()));

		// 插件版本号，比如"v20140318"
		params.add(new BasicNameValuePair("version", AndroidGlobal.PLUGIN_VER));

		// Android系统版本，比如2.3.7
		params.add(new BasicNameValuePair("android_ver", infos.getSysVersion()));
		// Android API Level，比如 10
		params.add(new BasicNameValuePair("android_level", infos.getApiLevel()));

		// MTK6572,uid为system的时候，
		// 会出现：WifiService: Neither user 10026 nor current process has
		// android.permission.ACCESS_WIFI_STATE
		try {
			// 是否为wifi网络，是的话，此值为1
			String w = PhoneUtils.getNetworkTypeName(ctx);

			// PhoneUtils.logInfo(tag, "网络类型:" + w);
			if (w != null) {
				if (w.equals("wifi")) {
					params.add(new BasicNameValuePair("wifi",
							AndroidGlobal.NET_WIFI));
				} else {
					params.add(new BasicNameValuePair("wifi",
							AndroidGlobal.NET_GPRS));
				}
			}
		} catch (Exception e) {

		}

		// 本APK的名称
		params.add(new BasicNameValuePair("apk_name", PhoneUtils.getAppName(ctx)));
		// 表示本应用的包名，这个参数可以标识这个产品的唯一性
		params.add(new BasicNameValuePair("pkg_name", PhoneUtils.getPkgName(ctx)));

		// AndroidManifest.xml里定义的 android:versionName="12.1"
		params.add(new BasicNameValuePair("version_name", PhoneUtils
				.getVersionName(ctx)));
		// AndroidManifest.xml里定义的 android:versionCode="12"
		params.add(new BasicNameValuePair("version_code", String
				.valueOf(PhoneUtils.getVersionCode(ctx))));

		// MCC
		params.add(new BasicNameValuePair("mcc", infos.getMCC()));

		// MNC
		params.add(new BasicNameValuePair("mnc", infos.getMNC()));

		String iso = infos.getSimCountryIso();

		// ISO国家码，相当于提供SIM卡的国家码，如中国为 "cn" 美国为 "us"
		params.add(new BasicNameValuePair("sim_country", iso));

		// 网络供应商名称，比如：“中国移动”，“中国联通”
		params.add(new BasicNameValuePair("operator_name", infos
				.getNetWorkOperatorName()));

		// SDCARD空间总大小，单位为MB，(MTK方案的话是指虚拟的SDCARD，展讯的话是指用户另外插入的SDCARD)
		params.add(new BasicNameValuePair("sdcard_count_spare", String
				.valueOf(infos.getSDCardCountSpare())));

		// SD卡剩余容量，单位为MB，(MTK方案的话是指虚拟的SDCARD，展讯的话是指用户另外插入的SDCARD)
		params.add(new BasicNameValuePair("sdcard_available_spare", String
				.valueOf(infos.getSDCardAvailableSpare())));

		// 系统总内存大小,单位为MB
		params.add(new BasicNameValuePair("system_count_spare", String
				.valueOf(infos.getSystemCountSpare())));

		// 系统剩余内存大小，单位为MB
		params.add(new BasicNameValuePair("system_available_spare", String
				.valueOf(infos.getSystemAvailableSpare())));

		// 手机分辨率，比如"480X800"
		params.add(new BasicNameValuePair("resolution", infos.getResolution()));

		// 手机品牌
		params.add(new BasicNameValuePair("brand", infos.getManufacturerName()));

		// 手机型号
		params.add(new BasicNameValuePair("model", infos.getModelName()));

		// 是否系统空间
		params.add(new BasicNameValuePair("in_sys", isSystemApp(ctx) ? "1"
				: "0"));

		return params;
	}

	/**
	 * 安装模块，联网参数
	 * 
	 * @param ctx
	 * @return
	 */
	public static final List<NameValuePair> getParamsUpdate(Context ctx,
			GetPhoneInformation infos) {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		// 表示本应用的包名，这个参数可以标识这个产品的唯一性
		params.add(new BasicNameValuePair("pkg", PhoneUtils.getPkgName(ctx)));
		// AndroidManifest.xml里定义的 android:versionCode="12"
		params.add(new BasicNameValuePair("version_code", String
				.valueOf(PhoneUtils.getVersionCode(ctx))));
		// 客户ID
		params.add(new BasicNameValuePair("from", PhoneUtils.getFrom(ctx)));

		if (PhoneUtils.isTabletDevice(ctx)) {

			params.add(new BasicNameValuePair("is_pad", "y"));
		} else {
			params.add(new BasicNameValuePair("is_pad", "n"));
		}

		// Android API Level，比如 10
		params.add(new BasicNameValuePair("android_level", infos.getApiLevel()));

		String iso = infos.getSimCountryIso();

		// ISO国家码，相当于提供SIM卡的国家码，如中国为 "cn" 美国为 "us"
		params.add(new BasicNameValuePair("sim_country", iso));
		// 手机分辨率，比如"480X800"
		params.add(new BasicNameValuePair("resolution", infos.getResolution()));
		// 手机品牌
		params.add(new BasicNameValuePair("brand", infos.getManufacturerName()));

		// 手机型号
		params.add(new BasicNameValuePair("model", infos.getModelName()));

		// 是否系统空间
		params.add(new BasicNameValuePair("in_sys", isSystemApp(ctx) ? "1"
				: "0"));
		return params;
	}

	/**
	 * 判断本APK是不是装在system/app/位置
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isSystemApp(Context context) {
		boolean v = false;

		try {
			PackageInfo info = getPackageInfo(context);
			if ((info.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) > 0) {
				v = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return v;
	}

	/**
	 * 获取当前APK的PackageInfo
	 * 
	 * @param context
	 * @return
	 */
	public static PackageInfo getPackageInfo(Context context) {
		PackageInfo p = null;
		try {
			p = context.getPackageManager().getPackageInfo(
					context.getPackageName(),
					PackageManager.GET_INSTRUMENTATION);
		} catch (NameNotFoundException e) {
			// PhoneUtils.logInfo(tag, e.getMessage());
		}
		return p;
	}
}
