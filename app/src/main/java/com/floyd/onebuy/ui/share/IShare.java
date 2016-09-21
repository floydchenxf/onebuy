package com.floyd.onebuy.ui.share;

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
}
