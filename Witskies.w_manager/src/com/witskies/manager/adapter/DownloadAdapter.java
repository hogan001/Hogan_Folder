package com.witskies.manager.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.witskies.manager.bean.ApkBean;
import com.witskies.w_manager.R;

/**
 * @作者 ch
 * @描述 下载管理页面的适配器
 * @时间 2015-4-2 下午3:51:43
 */
public class DownloadAdapter extends BaseAdapter {
	private ArrayList<ApkBean> mApks;
	private Context context;
	public static DownloadAdapter downloadAdapter;
	private Map<Integer, Boolean> mHideMap;

	public Map<Integer, Boolean> getmHideMap() {
		return mHideMap;
	}

	public DownloadAdapter(Context Context, ArrayList<ApkBean> mApks) {
		this.context = Context;
		this.mApks = mApks;
		mHideMap = new HashMap<Integer, Boolean>();
		for (int i = 0; i < mApks.size(); i++) {
			mHideMap.put(i, false);
		}
	}

	public void restMap() {
		for (int i = 0; i < mApks.size(); i++) {
			mHideMap.put(i, false);
		}
	}

	@Override
	public int getCount() {
		if (mApks != null && mApks.size() > 0) {
			return mApks.size();
		} else {
			return 0;
		}
	}

	@Override
	public Object getItem(int arg0) {
		return mApks.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup arg2) {
		final ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = View.inflate(context, R.layout.download_manage_item, null);
			holder.image = (ImageView) convertView.findViewById(R.id.downloaded_image);
			holder.fileName = (TextView) convertView.findViewById(R.id.downloaded_info_name);
			holder.fileSize = (TextView) convertView.findViewById(R.id.downloaded_info_size);
			holder.install = (LinearLayout) convertView.findViewById(R.id.click_install);
			holder.delete = (LinearLayout) convertView.findViewById(R.id.click_delete);
			holder.openList = (LinearLayout) convertView.findViewById(R.id.open_list);
			holder.fileVersion = (TextView) convertView.findViewById(R.id.downloaded_info_version);
			holder.arrow = (ImageView) convertView.findViewById(R.id.downloaded_arrow);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.image.setBackgroundDrawable(mApks.get(position).getIcon());
		holder.fileName.setText(mApks.get(position).getName());
		holder.fileSize.setText(context.getString(R.string.w_apk_unist_size)
				+ mApks.get(position).getSize() + "M");
		holder.fileVersion.setText(context.getString(R.string.w_apk_unist_version)
				+ mApks.get(position).getVersion());
		holder.openList.setVisibility(mHideMap.get(position) ? View.VISIBLE : View.GONE);
		holder.arrow.setBackgroundResource(mHideMap.get(position) ? R.drawable.arrow_up
				: R.drawable.arrow_down);
		return convertView;
	}

	private final class ViewHolder {
		public ImageView image;
		public TextView fileName;
		public TextView fileSize;
		public TextView fileVersion;
		public LinearLayout install;
		public LinearLayout delete;
		public LinearLayout openList;
		private ImageView arrow;

	}

	public ArrayList<ApkBean> getmApks() {
		return mApks;
	}

}
