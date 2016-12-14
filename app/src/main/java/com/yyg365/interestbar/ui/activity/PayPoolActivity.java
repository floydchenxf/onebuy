package com.yyg365.interestbar.ui.activity;

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
import com.yyg365.interestbar.aync.ApiCallback;
import com.yyg365.interestbar.biz.constants.APIConstants;
import com.yyg365.interestbar.biz.manager.CarManager;
import com.yyg365.interestbar.biz.manager.LoginManager;
import com.yyg365.interestbar.biz.manager.OrderManager;
import com.yyg365.interestbar.biz.vo.json.CarPayChannel;
import com.yyg365.interestbar.biz.vo.json.OrderVO;
import com.yyg365.interestbar.ui.ImageLoaderFactory;
import com.yyg365.interestbar.ui.R;
import com.yyg365.interestbar.ui.loading.DataLoadingView;
import com.yyg365.interestbar.ui.loading.DefaultDataLoadingView;
import com.unionpay.UPPayAssistEx;

import java.util.List;

public class PayPoolActivity extends BasePayActivity implements View.OnClickListener {

    private static final String TAG = "PayChargeActivity";
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
    private EditText remarkView;

    private long proId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_pool);

        proId = getIntent().getLongExtra(PRODUCT_ID, 1l);
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
        titleNameView.setText("捐款");
        addFeeView.setText("捐款");
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

        remarkView = (EditText) findViewById(R.id.remark_view);

        loadData();
    }

    private void loadData() {
        dataLoadingView.startLoading();
        String payType = null;
        payType = "gy";
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
                        View v = View.inflate(PayPoolActivity.this, R.layout.pay_type_item, null);
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

                String remark = remarkView.getText().toString();
                if (TextUtils.isEmpty(remark)) {
                    Toast.makeText(this, "请输入感想", Toast.LENGTH_SHORT).show();
                    return;
                }
                Long userId = LoginManager.getLoginInfo(this).ID;
                OrderManager.createPoolOrder(userId, proId, money + "", remark, payTypeChecked).startUI(new ApiCallback<OrderVO>() {
                    @Override
                    public void onError(int code, String errorInfo) {
                        Toast.makeText(PayPoolActivity.this, errorInfo, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess(OrderVO orderVO) {
                        if (payTypeChecked == 6) {
                            UPPayAssistEx.startPay(PayPoolActivity.this, null, null, orderVO.tn, APIConstants.PAY_MODE);
                        } else {
                            Toast.makeText(PayPoolActivity.this, "捐款成功", Toast.LENGTH_SHORT).show();
                            PayPoolActivity.this.finish();
                        }
                    }

                    @Override
                    public void onProgress(int progress) {

                    }
                });

                break;

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
}