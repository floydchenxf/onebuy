package com.floyd.onebuy.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.floyd.onebuy.aync.ApiCallback;
import com.floyd.onebuy.biz.manager.LoginManager;
import com.floyd.onebuy.ui.R;

import java.util.Map;

public class ForgotPasswordActivity extends Activity implements View.OnClickListener {

    public static final String MOBILE_NUMBER = "MOBILE_NUMBER";
    public static final String SMS_CODE = "SMS_CODE";
    private EditText mobileView;
    private EditText checkCodeView;
    private TextView checkCodeButton;
    private TextView nextStepButton;
    private Handler mHandler = new Handler(Looper.getMainLooper());

    private void doUpdateTime(final int time) {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                int k = time;
                if (k <= 0) {
                    checkCodeButton.setEnabled(true);
                    checkCodeButton.setText("获取验证码");
                    return;
                }
                k--;
                checkCodeButton.setText(k + "秒后重新获取");
                doUpdateTime(k);
            }
        }, 1000);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        findViewById(R.id.title_back).setOnClickListener(this);
        findViewById(R.id.next_step).setOnClickListener(this);
        TextView titleNameView = (TextView) findViewById(R.id.title_name);
        titleNameView.setText("找回密码");
        titleNameView.setVisibility(View.VISIBLE);
        mobileView = (EditText) findViewById(R.id.mobile_view);
        checkCodeView = (EditText) findViewById(R.id.check_code);
        checkCodeButton = (TextView) findViewById(R.id.check_code_button);
        nextStepButton = (TextView) findViewById(R.id.next_step);
        checkCodeButton.setOnClickListener(this);
        nextStepButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_back:
                this.finish();
                break;
            case R.id.next_step:
                //TODO 验证checkcode
                String mobile = mobileView.getText().toString();
                if (TextUtils.isEmpty(mobile)) {
                    Toast.makeText(this, "请输入注册手机号码", Toast.LENGTH_SHORT).show();
                    return;
                }

                String checkCode = checkCodeView.getText().toString();
                if (TextUtils.isEmpty(checkCode)) {
                    Toast.makeText(this, "请输入验证码", Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent nextIntent = new Intent(this, ForgotPassword2Activity.class);
                nextIntent.putExtra(MOBILE_NUMBER, mobile);
                nextIntent.putExtra(SMS_CODE, checkCode);
                startActivity(nextIntent);
                this.finish();
                break;
            case R.id.check_code_button:
                String mobile1 = mobileView.getText().toString();
                if (TextUtils.isEmpty(mobile1)) {
                    Toast.makeText(this, "请输入手机号码", Toast.LENGTH_SHORT).show();
                    return;
                }
                LoginManager.sendSms(mobile1).startUI(new ApiCallback<Map<String, String>>() {
                    @Override
                    public void onError(int code, String errorInfo) {
                        Toast.makeText(ForgotPasswordActivity.this, errorInfo, Toast.LENGTH_SHORT).show();
                        checkCodeButton.setEnabled(true);
                    }

                    @Override
                    public void onSuccess(Map<String, String> s) {
                        checkCodeButton.setEnabled(false);
                        checkCodeButton.setText("60秒后重新获取");
                        doUpdateTime(60);
                    }

                    @Override
                    public void onProgress(int progress) {

                    }
                });

                break;
        }

    }
}
