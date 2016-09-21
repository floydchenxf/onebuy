package com.floyd.onebuy.ui.share;

import android.app.Activity;

import com.floyd.onebuy.ui.R;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.media.QZoneShareContent;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.QZoneSsoHandler;

/**
 * Created by chenxiaofeng on 16/9/22.
 */
public class QQZoneShare implements IShare {

    private UMSocialService service;
    private Activity mContext;

    public QQZoneShare(UMSocialService service, Activity mContext) {
        this.service = service;
        this.mContext = mContext;
    }

    @Override
    public void init() {
        String appId = "1105636877";
        String appKey = "WUNMS4IJbYvk8Fdh";

        // 添加QZone平台
        QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(mContext, appId, appKey);
        qZoneSsoHandler.addToSocialSDK();
    }

    @Override
    public void setShareContent(String title, String content, String url) {
        QZoneShareContent qzone = new QZoneShareContent();
        //设置分享文字
        qzone.setShareContent(content);
        //设置点击消息的跳转URL
        qzone.setTargetUrl(url);
        qzone.setTitle(title);
        //设置分享图片
        qzone.setShareImage(new UMImage(mContext, R.drawable.icon));
        service.setShareMedia(qzone);
    }
}
