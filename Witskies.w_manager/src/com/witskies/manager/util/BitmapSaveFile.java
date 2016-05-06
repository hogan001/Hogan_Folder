package com.witskies.manager.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

/**
 * @作者 ch
 * @描述 把得到的视频的缩略图，以jpg保存到sd卡上
 * @时间 2015年5月7日 下午3:15:23
 */

public class BitmapSaveFile {
	public static BitmapSaveFile bitmapSaveFile;


	public static BitmapSaveFile getInstantiation(
			) {
		if (bitmapSaveFile == null) {
			bitmapSaveFile = new BitmapSaveFile();
		}
		return bitmapSaveFile;
	}

	public String saveMyBitmap(Bitmap mBitmap, String bitName) {
		String savePath=null;
		if(Environment.getExternalStorageState().equals(
				 Environment.MEDIA_MOUNTED)){
			savePath = Environment.getExternalStorageDirectory().getPath()
					+ "/Hogan_Shipin_thumbnailImages/";
		}
		

		String saveFileName = savePath + bitName + ".png";
		File file = new File(savePath);
		if (!file.exists()) {
			file.mkdirs();
		}

		File imageFile = null;

		try {
			imageFile = new File(saveFileName);
		} catch (Exception e) {
			return null;
		}
		FileOutputStream fOut = null;
		try {
			fOut = new FileOutputStream(imageFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
		try {
			fOut.flush();

		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		try {
			fOut.close();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return imageFile.getAbsolutePath();

	}

}
