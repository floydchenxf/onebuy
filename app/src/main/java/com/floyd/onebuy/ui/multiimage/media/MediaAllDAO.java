package com.floyd.onebuy.ui.multiimage.media;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore.Images.Media;

import com.floyd.onebuy.ui.multiimage.common.MediaVO;


public class MediaAllDAO extends AbstractCommonTemplateDAO<MediaVO> {

	public MediaAllDAO(Context context) {
		super(context, null);
		this.uri = Media.EXTERNAL_CONTENT_URI;
	}

	@Override
	public ContentValues fillContentValue(MediaVO arg0) {
		return null;
	}

	@Override
	public MediaVO fillObject(Cursor cur) {
		int photoIDIndex = cur.getColumnIndexOrThrow(Media._ID);
		int photoPathIndex = cur.getColumnIndexOrThrow(Media.DATA);
		int bucketDisplayNameIndex = cur.getColumnIndexOrThrow(Media.BUCKET_DISPLAY_NAME);
		int bucketIdIndex = cur.getColumnIndexOrThrow(Media.BUCKET_ID);
		int orientationIndex = cur.getColumnIndexOrThrow(Media.ORIENTATION);

		MediaVO vo = new MediaVO();
		vo.id = cur.getString(photoIDIndex);
		vo.path = cur.getString(photoPathIndex);
		vo.bucketName = cur.getString(bucketDisplayNameIndex);
		vo.bucketId = cur.getString(bucketIdIndex);
		vo.orientation = cur.getInt(orientationIndex);
		return vo;
	}
	
	public String getOriginalImagePath(String image_id) {
		MediaVO vo = this.queryOne(null, Media._ID + "=?", new String[] { image_id });
		return vo.path;
	}

}
