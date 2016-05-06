package com.witskies.manager.multithread;

import java.io.File;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.witskies.manager.helputil.Const;
import com.witskies.manager.helputil.FileUtil;

public class FileDownloader {
	private static final String TAG = "FileDownloader"; // 方便调试语句的编写
	private String urlStr; // 文件下载位置
	private int fileLength; // 下载文件长度
	private int downloadLength; // 文件已下载长度
	private File saveFile; // SD卡上的存储文件
	private int block; // 每个线程下载的大小
	private int threadCount = 1; // 该任务的线程数目
	private DownloadThread[] loadThreads;
	// private Handler handler;
	private Boolean isFinished = false; // 该任务是否完毕
	private Boolean isPause = false;
	private Context mContext;
	RandomAccessFile accessFile;

	private int id;

	protected synchronized void append(int size) { // 多线程访问需加锁
		this.downloadLength += size;
	}

	/*
	 * protected synchronized void update(int threadId , int
	 * thisThreadDownloadlength){ //写入数据库
	 * 
	 * }
	 */

	public FileDownloader(String urlStr, Context context, int id, String fileName, File file) {
		this.urlStr = urlStr;
		this.loadThreads = new DownloadThread[threadCount];
		this.mContext = context;
		this.id = id;
		try {
			URL url = new URL(urlStr);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(3000); // 设置超时

			if (conn.getResponseCode() == 200) {
				fileLength = conn.getContentLength(); // 获取下载文件长度
				saveFile = new File(file, fileName);
				accessFile = new RandomAccessFile(saveFile, "rws");
				accessFile.setLength(fileLength); // 设置本地文件和下载文件长度相同
				accessFile.close();
				// 开始下载
				Intent startIntent = new Intent(DownLoaderService.START_DOWNLOAD);
				startIntent.putExtra("fileLength", fileLength);
				startIntent.putExtra("id", id);
				mContext.sendBroadcast(startIntent);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void Download(CompleteListener listener) throws Exception {
		URL url = new URL(urlStr);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setConnectTimeout(10000); // 设置超时
		Intent loadingIntent = new Intent(DownLoaderService.LOADING_DOWNLOAD);
		if (conn.getResponseCode() == 200) {
			block = fileLength % threadCount == 0 ? fileLength / threadCount : fileLength
					/ threadCount + 1; // 计算线程下载量
			print(Integer.toString(block));
			for (int i = 0; i < threadCount; i++) {
				this.loadThreads[i] = new DownloadThread(mContext, this, urlStr, saveFile, block, i); // 新建线程开始下载
				this.loadThreads[i].start();
				print("线程：" + Integer.toString(i) + "开始下载");
			}

			while (!isPause && !this.isFinished) {
				this.isFinished = true;
				Thread.sleep(900);
				for (int i = 0; i < threadCount; i++) {
					if (loadThreads[i] != null && !loadThreads[i].isFinished()) {
						this.isFinished = false;
					}
				}
				loadingIntent.putExtra("currentlength", downloadLength);
				loadingIntent.putExtra("id", id);
				mContext.sendBroadcast(loadingIntent);
			}
			if (this.isFinished && listener != null) {
				// 完成
				if (saveFile.exists()) {
					saveFile.renameTo(new File(saveFile.getAbsolutePath() + ".apk"));
				}
				listener.isComplete(downloadLength);
				Intent finishedIntent = new Intent(DownLoaderService.FINISHED_DOWNLOAD);
				mContext.sendBroadcast(finishedIntent);
			}
		}
	}

	private void print(String msg) { // 打印提示消息
		Log.d(FileDownloader.TAG, msg);
	}

	public void setPause() {
		isPause = true; // 该任务暂停
		for (int i = 0; i < threadCount; i++) {
			if (loadThreads[i] != null && !loadThreads[i].isFinished()) {
				loadThreads[i].setPause(); // 设置该线程暂停
				print(Integer.toString(i) + "暂停");
			}
		}
	}

	public void setResume() {
		isPause = false; // 该任务暂停
		for (int i = 0; i < threadCount; i++) {
			if (loadThreads[i] != null && !loadThreads[i].isFinished()) {
				loadThreads[i].setResume(); // 设置该线程重启
				print(Integer.toString(i) + "恢复");
			}
		}
	}

	public void setResume(final CompleteListener listener) throws Exception {
		isPause = false;
		this.downloadLength = 0;
		this.Download(new CompleteListener() {
			public void isComplete(int size) {
				listener.isComplete(size);
				print("listener");
			}
		});
	}

	public Boolean isFinished() {
		return this.isFinished;
	}
}
