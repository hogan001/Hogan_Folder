package com.witskies.manager.app;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.witskies.manager.helputil.FileUtil;
import com.witskies.manager.image.ImageConstants;
import com.witskies.manager.image.MediaUtils;
import com.witskies.manager.service.CrashService;

/**
 * *
 * 
 * @作者 ch
 * @描述 管理所有Activity的基类
 * @时间 2015-3-23 上午10:20:33
 */
public class WitskiesApplication extends Application {
	private List<Activity> mList = new ArrayList<Activity>();// 把所有Activity加进来
	private boolean isDownload;// 用于APP更新是時，是否需要下载更新APP
	private boolean isFirst;// 用于是不是第一次出現dialog
	private static WitskiesApplication application;
	private JSONObject netJsonObject;// 网络分类app

	private String downloadApk;

	private int documentCounts;//

	public static ArrayList<String> thumbnailImagesList = new ArrayList<String>();// 把得到的缩略图保存，用于比较
	
	public static int ch;
    public static boolean logFlag=true;
    
	/**
	 * 图片常量
	 */
	public static final String IMAGE_POSITION = "universalimageloader.IMAGE_POSITION";

	// public static ArrayList<HashMap<String, String>> mVideos = null;

	public int getDocumentCounts() {
		return documentCounts;
	}

	public void setDocumentCounts(int documentCounts) {
		this.documentCounts = documentCounts;
	}

	public String getDownloadApk() {
		return downloadApk;
	}

	public void setDownloadApk(String downloadApk) {
		this.downloadApk = downloadApk;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		initDate();
		 CrashHandler.getInstance().init(getApplicationContext());
		 Intent intent=new Intent(getApplicationContext(),
		 CrashService.class);
		 startService(intent);
		initImageLoader(getApplicationContext());

	}

	/**
	 * @作者 ch
	 * @描述 数据初始化
	 * @时间 2015年5月18日 下午2:09:12
	 */
	@SuppressWarnings("unused")
	private void initDate() {
		isDownload = false;
		if (ImageConstants.Config.DEVELOPER_MODE
				&& Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
			StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectAll()
					.penaltyDialog().build());
			StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll().penaltyDeath()
					.build());
		}

	}

	public static void initImageLoader(Context context) {
		ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(context);
		config.threadPriority(Thread.MIN_PRIORITY + 2);
		config.denyCacheImageMultipleSizesInMemory();
		config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
		config.diskCacheSize(50 * 1024 * 1024); // 50 MiB
		config.tasksProcessingOrder(QueueProcessingType.LIFO);
		config.threadPoolSize(3);
		config.memoryCache(new UsingFreqLimitedMemoryCache(2 * 1024 * 1024)).memoryCacheSize(
				2 * 1024 * 1024);
		ImageLoader.getInstance().init(config.build());

	}

	public JSONObject getNetJsonObject() {
		return netJsonObject;
	}

	public void setNetJsonObject(JSONObject netJsonObject) {
		this.netJsonObject = netJsonObject;
	}

	// public ArrayList<HashMap<String, String>> getAppURLlist() {
	// return AppURLlist;
	// }
	//
	// public void setAppURLlist(ArrayList<HashMap<String, String>> appURLlist)
	// {
	// AppURLlist = appURLlist;
	// }

	/**
	 * 单例加锁双保险
	 **/
	public static WitskiesApplication getInstantiation() {
		if (application == null) {
			synchronized (WitskiesApplication.class) {
				if (application == null) {
					application = new WitskiesApplication();
				}
			}

		}
		return application;
	}

	public List<Activity> getmList() {
		return mList;
	}

	// add Activity
	public void addActivity(Activity activity) {
		if (!mList.contains(activity)) {
			mList.add(activity);
		}
	}

	/**
	 * @描述 退出所有Activity
	 * @时间 2015年5月19日 上午9:34:41
	 */
	public void exit(Context context) {
		clearThumnail();
		try {
			for (Activity activity : mList) {

				if (activity != null) {

					activity.finish();

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
		// finally {
		//
		// }
	}


	/**
	 * 清理缓存数据
	 */
	private void clearThumnail() {
		try {
			String path = Environment.getExternalStorageDirectory().getPath()
					+ "/Hogan_Shipin_thumbnailImages";
			if (path != null) {
				File file = new File(path);
				FileUtil.deleteAll(file);
				List<String> paths = new ArrayList<String>();
				paths.add(path);
				MediaUtils.getInstance().updateGallery(paths, getApplicationContext());
			}

		} catch (Exception e) {
		}

	}

	public boolean isFirst() {
		return isFirst;
	}

	public void setFirst(boolean isFirst) {
		this.isFirst = isFirst;
	}

	public boolean isDownload() {
		return isDownload;
	}

	public void setDownload(boolean isDownload) {
		this.isDownload = isDownload;
	}

}
