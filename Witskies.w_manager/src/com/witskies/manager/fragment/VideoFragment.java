package com.witskies.manager.fragment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.R.integer;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
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

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.download.ImageDownloader.Scheme;
import com.witskies.manager.activity.MainActivity;
import com.witskies.manager.adapter.CommonAdapter;
import com.witskies.manager.adapter.ViewHolder;
import com.witskies.manager.app.WitskiesApplication;
import com.witskies.manager.bean.AdapterItemBean;
import com.witskies.manager.bean.DelBean;
import com.witskies.manager.bean.ItemBean;
import com.witskies.manager.image.MediaUtils;
import com.witskies.manager.util.AnalyticalData;
import com.witskies.manager.util.BitmapSaveFile;
import com.witskies.manager.util.RoundProgressBar;
import com.witskies.manager.util.Utils;
import com.witskies.w_manager.R;

public class VideoFragment extends Fragment implements OnClickListener {

	private ListView mListView;

	public static List<AdapterItemBean> mVideos;
	private VideoAdapter mAdapter;
	private LinearLayout bottom_fragment, bottom_fragment_del,
			fragment_bottom_onclick;
	private ImageView delImage;
	private TextView delName, allSelect;
	private boolean isFirstSel = true;
	private int delCount;
	private Button del_ok, del_no;

	private Context mContext;
	/**
	 * 加载页
	 */
	private LinearLayout mLoadingView;
	/**
	 * 空页
	 */
	private LinearLayout mEmptyView;
	private ArrayList<DelBean> delBeansList = new ArrayList<DelBean>();// 删除文件
	// private LoadingDialog pagerDialog;

	private int pagerIndex;
	private RoundProgressBar progressbar_video;
	private TextView textView;
	private boolean flag;

	public boolean isFlag() {
		return flag;
	}

	public void setFlag(boolean flag) {
		this.flag = flag;
	}

