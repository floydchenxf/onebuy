package com.floyd.onebuy.ui.multiimage;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.floyd.onebuy.R;
import com.floyd.onebuy.biz.tools.ThumbnailUtils;
import com.floyd.onebuy.channel.threadpool.WxDefaultExecutor;
import com.floyd.onebuy.ui.multiimage.base.MulitImageVO;
import com.floyd.onebuy.ui.multiimage.base.PicViewObject;
import com.floyd.onebuy.ui.multiimage.common.AlbumAdapter;
import com.floyd.onebuy.ui.multiimage.common.GalleryAdapter;
import com.floyd.onebuy.ui.multiimage.common.ImageBucket;
import com.floyd.onebuy.ui.multiimage.common.ImageItem;
import com.floyd.onebuy.ui.multiimage.common.OnCheckChangedListener;
import com.floyd.onebuy.ui.multiimage.common.PhotoChooseHelper;
import com.floyd.onebuy.view.YWPopupWindow;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 图片多选界面
 *
 * @author yuanyi.rss
 *
 */
public class MultiPickGalleryActivity extends Activity implements
		OnClickListener, OnItemClickListener, OnCheckChangedListener {

	public static final String MAX_COUNT = "maxCount"; // 最大可选择数目
	public static final String BUCKET_ID = "bucketId"; // 相册id
	public static final String RESULT_LIST = "result_list"; // 返回的图片列表

	public static final String NEED_COMPRESS = "need_compress"; // 是否压缩


	public static final String MAX_TOAST = "max_toast"; // 超过数量上限提示
	public static final String PRE_CHECKED = "pre_checked_images"; // 预先选中的图
	private static final int SELECT_PREVIEW_REQUEST_CODE = 900;
	private static final int SELECT_ITEM_REQUEST_CODE = 901;
	public static final int SELECT_ALBUM_REQUEST_CODE = 902;
	public static final String ALL_PIC = "常用图片";
	public static final String TITLE_RIGHT_TEXT = "titleRightText";//右上角按钮的文案

	private GridView gridGallery;
	private Handler mHandler;
	private GalleryAdapter mAdapter;

	private ImageView imgNoMedia;
	private TextView mPreviewBtn;

	private TextView picDirView;

	private TextView mSelectedCount;

	// private String action;

	private int mMaxCount; // 最大可选择数目
	private String mMaxToast;
	private String mTitleRightText;

	private PhotoChooseHelper photoChooseHelper;

	private List<ImageItem> mImageItemList = new ArrayList<ImageItem>();

	//	private String mBucketId;
	private String mUserId;

	private YWPopupWindow ywPopupWindow;

	private ListView albumListView;

	private AlbumAdapter albumAdapter;

	private ImageBucket defaultImageBucket;
	private int currentDirIndex;

	private Button finish;
	private boolean mNeddCompress=true;
//	private RelativeLayout mLeftButton;
//	private ImageView mSendOriginalCheck;
//	private TextView mSendOriginal;
//	private boolean mUseOrignal =false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		int layoutId = R.layout.aliwx_multi_pick_gallery;
		setContentView(layoutId);;

		init();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onPause() {
		super.onPause();
		ywPopupWindow.hidePopUpWindow();
	}

	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	private void init() {
		Intent intent = getIntent();
		ArrayList<String> preCheckedList = null;

        mTitleRightText="发送"; //默认值是发送
		if (intent != null) {
			mMaxCount = intent.getIntExtra(MAX_COUNT, -1);
			mMaxToast = intent.getStringExtra(MAX_TOAST);
            if(intent.hasExtra(TITLE_RIGHT_TEXT)){
                mTitleRightText = intent.getStringExtra(TITLE_RIGHT_TEXT);
            }
//			mBucketId = intent.getStringExtra(BUCKET_ID);
			preCheckedList = intent.getStringArrayListExtra(PRE_CHECKED);
		}

		mHandler = new Handler();

		photoChooseHelper = PhotoChooseHelper.getHelper();
		photoChooseHelper.init(getApplicationContext());

		final View button = findViewById(R.id.title_back);
		button.setVisibility(View.VISIBLE);
		button.setOnClickListener(this);
//		TextView titleButton = (TextView) findViewById(R.id.title_button"));
//		titleButton.setVisibility(View.VISIBLE);
//		titleButton.setText(ResourceLoader.getStringIdByName("aliwx_cancel"));
//		titleButton.setOnClickListener(this);

		finish = (Button) findViewById(R.id.finish);
		finish.setOnClickListener(this);
        finish.setText(mTitleRightText);
		mPreviewBtn = (TextView) findViewById(R.id.preview);
		mPreviewBtn.setOnClickListener(this);

		picDirView = (TextView) findViewById(R.id.pic_dir);
		picDirView.setOnClickListener(this);
		picDirView.setText(ALL_PIC);

		ywPopupWindow = new YWPopupWindow(this);
		int popupLayoutId = R.layout.aliwx_multi_pick_album;
		int dd = R.dimen.aliwx_popup_height;
		float a = this.getResources().getDimension(dd);
		ywPopupWindow.initView(picDirView, popupLayoutId, (int)a, new YWPopupWindow.ViewInit() {
			@Override
			public void initView(View v) {
				int albumListViewId = R.id.album_list;
				albumListView = (ListView) v.findViewById(albumListViewId);
				albumListView.setFastScrollEnabled(false);
				albumListView.setFastScrollAlwaysVisible(false);
				albumListView.setVerticalScrollBarEnabled(false);
				List<ImageBucket> mImageBucketList = photoChooseHelper.getImagesBucketList(false);
				if (mImageBucketList != null && defaultImageBucket != null) {
					mImageBucketList.add(0, defaultImageBucket);
				}
				albumAdapter = new AlbumAdapter(MultiPickGalleryActivity.this, mImageBucketList,new OnClickListener() {
					@Override
					public void onClick(View view) {
						int albumPicId = R.id.album_pic;
						int i = (Integer) view.getTag(albumPicId);
						currentDirIndex = i;
						albumAdapter.setIndex(currentDirIndex);
						ImageBucket bucket = albumAdapter.getItem(i);
						picDirView.setText(bucket.getBucketName());
						List<ImageItem> images = bucket.getImageList();
						MultiPickGalleryActivity.this.mImageItemList.clear();
						MultiPickGalleryActivity.this.mImageItemList.addAll(images);
						mAdapter.updateDataAndNotify(images);
						ywPopupWindow.hidePopUpWindow();
					}
				});
				albumListView.setAdapter(albumAdapter);
			}
		});

        List<String> selectedSet = photoChooseHelper.getSelectedList();
        if ((preCheckedList != null && preCheckedList.size() > 0) ) { //preCheckedList为空
            selectedSet.addAll(preCheckedList);
            int count = selectedSet.size();
            finish.setText(mTitleRightText+"("+ String.valueOf(count)+"/"+mMaxCount+")");
            mPreviewBtn.setEnabled(true);
        } else {
            mPreviewBtn.setEnabled(false);
        }

		mSelectedCount = (TextView) findViewById(R.id.selected_count);
		mSelectedCount.setOnClickListener(this);
		gridGallery = (GridView) findViewById(R.id.gridGallery);
		gridGallery.setFastScrollEnabled(false);
		gridGallery.setFastScrollAlwaysVisible(false);
		gridGallery.setVerticalScrollBarEnabled(false);
		mAdapter = new GalleryAdapter(getApplicationContext(), mImageItemList);
		gridGallery.setOnItemClickListener(this);
		mAdapter.setMaxCount(mMaxCount);
		mAdapter.setMaxToast(mMaxToast);
		mAdapter.setOnCheckChangedListener(this);
		gridGallery.setAdapter(mAdapter);
		imgNoMedia = (ImageView) findViewById(R.id.imgNoMedia);
		mSelectedCount.setVisibility(View.GONE);

		WxDefaultExecutor.getInstance().executeLocal(new Runnable() {
			@Override
			public void run() {
				photoChooseHelper.getImagesBucketList(true);
				final List<ImageItem> localImageItemList = photoChooseHelper.getImageItemList();

				final List<ImageItem> allPics = new ArrayList<ImageItem>();
				if (localImageItemList != null) {
					for (ImageItem item : localImageItemList) {
						if (item != null && item.getImagePath() != null) {
							String imagepath = item.getImagePath();
							if (imagepath.toLowerCase().indexOf("dcim") > 0 || imagepath.toLowerCase().indexOf("screenshots") > 0 || imagepath.toLowerCase().indexOf("pictures") > 0) {
								allPics.add(item);
							}
						}
					}
				}

				if (allPics.isEmpty()) {
					allPics.addAll(localImageItemList);
				}

				defaultImageBucket = new ImageBucket();
				defaultImageBucket.setBucketName(ALL_PIC);
				defaultImageBucket.setCount(allPics.size());
				defaultImageBucket.setImageList(allPics);
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						if (localImageItemList != null) {
							mImageItemList.clear();
							mImageItemList.addAll(allPics);
							mAdapter.notifyDataSetChanged();
							checkImageStatus();
						}
					}
				});
			}
		});

