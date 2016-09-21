package com.floyd.onebuy.ui.share;

import android.app.Activity;

import com.floyd.onebuy.ui.R;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.media.QQShareContent;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;

/**
 * Created by chenxiaofeng on 16/9/22.
 */
public class QQShare implements IShare {

    private UMSocialService service;
    private Activity mContext;

    public QQShare(UMSocialService service, Activity mContext) {
        this.service = service;
        this.mContext = mContext;
    }

    @Override
    public void init() {
        String appId = "1105636877";
        String appKey = "WUNMS4IJbYvk8Fdh";
        // 添加QQ支持, 并且设置QQ分享内容的target url
        UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(mContext, appId, appKey);
        qqSsoHandler.addToSocialSDK();
    }

    @Override
    public void setShareContent(String title, String content, String url) {
        QQShareContent qqShareContent = new QQShareContent();
        //设置分享文字
        qqShareContent.setShareContent(content);
        //设置分享title
        qqShareContent.setTitle(title);
        //设置分享图片
        qqShareContent.setShareImage(new UMImage(mContext, R.drawable.icon));
        //设置点击分享内容的跳转链接
        qqShareContent.setTargetUrl(url);
        service.setShareMedia(qqShareContent);
    }
}
