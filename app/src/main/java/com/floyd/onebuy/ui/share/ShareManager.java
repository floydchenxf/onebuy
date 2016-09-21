package com.floyd.onebuy.ui.share;

import android.app.Activity;
import android.content.Context;

import com.floyd.onebuy.ui.R;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SnsPlatform;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.media.QQShareContent;
import com.umeng.socialize.media.QZoneShareContent;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.QZoneSsoHandler;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;
import com.umeng.socialize.weixin.media.WeiXinShareContent;

import java.util.List;

/**
 * Created by chenxiaofeng on 16/9/22.
 */
public class ShareManager {

    private UMSocialService service;
    private Activity mContext;

    public ShareManager(Activity mContext) {
        this.mContext = mContext;
        service = UMServiceFactory.getUMSocialService("com.umeng.share");
    }

    public static ShareManager getInstance(Activity context) {
        return new ShareManager(context);
    }

    public void init(SHARE_MEDIA... medias) {
        service.getConfig().setPlatforms(medias);
    }

    public IShare createShare(SHARE_MEDIA m) {
        IShare share = null;
        if (m == SHARE_MEDIA.QQ) {
            share = new QQShare(service, mContext);
        } else if (m == SHARE_MEDIA.QZONE) {
            share = new QQZoneShare(service, mContext);
        } else if (m == SHARE_MEDIA.WEIXIN) {
            share = new WeixinShare(service, mContext);
        } else if (m == SHARE_MEDIA.WEIXIN_CIRCLE) {
            share = new WxCircleShare(service, mContext);
        }

        return share;
    }

    public void show(boolean show) {
        service.openShare(mContext, show);
    }
}