//		initLeftBottomButton();

//		} else {
//			List<String> selectedSet = photoChooseHelper.getSelectedList();
//			int count = 0;
//			if (selectedSet != null) {
//				count = selectedSet.size();
//				if (count > 0) {
//					mSelectedCount.setText(String.valueOf(count));
//				} else {
//					mPreviewBtn.setEnabled(false);
//				}
//			}
//
//			WXThreadPoolMgr.getInstance().doAsyncRun(new Runnable() {
//				@Override
//				public void run() {
//					ImageBucket localImageBucket = photoChooseHelper.getImageBucket(mBucketId);
//					if (localImageBucket != null) {
//						final List<ImageItem> localImageItemList = localImageBucket
//								.getImageList();
//						mHandler.post(new Runnable() {
//							@Override
//							public void run() {
//								if (localImageItemList != null) {
//									mImageItemList.addAll(localImageItemList);
//									mAdapter.notifyDataSetChanged();
//									checkImageStatus();
//								}
//							}
//						});
//					}
//				}
//			}, true);
//		}
	}

	private String getSendBtnTitle(int selectedCount){
		return "发送("+ String.valueOf(selectedCount)+"/"+mMaxCount+")";
	}

	private void reInit(final String bucketId) {
		List<String> selectedSet = photoChooseHelper.getSelectedList();
		int count = 0;
		if (selectedSet != null) {
			count = selectedSet.size();
			if (count > 0) {
//				mSelectedCount.setText(String.valueOf(count));
				finish.setText(mTitleRightText+"("+ String.valueOf(count)+"/"+mMaxCount+")");
				mPreviewBtn.setEnabled(false);
			} else {
                finish.setText(mTitleRightText);
				mPreviewBtn.setEnabled(false);
			}
		}

		WxDefaultExecutor.getInstance().executeLocal(new Runnable() {
			@Override
			public void run() {
				ImageBucket localImageBucket = photoChooseHelper
						.getImageBucket(bucketId);
				if (localImageBucket != null) {
					final List<ImageItem> localImageItemList = localImageBucket
							.getImageList();
					mHandler.post(new Runnable() {
						@Override
						public void run() {
							if (localImageItemList != null) {
								mImageItemList.clear();
								mImageItemList.addAll(localImageItemList);
								mAdapter.notifyDataSetChanged();
								checkImageStatus();
							}
						}
					});
				}
			}
		});
	}

	private void checkImageStatus() {
		if (mAdapter.isEmpty()) {
			imgNoMedia.setVisibility(View.VISIBLE);
		} else {
			imgNoMedia.setVisibility(View.GONE);
		}
	}

	private void updateSelectedCountView() {
		List<String> selectedList = mAdapter.getSelectedSet();
		if (selectedList != null) {
			int count = selectedList.size();
			if (count > 0) {
//				mSelectedCount.setVisibility(View.VISIBLE);
//				mSelectedCount.setText(String.valueOf(count));
				finish.setText(mTitleRightText + "(" + String.valueOf(count)+"/"+mMaxCount+")");
				mPreviewBtn.setEnabled(true);
			} else {
                finish.setText(mTitleRightText);
				mPreviewBtn.setEnabled(false);
//				mSelectedCount.setVisibility(View.GONE);
			}
		}
	}

	OnItemClickListener mItemSingleClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> l, View v, int position, long id) {
			ImageItem item = mAdapter.getItem(position);
			Intent data = new Intent().putExtra("single_path",
					item.getImagePath());
			setResult(RESULT_OK, data);
			finish();
		}
	};

	@Override
	protected void onDestroy() {
		if (mAdapter != null) {
			mAdapter.recycle();
		}
		super.onDestroy();
	}

	@Override
	public void onBackPressed() {
		if (ywPopupWindow.isShowing() && !this.isFinishing()) {
			ywPopupWindow.hidePopUpWindow();
			return;
		}
		onback();
		super.onBackPressed();
	}

	private void onback() {
		// Intent intent = new Intent(this, MultiPickAlbumActivity.class);
		// intent.putExtra(MultiImageActivity.MAX_COUNT, mMaxCount);
		// intent.putExtra(MultiImageActivity.MAX_TOAST, mMaxToast);
		// startActivityForResult(intent, SELECT_ALBUM_REQUEST_CODE);
		PhotoChooseHelper.getHelper().getSelectedList().clear();//清除一下已选择的列表
		finish();
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.title_back) {
			onback();
		} else if (id == R.id.finish) {
			setResult(null,true);
		} else if (id == R.id.preview) {
			List<String> selectedList = PhotoChooseHelper.getHelper()
					.getSelectedList();
			if (selectedList != null && selectedList.size() > 0) {
				if (!this.isFinishing()) {
					ywPopupWindow.hidePopUpWindow();
				}
				List<PicViewObject> result = new ArrayList<PicViewObject>();
				ArrayList<String> checkedList = new ArrayList<String>();
				long _id = 100;
				if (selectedList != null && selectedList.size() > 0) {
					for (String path : selectedList) {
						PicViewObject view = new PicViewObject();
						view.setPicPreViewUrl(path);
						view.setPicUrl(path);
						view.setPicId(_id++);
						if(path.endsWith(".gif") || path.endsWith(".GIF")){ //简单的增加对GIF格式支持，此种判断不完全准确
							view.setPicType(PicViewObject.GIF);
						}else{
							view.setPicType(PicViewObject.IMAGE);
						}
						result.add(view);
						checkedList.add(path);
					}
				}
				MulitImageVO vo = new MulitImageVO(0, result);
				Intent intent = new Intent(this, MultiImageActivity.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable(MultiImageActivity.MULIT_IMAGE_VO, vo);
				intent.putExtra(MultiImageActivity.MULIT_IMAGE_VO, bundle);
				intent.putExtra(MultiImageActivity.MULIT_IMAGE_PICK_MODE,
						MultiImageActivity.MULIT_IMAGE_PICK_MODE_SELECT);
				intent.putStringArrayListExtra(
						MultiImageActivity.MULIT_IMAGE_CHECKED_LIST,
						checkedList);
				intent.putExtra(MultiImageActivity.MAX_COUNT, mMaxCount);
				intent.putExtra(MultiImageActivity.MAX_TOAST, mMaxToast);
				startActivityForResult(intent, SELECT_PREVIEW_REQUEST_CODE);
			}
//		} else if (id == R.id.title_button")) {
//			PhotoChooseHelper.getHelper().recycle();
//			setResult(RESULT_CANCELED);
//			finish();
		} else if (id == R.id.selected_count) {
		} else if (id == R.id.pic_dir){
			if (ywPopupWindow.isShowing()) {
				ywPopupWindow.hidePopUpWindow();
			} else {
				List<ImageBucket> mImageBucketList = photoChooseHelper.getImagesBucketList(false);
				if (albumAdapter != null && mImageBucketList != null) {
					if (defaultImageBucket != null) {
						mImageBucketList.add(0, defaultImageBucket);
					}
					albumAdapter.updateDataAndNotify(mImageBucketList);
				}
				ywPopupWindow.showPopUpWindow();
			}
//		}else if (id == R.id.left_button) {
//			changeSendOrignalState();
		}

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
							long id) {
		List<PicViewObject> result = new ArrayList<PicViewObject>();
		ArrayList<String> checkedList = new ArrayList<String>();
		List<String> selectedSet = PhotoChooseHelper.getHelper()
				.getSelectedList();
		int size = selectedSet.size();
		if (size >= mMaxCount) {
			for(int i = size - 1; i >= mMaxCount - 1; i--) {
				selectedSet.remove(i);
			}
		}
//		selectedSet.add(0, mAdapter.getItem(position).getImagePath());
		checkedList.addAll(selectedSet);

		long picId = 100;
		if (mImageItemList != null && mImageItemList.size() > 0) {
			for (ImageItem imageItem : mImageItemList) {
				String path = imageItem.getImagePath();
				String thumbnailPath = imageItem.getThumbnailPath();
				if (TextUtils.isEmpty(thumbnailPath)) {
					thumbnailPath = path;
				}
				PicViewObject picViewObject = new PicViewObject();
				picViewObject.setPicPreViewUrl(thumbnailPath);
				picViewObject.setPicUrl(path);
				picViewObject.setPicId(picId++);
				picViewObject.setPicType(PicViewObject.IMAGE);
				result.add(picViewObject);
			}
		}

		MulitImageVO vo = new MulitImageVO(position, result);
		Intent intent = new Intent(this, MultiImageActivity.class);
		Bundle bundle = new Bundle();
		bundle.putSerializable(MultiImageActivity.MULIT_IMAGE_VO, vo);
		intent.putExtra(MultiImageActivity.MULIT_IMAGE_VO, bundle);
		intent.putExtra(MultiImageActivity.MULIT_IMAGE_PICK_MODE,
				MultiImageActivity.MULIT_IMAGE_PICK_MODE_SELECT);

		// intent.putExtra(MultiImageActivity.MULIT_IMAGE_PICK_MODE,
		// MultiImageActivity.MULIT_IMAGE_PICK_MODE_DELETE);

		intent.putStringArrayListExtra(
				MultiImageActivity.MULIT_IMAGE_CHECKED_LIST, checkedList);

		intent.putExtra(MultiImageActivity.MAX_COUNT, mMaxCount);
		intent.putExtra(MultiImageActivity.MAX_TOAST, mMaxToast);

		try {
			startActivityForResult(intent, SELECT_ITEM_REQUEST_CODE);
		} catch (Exception e) {
			Log.e("Exception", e.getMessage(), e);
		}
	}

	@Override
	public void OnCheckChanged() {
		updateSelectedCountView();
//		checkAndUpdateSendOrignalState();
	}

	private void setResult(Intent data,boolean needCompress) {
		ArrayList<String> selectedList = new ArrayList<String>(
				photoChooseHelper.getSelectedList());

		Intent intent = new Intent();
		intent.putStringArrayListExtra(RESULT_LIST, selectedList);
		intent.putExtra(NEED_COMPRESS,needCompress);
		setResult(RESULT_OK, intent);

		PhotoChooseHelper.getHelper().recycle();
		finish();
	}
	//FIXME shuheng [solved] 实时判断是否需要压缩成原图
	private boolean needCompress(Intent data){
		boolean needCompress = true;
		if(data!=null){
			//todo [发送原图]和[压缩相反]，所以这里取反，默认［压缩］
			needCompress=!data.getBooleanExtra(MultiImageActivity.SEND_ORIGINAL,false);
		}
		return needCompress;
	}
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == SELECT_PREVIEW_REQUEST_CODE
					|| requestCode == SELECT_ITEM_REQUEST_CODE) {
				ArrayList<String> checkedList = data
						.getStringArrayListExtra(MultiImageActivity.MULIT_IMAGE_RESULT_CHECKED_LIST);
				ArrayList<String> uncheckedList = data
						.getStringArrayListExtra(MultiImageActivity.MULIT_IMAGE_RESULT_UNCHECKED_LIST);

				List<String> selectedList = mAdapter.getSelectedSet();
				if (selectedList != null) {
					selectedList.removeAll(uncheckedList);
					selectedList.addAll(checkedList);
				}

				setResult(data,needCompress(data));
			} else if (requestCode == SELECT_ALBUM_REQUEST_CODE) {
				String bucketId = data.getStringExtra(BUCKET_ID);
				reInit(bucketId);
			}
		} else if (resultCode == Activity.RESULT_CANCELED) {
			if ((requestCode == SELECT_PREVIEW_REQUEST_CODE || requestCode == SELECT_ITEM_REQUEST_CODE)
					&& data != null) {
				ArrayList<String> checkedList = data
						.getStringArrayListExtra(MultiImageActivity.MULIT_IMAGE_RESULT_CHECKED_LIST);
				ArrayList<String> uncheckedList = data
						.getStringArrayListExtra(MultiImageActivity.MULIT_IMAGE_RESULT_UNCHECKED_LIST);

				List<String> selectedList = mAdapter.getSelectedSet();
				if (selectedList != null) {
					selectedList.removeAll(uncheckedList);
					selectedList.addAll(checkedList);
				}
				mAdapter.notifyDataSetChanged();
				updateSelectedCountView();
			}
		}

	}
