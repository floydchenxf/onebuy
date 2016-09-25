package com.floyd.onebuy.ui.share;

import android.app.Activity;

import com.floyd.onebuy.ui.R;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.weixin.controller.UMWXHandler;
import com.umeng.socialize.weixin.media.CircleShareContent;

/**
 * Created by chenxiaofeng on 16/9/22.
 */
public class WxCircleShare implements IShare {

    private UMSocialService service;
    private Activity mContext;
    private UMImage umImage;

    public WxCircleShare(UMSocialService service, Activity mContext) {
        this.service = service;
        this.mContext = mContext;
        this.umImage = new UMImage(mContext, R.drawable.icon);
    }

    @Override
    public void init() {
        String appId = ShareConstants.WX_APP_ID;
        String appSecret = ShareConstants.WX_APP_SECRITY;

        UMWXHandler wxCircleHandler = new UMWXHandler(mContext, appId, appSecret);
        wxCircleHandler.setToCircle(true);
        wxCircleHandler.addToSocialSDK();
    }

    @Override
    public void setShareContent(String title, String content, String url) {
        CircleShareContent circleMedia = new CircleShareContent();
        circleMedia.setTitle(title);
        circleMedia.setShareContent(content);
        circleMedia.setShareImage(umImage);
        circleMedia.setTargetUrl(url);
        service.setShareMedia(circleMedia);
    }

    @Override
    public void setUMImager(UMImage umImager) {
        this.umImage = umImager;
    }
}
