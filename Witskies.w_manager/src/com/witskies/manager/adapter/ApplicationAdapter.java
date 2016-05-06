package com.witskies.manager.adapter;

import java.util.ArrayList;
import java.util.Locale;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.witskies.manager.bean.AppItemInfo;
import com.witskies.manager.fragment.ApplicationFragment;
import com.witskies.manager.helputil.FindAppUtil;
import com.witskies.manager.helputil.LogUtils;
import com.witskies.manager.makeheadimage.FileImageView;
import com.witskies.w_manager.R;

/**
 * @作者 ch
 * @描述 应用分类的adapter
 * @时间 2015年5月11日 下午2:57:29
 */
public class ApplicationAdapter extends BaseAdapter {
	private Context mContext;
	private ArrayList<String> categoryNames;
	private ArrayList<AppItemInfo> AppItemInfos;

	public ApplicationAdapter(Context context, ArrayList<String> categoryNames) {
		this.mContext = context;
		this.categoryNames = categoryNames;
	}

	@Override
	public int getCount() {
		return categoryNames.size();
	}

	@Override
	public Object getItem(int position) {
		return categoryNames.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;

		getTargetList(position);

		if (convertView == null) {
			holder = new ViewHolder();
			convertView = View.inflate(mContext, R.layout.app_fragment_item, null);
			holder.image = (FileImageView) convertView.findViewById(R.id.apps_categoryimage);
			holder.fileName = (TextView) convertView.findViewById(R.id.app_CategoryName);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		String filename = categoryNames.get(position);
		String language = Locale.getDefault().getLanguage();

		LogUtils.e(language);

		if (language.equals("zh")) {
			filename = changeLanguage(filename);
		}

		holder.fileName.setText(filename);
		holder.image.setImageBitmaps(getBitmap(AppItemInfos));

		return convertView;
	}

	private final class ViewHolder {
		public FileImageView image;
		public TextView fileName;
	}

	/**
	 * @作者 ch
	 * @描述 根据传来的ArrayList<AppItemInfo> AppItemInfos的长度， 返一个对应的ArrayList<Bitmap>
	 * @时间 2015年5月11日 下午2:58:33
	 */
	private ArrayList<Bitmap> getBitmap(ArrayList<AppItemInfo> AppItemInfos) {
		ArrayList<Bitmap> bitmapsList = new ArrayList<Bitmap>();

		switch (AppItemInfos.size()) {
		case 0:

			break;

		case 1:
			bitmapsList.add(drawableToBitamp(AppItemInfos.get(0).getIcon()));
			break;
		case 2:
			bitmapsList.add(drawableToBitamp(AppItemInfos.get(0).getIcon()));
			bitmapsList.add(drawableToBitamp(AppItemInfos.get(1).getIcon()));
			break;
		case 3:
			bitmapsList.add(drawableToBitamp(AppItemInfos.get(0).getIcon()));
			bitmapsList.add(drawableToBitamp(AppItemInfos.get(1).getIcon()));
			bitmapsList.add(drawableToBitamp(AppItemInfos.get(2).getIcon()));
			break;
		default:
			bitmapsList.add(drawableToBitamp(AppItemInfos.get(0).getIcon()));
			bitmapsList.add(drawableToBitamp(AppItemInfos.get(1).getIcon()));
			bitmapsList.add(drawableToBitamp(AppItemInfos.get(2).getIcon()));
			bitmapsList.add(drawableToBitamp(AppItemInfos.get(3).getIcon()));
			break;
		}

		return bitmapsList;

	}

	/**
	 * @作者 ch
	 * @描述 把Drawable转成bitmap
	 * @时间 2015年5月11日 下午2:59:29
	 */
	private Bitmap drawableToBitamp(Drawable drawable) {
		BitmapDrawable bd = (BitmapDrawable) drawable;
		Bitmap bitmap = bd.getBitmap();
		return bitmap;

	}

	/**
	 * @作者 ch
	 * @描述 初始化需要的<AppItemInfo> AppItemInfos
	 * @时间 2015年5月11日 下午3:00:02
	 */
	private void getTargetList(int index) {
		ArrayList<String> keys = ApplicationFragment.keys;
		ArrayList<String> needsLists = ApplicationFragment.cateMap.get(keys.get(index));
		// 获取所有包名对应的应用信息穿给适配器
		AppItemInfos = new ArrayList<AppItemInfo>();

		if (keys.size() > 0 && needsLists.size() > 0) {
			for (int i = 0; i < needsLists.size(); i++) {
				AppItemInfos.add(FindAppUtil.pcknameAndInfos.get(needsLists.get(i)));

			}
		}
	}

	/**
	 * @描述 把本地的英文改成中文
	 * @时间 2015年6月1日 上午11:22:49
	 */
	public static String changeLanguage(String str) {
		if (str.equals("Personalization")) {
			str = "个性化";
		} else if (str.equals("Game")) {
			str = "游戏";

		} else if (str.equals("Transportation")) {
			str = "交通运输";
		}

		else if (str.equals("Sports")) {
			str = "体育";
		}

		else if (str.equals("Health&Fitness")) {
			str = "健康与健身";

		} else if (str.equals("App&Wallpaper")) {
			str = "动态壁纸";

		} else if (str.equals("Comics")) {
			str = "动漫";
		} else if (str.equals("Medical")) {
			str = "医药";
		}

		else if (str.equals("Business")) {
			str = "商务";

		}

		else if (str.equals("Books&Reference")) {
			str = "图书与工具书";

		}

		else if (str.equals("Weather")) {
			str = "天气";

		}

		else if (str.equals("Entertainment")) {
			str = "娱乐";

		} else if (str.equals("Media&Video")) {
			str = "媒体与视频";

		} else if (str.equals("App&Widgets")) {
			str = "小部件";
		}

		else if (str.equals("Tools")) {
			str = "工具";

		} else if (str.equals("Photography")) {
			str = "摄影";

		} else if (str.equals("Productivity")) {
			str = "效率";

		} else if (str.equals("Education")) {
			str = "教育";

		} else if (str.equals("News&Magazines")) {
			str = "新闻杂志";
		}

		else if (str.equals("Travel&Local")) {
			str = "旅游与本地出行";

		} else if (str.equals("Lifestyle")) {
			str = "生活时尚";
		}

		else if (str.equals("Social")) {
			str = "社交";

		} else if (str.equals("Finance")) {
			str = "财务";

		} else if (str.equals("Shopping")) {
			str = "购物";

		} else if (str.equals("Libraries&Demo")) {
			str = "软件与演示";
		} else if (str.equals("Communication")) {
			str = "通讯";

		} else if (str.equals("Music&Audio")) {
			str = "音乐与音频";
		}

		return str;
	}
}
