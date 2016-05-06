package com.witskies.manager.util;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.witskies.manager.bean.AdapterItemBean;

/**
 * @作者 ch
 * @描述 用于解析从数据库暴露出来的数据转换为需要的数据
 * @时间 2015年5月20日 上午11:23:43
 */
public class AnalyticalData {

	private static AnalyticalData analyticalData;
	/**
	 * 所有的文件夹路径
	 */
	private List<String> mAllParentPath = new ArrayList<String>();

	public List<String> getmAllParentPath() {
		return mAllParentPath;
	}

	public void setmAllParentPath(List<String> mAllParentPath) {
		this.mAllParentPath = mAllParentPath;
	}

	public static AnalyticalData getInstance() {
		if (analyticalData == null) {
			synchronized (AnalyticalData.class) {
				if (analyticalData == null) {
					analyticalData = new AnalyticalData();
				}
			}

		}
		return analyticalData;
	}

	public ArrayList<AdapterItemBean> anlyticalDataToNeed(List<String> dataList) {
		/**
		 * 临时的辅助类，用于防止同一个文件夹的多次扫描
		 */
		HashSet<String> mDirPaths = new HashSet<String>();

		ArrayList<AdapterItemBean> adapteList = new ArrayList<AdapterItemBean>();
		for (int i=0;i<dataList.size();i++) {
			AdapterItemBean adapterItemBean = new AdapterItemBean();
			File file = new File(dataList.get(i));
			String date = CommentAdapterHelpUtil.getInstantiation().getDate(file);
			String size = CommentAdapterHelpUtil.getInstantiation().getSize(file.length());
			adapterItemBean.setCurrPath(dataList.get(i));
			adapterItemBean.setName(file.getName());
			adapterItemBean.setSize(size);
			adapterItemBean.setTime(date);
			adapterItemBean.setId(i);
			adapteList.add(adapterItemBean);
			// 遍历父亲路径
			File parentFile = file.getParentFile();
			if (parentFile == null)
				continue;
			// 父路径
			String parentPath = parentFile.getAbsolutePath();
			try {
				if (mDirPaths != null) {
					if (mDirPaths.contains(parentPath)) {
						// 结束当前循环
						continue;
					} else {
						mAllParentPath.add(parentPath);
						mDirPaths.add(parentPath);
					}
				}
			} catch (Exception e) {
			}

		}

		mDirPaths = null;
		return adapteList;
	}

}
