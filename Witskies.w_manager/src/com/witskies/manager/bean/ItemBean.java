package com.witskies.manager.bean;

import android.graphics.drawable.Drawable;

public class ItemBean {
	private String name;
	private Drawable drawable;
	private long size;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Drawable getDrawable() {
		return drawable;
	}
	public void setDrawable(Drawable drawable) {
		this.drawable = drawable;
	}
	public long getSize() {
		return size;
	}
	public void setSize(long size) {
		this.size = size;
	}
	

}
