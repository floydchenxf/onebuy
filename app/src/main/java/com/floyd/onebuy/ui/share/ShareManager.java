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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by chenxiaofeng on 16/9/22.
 */
public class ShareManager {

    public static final String COM_UMENG_SHARE = "com.umeng.share";
    private UMSocialService service;
    private Activity mContext;
    private Map<SHARE_MEDIA, IShare> shareMap = new HashMap<SHARE_MEDIA, IShare>();

    public ShareManager(Activity mContext) {
        this.mContext = mContext;
        service = UMServiceFactory.getUMSocialService(COM_UMENG_SHARE);
    }

    public static ShareManager getInstance(Activity context) {
        ShareManager s = new ShareManager(context);
        s.init();
        return s;
    }

    private void init() {
        service.getConfig().setPlatforms(SHARE_MEDIA.WEIXIN,
                SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE,
                SHARE_MEDIA.SINA, SHARE_MEDIA.TENCENT);
        SHARE_MEDIA[] medias = new SHARE_MEDIA[]{SHARE_MEDIA.WEIXIN,
                SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE,
                SHARE_MEDIA.SINA, SHARE_MEDIA.TENCENT};
        for (SHARE_MEDIA media : medias) {
            IShare share = createShare(media);
            if (share != null) {
                share.init();
                shareMap.put(media, share);
            }
        }
    }

    private IShare createShare(SHARE_MEDIA m) {
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

    public void setUmImager(UMImage umImager) {
        for (IShare s : shareMap.values()) {
            s.setUMImager(umImager);
        }
    }

    public void setShareContent(String title, String content, String url) {
        for (IShare s : shareMap.values()) {
            s.setShareContent(title, content, url);
        }
    }

    public void show(boolean show) {
        service.openShare(mContext, show);
    }
}
