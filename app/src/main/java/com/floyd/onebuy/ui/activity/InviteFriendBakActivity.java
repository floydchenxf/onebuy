package com.floyd.onebuy.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.floyd.onebuy.aync.ApiCallback;
import com.floyd.onebuy.biz.manager.LoginManager;
import com.floyd.onebuy.biz.vo.json.InviteVO;
import com.floyd.onebuy.biz.vo.json.UserVO;
import com.floyd.onebuy.ui.ImageLoaderFactory;
import com.floyd.onebuy.ui.R;
import com.floyd.onebuy.ui.loading.DataLoadingView;
import com.floyd.onebuy.ui.loading.DefaultDataLoadingView;
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

public class InviteFriendBakActivity extends Activity implements View.OnClickListener {

    private TextView descView;

    private NetworkImageView weiImageView;//二维码图片

    private TextView inviteCodeView;//邀请码
    private TextView inviteFriendView;//立即邀请按钮

    private DataLoadingView dataLoadingView;
    private ImageLoader mImageLoader;

    private ShareManager shareManager;
    private UserVO userVO;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_friend);
        dataLoadingView = new DefaultDataLoadingView();
        dataLoadingView.initView(findViewById(R.id.act_lsloading), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadData();
            }
        });

        shareManager = ShareManager.getInstance(this);
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

        loadData();
    }

    private void loadData() {
        dataLoadingView.startLoading();
        userVO = LoginManager.getLoginInfo(this);
        LoginManager.fetchInviteInfo(userVO.ID).startUI(new ApiCallback<InviteVO>() {
            @Override
            public void onError(int code, String errorInfo) {
                dataLoadingView.loadFail();
            }

            @Override
            public void onSuccess(InviteVO inviteVO) {
                dataLoadingView.loadSuccess();
                String title = "苏城云购";
                String content = "对发送发送到发送到发送地方";
                shareManager.setShareContent(title, content, inviteVO.url);
                weiImageView.setImageUrl(inviteVO.getQrcode(), mImageLoader);
                descView.setText(Html.fromHtml("成功邀请一位好友,可获取<font color=\"red\" size=\"16pt\">100</font>积分奖励"));
                inviteCodeView.setText(Html.fromHtml("您的邀请码 <font color=\"red\">" + userVO.InviterCode + "</font>"));
            }

            @Override
            public void onProgress(int progress) {

            }
        });
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