//	private void initLeftBottomButton() {
//		mLeftButton = (RelativeLayout) findViewById(R.id.left_button);
//		mLeftButton.setVisibility(View.VISIBLE);
//		mLeftButton.setOnClickListener(this);
//		mSendOriginalCheck = (ImageView) findViewById(R.id.send_original_check);
//		mSendOriginal = (TextView) findViewById(R.id.send_original);
//	}
	/**
	 * 切换［原图］按钮开启状态
	 */
//	private void changeSendOrignalState() {
//		if(mLeftButton.getVisibility()== View.VISIBLE&&!mUseOrignal){
//			mSendOriginal.setText(getCurrentTotalPicSize());
//			mSendOriginal.setTextColor(R.color.aliwx_color_blue);
//			mSendOriginalCheck.setImageResource(R.drawable.aliwx_send_original_btn_on);
//			mUseOrignal =true;
//		}else if(mLeftButton.getVisibility()== View.VISIBLE&& mUseOrignal){
//			mSendOriginal.setText(getCurrentTotalPicSize());
//			mSendOriginal.setTextColor(R.color.aliwx_color_gray_02);
//			mSendOriginalCheck.setImageResource(R.drawable.aliwx_send_original_btn_off);
//			mUseOrignal =false;
//		}
//	}

	/**
	 * 更新［原图］按钮状态
	 */
