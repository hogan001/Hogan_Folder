package com.witskies.manager.fileexplorer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.witskies.manager.adapter.DialogListAdapter;
import com.witskies.manager.fileexplorer.FileViewInteractionHub.Mode;
import com.witskies.manager.helputil.FileUtil;
import com.witskies.manager.widget.dialogplus.DialogPlus;
import com.witskies.manager.widget.dialogplus.DialogPlus.Gravity;
import com.witskies.manager.widget.dialogplus.DialogPlus.HolderAdapter;
import com.witskies.manager.widget.dialogplus.ListHolder;
import com.witskies.w_manager.R;

public class FileListAdapter extends BaseAdapter {

	private ArrayList<FileInfo> mFileNameList;
	private LayoutInflater mInflater;
	private FileViewInteractionHub mFileViewInteractionHub;
	private FileIconHelper mFileIcon;
	private Context mContext;
	private boolean isOpenCheckBox;
	private Map<Integer, Boolean> mHideMap;
	private DropDownListener mDownListener;

	public FileListAdapter(Context context, ArrayList<FileInfo> mFileNameList, FileViewInteractionHub f, FileIconHelper fileIcon,
			DropDownListener downListener) {
		this.mInflater = LayoutInflater.from(context);
		this.mFileViewInteractionHub = f;
		this.mFileIcon = fileIcon;
		this.mFileNameList = mFileNameList;
		this.mContext = context;
		mHideMap = new HashMap<Integer, Boolean>();
		this.mDownListener = downListener;

	}

	@Override
	public int getCount() {
		return mFileNameList.size();
	}

