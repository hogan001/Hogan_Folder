package com.witskies.manager.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.witskies.manager.activity.AppDispalyActivity;
import com.witskies.manager.adapter.ApplicationAdapter;
import com.witskies.manager.app.WitskiesApplication;
import com.witskies.manager.bean.AppItemInfo;
import com.witskies.manager.helputil.FindAppUtil;
import com.witskies.manager.helputil.PhoneUtils;
import com.witskies.manager.util.NetworkConnected;
import com.witskies.w_manager.R;

/**
 * *
 * 
 * @作者 ch
 * @描述 这个类是专门用于应用分类的
 * @时间 2015-3-27 下午2:43:45
 */
public class ApplicationFragment extends Fragment {

	private GridView gridView;

	private ApplicationAdapter appsAdapter;

	/**
	 * @内容 生成一个文件夹图标Map
	 * @说明 String 代表图标的名字（全部应用）
	 * @说明 ArrayList<String> 放的全是这个文件夹里面的apk名字
	 * 
	 **/
	public static Map<String, ArrayList<String>> cateMap;
	/**
	 * @内容 所有文件夹名字ArrayList
	 * @说明 String 生成的文件夹名字
	 * 
	 **/
	public static ArrayList<String> keys;

	private ProgressBar mpProgressBar;
	private Context mContext;
	private JSONObject netJsonObject;
	private JSONObject localData;

	public ApplicationFragment() {

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mContext = getActivity();
		View view = null;
		view = inflater.inflate(R.layout.app_fragment, null);
		mpProgressBar = (ProgressBar) view.findViewById(R.id.progressbar_un);
		gridView = (GridView) view.findViewById(R.id.App_gridview);
		addlistener();

		new Thread(new Runnable() {

			@Override
			public void run() {
				setupView();
			}
		}).start();

		return view;
	}

	private void addlistener() {

		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(mContext, AppDispalyActivity.class);
				intent.putExtra("position", position);
				startActivity(intent);
			}
		});
	}

	private void setupView() {

		boolean isInternet = NetworkConnected.isNetworkConnected(mContext);

		if (isInternet) {
			netJsonObject = WitskiesApplication.getInstantiation()
					.getNetJsonObject();

			if (netJsonObject != null && netJsonObject.length() > 0) {
				appClassIfication(netJsonObject);
				Log.e("AppsFragment,APP网络分类：", netJsonObject.toString()
						+ ">>防止为空");
			} else {
				localData = PhoneUtils.getCategory(mContext);
				if (localData != null && localData.length() > 1) {
					appClassIfication(localData);
				} else {
					Toast.makeText(mContext,
							mContext.getString(R.string.w_app_fragments),
							Toast.LENGTH_SHORT).show();
				}
			}
		} else {
			localData = PhoneUtils.getCategory(mContext);
			if (localData != null && localData.length() > 0) {
				appClassIfication(localData);
			} else {
				Toast.makeText(mContext,
						mContext.getString(R.string.w_app_fragments),
						Toast.LENGTH_SHORT).show();
			}

		}
	}

	/** APP分类方法 */
	private void appClassIfication(JSONObject data) {
		int count = data.length();

		cateMap = new HashMap<String, ArrayList<String>>();
		keys = new ArrayList<String>();

		// 得到所有应用名，跟返回的类型作比较，生成相应的文件夹名字
		ArrayList<String> packageNames = getUserApps();

		for (int i = 0; i < count; i++) {
			String key;
			try {
				// 本地或服务器返回的一个类别名
				key = data.names().getString(i);
				ArrayList<String> list = new ArrayList<String>();

				JSONArray jsonArray = data.getJSONArray(key);
				// 这个类别下面有有多少个类名的长度
				int keyCounts = jsonArray.length();
				// 循环去拿每个类名去作比较，符合要求加入到list中
				for (int j = 0; j < keyCounts; j++) {

					String keyName = jsonArray.get(j).toString();
					for (String pName : packageNames) {
						if (keyName.equals(pName)) {
							list.add(pName);

						}

					}
				}

				if (!list.isEmpty()) {
					keys.add(key);
					cateMap.put(key, list);
					// 放入后清空数据
					for (String str : list) {
						packageNames.remove(str);

					}

				}

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		// 得到所有的应用名字,放在一个叫《全部就用》的文件夹下
		ArrayList<String> allPackages = getUserApps();
		cateMap.put(mContext.getString(R.string.w_apk_all), allPackages);

		keys.add(mContext.getString(R.string.w_apk_all));

		mhHandler.sendEmptyMessage(100);

	}

	private Handler mhHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 100:
				mpProgressBar.setVisibility(View.GONE);
				appsAdapter = new ApplicationAdapter(mContext, keys);
				gridView.setAdapter(appsAdapter);
				break;

			default:
				break;
			}
		};
	};

	/** 获取手机（用户装的软件）的APP包名 */

	public ArrayList<String> getUserApps() {

		ArrayList<String> userAppName = new ArrayList<String>();

		for (int i = 0; i < FindAppUtil.UserApps.size(); i++) {
			PackageInfo pinfo = FindAppUtil.UserApps.get(i);
			AppItemInfo appItemInfo = new AppItemInfo();
			appItemInfo.setPackageName(pinfo.applicationInfo.packageName);
			userAppName.add(pinfo.applicationInfo.packageName);

		}
		return userAppName;
	}

}