//	private void checkAndUpdateSendOrignalState() {
//		if(mLeftButton.getVisibility()== View.VISIBLE&& mUseOrignal){
//			mSendOriginal.setText(getCurrentTotalPicSize());
//			mSendOriginal.setTextColor(R.color.aliwx_color_blue);
//					mSendOriginalCheck.setImageResource(R.drawable.aliwx_send_original_btn_on);
//		}else if(mLeftButton.getVisibility()== View.VISIBLE&&!mUseOrignal){
//			mSendOriginal.setText(getCurrentTotalPicSize());
//			mSendOriginal.setTextColor(R.color.aliwx_color_gray_02);
//					mSendOriginalCheck.setImageResource(R.drawable.aliwx_send_original_btn_off);
//		}
//	}
	//FIXME shuheng 获取所有图片的大小
	private String getCurrentTotalPicSize(){
		long totalFileSize=0;
		List<String> selectedSet = mAdapter.getSelectedSet();
		if(selectedSet!=null)
			for (String picLocalPath : selectedSet) {
				if(picLocalPath!=null){
					File f = new File(picLocalPath);
					if (f.exists() && f.isFile()) {
						totalFileSize+= f.length();
					}
				}

			}
		if(totalFileSize>0){
			StringBuilder sendOriginalText = new StringBuilder(this.getResources().getString(R.string.aliwx_send_original)).append("(共").append(ThumbnailUtils.bytes2KOrM(totalFileSize)).append(")");
			return sendOriginalText.toString();
		}
		return  new StringBuilder(R.string.aliwx_send_original).toString();
	}
}
