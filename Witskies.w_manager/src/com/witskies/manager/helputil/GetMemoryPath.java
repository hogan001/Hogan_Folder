package com.witskies.manager.helputil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.app.Activity;
import android.os.storage.StorageManager;

/**
 * @作者 ch
 * @描述 获取手机存储路径
 * @时间 2015年4月29日 下午2:15:15
 */

public class GetMemoryPath {
	private Activity mActivity;
	private StorageManager mStorageManager;
	private Method mMethodGetPaths;

	public GetMemoryPath(Activity activity) {
		mActivity = activity;
		if (mActivity != null) {
			mStorageManager = (StorageManager) mActivity
					.getSystemService(Activity.STORAGE_SERVICE);
			try {
				mMethodGetPaths = mStorageManager.getClass().getMethod(
						"getVolumePaths");
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			}
		}
	}

	public String[] getVolumePaths() {
		String[] paths = null;
		try {
			paths = (String[]) mMethodGetPaths.invoke(mStorageManager);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return paths;
	}
}
