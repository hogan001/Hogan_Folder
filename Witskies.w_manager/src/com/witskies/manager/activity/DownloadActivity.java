package com.witskies.manager.activity;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.witskies.manager.adapter.DownloadAdapter;
import com.witskies.manager.app.WitskiesApplication;
import com.witskies.manager.bean.ApkBean;
import com.witskies.manager.util.ExitApk;
import com.witskies.w_manager.R;

public class DownloadActivity extends Activity implements OnItemClickListener {
	private ListView mListView;
	private LinearLayout mBack;
	private DownloadAdapter mAdapter;
	private ArrayList<ApkBean> mApks;
	/**
	 * 加载页和空页
	 */
	private LinearLayout mLoadingView;
	private LinearLayout mEmptyView;
	/**
	 * 记录上次点击
	 */
	private int mLastPosition;

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				showLoading(false);
				if (mApks != null && mApks.size() > 0) {
					mAdapter = new DownloadAdapter(DownloadActivity.this, mApks);
					mListView.setAdapter(mAdapter);
				} else {
					showEmpty(true);
				}
				break;
			case 2:
				if (mAdapter.getmApks() != null) {
					if (mAdapter.getmApks().size() == 0) {
						showEmpty(true);
					}
				}

				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.download_manage);
		WitskiesApplication.getInstantiation().addActivity(this);
		setView();
	}

	private void setView() {
		mLoadingView = (LinearLayout) findViewById(R.id.layout_loading_view);
		mEmptyView = (LinearLayout) findViewById(R.id.layout_empty_view);
		TextView title = (TextView) findViewById(R.id.title_forever_title);
		title.setText(getString(R.string.W_manager_bot_two));
		mBack = (LinearLayout) findViewById(R.id.title_forever_back);
		mBack.setVisibility(View.INVISIBLE);
		mListView = (ListView) findViewById(R.id.download_manage_listView);
		mListView.setOnItemClickListener(this);

	}

	@Override
	protected void onResume() {
		loadApks();
		super.onResume();
	}

	private void loadApks() {
		showLoading(true);
		showEmpty(false);  
		// 数据填充
		new Thread(new Runnable() {

			@Override
			public void run() {
				String path = Environment.getExternalStorageDirectory().getPath()
						+ "/Download_From_W_Apk/";
				mApks = new ArrayList<ApkBean>();
				mApks = getApkInfo(getsdCardFile(path));
				Message message = mHandler.obtainMessage();
				message.what = 1;
				mHandler.sendMessage(message);
			}
		}).start();
	}

	private void showEmpty(boolean show) {
		mEmptyView.setVisibility(show ? View.VISIBLE : View.GONE);
	}

	private void showLoading(boolean show) {
		mLoadingView.setVisibility(show ? View.VISIBLE : View.GONE);
	}

	/**
	 * 扫描文件夹下的apk
	 * 
	 * @param sdPath2
	 * @return
	 */
	private ArrayList<String> getsdCardFile(String sdPath2) {
		ArrayList<String> paks = new ArrayList<String>();
		File file = new File(sdPath2);
		if (!file.exists()) {
			return null;
		}
		File[] files = file.listFiles();
		if (files != null) {
			for (int i = 0; i < files.length; i++) {
				File f = files[i];
				if (f.isFile()) {
					String end = f.getPath().substring(f.getPath().lastIndexOf(".") + 1);
					if (end.equals("apk")) {
						paks.add(f.getAbsolutePath());
					}

				} else if (f.isDirectory() && f.getPath().indexOf("/.") == -1) {
					getsdCardFile(f.getPath());
				}
			}
		}
		return paks;

	}

	/**
	 * @描述 获取下载包名信息
	 * @时间 2015-4-10 上午10:14:08
	 */

	private ArrayList<ApkBean> getApkInfo(ArrayList<String> filePaths) {
		try {
			ArrayList<ApkBean> apks = new ArrayList<ApkBean>();
			PackageManager pm = getPackageManager();
			if (filePaths != null) {
				for (String path : filePaths) {
					PackageInfo info = pm
							.getPackageArchiveInfo(path, PackageManager.GET_ACTIVITIES);
					ApkBean apk = new ApkBean();
					if (info != null) {
						ApplicationInfo appInfo = info.applicationInfo;
						String version = info.versionName;
						Drawable icon = pm.getApplicationIcon(appInfo);
						String filename = path.substring(path.lastIndexOf("/") + 1);
						File file = new File(path);
						long fileSize1 = file.length();
						DecimalFormat df = new DecimalFormat("0.00");
						float fileSize2 = (float) fileSize1 / (1024 * 1024);
						fileSize2 = Float.parseFloat(df.format(fileSize2));
						apk.setName(filename);
						apk.setIcon(icon);
						apk.setSize(fileSize2);
						apk.setVersion(version);
						apk.setPath(path);
						apks.add(apk);
					}
				}
				return apks;
			}
		} catch (Exception e) {
		}
		return null;

	}

	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			ExitApk.extiApplication(DownloadActivity.this); // 调用双击退出函数
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View view, int position, long arg3) {
		changeHideMenu(view, position);
	}

	/**
	 * 改变隐藏菜单，以前的方法效率低
	 * 
	 * @param view
	 * @param position
	 */
	private void changeHideMenu(View view, final int position) {
		View hideMenu = (LinearLayout) view.findViewById(R.id.open_list);
		LinearLayout dele = (LinearLayout) hideMenu.findViewById(R.id.click_delete);
		dele.setOnClickListener(new MyBtClick(position));
		LinearLayout instal = (LinearLayout) hideMenu.findViewById(R.id.click_install);
		instal.setOnClickListener(new MyBtClick(position));
		hideMenu.setVisibility(hideMenu.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
		if (hideMenu.getVisibility() == View.VISIBLE) {
			mAdapter.getmHideMap().put(position, true);
		} else {
			mAdapter.getmHideMap().put(position, false);
		}
		if (mLastPosition != position) {
			mAdapter.getmHideMap().put(mLastPosition, false);
		}
		mLastPosition = position;
		mAdapter.notifyDataSetChanged();
	}

	private class MyBtClick implements OnClickListener {
		int position;

		@SuppressWarnings("unused")
		public MyBtClick() {

		}

		public MyBtClick(int position) {
			this.position = position;
		}

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.click_delete:
				deleteApks();

				break;
			case R.id.click_install:
				instalAPks();
				break;
			case R.id.title_forever_back:
				DownloadActivity.this.finish();
				break;
			}
		}

		/**
		 * 安装apk
		 */
		private void instalAPks() {
			try {
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setDataAndType(
						Uri.fromFile(new File(mAdapter.getmApks().get(position).getPath())),
						"application/vnd.android.package-archive");
				startActivity(intent);

			} catch (Exception e) {
			}
		}

		/**
		 * 删除apk
		 */
		private void deleteApks() {
			try {

				AlertDialog.Builder builder = new Builder(DownloadActivity.this);
				builder.setTitle(R.string.delete_or_not);
				builder.setCancelable(false);
				builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						try {
							String apkPath = mAdapter.getmApks().get(position).getPath();
							File file = new File(apkPath);
							if (file.exists()) {
								file.delete();
								mAdapter.getmApks().remove(position);
								mAdapter.restMap();
								mAdapter.notifyDataSetChanged();
								Message message = mHandler.obtainMessage();
								message.what = 2;
								mHandler.sendMessage(message);
							}
						} catch (Exception e) {
							return;
						}
					}
				});
				builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

					}
				});

				builder.create().show();
			} catch (Exception e) {
			}
		}
	}
}
