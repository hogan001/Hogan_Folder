package com.witskies.manager.fileexplorer;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.ActionMode;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.witskies.manager.app.ActivitiesManager;
import com.witskies.manager.fileexplorer.FileListAdapter.DropDownListener;
import com.witskies.manager.helputil.FileUtil;
import com.witskies.w_manager.R;

/**
 * sd卡主界面
 * 
 * @author 陈蓝�? * @version 创建时间�?015-4-29 下午5:29:17
 */
public class FileMainActivity extends FragmentActivity {

	public static final String EXT_FILETER_KEY = "ext_filter";

	private static final String LOG_TAG = "FileViewActivity";

	public static final String EXT_FILE_FIRST_KEY = "ext_file_first";

	public static final String ROOT_DIRECTORY = "root_directory";

	public static final String PICK_FOLDER = "pick_folder";

	private ListView mFileListView;

	// private TextView mCurrentPathTextView;
	private FileListAdapter mAdapter;

	private FileViewInteractionHub mFileViewInteractionHub;

	private FileCategoryHelper mFileCagetoryHelper;

	private FileIconHelper mFileIconHelper;

	private ArrayList<FileInfo> mFileNameList = new ArrayList<FileInfo>();
	private boolean mBackspaceExit;
	// memorize the scroll positions of previous paths
	private ArrayList<PathScrollPositionItem> mScrollPositionList = new ArrayList<PathScrollPositionItem>();
	private String mPreviousPath;
	/**
	 * 接收的路径
	 */

