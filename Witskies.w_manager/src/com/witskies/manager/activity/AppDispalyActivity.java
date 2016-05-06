package com.witskies.manager.activity;

import java.util.ArrayList;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.witskies.manager.adapter.AppDispalyAdapter;
import com.witskies.manager.adapter.ApplicationAdapter;
import com.witskies.manager.app.WitskiesApplication;
import com.witskies.manager.bean.AppItemInfo;
import com.witskies.manager.fragment.ApplicationFragment;
import com.witskies.manager.helputil.FindAppUtil;
import com.witskies.manager.util.Utils;
import com.witskies.w_manager.R;

/**
 * @作者 ch
 * @描述 展示对应的apks
 * @时间 2015年4月21日 下午2:52:08
 */
public class AppDispalyActivity extends Activity {
	private Intent intent;
	private GridView gridView;
	private TextView classifyName;
	private ArrayList<String> keys;
	private ArrayList<String> packageNames;
	private int position;
	private ArrayList<AppItemInfo> Apps;
	private LinearLayout mBack;

	private Context mContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.app_after_classify);
		WitskiesApplication.getInstantiation().addActivity(this);
		mContext = this.getBaseContext();

		initView();
		String key = keys.get(position);// 对应item下的key

		packageNames = ApplicationFragment.cateMap.get(key);// 对应key下所有的包名

		if (packageNames != null && packageNames.size() > 0) {
			// 获取所有包名对应的应用信息穿给适配器
			Apps = new ArrayList<AppItemInfo>();
			for (int i = 0; i < packageNames.size(); i++) {
				Apps.add(FindAppUtil.pcknameAndInfos.get(packageNames.get(i)));

			}
			AppDispalyAdapter adapter = new AppDispalyAdapter(this, Apps);
			gridView.setAdapter(adapter);
		}
	}

	private void initView() {

		intent = getIntent();
		position = intent.getIntExtra("position", 0);
		keys = ApplicationFragment.keys;
		gridView = (GridView) findViewById(R.id.App_gridview);

		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				String packageName = packageNames.get(arg2);
				Utils.startApp(mContext, packageName);
			}
		});
		classifyName = (TextView) findViewById(R.id.title_forever_title);
		String language = Locale.getDefault().getLanguage();
		String titleName = keys.get(position);
		if (language.equals("zh") && titleName != null) {
			titleName = ApplicationAdapter.changeLanguage(titleName);
		}

		classifyName.setText(titleName);
		mBack = (LinearLayout) findViewById(R.id.title_forever_back);
		mBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				AppDispalyActivity.this.finish();
			}
		});

	}

}