	@Override
	public Object getItem(int arg0) {
		return mFileNameList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(final int position, View view, ViewGroup arg2) {

		ViewHolder holder = null;
		if (view == null) {
			view = mInflater.inflate(R.layout.item_sd_file_listview, null);
			holder = new ViewHolder();
			holder.fileCheckBox = (ImageView) view.findViewById(R.id.file_checkbox);
			holder.fileIcon = (ImageView) view.findViewById(R.id.file_image);
			holder.fileIconFrame = (ImageView) view.findViewById(R.id.file_image_frame);
			holder.fileName = (TextView) view.findViewById(R.id.file_name);
			holder.fileCount = (TextView) view.findViewById(R.id.file_count);
			holder.fileDate = (TextView) view.findViewById(R.id.modified_time);
			holder.fileSize = (TextView) view.findViewById(R.id.file_size);
			holder.arrow = (ImageView) view.findViewById(R.id.item_sd_file_listview_arrow);
			holder.hideMenu = (LinearLayout) view.findViewById(R.id.item_sd_file_listview_hideMenu);
			holder.openHide = (LinearLayout) view.findViewById(R.id.item_sd_file_listview_openHide);
			holder.copy = (LinearLayout) view.findViewById(R.id.item_sd_file_listview_copy);
			holder.move = (LinearLayout) view.findViewById(R.id.item_sd_file_listview_move);
			holder.delete = (LinearLayout) view.findViewById(R.id.item_sd_file_listview_delete);
			holder.more = (LinearLayout) view.findViewById(R.id.item_sd_file_listview_more);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}

		FileInfo lFileInfo = mFileNameList.get(position);
		holder.fileName.setText(lFileInfo.fileName);
		holder.fileCount.setText(lFileInfo.IsDir ? "(" + lFileInfo.Count + ")" : "");
		holder.fileDate.setText(FileUtil.formatDateString(mContext, lFileInfo.ModifiedDate));
		holder.fileSize.setText(lFileInfo.IsDir ? "" : FileUtil.convertStorage(lFileInfo.fileSize));
		if (lFileInfo.IsDir) {
			holder.fileIconFrame.setVisibility(View.GONE);
			// 设置文件夹的图标
			holder.fileIcon.setImageResource(R.drawable.sd_folder);
		} else {
			mFileIcon.setIcon(lFileInfo, holder.fileIcon, holder.fileIconFrame);
		}

		if (isOpenCheckBox) {
			holder.fileCheckBox.setVisibility(View.VISIBLE);
		} else {
			holder.fileCheckBox.setVisibility(View.GONE);
		}
		// if in moving mode, show selected file always
		if (mFileViewInteractionHub.isMoveState()) {
			lFileInfo.Selected = mFileViewInteractionHub.isFileSelected(lFileInfo.filePath);
		}

		// 设置checkbox
		holder.fileCheckBox.setImageResource(lFileInfo.Selected ? R.drawable.checkbox_pressed : R.drawable.checkbox_normal);
		holder.fileCheckBox.setTag(lFileInfo);
		// View checkArea = findViewById(R.id.file_checkbox_area);
		// checkArea.setOnClickListener(checkClick);
		holder.fileCheckBox.setSelected(lFileInfo.Selected);
		final View mHideMenu = holder.hideMenu;
		holder.openHide.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				mDownListener.onDropDiwnClick(position, mHideMenu);

			}
		});
		holder.hideMenu.setVisibility(mHideMap.get(position) != null ? View.VISIBLE : View.GONE);
		holder.arrow.setBackgroundResource(mHideMap.get(position) != null ? R.drawable.sd_item_more_up : R.drawable.sd_item_more_down);
		if (holder.fileCheckBox.getVisibility() == View.VISIBLE) {
			holder.openHide.setClickable(false);
		}
		holder.copy.setOnClickListener(new MyOnclickListener(position));
		holder.move.setOnClickListener(new MyOnclickListener(position));
		holder.delete.setOnClickListener(new MyOnclickListener(position));
		holder.more.setOnClickListener(new MyOnclickListener(position));
		return view;
	}

	private class ViewHolder {
		TextView fileName;
		TextView fileCount;
		TextView fileDate;
		TextView fileSize;
		ImageView fileIcon;
		ImageView fileIconFrame;
		ImageView fileCheckBox;
		ImageView arrow;
		LinearLayout openHide;
		LinearLayout hideMenu;

		LinearLayout copy;
		LinearLayout move;
		LinearLayout delete;
		LinearLayout more;
	}

	private class MyOnclickListener implements OnClickListener {
		int position;

		public MyOnclickListener(int position) {
			// FileInfo lFileInfo = mFileNameList.get(position);
			// lFileInfo.Selected = true;
			this.position = position;
		}

		@Override
		public void onClick(View arg0) {
			switch (arg0.getId()) {
			case R.id.item_sd_file_listview_copy:

				mFileViewInteractionHub.onOperationCopy(position);
				break;
			case R.id.item_sd_file_listview_delete:
				mFileViewInteractionHub.onOperationDelete(position);
				break;
			case R.id.item_sd_file_listview_move:
				mFileViewInteractionHub.onOperationMove(position);
				break;
			case R.id.item_sd_file_listview_more:
				showMoreDialog(position);
				break;

			}
		}
	}

	DialogPlus dialogPlus = null;

	private void showMoreDialog(final int position) {
		HolderAdapter holderAdapter = new ListHolder();
		List<String> datas = new ArrayList<String>();
		datas.add(mContext.getString(R.string.operation_copy_path));
		datas.add(mContext.getString(R.string.operation_send));
		datas.add(mContext.getString(R.string.operation_rename));
		datas.add(mContext.getString(R.string.operation_info));
		DialogListAdapter adapter = new DialogListAdapter(mContext, datas);
		dialogPlus = new DialogPlus.Builder(mContext).setContentHolder(holderAdapter).setAdapter(adapter).setGravity(Gravity.BOTTOM)
				.setOnItemClickListener(new DialogPlus.OnItemClickListener() {

					@Override
					public void onItemClick(DialogPlus dialog, Object item, View view, int pos) {
						switch (pos) {
						case 0:
							mFileViewInteractionHub.onOperationCopyPath(position);
							break;
						case 1:
							mFileViewInteractionHub.onOperationSend(position);
							break;
						case 2:
							mFileViewInteractionHub.onOperationRename(position);
							break;
						case 3:
							mFileViewInteractionHub.onOperationInfo(position);
							break;
						}
						dialogPlus.dismiss();
					}
				}).create();

		dialogPlus.show();
	}

	public boolean isOpenCheckBox() {
		return isOpenCheckBox;
	}

	public void setOpenCheckBox(boolean isOpenCheckBox) {
		this.isOpenCheckBox = isOpenCheckBox;
	}

	public interface DropDownListener {
		public void onDropDiwnClick(int position, View view);
	}

	public void clearHideMenu() {
		mHideMap.clear();
	}

	public void openHideMenu(int position, boolean isOpen) {
		mHideMap.put(position, isOpen);
	}
}
