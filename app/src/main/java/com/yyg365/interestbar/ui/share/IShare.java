package com.yyg365.interestbar.ui.share;

import android.graphics.Bitmap;

import com.umeng.socialize.media.UMImage;

import java.io.File;

/**
 * Created by chenxiaofeng on 16/9/22.
 */
public interface IShare {

    /**
     * 初始化
     */
    public void init();

    /**
     * 设置分享内容
     *
     * @param title
     * @param content
     * @param url
     */
    public void setShareContent(String title, String content, String url);

    /**
     * 设置um分享图片, 不设置默认是应用图标
     *
     * @param umImager
     */
    public void setUMImager(UMImage umImager);


}
