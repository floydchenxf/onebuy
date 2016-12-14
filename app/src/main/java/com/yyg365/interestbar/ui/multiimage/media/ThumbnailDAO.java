package com.yyg365.interestbar.ui.multiimage.media;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import com.yyg365.interestbar.ui.multiimage.common.ThumbnailVO;


/**
 * Created by floyd on 15-7-17.
 */
public class ThumbnailDAO extends AbstractCommonTemplateDAO<ThumbnailVO> {

	public ThumbnailDAO(Context context) {
		super(context, null);
		this.uri = MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI;
	}

	@Override
	public ThumbnailVO fillObject(Cursor cursor) {
		ThumbnailVO vo = new ThumbnailVO();
		int _idColumn = cursor.getColumnIndex(MediaStore.Images.Thumbnails._ID);
		int image_idColumn = cursor.getColumnIndex(MediaStore.Images.Thumbnails.IMAGE_ID);
		int dataColumn = cursor.getColumnIndex(MediaStore.Images.Thumbnails.DATA);

		vo.id = cursor.getInt(_idColumn);
		vo.imageId = cursor.getInt(image_idColumn);
		vo.imagePath = cursor.getString(dataColumn);

		return vo;
	}

	@Override
	public ContentValues fillContentValue(ThumbnailVO thumbnailVO) {
		return null;
	}
}
