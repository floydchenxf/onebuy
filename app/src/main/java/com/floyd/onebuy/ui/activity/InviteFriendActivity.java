package com.floyd.onebuy.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.floyd.onebuy.biz.manager.LoginManager;
import com.floyd.onebuy.biz.vo.json.UserVO;
import com.floyd.onebuy.ui.ImageLoaderFactory;
import com.floyd.onebuy.ui.R;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.media.QQShareContent;
import com.umeng.socialize.media.QZoneShareContent;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.QZoneSsoHandler;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;
import com.umeng.socialize.weixin.media.CircleShareContent;
import com.umeng.socialize.weixin.media.WeiXinShareContent;

public class InviteFriendActivity extends Activity implements View.OnClickListener {

    private TextView descView;

    private NetworkImageView weiImageView;//二维码图片

    private TextView inviteCodeView;//邀请码
    private TextView inviteFriendView;//立即邀请按钮

    private ImageLoader mImageLoader;

    private UMSocialService mShare;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_friend);
        findViewById(R.id.title_back).setOnClickListener(this);
        TextView titleNameView = (TextView) findViewById(R.id.title_name);
        titleNameView.setText("邀请好友");
        titleNameView.setVisibility(View.VISIBLE);
        mShare = UMServiceFactory.getUMSocialService("com.umeng.share");
        setShareContent();
        configPlatform();

        mImageLoader = ImageLoaderFactory.createImageLoader();
        descView = (TextView) findViewById(R.id.desc);
        inviteCodeView = (TextView) findViewById(R.id.invite_code_view);
        inviteFriendView = (TextView) findViewById(R.id.invite_friend_button);
        inviteFriendView.setOnClickListener(this);
        weiImageView = (NetworkImageView) findViewById(R.id.mark_view);

        descView.setText(Html.fromHtml("成功邀请一位好友,可获取<font color=\"red\" size=\"16pt\">100</font>积分奖励"));
        UserVO vo = LoginManager.getLoginInfo(this);
        inviteCodeView.setText(Html.fromHtml("您的邀请码 <font color=\"red\">" + vo.InviterCode + "</font>"));

        weiImageView.setImageUrl("http://qr.api.cli.im/qr?data=test%25E7%25BB%2586%25E5%2593%25A6%25E4%25BB%2596%25E8%2588%2592%25E6%259C%258D&level=H&transparent=false&bgcolor=%23ffffff&forecolor=%23000000&blockpixel=12&marginblock=1&logourl=&size=280&kid=cliim&key=fccabeb5a410c11736f1249d7a004fc0", mImageLoader);
    }

    protected void onResume() {
        super.onResume();
    }

    private void configPlatform() {

        // 添加微信平台
        addWXPlatform();

        // 添加QQ平台
        addQQZonePlatform();

        // 设置分享面板上的分享平台
        mShare.getConfig().setPlatforms(SHARE_MEDIA.WEIXIN,
                SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE,
                SHARE_MEDIA.SINA, SHARE_MEDIA.TENCENT
        );
    }

    private void addQQZonePlatform() {
        String appId = "1104979541";
        String appKey = "uMcMgTs7XX85f4eO";
        // 添加QQ支持, 并且设置QQ分享内容的target url
        UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(this,
                appId, appKey);
        qqSsoHandler.addToSocialSDK();

        // 添加QZone平台
        QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(
                this, appId, appKey);
        qZoneSsoHandler.addToSocialSDK();

        QQShareContent qqShareContent = new QQShareContent();
        //设置分享文字
        qqShareContent.setShareContent("基于移动互联网，构建颜值经济平台，聚集高颜值美女，帅哥，儿童，让颜值成为生产力，让颜值更有价值！圆你一个模特梦，让人人都有机会成为网络红人的平台！\n" +
                "在全民模特APP只要你有颜值！而颜值越用越亮！真正做到轻松赚钱，还能成为网红！");
        //设置分享title
        qqShareContent.setTitle("来自“全民模特”的分享");
        //设置分享图片
        qqShareContent.setShareImage(new UMImage(this, R.drawable.icon));
        //设置点击分享内容的跳转链接
        qqShareContent.setTargetUrl("https://mp.weixin.qq.com/s?__biz=MzA3MzUxMjE0Nw==&mid=402986533&idx=1&sn=d503481e7048afe058d7b7b19613919d&scene=0&previewkey=Y0AJm9zrE7wRVUc950Fuc8NS9bJajjJKzz%2F0By7ITJA%3D&uin=NTgzMTExODgw&key=710a5d99946419d9b3463e089f1d876636ceb5d18633bbe0cb595068d607d845498fc2369c6cb0fb9af4c15c0132292e&devicetype=iMac14%2C2+OSX+OSX+10.11.1+build%2815B42%29&version=11000003&lang=zh_CN&pass_ticket=fsq9NnAUofrE%2FMjugdWnmN1G2g9xOx1w2bLs%2BwX9n2wOSxs8FzTcB4eb5CHVLpyy");
        mShare.setShareMedia(qqShareContent);

        QZoneShareContent qzone = new QZoneShareContent();
        //设置分享文字
        qzone.setShareContent("基于移动互联网，构建颜值经济平台，聚集高颜值美女，帅哥，儿童，让颜值成为生产力，让颜值更有价值！圆你一个模特梦，让人人都有机会成为网络红人的平台！\n" +
                "在全民模特APP只要你有颜值！而颜值越用越亮！真正做到轻松赚钱，还能成为网红！");
        //设置点击消息的跳转URL
        qzone.setTargetUrl("https://mp.weixin.qq.com/s?__biz=MzA3MzUxMjE0Nw==&mid=402986533&idx=1&sn=d503481e7048afe058d7b7b19613919d&scene=0&previewkey=Y0AJm9zrE7wRVUc950Fuc8NS9bJajjJKzz%2F0By7ITJA%3D&uin=NTgzMTExODgw&key=710a5d99946419d9b3463e089f1d876636ceb5d18633bbe0cb595068d607d845498fc2369c6cb0fb9af4c15c0132292e&devicetype=iMac14%2C2+OSX+OSX+10.11.1+build%2815B42%29&version=11000003&lang=zh_CN&pass_ticket=fsq9NnAUofrE%2FMjugdWnmN1G2g9xOx1w2bLs%2BwX9n2wOSxs8FzTcB4eb5CHVLpyy");
        qzone.setTitle("来自“全民模特”的分享");
        //设置分享图片
        qzone.setShareImage(new UMImage(this, R.drawable.icon));
        mShare.setShareMedia(qzone);
    }

    //添加微信分享平台
    private void addWXPlatform() {
        // 注意：在微信授权的时候，必须传递appSecret
        // wx967daebe835fbeac是你在微信开发平台注册应用的AppID, 这里需要替换成你注册的AppID

        String appId = "wx6f4a5ebb3d2cd11e";
        String appSecret = "64710023bac7d1b314c1b3ed3db5949d";

        UMWXHandler wxHandler = new UMWXHandler(this, appId, appSecret);
        wxHandler.addToSocialSDK();

        //设置微信好友分享内容
        WeiXinShareContent weixinContent = new WeiXinShareContent();
        //设置分享文字
        weixinContent.setShareContent("基于移动互联网，构建颜值经济平台，聚集高颜值美女，帅哥，儿童，让颜值成为生产力，让颜值更有价值！圆你一个模特梦，让人人都有机会成为网络红人的平台！\n" +
                "在全民模特APP只要你有颜值！而颜值越用越亮！真正做到轻松赚钱，还能成为网红！");
        //设置title
        weixinContent.setTitle("来自“全民模特”的分享");
        //设置分享内容跳转URL
        weixinContent.setTargetUrl("https://mp.weixin.qq.com/s?__biz=MzA3MzUxMjE0Nw==&mid=402986533&idx=1&sn=d503481e7048afe058d7b7b19613919d&scene=0&previewkey=Y0AJm9zrE7wRVUc950Fuc8NS9bJajjJKzz%2F0By7ITJA%3D&uin=NTgzMTExODgw&key=710a5d99946419d9b3463e089f1d876636ceb5d18633bbe0cb595068d607d845498fc2369c6cb0fb9af4c15c0132292e&devicetype=iMac14%2C2+OSX+OSX+10.11.1+build%2815B42%29&version=11000003&lang=zh_CN&pass_ticket=fsq9NnAUofrE%2FMjugdWnmN1G2g9xOx1w2bLs%2BwX9n2wOSxs8FzTcB4eb5CHVLpyy");
        //设置分享图片
        weixinContent.setShareImage(new UMImage(this, R.drawable.icon));
        mShare.setShareMedia(weixinContent);
        // 添加微信平台

        //设置微信朋友圈分享内容
        CircleShareContent circleMedia = new CircleShareContent();
        circleMedia.setShareContent("基于移动互联网，构建颜值经济平台，聚集高颜值美女，帅哥，儿童，让颜值成为生产力，让颜值更有价值！圆你一个模特梦，让人人都有机会成为网络红人的平台！\n" +
                "在全民模特APP只要你有颜值！而颜值越用越亮！真正做到轻松赚钱，还能成为网红！");
        //设置朋友圈title
        circleMedia.setTitle("来自“全民模特”的分享");
        circleMedia.setShareImage(new UMImage(this, R.drawable.icon));
        circleMedia.setTargetUrl("https://mp.weixin.qq.com/s?__biz=MzA3MzUxMjE0Nw==&mid=402986533&idx=1&sn=d503481e7048afe058d7b7b19613919d&scene=0&previewkey=Y0AJm9zrE7wRVUc950Fuc8NS9bJajjJKzz%2F0By7ITJA%3D&uin=NTgzMTExODgw&key=710a5d99946419d9b3463e089f1d876636ceb5d18633bbe0cb595068d607d845498fc2369c6cb0fb9af4c15c0132292e&devicetype=iMac14%2C2+OSX+OSX+10.11.1+build%2815B42%29&version=11000003&lang=zh_CN&pass_ticket=fsq9NnAUofrE%2FMjugdWnmN1G2g9xOx1w2bLs%2BwX9n2wOSxs8FzTcB4eb5CHVLpyy");
        mShare.setShareMedia(circleMedia);

        // 支持微信朋友圈
        UMWXHandler wxCircleHandler = new UMWXHandler(this, appId, appSecret);
        wxCircleHandler.setToCircle(true);
        wxCircleHandler.addToSocialSDK();
    }

    // 设置分享内容的方法
    private void setShareContent() {
        // 分享字符串
        mShare.setShareContent("基于移动互联网，构建颜值经济平台，聚集高颜值美女，帅哥，儿童，让颜值成为生产力，让颜值更有价值！圆你一个模特梦，让人人都有机会成为网络红人的平台！\n" +
                "在全民模特APP只要你有颜值！而颜值越用越亮！真正做到轻松赚钱，还能成为网红！");
        // 设置分享图片, 参数2为图片的url地址
        mShare.setShareMedia(new UMImage(this,
                R.drawable.icon));

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_back:
                this.finish();
                break;
            case R.id.invite_friend_button:
                //分享
                mShare.openShare(this, false);
                break;
        }

    }
}
