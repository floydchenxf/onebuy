package com.floyd.onebuy.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.floyd.onebuy.aync.ApiCallback;
import com.floyd.onebuy.biz.manager.LoginManager;
import com.floyd.onebuy.biz.manager.OrderManager;
import com.floyd.onebuy.ui.R;

public class PayChargeActivity extends Activity implements View.OnClickListener {

    private static final String TAG = "PayChargeActivity";

    private CheckedTextView num1;
    private CheckedTextView num2;
    private CheckedTextView num3;
    private CheckedTextView num4;
    private CheckedTextView num5;

    private EditText num6;

    private View payLayout;
    private View alipayLayout;
    private View wxpayLayout;
    private RadioButton alipayButton;
    private RadioButton wxpayButton;

    private CheckedTextView[] arrays;

    private int checkedIndex = 0;
    private int payTypeChecked = 0;

    private int[] values = new int[]{20, 50, 100, 500, 1000};

    private TextView addFeeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_charge);

        findViewById(R.id.title_back).setOnClickListener(this);
        TextView titleNameView = (TextView) findViewById(R.id.title_name);
        titleNameView.setText("充值");
        titleNameView.setVisibility(View.VISIBLE);

        payLayout = findViewById(R.id.pay_layout);

        alipayLayout = findViewById(R.id.alipay_layout);
        wxpayLayout = findViewById(R.id.wxpay_layout);
        alipayButton = (RadioButton) findViewById(R.id.alipay_button);
        wxpayButton = (RadioButton) findViewById(R.id.wxpay_button);

        alipayButton.setOnClickListener(this);
        alipayButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    payTypeChecked = 0;
                    wxpayButton.setChecked(false);
                } else {
                    payTypeChecked = 1;
                    wxpayButton.setChecked(true);
                }
            }
        });
        alipayLayout.setOnClickListener(this);
        wxpayButton.setOnClickListener(this);
        wxpayButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    payTypeChecked = 1;
                    alipayButton.setChecked(false);
                } else {
                    payTypeChecked = 0;
                    alipayButton.setChecked(true);
                }
            }
        });

        wxpayLayout.setOnClickListener(this);

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

        addFeeView = (TextView) findViewById(R.id.add_fee_view);
        addFeeView.setOnClickListener(this);

        arrays = new CheckedTextView[]{num1, num2, num3, num4, num5};

        for (CheckedTextView v : arrays) {
            v.setOnClickListener(this);
        }

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
            case R.id.wxpay_layout:
                wxpayButton.setChecked(true);
                alipayButton.setChecked(false);
                payTypeChecked = 1;
                break;
            case R.id.alipay_layout:
                alipayButton.setChecked(true);
                wxpayButton.setChecked(false);
                payTypeChecked = 0;
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
                OrderManager.createOrderAndPayCharge(userId, money+"").startUI(new ApiCallback<Double>() {
                    @Override
                    public void onError(int code, String errorInfo) {
                        Toast.makeText(PayChargeActivity.this, errorInfo, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess(Double money) {
                        Toast.makeText(PayChargeActivity.this, "充值"+money+"成功!", Toast.LENGTH_SHORT).show();
                        PayChargeActivity.this.finish();
                    }

                    @Override
                    public void onProgress(int progress) {

                    }
                });

                //TODO 充值
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
