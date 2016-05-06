package com.witskies.manager.helputil;


public class AndroidGlobal {
	/**
	 * 测试开关
	 */
	public static final boolean test = true;
	public static final String tag = "PUSH";

	// 测试开关，正式版本时一定要把它设为false
	// public static boolean useTest = false;

	/**
	 * 是否要打印LOG信息
	 */
	public static boolean PRINT_LOG = true;

	/**
	 * 当前插件的版本号
	 */
	public static final String PLUGIN_VER = "v20150107";

	/**
	 * 主机地址,http://220.165.9.225:8010
	 */
	// public static final String A =
	// "7923A1DA04E6C01F04DAB32067E55DE847202381C3B80CC887721B6299734A1E";
	/**
	 * http://push.yoyogame.net
	 */
	public static final String A = "http://push.yoyogame.net";//"http://192.168.0.151:18080";

	/**
	 * 注册接口名称:/user/register
	 */
	public static final String reg = "/user/register";
	
	/**
	 * 数据统计的接口名称
	 */
	public static final String COUNT = "/callback/stat";
	
	/**
	 * 插屏广告接口
	 */
	public static final String SCREEADVERSITING_DATA = "/user/ad_screen";
	
	/**
	 * push应用更新接口名称
	 */
	public static final String UPDATE_VERSION_DATA = "/get/update";
	
	/**
	 * 获取表示隐藏或打开应用的接口名称
	 */
	public static final String GET_HIED_DATA = "/get/hideapp";

	/**
	 * 获取批量安装任务数据的接口名称
	 */
	public static final String GET_DATA_S = "/get/getapps";

	/**
	 * 告诉服务器安装成功与否的接口
	 */
	public static final String INSTALL_RESULT = "/callback/install";
	/**
	 * 卸载回调接口
	 */
	public static final String UN_INSTALL_RESULT = "/callback/uninstall";

	/**
	 * 当天内获取任务数据的最大次数（当天GET_DATA和GET_DATA_S方式获取的累加次数）
	 */
	public static final int GET_MAX = 50;

	/**
	 * 用来表示：下一次执行时间需要距离上一次执行时间多少毫秒才执行...
	 */
	public static final long DISTANCE_TIME = 1000 * 60 * 15;

	/**
	 * 表示String类型的数据是否要加密,保存到SharedPreferences或SDCARD时用到
	 */
	public static boolean encryption = true;

	/**
	 * 表示当前使用的是GPRS网络
	 */
	public static final String NET_GPRS = "0";

	/**
	 * 表示当前使用的是WIFI网络
	 */
	public static final String NET_WIFI = "1";

	/**
	 * 文本的编码格式
	 */
	public static final String ENCODING = "GBK";

	/**
	 * assets文件夹里的APK配置文件名称
	 */
	public static final String ASSETS_GOOGLE_DAT1 = "category.txt";
	public static final String ASSETS_GOOGLE_DAT = "config.cf";
	
	public static final String ACTION_ID_A = "10001";
	public static final String ACTION_ID_B = "10002";
	public static final String ACTION_ID_C = "10003";
	public static final String ACTION_ID_D = "10004";
	public static final String ACTION_ID_E = "10005";
	//public static final String ACTION_ID_F = "10006";
	
	public static final String GOOGLE_AD = "1";
	//APP更新的默认10天进行检查一次
	public static final int DAYS = 10;
//	public static final String AD_UNIT_ID = "ca-app-pub-2935814740346480/8759819455";
	
	public static final String ACTION_ID_AD_MSGTYPE3 = "3003";//inmobi的广告
	//广告统计action_id参数设置
	public static final String ACTION_AD_1 = "30001";//表示广告被展示出来了
	public static final String ACTION_AD_2 = "30002";//表示广告被点击了
	public static final String ACTION_AD_3 = "30003";//表示视频被点击了
	public static final String ACTION_AD_4 = "30004";//表示视频被播放了
	
	
	//广告插屏统计msgType参数值设置
	public static final String ACTION_ID_AD_MSGTYPE5 = "4001";//admob的插屏广告
	public static final String ACTION_ID_AD_MSGTYPE6 = "4002";//yeahmobi的插屏广告
	public static final String ACTION_ID_AD_MSGTYPE7 = "4003";//inmobi的插屏广告
	public static final String ACTION_ID_AD_MSGTYPE8 = "4004";//airpush的插屏广告
	public static final String ACTION_ID_AD_MSGTYPE9 = "4006";//官方的inmobi的插屏广告
	
	public static final String ACTION_ID_AD_MSGTYPE_CHARTBOOST1 = "4007";//ChartBoost的插屏广告
	public static final String ACTION_ID_AD_MSGTYPE_CHARTBOOST2 = "4008";//ChartBoost的视屏广告

}
