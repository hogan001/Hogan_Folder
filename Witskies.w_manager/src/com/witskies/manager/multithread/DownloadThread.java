package com.witskies.manager.multithread;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Context;
import android.util.Log;

public class DownloadThread extends Thread {
	private String urlStr; // 下载地址
	private int startPosition; // 开始下载位置
	private int downloadLength = 0; // 已下载字节数
	private int endPosition; // 结束位置
	private File saveFile; // 文件存储位置
	private int threadId; // 线程ID
	private FileDownloader fileDownloader; // 文件任务类
	private Boolean isFinished = false; // 文件快是否下载完毕
	private DatabaseUtil databaseUtil;
	private Boolean isPause = false; // 默认为运行（没有暂停)
	private Context context;

	public DownloadThread(String urlStr) {
		this.urlStr = urlStr;
	}

	public DownloadThread(Context context, FileDownloader fileDownloader, String urlStr, File file,
			int block, int threadId) {
		this.context = context;
		this.fileDownloader = fileDownloader; //
		this.urlStr = urlStr;
		this.saveFile = file;
		this.threadId = threadId;
		this.startPosition = threadId * block; // 计算起始下载位置
		this.endPosition = (threadId + 1) * block - 1; // 计算下载结束位置
	}

	public void run() {
		try {
			RandomAccessFile accessFile = new RandomAccessFile(saveFile, "rwd");
			databaseUtil = new DatabaseUtil(context); // context设置需思考
			ItemRecord record = databaseUtil.query(urlStr, threadId); // 查询是否有记录存在
			if (record != null) {
				downloadLength = record.getDownloadLength(); // 读取已下载字节数
				fileDownloader.append(downloadLength); // 更新总下载字节数
				print("存在记录" + Integer.toString(downloadLength));
			} else {
				synchronized (DatabaseUtil.lock) {
					databaseUtil.insert(urlStr, threadId, downloadLength); // 插入未完成线程任务
					print("不存在记录");
				}
			}
			accessFile.seek(startPosition + downloadLength); // 设置写入起始位置
			if ((endPosition + 1) == (startPosition + downloadLength)
					|| endPosition == (startPosition + downloadLength)) {
				print(Integer.toString(endPosition) + "endPosition");
				print(Integer.toString(startPosition) + "startPosition");
				print(Integer.toString(downloadLength) + "downloadLength");
				isFinished = true; // 更新下载标志
				print(" 曾成功下载");
			} else {
				URL url = new URL(urlStr);
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setConnectTimeout(3000); // 设置超时
				conn.setRequestMethod("GET");
				int tmpStartPosition = startPosition + downloadLength;
				print("上次下载的位置=" + tmpStartPosition);
				conn.setRequestProperty("Range", "bytes=" + tmpStartPosition + "-" + endPosition);

				if (conn.getResponseCode() == 206) {
					InputStream inStream = conn.getInputStream();
					byte[] buffer = new byte[1024];
					int len = 0;
					while (!isPause && (len = inStream.read(buffer)) != -1) {
						accessFile.write(buffer, 0, len); // 写入文件
						fileDownloader.append(len); // 动态更新下载数、
						downloadLength += len; // 已下载数更新
						// 写入数据库
						synchronized (DatabaseUtil.lock) {
							databaseUtil.update(urlStr, threadId, downloadLength);
						}
					}
					if ((endPosition + 1) == (startPosition + downloadLength)
							|| endPosition == (startPosition + downloadLength)) {
						print("endPosition=" + Integer.toString(endPosition));
						print("startPosition=" + Integer.toString(startPosition));
						print("downloadLength=" + Integer.toString(downloadLength));
						isFinished = true; // 更新下载标志
						// // 删除数据库记录
						databaseUtil.delete(urlStr, threadId);
						print(" 下载完毕");
					} else {
						print("endPosition=" + Integer.toString(endPosition));
						print("startPosition=" + Integer.toString(startPosition));
						print("downloadLength=" + Integer.toString(downloadLength));
						print(" 未下载完！");
					}
				} else {
					print(conn.getResponseMessage());
					print(Integer.toString(conn.getResponseCode()));
					print(" 没开始下载");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 返回下载结束的标志
	public Boolean isFinished() {
		return this.isFinished;
	}

	// 设置暂停
	public void setPause() {
		this.isPause = true;

	}

	public void setResume() {
		this.isPause = false;
	}

	private void print(String msg) {
		Log.d("DownloadThrea", msg);
	}
}
