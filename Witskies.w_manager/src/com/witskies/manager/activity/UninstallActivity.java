package com.witskies.manager.activity;

import java.lang.reflect.Method;
import java.util.ArrayList;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.witskies.manager.adapter.UninstallAdapter;
import com.witskies.manager.adapter.UninstallAdapter.BtnListener;
import com.witskies.manager.app.WitskiesApplication;
import com.witskies.manager.bean.AppItemInfo;
import com.witskies.manager.dialog.LoadingDialog;
import com.witskies.manager.helputil.FindAppUtil;
import com.witskies.manager.util.ExitApk;
import com.witskies.w_manager.R;

import de.greenrobot.event.EventBus;

/**
 * @作者 ch
 * @描述 应用卸载类
 * @时间 2015年5月11日 下午3:59:41
 */
public class UninstallActivity extends Activity {
	public static ListView listView;
	private LinearLayout w_back;
	private UninstallAdapter mUninstallAdapter;
	// private LoadingDialog dialog;
	/**
	 * 点击标识
	 */
	private MyInstalledReceiver mMyInstalledReceiver;

	/**
	 * app列表集合
	 */
	private ArrayList<AppItemInfo> mApps;
	private ArrayList<String> codeSizeList = new ArrayList<String>();// 应用程序大小
	/**
	 * 加载页和空页
	 */
	private LinearLayout mLoadingView;
	private LinearLayout mEmptyView;
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1000:
				// 这里注意下，必须把值赋过去，因为自定义的adpter.notifyDataSetChanged()这个方法刷新的是绑定的list
				mUninstallAdapter.UserApps = mApps;
				if (mApps != null && mApps.size() > 0) {
					for (AppItemInfo codeSize : mApps) {
						try {
							queryPacakgeSize(codeSize.getPackageName());

						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				} else {
					showEmpty(true);
				}
				break;
			case 113:
				refreshList(msg);

				break;
			case 1001:
				showLoading(false);
				mUninstallAdapter.notifyDataSetChanged();

			default:
				break;
			}
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.uninstall_manage);
		setView();
		getAppsList();
	}

	private void setView() {
		WitskiesApplication.getInstantiation().addActivity(this);
		TextView title = (TextView) findViewById(R.id.title_forever_title);
		title.setText(getString(R.string.W_manager_bot_three));
		mLoadingView = (LinearLayout) findViewById(R.id.layout_loading_view);
		mEmptyView = (LinearLayout) findViewById(R.id.layout_empty_view);
		listView = (ListView) findViewById(R.id.uninstall_manage_listView);
		mApps = new ArrayList<AppItemInfo>();

		mUninstallAdapter = new UninstallAdapter(UninstallActivity.this, mApps, codeSizeList, mBtnListener);
		listView.setAdapter(mUninstallAdapter);
		w_back = (LinearLayout) findViewById(R.id.title_forever_back);
		w_back.setVisibility(View.INVISIBLE);

	}

	private void showEmpty(boolean show) {
		mEmptyView.setVisibility(show ? View.VISIBLE : View.GONE);
	}

	private void showLoading(boolean show) {
		mLoadingView.setVisibility(show ? View.VISIBLE : View.GONE);
	}

	/**
	 * 获取apk信息
	 */
	public void getAppsList() {
		showLoading(true);
		showEmpty(false);
		new Thread() {
			public void run() {
				FindAppUtil allApp = new FindAppUtil(UninstallActivity.this);

				mApps = allApp.getUserAppIemInfo();

				mHandler.sendEmptyMessage(1000);
			};

		}.start();

	}

	@Override
	protected void onStart() {
		super.onStart();
		registerR(UninstallActivity.this);
	}

	/**
	 * 注销卸载监听广播
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		unRegisterR(UninstallActivity.this);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			ExitApk.extiApplication(UninstallActivity.this); // 调用双击退出函数
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}

	/**
	 * @描述 调用反射获取应用的大小
	 * @时间 2015年4月12日 下午3:53:13
	 */
	private void queryPacakgeSize(String packageName) throws Exception {
		Log.i("调用反射1", "已经调用了");
		if (packageName != null) {
			// 使用放射机制得到PackageManager类的隐藏函数getPackageSizeInfo
			PackageManager pm = getPackageManager(); // 得到pm对象
			try {
				// 通过反射机制获得该隐藏函数

				Method getPackageSizeInfo = pm.getClass().getMethod("getPackageSizeInfo", new Class[] { String.class, IPackageStatsObserver.class });

				// 调用该函数，并且给其分配参数 ，待调用流程完成后会回调PkgSizeObserver类的函数
				Log.i("调用反射2", "已经调用了");
				getPackageSizeInfo.invoke(pm, packageName, new PkgSizeObserver());
				Log.i("调用反射3", "已经调用了");
			} catch (Exception ex) {
				Log.e("调用反射", "NoSuchMethodException>>" + ex.toString());
				ex.printStackTrace();

			}
		}
	}

	// aidl文件形成的Bindler机制服务类
	public class PkgSizeObserver extends IPackageStatsObserver.Stub {
		/***
		 * 回调函数，
		 * 
		 * @param pStatus
		 *            ,返回数据封装在PackageStats对象中
		 * @param succeeded
		 *            代表回调成功
		 */
		@Override
		public void onGetStatsCompleted(PackageStats pStats, boolean succeeded) throws RemoteException {
			Log.i("调用反射", "已经调用了");

			long codesize = pStats.codeSize; // 应用程序大小
			if (codesize != 0) {
				codeSizeList.add(codesize + "");
			}
			if (codeSizeList.size() == mApps.size()) {
				mHandler.sendEmptyMessage(1001);
				Log.e("codeSizeList", codeSizeList.toString());
			}

		}
	}

	/**
	 * 注销卸载监听广播
	 * 
	 * @param context
	 */
	public void unRegisterR(Context context) {
		if (mMyInstalledReceiver != null) {
			context.unregisterReceiver(mMyInstalledReceiver);
		}
	}

	/**
	 * 注册卸载监听广播
	 * 
	 * @param context
	 */
	public void registerR(Context context) {
		mMyInstalledReceiver = new MyInstalledReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction("android.intent.action.PACKAGE_REMOVED");
		filter.addDataScheme("package");
		context.registerReceiver(mMyInstalledReceiver, filter);
	}

	/**
	 * 卸载监听
	 * 
	 * @author Administrator
	 * 
	 */
	public class MyInstalledReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {

			if (intent.getAction().equals("android.intent.action.PACKAGE_REMOVED")) {
				String packageName = intent.getData().getSchemeSpecificPart();
				Message message = mHandler.obtainMessage();
				message.what = 113;
				message.obj = packageName;
				mHandler.sendMessage(message);
			}
		}
	}

	/**
	 * 刷新listview
	 * 
	 * @param msg
	 */
	private void refreshList(android.os.Message msg) {
		try {
			for (int i = 0; i < mUninstallAdapter.getUserApps().size(); i++) {
				if (mUninstallAdapter.getUserApps().get(i).getPackageName().equals(msg.obj)) {
					mUninstallAdapter.getUserApps().remove(i);
					mUninstallAdapter.getCodeSizeList().remove(i);

				}
			}
			mUninstallAdapter.notifyDataSetChanged();

			EventBus.getDefault().post(mUninstallAdapter.getUserApps());

		} catch (Exception e) {
		}
	};

	private BtnListener mBtnListener = new BtnListener() {

		@Override
		public void onClick(int position, View view) {
			Uri packageURI = Uri.parse("package:" + mUninstallAdapter.getUserApps().get(position).getPackageName());
			Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageURI);
			startActivity(uninstallIntent);
		}
	};
}
