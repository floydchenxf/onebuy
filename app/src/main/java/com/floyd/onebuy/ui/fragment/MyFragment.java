package com.floyd.onebuy.ui.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.BitmapProcessor;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.floyd.onebuy.biz.manager.LoginManager;
import com.floyd.onebuy.biz.tools.ImageUtils;
import com.floyd.onebuy.biz.vo.NavigationVO;
import com.floyd.onebuy.biz.vo.json.UserVO;
import com.floyd.onebuy.ui.DialogCreator;
import com.floyd.onebuy.ui.ImageLoaderFactory;
import com.floyd.onebuy.ui.R;
import com.floyd.onebuy.ui.activity.AddressManagerActivity;
import com.floyd.onebuy.ui.activity.FeeRecordActivity;
import com.floyd.onebuy.ui.activity.JiFengActivity;
import com.floyd.onebuy.ui.activity.MyInfoActivity;
import com.floyd.onebuy.ui.activity.SettingActivity;
import com.floyd.onebuy.ui.activity.WinningDetailActivity;
import com.floyd.onebuy.ui.activity.WinningRecordActivity;
import com.floyd.onebuy.ui.adapter.NavigationAdapter;
import com.floyd.onebuy.ui.loading.DataLoadingView;
import com.floyd.onebuy.ui.loading.DefaultDataLoadingView;
import com.floyd.zxing.MipcaActivityCapture;
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

import java.util.ArrayList;
import java.util.List;

public class MyFragment extends BackHandledFragment implements View.OnClickListener {

    private static final String TAG = "MyFragment";

    private static final int CODE_GALLERY_REQUEST = 80;
    private static final int CROP_PICTURE_REQUEST = 81;

    private static final int SCANNIN_GREQUEST_CODE = 100;

    private List<NavigationVO> lstImageItem = new ArrayList<NavigationVO>();


    private ProgressDialog avatorDialog;

    private ImageLoader mImageLoader;

    private DataLoadingView dataLoadingView;
    private Dialog dataLoadingDialog;

    private TextView userNameView;
    private TextView feeView;
    private TextView jiFengView;
    private TextView addFeeView;
    private ListView operateListView;
    private ImageView saomiaoView;
    private NetworkImageView headImageView;
    private NetworkImageView bgHeadView;

    private UMSocialService mShare;

    private View shareLayout;

    private NavigationAdapter adapter;

    private String[] texts = new String[]{"夺宝记录", "我的公益", "中奖记录", "快乐星期五", "充值记录", "我的晒单", "软件设置", "邀请好友", "我的积分"};
    private int[] images = new int[]{R.drawable.prize_record, R.drawable.gongyi, R.drawable.winning, R.drawable.fri, R.drawable.add_fee, R.drawable.shandan, R.drawable.setting, R.drawable.invite, R.drawable.jifeng};
    private Class[] clazzs = new Class[]{WinningRecordActivity.class, FeeRecordActivity.class, FeeRecordActivity.class, FeeRecordActivity.class, JiFengActivity.class, FeeRecordActivity.class, SettingActivity.class, FeeRecordActivity.class, JiFengActivity.class};

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        mImageLoader = ImageLoaderFactory.createImageLoader();

        mShare = UMServiceFactory.getUMSocialService("com.umeng.share");
        //设置分享内容
        setShareContent();
        configPlatform();

