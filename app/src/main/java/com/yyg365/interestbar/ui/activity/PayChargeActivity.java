package com.yyg365.interestbar.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.yyg365.interestbar.aync.ApiCallback;
import com.yyg365.interestbar.aync.AsyncJob;
import com.yyg365.interestbar.biz.constants.APIConstants;
import com.yyg365.interestbar.biz.manager.CarManager;
import com.yyg365.interestbar.biz.manager.LoginManager;
import com.yyg365.interestbar.biz.manager.OrderManager;
import com.yyg365.interestbar.biz.vo.json.AlipayOrderVO;
import com.yyg365.interestbar.biz.vo.json.CarPayChannel;
import com.yyg365.interestbar.biz.vo.json.ChargeOrderVO;
import com.yyg365.interestbar.biz.vo.json.OrderVO;
import com.yyg365.interestbar.biz.vo.pay.PayResult;
import com.yyg365.interestbar.event.BuyCarNumEvent;
import com.yyg365.interestbar.event.WxPayEvent;
import com.yyg365.interestbar.ui.ImageLoaderFactory;
import com.yyg365.interestbar.ui.R;
import com.yyg365.interestbar.ui.loading.DataLoadingView;
import com.yyg365.interestbar.ui.loading.DefaultDataLoadingView;
import com.yyg365.interestbar.ui.share.ShareConstants;

import java.util.List;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;

public class PayChargeActivity extends BasePayActivity implements View.OnClickListener {

    private static final String TAG = "PayChargeActivity";
    public static final String IS_RECHARGE = "IS_RECHARGE";
    public static final String PRODUCT_ID = "PRODUCT_ID";

    private CheckedTextView num1;
    private CheckedTextView num2;
    private CheckedTextView num3;
    private CheckedTextView num4;
    private CheckedTextView num5;

    private EditText num6;

    private View payLayout;
    private CheckedTextView[] arrays;

    private DataLoadingView dataLoadingView;
    private ImageLoader mImageLoader;

    private int checkedIndex = 0;
    private int payTypeChecked = 0;

    private int[] values = new int[]{20, 50, 100, 500, 1000};

    private TextView addFeeView;
    private LinearLayout payTypeLayout;

    private boolean isRecharge;
    private long proId;

