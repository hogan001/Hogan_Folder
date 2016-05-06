package com.witskies.manager.activity;

import android.app.ActivityGroup;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;

import com.witskies.manager.app.WitskiesApplication;
import com.witskies.manager.multithread.DownLoaderService;
import com.witskies.manager.widget.badgeview.BadgeView;
import com.witskies.w_manager.R;

@SuppressWarnings("deprecation")
public class TabHostActivity extends ActivityGroup {
	private TabHost mTabHost;
	private TextView tabhost_pager, tabhost_download, tabhost_uninstall;
	/**
	 * 下载service发送的广播
	 */
	private MsgReceiver msgReceiver;
	public Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 2:
				downCount--;
				badgeView.setBadgeCount(downCount);
				break;

			case 1:
				downCount++;
				badgeView.setBadgeCount(downCount);
				break;
			}
			if (downCount == 0) {
				badgeView.setVisibility(View.GONE);
			} else {
				badgeView.setVisibility(View.VISIBLE);
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.tab_host_activity_main);
		WitskiesApplication.getInstantiation().addActivity(TabHostActivity.this);
		initTabs();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
	}

	View downloadView;
	BadgeView badgeView;
	int downCount;

	private void initTabs() {
		// // 动态注册广播接收器
		msgReceiver = new MsgReceiver();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(DownLoaderService.START_DOWNLOAD);
		intentFilter.addAction(DownLoaderService.FINISHED_DOWNLOAD);
		registerReceiver(msgReceiver, intentFilter);
		
		
		mTabHost = (TabHost) findViewById(R.id.tabhost);
		mTabHost.setup(this.getLocalActivityManager());

		View pagerView = this.getLayoutInflater().inflate(R.layout.tabhost_pager, null);
		tabhost_pager = (TextView) pagerView.findViewById(R.id.tabhost_pager);
		Intent intent = new Intent(this, MainActivity.class);
		mTabHost.addTab(mTabHost.newTabSpec("Pager").setIndicator(pagerView).setContent(intent));

		downloadView = this.getLayoutInflater().inflate(R.layout.tabhost_download, null);
		tabhost_download = (TextView) downloadView.findViewById(R.id.tabhost_download);
		intent = new Intent(this, DownloadActivity.class);
		mTabHost.addTab(mTabHost.newTabSpec("DownLoad").setIndicator(downloadView)
				.setContent(intent));

		View uninstallView = this.getLayoutInflater().inflate(R.layout.tabhost_uninstall, null);
		tabhost_uninstall = (TextView) uninstallView.findViewById(R.id.tabhost_uninstall);
		intent = new Intent(this, UninstallActivity.class);
		mTabHost.addTab(mTabHost.newTabSpec("Uninstall").setIndicator(uninstallView)
				.setContent(intent));
		badgeView = new BadgeView(TabHostActivity.this);
		badgeView.setTargetView(downloadView);
		// intent = new Intent(this, MoreActivity.class);
		// mTabHost.addTab(mTabHost.newTabSpec("Tab4")
		// .setIndicator(getString(R.string.more_indicator),
		// getResources().getDrawable(R.drawable.more_indicator_selector))
		// .setContent(intent));

		mTabHost.setOnTabChangedListener(new OnTabChangeListener() {

			@Override
			public void onTabChanged(String tabId) {
				// TODO Auto-generated method stub;
				Log.e("OK", tabId);
				if (tabId.equals("Pager")) {

					tabhost_pager.setTextColor(Color.parseColor("#58a7f8"));
					tabhost_download.setTextColor(Color.parseColor("#a5a5a5"));
					tabhost_uninstall.setTextColor(Color.parseColor("#a5a5a5"));
				} else if (tabId.equals("DownLoad")) {
					tabhost_pager.setTextColor(Color.parseColor("#a5a5a5"));
					tabhost_download.setTextColor(Color.parseColor("#58a7f8"));
					tabhost_uninstall.setTextColor(Color.parseColor("#a5a5a5"));
				}

				else if (tabId.equals("Uninstall")) {
					tabhost_pager.setTextColor(Color.parseColor("#a5a5a5"));
					tabhost_download.setTextColor(Color.parseColor("#a5a5a5"));
					tabhost_uninstall.setTextColor(Color.parseColor("#58a7f8"));
				}

			}
		});

	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onPause() {
		super.onPause();

		Log.e("TonPause", " onPause");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(msgReceiver);
		Intent intent = new Intent(DownLoaderService.STOP_DOWNLOAD);
		sendBroadcast(intent);
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
			if (intent.getAction().equals(DownLoaderService.FINISHED_DOWNLOAD)) {
				mHandler.obtainMessage(2).sendToTarget();
			} else if (intent.getAction().equals(DownLoaderService.START_DOWNLOAD)) {
				mHandler.obtainMessage(1).sendToTarget();
			}
		}
	}
}
