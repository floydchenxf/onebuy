package com.yyg365.interestbar.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.yyg365.interestbar.aync.ApiCallback;
import com.yyg365.interestbar.biz.manager.JiFenManager;
import com.yyg365.interestbar.biz.manager.LoginManager;
import com.yyg365.interestbar.biz.vo.json.ClientAddr;
import com.yyg365.interestbar.biz.vo.json.JFGoodsDetailVO;
import com.yyg365.interestbar.biz.vo.json.JFOrderDtailInfoVO;
import com.yyg365.interestbar.biz.vo.json.JFOrderInfoVO;
import com.yyg365.interestbar.ui.ImageLoaderFactory;
import com.yyg365.interestbar.ui.R;
import com.yyg365.interestbar.ui.loading.DataLoadingView;
import com.yyg365.interestbar.ui.loading.DefaultDataLoadingView;

public class MyJFGoodsDetailActivity extends Activity implements View.OnClickListener {

    public static final String GOODS_ID = "GOODS_ID";
    private Long id;
    private Long userId;


    private boolean isFirst;
    private ImageLoader mImageLoader;
    private DataLoadingView dataLoadingView;

    private View addressLayout;
    private View defaultAddressLayout;
    private TextView nameView;
    private TextView phoneView;
    private TextView addressView;
    private TextView emptyAddressView;

    private View phoneLayout;
    private TextView onlyPhoneView;

    private TextView productTitleView;
    private NetworkImageView productPicView;
    private TextView jfView;
    private TextView payJfView;

    private TextView orderStatusView;
    private TextView expressInfoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_jfgoods_detail);
        id = getIntent().getLongExtra(GOODS_ID, 0l);
        userId = LoginManager.getLoginInfo(this).ID;
        isFirst = true;

        findViewById(R.id.title_back).setOnClickListener(this);
        TextView titleNameView = (TextView) findViewById(R.id.title_name);
        titleNameView.setText("积分商品订单详情");
        titleNameView.setVisibility(View.VISIBLE);

        mImageLoader = ImageLoaderFactory.createImageLoader();
        dataLoadingView = new DefaultDataLoadingView();
        dataLoadingView.initView(findViewById(R.id.act_lsloading), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isFirst = true;
                loadData();
            }
        });

        productPicView = (NetworkImageView) findViewById(R.id.product_pic);
        productPicView.setDefaultImageResId(R.drawable.tupian);
        productTitleView = (TextView) findViewById(R.id.product_tilte_view);
        jfView = (TextView) findViewById(R.id.jf_view);
        payJfView = (TextView) findViewById(R.id.pay_jf_view);
        orderStatusView = (TextView) findViewById(R.id.order_goods_status_view);

        addressLayout = findViewById(R.id.address_layout);
        defaultAddressLayout = addressLayout.findViewById(R.id.default_address_layout);
        defaultAddressLayout.setOnClickListener(this);
        nameView = (TextView) addressLayout.findViewById(R.id.name_view);
        phoneView = (TextView) addressLayout.findViewById(R.id.phone_view);
        addressView = (TextView) addressLayout.findViewById(R.id.address_view);
        emptyAddressView = (TextView) addressLayout.findViewById(R.id.empty_address_view);

        phoneLayout = findViewById(R.id.phone_layout);
        onlyPhoneView = (TextView) findViewById(R.id.only_phone_view);
        expressInfoView = (TextView) findViewById(R.id.express_info_view);

        loadData();
    }

    private void loadData() {
        if (isFirst) {
            dataLoadingView.startLoading();
        }

        JiFenManager.fetchMyJFOrderDetailInfo(userId, id).startUI(new ApiCallback<JFOrderDtailInfoVO>() {
            @Override
            public void onError(int code, String errorInfo) {
                if (isFirst) {
                    dataLoadingView.loadFail();
                }
            }

            @Override
            public void onSuccess(JFOrderDtailInfoVO jfvo) {
                if (isFirst) {
                    dataLoadingView.loadSuccess();
                }

                JFGoodsDetailVO detailVO = jfvo.JFProInfo;
                productTitleView.setText(detailVO.Name);
                productPicView.setImageUrl(detailVO.getFirstPicUrl(), mImageLoader);
                if (detailVO.isNormalStatus()) {
                    if (detailVO.isDown()) {
                        orderStatusView.setVisibility(View.VISIBLE);
                        orderStatusView.setText("已下架");
                    } else {
                        orderStatusView.setVisibility(View.GONE);
                    }

                } else {
                    orderStatusView.setVisibility(View.VISIBLE);
                    orderStatusView.setText("已删除");
                }

                JFOrderInfoVO orderVO = jfvo.JFOrderInfo;
                Spanned originJF = Html.fromHtml("所需积分:&nbsp;<font color=\"red\">" + orderVO.OriginalJiFen + "</font>");
                jfView.setText(originJF);

                Spanned payJF = Html.fromHtml("实际兑换:&nbsp;<font color=\"red\">" + orderVO.PayJiFen + "</font>");
                payJfView.setText(payJF);

                int proType = detailVO.ProType;
                if (proType == 1) {
                    phoneLayout.setVisibility(View.GONE);
                    addressLayout.setVisibility(View.VISIBLE);

                    ClientAddr addressVO = jfvo.ClientAddr;
                    if (addressVO == null || addressVO.ID == 0) {
                        emptyAddressView.setVisibility(View.VISIBLE);
                        defaultAddressLayout.setVisibility(View.GONE);
                    } else {
                        emptyAddressView.setVisibility(View.GONE);
                        defaultAddressLayout.setVisibility(View.VISIBLE);
                        nameView.setText(addressVO.Name);
                        addressView.setText(addressVO.Address);
                        phoneView.setText(addressVO.Phone);
                    }
                } else if (proType == 2) {
                    phoneLayout.setVisibility(View.VISIBLE);
                    addressLayout.setVisibility(View.GONE);
                    Spanned mobleSp = Html.fromHtml("充值手机号:&nbsp;<font color=\"blue\">" + orderVO.Mobile + "</font>");
                    onlyPhoneView.setText(mobleSp);
                }

                Long expressCompanyID = orderVO.ExpressCompanyID;
                Spanned expressInfoSp = null;
                if (expressCompanyID != null && expressCompanyID != 0l) {
                    expressInfoSp = Html.fromHtml("您的商品将由&nbsp;<font color=\"blue\">" + orderVO.ExpressCompany + "</font>&nbsp;送到您的手中（单号：<font color=\"red\">" + orderVO.ExpressOrder + "</font>）,敬请期待！");
                } else {
                    expressInfoSp = Html.fromHtml("<font color=\"red\">尚未发货</font>");
                }
                expressInfoView.setText(expressInfoSp);


            }

            @Override
            public void onProgress(int progress) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_back:
                this.finish();
                break;
            case R.id.default_address_layout:
                Intent addressIntent = new Intent(this, AddressManagerActivity.class);
                startActivity(addressIntent);
                break;
        }

    }
}
