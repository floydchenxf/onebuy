package com.floyd.onebuy.biz.constants;

import android.os.Environment;

import java.io.File;

/**
 * Created by floyd on 15-11-29.
 */
public class EnvConstants {

    public static final String diamondPath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/onebuy";

    public static final String imageRootPath = diamondPath + "/images";

    public static final String thumbRootPath = Environment
            .getExternalStorageDirectory().getAbsolutePath() + "/一元购";

    static {
        File f = new File(imageRootPath);
        if (!f.exists()) {
            f.mkdir();
        }

        File th = new File(thumbRootPath);
        if (!th.exists()) {
            th.mkdir();
        }
    }
}
