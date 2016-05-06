package com.witskies.manager.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.witskies.manager.bean.AppItemInfo;
import com.witskies.w_manager.R;
/**
 * @描述 根据网络和本地分类的apapter
 * @时间 2015-4-21 下午3:00:00
 */
public class AppDispalyAdapter extends BaseAdapter {
	private Context context;
	private	ArrayList<AppItemInfo> Apps;
	

	public AppDispalyAdapter(Context context, ArrayList<AppItemInfo> Apps) {
		this.context = context;
		if (Apps == null) {
			this.Apps = new ArrayList<AppItemInfo>();
		} else {
			this.Apps = Apps;
		}
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return Apps.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return Apps.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = View.inflate(context, R.layout.app_fragment_item_two,
					null);
			holder.image = (ImageView) convertView
					.findViewById(R.id.apps_categoryimage);
			holder.fileName = (TextView) convertView
					.findViewById(R.id.app_CategoryName);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		String filename = Apps.get(position).getAppName();
		holder.fileName.setText(filename);
		
		holder.image.setImageDrawable(Apps.get(position).getIcon());

		return convertView;
	}

	private final class ViewHolder {
		public ImageView image;
		public TextView fileName;
	}

}
