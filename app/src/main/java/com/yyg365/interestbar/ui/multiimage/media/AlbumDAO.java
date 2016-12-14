package com.yyg365.interestbar.ui.multiimage.media;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore.Audio.Albums;

import com.yyg365.interestbar.ui.multiimage.common.AlbumVO;


/**
 * Created by floyd on 15-7-17.
 */
public class AlbumDAO extends AbstractCommonTemplateDAO<AlbumVO> {

	public AlbumDAO(Context context) {
		super(context, null);
		this.uri = Albums.EXTERNAL_CONTENT_URI;
	}

	@Override
	public AlbumVO fillObject(Cursor cur) {
		int _idColumn = cur.getColumnIndex(Albums._ID);
		int albumColumn = cur.getColumnIndex(Albums.ALBUM);
		int albumArtColumn = cur.getColumnIndex(Albums.ALBUM_ART);
		int albumKeyColumn = cur.getColumnIndex(Albums.ALBUM_KEY);
		int artistColumn = cur.getColumnIndex(Albums.ARTIST);
		int numOfSongsColumn = cur.getColumnIndex(Albums.NUMBER_OF_SONGS);
		
		AlbumVO vo = new AlbumVO();
		vo.id = cur.getInt(_idColumn);
		vo.album = cur.getString(albumColumn);
		vo.albumArt = cur.getString(albumArtColumn);
		vo.albumKey = cur.getString(albumKeyColumn);
		vo.artist = cur.getString(artistColumn);
		vo.numOfSongs = cur.getInt(numOfSongsColumn);
		return vo;
	}

	@Override
	public ContentValues fillContentValue(AlbumVO albumVO) {
		return null;
	}

}
