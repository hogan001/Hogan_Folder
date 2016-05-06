package com.witskies.manager.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.witskies.w_manager.R;

/**
 * @作者 ch
 * @描述 万能适配器
 * @时间 2015年4月24日 下午4:55:56
 */
public abstract class CommonAdapter<T> extends BaseAdapter {
	protected Context mContext;
	protected List<T> mDatas;
	protected LayoutInflater mInflater;
	protected int layoutId;
	protected boolean isOpenCheckBox;
	protected DisplayImageOptions options;
	/**
	 * CheckBox 是否选择的存储集合,key 是 position , value 是该position是否选中
	 */
	public Map<Integer, Boolean> isCheckMap = new HashMap<Integer, Boolean>();
	private List<CheckBox> mCheckBox;

	public CommonAdapter(Context context, List<T> datas, int layoutId) {
		this.mContext = context;
		this.mDatas = datas;
		this.layoutId = layoutId;
		mInflater = LayoutInflater.from(context);
		options = new DisplayImageOptions.Builder()
				// 加载时显示的图片
				.showImageOnLoading(R.drawable.video_fragment)
				// 加载失败
				.showImageOnFail(R.drawable.video_fragment).cacheInMemory(true).cacheOnDisk(true)
				// 内存缓存
				.imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
				.displayer(new SimpleBitmapDisplayer())
				// sd卡缓存
				.considerExifParams(true).bitmapConfig(Bitmap.Config.RGB_565).build();
		// 初始化,默认都没有选中
		if (mDatas.size() > 0) {
			configCheckMap(false);
		}

		mCheckBox = new ArrayList<CheckBox>();
	}

	public Map<Integer, Boolean> getCheckMap() {
		return isCheckMap;
	}

	public void setCheckMap(Map<Integer, Boolean> isCheckMap) {
		this.isCheckMap = isCheckMap;
	}

	/**
	 * 首先,默认情况下,所有项目都是没有选中的.这里进行初始化
	 */
	public void configCheckMap(boolean bool) {

		for (int i = 0; i < mDatas.size(); i++) {
			isCheckMap.put(i, bool);
		}

	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if (mDatas.size() > 0) {
			return mDatas.size();
		}
		return 0;
	}

	@Override
	public T getItem(int position) {
		// TODO Auto-generated method stub
		return mDatas.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	public boolean isOpenCheckBox() {
		return isOpenCheckBox;
	}

	public void setOpenCheckBox(boolean isOpenCheckBox) {
		this.isOpenCheckBox = isOpenCheckBox;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder = ViewHolder.get(mContext, convertView, parent, layoutId, position);
		convert(holder, getItem(position));

		/*
		 * 获得单选按钮
		 */
		CheckBox cbCheck = holder.getView(R.id.comment_adapter_checkbox);
		if (isOpenCheckBox) {
			cbCheck.setVisibility(View.VISIBLE);
		} else {
			cbCheck.setVisibility(View.GONE);
		}
		mCheckBox.add(cbCheck);
		cbCheck.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				isCheckMap.put(position, isChecked);

			}
		});

		if (isCheckMap.get(position) == null) {
			isCheckMap.put(position, false);
		}

		cbCheck.setChecked(isCheckMap.get(position));

		return holder.getConvertView();

	}

	public abstract void convert(ViewHolder holder, T t);

	/**
	 * 打开checkbox
	 */
	public void openCheckBox() {
		for (View view : mCheckBox) {
			if (view.getVisibility() == View.GONE) {
				view.setVisibility(View.VISIBLE);
			}
		}
	}

	/**
	 * 关闭checkbox
	 */
	public void closeCheckBox() {
		for (View view : mCheckBox) {
			if (view.getVisibility() == View.VISIBLE) {
				view.setVisibility(View.GONE);
			}
		}
	}

	/**
	 * 全选或者反选
	 */
	public void selectAllOrCancle(boolean flag) {
		for (CheckBox checkBox : mCheckBox) {
			if (flag) {
				checkBox.setChecked(true);
			} else {
				checkBox.setChecked(false);
			}
		}
	}
}
