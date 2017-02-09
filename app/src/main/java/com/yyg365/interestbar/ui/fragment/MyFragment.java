package com.yyg365.interestbar.ui.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
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
import com.yyg365.interestbar.aync.ApiCallback;
import com.yyg365.interestbar.aync.Func;
import com.yyg365.interestbar.aync.JobFactory;
import com.yyg365.interestbar.biz.constants.EnvConstants;
import com.yyg365.interestbar.biz.manager.LoginManager;
import com.yyg365.interestbar.biz.tools.FileUtils;
import com.yyg365.interestbar.biz.tools.ImageUtils;
import com.yyg365.interestbar.biz.vo.NavigationVO;
import com.yyg365.interestbar.biz.vo.json.UserVO;
import com.yyg365.interestbar.ui.DialogCreator;
import com.yyg365.interestbar.ui.ImageLoaderFactory;
import com.yyg365.interestbar.ui.R;
import com.yyg365.interestbar.ui.activity.ChargeListActivity;
import com.yyg365.interestbar.ui.activity.FridayActivity;
import com.yyg365.interestbar.ui.activity.InviteFriendActivity;
import com.yyg365.interestbar.ui.activity.JiFengActivity;
import com.yyg365.interestbar.ui.activity.MsgBoxActivity;
import com.yyg365.interestbar.ui.activity.MyCommonwealActivity;
import com.yyg365.interestbar.ui.activity.MyInfoActivity;
import com.yyg365.interestbar.ui.activity.MyJFGoodsActivity;
import com.yyg365.interestbar.ui.activity.MyLuckActivity;
import com.yyg365.interestbar.ui.activity.MyPawnActivity;
import com.yyg365.interestbar.ui.activity.PayChargeActivity;
import com.yyg365.interestbar.ui.activity.SettingActivity;
import com.yyg365.interestbar.ui.activity.ShowShareActivity;
import com.yyg365.interestbar.ui.activity.SubjectInfoActivity;
import com.yyg365.interestbar.ui.activity.UserCommissionActivity;
import com.yyg365.interestbar.ui.activity.WinningRecordActivity;
import com.yyg365.interestbar.ui.adapter.MyPawnAdapter;
import com.yyg365.interestbar.ui.adapter.NavigationAdapter;
import com.yyg365.interestbar.ui.loading.DataLoadingView;
import com.yyg365.interestbar.ui.loading.DefaultDataLoadingView;
import com.yyg365.interestbar.view.UIAlertDialog;
import com.yyg365.zxing.MipcaActivityCapture;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MyFragment extends BackHandledFragment implements View.OnClickListener {

    private static final String TAG = "MyFragment";

    private static final int SCANNIN_GREQUEST_CODE = 100;

    private List<NavigationVO> lstImageItem = new ArrayList<NavigationVO>();

    private ImageLoader mImageLoader;

    private DataLoadingView dataLoadingView;

    private TextView userNameView;
    private TextView feeView;
    private TextView jiFengView;
    private TextView commissionView;
    private TextView clientLevelView;
    private TextView addFeeView;
    private ListView operateListView;
    private ImageView saomiaoView;
    private ImageView settingView;
    private ImageView msgBoxView;
    private ImageView unreadDotView;//红点

    private NetworkImageView headImageView;
    private NetworkImageView bgHeadView;

    private View shareLayout;

    private NavigationAdapter adapter;

    private String[] texts = new String[]{"夺宝记录", "我的金豆", "中奖记录", "充值记录", "我的當铺", "我的积分", "邀请好友", "积分商城", "我的晒单"};
    private int[] images = new int[]{R.drawable.prize_record, R.drawable.bean, R.drawable.winning, R.drawable.add_fee, R.drawable.gongyi, R.drawable.jifeng, R.drawable.invite, R.drawable.fri, R.drawable.shandan};
    private Class[] clazzs = new Class[]{WinningRecordActivity.class, UserCommissionActivity.class, MyLuckActivity.class, ChargeListActivity.class, MyPawnActivity.class, JiFengActivity.class, InviteFriendActivity.class, MyJFGoodsActivity.class, ShowShareActivity.class};

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
                    if (k ==1) {
                        it.putExtra(UserCommissionActivity.CURRENT_USER_ID, LoginManager.getLoginInfo(getActivity()).ID);
                    } else if (k == 4) {
                        it.putExtra("USER_ID", LoginManager.getLoginInfo(getActivity()).ID);
                    } else if (k == 6 || k == 8) {
                        it.putExtra(ShowShareActivity.CURRENT_USER_ID, LoginManager.getLoginInfo(getActivity()).ID);
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

        View headView = View.inflate(getActivity(), R.layout.my_head, null);

        userNameView = (TextView) headView.findViewById(R.id.user_name);
        feeView = (TextView) headView.findViewById(R.id.fee);
        commissionView = (TextView) headView.findViewById(R.id.commission);
        clientLevelView = (TextView) headView.findViewById(R.id.client_level_view);
        jiFengView = (TextView) headView.findViewById(R.id.jifeng);
        addFeeView = (TextView) headView.findViewById(R.id.add_fee);
        bgHeadView = (NetworkImageView) headView.findViewById(R.id.head_bg);
        addFeeView.setOnClickListener(this);
        headImageView = (NetworkImageView) headView.findViewById(R.id.head);
        headImageView.setDefaultImageResId(R.drawable.default_head);
        headImageView.setOnClickListener(this);

        operateListView = (ListView) view.findViewById(R.id.operate_listview);
        operateListView.addHeaderView(headView);
        adapter = new NavigationAdapter(getActivity(), lstImageItem);
        operateListView.setAdapter(adapter);

        saomiaoView = (ImageView) view.findViewById(R.id.saomiao_view);
        saomiaoView.setOnClickListener(this);

        settingView = (ImageView) view.findViewById(R.id.setting_view);
        settingView.setOnClickListener(this);

        msgBoxView = (ImageView) view.findViewById(R.id.msgbox_view);
        msgBoxView.setOnClickListener(this);

        unreadDotView = (ImageView) view.findViewById(R.id.unread_dot_view);

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

        if (vo.UnreadMessageNum > 0) {
            unreadDotView.setVisibility(View.VISIBLE);
        } else {
            unreadDotView.setVisibility(View.GONE);
        }

        userNameView.setText(vo.getUserName());
        feeView.setText("金币：" + vo.Amount);
        jiFengView.setText("积分：" + vo.JiFen);
        commissionView.setText("金豆: " + vo.Commission);
        clientLevelView.setText(vo.ClientLevelName);
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
            case R.id.add_fee:
                Intent it = new Intent(getActivity(), PayChargeActivity.class);
                startActivity(it);
                break;
            case R.id.head:
                Intent myInfoIntent = new Intent(getActivity(), MyInfoActivity.class);
                startActivity(myInfoIntent);
                break;
            case R.id.setting_view:
                Intent settingIntent = new Intent(this.getActivity(), SettingActivity.class);
                startActivity(settingIntent);
                break;
            case R.id.msgbox_view:
                Intent msgboxIntent = new Intent(getActivity(), MsgBoxActivity.class);
                msgboxIntent.putExtra(MsgBoxActivity.CURRENT_USER_ID, LoginManager.getLoginInfo(getActivity()).ID);
                startActivity(msgboxIntent);
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
                    String resultString = bundle.getString("result");
                    if (TextUtils.isEmpty(resultString) || !resultString.startsWith("suchengyungou_subject_")) {
                        Toast.makeText(getActivity(), "不是有效的商品", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String[] array = resultString.split("_");
                    String idStr = array[2];
                    if (!TextUtils.isDigitsOnly(idStr)) {
                        Toast.makeText(getActivity(), "不是有效的商品ID", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Long id = Long.parseLong(idStr);
                    Intent detailIntent = new Intent(getActivity(), SubjectInfoActivity.class);
                    detailIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    detailIntent.putExtra(SubjectInfoActivity.SUBJECT_ID, id);
                    startActivity(detailIntent);
                }
                break;
        }
    }
}
