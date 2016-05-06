package com.witskies.manager.bean;

import java.io.Serializable;

public class RecommendBean implements Serializable {
	String name;
	String url;
	String icon;
	String type;

	/**
	 * 此空构造器必要,否则： com.alibaba.fastjson.JSONException: default constructor not
	 * found. class com.test.Person
	 */
	public RecommendBean() {
	}

	public RecommendBean(String name, String url, String icon, String type) {
		this.name = name;
		this.url = url;
		this.icon = icon;
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
