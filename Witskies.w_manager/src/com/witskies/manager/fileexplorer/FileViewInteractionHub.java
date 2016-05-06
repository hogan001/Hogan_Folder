package com.witskies.manager.fileexplorer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.witskies.manager.adapter.DialogListAdapter;
import com.witskies.manager.adapter.DialogPathListAdapter;
import com.witskies.manager.fileexplorer.FileMainActivity.SelectFilesCallback;
import com.witskies.manager.fileexplorer.FileOperationHelper.IOperationProgressListener;
import com.witskies.manager.fileexplorer.FileSortHelper.SortMethod;
import com.witskies.manager.fileexplorer.FileTextInputDialog.OnFinishListener;
import com.witskies.manager.helputil.FileUtil;
import com.witskies.manager.helputil.ToastUtil;
import com.witskies.manager.widget.dialogplus.DialogPlus;
import com.witskies.manager.widget.dialogplus.DialogPlus.Gravity;
import com.witskies.manager.widget.dialogplus.DialogPlus.HolderAdapter;
import com.witskies.manager.widget.dialogplus.Holder;
import com.witskies.manager.widget.dialogplus.ListHolder;
import com.witskies.w_manager.R;

public class FileViewInteractionHub implements IOperationProgressListener {
	private static final String LOG_TAG = "FileViewInteractionHub";

	private IFileInteractionListener mFileViewListener;

	private ArrayList<FileInfo> mCheckedFileNameList = new ArrayList<FileInfo>();

	private FileOperationHelper mFileOperationHelper;

	private FileSortHelper mFileSortHelper;

	private View mConfirmOperationBar;

	private ProgressDialog progressDialog;

	private View mNavigationBar;

	private TextView mNavigationBarText;
	private LinearLayout mNavigationBarLayout;

	private Context mContext;
	/**
	 * 新建文件夹对话框
	 */
	private DialogPlus addDialog = null;
	/**
	 * 排序对话框
	 */
	private DialogPlus sortDialog = null;
	/**
	 * 路径对话框
	 */
	private DialogPlus pathDialog = null;

	public enum Mode {
		View, Pick
	};

	public FileViewInteractionHub(IFileInteractionListener fileViewListener) {
		assert (fileViewListener != null);
		mFileViewListener = fileViewListener;
		setup();
		mFileOperationHelper = new FileOperationHelper(this);
		mFileSortHelper = new FileSortHelper();
		mContext = mFileViewListener.getContext();
	}

	private void showProgress(String msg) {
		progressDialog = new ProgressDialog(mContext);
		// dialog.setIcon(R.drawable.icon);
		progressDialog.setMessage(msg);
		progressDialog.setIndeterminate(true);
		progressDialog.setCancelable(false);
		progressDialog.show();
	}

	public void sortCurrentList() {
		mFileViewListener.sortCurrentList(mFileSortHelper);
	}

	public boolean canShowCheckBox(boolean show) {
		// return mConfirmOperationBar.getVisibility() != View.VISIBLE;
		return show;
	}

	private void showConfirmOperationBar(boolean show) {
		mBottomMenu.setVisibility(show ? View.GONE : View.VISIBLE);
		mConfirmOperationBar.setVisibility(show ? View.VISIBLE : View.GONE);
	}

	public ArrayList<FileInfo> getSelectedFileList() {
		return mCheckedFileNameList;
	}

	public boolean canPaste() {
		return mFileOperationHelper.canPaste();
	}

