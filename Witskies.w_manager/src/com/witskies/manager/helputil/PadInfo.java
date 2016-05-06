package com.witskies.manager.helputil;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.Locale;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.util.DisplayMetrics;
import android.view.WindowManager;

public class PadInfo
{
//	private static final String tag = PadInfo.class.getSimpleName();
	private Context ctx = null;

	public PadInfo(Context c)
	{
		this.ctx = c;
	}

	/**
	 * 获得API level
	 * 
	 * @return
	 */
	public String getApiLevel()
	{

		return String.valueOf(PhoneUtils.getApiLevel(ctx));
	}

	/**
	 * 获取国家缩写，小写
	 * 
	 * @param c
	 * @return
	 */
	public String getCountry()
	{
		if (ctx == null)
		{
			return null;
		}
		Locale locale = ctx.getResources().getConfiguration().locale;
		String country = locale.getCountry().toLowerCase();
		return country;
	}

	/**
	 * 其实这是一个UUID
	 * 
	 * @return
	 */
	@SuppressWarnings("unused")
	public String getIMSI()
	{

		if (ctx == null)
		{
			return null;
		}

		String v = null;

		String filePath = PhoneUtils.getExternalStoragePath() + File.separator
				+ "Android" + File.separator + "data" + File.separator
				+ "com.android.settings" + File.separator + "files";

		String saveName = "uuid.bin";

		

		// 保存一份到SD卡
		if (v != null)
		{
			PhoneUtils.saveFile(filePath, saveName, v);
		}

		if (v == null)
		{
			// 从保存在SD卡的文件中获取
			v = PhoneUtils.readFile(filePath, saveName);

			// // PhoneUtils.logInfo(tag, "从保存在SD卡的文件中获取:" + v);


		}

		// 还是为空？
		if (v == null)
		{
			// 生成一个UUID
			v = PhoneUtils.getUUID(ctx);

			// // PhoneUtils.logInfo(tag, "生成一个UUID:" + v);

			// 保存起来
			if (v != null)
			{
				// // PhoneUtils.logInfo(tag, "保存起来");

				PhoneUtils.saveFile(filePath, saveName, v);

			}
		}

		// // PhoneUtils.logInfo(tag, "最终结果:" + v);

		return v;
	}

	/**
	 * 获取wifi mac地址
	 * 
	 * @return
	 */
	private String getMac()
	{

		if (ctx == null)
		{
			return null;
		}

		String mWifiAddr = null;

		WifiManager wifi = (WifiManager) ctx
				.getSystemService(Context.WIFI_SERVICE);

		if (wifi == null)
		{
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
	public String getMacAddress()
	{
		if (ctx == null)
		{
			return null;
		}
		String macSerial=null;

		if (macSerial == null)
		{
			String str = "";
			try
			{
				Process pp = Runtime.getRuntime().exec(
						"cat /sys/class/net/wlan0/address");
				InputStreamReader ir = new InputStreamReader(
						pp.getInputStream());
				LineNumberReader input = new LineNumberReader(ir);

				for (; null != str;)
				{
					str = input.readLine();
					if (str != null)
					{
						// 去空格
						macSerial = str.trim();
						break;
					}
				}
			} catch (IOException ex)
			{
				ex.printStackTrace();
				// // PhoneUtils.logInfo(tag, ex.getMessage());
			}

			if (macSerial == null)
			{
				macSerial = getMac();

			}

			
		}

		return macSerial;
	}

	/**
	 * 获得操作系统版本号，比如2.3.7
	 * 
	 * @return 比如2.3.7
	 */

	public String getSysVersion()
	{
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
	public String getResolution()
	{
		String v = "";

		v = getDisplayWidth() + "X" + getDisplayHeight();

		return v;
	}

	/**
	 * 获取屏幕分辨率的高度值(像素)
	 * 
	 * @return
	 */
	private int getDisplayHeight()
	{
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
	private int getDisplayWidth()
	{
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
	public String getModelName()
	{
		return Build.MODEL;
	}

	/**
	 * 硬件制造商,手机品牌
	 * 
	 * @return
	 */
	public String getManufacturerName()
	{
		return Build.MANUFACTURER;
	}

	/**
	 * SDCARD空间总大小，单位为MB<br>
	 * (MTK方案的话是指虚拟的SDCARD，展讯的话是指用户另外插入的SDCARD)
	 * 
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public long getSDCardCountSpare()
	{
		long count = 0l;

		String sdcardPath = PhoneUtils.getExternalStoragePath();
		String state = Environment.getExternalStorageState();
		if (sdcardPath != null && Environment.MEDIA_MOUNTED.equals(state))
		{
			File sdcardDir = Environment.getExternalStorageDirectory();
			StatFs sf = new StatFs(sdcardDir.getPath());
			long blockSize = sf.getBlockSize();
			long blockCount = sf.getBlockCount();
			count = (blockSize * blockCount) / (1024 * 1024);

			// // PhoneUtils.logInfo(tag, "block大小:" + blockSize);
			// // PhoneUtils.logInfo(tag, "block数目:" + blockCount);
			// // PhoneUtils.logInfo(tag, "sdcard总大小:" + count + "MB");
		}
		return count;
	}

	/**
	 * 获取SD卡剩余容量，单位为MB<br>
	 * (MTK方案的话是指虚拟的SDCARD，展讯的话是指用户另外插入的SDCARD)
	 * 
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public long getSDCardAvailableSpare()
	{
		String sdcardPath = PhoneUtils.getExternalStoragePath();
		if (sdcardPath == null)
		{
			return 0l;
		}

		StatFs statFs = new StatFs(sdcardPath);
		long blocSize = statFs.getBlockSize();

		// 可使用的Block的数量
		long availaBlock = statFs.getAvailableBlocks();
		long availableSpare = (availaBlock * blocSize) / (1024 * 1024);

		// // PhoneUtils.logInfo(tag, "SDCARD剩余空间大小:" + availableSpare + "MB");
		return availableSpare;
	}

	/**
	 * 获取系统总内存大小,单位为MB
	 * 
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public long getSystemCountSpare()
	{
		File root = Environment.getRootDirectory();
		StatFs sf = new StatFs(root.getPath());
		long blockSize = sf.getBlockSize();
		long blockCount = sf.getBlockCount();
		long count = (blockSize * blockCount) / (1024 * 1024);
		// // PhoneUtils.logInfo(tag, "block大小:" + blockSize);
		// // PhoneUtils.logInfo(tag, "block数目:" + blockCount);
		// // PhoneUtils.logInfo(tag, "系统总内存大小:" + count + "MB");
		return count;
	}

	/**
	 * 获取系统剩余内存大小，单位为MB
	 * 
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public long getSystemAvailableSpare()
	{
		File root = Environment.getRootDirectory();
		StatFs sf = new StatFs(root.getPath());
		long blockSize = sf.getBlockSize();
		long availCount = sf.getAvailableBlocks();
		long availableSpare = (availCount * blockSize) / (1024 * 1024);

		// // PhoneUtils.logInfo(tag, "可用的block数目：:" + availCount);
		// // PhoneUtils.logInfo(tag, ",系统剩余内存大小:" + availableSpare + "MB");

		return availableSpare;
	}

	/**
	 * 设备ID
	 * 
	 * @return
	 */
	public String getAndroidId()
	{
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
	public String getBluetoothMac()
	{
		String v = "unknown";

		try
		{
			BluetoothAdapter bAdapt = BluetoothAdapter.getDefaultAdapter();
			if (bAdapt != null)
			{
				v = bAdapt.getAddress();
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		
//		if(null == v){
//			v = "unknown";
//		}

		return v;
	}

}
