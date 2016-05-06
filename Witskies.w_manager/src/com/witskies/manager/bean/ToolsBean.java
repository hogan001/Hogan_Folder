package com.witskies.manager.bean;

import java.io.Serializable;

public class ToolsBean implements Serializable {
	@Override
	public String toString() {
		return "ToolsBean [name=" + name + ", url=" + url + ", icon=" + icon + ", type=" + type
				+ ", md5=" + md5 + ", path=" + path + ", id=" + id + ", finshed=" + finshed
				+ ", size=" + size + ", version=" + version + "]";
	}

	String name;
	String url;
	String icon;
	String type;
	String md5;
	String path;
	int id;
	boolean finshed;
	int size;
	String version;
	int downLoadId;
	int progress;
	int length;

	/**
	 * 此空构造器必要,否则： com.alibaba.fastjson.JSONException: default constructor not
	 * found. class com.test.Person
	 */
	public ToolsBean() {
	}

	/**
	 * 返回没有转换过后的长度
	 * 
	 * @return
	 */
	public int getLength() {
		return size;
	}

	// /**
	// * 设置长度
	// *
	// * @param length
	// */
	// public void setLength(int length) {
	// this.length = length;
	// }

	public int getDownLoadId() {
		return downLoadId;
	}

	public void setDownLoadId(int downLoadId) {
		this.downLoadId = downLoadId;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version2) {
		this.version = version2;
	}

	/**
	 * 返回转换过后的大小x.xx
	 * 
	 * @return
	 */
	public String getSize() {
		java.text.DecimalFormat df = new java.text.DecimalFormat("#.00");

		return df.format(size / 1024 / 1024);
	}

	/**
	 * 设置apk的大小
	 * 
	 * @param size
	 */
	public void setSize(int size) {
		this.size = size;
	}

	public int getProgress() {
		return progress;
	}

	/**
	 * 设置当前下载进度
	 * 
	 * @param progress
	 */
	public void setProgress(int progress) {
		this.progress = progress;
	}

	public boolean getFinshed() {
		return finshed;
	}

	/**
	 * 标志是否下载完成
	 * 
	 * @param finshed
	 */
	public void setFinshed(Boolean finshed) {
		this.finshed = finshed;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
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

	public String getMd5() {
		return md5;
	}

	public void setMd5(String md5) {
		this.md5 = md5;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
}
