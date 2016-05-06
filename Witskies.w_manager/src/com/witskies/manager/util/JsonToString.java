package com.witskies.manager.util;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Handler;

import com.witskies.manager.app.WitskiesApplication;

/**
 * 把json对象转换在字符串
 * */
public class JsonToString {

	private JsonToString js;
	private ArrayList<HashMap<String, String>> list;

	public JsonToString(ArrayList<HashMap<String, String>> list) {
		this.list = list;
	}

	public JsonToString getInstantiation() {
		if (js == null) {
			js = new JsonToString(list);
		}
		return js;
	}

	/**
	 * 解析（热门推荐与相关工具）的json
	 * 
	 * @param jArray
	 *            是一个集合
	 * @param mHandler
	 *            异步
	 * @param msg
	 *            区分mHandler的消息
	 * 
	 **/
	public ArrayList<HashMap<String, String>> hotAndUtil(JSONArray jArray,
			Handler mHandler, int msg) {
		for (int i = 0; i < jArray.length(); i++) {
			try {
				HashMap<String, String> map = new HashMap<String, String>();
				JSONObject ob = jArray.getJSONObject(i);
				String name = ob.getString("name");// 标题名字
				String url = ob.getString("url");//  网络地址ַ
				map.put("name", name);
				map.put("url", url);
				list.add(map);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		if (list.size() > 0) {
			// 通知界面更新数据
			mHandler.sendEmptyMessage(msg);
		
		if (msg == 10006) {
//			WitskiesApplication.getInstantiation().setAppURLlist(list);
		}
		return list;
		}
		return null;
	}

}
