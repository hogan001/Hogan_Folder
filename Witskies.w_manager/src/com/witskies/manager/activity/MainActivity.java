package com.witskies.manager.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.witskies.manager.app.WitskiesApplication;
import com.witskies.manager.bean.AppItemInfo;
import com.witskies.manager.bean.DownloadApk;
import com.witskies.manager.bean.RecommendBean;
import com.witskies.manager.bean.ToolsBean;
import com.witskies.manager.dialog.LoadingDialog;
import com.witskies.manager.fileexplorer.FileMainActivity;
import com.witskies.manager.fragment.VideoFragment;
import com.witskies.manager.helputil.Const;
import com.witskies.manager.helputil.FileUtil;
import com.witskies.manager.helputil.FindAppUtil;
import com.witskies.manager.helputil.FindDocumentsUtil;
import com.witskies.manager.helputil.GetMemory;
import com.witskies.manager.helputil.GetPhoneInformation;
import com.witskies.manager.helputil.LogUtils;
import com.witskies.manager.helputil.NetParms;
import com.witskies.manager.helputil.PhoneUtils;
import com.witskies.manager.helputil.SdCardUtil;
import com.witskies.manager.helputil.ServiceUtils;
import com.witskies.manager.helputil.ToastUtil;
import com.witskies.manager.image.MediaUtils;
import com.witskies.manager.image.MediaUtils.MediaCategory;
import com.witskies.manager.multithread.DownLoaderService;
import com.witskies.manager.service.DownloadService;
import com.witskies.manager.service.DownloadService.DownloadBinder;
import com.witskies.manager.util.ExitApk;
import com.witskies.manager.util.HttpClient;
import com.witskies.manager.util.NetworkConnected;
import com.witskies.manager.util.WitskieHttpClient;
import com.witskies.w_manager.R;

import de.greenrobot.event.EventBus;

@SuppressWarnings("deprecation")
public class MainActivity extends Activity implements View.OnClickListener {
	private final String TAG = "MainActivity";
	private TextView newsWeb_tv, pretty_picture_tv, hot_app_tv, top10_game_tv;

	private LinearLayout newsWeb, pretty_picture, hot_app, top10_game,
			w_back_A;

	private ImageView documentBt;// 文档按钮
	private ImageView videotBt;// 视频按钮
	private ImageView pictureBt;// 图片按钮
	private ImageView musicBt;// 音乐按钮
	private ImageView appBt;// 应用按钮
	private ImageView sdcardBt;// SD卡按钮 add xuzhenqin
	private TextView phoneAvailMemory;// 手机可用空间
	private TextView phoneTotalMemory;// 手机总空间
	private TextView sdAvailMemory;// 手机可用空间
	private TextView sdTotalMemory;// 手机总空间
	private TextView first_recommend;// 手机加速
	private TextView second_recommend;// 网络快传
	private TextView third_recommend;// 任务管理器
	private TextView apkCount;// 用戶apk总数量
	/** ch */
	private int currentVersionCode;
	private DownloadBinder binder;
	private boolean isBinded;

	private LoadingDialog pagerDialog;
	private Dialog utilDialog;// 工具栏Dialog
	private ImageView utilOne, utilTwo, utilThree;
	private TextView photo_tv, music_tv, video_tv, documes_tv;

	private int apkCounts;
	private ProgressBar progressApks, progressPhoto, progressMusic,
			progressDocument, progressVideo, progressbar_first_recommend,
			progressbar_second_recommend, progressbar_third_recommend;

	public static String access_key = null;
	private FindAppUtil findAllApp;

