//package com.witskies.manager.util;
//
//import java.lang.ref.WeakReference;
//
//import android.content.Context;
//import android.graphics.Bitmap;
//import android.media.ThumbnailUtils;
//import android.os.AsyncTask;
//import android.provider.MediaStore;
//import android.util.Log;
//import android.widget.ImageView;
//
//import com.witskies.manager.app.WitskiesApplication;
//import com.witskies.manager.bean.AdapterItemBean;
//
///**
// * @作者 ch
// * @描述 异步加载视频缩略图
// * @时间 2015年5月6日 上午10:48:31
// */
//public class BitMapAsyncTask extends AsyncTask<String, Void, Bitmap> {
//	private WeakReference<ImageView> imageViewReference;
//	private String data = "";
//	private AdapterItemBean itemBean;
//
//	public BitMapAsyncTask(ImageView imageView, Context mContext, AdapterItemBean itemBean) {
//		imageViewReference = new WeakReference<ImageView>(imageView);
//		this.itemBean = itemBean;
//	}
//
//	@Override
//	protected Bitmap doInBackground(String... params) {
//		// TODO Auto-generated method stub
//		data = params[0];
//		System.out.println("data:" + data);
//
//		/**
//		 * 获取视频的缩略图 先通过ThumbnailUtils来创建一个视频的缩略图，然后再利用ThumbnailUtils来生成指定大小的缩略图。
//		 * 如果想要的缩略图的宽和高都小于MICRO_KIND，则类型要使用MICRO_KIND作为kind的值，这样会节省内存。
//		 * 
//		 * @param videoPath
//		 *            视频的路径
//		 * @param width
//		 *            指定输出视频缩略图的宽度
//		 * @param height
//		 *            指定输出视频缩略图的高度度
//		 * @param kind
//		 *            参照MediaStore.Images.Thumbnails类中的常量MINI_KIND和MICRO_KIND。
//		 *            其中，MINI_KIND: 512 x 384，MICRO_KIND: 96 x 96
//		 * @return 指定大小的视频缩略图
//		 */
//		Bitmap bmp = ThumbnailUtils.createVideoThumbnail(data, MediaStore.Images.Thumbnails.MINI_KIND);
//		bmp = ThumbnailUtils.extractThumbnail(bmp, 100, 100, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
//
//		WitskiesApplication.thumbnailImagesSize++;
//
//		Log.e("缩略图数量增加", WitskiesApplication.thumbnailImagesSize + " ");
//
//		if (bmp != null) {
//			String bmpPath = BitmapSaveFile.getInstantiation().saveMyBitmap(bmp, itemBean.getName());
//			if (bmpPath != null) {
//				WitskiesApplication.thumbnailImagesList.add(bmpPath);
//				Log.e("加进去的", bmpPath);
//			}
//		}
//		
//		return bmp;
//
//	}
//
//	@Override
//	protected void onPostExecute(Bitmap bitmap) {
//		ImageView imageView = null;
//		if (imageViewReference != null && bitmap != null) {
//			imageView = imageViewReference.get();
//			if (imageView != null) {
//				imageView.setImageBitmap(bitmap);
//			}
//		}
//	}
//}
