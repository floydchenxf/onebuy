package com.floyd.onebuy.ui.multiimage.common;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.provider.MediaStore.Audio.Albums;
import android.provider.MediaStore.Images.Media;
import android.provider.MediaStore.Images.Thumbnails;
import android.support.v4.util.LruCache;
import android.util.Log;

import com.floyd.onebuy.ui.multiimage.media.AlbumDAO;
import com.floyd.onebuy.ui.multiimage.media.MediaAllDAO;
import com.floyd.onebuy.ui.multiimage.media.MediaDAOManager;
import com.floyd.onebuy.ui.multiimage.media.MediaDAOType;
import com.floyd.onebuy.ui.multiimage.media.ThumbnailDAO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 专辑帮助类
 * 
 * 
 */
public class PhotoChooseHelper {
	final String TAG = getClass().getSimpleName();
	private Context context;

	// 缩略图列表
	private HashMap<String, String> thumbnailList = new HashMap<String, String>();
	
	private HashMap<String, String> originPicMap = new HashMap<String, String>();
	
	// 专辑列表
	private List<AlbumVO> albumList = new ArrayList<AlbumVO>();
	private Map<String, ImageBucket> bucketList = new LinkedHashMap<String, ImageBucket>();
	private List<ImageItem> imageItemList = new ArrayList<ImageItem>();

	private LruCache<String, Bitmap> mImageCache;

	/**
	 * 选中的图片集合
	 */
	private List<String> mSelectedList = new ArrayList<String>();

	private static PhotoChooseHelper instance = new PhotoChooseHelper();

	/**
	 * 是否创建了图片集
	 */
	boolean hasBuildImagesBucketList = false;

	private PhotoChooseHelper() {
	}

	public static PhotoChooseHelper getHelper() {
		return instance;
	}

	/**
	 * 初始化
	 * 
	 * @param context
	 */
	public void init(Context context) {
		if (this.context == null) {
			this.context = context;
		}
	}

	/**
	 * 得到缩略图
	 */
	private void getThumbnail() {
		String[] projection = { Thumbnails._ID, Thumbnails.IMAGE_ID, Thumbnails.DATA };
		ThumbnailDAO dao = MediaDAOManager.getInstance().getDAO(context, MediaDAOType.THUMBNAIL);
		List<ThumbnailVO> vos = dao.queryList(projection, null, null);
		if (vos != null) {
			for(ThumbnailVO vo:vos) {
				thumbnailList.put("" + vo.imageId, vo.imagePath);
			}
		}
	}

	/**
	 * 从本地数据库中得到原图
	 */
	void getAlbum() {
		AlbumDAO dao = MediaDAOManager.getInstance().getDAO(context, MediaDAOType.ALBUM);
		String[] projection = { Albums._ID, Albums.ALBUM, Albums.ALBUM_ART, Albums.ALBUM_KEY, Albums.ARTIST, Albums.NUMBER_OF_SONGS };
		albumList = dao.queryList(projection, null, null);
	}