        for (int i = 0; i < texts.length; i++) {
            NavigationVO vo = new NavigationVO();
            vo.drawIcon = images[i];
            vo.navigationName = texts[i];
            final int k = i;
            View.OnClickListener onClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (k == 7) {
                        mShare.openShare(MyFragment.this.getActivity(), false);
                    } else {
                        Intent it = new Intent(MyFragment.this.getActivity(), clazzs[k]);
                        MyFragment.this.getActivity().startActivity(it);
                    }
                }
            };

            vo.onClickListener = onClickListener;
            lstImageItem.add(vo);
        }

    }

    // 用来配置各个平台的SDKF
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
        UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(MyFragment.this.getActivity(),
                appId, appKey);
        qqSsoHandler.addToSocialSDK();

        // 添加QZone平台
        QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(
                MyFragment.this.getActivity(), appId, appKey);
        qZoneSsoHandler.addToSocialSDK();

        QQShareContent qqShareContent = new QQShareContent();
        //设置分享文字
        qqShareContent.setShareContent("基于移动互联网，构建颜值经济平台，聚集高颜值美女，帅哥，儿童，让颜值成为生产力，让颜值更有价值！圆你一个模特梦，让人人都有机会成为网络红人的平台！\n" +
                "在全民模特APP只要你有颜值！而颜值越用越亮！真正做到轻松赚钱，还能成为网红！");
        //设置分享title
        qqShareContent.setTitle("来自“全民模特”的分享");
        //设置分享图片
        qqShareContent.setShareImage(new UMImage(MyFragment.this.getActivity(), R.drawable.icon));
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
        qzone.setShareImage(new UMImage(MyFragment.this.getActivity(), R.drawable.icon));
        mShare.setShareMedia(qzone);
    }

    //添加微信分享平台
    private void addWXPlatform() {
        // 注意：在微信授权的时候，必须传递appSecret
        // wx967daebe835fbeac是你在微信开发平台注册应用的AppID, 这里需要替换成你注册的AppID

        String appId = "wx6f4a5ebb3d2cd11e";
        String appSecret = "64710023bac7d1b314c1b3ed3db5949d";

        UMWXHandler wxHandler = new UMWXHandler(MyFragment.this.getActivity(), appId, appSecret);
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
        weixinContent.setShareImage(new UMImage(MyFragment.this.getActivity(), R.drawable.icon));
        mShare.setShareMedia(weixinContent);
        // 添加微信平台

        //设置微信朋友圈分享内容
        CircleShareContent circleMedia = new CircleShareContent();
        circleMedia.setShareContent("基于移动互联网，构建颜值经济平台，聚集高颜值美女，帅哥，儿童，让颜值成为生产力，让颜值更有价值！圆你一个模特梦，让人人都有机会成为网络红人的平台！\n" +
                "在全民模特APP只要你有颜值！而颜值越用越亮！真正做到轻松赚钱，还能成为网红！");
        //设置朋友圈title
        circleMedia.setTitle("来自“全民模特”的分享");
        circleMedia.setShareImage(new UMImage(MyFragment.this.getActivity(), R.drawable.icon));
        circleMedia.setTargetUrl("https://mp.weixin.qq.com/s?__biz=MzA3MzUxMjE0Nw==&mid=402986533&idx=1&sn=d503481e7048afe058d7b7b19613919d&scene=0&previewkey=Y0AJm9zrE7wRVUc950Fuc8NS9bJajjJKzz%2F0By7ITJA%3D&uin=NTgzMTExODgw&key=710a5d99946419d9b3463e089f1d876636ceb5d18633bbe0cb595068d607d845498fc2369c6cb0fb9af4c15c0132292e&devicetype=iMac14%2C2+OSX+OSX+10.11.1+build%2815B42%29&version=11000003&lang=zh_CN&pass_ticket=fsq9NnAUofrE%2FMjugdWnmN1G2g9xOx1w2bLs%2BwX9n2wOSxs8FzTcB4eb5CHVLpyy");
        mShare.setShareMedia(circleMedia);

        // 支持微信朋友圈
        UMWXHandler wxCircleHandler = new UMWXHandler(MyFragment.this.getActivity(), appId, appSecret);
        wxCircleHandler.setToCircle(true);
        wxCircleHandler.addToSocialSDK();
    }

    // 设置分享内容的方法
    private void setShareContent() {
        // 分享字符串
        mShare.setShareContent("基于移动互联网，构建颜值经济平台，聚集高颜值美女，帅哥，儿童，让颜值成为生产力，让颜值更有价值！圆你一个模特梦，让人人都有机会成为网络红人的平台！\n" +
                "在全民模特APP只要你有颜值！而颜值越用越亮！真正做到轻松赚钱，还能成为网红！");
        // 设置分享图片, 参数2为图片的url地址
        mShare.setShareMedia(new UMImage(MyFragment.this.getActivity(),
                R.drawable.icon));

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my, container, false);

        dataLoadingView = new DefaultDataLoadingView();
        dataLoadingView.initView(view, this);
        dataLoadingDialog = DialogCreator.createDataLoadingDialog(this.getActivity());

        View headView = View.inflate(getActivity(), R.layout.my_head, null);

        userNameView = (TextView) headView.findViewById(R.id.user_name);
        feeView = (TextView) headView.findViewById(R.id.fee);
        jiFengView = (TextView) headView.findViewById(R.id.jifeng);
        addFeeView = (TextView) headView.findViewById(R.id.add_fee);
        bgHeadView = (NetworkImageView) headView.findViewById(R.id.head_bg);
        addFeeView.setOnClickListener(this);
        headImageView = (NetworkImageView) headView.findViewById(R.id.head);
        headImageView.setDefaultImageResId(R.drawable.default_image);
        headImageView.setOnClickListener(this);

        operateListView = (ListView) view.findViewById(R.id.operate_listview);
        operateListView.addHeaderView(headView);
        adapter = new NavigationAdapter(getActivity(), lstImageItem);
        operateListView.setAdapter(adapter);

        saomiaoView = (ImageView) view.findViewById(R.id.saomiao_view);
        saomiaoView.setOnClickListener(this);

        shareLayout = view.findViewById(R.id.share);
        shareLayout.setOnClickListener(this);
        loadData(true, true);
        return view;
    }

    public void onResume() {
        super.onResume();
        loadData(false, false);
    }

    private void loadData(final boolean needDialog, final boolean isFirst) {
        UserVO vo = LoginManager.getLoginInfo(this.getActivity());
        headImageView.setImageUrl(vo.getFullPic(), mImageLoader, new BitmapProcessor() {
            @Override
            public Bitmap processBitmap(Bitmap bitmap) {
                return ImageUtils.getCircleBitmap(bitmap, getActivity().getResources().getDimension(R.dimen.cycle_head_image_size));
            }
        });

        userNameView.setText(vo.getUserName());
    }

    @Override
    public boolean onBackPressed() {
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.saomiao_view:
                Intent intent = new Intent(this.getActivity(), MipcaActivityCapture.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivityForResult(intent, SCANNIN_GREQUEST_CODE);
                break;
            case R.id.share:
                Intent settingIntent = new Intent(this.getActivity(), SettingActivity.class);
                settingIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(settingIntent);
                break;
            case R.id.add_fee:
                break;
            case R.id.head:
                Intent myInfoIntent = new Intent(getActivity(), MyInfoActivity.class);
                startActivity(myInfoIntent);
                break;
        }

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SCANNIN_GREQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    //显示扫描到的内容
                    String lssueIdStr = bundle.getString("result");
                    if (!TextUtils.isDigitsOnly(lssueIdStr)) {
                        Toast.makeText(getActivity(), "不是有效的商品类型", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Intent detailIntent = new Intent(getActivity(), WinningDetailActivity.class);
                    startActivity(detailIntent);
                }
                break;
        }
    }
}
