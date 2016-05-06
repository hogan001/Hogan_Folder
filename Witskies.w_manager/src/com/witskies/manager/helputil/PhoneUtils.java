package com.witskies.manager.helputil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.TreeMap;
import java.util.UUID;

import org.apache.http.NameValuePair;
import org.apache.http.util.EncodingUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.util.Log;

@SuppressWarnings("deprecation")
public class PhoneUtils {
	private static final String tag = PhoneUtils.class.getSimpleName();
	private static final String SIGN_KEY = "226f3ed5caa1040d1e6a2f3e55ce4e5c";

	public static void logInfo(String tag, String log) {
		

	}

	/**
	 * 获得注册时的sign_string
	 * 
	 * @param param
	 *            传入参数格式from=xxx&imsi=xxx
	 * @return sign_string
	 */
	public static String getSignString(String param) {
		if (param == null)
			return null;
		String[] params = param.split("&");
		TreeMap<String, Object> map = new TreeMap<String, Object>();
		for (String s : params) {
			String _t[] = s.split("=");
			if (_t.length == 2) {
				map.put(_t[0], _t[1]);
			} else {
				map.put(_t[0], "");
			}

		}
		return getSignString(map);

	}

	public static String getSignString(Map<String, Object> param) {
		if (param == null)
			return null;
		Iterator<Entry<String, Object>> iter = param.entrySet().iterator();
		StringBuffer sb = new StringBuffer(100);
		while (iter.hasNext()) {
			Entry<String, Object> entry = iter.next();
			sb.append("&").append(entry.getKey()).append("=")
					.append(entry.getValue());
		}
		String signStr = sb.substring(1) + SIGN_KEY;
		return getMD5(signStr.getBytes());
	}

	public static String getMD5(byte[] source) {
		String s = null;
		char hexDigits[] = { // 用来将字节转换成 16 进制表示的字符
		'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd',
				'e', 'f' };
		try {
			java.security.MessageDigest md = java.security.MessageDigest
					.getInstance("MD5");
			md.update(source);
			byte tmp[] = md.digest(); // MD5 的计算结果是一个 128 位的长整数，
			// 用字节表示就是 16 个字节
			char str[] = new char[16 * 2]; // 每个字节用 16 进制表示的话，使用两个字符，
			// 所以表示成 16 进制需要 32 个字符
			int k = 0; // 表示转换结果中对应的字符位置
			for (int i = 0; i < 16; i++) { // 从第一个字节开始，对 MD5 的每一个字节
				// 转换成 16 进制字符的转换
				byte byte0 = tmp[i]; // 取第 i 个字节
				str[k++] = hexDigits[byte0 >>> 4 & 0xf]; // 取字节中高 4 位的数字转换,
				// >>> 为逻辑右移，将符号位一起右移
				str[k++] = hexDigits[byte0 & 0xf]; // 取字节中低 4 位的数字转换
			}
			s = new String(str); // 换后的结果转换为字符串

		} catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}

	// public static void Toast(Context c, String text)
	// {
	// Toast.makeText(c, text, Toast.LENGTH_LONG);
	// }

	/**
	 * 判断是否具备某项权限
	 * 
	 * @param ctx
	 *            Context
	 * @param p
	 *            比如Manifest.permission.READ_PHONE_STATE
	 * @return
	 */
	public static boolean hasPermission(Context ctx, String p) {

		if (ctx == null) {
			return false;
		} else if (PackageManager.PERMISSION_GRANTED == ctx.getPackageManager()
				.checkPermission(p, ctx.getPackageName())) {
			return true;
		} else {
			return false;
		}

	}

