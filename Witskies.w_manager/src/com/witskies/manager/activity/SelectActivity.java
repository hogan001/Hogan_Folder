package com.witskies.manager.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.witskies.manager.app.WitskiesApplication;
import com.witskies.manager.fragment.ApplicationFragment;
import com.witskies.manager.fragment.DocumentFragment;
import com.witskies.manager.fragment.ImageFragment;
import com.witskies.manager.fragment.MusicFragment;
import com.witskies.manager.fragment.VideoFragment;
import com.witskies.w_manager.R;

/**
 * @作者 ch
 * @描述 根据传过来的序号，打开相应的Fragment
 * @时间 2015-3-30 下午3:28:36
 */
public class SelectActivity extends FragmentActivity {
	private FragmentManager fragmentManager;
	private FragmentTransaction fragmentTransaction;
	private TextView title;
	private LinearLayout w_back;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_select);
		WitskiesApplication.getInstantiation().addActivity(this);
		title = (TextView) findViewById(R.id.title_forever_title);
		fragmentManager = getSupportFragmentManager();
		fragmentTransaction = fragmentManager.beginTransaction();

		Intent intent = getIntent();
		String index = intent.getStringExtra("index");

		// 然后，你能够使用add()方法把Fragment添加到指定的视图中
		getFragment(Integer.parseInt(index));

		title.setText(intent.getStringExtra("name"));

		w_back = (LinearLayout) findViewById(R.id.title_forever_back);

		w_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent intent = new Intent(SelectActivity.this, MainActivity.class);
				setResult(186, intent);
				SelectActivity.this.finish();
			}
		});

	}

	/**
	 * @描述 根据传来的index,选择相应的Fragment
	 * @时间 2015-3-30 下午4:04:48
	 */
	private void getFragment(int index) {
		switch (index) {
		case 1:
			ApplicationFragment appsFragment = new ApplicationFragment();
			fragmentTransaction.replace(R.id.fragment_con, appsFragment);
			fragmentTransaction.commit();
			break;
		case 2:
			ImageFragment mFragment1 = new ImageFragment();
			fragmentTransaction.replace(R.id.fragment_con, mFragment1);
			fragmentTransaction.commit();

			break;
		case 3:
			MusicFragment mFragment2 = new MusicFragment();
			fragmentTransaction.replace(R.id.fragment_con, mFragment2);
			fragmentTransaction.commit();

			break;
		case 4:
			VideoFragment mFragment3 = new VideoFragment();
			fragmentTransaction.replace(R.id.fragment_con, mFragment3);
			fragmentTransaction.commit();
			break;
		case 5:
			DocumentFragment mFragment4 = new DocumentFragment();
			fragmentTransaction.replace(R.id.fragment_con, mFragment4);
			fragmentTransaction.commit();

			break;

		default:
			break;
		}

	}

	@Override
	protected void onDestroy() {
		if (ImageLoader.getInstance() != null) {
			ImageLoader.getInstance().clearMemoryCache();
		}
		super.onDestroy();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent intent = new Intent(SelectActivity.this, MainActivity.class);
			setResult(186, intent);
			SelectActivity.this.finish();
		}

		return super.onKeyDown(keyCode, event);
	}

}
