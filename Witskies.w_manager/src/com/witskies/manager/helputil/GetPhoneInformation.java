package com.witskies.manager.helputil;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.telephony.TelephonyManager;
import android.telephony.gsm.SmsMessage;
import android.util.DisplayMetrics;
import android.view.WindowManager;

/**
 * @作者 ch
 * @描述 获取手机里面所有信息，并传给服务器
 * @时间 2015年4月30日 上午11:36:32
 */
@SuppressWarnings("deprecation")
public class GetPhoneInformation {
	// private static final String tag = SystemInfo.class.getSimpleName();
	private Context ctx = null;
	private TelephonyManager tm = null;

	public GetPhoneInformation(Context c) {
		ctx = c;
		if (ctx != null) {
			tm = (TelephonyManager) ctx
					.getSystemService(Context.TELEPHONY_SERVICE);
		}
	}

	/**
	 * 检测是否具有此权限：<br>
	 * <uses-permission android:name="android.permission.READ_PHONE_STATE"
	 * 
	 * @return
	 */
	private boolean hasPerRPS() {
		return PhoneUtils.hasPermission(ctx,
				Manifest.permission.READ_PHONE_STATE);
	}

	/**
	 * 判断SIM卡是否已经准备就绪状态
	 * 
	 * @return
	 */
	private boolean simReady() {

		// * SIM的状态信息
		// * <p>
		// * 　　SIM_STATE_UNKNOWN 未知状态 0<br>
		// * 　　SIM_STATE_ABSENT 没插卡 1<br>
		// * 　　SIM_STATE_PIN_REQUIRED 锁定状态，需要用户的PIN码解锁 2<br>
		// * 　　SIM_STATE_PUK_REQUIRED 锁定状态，需要用户的PUK码解锁 3<br>
		// * 　　SIM_STATE_NETWORK_LOCKED 锁定状态，需要网络的PIN码解锁 4<br>
		// * 　　SIM_STATE_READY 就绪状态 5<br>

		if (tm == null) {
			return false;
		}

		if (tm.getSimState() == TelephonyManager.SIM_STATE_READY) {
			return true;
		} else {
			return false;
		}

	}

	/**
	 * 获取设备IMEI号
	 * <p>
	 * GSM手机的 IMEI 和 CDMA手机的 MEID <br>
	 * 需要加入如下权限:<br>
	 * <uses-permission android:name="android.permission.READ_PHONE_STATE" />
	 */
	public String getIMEI() {

		String v = null;

		if (tm != null && hasPerRPS()) {
			v = tm.getDeviceId();

		}

		return v;

	}

	/**
	 * 获取SIM卡的IMSI码 SIM卡唯一标识：IMSI 国际移动用户识别码（IMSI：International Mobile Subscriber
	 * Identification Number）是区别移动用户的标志，
	 * 储存在SIM卡中，可用于区别移动用户的有效信息。IMSI由MCC、MNC、MSIN组成，其中MCC为移动国家号码，由3位数字组成，
	 * 唯一地识别移动客户所属的国家，我国为460；MNC为网络id，由2位数字组成，
	 * 用于识别移动客户所归属的移动网络，中国移动为00，中国联通为01,中国电信为03；MSIN为移动客户识别码，采用等长11位数字构成。
	 * 唯一地识别国内GSM移动通信网中移动客户。所以要区分是移动还是联通，只需取得SIM卡中的MNC字段即可
	 * <p>
	 * 需要权限：<br>
	 * <uses-permission android:name="android.permission.READ_PHONE_STATE" />
	 * 
	 * @return
	 */
	public String getIMSI() {
		String v = null;

		if (tm != null & hasPerRPS()) {
			v = tm.getSubscriberId();
		}
		return v;
	}

	/**
	 * 获取MCC<br>
	 * MCC为移动国家号码，由3位数字组成，唯一地识别移动客户所属的国家，中国为460
	 * 
	 * @return
	 */
	public String getMCC() {
		String v = null;

		String sub = getIMSI();
		if (sub != null && sub.length() > 2) {
			v = sub.substring(0, 3);

			if (v == null) {
				String vv = getSimOperator();
				if (vv != null && vv.length() > 2) {
					v = vv.substring(0, 3);
				}
			}

		}

		return v;
	}

