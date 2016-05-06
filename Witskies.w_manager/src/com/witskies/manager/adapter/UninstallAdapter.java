package com.witskies.manager.adapter;

import java.text.DecimalFormat;
import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.witskies.manager.bean.AppItemInfo;
import com.witskies.w_manager.R;

public class UninstallAdapter extends BaseAdapter {
	public ArrayList<AppItemInfo> UserApps = new ArrayList<AppItemInfo>();
	private Context mContext;
	public static UninstallAdapter downloadAdapter;
	private ArrayList<String> codeSizeList;// 应用程序大小

	private BtnListener mListener;

	public UninstallAdapter() {

	}

	public UninstallAdapter(Context context, ArrayList<AppItemInfo> UserApps, ArrayList<String> codeSizeList, BtnListener mListener) {
		this.mContext = context;
		this.UserApps = UserApps;
		this.codeSizeList = codeSizeList;
		this.mListener = mListener;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return UserApps.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return UserApps.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup arg2) {
		final ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = View.inflate(mContext, R.layout.uninstall_manage_item, null);
			holder.icon = (ImageView) convertView.findViewById(R.id.uninstall_image);
			holder.appName = (TextView) convertView.findViewById(R.id.uninstall_name);
			holder.appVersion = (TextView) convertView.findViewById(R.id.uninstall_version);
			holder.appSize = (TextView) convertView.findViewById(R.id.uninstall_size);

			holder.uninstall = (Button) convertView.findViewById(R.id.uninstall);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.appName.setText(UserApps.get(position).getAppName());
		holder.icon.setImageDrawable(UserApps.get(position).getIcon());
		holder.appVersion.setText(mContext.getString(R.string.w_apk_unist_version) + UserApps.get(position).getVersion());

		if (codeSizeList.size() != 0 && codeSizeList.size() > position) {
			DecimalFormat df = new DecimalFormat("0.00");
			int codesize = Integer.parseInt(codeSizeList.get(position));
			float fileSize2 = (float) codesize / (1024 * 1024);
			fileSize2 = Float.parseFloat(df.format(fileSize2));
			holder.appSize.setText(mContext.getString(R.string.w_apk_unist_size) + fileSize2 + "M");
		} else {
			holder.appSize.setText(mContext.getString(R.string.W_app_musicName));
		}
		holder.uninstall.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mListener.onClick(position, v);
			}
		});
		return convertView;
	}

	private final class ViewHolder {
		public ImageView icon;// 存放图片
		public TextView appVersion;// 版本号
		public TextView appName;// 存放应用程序名
		public TextView appSize;// 应用程序大小
		public Button uninstall;

	}

	public interface BtnListener {
		public void onClick(int position, View view);
	}

	public ArrayList<AppItemInfo> getUserApps() {
		return UserApps;
	}

	public ArrayList<String> getCodeSizeList() {
		return codeSizeList;
	}

}