	private String mPathShow;
	/**
	 * 刷新UI的广播，ACTION_MEDIA_MOUNTED变化
	 */
	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {

			String action = intent.getAction();
			Log.v(LOG_TAG, "received broadcast:" + intent.toString());
			if (action.equals(Intent.ACTION_MEDIA_MOUNTED) || action.equals(Intent.ACTION_MEDIA_UNMOUNTED)) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						updateUI();
					}
				});
			}
		}
	};

	@Override
	protected void onCreate(Bundle arg0) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		ActivitiesManager.getInstance().registerActivity(FileMainActivity.this);
		setContentView(R.layout.sd_file_explorer);
		initViews();
		updateUI();
		super.onCreate(arg0);
	}

	private void initViews() {

		mFileCagetoryHelper = new FileCategoryHelper(this);
		mFileViewInteractionHub = new FileViewInteractionHub(new MyIFileInteractionListener());
		Intent intent = getIntent();
		String action = intent.getAction();
		mPathShow = intent.getStringExtra("SDPATH");
		if (!TextUtils.isEmpty(action) && (action.equals(Intent.ACTION_PICK) || action.equals(Intent.ACTION_GET_CONTENT))) {

			boolean pickFolder = intent.getBooleanExtra(PICK_FOLDER, false);
			if (!pickFolder) {
				String[] exts = intent.getStringArrayExtra(EXT_FILETER_KEY);
				if (exts != null) {
					mFileCagetoryHelper.setCustomCategory(exts);
				}
			} else {
				mFileCagetoryHelper.setCustomCategory(new String[] {} /*
																	 * folder
																	 * only
																	 */);
				findViewById(R.id.pick_operation_bar).setVisibility(View.VISIBLE);

				findViewById(R.id.button_pick_confirm).setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						try {
							Intent intent = Intent.parseUri(mFileViewInteractionHub.getCurrentPath(), 0);
							setResult(Activity.RESULT_OK, intent);
							finish();
						} catch (URISyntaxException e) {
							e.printStackTrace();
						}
					}
				});

				findViewById(R.id.button_pick_cancel).setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						finish();
					}
				});
			}
		} else {
		}

		mFileListView = (ListView) findViewById(R.id.file_path_expandlist);
		mFileIconHelper = new FileIconHelper(this);
		// mAdapter = new FileListAdapter(this, R.layout.file_browse_item,
		// mFileNameList, mFileViewInteractionHub, mFileIconHelper);

		mAdapter = new FileListAdapter(this, mFileNameList, mFileViewInteractionHub, mFileIconHelper, downListener);

		boolean baseSd = intent.getBooleanExtra(GlobalConsts.KEY_BASE_SD, true);
		// final String sdDir = FileUtil.getSdDirectory();
		String rootDir = intent.getStringExtra(ROOT_DIRECTORY);
		if (!TextUtils.isEmpty(rootDir)) {
			if (baseSd && mPathShow.startsWith(rootDir)) {
				rootDir = mPathShow;
			}
		} else {
			rootDir = baseSd ? mPathShow : GlobalConsts.ROOT_PATH;
		}
		mFileViewInteractionHub.setRootPath(rootDir);

		String currentDir = null;
		Uri uri = intent.getData();
		if (uri != null) {
			if (baseSd && mPathShow.startsWith(uri.getPath())) {
				currentDir = mPathShow;
			} else {
				currentDir = uri.getPath();
			}
			mFileViewInteractionHub.setCurrentPath(currentDir);
		}

		mBackspaceExit = (uri != null)
				&& (TextUtils.isEmpty(action) || (!action.equals(Intent.ACTION_PICK) && !action.equals(Intent.ACTION_GET_CONTENT)));

		mFileListView.setAdapter(mAdapter);

		mFileViewInteractionHub.refreshFileList();

		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);
		intentFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
		intentFilter.addDataScheme("file");
		registerReceiver(mReceiver, intentFilter);

		LinearLayout back = (LinearLayout) findViewById(R.id.title_forever_back);
		TextView title = (TextView) findViewById(R.id.title_forever_title);
		title.setText("SD");
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();
			}
		});

	}

	private DropDownListener downListener = new DropDownListener() {

		@Override
		public void onDropDiwnClick(int position, View view) {
			mAdapter.clearHideMenu();
			if (view.getVisibility() == View.GONE) {
				view.setVisibility(View.VISIBLE);
				mAdapter.openHideMenu(position, true);
			}
			mAdapter.notifyDataSetChanged();
		}
	};

	@Override
	protected void onDestroy() {
		unregisterReceiver(mReceiver);
		super.onDestroy();
	}

	@Override
	public void onBackPressed() {
		// 控制路径的跳�?
		boolean isReturn = !mBackspaceExit && mFileViewInteractionHub.onBackPressed();
		if (!isReturn) {
			super.onBackPressed();
		}
	}

	/**
	 * 设置弹出菜单
	 */
	private ActionMode mActionMode;

	public void setActionMode(ActionMode actionMode) {
		mActionMode = actionMode;
	}

	public ActionMode getActionMode() {
		return mActionMode;
	}

	private class PathScrollPositionItem {
		String path;
		int pos;

		PathScrollPositionItem(String s, int p) {
			path = s;
			pos = p;
		}
	}

	// execute before change, return the memorized scroll position
	private int computeScrollPosition(String path) {
		int pos = 0;
		if (mPreviousPath != null) {
			if (path.startsWith(mPreviousPath)) {
				int firstVisiblePosition = mFileListView.getFirstVisiblePosition();
				if (mScrollPositionList.size() != 0 && mPreviousPath.equals(mScrollPositionList.get(mScrollPositionList.size() - 1).path)) {
					mScrollPositionList.get(mScrollPositionList.size() - 1).pos = firstVisiblePosition;
					Log.i(LOG_TAG, "computeScrollPosition: update item: " + mPreviousPath + " " + firstVisiblePosition + " stack count:"
							+ mScrollPositionList.size());
					pos = firstVisiblePosition;
				} else {
					mScrollPositionList.add(new PathScrollPositionItem(mPreviousPath, firstVisiblePosition));
					Log.i(LOG_TAG, "computeScrollPosition: add item: " + mPreviousPath + " " + firstVisiblePosition + " stack count:"
							+ mScrollPositionList.size());
				}
			} else {
				int i;
				for (i = 0; i < mScrollPositionList.size(); i++) {
					if (!path.startsWith(mScrollPositionList.get(i).path)) {
						break;
					}
				}
				// navigate to a totally new branch, not in current stack
				if (i > 0) {
					pos = mScrollPositionList.get(i - 1).pos;
				}

				for (int j = mScrollPositionList.size() - 1; j >= i - 1 && j >= 0; j--) {
					mScrollPositionList.remove(j);
				}
			}
		}

		Log.i(LOG_TAG, "computeScrollPosition: result pos: " + path + " " + pos + " stack count:" + mScrollPositionList.size());
		mPreviousPath = path;
		return pos;
	}

	/**
	 * 更新UI
	 */
	private void updateUI() {
		boolean sdCardReady = FileUtil.isSDCardReady();
		View noSdView = findViewById(R.id.sd_not_available_page);
		noSdView.setVisibility(sdCardReady ? View.GONE : View.VISIBLE);

		View navigationBar = findViewById(R.id.navigation_bar);
		navigationBar.setVisibility(sdCardReady ? View.VISIBLE : View.GONE);
		mFileListView.setVisibility(sdCardReady ? View.VISIBLE : View.GONE);

		if (sdCardReady) {
			mFileViewInteractionHub.refreshFileList();
		}
	}

	/**
	 * 显示�? * @param show
	 */
	private void showEmptyView(boolean show) {
		View emptyView = findViewById(R.id.empty_view);
		if (emptyView != null)
			emptyView.setVisibility(show ? View.VISIBLE : View.GONE);
	}

	public interface SelectFilesCallback {
		void selected(ArrayList<FileInfo> files);
	}

	public void startSelectFiles(SelectFilesCallback callback) {
		mFileViewInteractionHub.startSelectFiles(callback);
	}

	private class MyIFileInteractionListener implements IFileInteractionListener {
		@Override
		public View getViewById(int id) {
			return findViewById(id);
		}

		@Override
		public Context getContext() {
			return FileMainActivity.this;
		}

		@Override
		public void onDataChanged() {
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					mAdapter.clearHideMenu();
					mAdapter.notifyDataSetChanged();
				}

			});
		}

		@Override
		public void onPick(FileInfo f) {
			try {
				Intent intent = Intent.parseUri(Uri.fromFile(new File(f.filePath)).toString(), 0);
				setResult(Activity.RESULT_OK, intent);
				finish();
				return;
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
		}

		@Override
		public boolean shouldShowOperationPane() {
			return true;
		}

		@Override
		public boolean onOperation(int id) {
			return false;
		}

		@Override
		public String getDisplayPath(String path) {
			String root = mFileViewInteractionHub.getRootPath();

			if (root.equals(path))
				return getString(R.string.sd_folder);

			if (!root.equals("/")) {
				int pos = path.indexOf(root);
				if (pos == 0) {
					path = path.substring(root.length());
				}
			}

			return getString(R.string.sd_folder) + path;
		}

		@Override
		public String getRealPath(String displayPath) {
			String root = mFileViewInteractionHub.getRootPath();
			if (displayPath.equals(getString(R.string.sd_folder)))
				return root;

			String ret = displayPath.substring(displayPath.indexOf("/"));
			if (!root.equals("/")) {
				ret = root + ret;
			}
			return ret;
		}

		@Override
		public boolean onNavigation(String path) {
			return false;
		}

		@Override
		public boolean shouldHideMenu(int menu) {
			return false;
		}

		@Override
		public FileIconHelper getFileIconHelper() {
			return mFileIconHelper;
		}

		@Override
		public FileInfo getItem(int pos) {
			if (pos < 0 || pos > mFileNameList.size() - 1)
				return null;

			return mFileNameList.get(pos);
		}

		@SuppressWarnings("unchecked")
		@Override
		public void sortCurrentList(FileSortHelper sort) {
			Collections.sort(mFileNameList, sort.getComparator());
			onDataChanged();
		}

		@Override
		public ArrayList<FileInfo> getAllFiles() {
			return mFileNameList;
		}

		@Override
		public void addSingleFile(FileInfo file) {
			mFileNameList.add(file);
			onDataChanged();
		}

		@Override
		public int getItemCount() {
			return mFileNameList.size();
		}

		@Override
		public void startActivity(Intent intent) {

		}

		@Override
		public void runOnUiThread(Runnable r) {
			FileMainActivity.this.runOnUiThread(r);

		}

		@Override
		public boolean onRefreshFileList(String path, FileSortHelper sort) {
			File file = new File(path);
			if (!file.exists() || !file.isDirectory()) {
				return false;
			}
			final int pos = computeScrollPosition(path);
			ArrayList<FileInfo> fileList = mFileNameList;
			fileList.clear();

			File[] listFiles = file.listFiles(mFileCagetoryHelper.getFilter());
			if (listFiles == null)
				return true;

			for (File child : listFiles) {
				// do not show selected file if in move state
				if (mFileViewInteractionHub.isMoveState() && mFileViewInteractionHub.isFileSelected(child.getPath()))
					continue;

				String absolutePath = child.getAbsolutePath();
				if (FileUtil.isNormalFile(absolutePath) && FileUtil.shouldShowFile(absolutePath)) {
					FileInfo lFileInfo = FileUtil.GetFileInfo(child, mFileCagetoryHelper
							.getFilter(), FileSettings.instance().getShowDotAndHiddenFiles());
					if (lFileInfo != null) {
						fileList.add(lFileInfo);
					}
				}
			}

			sortCurrentList(sort);
			showEmptyView(fileList.size() == 0);
			mFileListView.post(new Runnable() {
				@Override
				public void run() {
					mFileListView.setSelection(pos);
				}
			});
			return true;
		}
	}

}
