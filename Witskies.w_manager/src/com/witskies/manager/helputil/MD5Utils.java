package com.witskies.manager.helputil;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;

/**
 * MD5验证
 * 
 * @author Clance
 * 
 */
public class MD5Utils {
	/**
	 * 比较与服务器上的MD5值是否一致
	 * 
	 * @param FilePath
	 * @return
	 */
	public static String GetMd5(String FilePath) {
		char hexdigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd',
				'e', 'f' };
		FileInputStream fis = null;
		String md5;
		char str[] = new char[16 * 2];
		int k = 0;
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			fis = new FileInputStream(FilePath);
			byte[] buffer = new byte[2048];
			int length = -1;
			while ((length = fis.read(buffer)) != -1) {
				md.update(buffer, 0, length);
			}
			byte[] b = md.digest();

			for (int i = 0; i < 16; i++) {
				byte byte0 = b[i];
				str[k++] = hexdigits[byte0 >>> 4 & 0xf];
				str[k++] = hexdigits[byte0 & 0xf];
			}
			fis.close();
			md5 = new String(str);
			return md5;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	/**
	 * * 检验文件的MD5值
	 * 
	 * @param fileName
	 *            目标文件的完整名称
	 * 
	 * @param md5
	 *            基准MD5值
	 * 
	 * @return 检验结果
	 */
	public static boolean checkFileMD5(String filePath, String md5) {
		return GetMd5(filePath).equalsIgnoreCase(md5);

	}
}
