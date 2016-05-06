package com.witskies.manager.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.witskies.w_manager.R;

/**
 * @author alessandro.balocco
 */
public class DialogPathListAdapter extends BaseAdapter {

	private LayoutInflater layoutInflater;
	private List<String> datas;

	public DialogPathListAdapter(Context context, List<String> datas) {
		layoutInflater = LayoutInflater.from(context);
		this.datas = datas;
	}

	@Override
	public int getCount() {
		return datas.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		View view = convertView;

		if (view == null) {
			view = layoutInflater.inflate(R.layout.item_sd_dialog_path, parent,
					false);
			viewHolder = new ViewHolder();
			viewHolder.textView = (TextView) view
					.findViewById(R.id.item_sd_dialog_path_text);
			viewHolder.imageView = (ImageView) view
					.findViewById(R.id.item_sd_dialog_path_image);
			viewHolder.layout = (LinearLayout) view
					.findViewById(R.id.item_sd_dialog_path_layout);
			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}
		if (position == 0) {
			viewHolder.imageView
					.setBackgroundResource(R.drawable.sd_dropdown_icon_root);
		} else {
			viewHolder.imageView
					.setBackgroundResource(R.drawable.sd_dropdown_icon_folder);
		}
		viewHolder.textView.setText(datas.get(position));
		viewHolder.layout.setPadding(position * 50, 0, 0, 0);
		return view;
	}

	private static class ViewHolder {
		TextView textView;
		ImageView imageView;
		LinearLayout layout;
	}
}
