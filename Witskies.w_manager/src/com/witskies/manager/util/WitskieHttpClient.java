package com.witskies.manager.util;

import java.io.IOException;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import com.witskies.manager.activity.MainActivity;
import com.witskies.w_manager.R;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * *
 * 
 * @作者 ch
 * @描述 一个用于网络访问的类
 * @时间 2015-3-23 下午3:59:44
 */
@SuppressWarnings("deprecation")
public class WitskieHttpClient {
	private static WitskieHttpClient loginHttp;
	private Context mContext;

	public WitskieHttpClient(Context context) {
		// TODO Auto-generated constructor stub
		this.mContext = context;
	}

	public static WitskieHttpClient getInstance(Context context) {

		if (loginHttp == null) {
			synchronized (WitskieHttpClient.class) {
				if (loginHttp == null) {
					loginHttp = new WitskieHttpClient(context);
				}
			}
		}
		return loginHttp;
	}

	/** 启动时向服务器发送一个请求就OK(启动统计) */
	public void justAccessOneNet(String urlPath) {
		// 新建HttpGet对象
		HttpGet httpGet = new HttpGet(urlPath);
		httpGet.addHeader("ZH-WF-TOKEN", MainActivity.access_key);
		httpGet.addHeader("language", mContext.getString(R.string.w_language));
		// 获取HttpClient对象
		HttpClient httpClient = new DefaultHttpClient();
		// 请求超时15秒
		httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 15000);
		// 读取超时 5秒
		httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 5000);

		// 获取HttpResponse实例
		HttpResponse httpResp;

		try {

			httpResp = httpClient.execute(httpGet);

			// 判断是够请求成功
			if (httpResp.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {

				// 获取返回的数据
				String result = EntityUtils.toString(httpResp.getEntity(), "UTF-8");
				Log.e("WitskieHttpClient启动统计返回结果：", result + " ");

			}

		} catch (ClientProtocolException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();

		}

	}

	/** 注册接口 (默认注册,记录设备信息) */
	public void postToHttp(String url, List<NameValuePair> paras, Handler handler) {

		HttpPost request = new HttpPost(url);
		request.addHeader("language", mContext.getString(R.string.w_language));
		String msg = null;
		Message message = null;
		if (handler != null) {
			message = handler.obtainMessage();
			message.what = 10001;
		}

		try {
			request.setEntity(new UrlEncodedFormEntity(paras, HTTP.UTF_8));
			request.addHeader("language", "zh_CN");
			HttpResponse response = new DefaultHttpClient().execute(request);
			// 请求超时15秒
			response.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 15000);
			// 读取超时 5秒
			response.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 5000);

			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				msg = EntityUtils.toString(response.getEntity());
				Log.e("WitskieHttpClient注册时必须调用返回结果(销售与注册)", msg + " ");

			}
			if (handler != null) {
				message.obj = msg;
				handler.sendMessage(message);
			}
		} catch (Exception e) {
			e.printStackTrace();
			if (handler != null) {
				message.obj = msg;
				handler.sendMessage(message);
			}

		}

	}

	/** 归类(网络分类接口)返回分类数据 */
	public String postToNetClassify(String url, List<NameValuePair> paras) {

		HttpPost request = new HttpPost(url);
		request.addHeader("ZH-WF-TOKEN", MainActivity.access_key);
		request.addHeader("language", mContext.getString(R.string.w_language));
		String msg = null;
		try {
			request.setEntity(new UrlEncodedFormEntity(paras, HTTP.UTF_8));
			request.addHeader("language", "zh_CN");
			HttpResponse response = new DefaultHttpClient().execute(request);
			// 请求超时15秒
			response.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 15000);
			// 读取超时 5秒
			response.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 5000);

			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				msg = EntityUtils.toString(response.getEntity());
			}
		} catch (Exception e) {
			e.printStackTrace();

			return null;
		}
		return msg;
	}

	/** 相关工具与热点推荐内容接口，并返回数据 */
	public String getHttpRecommend(String url, Context context) {
		HttpGet httpGet = new HttpGet(url);

		httpGet.addHeader("ZH-WF-TOKEN", MainActivity.access_key);
		httpGet.addHeader("language", mContext.getString(R.string.w_language));
		String msg = null;
		try {
			HttpResponse response = new DefaultHttpClient().execute(httpGet);
			// 请求超时15秒
			response.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 15000);
			// 读取超时 5秒
			response.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 5000);
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				msg = EntityUtils.toString(response.getEntity());
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}
		return msg;

	}
}
