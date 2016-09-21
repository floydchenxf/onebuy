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

    public WxCircleShare(UMSocialService service, Activity mContext) {
        this.service = service;
        this.mContext = mContext;
    }

    @Override
    public void init() {
        String appId = "wx6f4a5ebb3d2cd11e";
        String appSecret = "64710023bac7d1b314c1b3ed3db5949d";

        UMWXHandler wxCircleHandler = new UMWXHandler(mContext, appId, appSecret);
        wxCircleHandler.setToCircle(true);
        wxCircleHandler.addToSocialSDK();
    }

    @Override
    public void setShareContent(String title, String content, String url) {
        CircleShareContent circleMedia = new CircleShareContent();
        circleMedia.setShareContent(content);
        //设置朋友圈title
        circleMedia.setTitle(title);
        circleMedia.setShareImage(new UMImage(mContext, R.drawable.icon));
        circleMedia.setTargetUrl(url);
        service.setShareMedia(circleMedia);
    }
}