    private IWXAPI iwxapi;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_charge);

        iwxapi = WXAPIFactory.createWXAPI(this, ShareConstants.WX_APP_ID);
        iwxapi.registerApp(ShareConstants.WX_APP_ID);
        EventBus.getDefault().register(this);

        isRecharge = getIntent().getBooleanExtra(IS_RECHARGE, true);
        proId = getIntent().getLongExtra(PRODUCT_ID, 0l);
        mImageLoader = ImageLoaderFactory.createImageLoader();
        dataLoadingView = new DefaultDataLoadingView();
        dataLoadingView.initView(findViewById(R.id.act_lsloading), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadData();
            }
        });
        findViewById(R.id.title_back).setOnClickListener(this);
        TextView titleNameView = (TextView) findViewById(R.id.title_name);
        addFeeView = (TextView) findViewById(R.id.add_fee_view);
        addFeeView.setOnClickListener(this);
        if (isRecharge) {
            titleNameView.setText("充值");
            addFeeView.setText("充值");
        } else {
            titleNameView.setText("捐款");
            addFeeView.setText("捐款");
        }
        titleNameView.setVisibility(View.VISIBLE);

        payLayout = findViewById(R.id.pay_layout);

        payTypeLayout = (LinearLayout) findViewById(R.id.pay_type_layout);
        num1 = (CheckedTextView) findViewById(R.id.num1);
        num2 = (CheckedTextView) findViewById(R.id.num2);
        num3 = (CheckedTextView) findViewById(R.id.num3);
        num4 = (CheckedTextView) findViewById(R.id.num4);
        num5 = (CheckedTextView) findViewById(R.id.num5);
        num6 = (EditText) findViewById(R.id.num6);
        num6.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    checkedIndex = 5;
                    Log.i(TAG, "checkedIndex---------" + checkedIndex);
                    for (CheckedTextView vv : arrays) {
                        vv.setChecked(false);
                    }
                }
            }
        });

        arrays = new CheckedTextView[]{num1, num2, num3, num4, num5};

        for (CheckedTextView v : arrays) {
            v.setOnClickListener(this);
        }

        loadData();
    }

    private void loadData() {
        dataLoadingView.startLoading();
        String payType = null;
        if (isRecharge) {
            payType = "recharge";
        } else {
            payType = "gy";
        }
        CarManager.getPayChannels(payType, LoginManager.getLoginInfo(this).ID).startUI(new ApiCallback<List<CarPayChannel>>() {
            @Override
            public void onError(int code, String errorInfo) {
                dataLoadingView.loadFail();
            }

            @Override
            public void onSuccess(List<CarPayChannel> carPayChannels) {
                dataLoadingView.loadSuccess();
                if (carPayChannels != null && !carPayChannels.isEmpty()) {
                    final RadioButton[] rbArray = new RadioButton[carPayChannels.size()];
                    View.OnClickListener l = new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Long id = (Long) view.getTag();
                            for (RadioButton trb : rbArray) {
                                Long rbId = (Long) trb.getTag();
                                if (id.equals(rbId)) {
                                    trb.setChecked(true);
                                    payTypeChecked = id.intValue();
                                } else {
                                    trb.setChecked(false);
                                }
                            }
                        }
                    };

                    CompoundButton.OnCheckedChangeListener checkChangedListener = new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                            if (!isChecked) {
                                compoundButton.setChecked(false);
                                return;
                            }
                            Long id = (Long) compoundButton.getTag();
                            for (RadioButton trb : rbArray) {
                                Long rbId = (Long) trb.getTag();
                                if (id.equals(rbId)) {
                                    trb.setChecked(true);
                                    payTypeChecked = id.intValue();
                                } else {
                                    trb.setChecked(false);
                                }
                            }
                        }
                    };
                    int idx = 0;
                    for (CarPayChannel channel : carPayChannels) {
                        View v = View.inflate(PayChargeActivity.this, R.layout.pay_type_item, null);
                        v.setTag(channel.ID);
                        NetworkImageView imageView = (NetworkImageView) v.findViewById(R.id.wx_icon);
                        imageView.setImageUrl(channel.getPicUrl(), mImageLoader);
                        TextView payNameView = (TextView) v.findViewById(R.id.pay_name_view);
                        payNameView.setText(channel.Name);
                        RadioButton rb = (RadioButton) v.findViewById(R.id.wx_radio);
                        rb.setTag(channel.ID);
                        rbArray[idx] = rb;
                        if (idx == 0) {
                            rb.setChecked(true);
                            payTypeChecked = channel.ID.intValue();
                        } else {
                            rb.setChecked(false);
                        }

                        rb.setOnCheckedChangeListener(checkChangedListener);
                        v.setOnClickListener(l);
                        payTypeLayout.addView(v);
                        idx++;
                    }
                }
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
            case R.id.num1:
            case R.id.num2:
            case R.id.num3:
            case R.id.num4:
            case R.id.num5:
                checkClick(v.getId());
                break;
            case R.id.add_fee_view:
                int money = 0;
                if (checkedIndex < 5) {
                    money = values[checkedIndex];
                } else {
                    String moneyString = num6.getEditableText().toString();
                    if (TextUtils.isEmpty(moneyString)) {
                        Toast.makeText(this, "请输入金额", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (!TextUtils.isDigitsOnly(moneyString)) {
                        Toast.makeText(this, "请输入数字", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    money = Integer.parseInt(moneyString);
                }
                Long userId = LoginManager.getLoginInfo(this).ID;
                if (isRecharge) {

                    OrderManager.createChargeOrder(userId, money + "", payTypeChecked).startUI(new ApiCallback<ChargeOrderVO>() {
                        @Override
                        public void onError(int code, String errorInfo) {
                            Toast.makeText(PayChargeActivity.this, errorInfo, Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onSuccess(ChargeOrderVO chargeOrderVO) {
                            if (payTypeChecked == 4) { //微信
                                OrderManager.pay(chargeOrderVO.tn, iwxapi);
                            } else if (payTypeChecked == 3) { //支付宝
                                AlipayOrderVO orderVO = chargeOrderVO.orders;
                                OrderManager.payByAlipay(PayChargeActivity.this, orderVO).startUI(new ApiCallback<PayResult>() {
                                    @Override
                                    public void onError(int code, String errorInfo) {

                                    }

                                    @Override
                                    public void onSuccess(PayResult result) {
                                        String resultStatus = result.getResultStatus();
                                        if (TextUtils.equals(resultStatus, "9000")) {
                                            if (!PayChargeActivity.this.isFinishing()) {
                                                PayChargeActivity.this.finish();
                                            }
                                        } else {
                                            if (TextUtils.equals(resultStatus, "8000")) {
                                                Toast.makeText(PayChargeActivity.this, "支付结果确认中", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(PayChargeActivity.this, "支付失败", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }

                                    @Override
                                    public void onProgress(int progress) {

                                    }
                                });
                            }
                        }

                        @Override
                        public void onProgress(int progress) {

                        }
                    });
                } else {
                    OrderManager.createCommonwealOrder(userId, proId, money + "", "", payTypeChecked).startUI(new ApiCallback<OrderVO>() {
                        @Override
                        public void onError(int code, String errorInfo) {
                            Toast.makeText(PayChargeActivity.this, errorInfo, Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onSuccess(OrderVO orderVO) {
                            if (payTypeChecked == 4) { //微信
                                OrderManager.pay(orderVO.tn, iwxapi);
                            } else {
                                if (isRecharge) {
                                    Toast.makeText(PayChargeActivity.this, "充值成功", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(PayChargeActivity.this, "捐款成功", Toast.LENGTH_SHORT).show();
                                }
                                PayChargeActivity.this.finish();
                            }
                        }

                        @Override
                        public void onProgress(int progress) {

                        }
                    });

                }
                break;

        }

    }

    @Subscribe
    public void onEventMainThread(WxPayEvent event) {
        if (!this.isFinishing()) {
            int errorCode = event.errorCode;
            if (errorCode == BaseResp.ErrCode.ERR_OK) {
                Toast.makeText(this, "支付成功", Toast.LENGTH_SHORT).show();
                this.finish();
            } else if (errorCode == BaseResp.ErrCode.ERR_USER_CANCEL){
                Toast.makeText(this, "已取消支付", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "支付失败", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void checkClick(int id) {
        int idx = 0;
        for (CheckedTextView v : arrays) {
            if (id == v.getId()) {
                v.setChecked(true);
                checkedIndex = idx;
                payLayout.requestFocus();
            } else {
                v.setChecked(false);
            }

            idx++;
        }

        Log.i(TAG, "checkedIndex---------" + checkedIndex);
    }

    public void onDestroy() {
        super.onDestroy();
        iwxapi.unregisterApp();
        EventBus.getDefault().unregister(this);
    }
}
