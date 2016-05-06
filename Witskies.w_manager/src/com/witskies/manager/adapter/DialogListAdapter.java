package com.witskies.manager.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.witskies.w_manager.R;

/**
 * @author alessandro.balocco
 */
public class DialogListAdapter extends BaseAdapter {

	private LayoutInflater layoutInflater;
	private List<String> datas;

	public DialogListAdapter(Context context, List<String> datas) {
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
			view = layoutInflater.inflate(R.layout.item_sd_bottom_sort, parent,
					false);
			viewHolder = new ViewHolder();
			viewHolder.textView = (TextView) view
					.findViewById(R.id.item_sd_bottom_sort_text);
			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}
		viewHolder.textView.setText(datas.get(position));
		return view;
	}

	private static class ViewHolder {
		TextView textView;
	}
}