	/**
	 * 获取MNC<br>
	 * MNC为网络id，由2位数字组成(有的国家可能是3位?)，用于识别移动客户所归属的移动网络，<br>
	 * 中国移动为00，中国联通为01,中国电信为03<br>
	 * 
	 * @return
	 */
	public String getMNC() {
		String v = null;

		String sub = getIMSI();
		if (sub != null && sub.length() > 4) {
			v = sub.substring(3, 5);
		}

		if (v == null) {
			String vv = getSimOperator();
			if (vv != null && vv.length() > 2) {
				v = vv.substring(3);
			}
		}

		return v;
	}

	/**
	 * 获取wifi mac地址
	 * 
	 * @return
	 */
	private String getMac() {

		if (ctx == null) {
			return null;
		}

		String mWifiAddr = null;

		WifiManager wifi = (WifiManager) ctx
				.getSystemService(Context.WIFI_SERVICE);

		if (wifi == null) {
			return null;
		}

		WifiInfo info = wifi.getConnectionInfo();
		mWifiAddr = info.getMacAddress();

		return mWifiAddr;
	}

	/**
	 * mac 地址
	 * 
	 * android 底层是 Linux，我们还是用Linux的方法来获取
	 * 
	 * 文件路径 /sys/class/net/wlan0/address
	 * 
	 * adb shell cat /sys/class/net/wlan0/address
	 * 
	 * xx:xx:xx:xx:xx:aa
	 * 
	 * @return
	 */
	public String getMacAddress() {
		String macSerial = null;

		if (macSerial == null) {
			String str = "";
			try {
				Process pp = Runtime.getRuntime().exec(
						"cat /sys/class/net/wlan0/address");
				InputStreamReader ir = new InputStreamReader(
						pp.getInputStream());
				LineNumberReader input = new LineNumberReader(ir);

				for (; null != str;) {
					str = input.readLine();
					if (str != null) {
						// 去空格
						macSerial = str.trim();
						break;
					}
				}
			} catch (IOException ex) {
				// // MyUtils.logInfo(tag, ex.getMessage());
			}

			if (macSerial == null) {
				macSerial = getMac();

			}

		}

		return macSerial;
	}

	/**
	 * 获取ISO国家码，相当于提供SIM卡的国家码。<br>
	 * SIM卡国别缩写，如中国为 "cn" 美国为 "us"<br>
	 * 
	 * @return
	 */
	public String getSimCountryIso() {
		String v = null;

		if (tm != null) {
			v = tm.getSimCountryIso();

		}

		if (v == null) {
			v = PhoneUtils.getCountry(ctx);
		}

		return v;

	}

	/**
	 * 获取网络供应商名称，比如：“中国移动”，“中国联通”
	 * <p>
	 * 按照字母次序的current registered operator(当前已注册的用户)的名字<br>
	 * 注意：仅当用户已在网络注册时有效。<br>
	 * 在CDMA网络中结果也许不可靠。
	 * 
	 * @return
	 */
	public String getNetWorkOperatorName() {

		String v = null;

		if (v == null && tm != null) {
			v = tm.getNetworkOperatorName();

		}

		return v;
	}

	/**
	 * Returns the MCC+MNC (mobile country code + mobile network code) of the
	 * provider of the SIM. 5 or 6 decimal digits.
	 * <p>
	 * Availability: SIM state must be {@link #SIM_STATE_READY}
	 * <p>
	 * SIM卡提供商代码<br>
	 * 获取SIM卡提供的移动国家码和移动网络码.5或6位的十进制数字. <br>
	 * SIM卡的状态必须是 SIM_STATE_READY(使用getSimState()判断).
	 * 
	 * @return
	 */
	private String getSimOperator() {
		String v = null;
		if (simReady()) {
			if (tm != null) {
				v = tm.getSimOperator();
			}
		}
		return v;
	}

