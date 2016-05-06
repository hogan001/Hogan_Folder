package com.witskies.manager.multithread;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Environment;
import android.os.IBinder;

import com.witskies.manager.bean.ToolsBean;
import com.witskies.manager.helputil.Const;
import com.witskies.manager.helputil.FileUtil;
import com.witskies.manager.helputil.LogUtils;

public class DownLoaderService extends Service {
	/**
	 * 停止
	 */
	public static final String STOP_DOWNLOAD = "stopService";
	/**
	 * 开始
	 */
	public static final String START_DOWNLOAD = "startService";
	/**
	 * 下载中
	 */
	public static final String LOADING_DOWNLOAD = "loadingService";
	/**
	 * 完成
	 */
	public static final String FINISHED_DOWNLOAD = "finishedService";
	/**
	 * 后台任务集合
	 */
	private static ArrayList<ToolsBean> mTools;
	/**
	 * 每个任务的控制器
	 */
	private static FileDownloader mDownloader;
	/**
	 * 存储下载文件的地址
	 */
	private File mFile = null;
	/**
	 * 任务控制器集合
	 */
	private static HashMap<Integer, FileDownloader> mDownloaders;
	private MsgReceiver msgReceiver;
	/**
	 * 任务
	 */
	private ToolsBean mTool;

	@Override
	public void onCreate() {

		// // 动态注册广播接收器
		msgReceiver = new MsgReceiver();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(STOP_DOWNLOAD);
		intentFilter.addAction(START_DOWNLOAD);
		intentFilter.addAction(FINISHED_DOWNLOAD);
		registerReceiver(msgReceiver, intentFilter);

		mTools = new ArrayList<ToolsBean>();
		mDownloaders = new HashMap<Integer, FileDownloader>();
		mFile = new File(FileUtil.makePath(Environment.getExternalStorageDirectory().getPath(),
				Const.DOWNLOAD_APK_PATH));
		if (!mFile.exists()) {
			mFile.mkdirs();
		}
		super.onCreate();
	}

	int index = -1;

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		mTool = (ToolsBean) intent.getSerializableExtra("tool");
		mTool.setPath(mFile.getPath() + "/" + mTool.getName());
		String url = mTool.getUrl();
		final String downUrl = Const.START + url.substring(url.lastIndexOf("/") + 1);
		new Thread() {
			public void run() {
				try {
					index++;
					mDownloader = new FileDownloader(downUrl, getApplicationContext(), index,
							mTool.getName(), mFile);
					mDownloaders.put(index, mDownloader);
					mDownloader.Download(new CompleteListener() {
						public void isComplete(int size) {
							// 完成
						}
					});
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onDestroy() {
		unregisterReceiver(msgReceiver);
		LogUtils.d("service onDestroy");
		mTools.clear();
		mDownloaders.clear();
		super.onDestroy();
	}

	public static FileDownloader getFileDownloader(int id) {
		if (mDownloaders == null) {
			return null;
		}
		return mDownloaders.get(id);
	}

	public static ArrayList<ToolsBean> getmTools() {
		return mTools;
	}

	/**
	 * 广播接收器
	 * 
	 * @author lance
	 * 
	 */
	public class MsgReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(STOP_DOWNLOAD)) {// 停止
				LogUtils.d("接收到了停止的广播");
				// 停止
				stopSelf();
				for (int i = 0; i < mDownloaders.size(); i++) {
					mDownloaders.get(i).setPause();
				}
			} else if (intent.getAction().equals(START_DOWNLOAD)) {// 开始
				int length = intent.getIntExtra("fileLength", 0);
				mTool.setSize(length);
				mTool.setDownLoadId(intent.getIntExtra("id", 0));
				mTools.add(mTool);
			} else if (intent.getAction().equals(FINISHED_DOWNLOAD)) {// 完成

			} else if (intent.getAction().equals(LOADING_DOWNLOAD)) {// 下载中

			}
		}
	}
}