	/**
	 * 得到图片集
	 */
	void buildImagesBucketList() {
		long startTime = System.currentTimeMillis();

		// 构造缩略图索引
		getThumbnail();

		// String orderBy = Media.DATE_ADDED + " desc";
		// 构造相册索引
		String columns[] = new String[] { Media._ID, Media.BUCKET_ID,
				Media.DATA, Media.DISPLAY_NAME, Media.BUCKET_DISPLAY_NAME,
				Media.ORIENTATION };
		
		MediaAllDAO mediaAllDAO = MediaDAOManager.getInstance().getDAO(context, MediaDAOType.MEDIA_ALL);
		
		List<MediaVO> mediaVOs = mediaAllDAO.queryList(columns, null, null);
		
		if (mediaVOs != null) {
			for(MediaVO vo : mediaVOs) {
				String bucketId = vo.bucketId;
				String bucketName = vo.bucketName;
				String id = vo.id;
				String path = vo.path;
				int orientation = vo.orientation;
				ImageBucket bucket = bucketList.get(bucketId);
				if (bucket == null) {
					bucket = new ImageBucket();
					bucket.setImageList(new ArrayList<ImageItem>());
					bucket.setBucketName(bucketName);
					bucket.setBucketId(bucketId);
					bucketList.put(bucketId, bucket);
				}
				bucket.setCount(bucket.getCount() + 1);
				ImageItem imageItem = new ImageItem();
				imageItem.setImageId(id);
				imageItem.setImagePath(path);
				imageItem.setThumbnailPath(thumbnailList.get(id));
				imageItem.setOrientation(orientation);
				bucket.getImageList().add(imageItem);
				originPicMap.put(path, thumbnailList.get(id));
				imageItemList.add(imageItem);
			}
		}

		hasBuildImagesBucketList = true;
		long endTime = System.currentTimeMillis();
		Log.d(TAG, "use time: " + (endTime - startTime) + " ms");
	}

	/**
	 * 得到图片集
	 * 
	 * @param refresh
	 * @return
	 */
	public synchronized List<ImageBucket> getImagesBucketList(boolean refresh) {
		if (refresh || (!refresh && !hasBuildImagesBucketList)) {
            thumbnailList.clear();
            originPicMap.clear();
            albumList.clear();
            bucketList.clear();
            imageItemList.clear();
			buildImagesBucketList();
		}
		
		List<ImageBucket> tmpList = new ArrayList<ImageBucket>();
		tmpList.addAll(bucketList.values());
		return tmpList;
	}

	public synchronized ImageBucket getImageBucket(String bucketId) {
		if (!hasBuildImagesBucketList) {
			buildImagesBucketList();
		}
		return bucketList.get(bucketId);
	}

	public ImageBucket getMaxImageBucket() {
		ImageBucket result = null;
		List<ImageBucket> buckets = getImagesBucketList(false);
		if (buckets != null) {
			int max = 0;
			for (ImageBucket bucket:buckets) {
				int count = bucket.getCount();
				if (max < count) {
					max = count;
					result = bucket;
				}
			}
		}

		return result;
	}

	public synchronized List<ImageItem> getImageItemList() {
		if (!hasBuildImagesBucketList) {
			buildImagesBucketList();
		}
		return new ArrayList<ImageItem>(imageItemList);
	}

	public synchronized void recycle() {
		mSelectedList.clear();
		if (mImageCache != null) {
			mImageCache.evictAll();
		}
		thumbnailList.clear();
		albumList.clear();
		bucketList.clear();
		imageItemList.clear();
		hasBuildImagesBucketList = false;
	}

	public LruCache<String, Bitmap> getImageCache() {
		if (mImageCache == null) {
			long max = MemoryManager.getMaxMem();
			long vmSize = MemoryManager.getVMAlloc();
			long nativeSieze = MemoryManager.getNativeHeapSize();
			long avail = max - vmSize - nativeSieze;
			int memCacheSize = (int) Math.max(avail / 6, max / 16);
			Log.d("test", "memCacheSize:" + memCacheSize);
			mImageCache = new LruCache<String, Bitmap>(memCacheSize) {
				@SuppressLint("NewApi")
				@Override
				protected int sizeOf(String key, Bitmap bitmap) {
					int size = 0;
					if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB_MR1) {
						size = bitmap.getRowBytes() * bitmap.getHeight();
					} else {
						size = bitmap.getByteCount();
					}
					return size;
				}
			};
		}
		return mImageCache;
	}

	public synchronized void clear() {
		thumbnailList.clear();
		originPicMap.clear();
		albumList.clear();
		bucketList.clear();
		imageItemList.clear();
		mSelectedList.clear();
	}

	public List<String> getSelectedList() {
		return mSelectedList;
	}
	
	public String getThumbnailPic(String originPic) {
		return originPicMap.get(originPic);
	}

}