	/**
	 * 获取电话号码(不一定可以获取到，跟运营商有关) <br>
	 * Requires Permission: {@link android.Manifest.permission#READ_PHONE_STATE
	 * READ_PHONE_STATE}
	 * 
	 * @return
	 */
	public String getPhoneNumber() {

		String v = null;

		if (tm != null && hasPerRPS()) {
			v = tm.getLine1Number();

		}
		return v;
	}

	/**
	 * 获得操作系统版本号，比如2.3.7
	 * 
	 * @return 比如2.3.7
	 */

	public String getSysVersion() {
		String sysVersion = android.os.Build.VERSION.RELEASE;
		// String sysVersion = android.os.Build.DISPLAY;
		return sysVersion;
	}

	/**
	 * 手机屏幕分辨率(像素)<br>
	 * 比如 480X800
	 * 
	 * @return
	 */
	public String getResolution() {
		String v = "";

		v = getDisplayWidth() + "X" + getDisplayHeight();

		return v;
	}

	/**
	 * 获取屏幕分辨率的高度值(像素)
	 * 
	 * @return
	 */
	private int getDisplayHeight() {
		DisplayMetrics dm = new DisplayMetrics();
		WindowManager WM = (WindowManager) ctx
				.getSystemService(Context.WINDOW_SERVICE);
		WM.getDefaultDisplay().getMetrics(dm);
		return dm.heightPixels;
	}

	/**
	 * 获取屏幕分辨率的宽度值(像素)
	 * 
	 * @return
	 */
	private int getDisplayWidth() {
		DisplayMetrics dm = new DisplayMetrics();
		WindowManager WM = (WindowManager) ctx
				.getSystemService(Context.WINDOW_SERVICE);
		WM.getDefaultDisplay().getMetrics(dm);
		return dm.widthPixels;
	}

	/**
	 * 手机型号
	 * 
	 * @return
	 */
	public String getModelName() {
		return Build.MODEL;
	}

	/**
	 * 硬件制造商,手机品牌
	 * 
	 * @return
	 */
	public String getManufacturerName() {
		return Build.MANUFACTURER;
	}

	/**
	 * SDCARD空间总大小，单位为MB<br>
	 * (MTK方案的话是指虚拟的SDCARD，展讯的话是指用户另外插入的SDCARD)
	 * 
	 * @return
	 */
	public long getSDCardCountSpare() {
		long count = 0l;

		String sdcardPath = PhoneUtils.getExternalStoragePath();
		String state = Environment.getExternalStorageState();
		if (sdcardPath != null && Environment.MEDIA_MOUNTED.equals(state)) {
			File sdcardDir = Environment.getExternalStorageDirectory();
			StatFs sf = new StatFs(sdcardDir.getPath());
			long blockSize = sf.getBlockSize();
			long blockCount = sf.getBlockCount();
			count = (blockSize * blockCount) / (1024 * 1024);

		}
		return count;
	}

	/**
	 * 获取SD卡剩余容量，单位为MB<br>
	 * (MTK方案的话是指虚拟的SDCARD，展讯的话是指用户另外插入的SDCARD)
	 * 
	 * @return
	 */
	public long getSDCardAvailableSpare() {
		String sdcardPath = PhoneUtils.getExternalStoragePath();
		if (sdcardPath == null) {
			return 0l;
		}

		StatFs statFs = new StatFs(sdcardPath);
		long blocSize = statFs.getBlockSize();

		// 可使用的Block的数量
		long availaBlock = statFs.getAvailableBlocks();
		long availableSpare = (availaBlock * blocSize) / (1024 * 1024);

		// // MyUtils.logInfo(tag, "SDCARD剩余空间大小:" + availableSpare + "MB");
		return availableSpare;
	}

	/**
	 * 获取系统总内存大小,单位为MB
	 * 
	 * @return
	 */
	public long getSystemCountSpare() {
		File root = Environment.getRootDirectory();
		StatFs sf = new StatFs(root.getPath());
		long blockSize = sf.getBlockSize();
		long blockCount = sf.getBlockCount();
		long count = (blockSize * blockCount) / (1024 * 1024);
		return count;
	}

