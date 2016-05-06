package com.witskies.manager.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.witskies.manager.app.WitskiesApplication;
import com.witskies.manager.fileexplorer.FileMainActivity;
import com.witskies.manager.helputil.FileUtil;
import com.witskies.manager.helputil.ToastUtil;
import com.witskies.w_manager.R;

/**
 * sd卡多路径选择界面
 * 
 * @author Clance
 * 
 */
public class SDSelectActivity extends Activity {

	private ListView mListView;
	/**
	 * 选择路径
	 */
	private String mPath;
	/**
	 * 外置sd集合
	 */
	private List<String> mSDPaths;
	private List<String> mSDNames;
	private LinearLayout mEmpty;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_sdselect);
		WitskiesApplication.getInstantiation().addActivity(this);
		initViews();
		super.onCreate(savedInstanceState);
	}

	private void initViews() {
		LinearLayout back = (LinearLayout) findViewById(R.id.title_forever_back);
		TextView title = (TextView) findViewById(R.id.title_forever_title);
		title.setText("SD");
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		mEmpty = (LinearLayout) findViewById(R.id.activity_sdselect_empty);
		mListView = (ListView) findViewById(R.id.activity_sdselect_listview);
		mSDPaths = new ArrayList<String>();
		mSDNames = new ArrayList<String>();
		try {
			mSDPaths = FileUtil.getAllExterSdcardPath();
			for (int i = 0; i < FileUtil.getAllExterSdcardPath().size(); i++) {
				if (i == 0) {
					mSDNames.add(getString(R.string.sd_internal));
				} else {
					mSDNames.add(getString(R.string.sd_external) + i);
				}
			}
			if (mSDNames.size() == 0) {
				mEmpty.setVisibility(View.VISIBLE);
				return;
			}
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
					R.layout.item_sdselect_paths, R.id.item_sdselect_paths_text, mSDNames);
			mListView.setAdapter(adapter);
			mListView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
					try {
						mPath = mSDPaths.get(arg2);
						if (!TextUtils.isEmpty(mPath)) {
							Intent intent = new Intent(SDSelectActivity.this,
									FileMainActivity.class);
							intent.putExtra("SDPATH", mPath);
							startActivity(intent);
						}

					} catch (Exception e) {
						ToastUtil.showToast(SDSelectActivity.this, "error", 1);
					}
				}
			});
		} catch (Exception e) {
			ToastUtil.showToast(SDSelectActivity.this, "error", 1);
		}

	}
}
