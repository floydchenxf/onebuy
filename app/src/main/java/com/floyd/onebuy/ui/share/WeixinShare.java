package com.floyd.onebuy.ui.share;

import android.app.Activity;

import com.floyd.onebuy.ui.R;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.QZoneSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;
import com.umeng.socialize.weixin.media.WeiXinShareContent;

/**
 * Created by chenxiaofeng on 16/9/22.
 */
public class WeixinShare implements IShare {
    private UMSocialService service;
    private Activity mContext;

    public WeixinShare(UMSocialService service, Activity mContext) {
        this.service = service;
        this.mContext = mContext;
    }

    @Override
    public void init() {
        String appId = ShareConstants.WX_APP_ID;
        String appSecret = ShareConstants.WX_APP_SECRITY;

        UMWXHandler wxHandler = new UMWXHandler(mContext, appId, appSecret);
        wxHandler.addToSocialSDK();
    }

    @Override
    public void setShareContent(String title, String content, String url) {
        WeiXinShareContent weixinContent = new WeiXinShareContent();
        weixinContent.setTitle(title);
        weixinContent.setShareContent(content);
        //设置分享内容跳转URL
        weixinContent.setTargetUrl(url);
        //设置分享图片
        weixinContent.setShareImage(new UMImage(mContext, R.drawable.icon));
        service.setShareMedia(weixinContent);
    }
}
