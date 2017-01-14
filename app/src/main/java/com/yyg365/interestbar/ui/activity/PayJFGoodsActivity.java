package com.yyg365.interestbar.ui.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.yyg365.interestbar.aync.ApiCallback;
import com.yyg365.interestbar.biz.manager.JiFenManager;
import com.yyg365.interestbar.biz.manager.LoginManager;
import com.yyg365.interestbar.biz.vo.json.JFGoodsDetailVO;
import com.yyg365.interestbar.biz.vo.json.JFGoodsPayVO;
import com.yyg365.interestbar.biz.vo.json.PayChannelVO;
import com.yyg365.interestbar.event.AddressModifiedEvent;
import com.yyg365.interestbar.ui.DialogCreator;
import com.yyg365.interestbar.ui.ImageLoaderFactory;
import com.yyg365.interestbar.ui.R;
import com.yyg365.interestbar.ui.adapter.PayChannelAdapter;
import com.yyg365.interestbar.ui.loading.DefaultDataLoadingView;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;

public class PayJFGoodsActivity extends Activity implements View.OnClickListener {

    public static final String JF_PRODUCT_ID = "JF_PRODUCT_ID";

    private LinearLayout defaultAddressLayout;
    private TextView nameView;
    private TextView phoneView;
    private TextView addressView;
    private TextView emptyAddressView;

    private NetworkImageView productPicView;
    private TextView productTitleView;

    private TextView jfPriceView;
    private TextView jfDiscountView;
    private TextView jfTipView;//价格提示
    private TextView duihuanButton;

    private ListView payChannelListView;
    private PayChannelAdapter mAdapter;

    private ImageLoader mImageLoader;
    private DefaultDataLoadingView dataLoadingView;
    private Dialog dataLoadingDialog;

    private boolean isFirst = true;

    private Long proId;
    private Long userId;

    private Long addressId = 0l;
    private String phone = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_jfgoods);

        TextView titleNameView = (TextView) findViewById(R.id.title_name);
        titleNameView.setText("兑换商品");
        titleNameView.setVisibility(View.VISIBLE);

        findViewById(R.id.title_back).setOnClickListener(this);

        proId = getIntent().getLongExtra(JF_PRODUCT_ID, 0l);
        userId = LoginManager.getLoginInfo(this).ID;

        EventBus.getDefault().register(this);

        View addressHeadview = findViewById(R.id.address_layout);
        addressHeadview.setOnClickListener(this);
        defaultAddressLayout = (LinearLayout) addressHeadview.findViewById(R.id.default_address_layout);
        nameView = (TextView) addressHeadview.findViewById(R.id.name_view);
        phoneView = (TextView) addressHeadview.findViewById(R.id.phone_view);
        addressView = (TextView) addressHeadview.findViewById(R.id.address_view);
        emptyAddressView = (TextView) addressHeadview.findViewById(R.id.empty_address_view);

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
        productPicView.setDefaultImageResId(R.drawable.tupian);
        productTitleView = (TextView) findViewById(R.id.product_tilte_view);


        jfPriceView = (TextView) findViewById(R.id.jf_score_view);
        jfDiscountView = (TextView) findViewById(R.id.jf_discount_view);

        payChannelListView = (ListView) findViewById(R.id.pay_listview);
        duihuanButton = (TextView) findViewById(R.id.duihuan_button);
        duihuanButton.setOnClickListener(this);
        jfTipView = (TextView) findViewById(R.id.jf_tip_view);
        mAdapter = new PayChannelAdapter(this, new ArrayList<PayChannelVO>(), mImageLoader);
        payChannelListView.setAdapter(mAdapter);

        loadData();
    }

    private void loadData() {
        dataLoadingView.startLoading();
        JiFenManager.payJiFengGoods(userId, proId).startUI(new ApiCallback<JFGoodsPayVO>() {
            @Override
            public void onError(int code, String errorInfo) {
                if (isFirst) {
                    dataLoadingView.loadFail();
                }
            }

            @Override
            public void onSuccess(JFGoodsPayVO jfvo) {
                if (isFirst) {
                    dataLoadingView.loadSuccess();
                }

                JFGoodsDetailVO detailVO = jfvo.JFProInfo;
                productPicView.setImageUrl(detailVO.getFirstPicUrl(), mImageLoader);
                productTitleView.setText(detailVO.Name);

                List<PayChannelVO> payChannels = jfvo.PayChannel;
                if (payChannels != null && !payChannels.isEmpty()) {
                    mAdapter.addAll(payChannels, true);
                    Long payId = payChannels.get(0).ID;
                    mAdapter.setDefaultChecked(payId);
                }

                JFGoodsPayVO.UserInfo userInfo = jfvo.UserInfo;

                Spanned jfScore = Html.fromHtml("兑换积分:&nbsp;<font color=\"red\">" + jfvo.PayJiFen + "</font>");
                jfPriceView.setText(jfScore);

                Spanned discount = Html.fromHtml("优惠积分:&nbsp;<font color=\"red\">" + ((int) (jfvo.PayJiFen * userInfo.ClientLevelRatio / 100)) + "</font>");
                jfDiscountView.setText(discount);
                jfTipView.setText(jfScore);

                JFGoodsPayVO.ClientAddr addressVO = jfvo.ClientAddr;
                if (addressVO == null || addressVO.ID == 0) {
                    emptyAddressView.setVisibility(View.VISIBLE);
                    defaultAddressLayout.setVisibility(View.GONE);
                } else {
                    addressId = addressVO.ID;
                    phone = addressVO.Phone;
                    emptyAddressView.setVisibility(View.GONE);
                    defaultAddressLayout.setVisibility(View.VISIBLE);
                    nameView.setText(addressVO.Name);
                    addressView.setText(addressVO.Address);
                    phoneView.setText(addressVO.Phone);
                }
            }

            @Override
            public void onProgress(int progress) {

            }
        });
    }

    @Subscribe
    public void onEventMainThread(AddressModifiedEvent event) {
        if (!this.isFinishing()) {
            isFirst = false;
            loadData();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_back:
                this.finish();
                break;
            case R.id.duihuan_button:
                if (addressId == 0l) {
                    Toast.makeText(PayJFGoodsActivity.this, "请先设置收货地址才能兑换,方便商品能够正常接收!", Toast.LENGTH_SHORT).show();
                    return;
                }

                dataLoadingDialog.show();
                JiFenManager.createJiFenProOrder(userId, proId, addressId, phone).startUI(new ApiCallback<Boolean>() {
                    @Override
                    public void onError(int code, String errorInfo) {
                        dataLoadingDialog.dismiss();
                        Toast.makeText(PayJFGoodsActivity.this, errorInfo, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess(Boolean aBoolean) {
                        dataLoadingDialog.dismiss();
                        PayJFGoodsActivity.this.finish();
                        Toast.makeText(PayJFGoodsActivity.this, "兑换成功", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onProgress(int progress) {

                    }
                });
                break;
            case R.id.address_layout:
                Intent addressIntent = new Intent(this, AddressManagerActivity.class);
                startActivity(addressIntent);
                break;

        }

    }

    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