	/**
	 * 功能：获取当前网络类型
	 * <p>
	 * 
	 * 需要权限:<br>
	 * android.Manifest.permission#ACCESS_NETWORK_STATE
	 * 
	 * @param ctx
	 *            Context
	 * @return "mobile" 表示移动网络 "wifi"表示wifi网络 "" 表示网络不可用
	 * 
	 */
	public static String getNetworkTypeName(Context ctx) {

		// NetworkInfo networkInfo = (NetworkInfo) intent
		// .getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
		// if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI)
		// type = "wifi";
		// else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE)
		// type = "3g";
		// else
		// type = "other";

		if (ctx == null) {
			return "";
		}

		try {
			ConnectivityManager conManager = (ConnectivityManager) ctx
					.getSystemService(Context.CONNECTIVITY_SERVICE);

			NetworkInfo networkInfo = null;

			if (hasPermission(ctx, Manifest.permission.ACCESS_NETWORK_STATE)) {
				networkInfo = conManager.getActiveNetworkInfo();
			}

			if (networkInfo != null)// 注意，这个判断一定要的，要不然会出错
			{
				return networkInfo.getTypeName().toLowerCase();
			}
		} catch (Exception e) {
			
			return "";
		}

		return "";
	}

	/**
	 * 判断是否有可用的网络<br>
	 * 需要权限:android.permission.ACCESS_NETWORK_STATE
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isAvailableNetwork(Context context) {
		boolean v = false;
		ConnectivityManager manager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = manager.getActiveNetworkInfo();
		if (networkInfo != null) {
			v = networkInfo.isAvailable();
		}

		return v;

	}

	/**
	 * 功能：获取sd卡路径<br>
	 * 如果是MTK平台的话，获取到的是内部虚拟的SDCARD，即sdcard0
	 * 
	 * @return sd卡路径
	 */
	public static String getExternalStoragePath() {

		boolean sdCardExist = Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED); // 判断sd卡是否存在

		if (sdCardExist) {
			return android.os.Environment.getExternalStorageDirectory()
					.getPath();
		}

		// if (sdCardExist)
		// {
		// sdDir = Environment.getExternalStorageDirectory();// 获取跟目录
		// }
		// return sdDir.toString();