	public VideoFragment() {

	}

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == 1) {
				// Log.e("本地", "本地");

				progressbar_video.setVisibility(View.VISIBLE);

				if (mVideos != null && mVideos.size() > 0) {
					mAdapter = new VideoAdapter(mContext, mVideos,
							R.layout.comment_adapter_item_video);
					mListView.setAdapter(mAdapter);
				} else {
					showEmpty(true);
				}
				new SaveBitmapThread().start();
				showLoading(false);
			}
			if (msg.what == 2) {

				if (AnalyticalData.getInstance().getmAllParentPath() != null) {

					MediaUtils.getInstance().updateGallery(
							AnalyticalData.getInstance().getmAllParentPath(),
							getActivity());
				}

				mAdapter.selectAllOrCancle(false);

				if (mVideos == null || mVideos.size() == 0) {
					showEmpty(true);
				} else {
//					mVideos = AnalyticalData.getInstance().anlyticalDataToNeed(
//							MainActivity.getmVideos());
//					if (mVideos != null && mVideos.size() > 0) {
//						mAdapter = new VideoAdapter(mContext, mVideos,
//								R.layout.comment_adapter_item_video);
//						mListView.setAdapter(mAdapter);
//					} else {
//						showEmpty(true);
//					}
					
					for(int i=0;i<mVideos.size();i++){
						Log.e("mVideos==", mVideos.get(i).getCurrPath());
					}
					for(int i=0;i<WitskiesApplication.thumbnailImagesList.size();i++){
						Log.e("thumbnailImagesList==", WitskiesApplication.thumbnailImagesList.get(i));
					}
					
					
					 Log.e("mVideos总数+ ", mVideos.size() + " "+"WitskiesApplication.thumbnailImagesList总数"+WitskiesApplication.thumbnailImagesList.size()+" ");;
					mAdapter.notifyDataSetChanged();
				}

				Toast.makeText(getActivity(),
						delCount + mContext.getString(R.string.w_dialog_suc),
						Toast.LENGTH_SHORT).show();

			}
			if (msg.what == 3) {
				showLoading(false);
				progressbar_video.setVisibility(View.GONE);
				textView.setVisibility(View.GONE);

				if (mVideos != null && mVideos.size() > 0) {
					mAdapter.notifyDataSetChanged();
//					mAdapter = new VideoAdapter(mContext, mVideos,
//							R.layout.comment_adapter_item_video);
//					mListView.setAdapter(mAdapter);
				} else {
					showEmpty(true);
				}
			}

			if (msg.what == 4) {
				progressbar_video.setVisibility(View.GONE);
				textView.setVisibility(View.GONE);
				showLoading(false);

				mAdapter.notifyDataSetChanged();

			}

			if (msg.what == 5) {
				int rate = msg.arg1;
				textView.setVisibility(View.VISIBLE);
				progressbar_video.setProgress(rate);

			}
		};
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = initViews(inflater);
		progressbar_video = (RoundProgressBar) view
				.findViewById(R.id.roundprogressBar_video);
		textView = (TextView) view.findViewById(R.id.roundprogressBar_video_tv);
		setFlag(true);
		loadDatas();
		return view;
	}

	/**
	 * 加载布局
	 * 
	 * @param inflater
	 * @return
	 */
	private View initViews(LayoutInflater inflater) {
		mContext = getActivity();
		View view = inflater.inflate(R.layout.video_fragment, null);
		mLoadingView = (LinearLayout) view
				.findViewById(R.id.layout_loading_view);
		mEmptyView = (LinearLayout) view.findViewById(R.id.layout_empty_view);
		mListView = (ListView) view.findViewById(R.id.video_gridview);
		delImage = (ImageView) view
				.findViewById(R.id.fragment_bottom_del_image);
		delName = (TextView) view.findViewById(R.id.fragment_bottom_del_name);
		allSelect = (TextView) getActivity().findViewById(
				R.id.title_forever_selectall);
		delImage.setBackgroundResource(R.drawable.w_first_uninstall);
		delName.setTextColor(Color.parseColor("#a5a5a5"));
		delName.setTextSize(12);
		delName.setText(mContext.getString(R.string.w_app_dec));

		bottom_fragment = (LinearLayout) view
				.findViewById(R.id.bottom_fragment);
		bottom_fragment_del = (LinearLayout) view
				.findViewById(R.id.bottom_fragment_del);
		fragment_bottom_onclick = (LinearLayout) view
				.findViewById(R.id.fragment_bottom_onclick);
		del_ok = (Button) view.findViewById(R.id.fragment_bottom_del_ok);
		del_no = (Button) view.findViewById(R.id.fragment_bottom_del_no);

		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				String filePath = mVideos.get(arg2).getCurrPath();
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
					if (MainActivity.getmVideos() != null) {

						mVideos = AnalyticalData.getInstance()
								.anlyticalDataToNeed(MainActivity.getmVideos());
						Message message = mHandler.obtainMessage();
						message.what = 1;
						mHandler.sendMessage(message);

					}

				} catch (Exception e) {
				}

			}
		}).start();
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

	private void delDate() {
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

				AdapterItemBean bean = (AdapterItemBean) mAdapter
						.getItem(position);
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
							mContext.getString(R.string.operation_delete_confirm_message)
									+ delCount
									+ ""
									+ mContext
											.getString(R.string.operation_delete_confirm_message1))
					.setPositiveButton(R.string.operation_ok,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {

									if (delBeansList != null
											&& delBeansList.size() > 0) {
										for (int i = 0; i < delBeansList.size(); i++) {
											DelBean bean = delBeansList.get(i);
											File file = bean.getCurPath();
											int index = bean.getIndex();
											int position = bean.getPosition();
											int temp = position - i;
											if (temp >= 0
													&& mVideos.size() > temp) {
												for(int j=0;j<mVideos.size();j++){
													Log.e("mVideos11==", mVideos.get(j).getCurrPath());
												}
												for(int k=0;k<WitskiesApplication.thumbnailImagesList.size();k++){
													Log.e("thumbnailImagesList11==", WitskiesApplication.thumbnailImagesList.get(k));
												}
												
												
												
												
								 Log.e("mVideos ", mVideos.get(temp).getCurrPath() + ">>temp"+temp+"WitskiesApplication.thumbnailImagesList>"+WitskiesApplication.thumbnailImagesList.get(temp));;
												mVideos.remove(temp);

												MainActivity.getmVideos()
														.remove(temp);
												mAdapter.getCheckMap().remove(
														index);
												WitskiesApplication.thumbnailImagesList.remove(temp);
												
											}
											file.delete();
										}

									}

									mAdapter.closeCheckBox();
									bottom_fragment_del
											.setVisibility(View.GONE);
									bottom_fragment.setVisibility(View.VISIBLE);

									delName.setText(mContext
											.getString(R.string.w_app_dec));

									mAdapter.setOpenCheckBox(false);
									delBeansList = null;
									allSelect.setVisibility(View.GONE);

									Message message = mHandler.obtainMessage();
								
									message.what = 2;
									mHandler.sendMessage(message);

								}
							})
					.setNegativeButton(R.string.operation_cancel,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {

									mAdapter.closeCheckBox();
									bottom_fragment_del
											.setVisibility(View.GONE);
									bottom_fragment.setVisibility(View.VISIBLE);

									delName.setText(mContext
											.getString(R.string.w_app_dec));

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

	class VideoAdapter extends CommonAdapter<AdapterItemBean> {

		public VideoAdapter(Context context, List<AdapterItemBean> datas,
				int layoutId) {
			super(context, datas, layoutId);

		}

		public void remove(int position) {

		}

		@Override
		public void convert(final ViewHolder holder, AdapterItemBean itemBean) {
			TextView title = holder.getView(R.id.comment_adapter_title);
			TextView time = holder.getView(R.id.comment_adapter_time);
			TextView size = holder.getView(R.id.comment_adapter_size);
			title.setText(itemBean.getName());
			time.setText(itemBean.getTime());
			size.setText(itemBean.getSize());

			ImageView imageView = holder
					.getView(R.id.comment_adapter_image_video);

			if (WitskiesApplication.thumbnailImagesList != null
					&& WitskiesApplication.thumbnailImagesList.size() > 0
					&& WitskiesApplication.thumbnailImagesList.size() == mVideos
							.size()) {

				 Log.e("TAG", "ID>>" + itemBean.getId() + " " + "Position>>"
				+ holder.getmPosition() + "");
			
					ImageLoader
							.getInstance()
							.displayImage(
									Scheme.FILE.wrap(WitskiesApplication.thumbnailImagesList
											.get(holder.getmPosition())), imageView,
									options);
				
			}
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

	/**
	 * 异步保存图片缩略图
	 * */
	class SaveBitmapThread extends Thread {

		public void run() {
			Log.e("线程启动", "线程启动成功");
			if (mVideos != null && mVideos.size() > 0) {

				if (WitskiesApplication.thumbnailImagesList != null
						&& WitskiesApplication.thumbnailImagesList.size() == mVideos
								.size()) {
					Message message = mHandler.obtainMessage();
					message.what = 3;
					mHandler.sendMessage(message);
					// Log.e("本地2", "本地2");
				} else {

					initDate();

				}
			}
		}

	}

	private void initDate() {

		for (int i = 0; i < mVideos.size(); i++) {
			if (flag) {

				Bitmap bmp = ThumbnailUtils.createVideoThumbnail(mVideos.get(i)
						.getCurrPath(), MediaStore.Images.Thumbnails.MINI_KIND);
				bmp = ThumbnailUtils.extractThumbnail(bmp, 100, 100,
						ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
				if (bmp != null) {
					String bmpPath = BitmapSaveFile.getInstantiation()
							.saveMyBitmap(bmp, mVideos.get(i).getName());
					mVideos.get(i).setId(i);
					// Log.e("bmpPath ", bmpPath + "  防空");

					if (WitskiesApplication.thumbnailImagesList == null) {
						WitskiesApplication.thumbnailImagesList = new ArrayList<String>();
					}
					if (bmpPath != null) {

						WitskiesApplication.thumbnailImagesList.add(bmpPath);
						// Log.e("加进去的", bmpPath);

					}

				} else {
					// // 将本地的图片背景用imageloader加载
					// Uri uri =
					// Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
					// + "://"
					// + getResources().getResourcePackageName(
					// R.drawable.video_fragment)
					// + "/"
					// + getResources().getResourceTypeName(
					// R.drawable.video_fragment)
					// + "/"
					// + getResources().getResourceEntryName(
					// R.drawable.video_fragment));
					//
					// if (uri != null) {
					//
					// WitskiesApplication.thumbnailImagesList.add(uri
					// .toString());
					// } else {
					WitskiesApplication.thumbnailImagesList
							.add("bitmap is null");
					// }
				}

				Message message = mHandler.obtainMessage();
				pagerIndex = (int) (((float) WitskiesApplication.thumbnailImagesList
						.size() / mVideos.size()) * 100);
				Log.e("pagerIndex", pagerIndex + ">>mVideos" + mVideos.size()
						+ " >>ImagesList"
						+ WitskiesApplication.thumbnailImagesList.size() + "");
				if (pagerIndex < 101) {
					message.arg1 = pagerIndex;
					message.what = 5;
					mHandler.sendMessage(message);
				}

//				if (WitskiesApplication.thumbnailImagesList.size() == mVideos
//						.size()) {
//
//					Message msg = mHandler.obtainMessage();
//					msg.obj = WitskiesApplication.thumbnailImagesList;
//					msg.what = 4;
//					mHandler.sendMessage(msg);
//
//					Log.e("线程结束", "线程结束");
//
//				}

				if (WitskiesApplication.ch > 0) {
					if ((WitskiesApplication.thumbnailImagesList.size() - 1) == VideoFragment.mVideos
							.size()) {

						Message msg = mHandler.obtainMessage();
						WitskiesApplication.thumbnailImagesList.remove(0);
						msg.obj = WitskiesApplication.thumbnailImagesList;
						msg.what = 4;
						mHandler.sendMessage(msg);

						Log.e("线程结束", "线程结束");

					}
				} else {
					if ((WitskiesApplication.thumbnailImagesList.size()) == VideoFragment.mVideos
							.size()) {

						Message msg = mHandler.obtainMessage();
					
						msg.obj = WitskiesApplication.thumbnailImagesList;
						msg.what = 4;
						mHandler.sendMessage(msg);

						Log.e("线程结束", "线程结束");

					}
				

				}
			}
		}
		WitskiesApplication.ch++;
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (WitskiesApplication.thumbnailImagesList.size() != mVideos.size()) {
			WitskiesApplication.thumbnailImagesList
					.removeAll(WitskiesApplication.thumbnailImagesList);
			WitskiesApplication.thumbnailImagesList = null;
		}
		setFlag(false);
	}
}
