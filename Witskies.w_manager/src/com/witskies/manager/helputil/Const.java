package com.witskies.manager.helputil;

import android.os.Environment;

/**
 * @描述 常量类，用于定义网络接口
 * @时间 2015-4-1 上午9:58:25
 */
public class Const {
	/** 相关工具栏推荐的APK的前面部份地址 */
	public static final String START = "http://file.witskies.net/api/app/";
	/** 注册接口 */
	public static final String SERVER_REGIST = "http://file.witskies.net/api/user/register";

	/** 注册时必须调用，用来统计销售人员 */
	public static final String SERVER_REGIST_MUST = "http://psm-api.witskies.net/api/register";

	/** 获取推荐信息 */
	public static final String SERVER_RECOMMEND = "http://file.witskies.net/api/recommend";

	/** 获取网络分类结果 */
	public static final String SERVER_CLASS = "http://file.witskies.net/api/app/classify";

	/** 启动统计接口 */
	public static final String START_URL = "http://file.witskies.net/api/stat/startup";
	/** apk检查更新接口 */
	public static final String APPUPDATE_URL = "http://psm-api.witskies.net/api/upgrade";

	/**
	 * 下载保存地址
	 */
	public static final String DOWNLOAD_APK_PATH = "Download_From_W_Apk";
}
