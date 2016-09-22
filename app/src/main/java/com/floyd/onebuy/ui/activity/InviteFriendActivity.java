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
import com.floyd.onebuy.ui.share.IShare;
import com.floyd.onebuy.ui.share.ShareManager;
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

    private ShareManager shareManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_friend);
        findViewById(R.id.title_back).setOnClickListener(this);
        TextView titleNameView = (TextView) findViewById(R.id.title_name);
        titleNameView.setText("邀请好友");
        titleNameView.setVisibility(View.VISIBLE);

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

        initShare();
    }

    private void initShare() {
        shareManager = ShareManager.getInstance(this);
        shareManager.init();
        String title = "苏城云购";
        String content = "对发送发送到发送到发送地方";
        String url = "https://mp.weixin.qq.com/s?__biz=MzA3MzUxMjE0Nw==&mid=402986533&idx=1&sn=d503481e7048afe058d7b7b19613919d&scene=0&previewkey=Y0AJm9zrE7wRVUc950Fuc8NS9bJajjJKzz%2F0By7ITJA%3D&uin=NTgzMTExODgw&key=710a5d99946419d9b3463e089f1d876636ceb5d18633bbe0cb595068d607d845498fc2369c6cb0fb9af4c15c0132292e&devicetype=iMac14%2C2+OSX+OSX+10.11.1+build%2815B42%29&version=11000003&lang=zh_CN&pass_ticket=fsq9NnAUofrE%2FMjugdWnmN1G2g9xOx1w2bLs%2BwX9n2wOSxs8FzTcB4eb5CHVLpyy";
        shareManager.setShareContent(title, content, url);
    }

    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_back:
                this.finish();
                break;
            case R.id.invite_friend_button:
                //分享
                shareManager.show(false);
                break;
        }

    }
}
