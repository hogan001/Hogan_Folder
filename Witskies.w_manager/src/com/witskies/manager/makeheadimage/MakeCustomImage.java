package com.witskies.manager.makeheadimage;

import java.util.ArrayList;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;

/**
 * 制做个性图片
 **/
public class MakeCustomImage {

	private static MakeCustomImage makeCustomImage;

	public static MakeCustomImage getInstantiation() {
		if (makeCustomImage == null) {
			makeCustomImage = new MakeCustomImage();
		}

		return makeCustomImage;
	}

	/**
	 * 创建单个图片
	 **/
	private Bitmap createSingleImage(Bitmap source) {
		final Paint paint = new Paint();
		paint.setAntiAlias(true);
		// 控制图片大小
		int width = 13 * source.getWidth() / 10;

		Bitmap target = Bitmap.createBitmap(width, width, Config.ARGB_8888);

		Canvas canvas = new Canvas(target);

		canvas.drawRect(0, 0, 7 * width / 10, 7 * width / 10, paint);

		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

		canvas.drawBitmap(source, 0, 0, paint);

		return target;
	}

	/**
	 * 创建我们需要的头像
	 * */
	public Bitmap makeTouxiang(ArrayList<Bitmap> bitmaps, int width) {
		final Paint paint = new Paint();
		paint.setAntiAlias(true);
		Bitmap target = Bitmap.createBitmap(width, width, Config.ARGB_8888);
		Canvas canvas = new Canvas(target);

		ArrayList<Bitmap> circleBitmaps = getSingleBitmaps(bitmaps);
		if (circleBitmaps != null && circleBitmaps.size() > 0) {

			for (int index = 0; index < circleBitmaps.size(); index++) {
				float ratio = getRatio(circleBitmaps, width, index);

				Bitmap bitmap = circleBitmaps.get(index);
				// MATRIX
				Matrix matrix = new Matrix();
				matrix.postScale(ratio, ratio);
				Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0,
						bitmap.getWidth(), bitmap.getHeight(), matrix, true);

				float[] zuobiao = getPictureXY(circleBitmaps.size(), index,
						width);
				canvas.drawBitmap(newBitmap, zuobiao[0], zuobiao[1], paint);
			}
			return target;
		} else {
			return null;
		}
	}

	/**
	 * 得到图片XY座标
	 **/
	private float[] getPictureXY(int size, int index, int width) {

		switch (size) {
		case 1:
			return getOnePictureXY(index, width);
		case 2:
			return getTwoPictureXY(index, width);
		case 3:
			return getThreePictureXY(index, width);
		case 4:
			return getFourPictureXY(index, width);
		}
		return null;
	}

	/**
	 * 四张图片XY座标
	 **/
	private float[] getFourPictureXY(int index, float width) {
		float[] zuobiao = { 0.0f, 0.0f };

		switch (index) {
		case 0:

			zuobiao[0] = (float) (6 * width / 50.);
			zuobiao[1] = (float) (6 * width / 50.);
		//	Log.e("第一张图XY坐标", zuobiao[0] + ">>>" + zuobiao[1] + "");

			break;
		case 1:
			zuobiao[0] = (float) (26 * width / 50.);
			zuobiao[1] = (float) (6 * width / 50.);
		//	Log.e("第二张图XY坐标", zuobiao[0] + ">>>" + zuobiao[1] + "");
			break;
		case 2:
			zuobiao[0] = (float) (6 * width / 50.);
			zuobiao[1] = (float) (26 * width / 50.);
		//	Log.e("第三张图XY坐标", zuobiao[0] + ">>>" + zuobiao[1] + "");
			break;
		case 3:
			zuobiao[0] = (float) (26 * width / 50.);
			zuobiao[1] = (float) (26 * width / 50.);
		//	Log.e("第四张图XY坐标", zuobiao[0] + ">>>" + zuobiao[1] + "");
			break;
		}
		return zuobiao;
	}

	/**
	 * 三张图片XY座标
	 **/
	private float[] getThreePictureXY(int index, int width) {
		float[] zuobiao = { 0.0f, 0.0f };

		switch (index) {
		case 0:
			zuobiao[0] = (float) (6 * width / 50.);
			zuobiao[1] = (float) (6 * width / 50.);
			break;
		case 1:
			zuobiao[0] = (float) (26 * width / 50.);
			zuobiao[1] = (float) (6 * width / 50.);
			break;
		case 2:
			zuobiao[0] = (float) (6 * width / 50.);
			zuobiao[1] = (float) (26 * width / 50.);
			break;
		}
		return zuobiao;
	}

	/**
	 * 二张图片XY座标
	 **/
	private float[] getTwoPictureXY(int index, int width) {
		float[] zuobiao = { 0.0f, 0.0f };

		switch (index) {
		case 0:
			zuobiao[0] = (float) (6 * width / 50.);
			zuobiao[1] = (float) (6 * width / 50.);
			break;
		case 1:
			zuobiao[0] = (float) (26 * width / 50.);
			zuobiao[1] = (float) (6 * width / 50.);
			break;
		}
		return zuobiao;
	}

	/**
	 * 一张图片XY座标
	 **/
	private float[] getOnePictureXY(int index, int width) {
		float[] zuobiao = { 0.0f, 0.0f };
		zuobiao[0] = (float) (6 * width / 50.);
		zuobiao[1] = (float) (6 * width / 50.);
		return zuobiao;
	}

	/**
	 * 单个图片合在一起的一个List
	 **/
	private ArrayList<Bitmap> getSingleBitmaps(ArrayList<Bitmap> bitmaps) {
		ArrayList<Bitmap> circleBitmaps = new ArrayList<Bitmap>();
		for (Bitmap bitmap : bitmaps) {
			circleBitmaps.add(createSingleImage(bitmap));
		}
		return circleBitmaps;
	}

	/**
	 * 得到缩放倍数
	 * */
	private float getRatio(ArrayList<Bitmap> bitmaps, int width, int index) {
		if (bitmaps != null && bitmaps.size() > 0) {

			return (float) ((width / 2.) / bitmaps.get(index).getWidth());
		} else {
			return 0;
		}
	}
}
