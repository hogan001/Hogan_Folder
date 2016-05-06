package com.witskies.manager.fragment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.witskies.manager.activity.MainActivity;
import com.witskies.manager.adapter.CommonAdapter;
import com.witskies.manager.adapter.ViewHolder;
import com.witskies.manager.bean.AdapterItemBean;
import com.witskies.manager.bean.DelBean;
import com.witskies.manager.image.MediaUtils;
import com.witskies.manager.util.AnalyticalData;
import com.witskies.manager.util.Utils;
import com.witskies.w_manager.R;

public class DocumentFragment extends Fragment implements OnClickListener {

	private ListView mListView;
	private List<AdapterItemBean> mDocuments;
	private MyAdapter mAdapter;
	private LinearLayout bottom_fragment, bottom_fragment_del,fragment_bottom_onclick;
	private ImageView delImage, comment_adapter_image;
	private TextView delName, allSelect;
	private boolean isFirstSel = true;
	private int delCount;
	private Context mContext;
	private Button del_ok, del_no;
	/**
	 * 加载页
	 */
	private LinearLayout mLoadingView;
	/**
	 * 空页
	 */
	private LinearLayout mEmptyView;
	private ArrayList<DelBean> delBeansList = new ArrayList<DelBean>();// 删除文件

	public DocumentFragment() {

	}

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == 1) {
				showLoading(false);
				if (mDocuments != null && mDocuments.size() > 0) {
					mAdapter = new MyAdapter(mContext, mDocuments, R.layout.comment_adapter_item);
					mListView.setAdapter(mAdapter);
				} else {
					showEmpty(true);
				}
			}
			if (msg.what == 2) {
				if (AnalyticalData.getInstance().getmAllParentPath() != null) {

					MediaUtils.getInstance().updateGallery(AnalyticalData.getInstance().getmAllParentPath(), getActivity());
				}
				mAdapter.selectAllOrCancle(false);
				mAdapter.notifyDataSetChanged();
				if (mDocuments == null || mDocuments.size() == 0) {
					showEmpty(true);
				}

				Toast.makeText(getActivity(), delCount + mContext.getString(R.string.w_dialog_suc), Toast.LENGTH_SHORT).show();

			}
		};
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = initViews(inflater);
		loadDatas();
		return view;
	}

	private View initViews(LayoutInflater inflater) {
		View view = inflater.inflate(R.layout.doucument_fragment, null);
		mContext = getActivity();
		mLoadingView = (LinearLayout) view.findViewById(R.id.layout_loading_view);
		mEmptyView = (LinearLayout) view.findViewById(R.id.layout_empty_view);
		mListView = (ListView) view.findViewById(R.id.document_listview);
		delImage = (ImageView) view.findViewById(R.id.fragment_bottom_del_image);
		delName = (TextView) view.findViewById(R.id.fragment_bottom_del_name);
		allSelect = (TextView) getActivity().findViewById(R.id.title_forever_selectall);
		delImage.setBackgroundResource(R.drawable.w_first_uninstall);
		delName.setTextColor(Color.parseColor("#a5a5a5"));
		delName.setTextSize(12);
		delName.setText(mContext.getString(R.string.w_app_dec));
		bottom_fragment = (LinearLayout) view.findViewById(R.id.bottom_fragment);
		bottom_fragment_del = (LinearLayout) view.findViewById(R.id.bottom_fragment_del);
		fragment_bottom_onclick = (LinearLayout) view.findViewById(R.id.fragment_bottom_onclick);
		del_ok = (Button) view.findViewById(R.id.fragment_bottom_del_ok);
		del_no = (Button) view.findViewById(R.id.fragment_bottom_del_no);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

				String filePath = mDocuments.get(arg2).getCurrPath();
				Utils.openFile(mContext, filePath);
			}
		});

		delListener();
		fragment_bottom_onclick.setOnClickListener(this);
		allSelect.setOnClickListener(this);
		return view;

	}

	/**
	 * 加载数据
	 */
	private void loadDatas() {
		showLoading(true);
		showEmpty(false);
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					if (MainActivity.getmDocuments() != null) {

						mDocuments = AnalyticalData.getInstance().anlyticalDataToNeed(MainActivity.getmDocuments());
					}

					Message message = mHandler.obtainMessage();
					message.what = 1;
					mHandler.sendMessage(message);
				} catch (Exception e) {
				}

			}
		}).start();
		;
	}

	private void showEmpty(boolean show) {
		mEmptyView.setVisibility(show ? View.VISIBLE : View.GONE);
	}

	private void showLoading(boolean show) {
		mLoadingView.setVisibility(show ? View.VISIBLE : View.GONE);
	}

	private void delListener() {

		del_ok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				isFirstSel = true;
				allSelect.setText(getActivity().getString(R.string.all));
				delDate();

			}
		});

		del_no.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				isFirstSel = true;
				allSelect.setText(getActivity().getString(R.string.all));

				bottom_fragment_del.setVisibility(View.GONE);
				bottom_fragment.setVisibility(View.VISIBLE);
				mAdapter.closeCheckBox();

				mAdapter.setOpenCheckBox(false);
				mAdapter.configCheckMap(false);

				allSelect.setVisibility(View.GONE);
				mAdapter.notifyDataSetInvalidated();
			}
		});

	}

	protected void delDate() {

		/*
		 * 删除算法最复杂,拿到checkBox选择寄存map
		 */
		Map<Integer, Boolean> map = mAdapter.getCheckMap();
		// 获取当前的数据数量
		int count = mAdapter.getCount();

		// 进行遍历
		for (int i = 0; i < count; i++) {

			// 因为List的特性,删除了2个item,则3变成2,所以这里要进行这样的换算,才能拿到删除后真正的position
			int position = i - (count - mAdapter.getCount());

			if (map.get(i) != null && map.get(i)) {

				AdapterItemBean bean = (AdapterItemBean) mAdapter.getItem(position);
				String filePath = bean.getCurrPath();
				if (filePath.length() > 0) {
					File file = new File(filePath);

					if (file.exists()) {
						delCount++;
						DelBean delBean = new DelBean();
						delBean.setCurPath(file);
						delBean.setIndex(i);
						delBean.setPosition(position);
						if (delBeansList == null) {
							delBeansList = new ArrayList<DelBean>();

						}
						delBeansList.add(delBean);
					}

				}
			}
		}

		if (delCount > 0) {
			Dialog dialog = new AlertDialog.Builder(mContext)
					.setMessage(
							mContext.getString(R.string.operation_delete_confirm_message) + delCount + ""
									+ mContext.getString(R.string.operation_delete_confirm_message1))
					.setPositiveButton(R.string.operation_ok, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {

							if (delBeansList != null && delBeansList.size() > 0) {
								for (int i = 0; i < delBeansList.size(); i++) {
									DelBean bean = delBeansList.get(i);
									File file = bean.getCurPath();
									int index = bean.getIndex();
									int position = bean.getPosition();
									
									int temp = position - i;
									if (temp >= 0&&mDocuments.size()>temp) {
										mDocuments.remove(temp);
										MainActivity.getmDocuments().remove(temp);
										mAdapter.getCheckMap().remove(index);
									}
									file.delete();
								}

							}

							mAdapter.closeCheckBox();
							bottom_fragment_del.setVisibility(View.GONE);
							bottom_fragment.setVisibility(View.VISIBLE);

							delName.setText(mContext.getString(R.string.w_app_dec));

							mAdapter.setOpenCheckBox(false);
							delBeansList = null;
							allSelect.setVisibility(View.GONE);

							Message message = mHandler.obtainMessage();
							message.what = 2;
							mHandler.sendMessage(message);

						}
					}).setNegativeButton(R.string.operation_cancel, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {

							mAdapter.closeCheckBox();
							bottom_fragment_del.setVisibility(View.GONE);
							bottom_fragment.setVisibility(View.VISIBLE);

							delName.setText(mContext.getString(R.string.w_app_dec));

							mAdapter.setOpenCheckBox(false);
							mAdapter.configCheckMap(false);
							delBeansList = null;

							allSelect.setVisibility(View.GONE);
						}
					}).create();
			dialog.show();

		} else {
			mAdapter.closeCheckBox();
			bottom_fragment_del.setVisibility(View.GONE);
			bottom_fragment.setVisibility(View.VISIBLE);

			delName.setText(mContext.getString(R.string.w_app_dec));

			mAdapter.setOpenCheckBox(false);

			allSelect.setVisibility(View.GONE);
		}
	}

	class MyAdapter extends CommonAdapter<AdapterItemBean> {

		public MyAdapter(Context context, List<AdapterItemBean> datas, int layoutId) {
			super(context, datas, layoutId);

		}

		public void remove(int position) {

		}

		@Override
		public void convert(final ViewHolder holder, AdapterItemBean itemBean) {
			TextView title = holder.getView(R.id.comment_adapter_title);
			TextView time = holder.getView(R.id.comment_adapter_time);
			TextView size = holder.getView(R.id.comment_adapter_size);
			comment_adapter_image = holder.getView(R.id.comment_adapter_image);
			comment_adapter_image.setBackgroundResource(R.drawable.document_fragment);
			title.setText(itemBean.getName());
			time.setText(itemBean.getTime());
			size.setText(itemBean.getSize());

		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.fragment_bottom_onclick:

			bottomDelete();

			break;
		case R.id.title_forever_selectall:

			allSelect();

			break;
		}
	}

	private void allSelect() {
		if (isFirstSel) {
			mAdapter.configCheckMap(true);
			mAdapter.selectAllOrCancle(true);
			allSelect.setText(getActivity().getString(R.string.camera_cancel));
			isFirstSel = false;
		} else {
			mAdapter.configCheckMap(false);
			mAdapter.selectAllOrCancle(false);
			allSelect.setText(getActivity().getString(R.string.all));
			isFirstSel = true;
		}
	}

	private void bottomDelete() {
		if (mAdapter != null) {
			delCount = 0;
			mAdapter.openCheckBox();

			if (mAdapter.isOpenCheckBox()) {
				mAdapter.setOpenCheckBox(false);

			} else {
				mAdapter.setOpenCheckBox(true);

			}

			bottom_fragment_del.setVisibility(View.VISIBLE);
			bottom_fragment.setVisibility(View.GONE);
			allSelect.setVisibility(View.VISIBLE);
		}
	}

}
