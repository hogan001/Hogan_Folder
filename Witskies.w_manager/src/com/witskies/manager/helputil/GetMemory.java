package com.witskies.manager.helputil;

import java.io.Serializable;
import java.text.DecimalFormat;

import android.os.StatFs;
/**
 * @作者 ch
 * @描述   获取手机内部存储大小与SD卡内部存储大小
 * @时间 2015年4月29日 下午2:16:47
 */
public class GetMemory implements Serializable {

	private static final long serialVersionUID = 1L;
	private String inMemory;
	public float phoneTotalSize;
	public float phoneAvailSize;


	public GetMemory(String inMemory2) {
		this.inMemory = inMemory2;
		getPhoneTotalMemory();
		getPhoneAvailMemory();

	}

	
	@SuppressWarnings("deprecation")
	public float getPhoneTotalMemory() {
		StatFs sf = new StatFs(inMemory);

		long blockSize = sf.getBlockSize();

		long blockCount = sf.getBlockCount();

		float phoneSize = (float) (blockCount * blockSize)
				/ (1024 * 1024 * 1024);
		DecimalFormat df = new DecimalFormat("0.00");
		phoneTotalSize = Float.parseFloat(df.format(phoneSize));

		return phoneTotalSize;

	}

	
	@SuppressWarnings("deprecation")
	public float getPhoneAvailMemory() {
		StatFs sf = new StatFs(inMemory);
		long blockSize = sf.getBlockSize();

		long availCount = sf.getAvailableBlocks();

		DecimalFormat df = new DecimalFormat("0.00");
		float phoneAvailSize1 = (float) (availCount * blockSize)
				/ (1024 * 1024 * 1024);

		phoneAvailSize = Float.parseFloat(df.format(phoneAvailSize1));
		return phoneAvailSize;
	}

}
