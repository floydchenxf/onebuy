package com.floyd.onebuy.ui.multiimage.media;

import android.content.Context;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by floyd on 15-7-17.
 */
public class MediaDAOManager {

    private static final String TAG = "MediaDAOManager";

    private static MediaDAOManager instance = new MediaDAOManager();

    private static Map<MediaDAOType, Object> daoMaps = new HashMap<MediaDAOType, Object>();

    public static MediaDAOManager getInstance() {
        return instance;
    }

    public <T> T getDAO(Context context, MediaDAOType type) {
        Object obj = daoMaps.get(type);
        if (obj != null) {
            return (T) obj;
        }

        if (type == MediaDAOType.ALBUM) {
            obj = new AlbumDAO(context.getApplicationContext());
        } else if (type == MediaDAOType.THUMBNAIL) {
            obj = new ThumbnailDAO(context.getApplicationContext());
        } else if (type == MediaDAOType.MEDIA_ALL) {
        	obj = new MediaAllDAO(context.getApplicationContext());
        }

        if (obj != null) {
            daoMaps.put(type, obj);
        } else {
            Log.e(TAG, "------type:" + type + "----can not DAO!");
        }
        return (T) obj;
    }
}