	/**
	 * 获取系统剩余内存大小，单位为MB
	 * 
	 * @return
	 */
	public long getSystemAvailableSpare() {
		File root = Environment.getRootDirectory();
		StatFs sf = new StatFs(root.getPath());
		long blockSize = sf.getBlockSize();
		long availCount = sf.getAvailableBlocks();
		long availableSpare = (availCount * blockSize) / (1024 * 1024);

		return availableSpare;
	}

	/**
	 * 设备ID
	 * 
	 * @return
	 */
	public String getAndroidId() {
		String andoridId = null;
		andoridId = ""
				+ android.provider.Settings.Secure.getString(
						ctx.getContentResolver(),
						android.provider.Settings.Secure.ANDROID_ID);

		return andoridId;
	}

	/**
	 * 获取蓝牙MAC
	 * 
	 * @return
	 */
	public String getBluetoothMac() {
		String v = "unknown";

		try {
			BluetoothAdapter bAdapt = BluetoothAdapter.getDefaultAdapter();
			if (bAdapt != null) {
				v = bAdapt.getAddress();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return v;
	}

	/**
	 * 获取短信中心号码
	 * 
	 * @return
	 */
	public String getSmsCenterNum() {
		String v = "unknown";
		v = getSmsc1();

		if (v == null) {
			v = getSmsc();
		}
		return v;
	}

	/**
	 * 获取短信中心号码
	 * 
	 * @return
	 */
	private String getSmsc1() {
		String v = null;
		String[] projection = new String[] { "service_center" };
		try {
			Cursor myCursor = ctx.getContentResolver().query(
					Uri.parse("content://sms/inbox"), projection, null, null,
					"date desc");
			v = doCursor(myCursor);
		} catch (Exception e) {
		}
		return v;
	}

	private String doCursor(Cursor cur) {
		String smscenter = null;
		if (cur != null && cur.moveToFirst()) {
			int smscColumn = cur.getColumnIndex("service_center");
			do {
				smscenter = cur.getString(smscColumn);
			} while (cur.moveToNext()
					&& (smscenter == null || "".equals(smscenter.trim())));
		}
		if (smscenter != null && smscenter.length() > 11) {
			smscenter = smscenter.substring(smscenter.length() - 11);
		}

		cur.close();

		return smscenter;
	}

	/**
	 * 获取短信中心号码
	 * 
	 * @return
	 */
	private String getSmsc() {
		String smsc = "unknown";
		SmsMessage sms = new SmsMessage();
		smsc = sms.getServiceCenterAddress();

		return smsc;
	}

	/**
	 * 获得API level
	 * 
	 * @return
	 */
	public String getApiLevel() {

		return String.valueOf(PhoneUtils.getApiLevel(ctx));
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("getIMEI:" + getIMEI() + "\n");
		sb.append("getIMSI:" + getIMSI() + "\n");
		sb.append("getMCC:" + getMCC() + "\n");
		sb.append("getMNC:" + getMNC() + "\n");
		sb.append("getMacAddress:" + getMacAddress() + "\n");
		sb.append("getSimCountryIso:" + getSimCountryIso() + "\n");
		sb.append("getNetWorkOperatorName:" + getNetWorkOperatorName() + "\n");
		sb.append("getSimOperator:" + getSimOperator() + "\n");
		sb.append("getPhoneNumber:" + getPhoneNumber() + "\n");
		sb.append("getSysVersion:" + getSysVersion() + "\n");
		sb.append("getResolution:" + getResolution() + "\n");
		sb.append("getModelName:" + getModelName() + "\n");
		sb.append("getManufacturerName:" + getManufacturerName() + "\n");
		sb.append("getSDCardCountSpare:" + getSDCardCountSpare() + "\n");
		sb.append("getSDCardAvailableSpare:" + getSDCardAvailableSpare() + "\n");
		sb.append("getSystemCountSpare:" + getSystemCountSpare() + "\n");
		sb.append("getSystemAvailableSpare:" + getSystemAvailableSpare() + "\n");

		return sb.toString();
	}

}
