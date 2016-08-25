package com.floyd.onebuy.ui.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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
import com.floyd.onebuy.aync.ApiCallback;
import com.floyd.onebuy.biz.manager.ImagerInfoManager;
import com.floyd.onebuy.biz.manager.LoginManager;
import com.floyd.onebuy.biz.tools.ImageUtils;
import com.floyd.onebuy.biz.vo.NavigationVO;
import com.floyd.onebuy.biz.vo.json.UserVO;
import com.floyd.onebuy.ui.DialogCreator;
import com.floyd.onebuy.ui.ImageLoaderFactory;
import com.floyd.onebuy.ui.R;
import com.floyd.onebuy.ui.activity.ChargeListActivity;
import com.floyd.onebuy.ui.activity.CommonwealBakActivity;
import com.floyd.onebuy.ui.activity.InviteFriendActivity;
import com.floyd.onebuy.ui.activity.JiFengActivity;
import com.floyd.onebuy.ui.activity.MyInfoActivity;
import com.floyd.onebuy.ui.activity.MyLuckActivity;
import com.floyd.onebuy.ui.activity.PayChargeActivity;
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

import java.io.File;
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

    private View shareLayout;

    private NavigationAdapter adapter;

    private String[] texts = new String[]{"夺宝记录", "我的公益", "中奖记录", "快乐星期五", "充值记录", "我的晒单", "软件设置", "邀请好友", "我的积分"};
    private int[] images = new int[]{R.drawable.prize_record, R.drawable.gongyi, R.drawable.winning, R.drawable.fri, R.drawable.add_fee, R.drawable.shandan, R.drawable.setting, R.drawable.invite, R.drawable.jifeng};
    private Class[] clazzs = new Class[]{WinningRecordActivity.class, CommonwealBakActivity.class, MyLuckActivity.class, MyLuckActivity.class, ChargeListActivity.class, JiFengActivity.class, SettingActivity.class, InviteFriendActivity.class, JiFengActivity.class};

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        mImageLoader = ImageLoaderFactory.createImageLoader();

        for (int i = 0; i < texts.length; i++) {
            NavigationVO vo = new NavigationVO();
            vo.drawIcon = images[i];
            vo.navigationName = texts[i];
            final int k = i;
            View.OnClickListener onClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent it = new Intent(MyFragment.this.getActivity(), clazzs[k]);
                    if (k == 1) {
                        it.putExtra("USER_ID", LoginManager.getLoginInfo(getActivity()).ID);
                    }
                    MyFragment.this.getActivity().startActivity(it);
                }
            };

            vo.onClickListener = onClickListener;
            lstImageItem.add(vo);
        }

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

        if (isFirst) {
            dataLoadingView.startLoading();
        }

        final UserVO vo = LoginManager.getLoginInfo(this.getActivity());
        LoginManager.fetchUserInfo(vo.ID).startUI(new ApiCallback<UserVO>() {
            @Override
            public void onError(int code, String errorInfo) {
                if (isFirst) {
                    dataLoadingView.loadSuccess();
                }
                fillView(vo);
            }

            @Override
            public void onSuccess(UserVO userVO) {
                if (isFirst) {
                    dataLoadingView.loadSuccess();
                }

                fillView(userVO);
            }

            @Override
            public void onProgress(int progress) {

            }
        });

    }

    private void fillView(UserVO vo) {
        headImageView.setImageUrl(vo.getFullPic(), mImageLoader, new BitmapProcessor() {
            @Override
            public Bitmap processBitmap(Bitmap bitmap) {
                return ImageUtils.getCircleBitmap(bitmap, getActivity().getResources().getDimension(R.dimen.cycle_head_image_size));
            }
        });

        userNameView.setText(vo.getUserName());
        feeView.setText("金额：" + vo.Amount);
        jiFengView.setText("积分：" + vo.JiFen);
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
                Intent it = new Intent(getActivity(), PayChargeActivity.class);
                startActivity(it);
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
                    detailIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    detailIntent.putExtra(WinningDetailActivity.LSSUE_ID, lssueIdStr);
                    startActivity(detailIntent);
                }
                break;
        }
    }
}