	/**
	 * 判断是否app扫描完成
	 */
	private boolean mAppOk;
	/**
	 * 文档
	 */
	private static List<String> mDocuments = null;
	/**
	 * 视频
	 */
	private static List<String> mVideos = null;
	/**
	 * 音乐
	 */
	private static List<String> mAudios = null;
	/**
	 * 图片
	 */
	private static List<String> mImages = null;
	/**
	 * 是否打开了sd卡
	 */
	private boolean mOpenSD;
	/**
	 * 热点推荐
	 */
	List<RecommendBean> mRecommends;
	/**
	 * 推荐工具
	 */
	List<ToolsBean> mTools;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.homepage);
		WitskiesApplication.getInstantiation().addActivity(MainActivity.this);

		// 初始化
		EventBus.getDefault().register(this);
		initView();
		loadMedias();
		loadRegister();
		loadApks();

	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
		}
		if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {

		}
		if (newConfig.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_NO) {
		} else if (newConfig.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_YES) {
		}

	}

	/**
	 * @描述 数据初始化
	 * @时间 2015-3-27 上午10:34:44
	 */
	private void initView() {
		sdcardBt = (ImageView) findViewById(R.id.sdcard_bt);
		documentBt = (ImageView) findViewById(R.id.homepage_documentBt);
		videotBt = (ImageView) findViewById(R.id.homepage_videoBt);
		pictureBt = (ImageView) findViewById(R.id.homepage_pictureBt);
		musicBt = (ImageView) findViewById(R.id.homepage_musicBt);
		appBt = (ImageView) findViewById(R.id.homepage_appBt);
		phoneAvailMemory = (TextView) findViewById(R.id.available_memory);
		phoneTotalMemory = (TextView) findViewById(R.id.whole_internal_memory);
		sdAvailMemory = (TextView) findViewById(R.id.external_available_memory);
		sdTotalMemory = (TextView) findViewById(R.id.whole_external_memory);
		// 热门推荐
		newsWeb = (LinearLayout) findViewById(R.id.news);
		pretty_picture = (LinearLayout) findViewById(R.id.pretty_picture);
		hot_app = (LinearLayout) findViewById(R.id.hot_app);
		top10_game = (LinearLayout) findViewById(R.id.TOP10_game);
		newsWeb_tv = (TextView) findViewById(R.id.news_tv);
		pretty_picture_tv = (TextView) findViewById(R.id.pretty_picture_tv);
		hot_app_tv = (TextView) findViewById(R.id.hot_app_tv);
		top10_game_tv = (TextView) findViewById(R.id.TOP10_game_tv);

		first_recommend = (TextView) findViewById(R.id.first_recommend);
		second_recommend = (TextView) findViewById(R.id.second_recommend);
		third_recommend = (TextView) findViewById(R.id.third_recommend);
		apkCount = (TextView) findViewById(R.id.apkCount);
		utilOne = (ImageView) findViewById(R.id.util_first);
		utilTwo = (ImageView) findViewById(R.id.util_two);
		utilThree = (ImageView) findViewById(R.id.util_three);

		photo_tv = (TextView) findViewById(R.id.photo_tv);
		music_tv = (TextView) findViewById(R.id.music_tv);
		video_tv = (TextView) findViewById(R.id.video_tv);
		documes_tv = (TextView) findViewById(R.id.documes_tv);

		progressApks = (ProgressBar) findViewById(R.id.progressbar_apks);
		progressPhoto = (ProgressBar) findViewById(R.id.progressbar_photo);
		progressMusic = (ProgressBar) findViewById(R.id.progressbar_music);
		progressVideo = (ProgressBar) findViewById(R.id.progressbar_video);
		progressDocument = (ProgressBar) findViewById(R.id.progressbar_document);

		progressbar_first_recommend = (ProgressBar) findViewById(R.id.progressbar_first_recommend);
		progressbar_second_recommend = (ProgressBar) findViewById(R.id.progressbar_second_recommend);
		progressbar_third_recommend = (ProgressBar) findViewById(R.id.progressbar_third_recommend);

		newsWeb.setOnClickListener(this);
		pretty_picture.setOnClickListener(this);
		first_recommend.setOnClickListener(this);
		second_recommend.setOnClickListener(this);
		third_recommend.setOnClickListener(this);
		pictureBt.setOnClickListener(this);
		musicBt.setOnClickListener(this);
		videotBt.setOnClickListener(this);
		documentBt.setOnClickListener(this);
		appBt.setOnClickListener(this);
		sdcardBt.setOnClickListener(this);
		// download_manage.setOnClickListener(this);
		// uninstall_manage.setOnClickListener(this);
		hot_app.setOnClickListener(this);
		top10_game.setOnClickListener(this);
		// first_layout.setOnClickListener(this);
		w_back_A = (LinearLayout) findViewById(R.id.title_forever_back);
		w_back_A.setVisibility(View.INVISIBLE);

		TextView title = (TextView) findViewById(R.id.title_forever_title);
		title.setText(MainActivity.this.getString(R.string.w_apk_name));

		pagerDialog = new LoadingDialog(MainActivity.this);
		pagerDialog.show();
		pagerDialog.setCancelable(false);

	}

	/**
	 * 网络访问接口
	 */
	private void loadRegister() {
		// 因为注册接口中有一个值在所有的后面网络访问中要用，所以先行一步
		if (NetworkConnected.isNetworkConnected(MainActivity.this)) {
			new Thread(new Runnable() {

				@Override
				public void run() {
					/** 注册接口 (默认注册,记录设备信息)与统计销售人员 */
					registerMobile();
				}
			}).start();
		} else {
			mHandler.sendEmptyMessage(10000);

		}
	}

	/**
	 * @描述 数据库查询
	 * @时间 2015-4-9 上午9:44:46
	 */
	private void loadMedias() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				// 开启等待条
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {

					mDocuments = MediaUtils.getInstance().getPaths(
							MainActivity.this, MediaCategory.Doc);
				} else {
					if (FileUtil.getAllExterSdcardPath() != null
							&& FileUtil.getAllExterSdcardPath().size() == 1) {
						FindDocumentsUtil util = new FindDocumentsUtil(FileUtil
								.getAllExterSdcardPath().get(0), null);
						mDocuments = util.mDocPaths;
					}
					if (FileUtil.getAllExterSdcardPath() != null
							&& FileUtil.getAllExterSdcardPath().size() == 2) {
						FindDocumentsUtil util = new FindDocumentsUtil(FileUtil
								.getAllExterSdcardPath().get(0), FileUtil
								.getAllExterSdcardPath().get(1));
						mDocuments = util.mDocPaths;
					}
					// 增加3.0以下的文档遍历sd
				}
				mVideos = MediaUtils.getInstance().getPaths(MainActivity.this,
						MediaCategory.Video);
				mAudios = MediaUtils.getInstance().getPaths(MainActivity.this,
						MediaCategory.Music);
				mImages = MediaUtils.getInstance().getPaths(MainActivity.this,
						MediaCategory.Picture);
				Message msg = mHandler.obtainMessage();
				mHandler.sendEmptyMessage(10004);
				mHandler.sendMessage(msg);

			}

		}).start();

	}

	/**
	 * 获取手机中的APK数量
	 */
	private void loadApks() {
		new Thread(new Runnable() {

			@Override
			public void run() {

				// 得到APP的总数量
				apkCounts = PhoneUtils.getApkCount(getApplicationContext());
				findAllApp = new FindAppUtil(MainActivity.this);
				mHandler.sendEmptyMessage(10008);
			}
		}).start();
	}

	/**
	 * 设置sd卡容量
	 * 
	 * @param getMemory
	 * @param size
	 */
	private void setMemoryView() {
		try {
			List<String> sdNumbers = SdCardUtil.getAllExterSdcardPath();
			GetMemory phoneMemory = new GetMemory(sdNumbers.get(0));
			phoneAvailMemory.setText(phoneMemory.phoneAvailSize + "G");
			phoneTotalMemory.setText(phoneMemory.phoneTotalSize + "G");

			if (sdNumbers.size() == 2) {
				// 外置sd卡
				GetMemory sdMemory = new GetMemory(sdNumbers.get(1));
				sdAvailMemory.setText(sdMemory.phoneAvailSize + "G");
				sdTotalMemory.setText(sdMemory.phoneTotalSize + "G");
			} else {
				sdAvailMemory.setText(0 + "G");
				sdTotalMemory.setText(0 + "G");
			}
		} catch (Exception e) {
		}

	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.homepage_appBt:
			if (mAppOk) {

				Intent intent = new Intent(MainActivity.this,
						SelectActivity.class);
				intent.putExtra("index", "1");
				intent.putExtra("name", getString(R.string.W_app_apps));
				startActivity(intent);
			} else {
				ToastUtil.showToast(MainActivity.this,
						getString(R.string.scan_app_wait), 1);
				return;
			}
			break;
		case R.id.homepage_pictureBt:
			Intent intent1 = new Intent(MainActivity.this, SelectActivity.class);

			intent1.putExtra("index", "2");
			intent1.putExtra("name", getString(R.string.W_app_photos));

			startActivityForResult(intent1, 185);

			break;
		case R.id.homepage_musicBt:
			Intent intent2 = new Intent(MainActivity.this, SelectActivity.class);
			intent2.putExtra("index", "3");
			intent2.putExtra("name", getString(R.string.W_app_musics));
			startActivityForResult(intent2, 185);
			break;

		case R.id.homepage_videoBt:
			Intent intent3 = new Intent(MainActivity.this, SelectActivity.class);
			intent3.putExtra("index", "4");
			intent3.putExtra("name", getString(R.string.W_app_videos));
			startActivityForResult(intent3, 185);
			break;

		case R.id.homepage_documentBt:
			Intent intent4 = new Intent(MainActivity.this, SelectActivity.class);
			intent4.putExtra("index", "5");
			intent4.putExtra("name", getString(R.string.W_app_documes));
			startActivityForResult(intent4, 185);

			break;
		case R.id.sdcard_bt:
			try {
				List<String> sdList = FileUtil.getAllExterSdcardPath();
				Intent intent5 = null;
				if (sdList != null && sdList.size() == 1) {
					intent5 = new Intent(MainActivity.this,
							FileMainActivity.class);
					intent5.putExtra("SDPATH", sdList.get(0));
				}
				if (sdList != null && sdList.size() > 1) {
					intent5 = new Intent(MainActivity.this,
							SDSelectActivity.class);
					intent5.putExtra("fragmentIndex", 4);
				}
				if (intent5 != null) {
					startActivity(intent5);
					mOpenSD = true;
				}

			} catch (Exception e) {
				ToastUtil.showToast(MainActivity.this, "error", 1);
			}
			break;
		case R.id.download_manage:
			Intent intent6 = new Intent(MainActivity.this,
					DownloadActivity.class);

			startActivity(intent6);
			break;
		case R.id.uninstall_manage:
			Intent intent7 = new Intent(MainActivity.this,
					UninstallActivity.class);

			startActivity(intent7);
			break;
		case R.id.news:

			intentEveryHere(0);

			break;
		case R.id.pretty_picture:

			intentEveryHere(1);

			break;
		case R.id.first_recommend:
			if (NetworkConnected.isNetworkConnected(getApplicationContext())) {
				if (mTools != null && mTools.size() > 0) {
					utilDialog(mTools.get(0), 0);

				} else {
					Toast.makeText(getApplicationContext(),
							getString(R.string.toast_long), Toast.LENGTH_SHORT)
							.show();
				}
			} else {
				Toast.makeText(getApplicationContext(),
						getString(R.string.toast_short), Toast.LENGTH_SHORT)
						.show();
			}

			break;
		case R.id.second_recommend:
			if (NetworkConnected.isNetworkConnected(getApplicationContext())) {
				if (mTools != null && mTools.size() > 0) {
					utilDialog(mTools.get(1), 1);
				} else {
					Toast.makeText(getApplicationContext(),
							getString(R.string.toast_long), Toast.LENGTH_SHORT)
							.show();
				}
			} else {
				Toast.makeText(getApplicationContext(),
						getString(R.string.toast_short), Toast.LENGTH_SHORT)
						.show();
			}
			break;
		case R.id.third_recommend:
			if (NetworkConnected.isNetworkConnected(getApplicationContext())) {
				if (mTools != null && mTools.size() > 0) {
					utilDialog(mTools.get(2), 2);
				} else {
					Toast.makeText(getApplicationContext(),
							getString(R.string.toast_long), Toast.LENGTH_SHORT)
							.show();
				}
			} else {
				Toast.makeText(getApplicationContext(),
						getString(R.string.toast_short), Toast.LENGTH_SHORT)
						.show();
			}
			break;
		case R.id.hot_app:
			intentEveryHere(2);// 未返回，所以用第一个
			break;
		case R.id.TOP10_game:
			intentEveryHere(3);// 未返回，所以用第二个
			break;

		default:
			break;
		}

	}

	/**
	 * 一个跳转到Activity的方法
	 * 
	 * @param getApplicationContext
	 *            () 上下文对象
	 * @param index
	 *            arrayList里面的索引号好得到要带去的网址
	 * 
	 * */
	private void intentEveryHere(int index) {
		// 改为抬起时的图片
		if (mRecommends != null) {
			if (mRecommends.size() > 0) {
				Intent intent = new Intent(MainActivity.this, WebActivity.class);
				intent.putExtra("web", mRecommends.get(index).getUrl());
				startActivity(intent);
			} else {

				Toast.makeText(getApplicationContext(),
						getString(R.string.toast_short), Toast.LENGTH_SHORT)
						.show();
			}
		} else {

			Toast.makeText(getApplicationContext(),
					getString(R.string.toast_net), Toast.LENGTH_SHORT).show();

		}
	}

	/**
	 * @描述 提示对话框，用于提醒用户是否更新APP
	 * @时间 2015-3-23 下午6:54:30
	 */
	private void showUpdateDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
		builder.setCancelable(false);
		builder.setTitle(getString(R.string.w_app_update));

		builder.setPositiveButton(getString(R.string.w_app_ok),
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

						if (isBinded) {
							getApplicationContext().unbindService(conn);
						}
						Intent it = new Intent(MainActivity.this,
								DownloadService.class);
						startService(it);
						getApplicationContext().bindService(it, conn,
								Context.BIND_AUTO_CREATE);
					}
				}).setNegativeButton(getString(R.string.w_app_no),
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});

		builder.show();

	}

	ServiceConnection conn = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
			isBinded = false;
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			binder = (DownloadBinder) service;
			// 开始下载
			isBinded = true;
			binder.start();

		}
	};

	/**
	 * @描述 从服务器获取数据（检测新版本，相关工具，热门推荐等内容）
	 * @时间 2015-4-17 下午12:02:32
	 */
	private void getDateFromService() {
		if (NetworkConnected.isNetworkConnected(MainActivity.this)) {

			new Thread(new Runnable() {
				@Override
				public void run() {

					if (NetworkConnected.isNetworkConnected(MainActivity.this)) {

						/** 启动时向服务器发送一个请求就OK(启动统计) */
						WitskieHttpClient.getInstance(MainActivity.this)
								.justAccessOneNet(Const.START_URL);

						/** 更新接口 (本app的版本升级) */
						updateApk();

						/** 访问网络分类接口 */
						networkClassification();
						/** 首页推荐 (包含 相关工具 - 热门推荐) */
						getUtilDate();

					} else {
						showNetLoad();

						Toast.makeText(getApplicationContext(),
								getString(R.string.toast_con),
								Toast.LENGTH_LONG).show();
					}
				}
			}).start();// 开程新线程网络访问
		} else {
			showNetLoad();

			Toast.makeText(getApplicationContext(),
					getString(R.string.toast_con), Toast.LENGTH_LONG).show();
		}
	}

	/**
	 * 显示三个下载地址行
	 * */
	private void showNetLoad() {
		first_recommend.setVisibility(View.VISIBLE);
		progressbar_first_recommend.setVisibility(View.GONE);

		second_recommend.setVisibility(View.VISIBLE);
		progressbar_second_recommend.setVisibility(View.GONE);

		third_recommend.setVisibility(View.VISIBLE);
		progressbar_third_recommend.setVisibility(View.GONE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 185) {
			if (resultCode == 186) {
				try {
					photo_tv.setText(mImages.size() + "");
					music_tv.setText(mAudios.size() + "");
					video_tv.setText(mVideos.size() + "");
					documes_tv.setText(mDocuments.size() + "");
				} catch (Exception e) {
					photo_tv.setText(0 + "");
					music_tv.setText(0 + "");
					video_tv.setText(0 + "");
					documes_tv.setText(0 + "");
				}

			}
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onResume() {
		super.onResume();
		/**
		 * 如果进入过SD卡，因为不知道有什么操作，所以应该查询数据库一次
		 */
		if (mOpenSD) {
			loadMedias();
			mOpenSD = false;
		}

		// Log.e("onResume", " onResume");
	}

	public void onEvent(ArrayList<AppItemInfo> mApps) {
		if (mApps != null && mApps.size() > 0) {
			apkCount.setText(mApps.size() + " ");
		}
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		killService(MainActivity.this);
		EventBus.getDefault().unregister(this);
		if (isBinded) {
			getApplicationContext().unbindService(conn);
		}
		if (binder != null) {
			// 在程序退出之前，让通知消失
			binder.cancel();
			binder.cancelNotification();

			Intent it = new Intent(this, DownloadService.class);
			getApplicationContext().stopService(it);
		}
//		if (VideoFragment.mVideos != null) {
//			VideoFragment.mVideos = null;
//		}
//		
//		if(WitskiesApplication.thumbnailImagesList != null){
//			WitskiesApplication.thumbnailImagesList = null;
//		}
		File file = new File(Environment.getExternalStorageDirectory()
				.getPath() + "/Hogan_Shipin_thumbnailImages/");
		if (file.exists()) {
			file.delete();
			Log.e("MainActivity缩略图文件删除成功", "OK");
		}

	}

	/**
	 * 杀掉下载任务
	 * 
	 * @param context
	 */
	private void killService(Context context) {

		boolean isRun = ServiceUtils.isServiceRunning(context,
				DownLoaderService.class.getName());
		if (isRun) {
			LogUtils.d(DownLoaderService.class.getName());
			stopService(new Intent(context, DownLoaderService.class));
		}
	}

	/**
	 * @描述 异步任务
	 * @时间 2015-3-23 下午5:55:26
	 */
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {

		public void handleMessage(android.os.Message msg) {

			switch (msg.what) {
			case HttpClient.WHAT_SUCCESS:
				// apk网络访问更新后UI操作
				String date = (String) msg.obj;// 访问网络后返回的数据
				// Log.e("MainActivity,apk更新返回数据：", date + " ");

				if (date != null && date.length() > 0) {

					getDateFromJson(date);

				} else {
					Toast.makeText(getApplicationContext(),
							getString(R.string.toast_failure),
							Toast.LENGTH_LONG).show();

				}
				break;
			case 10000:
				Toast.makeText(MainActivity.this,
						getString(R.string.toast_con), Toast.LENGTH_SHORT)
						.show();
				break;
			case 10001:
				// 注册接口网络访问更新后UI操作
				String keydate = (String) msg.obj;// 访问网络后返回的数据

				if (keydate != null && keydate.length() > 0) {

					JSONObject response;
					try {
						response = new JSONObject(keydate);
						if (response.getBoolean("flag")) {
							access_key = response.getString("data");
							// 其他的网络访问都要这个值access_key
							getDateFromService();

						}
					} catch (JSONException e) {
						e.printStackTrace();
						showNetLoad();
					}

				} else {
					showNetLoad();
				}

				break;

			case 10002:
				// 更新工具栏三个图标

				if (mTools != null && mTools.size() > 0) {
					updateImageView(mTools.get(0).getUrl(), utilOne);
					updateImageView(mTools.get(1).getUrl(), utilTwo);
					updateImageView(mTools.get(2).getUrl(), utilThree);
				}
				break;
			case 10003:

				break;
			case 10004:

				showMediaNumber();

				break;

			case 10005:

				updateUtil(newsWeb_tv, null, pretty_picture_tv, null,
						hot_app_tv, null, top10_game_tv, null, null,
						mRecommends);
				updateUtil(first_recommend, progressbar_first_recommend,
						second_recommend, progressbar_second_recommend,
						third_recommend, progressbar_third_recommend, null,
						null, mTools, null);

				break;
			case 10006:
				break;
			case 10007:

				break;
			case 10008:
				// 更新APK数量
				progressApks.setVisibility(View.GONE);
				apkCount.setText(String.valueOf(apkCounts));// 更新APK数量
				apkCount.setVisibility(View.VISIBLE);
				mAppOk = true;
				break;
			default:
				break;
			}
		}

	};

	/**
	 * 相关工具三行内容UI刷新
	 * 
	 * @param 三个TextView
	 *            ,无就用NULL,一个LIST
	 * */
	private void updateUtil(TextView tv1, ProgressBar pb1, TextView tv2,
			ProgressBar pb2, TextView tv3, ProgressBar pb3, TextView tv4,
			ProgressBar pb4, List<ToolsBean> tools,
			List<RecommendBean> recommends) {
		if (tools != null && tools.size() > 0) {
			for (int i = 0; i < tools.size(); i++) {
				String title = tools.get(i).getName();
				switch (i) {
				case 0:

					showTextView(tv1, pb1, title);
					break;
				case 1:

					showTextView(tv2, pb2, title);
					break;
				case 2:

					showTextView(tv3, pb3, title);
					break;
				}
			}
		}
		if (recommends != null && recommends.size() > 0) {
			for (int i = 0; i < recommends.size(); i++) {
				String title = recommends.get(i).getName();
				switch (i) {
				case 0:

					showTextView(tv1, pb1, title);
					break;
				case 1:

					showTextView(tv2, pb2, title);
					break;
				case 2:

					showTextView(tv3, pb3, title);
					break;
				case 3:

					showTextView(tv4, pb4, title);
					break;
				}

			}
		}
	}

	/**
	 * 设置图片，音乐，视频，文档的个数
	 * 
	 * @param msg
	 */
	private void showMediaNumber() {
		if (mImages == null) {
			photo_tv.setText("0");
		} else {
			photo_tv.setText(String.valueOf(mImages.size()));
		}
		photo_tv.setVisibility(View.VISIBLE);
		progressPhoto.setVisibility(View.GONE);

		if (mVideos == null) {
			video_tv.setText("0");
		} else {
			video_tv.setText(String.valueOf(mVideos.size()));
		}
		video_tv.setVisibility(View.VISIBLE);
		progressVideo.setVisibility(View.GONE);

		if (mAudios == null) {
			music_tv.setText("0");
		} else {
			music_tv.setText(String.valueOf(String.valueOf(mAudios.size())));
		}

		music_tv.setVisibility(View.VISIBLE);
		progressMusic.setVisibility(View.GONE);

		if (mDocuments == null) {
			documes_tv.setText("0");

		} else {
			documes_tv.setText(String.valueOf(mDocuments.size()));

		}

		documes_tv.setVisibility(View.VISIBLE);
		progressDocument.setVisibility(View.GONE);
		setMemoryView();
		pagerDialog.dismiss();
		if (!NetworkConnected.isNetworkConnected(MainActivity.this)) {
			showNetLoad();
		}
	}

	/**
	 * @描述 让TextView显示，ProgressBar消失
	 * @时间 2015年5月19日 上午10:10:36
	 */
	private void showTextView(TextView textView, ProgressBar progressBar,
			String title) {
		if (textView != null && textView.length() > 0) {
			textView.setText(title);
			textView.setVisibility(View.VISIBLE);
		}
		if (progressBar != null) {
			progressBar.setVisibility(View.GONE);
		}

	}

	/**
	 * 从服务器请求接口后，看是否需要更新APP
	 * */
	public void getDateFromJson(String date) {
		try {
			JSONObject response = new JSONObject(date);

			if (response.getBoolean("flag")) {
				JSONObject dataobj = response.getJSONObject("data");
				String appPath = dataobj.getString("url");
				if (appPath != null && appPath.length() > 0) {

					DownloadApk.getInstant().setUrl_apk(appPath);
					DownloadApk.getInstant().setServiceName(
							getString(R.string.w_apk_name));
				}
				showUpdateDialog();
			} else {
				// Toast.makeText(MainActivity.this, "apk暂无更新",
				// Toast.LENGTH_SHORT).show();
			}
		} catch (JSONException e) {

			e.printStackTrace();
		}

	}

	/**
	 * @描述 访问更新APK接口
	 * @时间 2015年5月15日 下午12:19:52
	 */
	private void updateApk() {
		PackageManager manager = MainActivity.this.getPackageManager();
		try {
			PackageInfo info = manager.getPackageInfo(
					MainActivity.this.getPackageName(), 0);
			currentVersionCode = info.versionCode; // 版本号
			Map<String, Object> param = new HashMap<String, Object>();
			param.put("from", PhoneUtils.getProperty(MainActivity.this, "from"));
			param.put("code", info.packageName);
			param.put("versionCode", currentVersionCode);
			param.put("sdkVersion", 8);
			HttpClient.getInstance().sendAsynRequest(Const.APPUPDATE_URL,
					HttpClient.HttpMethod.POST, param, mHandler);

		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @描述 注册接口与统计销售人员
	 * @时间 2015年5月15日 下午12:24:58
	 */
	private void registerMobile() {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		GetPhoneInformation systemInfo = new GetPhoneInformation(
				MainActivity.this);
		NetParms netParms = new NetParms();

		params = netParms.getParams(MainActivity.this, systemInfo);

		/** 注册时必须调用，用来统计销售人员 */
		WitskieHttpClient.getInstance(MainActivity.this).postToHttp(
				Const.SERVER_REGIST_MUST, params, null);
		/** 调用注册接口 */
		WitskieHttpClient.getInstance(MainActivity.this).postToHttp(
				Const.SERVER_REGIST, params, mHandler);
	}

	/**
	 * @描述 访问网络分类接口
	 * @时间 2015年5月15日 下午12:19:52
	 */
	private void networkClassification() {

		/** 归类(网络分类,把包名拼接成JSON字符串) */

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		String packages = null;
		StringBuilder builder = new StringBuilder();

		for (int i = 0; i < findAllApp.packageNames.size(); i++) {
			builder.append(findAllApp.packageNames.get(i) + ",");

		}
		// 截取多了一位 所以减2
		packages = builder.substring(0, builder.length());
		params.add(new BasicNameValuePair("packages", packages));

		String app_classify = WitskieHttpClient.getInstance(MainActivity.this)
				.postToNetClassify(Const.SERVER_CLASS, params);

		if (app_classify != null && app_classify.length() > 0) {

			JSONTokener jsonParser2 = new JSONTokener(app_classify);
			JSONObject head;

			try {

				head = (JSONObject) jsonParser2.nextValue();

				JSONObject data = head.getJSONObject("data");
				if (data != null && data.length() > 0) {

					WitskiesApplication.getInstantiation().setNetJsonObject(
							data);
				}
				// Log.e("MainActivity:网络分类数据结果：", data.toString());
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @描述 相关工具与热点推荐内容
	 * @时间 2015-3-27 上午11:37:08
	 */
	private void getUtilDate() {
		// 获取推荐信息

		String recommend = WitskieHttpClient.getInstance(MainActivity.this)
				.getHttpRecommend(Const.SERVER_RECOMMEND, MainActivity.this);

		if (recommend != null && recommend.length() > 0) {
			Log.d(TAG, recommend);
			// 利用fastjson解析并生产list集合
			com.alibaba.fastjson.JSONObject jsonObject = JSON.parseObject(
					recommend).getJSONObject("data");
			// recommend
			com.alibaba.fastjson.JSONArray recommendArray = jsonObject
					.getJSONArray(getString(R.string.request_recommend));
			mRecommends = new ArrayList<RecommendBean>();
			if (recommendArray.toString() != null) {
				mRecommends.addAll(JSON.parseArray(recommendArray.toString(),
						RecommendBean.class));

			}
			// tools
			com.alibaba.fastjson.JSONArray toolsArray = jsonObject
					.getJSONArray(getString(R.string.request_tools));
			mTools = new ArrayList<ToolsBean>();
			if (toolsArray.toString() != null) {
				mTools.addAll(JSON.parseArray(toolsArray.toString(),
						ToolsBean.class));
			}
			mHandler.obtainMessage(10005).sendToTarget();
		}
	}

	/**
	 * @描述 相关工具栏时，点击出现的Dialog
	 * @时间 2015-3-27 下午5:42:12
	 */

	@SuppressLint("InlinedApi")
	private void utilDialog(final ToolsBean tool, final int id) {
		utilDialog = new Dialog(MainActivity.this, R.style.dialog_show_style);// 自定义dialog

		View view = LayoutInflater.from(MainActivity.this).inflate(
				R.layout.alertdialog_updatemoblic, null);

		TextView alter_on = (TextView) view.findViewById(R.id.alter_on);

		TextView alter_ok = (TextView) view.findViewById(R.id.alter_ok);
		TextView alert_gift_coin = (TextView) view
				.findViewById(R.id.alert_gift_coin);
		alert_gift_coin.setText(tool.getName() + ".apk");
		alter_ok.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {

				utilDialog.dismiss();
				// // downloadFile.db
				// deleteDatabase("downloadFile.db");
				// // 开启多线程下载service
				// Intent intent = new Intent(MainActivity.this,
				// DownLoaderService.class);
				// tool.setDownLoadId(id);
				// intent.putExtra("tool", tool);
				// startService(intent);

				// 返回的数据截取APK地址
				String fileName = mTools
						.get(id)
						.getUrl()
						.substring(mTools.get(id).getUrl().lastIndexOf("/") + 1);

				String apkUrl1 = Const.START + fileName;

				if (NetworkConnected
						.isNetworkConnected(getApplicationContext())) {
					DownloadApk.getInstant().setUrl_apk(apkUrl1);
					DownloadApk.getInstant().setServiceName(
							mTools.get(id).getName());

					if (isBinded) {
						getApplicationContext().unbindService(conn);
					}
					Intent it = new Intent(MainActivity.this,
							DownloadService.class);
					startService(it);

					getApplicationContext().bindService(it, conn,
							Context.BIND_AUTO_CREATE);

				} else {
					Toast.makeText(getApplicationContext(),
							getString(R.string.toast_short), Toast.LENGTH_LONG)
							.show();
				}

			}
		});

		alter_on.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {

				utilDialog.dismiss();

			}
		});

		utilDialog.setContentView(view);
		utilDialog.show();//

		WindowManager.LayoutParams params = utilDialog.getWindow()
				.getAttributes();// 动态加载宽高
		params.width = LayoutParams.MATCH_PARENT;
		params.height = LayoutParams.WRAP_CONTENT;

		utilDialog.getWindow().setAttributes(params);//
		utilDialog.setCancelable(false);//

	}

	/**
	 * @描述 使用universal image loader下载图片
	 * @时间 2015-3-31 下午5:25:17
	 * @param imageUrl
	 *            图片网络地址
	 * @param mImageView
	 *            ImageView对像
	 */
	private void updateImageView(String imageUrl, ImageView mImageView) {
		DisplayImageOptions options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.network)
				.showImageOnFail(R.drawable.network).cacheInMemory(true)
				.cacheOnDisk(true).bitmapConfig(Bitmap.Config.RGB_565).build();

		ImageLoader.getInstance().displayImage(imageUrl, mImageView, options);
	}

	// 捕获物理后退键

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			ExitApk.extiApplication(MainActivity.this); // 调用双击退出函数
			return true;

		}

		return super.onKeyDown(keyCode, event);
	}

	public static List<String> getmDocuments() {
		return mDocuments;
	}

	public static List<String> getmVideos() {
		return mVideos;
	}

	public static List<String> getmAudios() {
		return mAudios;
	}

	public static List<String> getmImages() {
		return mImages;
	}

}
