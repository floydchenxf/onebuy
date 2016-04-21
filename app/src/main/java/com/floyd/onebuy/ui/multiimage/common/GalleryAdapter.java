package com.floyd.onebuy.ui.multiimage.common;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.floyd.onebuy.ui.R;
import com.floyd.onebuy.utils.NotificationUtils;

import java.util.ArrayList;
import java.util.List;

public class GalleryAdapter extends BaseAdapter implements OnClickListener {

	private LayoutInflater infalter;

	private LruCache<String, Bitmap> mImageCache;
	private List<ImageItem> mImageItemList;
	private int mMaxCount; // 最大可选择数目
	private int mItemWidth;
	private OnCheckChangedListener mOnCheckChangedListener;
	private List<String> mSelectedList;
	private String mMaxToast;
	private Context mContext;

	public GalleryAdapter(Context c, List<ImageItem> customGalleryList) {
		infalter = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mImageItemList = customGalleryList;
		mImageCache=PhotoChooseHelper.getHelper().getImageCache();
		mSelectedList = PhotoChooseHelper.getHelper().getSelectedList();
		DisplayMetrics dm = new DisplayMetrics();
		dm = c.getResources().getDisplayMetrics();
		int screenWidth = dm.widthPixels; // 屏幕宽（像素，如：480px）
		mItemWidth = screenWidth / 3;
		this.mContext=c;
	}

	public void updateDataAndNotify(List<ImageItem> data) {
		mImageItemList = data;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return mImageItemList.size();
	}

	@Override
	public ImageItem getItem(int position) {
		return mImageItemList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}


	public void selectAll(boolean selection) {
		for (int i = 0; i < mImageItemList.size(); i++) {
			mImageItemList.get(i).setSelected(selection);
		}
		notifyDataSetChanged();
	}

	public boolean isAllSelected() {
		boolean isAllSelected = true;

		for (int i = 0; i < mImageItemList.size(); i++) {
			if (!mImageItemList.get(i).isSelected()) {
				isAllSelected = false;
				break;
			}
		}

		return isAllSelected;
	}

	public boolean isAnySelected() {
		boolean isAnySelected = false;

		for (int i = 0; i < mImageItemList.size(); i++) {
			if (mImageItemList.get(i).isSelected()) {
				isAnySelected = true;
				break;
			}
		}

		return isAnySelected;
	}

	public ArrayList<ImageItem> getSelected() {
		ArrayList<ImageItem> dataT = new ArrayList<ImageItem>();

		for (int i = 0; i < mImageItemList.size(); i++) {
			if (mImageItemList.get(i).isSelected()) {
				dataT.add(mImageItemList.get(i));
			}
		}

		return dataT;
	}

	public void changeSelection(View v, int position) {
		ImageItem imageItem = mImageItemList.get(position);
		if (mSelectedList.contains(imageItem.getImagePath())) {
			mSelectedList.remove(imageItem.getImagePath());
		} else {
			if (mMaxCount > 0 && mSelectedList.size() >= mMaxCount) {
				if (!TextUtils.isEmpty(mMaxToast)) {
					String msg = String.format(mMaxToast, String.valueOf(mMaxCount));
					NotificationUtils.showToast(msg, this.mContext);
				}
				return;
			}
			mSelectedList.add(imageItem.getImagePath());
		}
		notifyDataSetChanged();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if (convertView == null) {
			convertView = infalter.inflate(R.layout.aliwx_multi_pick_gallery_item, null);
			holder = new ViewHolder();
			holder.imageItem = (ImageView) convertView.findViewById(R.id.image_item);
			LayoutParams lp = holder.imageItem.getLayoutParams();
			lp.height = mItemWidth;
			lp.width = mItemWidth;
			holder.imageCheck = (ImageView) convertView.findViewById(R.id.image_check);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		ImageItem imageItem = mImageItemList.get(position);
		String path = imageItem.getImagePath();
		String oriPath=path;
		if (!TextUtils.isEmpty(imageItem.getThumbnailPath())) {
			path = imageItem.getThumbnailPath();
		}

		if(!TextUtils.isEmpty(path)) {
			Bitmap cacheBitmap = mImageCache.get(path);
			if (cacheBitmap != null) {
				holder.imageItem.setImageBitmap(cacheBitmap);
			} else {
//			new MultiPickImageGetTask(path, holder.imageItem, mImageCache,oriPath,imageItem.getOrientation()).executeOnThreadPool();
				ImageLoaderHelper.getHelper(mContext).loadBitmap(path, holder.imageItem, mImageCache, imageItem.getOrientation());
			}
		}
		holder.imageCheck.setTag(position);
		if (mSelectedList.contains(imageItem.getImagePath())) {
			holder.imageCheck.setImageResource(R.drawable.aliwx_picture_select);
		} else {
			holder.imageCheck.setImageResource(R.drawable.aliwx_picture_unselect);
		}
		holder.imageCheck.setOnClickListener(this);

		return convertView;
	}

	public class ViewHolder {
		ImageView imageItem;
		ImageView imageCheck;
	}

	public void clearCache() {
		// imageLoader.clearDiscCache();
		// imageLoader.clearMemoryCache();
	}

	public void clear() {
		mImageItemList.clear();
		notifyDataSetChanged();
	}

	public void recycle() {
//		mImageCache.evictAll();
	}

	public void setMaxCount(int mMaxCount) {
		this.mMaxCount = mMaxCount;
	}

	public List<String> getSelectedSet() {
		return mSelectedList;
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.image_check && v instanceof ImageView) {
			Object tagObject = v.getTag();
			if (tagObject != null && tagObject instanceof Integer) {
				Integer valueInteger = (Integer) tagObject;
				changeSelection(v, valueInteger);
				if (mOnCheckChangedListener != null) {
					mOnCheckChangedListener.OnCheckChanged();
				}
			}
		}
	}
	
	
	public void setOnCheckChangedListener(OnCheckChangedListener listener){
		this.mOnCheckChangedListener=listener;
	}


	public void setMaxToast(String mMaxToast) {
		this.mMaxToast = mMaxToast;
	}

}
