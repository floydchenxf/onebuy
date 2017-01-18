package com.yyg365.interestbar.ui.activity;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.yyg365.interestbar.aync.ApiCallback;
import com.yyg365.interestbar.biz.manager.LoginManager;
import com.yyg365.interestbar.biz.manager.OrderManager;
import com.yyg365.interestbar.biz.manager.PawnManager;
import com.yyg365.interestbar.biz.vo.json.AlipayOrderVO;
import com.yyg365.interestbar.biz.vo.json.PawnRedeemVO;
import com.yyg365.interestbar.biz.vo.json.RedeemInfoVO;
import com.yyg365.interestbar.biz.vo.json.PayChannelVO;
import com.yyg365.interestbar.biz.vo.json.RedeemOrderVO;
import com.yyg365.interestbar.biz.vo.pay.PayResult;
import com.yyg365.interestbar.ui.DialogCreator;
import com.yyg365.interestbar.ui.ImageLoaderFactory;
import com.yyg365.interestbar.ui.R;
import com.yyg365.interestbar.ui.adapter.PayChannelAdapter;
import com.yyg365.interestbar.ui.loading.DefaultDataLoadingView;
import com.yyg365.interestbar.ui.share.ShareConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * 创建典当的信息
 */
public class RedeemLogActivity extends Activity implements View.OnClickListener {

    public static final String PAWN_ID = "PAWN_ID";

    private NetworkImageView productPicView;
    private TextView productTitleView;
    private TextView redeemDaysView;
    private TextView pawnPriceView;
    private TextView redeemDaysTypeView;
    private TextView pawnTimeView;
    private ListView redeemPayListView;
    private PayChannelAdapter mAdapter;
    private ImageLoader mImageLoader;
    private DefaultDataLoadingView dataLoadingView;
    private Dialog dataLoadingDialog;

    private TextView pawnBasePriceView;
    private TextView redeemRatioPriceView;

    private TextView redeemPriceTipView;//价格提示
    private TextView redeemButton;

    private boolean isFirst = true;

    private Long pawnId;
    private Long userId;

    private Double pawnMsPrice = 0d;