		return null;
	}

	/**
	 * 获取今天的日期，如"2013-08-09"
	 * 
	 * @return 如"2013-08-09"
	 */
	public static final String getTodayYMD() {
		Date date = new Date(System.currentTimeMillis());
		Calendar c1 = Calendar.getInstance();
		c1.setTime(date);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String vv = format.format(c1.getTime());
		return vv;

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
			logInfo(tag, e.getMessage());
		}
		return p;
	}

	/**
	 * 表示本身这个apk应用的版本，字符串，这是给用户看的，用户在应用管理器里看到的应用版本，即系AndroidManifest.xml里定义的
	 * android:versionName="12.1"
	 * 
	 * @param ctx
	 * @return
	 */
	public static final String getVersionName(Context ctx) {
		String values = "unknown";

		if (ctx != null) {
			String pkName = getPkgName(ctx);
			try {
				values = ctx.getPackageManager().getPackageInfo(pkName, 0).versionName;
			} catch (NameNotFoundException e)

			{
				logInfo(tag, e.getMessage());
			}
		}

		return values;
	}

	/**
	 * 表示应用实际的版号，整数，这个是给开发者使用的，在检测升级时用到，即系AndroidManifest.xml里定义的
	 * android:versionCode="12"
	 * 
	 * @param ctx
	 * @return
	 */
	public static final int getVersionCode(Context ctx) {
		int v = 0;

		if (ctx != null) {
			String pkName = getPkgName(ctx);
			try {
				v = ctx.getPackageManager().getPackageInfo(pkName, 0).versionCode;
			} catch (NameNotFoundException e)

			{
				logInfo(tag, e.getMessage());
			}
		}

		return v;
	}

	/**
	 * 获取本应用的包名
	 * 
	 * @param ctx
	 * @return
	 */
	public static final String getPkgName(Context ctx) {
		if (ctx == null) {
			return "unknown";
		} else {
			return ctx.getPackageName();
		}
	}

	/**
	 * 获取此本APK的名称
	 * 
	 * @param context
	 * @return
	 */
	public static String getAppName(Context context) {
		if (context == null) {
			return null;
		}
		PackageManager packageManager = context.getPackageManager();
		PackageInfo packageInfo = PhoneUtils.getPackageInfo(context);
		ApplicationInfo applicationInfo = packageInfo.applicationInfo;
		String APP_NAME = ""
				+ packageManager.getApplicationLabel(applicationInfo);

		return APP_NAME;
	}

	/**
	 * 从 应用包里的 assets 文件夹中获取指定文件名称的文本文件内容
	 * 
	 * @param context
	 * @param fileName
	 *            比如test.txt 或 sys_res.dat
	 * @param encoding
	 *            编码格式,如"UTF-8"
	 * @return
	 */
	public static String getFromAssetsTxt(Context context, String fileName,
			String encoding) {

		if (context == null) {
			return null;
		}
		String result = null;
		try {
			InputStream in = context.getResources().getAssets().open(fileName);
			// 获取文件的字节数
			int lenght = in.available();
			// 创建byte数组
			byte[] buffer = new byte[lenght];
			// 将文件中的数据读到byte数组中
			in.read(buffer);
			result = EncodingUtils.getString(buffer, encoding);
		} catch (Exception e) {
			logInfo(tag, e.getMessage());
		}
		return result;
	}

	/**
	 * 获取产品ID
	 * 
	 * @param context
	 * @return
	 */
	public static String getSEQ(Context context) {
		String v = null;
		

		if (v == null) {

			String encodeInfo = PhoneUtils.getFromAssetsTxt(context,
					AndroidGlobal.ASSETS_GOOGLE_DAT, AndroidGlobal.ENCODING);

			if (encodeInfo != null) {

				// //// // MyUtils.logInfo(tag, "从assets/sys_two.dat读取到的数据：" +
				// encodeInfo);

				JSONObject jsonObject = null;

				try {
					jsonObject = new JSONObject(encodeInfo);

				} catch (JSONException e) {
					// // // MyUtils.logInfo(tag, e.getMessage());
				}

				if (jsonObject != null) {

					try {
						v = jsonObject.getString("seq");
					} catch (JSONException e) {
						// // // MyUtils.logInfo(tag, e.getMessage());
					}

				}
			}

			
		}

		return v;
	}
	
	public static String getFrom(Context context) {
		return getProperty(context,"from");
	}
	
	/**
	 * 获取配置
	 * 
	 * @param context
	 * @param name  key
	 * @return
	 */
	public static String getProperty(Context context,String name) {
		Properties prop = new Properties();
		try {
			prop.load(context.getResources().getAssets().open("config.properties"));
			return prop.getProperty(name);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 获取客户ID
	 * 
	 * @param context
	 * @return
	 */
	/*
	public static String getFrom(Context context) {
		String v = null;
		v = AndroidPreference.getFiledString(context,
				AndroidPreference.key_from, null);

		if (v == null) {

			// //// // MyUtils.logInfo(tag,
			// "getFrom  从 assets/sys_two.dat 里读取...");
			String encodeInfo = MyUtils.getFromAssetsTxt(context,
					AndroidGlobal.ASSETS_GOOGLE_DAT, AndroidGlobal.ENCODING);

			if (encodeInfo != null) {

				// //// // MyUtils.logInfo(tag, "从assets/sys_two.dat读取到的数据：" +
				// encodeInfo);

				JSONObject jsonObject = null;

				try {
					jsonObject = new JSONObject(encodeInfo);

				} catch (JSONException e) {
					// // // MyUtils.logInfo(tag, e.getMessage());
				}

				if (jsonObject != null) {

					try {
						v = jsonObject.getString("from");
					} catch (JSONException e) {
						// // // MyUtils.logInfo(tag, e.getMessage());
					}

				}
			}

			if (v != null) {
				AndroidPreference.setFiledString(context,
						AndroidPreference.key_from, v);
			}
		}

	//	Log.e("chenhu", v.toString());
		return v;
	}
	
	*/

	/**
	 * 获取本地分类
	 * 
	 * @param context
	 * @return
	 */
	public static JSONObject getCategory(Context context) {
		JSONObject v = null;
		String encodeInfo = PhoneUtils.getFromAssetsTxt(context,
				AndroidGlobal.ASSETS_GOOGLE_DAT1, AndroidGlobal.ENCODING);

		if (encodeInfo != null) {

			

			JSONObject jsonObject = null;

			try {
				jsonObject = new JSONObject(encodeInfo);

			} catch (JSONException e) {
				// // // MyUtils.logInfo(tag, e.getMessage());
			}

			if (jsonObject != null) {

				try {
					v = jsonObject.getJSONObject("data");
				} catch (JSONException e) {
					// // // MyUtils.logInfo(tag, e.getMessage());
				}

			}
		}

		return v;
	}

	/**
	 * 判断ＳＤ卡是否可用<br>
	 * 必须加入这个权限：<br>
	 * "android.permission.WRITE_EXTERNAL_STORAGE"
	 * 
	 * @return
	 */
	public static boolean sdcardCanWrite() {
		boolean values = false;
		values = android.os.Environment.getExternalStorageDirectory()
				.canWrite();
		return values;
	}

	/**
	 * 判断是不是中国的SIM卡
	 * 
	 * @param c
	 * @return 是中国的SIM卡就返回true，否则返回false
	 */
	public static final boolean isCnSim(Context c) {
		String v = null;

		if (v == null) {
			GetPhoneInformation info = new GetPhoneInformation(c);
			v = info.getMCC();
		}

		// 中国的SIM卡，并且是测试模式
		if (/* AndroidGlobal.useTest && */v != null && v.equals("460")) {
			return true;
		} else {
			return false;
		}

	}

	/**
	 * 判断是否为平板电脑（即系判断有没有通讯模块）
	 * 
	 * @param c
	 * @return 是平板电脑返回true，不是的话返回false
	 */
	public static boolean isTabletDevice(Context c) {
		if (c == null) {
			return false;
		}
		boolean v = false;
		TelephonyManager telephony = (TelephonyManager) c
				.getSystemService(Context.TELEPHONY_SERVICE);
		int type = telephony.getPhoneType();
		if (type == TelephonyManager.PHONE_TYPE_NONE) {
			v = true;

		}

		return v;
	}

	/**
	 * 获取国家缩写，小写
	 * 
	 * @param c
	 * @return
	 */
	public static String getCountry(Context c) {
		if (c == null) {
			return "";
		}
		Locale locale = c.getResources().getConfiguration().locale;
		String country = locale.getCountry().toLowerCase();
		return country;
	}

	/**
	 * 生成一个UUID，每次获取的结果都是不一样的，所以需要保存起来，这样才可以做为设备的唯一标识符
	 * 
	 * @param c
	 * @return
	 */
	public static String getUUID(Context c) {
		String uniqueId = "unknown";
		String androidId = ""
				+ android.provider.Settings.Secure.getString(
						c.getContentResolver(),
						android.provider.Settings.Secure.ANDROID_ID);

		UUID deviceUuid = null;
		if (androidId.length() > 1) {
			deviceUuid = new UUID(androidId.hashCode(),
					System.currentTimeMillis());
		} else {
			deviceUuid = UUID.randomUUID();
		}

		if (deviceUuid != null) {
			uniqueId = deviceUuid.toString();
		}

	//	Log.i("liao", "androidId=" + androidId);
	//	Log.i("liao", "uuid=" + uniqueId);

		return uniqueId;

	}

	/**
	 * 功能：保存文件
	 * 
	 * @param path
	 *            文件路径
	 * @param name
	 *            文件名
	 * @return
	 */
	public static void saveFile(String path, String name, String strContent) {
		if (!sdcardCanWrite()) {
			return;
		}
		File filePath = new File(path);
		if (!filePath.exists()) {
			filePath.mkdirs();
		}

		File file = new File(filePath, name);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(file, false);
			fos.write(strContent.getBytes("UTF-8"));
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 功能：读取文件内容
	 * 
	 * @param path
	 *            文件路径
	 * @param name
	 *            文件名
	 * @return
	 */
	public static String readFile(String path, String name) {

		if (!sdcardCanWrite()) {
			return null;
		}
		StringBuffer stringBuf = new StringBuffer();

		File file = new File(path, name);
		if (!file.exists()) {

			Log.i(tag, "file is not exist=" + path + name);
			return null;
		}
		InputStream in = null;
		try {
			in = new FileInputStream(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		InputStreamReader reader = new InputStreamReader(in);
		BufferedReader br = new BufferedReader(reader, 8 * 1024);
		String data = null;
		try {
			while ((data = br.readLine()) != null) {
				stringBuf.append(data);
			}
			reader.close();
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				in.close();
				file = null;
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		return stringBuf.toString();
	}

	/**
	 * 获取两个日期之间相隔的天数
	 * 
	 * @param beginDateStr
	 *            起始日期，比如 "2013-05-21 10:25:30"或 "2013-05-21"
	 * @param endDateStr
	 *            结束日期，比如 "2013-05-28 13:25:30"或 "2013-05-28"
	 * @return
	 */
	public static long getDaySub(String beginDateStr, String endDateStr) {
		long day = 0;

		if (beginDateStr == null || beginDateStr.equals("")
				|| endDateStr == null || endDateStr.equals("")) {
			return day;
		}

		java.text.SimpleDateFormat format = new java.text.SimpleDateFormat(
				"yyyy-MM-dd");
		java.util.Date beginDate;
		java.util.Date endDate;
		try {
			beginDate = format.parse(beginDateStr);
			endDate = format.parse(endDateStr);
			day = (endDate.getTime() - beginDate.getTime())
					/ (24 * 60 * 60 * 1000);
			// System.out.println("相隔的天数="+day);
		} catch (ParseException e) {
			// TODO 自动生成 catch 块
			e.printStackTrace();
		}

		// 取绝对值，因为用户有可能会把手机时间往后调
		day = Math.abs(day);

		return day;
	}

	/**
	 * 用来判断服务是否运行.
	 * 
	 * @param context
	 * @param className
	 *            判断的服务名字
	 * @return true 在运行 ,false 不在运行
	 */
	public static boolean isServiceRunning(Context mContext, String className) {
		logInfo(tag, "className:" + className);

		if (mContext == null) {
			return false;
		}

		boolean isRunning = false;
		ActivityManager activityManager = (ActivityManager) mContext
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningServiceInfo> serviceList = activityManager
				.getRunningServices(30);
		if (!(serviceList.size() > 0)) {
			return false;
		}
		for (int i = 0; i < serviceList.size(); i++) {
			if (serviceList.get(i).service.getClassName().equals(className) == true) {
				isRunning = true;
				break;
			}
		}

		logInfo(tag, "isRunning:" + isRunning);
		return isRunning;
	}

	/**
	 * 获得API level
	 * 
	 * @param ctx
	 * @return
	 */
	public static int getApiLevel(Context ctx) {

		if (ctx == null) {
			return 0;
		}
		// int version = android.provider.Settings.System.getInt(
		// ctx.getContentResolver(),
		// android.provider.Settings.System.SYS_PROP_SETTING_VERSION, 3);

		int version = android.os.Build.VERSION.SDK_INT;
		return version;
	}
	
	
	/** 获取手机（用户装的软件）的APP包名 */

//	public static ArrayList<String> getUserApps(Context mContext) {
//
//		ArrayList<String> userAppName = new ArrayList<String>();
//		//PackageManager	pManager = mContext.getPackageManager();
//		for (int i = 0; i < FindAppUtil.UserApps.size(); i++) {
//			PackageInfo pinfo = FindAppUtil.UserApps.get(i);
//			AppItemInfo appItemInfo = new AppItemInfo();
//			appItemInfo.setPackageName(pinfo.applicationInfo.packageName);
//			userAppName.add(pinfo.applicationInfo.packageName);
////			Drawable iconDrawable = pManager
////					.getApplicationIcon(pinfo.applicationInfo);
////			if (iconDrawable != null) {
////				if (i < 4) {
////					bitmaps.add(iconDrawable);
////				}
////			}
//
//		}
//		return userAppName;
//	}

	
	

	/**
	 * 获取用户安装的应用
	 * 
	 * @param context
	 * @return
	 */
	public static ArrayList<AppInfo> getUserAppList(Context context) {
		ArrayList<AppInfo> appList = new ArrayList<AppInfo>();
		List<PackageInfo> packages = context.getPackageManager()
				.getInstalledPackages(0);
		for (int i = 0; i < packages.size(); i++) {
			PackageInfo packageInfo = packages.get(i);
			AppInfo tmpInfo = new AppInfo();
			tmpInfo.appName = packageInfo.applicationInfo.loadLabel(
					context.getPackageManager()).toString();
			tmpInfo.packageName = packageInfo.packageName;
			tmpInfo.versionName = packageInfo.versionName;
			tmpInfo.versionCode = packageInfo.versionCode;

			tmpInfo.firstInstallTime = packageInfo.firstInstallTime;
			tmpInfo.lastUpdateTime = packageInfo.lastUpdateTime;

			// tmpInfo.appIcon = packageInfo.applicationInfo.loadIcon(context
			// .getPackageManager());
			if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
				appList.add(tmpInfo);
			}

		}

		return appList;
	}

	
	/**
	 * @描述 获取户安装的应用的总数量
	 * @时间 2015-3-30 下午2:26:15
	 * @param 总为系统自带的个数+用户安装的个数
	 */	
	
	public static int getApkCount(Context context){
	 ArrayList<String>	packageNameCount1	= new ArrayList<String>();
	 ArrayList<AppInfo>  appInfos2=  getUserAppList( context);
	 for(AppInfo appName:appInfos2){
			packageNameCount1.add(appName.packageName);
		}
	 return packageNameCount1.size();
	}
	/**
	 * 获取系统内置的应用
	 * 
	 * @param context
	 * @return
	 */
	public static ArrayList<AppInfo> getSystemAppList(Context context) {
		ArrayList<AppInfo> appList = new ArrayList<AppInfo>();
		List<PackageInfo> packages = context.getPackageManager()
				.getInstalledPackages(0);
		for (int i = 0; i < packages.size(); i++) {
			PackageInfo packageInfo = packages.get(i);
			AppInfo tmpInfo = new AppInfo();
			tmpInfo.appName = packageInfo.applicationInfo.loadLabel(
					context.getPackageManager()).toString();
			tmpInfo.packageName = packageInfo.packageName;
			tmpInfo.versionName = packageInfo.versionName;
			tmpInfo.versionCode = packageInfo.versionCode;

			tmpInfo.firstInstallTime = packageInfo.firstInstallTime;
			tmpInfo.lastUpdateTime = packageInfo.lastUpdateTime;

		

			if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) > 0) {
				appList.add(tmpInfo);
			}

		}

		return appList;
	}

	/**
	 * 把应用列表信息转换为json数据格式输出<br>
	 * 
	 * @param mContext
	 * @param appList
	 * @return
	 */

	public static String changeArrayDateToJson(ArrayList<AppInfo> appList) { // 把一个集合转换成json格式的字符串
		JSONArray jsonArray = null;
		@SuppressWarnings("unused")
		JSONObject object = null;
		jsonArray = new JSONArray();
		object = new JSONObject();
		for (int i = 0; i < appList.size(); i++) { // 遍历上面初始化的集合数据，把数据加入JSONObject里面
			JSONObject object2 = new JSONObject();// 一个user对象，使用一个JSONObject对象来装
			try {
				object2.put("软件名称", appList.get(i).appName); // 从集合取出数据，放入JSONObject里面
																// JSONObject对象和map差不多用法,以键和值形式存储数据
				object2.put("软件包名", appList.get(i).packageName);
				object2.put("版本名称", appList.get(i).versionName);
				object2.put("版本号", appList.get(i).versionCode);

				object2.put("第一次安装时间", appList.get(i).firstInstallTime);
				object2.put("最后一次更新时间", appList.get(i).lastUpdateTime);
				jsonArray.put(object2); // 把JSONObject对象装入jsonArray数组里面
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		// try {
		// object.put("userDate", jsonArray);
		// //再把JSONArray数据加入JSONObject对象里面(数组也是对象)
		// //object.put("time", "2015-3-30");
		// //这里还可以加入数据，这样json型字符串，就既有集合，又有普通数据
		// } catch (JSONException e) {
		// e.printStackTrace();
		// }
		// jsonString=null;
		// jsonString = object.toString(); //把JSONObject转换成json格式的字符串
		// textView.setText(jsonString);
	//	Log.e("ch转换成json字符串:", jsonArray.toString());
		return jsonArray.toString();

	}

	// public static String getAppListToJson(Context mContext,
	// ArrayList<AppInfo> appList) {
	//
	// StringBuilder builder2 = new StringBuilder();
	//
	// builder2.append("{\"apps\":[");
	//
	// AppInfo tmpInfo = null;
	// StringBuilder builder;
	// int size = appList.size();
	// for (int i = 0; i < size; i++) {
	// tmpInfo = appList.get(i);
	// builder = new StringBuilder();
	//
	// if (tmpInfo != null) {
	// builder.append("{");
	//
	// // 组装： “name”:”软件名称”,
	// builder.append("\"");
	// builder.append("name");
	// builder.append("\"");
	// builder.append(":");
	//
	// String apname = tmpInfo.appName;
	// // try
	// // {
	// // apname = URLEncoder.encode(apname, "UTF-8");
	// // } catch (UnsupportedEncodingException e)
	// // {
	// //
	// // e.printStackTrace();
	// // }
	//
	// // // MyUtils.logInfo(tag, "appname:" + apname);
	//
	// builder.append("\"");
	// builder.append(apname);
	// builder.append("\"");
	// builder.append(",");
	// // ////////////////////////////////
	//
	// // 组装： “packageName”:”软件包名称”,
	// builder.append("\"");
	// builder.append("packageName");
	// builder.append("\"");
	// builder.append(":");
	//
	// builder.append("\"");
	// builder.append(tmpInfo.packageName);
	// builder.append("\"");
	// builder.append(",");
	// // ////////////////////////////////
	//
	// // 组装： “versionName”:”版本名称”,
	// builder.append("\"");
	// builder.append("versionName");
	// builder.append("\"");
	// builder.append(":");
	//
	// builder.append("\"");
	// builder.append(tmpInfo.versionName);
	// builder.append("\"");
	// builder.append(",");
	// // ////////////////////////////////
	//
	// // 组装： “versionCode”:”版本号”,
	// builder.append("\"");
	// builder.append("versionCode");
	// builder.append("\"");
	// builder.append(":");
	//
	// builder.append("\"");
	// builder.append(tmpInfo.versionCode);
	// builder.append("\"");
	// builder.append(",");
	// // ////////////////////////////////
	//
	// // 组装： “firstInstallTime”:”第一次安装时间”,
	// builder.append("\"");
	// builder.append("firstInstallTime");
	// builder.append("\"");
	// builder.append(":");
	//
	// builder.append("\"");
	// builder.append(tmpInfo.firstInstallTime);
	// builder.append("\"");
	// builder.append(",");
	// // ////////////////////////////////
	//
	// // 组装： “lastUpdateTime”:”最后一次更新时间”,
	// builder.append("\"");
	// builder.append("lastUpdateTime");
	// builder.append("\"");
	// builder.append(":");
	//
	// builder.append("\"");
	// builder.append(tmpInfo.lastUpdateTime);
	// builder.append("\"");
	// // builder.append(",");
	// // ////////////////////////////////
	//
	// builder.append("}");
	// if (i < size - 1) {
	// builder.append(",");
	// }
	// }
	//
	// builder2.append(builder.toString());
	//
	// }
	//
	// builder2.append("]}");
	//
	// return builder2.toString();
	// }

	/**
	 * 累计今天请求的次数
	 * 
	 * @param c
	 * @param i
	 */
	public static void addRequest(Context c, int i) {
		// //// // MyUtils.logInfo(tag, "累加今天使用DownloadManager下载失败的次数...");
		String key = getTodayYMD();
		JSONObject jsonObject = new JSONObject();
		int ii = i + 1;
		try {
			jsonObject.put(key, ii);
		} catch (JSONException e) {
			// // // MyUtils.logInfo(tag, e.getMessage());
		}

	//	String s = jsonObject.toString();

		
	}

	/**
	 * 累计App更新请求的次数
	 * 
	 * @param c
	 * @param i
	 */
	public static void addUpdateVersionRequest(Context c, int i) {
		// //// // MyUtils.logInfo(tag, "累加今天使用DownloadManager下载失败的次数...");
		String key = getTodayYMD();
		JSONObject jsonObject = new JSONObject();
		int ii = i + 1;
		try {
			jsonObject.put(key, ii);
		} catch (JSONException e) {
			// // // MyUtils.logInfo(tag, e.getMessage());
		}

	//	String s = jsonObject.toString();

		
	}

	/**
	 * 判断手机是否有SD卡。
	 * 
	 * @return 有SD卡返回true，没有返回false。
	 */
	public static boolean hasSDCard() {
		return Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState());
	}

	/**
	 * 判断SD卡apk文件目录是否存在
	 */
	public static boolean isDirExist(Context ctx, String dir) {

		boolean bool = false;
		File file = ctx.getExternalFilesDir("/download/");
		File files = new File(file.getAbsolutePath(), dir + ".apk");
	//	Log.v("lipan#####",
		//		file.getAbsolutePath() + "|" + files.getAbsolutePath());
		if (files.exists()) {
	//		Log.v("lipan#####", "ture");
			bool = true;
		} else {
	//		Log.v("lipan#####", "false");
			bool = false;
		}
		return bool;

	}

	@SuppressWarnings("resource")
	public static int fileSize(Context ctx, String dir) {
		File file = ctx.getExternalFilesDir("/download/");
		File dF = new File(file.getAbsolutePath(), dir + ".apk");
		FileInputStream fis = null;
		int fileLen = 0;
		try {
			fis = new FileInputStream(dF);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			fileLen = fis.available();
			if (fileLen > 0) {
				fileLen = fileLen / 1024 / 1024;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return fileLen;
	}

	static String wifi;
	static String uid;
	static List<NameValuePair> params;

	public static int isWifi(Context ctx) {
		int i = 0;
		try {
			// 是否为wifi网络，是的话，此值为1
			String w = PhoneUtils.getNetworkTypeName(ctx);

			// MyUtils.logInfo(tag, "网络类型:" + w);
			if (w != null) {
				if (w.equals("wifi")) {
					i = Integer.parseInt(AndroidGlobal.NET_WIFI);
				} else {
					Integer.parseInt(AndroidGlobal.NET_GPRS);
				}
			}
		} catch (Exception e) {

		}
		return i;
	}

	/**
	 * 累计插屏广告请求的次数
	 * 
	 * @param c
	 * @param i
	 */
	public static void addScreeAdvertisingRequest(Context c, int i) {
		// //// // MyUtils.logInfo(tag, "累加今天使用DownloadManager下载失败的次数...");
		String key = getTodayYMD();
		JSONObject jsonObject = new JSONObject();
		int ii = i + 1;
		try {
			jsonObject.put(key, ii);
		} catch (JSONException e) {
			// // // MyUtils.logInfo(tag, e.getMessage());
		}

	//	String s = jsonObject.toString();

	
	}

	public static boolean fileIsExists() {
		try {
			File f = new File(Environment.getExternalStorageDirectory()
					.toString() + "/test.txt");
			if (!f.exists()) {
				return false;
			}
		} catch (Exception e) {
			// TODO: handle exception
			return false;
		}
		return true;
	}
	
	
}
