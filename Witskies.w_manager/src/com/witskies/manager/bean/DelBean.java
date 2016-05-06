package com.witskies.manager.bean;

import java.io.File;
/**
 * @作者 ch
 * @描述  用于删除辅助的一个类
 * @时间 2015年5月26日 上午10:45:08
 */
public class DelBean {
	private File curPath;
	private int index;
	private int position;

	public File getCurPath() {
		return curPath;
	}

	public void setCurPath(File curPath) {
		this.curPath = curPath;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

}
