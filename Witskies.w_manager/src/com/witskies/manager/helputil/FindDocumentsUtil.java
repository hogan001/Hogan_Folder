package com.witskies.manager.helputil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;

import com.witskies.manager.bean.AdapterItemBean;
import com.witskies.manager.database.MobileContent;
import com.witskies.manager.util.CommentAdapterHelpUtil;

/**
 * @作者 ch
 * @描述 搜索手机里面所有的文档资料（SD卡手机内部）
 * @时间 2015年5月8日 上午10:18:31
 */
public class FindDocumentsUtil {

	private String inPath;
	private String sdPath;
	public List<String> mDocPaths;

	public FindDocumentsUtil(String inDir, String sdDir) {
		inPath = inDir;
		sdPath = sdDir;
		mDocPaths = new ArrayList<String>();
		if (inDir != null) {

			getsdCardFile(inPath);
		}
		if (sdDir != null) {

			getsdCardFile(sdPath);
		}

	}

	// public FindDocumentsUtil(String inDir, Context context) {
	// inPath = inDir;
	// this.context = context;
	// documents.removeAll(documents);
	// getInternalFile(inPath);
	//
	// }

	private List<String> getsdCardFile(String sdPath2) {
		File[] files = new File(sdPath2).listFiles();
		if (files != null && files.length > 0) {
			if (files != null) {
				for (int i = 0; i < files.length; i++) {
					File f = files[i];
					if (f.isFile()) {
						String end = f.getPath().substring(f.getPath().lastIndexOf(".") + 1);
						if (end.equals("txt") || end.equals("doc") || end.equals("docx")
								|| end.equals("pdf") || end.equals("xls") || end.equals("xlsx")
								|| end.equals("ppt") || end.equals("xml") || end.equals("html")) {
							File file = new File(f.getPath());
							if (file.length() > 0) {
								mDocPaths.add(f.getPath());
							}
						}

					} else if (f.isDirectory() && f.getPath().indexOf("/.") == -1) {
						getsdCardFile(f.getPath());
					}
				}
			}
		}
		return mDocPaths;
	}

	// private void getInternalFile(String inPath2) {
	// File[] files = new File(inPath2).listFiles();
	// if (files != null && files.length > 0) {
	// for (int i = 0; i < files.length; i++) {
	// File f = files[i];
	// if (f.isFile()) {
	//
	// String end = f.getPath().substring(f.getPath().lastIndexOf(".") + 1);
	//
	// if (end.equals("txt") || end.equals("doc") || end.equals("docx")
	// || end.equals("pdf") || end.equals("xls") || end.equals("xlsx")
	// || end.equals("ppt") || end.equals("xml") || end.equals("html")) {
	// AdapterItemBean adapterItemBean = new AdapterItemBean();
	// adapterItemBean.setDocumenCurrPath(f.getAbsolutePath());
	// String name = f.getPath().substring(f.getPath().lastIndexOf("/") + 1);
	// String data = CommentAdapterHelpUtil.getInstantiation().getDate(
	// f.getAbsolutePath());
	// String size =
	// CommentAdapterHelpUtil.getInstantiation().getSize(f.length());
	// if (!size.equals("0.00B")) {
	// adapterItemBean.setDocumentName(name);
	// adapterItemBean.setDocumentSize(size);
	// adapterItemBean.setDocumentTime(data);
	// Log.e("FindDocumentsUtil文档内容", name + ">>>" + data + ">>>" + size);
	// documents.add(adapterItemBean);
	// cMobileContent.insertDocument(f.getAbsolutePath());
	// }
	// }
	//
	// } else if (f.isDirectory() && f.getPath().indexOf("/.") == -1) {
	// getInternalFile(f.getPath());
	// }
	// }
	// }
	// }

}
