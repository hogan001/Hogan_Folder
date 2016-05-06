package com.witskies.manager.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import com.witskies.manager.app.WitskiesApplication;
import com.witskies.w_manager.R;

public class WebActivity extends Activity {
	private WebView wv;

	private LinearLayout mLoadingView;

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.web);
		mLoadingView = (LinearLayout) findViewById(R.id.layout_loading_view);
		WitskiesApplication.getInstantiation().addActivity(this);
		/*
		 * 代码直接创建webview控件 wv = new WebView(this);
		 */
		wv = (WebView) this.findViewById(R.id.myweb);
		WebSettings ws = wv.getSettings();
		// 设定WebView可以执行JavaScript脚本
		ws.setJavaScriptEnabled(true);
		// 加载需要显示的网页
		Intent intent = getIntent();
		String link = intent.getStringExtra("web");

		wv.loadUrl(link);

		/*
		 * 如果希望点击链接继续在当前browser中响应， 而不是新开Android的系统browser中响应该链接， 必须覆盖
		 * webview的WebViewClient对象。
		 */
		wv.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
				showLoading(true);
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				showLoading(false);
			}
		});

	}

	private void showLoading(boolean show) {
		mLoadingView.setVisibility(show ? View.VISIBLE : View.GONE);
	}

	/*
	 * 设置回退 如果不做任何处理，浏览网页，点击系统“Back”键， 整个Browser会调用finish()而结束自身， 如果希望浏览的网
	 * 页回退而不是推出浏览器，需要在当前Activity中处理并消费掉该Back事件。
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {

			// Intent intent = new Intent(WebActivity.this, MainActivity.class);
			// startActivity(intent);
			WebActivity.this.finish();
			return true;
		} else {
			WebActivity.this.finish();
		}
		return false;
	}

}