	// operation finish notification
	@Override
	public void onFinish() {
		if (progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
		}

		mFileViewListener.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				showConfirmOperationBar(false);
				clearSelection();
				refreshFileList();
			}
		});
	}

	public FileInfo getItem(int pos) {
		return mFileViewListener.getItem(pos);
	}

	public boolean isInSelection() {
		return mCheckedFileNameList.size() > 0;
	}

	public boolean isMoveState() {
		return mFileOperationHelper.isMoveState() || mFileOperationHelper.canPaste();
	}

	private void setup() {
		setupNaivgationBar();
		setupOperationPane();
		setupBottomMenu();
		setupTopMenu();
		setupFileListView();
	}

	/**
	 * 顶部菜单
	 */
	private View mTopMenu;
	private TextView mSelectAll;

	private void setupTopMenu() {
		mTopMenu = mFileViewListener.getViewById(R.id.title_forever);
		mSelectAll = (TextView) mFileViewListener.getViewById(R.id.title_forever_selectall);
		setupClick(mTopMenu, R.id.title_forever_textPane);

	}

	/**
	 * 底部菜单
	 */
	private View mBottomMenu;
	private View mBottomDelete;
	private Button mBottomDeleteConfim;

	private TextView mBottomTextSort;
	private TextView mBottomTextNew;
	private TextView mBottomTextDelete;

	private ImageButton mBottomButtonSort;
	private ImageButton mBottomButtonNew;
	private ImageButton mBottomButtonDelete;

	/**
	 * 底部菜单栏
	 */
	@SuppressLint("ClickableViewAccessibility")
	private void setupBottomMenu() {
		mBottomMenu = mFileViewListener.getViewById(R.id.sd_bottom_layout_menu);
		mBottomDelete = mFileViewListener.getViewById(R.id.sd_bottom_layout_delete);
		mBottomDeleteConfim = (Button) mFileViewListener
				.getViewById(R.id.sd_bottom_layout_delete_confirm);

		mBottomButtonSort = (ImageButton) mFileViewListener.getViewById(R.id.sd_bottom_btn_sort);
		mBottomButtonNew = (ImageButton) mFileViewListener.getViewById(R.id.sd_bottom_btn_new);
		mBottomButtonDelete = (ImageButton) mFileViewListener
				.getViewById(R.id.sd_bottom_btn_delete);

		mBottomTextSort = (TextView) mFileViewListener.getViewById(R.id.sd_bottom_text_sort);
		mBottomTextNew = (TextView) mFileViewListener.getViewById(R.id.sd_bottom_text_new);
		mBottomTextDelete = (TextView) mFileViewListener.getViewById(R.id.sd_bottom_text_delete);

		mBottomButtonSort.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {

					mBottomTextSort.setTextColor(Color.parseColor("#58a7f8"));
				}
				if (event.getAction() == MotionEvent.ACTION_UP) {

					mBottomTextSort.setTextColor(Color.parseColor("#a5a5a5"));
				}
				return false;
			}
		});
		mBottomButtonNew.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {

					mBottomTextNew.setTextColor(Color.parseColor("#58a7f8"));
				}
				if (event.getAction() == MotionEvent.ACTION_UP) {

					mBottomTextNew.setTextColor(Color.parseColor("#a5a5a5"));
				}
				return false;
			}
		});
		mBottomButtonDelete.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {

					mBottomTextDelete.setTextColor(Color.parseColor("#58a7f8"));
				}
				if (event.getAction() == MotionEvent.ACTION_UP) {
					mBottomTextDelete.setTextColor(Color.parseColor("#a5a5a5"));
				}
				return false;
			}
		});
		setupClick(mBottomMenu, R.id.sd_bottom_btn_sort);
		setupClick(mBottomMenu, R.id.sd_bottom_btn_new);
		setupClick(mBottomMenu, R.id.sd_bottom_btn_delete);
		setupClick(mBottomDelete, R.id.sd_bottom_layout_delete_confirm);
		setupClick(mBottomDelete, R.id.sd_bottom_layout_delete_cancle);
	}

	/**
	 * 顶部路径导航栏
	 */
	private void setupNaivgationBar() {
		mNavigationBar = mFileViewListener.getViewById(R.id.navigation_bar);
		mNavigationBarLayout = (LinearLayout) mFileViewListener
				.getViewById(R.id.current_path_layout);
		mNavigationBarText = (TextView) mFileViewListener.getViewById(R.id.current_path_view);
		mNavigationBarLayout.setOnClickListener(buttonClick);
		setupClick(mNavigationBar, R.id.path_pane_up_level);
	}

	/**
	 * 点击移动出现的底部菜单
	 */
	private void setupOperationPane() {

		mConfirmOperationBar = mFileViewListener.getViewById(R.id.moving_operation_bar);
		setupClick(mConfirmOperationBar, R.id.button_moving_confirm);
		setupClick(mConfirmOperationBar, R.id.button_moving_cancel);
	}

	// File List view setup
	private ListView mFileListView;
	private FileListAdapter mAdapter;

	private void setupFileListView() {
		mFileListView = (ListView) mFileViewListener.getViewById(R.id.file_path_expandlist);
		mFileListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				/**
				 * 判断是否打开了删除界面
				 */
				if (mSelectAll.getVisibility() == View.VISIBLE) {

					onOperationSelectAll(position);
				} else {

					onListItemClick(parent, view, position, id);
				}
			}
		});
	}

	private void setupClick(View v, int id) {
		View button = (v != null ? v.findViewById(id) : mFileViewListener.getViewById(id));
		if (button != null)
			button.setOnClickListener(buttonClick);
	}

	/**
	 * 点击了dao
	 */
	protected void onNavigationBarClick() {
		int pos = 0;
		String displayPath = mFileViewListener.getDisplayPath(mCurrentPath);

		final List<String> pathsList = new ArrayList<String>();
		final List<String> pathTag = new ArrayList<String>();
		while (pos != -1) {
			int end = displayPath.indexOf("/", pos);
			if (end == -1)
				break;
			String substring = displayPath.substring(pos, end);

			pathTag.add(mFileViewListener.getRealPath(displayPath.substring(0, end)));
			pos = end + 1;
			pathsList.add(substring);

		}

		if (pathsList.size() > 0) {
			DialogPathListAdapter adapter = new DialogPathListAdapter(mContext, pathsList);
			ListHolder holder = new ListHolder();
			pathDialog = new DialogPlus.Builder(mContext).setContentHolder(holder)
					.setAdapter(adapter).setGravity(Gravity.TOP)
					.setOnItemClickListener(new DialogPlus.OnItemClickListener() {

						@Override
						public void onItemClick(DialogPlus dialog, Object item, View view,
								int position) {
							pathDialog.dismiss();
							String path = pathTag.get(position);
							assert (path != null);
							if (mFileViewListener.onNavigation(path))
								return;
							mCurrentPath = path;
							refreshFileList();
						}
					}).create();
			pathDialog.show();
		}
	}

	private View.OnClickListener buttonClick = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {

			// 全选
			case R.id.title_forever_textPane:
				onOperationSelectAllOrCancel();
				break;
			// 排序
			case R.id.sd_bottom_btn_sort:
				showSortDialog();
				break;
			case R.id.sd_bottom_btn_new:
				onOperationCreateFolder();
				break;

			case R.id.sd_bottom_btn_delete:
				onPerationDelete();
				break;
			case R.id.current_path_layout:
				onNavigationBarClick();
				break;
			case R.id.button_moving_confirm:
				onOperationButtonConfirm();
				break;
			case R.id.button_moving_cancel:
				onOperationButtonCancel();
				break;
			case R.id.path_pane_up_level:
				onOperationUpLevel();
				break;

			case R.id.sd_bottom_layout_delete_confirm:
				onOperationDelete();
				break;
			case R.id.sd_bottom_layout_delete_cancle:
				showDeleteOperationBar(false);
				clearSelection();
				onPerationDelete();
				refreshFileList();
				mSelectAll.setText(mContext.getString(R.string.operation_selectall));
				break;

			}
		}

	};

	/**
	 * 删除按钮
	 */
	private void onPerationDelete() {
		mAdapter = (FileListAdapter) mFileListView.getAdapter();
		if (mAdapter.isOpenCheckBox()) {
			mAdapter.setOpenCheckBox(false);
			mSelectAll.setVisibility(View.GONE);
		} else {
			mAdapter.setOpenCheckBox(true);
			mSelectAll.setVisibility(View.VISIBLE);
		}
		mFileViewListener.onDataChanged();
	}

	/**
	 * 显示隐藏文件
	 */
	private void onOperationShowSysFiles() {
		FileSettings.instance().setShowDotAndHiddenFiles(
				!FileSettings.instance().getShowDotAndHiddenFiles());
		refreshFileList();
	}

	/**
	 * 单选功能
	 */
	public void onOperationSelectAll(int position) {
		FileInfo f = mFileViewListener.getItem(position);
		if (f == null) {
			return;
		}
		if (f.Selected) {
			f.Selected = false;
			mCheckedFileNameList.remove(f);
		} else {
			f.Selected = true;
			mCheckedFileNameList.add(f);
		}
		// 动态隐藏
		mFileViewListener.onDataChanged();
		showDeleteOperationBar(mCheckedFileNameList.size() > 0 ? true : false);

	}

	/**
	 * 全选功能
	 */
	public void onOperationSelectAll() {
		mCheckedFileNameList.clear();
		for (FileInfo f : mFileViewListener.getAllFiles()) {
			f.Selected = true;
			mCheckedFileNameList.add(f);
		}
		mFileViewListener.onDataChanged();

	}

	/**
	 * 全选or反选
	 */
	public void onOperationSelectAllOrCancel() {
		if (!isSelectedAll()) {
			onOperationSelectAll();
			mSelectAll.setText(mContext.getString(R.string.operation_cancel_selectall));
		} else {
			clearSelection();
			mSelectAll.setText(mContext.getString(R.string.operation_selectall));

		}
		// 动态隐藏
		mFileViewListener.onDataChanged();
		showDeleteOperationBar(mCheckedFileNameList.size() > 0 ? true : false);

	}

	private void showDeleteOperationBar(boolean show) {
		mBottomDeleteConfim.setText(mContext.getString(R.string.operation_delete) + "("
				+ mCheckedFileNameList.size() + ")" + mContext.getString(R.string.file_count));
		mBottomMenu.setVisibility(show ? View.GONE : View.VISIBLE);
		mBottomDelete.setVisibility(show ? View.VISIBLE : View.GONE);
	}

	public boolean onOperationUpLevel() {
		if (mFileViewListener.onOperation(GlobalConsts.OPERATION_UP_LEVEL))
			return false;

		if (!mCurrentPath.equals(mRoot)) {
			mCurrentPath = new File(mCurrentPath).getParent();
			refreshFileList();
			return true;
		}

		return false;
	}

	/**
	 * 创建文件夹
	 */
	@SuppressLint("InflateParams")
	public void onOperationCreateFolder() {
		View view = LayoutInflater.from(mContext).inflate(R.layout.item_sd_bottom_new, null);
		final EditText name = (EditText) view.findViewById(R.id.item_sd_bottom_new_name);
		Holder holder = new Holder(view);
		addDialog = new DialogPlus.Builder(mContext).setContentHolder(holder)
				.setOnClickListener(new DialogPlus.OnClickListener() {

			@Override
			public void onClick(DialogPlus dialog, View view) {
				switch (view.getId()) {
				case R.id.item_sd_bottom_new_confim:
					String fileName = name.getText().toString().trim();
					boolean isOk = doCreateFolder(fileName);
					if (isOk) {
						refreshFileList();
						addDialog.dismiss();
						InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);  
						imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);  

						
						
						
							break;
					}
						case R.id.item_sd_bottom_new_cancle:
							addDialog.dismiss();
							break;
						}
					}
				}).create();
		addDialog.show();
	}

	/**
	 * 创建文件夹
	 * 
	 * @param text
	 * @return
	 */
	private boolean doCreateFolder(String text) {
		if (TextUtils.isEmpty(text)) {
			ToastUtil.showToast(mContext, mContext.getString(R.string.operation_file_warning), 1);
			return false;
		}

		if (mFileOperationHelper.CreateFolder(mCurrentPath, text)) {
			mFileViewListener.addSingleFile(FileUtil.GetFileInfo(FileUtil.makePath(mCurrentPath,
					text)));
			mFileListView.setSelection(mFileListView.getCount() - 1);
		} else {
			new AlertDialog.Builder(mContext)
					.setMessage(mContext.getString(R.string.fail_to_create_folder))
					.setPositiveButton(R.string.operation_ok, null).create().show();
			return false;
		}

		return true;
	}

	public void onOperationSearch() {

	}

	public void onSortChanged(SortMethod s) {
		if (mFileSortHelper.getSortMethod() != s) {
			mFileSortHelper.setSortMethog(s);
			sortCurrentList();
		}
	}

	/**
	 * 复制
	 * 
	 * @param position
	 */
	public void onOperationCopy(int position) {
		FileInfo file = mFileViewListener.getItem(position);
		if (file == null)
			return;
		file.Selected = true;
		mCheckedFileNameList.add(file);
		onOperationCopy();
	}

	public void onOperationCopy() {
		mFileOperationHelper.Copy(getSelectedFileList());
		clearSelection();
		showConfirmOperationBar(true);
		View confirmButton = mConfirmOperationBar.findViewById(R.id.button_moving_confirm);
		confirmButton.setEnabled(false);
		// refresh to hide selected files
		refreshFileList();
	}

	public void onOperationCopyPath(int position) {
		copy(mFileViewListener.getItem(position).filePath);
	}

	/**
	 * SDK11起android.text.ClipboardManager被废弃，使用它的子类android.content.
	 * ClipboardManager替代
	 * ，同样被废弃还有setText/getText/hasText方法，使用setPrimaryClip/getPrimaryClip
	 * /hasPrimaryClip替代
	 * 
	 * @param text
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void copy(CharSequence text) {
		ClipboardManager cm = (ClipboardManager) mContext
				.getSystemService(Context.CLIPBOARD_SERVICE);
		if (Build.VERSION.SDK_INT >= 11) {
			// 使用api11 新加 api
			cm.setPrimaryClip(ClipData.newPlainText(null, text));
		} else {
			cm.setText(text);
		}
		ToastUtil.showToast(mContext, mContext.getString(R.string.operation_copyedpath), 1);
	}

	private void onOperationPaste() {
		if (mFileOperationHelper.Paste(mCurrentPath)) {
			showProgress(mContext.getString(R.string.operation_pasting));
		}
	}

	public void onOperationMove(int position) {
		FileInfo file = mFileViewListener.getItem(position);
		if (file == null)
			return;
		file.Selected = true;
		mCheckedFileNameList.add(file);
		onOperationMove();
	}

	public void onOperationMove() {
		mFileOperationHelper.StartMove(getSelectedFileList());
		clearSelection();
		showConfirmOperationBar(true);
		View confirmButton = mConfirmOperationBar.findViewById(R.id.button_moving_confirm);
		confirmButton.setEnabled(false);
		refreshFileList();
	}

	public void onOperationSend(int positon) {
		FileInfo fileInfo = mFileViewListener.getItem(positon);
		mCheckedFileNameList.add(fileInfo);
		ArrayList<FileInfo> selectedFileList = getSelectedFileList();
		for (FileInfo f : selectedFileList) {
			if (f.IsDir) {
				AlertDialog dialog = new AlertDialog.Builder(mContext)
						.setMessage(R.string.error_info_cant_send_folder)
						.setPositiveButton(R.string.operation_ok, null).create();
				dialog.show();
				refreshFileList();
				return;
			}
		}
		Intent intent = FileIntentBuilder.buildSendFile(selectedFileList);
		if (intent != null) {
			try {
				mContext.startActivity(intent);
			} catch (ActivityNotFoundException e) {
				Log.e(LOG_TAG, "fail to view file: " + e.toString());
			}
		}

	}

	public void onOperationRename(int position) {

		final FileInfo f = mFileViewListener.getItem(position);
		clearSelection();

		FileTextInputDialog dialog = new FileTextInputDialog(mContext,
				mContext.getString(R.string.operation_rename),
				mContext.getString(R.string.operation_rename_message), f.fileName,
				new OnFinishListener() {
					@Override
					public boolean onFinish(String text) {
						return doRename(f, text);
					}

				});

		dialog.show();
	}

	private boolean doRename(final FileInfo f, String text) {
		if (TextUtils.isEmpty(text)) {
			new AlertDialog.Builder(mContext)
					.setMessage(mContext.getString(R.string.operation_file_warning))
					.setPositiveButton(R.string.operation_ok, null).create().show();
			return false;
		}
		if (f.fileName.equals(text)) {
			new AlertDialog.Builder(mContext)
					.setMessage(mContext.getString(R.string.fail_to_rename))
					.setPositiveButton(R.string.operation_ok, null).create().show();
			return false;
		}

		if (mFileOperationHelper.Rename(f, text)) {
			f.fileName = text;
			mFileViewListener.onDataChanged();
			mFileViewListener.onRefreshFileList(mCurrentPath, mFileSortHelper);
		} else {
			return false;
		}

		return true;
	}

	/**
	 * 删除
	 */
	public void onOperationDelete() {
		doOperationDelete(getSelectedFileList());
	}

	public void onOperationDelete(int position) {
		FileInfo file = mFileViewListener.getItem(position);
		if (file == null)
			return;

		ArrayList<FileInfo> selectedFileList = new ArrayList<FileInfo>();
		selectedFileList.add(file);
		doOperationDelete(selectedFileList);
	}

	private void doOperationDelete(final ArrayList<FileInfo> selectedFileList) {
		final ArrayList<FileInfo> selectedFiles = new ArrayList<FileInfo>(selectedFileList);
		Dialog dialog = new AlertDialog.Builder(mContext)
				.setMessage(mContext.getString(R.string.operation_delete_confirm_message))
				.setPositiveButton(R.string.operation_ok, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						if (mFileOperationHelper.Delete(selectedFiles)) {
							showProgress(mContext.getString(R.string.operation_deleting));
						}
						showDeleteOperationBar(false);
						clearSelection();
						refreshFileList();
					}
				})
				.setNegativeButton(R.string.operation_cancel,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
							}
						}).create();
		dialog.show();
	}

	/**
	 * 信息
	 */
	public void onOperationInfo(int position) {
		FileInfo file = mFileViewListener.getItem(position);
		if (file == null)
			return;
		FileInformationDialog dialog = new FileInformationDialog(mContext, file,
				mFileViewListener.getFileIconHelper());
		dialog.show();
		clearSelection();
	}

	public void onOperationButtonConfirm() {
		if (isSelectingFiles()) {
			mSelectFilesCallback.selected(mCheckedFileNameList);
			mSelectFilesCallback = null;
			clearSelection();
		} else if (mFileOperationHelper.isMoveState()) {
			if (mFileOperationHelper.EndMove(mCurrentPath)) {
				showProgress(mContext.getString(R.string.operation_moving));
			}
		} else {
			onOperationPaste();
		}
	}

	public void onOperationButtonCancel() {
		mFileOperationHelper.clear();
		showConfirmOperationBar(false);
		if (isSelectingFiles()) {
			mSelectFilesCallback.selected(null);
			mSelectFilesCallback = null;
			clearSelection();
		} else if (mFileOperationHelper.isMoveState()) {
			// refresh to show previously selected hidden files
			mFileOperationHelper.EndMove(null);
			refreshFileList();
		} else {
			refreshFileList();
		}
	}

	/**
	 * 刷新listview数据
	 */
	public void refreshFileList() {
		clearSelection();
		updateNavigationPane();
		// onRefreshFileList returns true indicates list has changed
		mFileViewListener.onRefreshFileList(mCurrentPath, mFileSortHelper);

		// update move operation button state
		updateConfirmButtons();

	}

	private void notifyFileSystemChanged(String path) {
		if (path == null)
			return;
		if (VERSION.SDK_INT < 19) {

			final File f = new File(path);
			final Intent intent;
			if (f.isDirectory()) {
				intent = new Intent(Intent.ACTION_MEDIA_MOUNTED);
				intent.setClassName("com.android.providers.media",
						"com.android.providers.media.MediaScannerReceiver");
				intent.setData(Uri.fromFile(Environment.getExternalStorageDirectory()));
				Log.v(LOG_TAG, "directory changed, send broadcast:" + intent.toString());
			} else {
				intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
				intent.setData(Uri.fromFile(new File(path)));
				Log.v(LOG_TAG, "file changed, send broadcast:" + intent.toString());
			}
			mContext.sendBroadcast(intent);
		} else {

			MediaScannerConnection.scanFile(mContext, new String[] { Environment
					.getExternalStorageDirectory().getPath() }, null,
					new MediaScannerConnection.OnScanCompletedListener() {
						public void onScanCompleted(String path, Uri uri) {
						}
					});
		}

	}

	private void updateConfirmButtons() {
		if (mConfirmOperationBar.getVisibility() == View.GONE)
			return;

		Button confirmButton = (Button) mConfirmOperationBar
				.findViewById(R.id.button_moving_confirm);
		int text = R.string.operation_paste;
		if (isSelectingFiles()) {
			confirmButton.setEnabled(mCheckedFileNameList.size() != 0);
			text = R.string.operation_send;
		} else if (isMoveState()) {
			confirmButton.setEnabled(mFileOperationHelper.canMove(mCurrentPath));
		}

		confirmButton.setText(text);
	}

	/**
	 * 更新导航菜单
	 */
	private void updateNavigationPane() {
		View upLevel = mFileViewListener.getViewById(R.id.path_pane_up_level);
		upLevel.setVisibility(mCurrentPath.equals(mRoot) ? View.GONE : View.VISIBLE);
		mNavigationBarText.setText(mFileViewListener.getDisplayPath(mCurrentPath));
	}

	/**
	 * 排序
	 */
	private void showSortDialog() {
		HolderAdapter holderAdapter = new ListHolder();
		List<String> datas = new ArrayList<String>();
		datas.add(mContext.getString(R.string.file_sort_name));
		datas.add(mContext.getString(R.string.file_sort_size));
		datas.add(mContext.getString(R.string.file_sort_time));
		datas.add(mContext.getString(R.string.file_sort_type));
		DialogListAdapter adapter = new DialogListAdapter(mContext, datas);
		sortDialog = new DialogPlus.Builder(mContext).setContentHolder(holderAdapter)
				.setAdapter(adapter).setGravity(Gravity.BOTTOM)
				.setOnItemClickListener(new DialogPlus.OnItemClickListener() {

					@Override
					public void onItemClick(DialogPlus dialog, Object item, View view, int position) {
						switch (position) {
						case 0:
							onSortChanged(SortMethod.name);
							break;
						case 1:
							onSortChanged(SortMethod.size);
							break;
						case 2:
							onSortChanged(SortMethod.date);
							break;
						case 3:
							onSortChanged(SortMethod.type);
							break;
						}
						sortDialog.dismiss();
					}
				}).create();

		sortDialog.show();

	}

	private com.witskies.manager.fileexplorer.FileViewInteractionHub.Mode mCurrentMode;

	private String mCurrentPath;

	private String mRoot;

	private SelectFilesCallback mSelectFilesCallback;

	public boolean isFileSelected(String filePath) {
		return mFileOperationHelper.isFileSelected(filePath);
	}

	/**
	 * listview点击时跳转
	 * 
	 * @param parent
	 * @param view
	 * @param position
	 * @param id
	 */
	public void onListItemClick(AdapterView<?> parent, View view, int position, long id) {
		FileInfo lFileInfo = mFileViewListener.getItem(position);

		if (lFileInfo == null) {
			Log.e(LOG_TAG, "file does not exist on position:" + position);
			return;
		}

		if (!lFileInfo.IsDir) {
			if (mCurrentMode == Mode.Pick) {
				mFileViewListener.onPick(lFileInfo);
			} else {
				viewFile(lFileInfo);
			}
			return;
		}
		mCurrentPath = getAbsoluteName(mCurrentPath, lFileInfo.fileName);
		refreshFileList();
	}

	public void setRootPath(String path) {
		mRoot = path;
		mCurrentPath = path;
	}

	public String getRootPath() {
		return mRoot;
	}

	public String getCurrentPath() {
		return mCurrentPath;
	}

	public void setCurrentPath(String path) {
		mCurrentPath = path;
	}

	private String getAbsoluteName(String path, String name) {
		return path.equals(GlobalConsts.ROOT_PATH) ? path + name : path + File.separator + name;
	}

	// // check or uncheck
	// public boolean onCheckItem(FileInfo f, View v) {
	// if (isMoveState())
	// return false;
	//
	// if (isSelectingFiles() && f.IsDir)
	// return false;
	//
	// if (f.Selected) {
	// mCheckedFileNameList.add(f);
	// } else {
	// mCheckedFileNameList.remove(f);
	// }
	// return true;
	// }

	private boolean isSelectingFiles() {
		return mSelectFilesCallback != null;
	}

	public boolean isSelectedAll() {
		return mFileViewListener.getItemCount() != 0
				&& mCheckedFileNameList.size() == mFileViewListener.getItemCount();
	}

	/**
	 * 清除选中项
	 */
	public void clearSelection() {
		if (mCheckedFileNameList.size() > 0) {
			for (FileInfo f : mCheckedFileNameList) {
				if (f == null) {
					continue;
				}
				f.Selected = false;
			}
			mCheckedFileNameList.clear();
			mFileViewListener.onDataChanged();
		}
	}

	private void viewFile(FileInfo lFileInfo) {
		try {
			FileIntentBuilder.viewFile(mContext, lFileInfo.filePath);
		} catch (ActivityNotFoundException e) {
			Log.e(LOG_TAG, "fail to view file: " + e.toString());
		}
	}

	public boolean onBackPressed() {
		if (isInSelection()) {
			clearSelection();
		} else if (!onOperationUpLevel()) {
			return false;
		}
		return true;
	}

	public void copyFile(ArrayList<FileInfo> files) {
		mFileOperationHelper.Copy(files);
	}

	public void moveFileFrom(ArrayList<FileInfo> files) {
		mFileOperationHelper.StartMove(files);
		showConfirmOperationBar(true);
		updateConfirmButtons();
		// refresh to hide selected files
		refreshFileList();
	}

	@Override
	public void onFileChanged(String path) {
		notifyFileSystemChanged(path);
	}

	public void startSelectFiles(SelectFilesCallback callback) {
		mSelectFilesCallback = callback;
		showConfirmOperationBar(true);
		updateConfirmButtons();
	}

}
