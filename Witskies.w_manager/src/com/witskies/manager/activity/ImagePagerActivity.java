package com.witskies.manager.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.witskies.manager.app.WitskiesApplication;
import com.witskies.manager.bean.AdapterItemBean;
import com.witskies.manager.image.ImagePagerAdpter;
import com.witskies.manager.util.AnalyticalData;
import com.witskies.w_manager.R;

/**
 * 图片查看类 2015年4月21日15:43:38
 * 
 * @author lance
 * 
 */
public class ImagePagerActivity extends Activity {

	public static final int INDEX = 1;

	private ViewPager mViewPager;
	private List<String> mImageUris;
	private ImagePagerAdpter mAdpter;
	private LinearLayout title_forever_back;
	private List<String> mImageName;
	private TextView mTitleView;
	private String mTitle;
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			mTitleView.setText(mTitle);
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_image_pager);
		WitskiesApplication.getInstantiation().addActivity(ImagePagerActivity.this);
		String title = getIntent().getStringExtra("title");
		if (title != null) {
			mTitleView = (TextView) findViewById(R.id.title_forever_title);
			mTitleView.setText(title);
		}

		title_forever_back = (LinearLayout) findViewById(R.id.title_forever_back);
		title_forever_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ImagePagerActivity.this.finish();
			}
		});

		mImageUris = new ArrayList<String>();
		mImageName = new ArrayList<String>();
		try {
			List<AdapterItemBean> list = AnalyticalData.getInstance().anlyticalDataToNeed(MainActivity.getmImages());
			for (int i = 0; i < list.size(); i++) {
				mImageUris.add(list.get(i).getCurrPath());
				if (list.get(i).getName() != null) {
					mImageName.add(list.get(i).getName());
				} else {
					mImageName.add("un kown");
				}

			}

		} catch (Exception e) {
		}

		mViewPager = (ViewPager) findViewById(R.id.item_image_pager_pager);
		mViewPager.setOffscreenPageLimit(2);
		mAdpter = new ImagePagerAdpter(getApplicationContext(), mImageUris);
		mViewPager.setAdapter(mAdpter);
		mViewPager.setCurrentItem(getIntent().getIntExtra(WitskiesApplication.IMAGE_POSITION, 0));
		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				mTitle = mImageName.get(arg0);
				mHandler.sendEmptyMessage(0);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});

	}

}