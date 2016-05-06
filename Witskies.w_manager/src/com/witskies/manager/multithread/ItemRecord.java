package com.witskies.manager.multithread;

public class ItemRecord {

	private String path;
	private int threadId;
	private int downloadLength;
	
	public ItemRecord(String path,int threadId,int downloadLength){
		this.path = path;
		this.threadId = threadId;
		this.downloadLength = downloadLength;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public int getThreadId() {
		return threadId;
	}

	public void setThreadId(int threadId) {
		this.threadId = threadId;
	}

	public int getDownloadLength() {
		return downloadLength;
	}

	public void setDownloadLength(int downloadLength) {
		this.downloadLength = downloadLength;
	}
	

}
