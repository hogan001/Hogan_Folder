package com.witskies.manager.util;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @作者 ch
 * @描述 专为（视频，音乐，文档，图片）写的一个转化为时间，大小的一个类
 * @时间 2015年5月6日 上午10:34:41
 */
public class CommentAdapterHelpUtil {
	private static CommentAdapterHelpUtil helpUtil;

	public static CommentAdapterHelpUtil getInstantiation() {
		if (helpUtil == null) {
			helpUtil = new CommentAdapterHelpUtil();
		}
		return helpUtil;
	}

	/**
	 * @描述 传一个路径字符串，得到最近修改的时间
	 * @时间 2015年5月6日 上午10:41:27
	 */
	public String getDate(String currPath) {
		File file = new File(currPath);
		Date date = new Date(file.lastModified());
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String currenTime = simpleDateFormat.format(date);
		return currenTime;
	}

	/**
	 * @描述 传一个file，得到最近修改的时间
	 * @时间 2015年5月6日 上午10:41:27
	 */
	public String getDate(File file) {
		Date date = new Date(file.lastModified());
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String currenTime = simpleDateFormat.format(date);
		return currenTime;
	}

	/**
	 * @描述 传一个文件大小，得到一个符合要求大小的字符串
	 * @时间 2015年5月6日 上午10:55:03
	 */
	public String getSize(long fileSize) {

		String before = null;// 小数点前部分
		String after = null;// 小数点后部分
		String needSize = null;// 我们需要的字段
		// 以M为单位
		if (fileSize > 1024 * 1024) {
			float size = (float) fileSize / 1024 / 1024;
			String sizeFile = String.valueOf(size);

			if (sizeFile.contains(".")) {
				if (sizeFile.contains("E")) {
					// 不要问我为什么，经验任性
					needSize = "0.00M";
				} else {
					int i = sizeFile.indexOf(".");
					if (sizeFile.length() > i + 2) {
						before = sizeFile.substring(0, i);
						after = sizeFile.substring(i, i + 3);
						needSize = before + after + "M";
					} else {

						needSize = sizeFile + "0M";
					}
				}
			}
		}
		// 以KB为单位
		else if (fileSize > 1024) {

			float size = (float) fileSize / 1024;
			String sizeFile = String.valueOf(size);

			if (sizeFile.contains(".")) {
				if (sizeFile.contains("E")) {
					// 不要问我为什么，经验任性
					needSize = "0.00K";
				} else {
					int i = sizeFile.indexOf(".");
					if (sizeFile.length() > i + 2) {
						before = sizeFile.substring(0, i);
						after = sizeFile.substring(i, i + 3);
						needSize = before + after + "K";
					} else {

						needSize = sizeFile + "0K";
					}
				}
			}

		}
		// //以B为单位
		else {
			float size = (float) fileSize;
			String sizeFile = String.valueOf(size);

			if (sizeFile.contains(".")) {
				if (sizeFile.contains("E")) {
					// 不要问我为什么，经验任性
					needSize = "0.00B";
				} else {
					int i = sizeFile.indexOf(".");
					if (sizeFile.length() > i + 2) {
						before = sizeFile.substring(0, i);
						after = sizeFile.substring(i, i + 3);
						needSize = before + after + "B";
					} else {

						needSize = sizeFile + "0B";
					}
				}
			}

		}

		return needSize;

	}
}
