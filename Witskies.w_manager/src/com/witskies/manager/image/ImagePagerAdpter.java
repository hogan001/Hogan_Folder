package com.witskies.manager.image;

import java.util.List;

import uk.co.senab.photoview.PhotoView;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.LayoutParams;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.download.ImageDownloader.Scheme;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.witskies.w_manager.R;


public class ImagePagerAdpter extends PagerAdapter {
	public List<String> mImageList;
	private LayoutInflater mInflater;
	private DisplayImageOptions mOptions;

	public ImagePagerAdpter(Context context, List<String> image_uris) {
		this.mImageList = image_uris;
		mInflater = LayoutInflater.from(context);
		mOptions = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.pictures_no)
				.showImageForEmptyUri(R.drawable.ic_empty)
				.showImageOnFail(R.drawable.ic_empty)
				.imageScaleType(ImageScaleType.EXACTLY)
				.resetViewBeforeLoading(true).cacheOnDisk(true)
				.bitmapConfig(Bitmap.Config.RGB_565).considerExifParams(true)
				.build();
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView((View) object);
	}

	@Override
	public int getCount() {
		return mImageList.size();
	}

	@Override
	public Object instantiateItem(ViewGroup view, int position) {
		View imageLayout = mInflater.inflate(R.layout.item_image_pager, null);
		assert imageLayout != null;
		PhotoView photoView = (PhotoView) imageLayout
				.findViewById(R.id.item_image_pager_image);
		ImageLoader.getInstance().displayImage(
				Scheme.FILE.wrap(mImageList.get(position)), photoView,
				mOptions, new MySimpleImageLoadingListener());
		view.addView(imageLayout, LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		return imageLayout;

	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view.equals(object);
	}

	@Override
	public void restoreState(Parcelable state, ClassLoader loader) {
	}

	@Override
	public Parcelable saveState() {
		return null;
	}

	/**
	 * loader 加载监听回调
	 * 
	 * @author lance
	 * 
	 */
	private class MySimpleImageLoadingListener extends
			SimpleImageLoadingListener {

		@Override
		public void onLoadingStarted(String imageUri, View view) {
		}

		@Override
		public void onLoadingFailed(String imageUri, View view,
				FailReason failReason) {
			String message = null;
			switch (failReason.getType()) {
			case IO_ERROR:
				message = "Input/Output error";
				break;
			case DECODING_ERROR:
				message = "Image can't be decoded";
				break;
			case NETWORK_DENIED:
				message = "Downloads are denied";
				break;
			case OUT_OF_MEMORY:
				message = "Out Of Memory error";
				break;
			case UNKNOWN:
				message = "Unknown error";
				break;
			}
			Toast.makeText(view.getContext(), message, Toast.LENGTH_SHORT)
					.show();
		}

		@Override
		public void onLoadingComplete(String imageUri, View view,
				Bitmap loadedImage) {

		}

	}
}
