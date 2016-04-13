package com.floyd.onebuy.ui.multiimage;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.floyd.onebuy.R;
import com.floyd.onebuy.channel.threadpool.WxDefaultExecutor;
import com.floyd.onebuy.ui.multiimage.common.AlbumAdapter;
import com.floyd.onebuy.ui.multiimage.common.ImageBucket;
import com.floyd.onebuy.ui.multiimage.common.PhotoChooseHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author yuanyi.rss
 * 
 */
public class MultiPickAlbumActivity extends Activity implements
		OnItemClickListener, OnClickListener {

	private static final int MULIT_PIC_CHOOSE_WITH_DATA = 10;

	private PhotoChooseHelper chooseHelper;
	private Handler mHandler = new Handler();
	private List<ImageBucket> mImageBucketList = new ArrayList<ImageBucket>();
	private AlbumAdapter mAdapter;
	private ListView mAlbumListView;
	private int mMaxCount; // 最大可选择数目
	
	private String mMaxToast;
	private int titleBackId;
	private int titleButtonId;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		int layoutId = R.layout.aliwx_multi_pick_album;
		setContentView(layoutId);
		init();
	}

	private void init() {
		chooseHelper = PhotoChooseHelper.getHelper();
		chooseHelper.init(getApplicationContext());

		int albumListId = R.id.album_list;
		mAlbumListView = (ListView) findViewById(albumListId);
		mAdapter = new AlbumAdapter(this, mImageBucketList, null);
		mAlbumListView.setAdapter(mAdapter);
		mAlbumListView.setOnItemClickListener(this);
		

		mMaxCount = getIntent().getIntExtra(MultiPickGalleryActivity.MAX_COUNT, -1);
		mMaxToast = getIntent().getStringExtra(MultiPickGalleryActivity.MAX_TOAST);
//
//		int titleDescId = R.id.title_text;
//		TextView titleDescView = (TextView)findViewById(titleDescId);
//		titleDescView.setText("图片专辑");

		titleBackId = R.id.title_back;
		TextView titleBack = (TextView) findViewById(titleBackId);
		titleBack.setOnClickListener(this);

//		titleButtonId = R.id.title_button;
//		TextView titleButtonView = (TextView)findViewById(titleButtonId);
//		titleButtonView.setOnClickListener(this);
//		titleButtonView.setVisibility(View.VISIBLE);
//		titleButtonView.setText("取消");


		WxDefaultExecutor.getInstance().executeLocal(new Runnable() {
			@Override
			public void run() {
				final List<ImageBucket> localImageBucketList = chooseHelper
						.getImagesBucketList(true);
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						if (localImageBucketList != null) {
							mImageBucketList.addAll(localImageBucketList);
							mAdapter.notifyDataSetChanged();
						}
					}
				});
			}
		});

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if (position >= 0 && position < mImageBucketList.size()) {
			ImageBucket imageBucket = mImageBucketList.get(position);
			String bucketId = imageBucket.getBucketId();
			if(!TextUtils.isEmpty(bucketId)){
//				Intent intent = new Intent(this, MultiPickGalleryActivity.class);
//				intent.putExtra(MultiPickGalleryActivity.BUCKET_ID, bucketId);
//				intent.putExtra(MultiPickGalleryActivity.MAX_COUNT, mMaxCount);
//				intent.putExtra(MultiPickGalleryActivity.MAX_TOAST, mMaxToast);
//
//				setResult(RESULT_OK, intent);
//				finish();
				Intent picIntent = new Intent(this, MultiPickGalleryActivity.class);
				int mMaxCount=6;
				picIntent.putExtra(MultiPickGalleryActivity.MAX_COUNT, mMaxCount);
				picIntent.putExtra(MultiPickGalleryActivity.MAX_TOAST, "最多选择"+mMaxCount+"张图片");
				picIntent.putExtra(MultiPickGalleryActivity.BUCKET_ID, bucketId);
				startActivityForResult(picIntent, MULIT_PIC_CHOOSE_WITH_DATA);
			}
		}
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == titleButtonId || v.getId() == titleBackId) {
			finish();
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		finish();
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (MULIT_PIC_CHOOSE_WITH_DATA == requestCode && resultCode == RESULT_OK) {
			setResult(RESULT_OK, data);
			finish();
		}
	}

}