    private IWXAPI iwxapi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_redeem_log);

        TextView titleNameView = (TextView) findViewById(R.id.title_name);
        titleNameView.setText("赎回");
        titleNameView.setVisibility(View.VISIBLE);

        findViewById(R.id.title_back).setOnClickListener(this);

        pawnId = getIntent().getLongExtra(PAWN_ID, 0l);
        userId = LoginManager.getLoginInfo(this).ID;

        iwxapi = WXAPIFactory.createWXAPI(this, ShareConstants.WX_APP_ID);
        iwxapi.registerApp(ShareConstants.WX_APP_ID);

        mImageLoader = ImageLoaderFactory.createImageLoader();

        dataLoadingView = new DefaultDataLoadingView();
        dataLoadingView.initView(findViewById(R.id.act_lsloading), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isFirst = true;
                loadData();
            }
        });

        dataLoadingDialog = DialogCreator.createDataLoadingDialog(this);

        productPicView = (NetworkImageView) findViewById(R.id.product_pic);
        productTitleView = (TextView) findViewById(R.id.pawn_product_tilte_view);
        redeemDaysView = (TextView) findViewById(R.id.redeem_days_view);
        pawnPriceView = (TextView) findViewById(R.id.pawn_price_view);
        redeemDaysTypeView = (TextView) findViewById(R.id.pawn_redeem_days_type_view);
        pawnTimeView = (TextView) findViewById(R.id.pawn_time_view);
        pawnBasePriceView = (TextView) findViewById(R.id.pawn_base_price_view);
        redeemRatioPriceView = (TextView) findViewById(R.id.redeem_ratio_price_view);

        redeemPayListView = (ListView) findViewById(R.id.redeem_pay_listview);
        redeemButton = (TextView) findViewById(R.id.redeem_button);
        redeemButton.setOnClickListener(this);
        redeemPriceTipView = (TextView) findViewById(R.id.redeem_price_tip_view);
        mAdapter = new PayChannelAdapter(this, new ArrayList<PayChannelVO>(), mImageLoader);
        redeemPayListView.setAdapter(mAdapter);

        loadData();
    }

    private void loadData() {
        if (isFirst) {
            dataLoadingView.startLoading();
        }
        PawnManager.fetchPawnRedeemInfoVO(pawnId, userId).startUI(new ApiCallback<RedeemInfoVO>() {
            @Override
            public void onError(int code, String errorInfo) {
                if (isFirst) {
                    dataLoadingView.loadFail();
                }
            }

            @Override
            public void onSuccess(RedeemInfoVO redeemInfoVO) {
                if (isFirst) {
                    dataLoadingView.loadSuccess();
                }

                if (redeemInfoVO == null) {
                    return;
                }

                List<PayChannelVO> payChannels = redeemInfoVO.PayChannel;
                mAdapter.addAll(payChannels, isFirst);

                Integer status = redeemInfoVO.PayStatus;
                if (status != null && status < payChannels.size()) {
                    Long payId = payChannels.get(status).ID;
                    mAdapter.setDefaultChecked(payId);
                }

                PawnRedeemVO info = redeemInfoVO.PawnLogInfo;
                productTitleView.setText(info.ProTitle);
                productPicView.setDefaultImageResId(R.drawable.tupian);
                productPicView.setImageUrl(info.getProductImage(), mImageLoader);

                Spanned redeemDays = Html.fromHtml("该商品赎回期剩余&nbsp;<font color=\"red\">" + info.RedeemDays + "</font>&nbsp;天!");
                redeemDaysView.setText(redeemDays);

                Spanned pawnPrice = Html.fromHtml("典當价格:&nbsp;<font color=\"red\">" + info.PawnPrice + "</font>&nbsp;元");
                pawnPriceView.setText(pawnPrice);

                Spanned daysType = Html.fromHtml("典當当期:&nbsp;" + info.PawnLevelName);
                redeemDaysTypeView.setText(daysType);

                Spanned pawnTimeSpanned = Html.fromHtml("典當时间:&nbsp;<font color=\"#00b4ff\">" + info.getPawnTime() + "</font>");
                pawnTimeView.setText(pawnTimeSpanned);

                Spanned pawnBasePriceSpanned = Html.fromHtml("典當基价:&nbsp;<font color=\"red\">" + info.RealRedeemPrice + "</font>&nbsp;元");
                pawnBasePriceView.setText(pawnBasePriceSpanned);

                Integer ratio = info.RedeemClientLevelRetio;

                Spanned ratioSpanned = Html.fromHtml("会员优惠价:&nbsp;<font color=\"red\">" + ((int) (info.RealRedeemPrice * ratio)) / 100 + "&nbsp;元");
                redeemRatioPriceView.setText(ratioSpanned);

                Spanned tipInfoSpanned = Html.fromHtml("赎回价格:&nbsp;<font color=\"red\">" + info.RealRedeemPrice + "&nbsp;元");
                redeemPriceTipView.setText(tipInfoSpanned);
            }

            @Override
            public void onProgress(int progress) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.redeem_button:
                dataLoadingDialog.show();
                final Long payChannelId = mAdapter.getCheckedId();

                PawnManager.createRedeemLog(pawnId, userId, payChannelId).startUI(new ApiCallback<RedeemOrderVO>() {
                    @Override
                    public void onError(int code, String errorInfo) {
                        dataLoadingDialog.dismiss();
                        Toast.makeText(RedeemLogActivity.this, errorInfo, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess(RedeemOrderVO orderVO) {
                        dataLoadingDialog.dismiss();
                        if (payChannelId == 3) { //支付宝
                            AlipayOrderVO payVO = orderVO.orders;
                            OrderManager.payByAlipay(RedeemLogActivity.this, payVO).startUI(new ApiCallback<PayResult>() {
                                @Override
                                public void onError(int code, String errorInfo) {

                                }

                                @Override
                                public void onSuccess(PayResult payResult) {
                                    String resultStatus = payResult.getResultStatus();
                                    if (TextUtils.equals(resultStatus, "9000")) {
                                        if (!RedeemLogActivity.this.isFinishing()) {
                                            Toast.makeText(RedeemLogActivity.this, "赎回成功", Toast.LENGTH_SHORT).show();
                                            RedeemLogActivity.this.finish();
                                        }
                                    } else {
                                        if (TextUtils.equals(resultStatus, "8000")) {
                                            Toast.makeText(RedeemLogActivity.this, "支付结果确认中", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(RedeemLogActivity.this, "支付失败", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }

                                @Override
                                public void onProgress(int progress) {

                                }
                            });
                        } else if (payChannelId == 4) { //微信
                            OrderManager.pay(orderVO.tn, iwxapi);
                        } else {
                            Toast.makeText(RedeemLogActivity.this, "赎回成功!", Toast.LENGTH_SHORT).show();
                            RedeemLogActivity.this.finish();
                        }
                    }

                    @Override
                    public void onProgress(int progress) {

                    }
                });

                break;
            case R.id.title_back:
                this.finish();
                break;
        }
    }

    protected void onDestroy() {
        super.onDestroy();
        iwxapi.unregisterApp();
    }
}
