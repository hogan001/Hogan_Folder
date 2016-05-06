package com.witskies.manager.makeheadimage;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class FileImageView extends View {

	private ArrayList<Bitmap> bitmaps;

	private Paint paint;

	public FileImageView(Context context) {
		this(context, null, 0);
	}

	public FileImageView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public FileImageView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		paint = new Paint();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		try {
			if (bitmaps != null && bitmaps.size() != 0) {

				Bitmap bitmap = MakeCustomImage.getInstantiation()
						.makeTouxiang(bitmaps, getMeasuredWidth());

				canvas.drawBitmap(bitmap, 0, 0, paint);
			}
		} catch (Exception e) {
			
			bitmaps =null;

		}
	}

	public void setImageBitmaps(ArrayList<Bitmap> bitmaps) {

		this.bitmaps = bitmaps;
		invalidate();

	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);
		int width;
		int height;
		if (widthMode == MeasureSpec.EXACTLY) {
			width = widthSize;
		} else {
			width = 60;
		}
		if (heightMode == MeasureSpec.EXACTLY) {
			height = heightSize;
		} else {
			height = 60;
		}
		setMeasuredDimension(width, height);
	}

	@Override
	protected void onFinishInflate() {
		// TODO Auto-generated method stub
		super.onFinishInflate();
		if (bitmaps != null && bitmaps.size() != 0) {
			for (Bitmap bt : bitmaps) {
				if (!bt.isRecycled()) {
					bt.recycle();
					System.gc();
					bt = null;
				}
			}
		}
	}
}
