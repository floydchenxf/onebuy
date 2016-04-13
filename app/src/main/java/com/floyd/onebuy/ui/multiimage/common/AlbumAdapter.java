package com.floyd.onebuy.ui.multiimage.common;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.support.v4.util.LruCache;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.floyd.onebuy.R;

import java.util.List;


public class AlbumAdapter extends BaseAdapter {

	private List<ImageBucket> mImageBucketList;
	private LayoutInflater infalter;
	private Handler mHandler = new Handler();
	private LruCache<String, Bitmap> mImageCache;
	private Context mContext;

	//解决红米手机无法调用onItemClickListener问题.原因没找到
	private View.OnClickListener itemClickListener;

	private int currentIndex = 0;

	
	
	public AlbumAdapter(Context context, List<ImageBucket> imageBucketList, View.OnClickListener itemClickListener) {
		this.mImageBucketList = imageBucketList;
		infalter = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mImageCache=PhotoChooseHelper.getHelper().getImageCache();
		this.mContext = context;
		this.itemClickListener = itemClickListener;
	}

	public void updateDataAndNotify(List<ImageBucket> data) {
		this.mImageBucketList = data;
		this.notifyDataSetChanged();
	}

	public void setIndex(int currentIndex) {
		this.currentIndex = currentIndex;
	}

	@Override
	public int getCount() {
		return mImageBucketList.size();
	}

	@Override
	public ImageBucket getItem(int position) {
		return mImageBucketList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if (convertView == null) {
			int layoutId = R.layout.aliwx_multi_pick_album_item;
			convertView = infalter.inflate(layoutId,
					parent, false);
			holder = new ViewHolder();
			int albumPicId = R.id.album_pic;
			holder.albumPic = (ImageView) convertView
					.findViewById(albumPicId);

			int alibumNumId = R.id.album_num;
			holder.albumNum = (TextView) convertView
					.findViewById(alibumNumId);

			int albumNameId = R.id.album_name;
			holder.albumName = (TextView) convertView
					.findViewById(albumNameId);

			int albumChecked = R.id.album_iv;
			holder.albumChecked = (ImageView) convertView.findViewById(albumChecked);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		if (itemClickListener != null) {
			int albumPicId = R.id.album_pic;
			convertView.setTag(albumPicId, position);
			convertView.setOnClickListener(itemClickListener);
		}

		ImageBucket imageBucket = mImageBucketList.get(position);
		if(imageBucket!=null){
			String nameString = imageBucket.getBucketName();
			holder.albumName.setText(nameString);
			int count = imageBucket.getCount();
			holder.albumNum.setText(count + "张");

			if (currentIndex == position) {
				holder.albumChecked.setVisibility(View.VISIBLE);
			} else {
				holder.albumChecked.setVisibility(View.GONE);
			}
			
			List<ImageItem> imageItemsList = imageBucket.getImageList();
			if (imageItemsList != null && imageItemsList.size() > 0) {
				ImageItem imageItem = imageItemsList.get(0);
				String path = imageItem.getImagePath();
				String oriPath=path;
				if (!TextUtils.isEmpty(imageItem.getThumbnailPath())) {
					path = imageItem.getThumbnailPath();
				}
                if(!TextUtils.isEmpty(path)){
                    Bitmap cacheBitmap = mImageCache.get(path);
                    if (cacheBitmap != null) {
                        holder.albumPic.setImageBitmap(cacheBitmap);
					} else {
						holder.albumPic.setTag(path);
                        ImageLoaderHelper.getHelper(mContext).loadBitmap(path, holder.albumPic, mImageCache,imageItem.getOrientation());
					}
				}

			}
		}
		return convertView;
	}

	public class ViewHolder {
		ImageView albumPic;
		TextView albumNum;
		TextView albumName;
		ImageView albumChecked;

	}
}
