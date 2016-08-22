package com.floyd.onebuy.ui.activity;

import android.app.Activity;
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

public class MobileBindActivity extends Activity implements View.OnClickListener {

    private TextView checkCodeButton;
    private TextView mobileView;
    private EditText checkCodeView;

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
        setContentView(R.layout.activity_mobile_bind);
        checkCodeButton = (TextView) findViewById(R.id.check_code_button);
        mobileView = (TextView) findViewById(R.id.mobile_view);

        checkCodeView = (EditText) findViewById(R.id.check_code);
        checkCodeButton.setOnClickListener(this);

        findViewById(R.id.title_back).setOnClickListener(this);
        TextView titleNameView = (TextView) findViewById(R.id.title_name);
        titleNameView.setText("修改手机号码");
        titleNameView.setVisibility(View.VISIBLE);

        TextView rightView = (TextView) findViewById(R.id.right);
        rightView.setText("保存");
        rightView.setVisibility(View.VISIBLE);

        rightView.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.right:
                Long id = LoginManager.getLoginInfo(this).ID;
                String mobile = mobileView.getText().toString();
                if (TextUtils.isEmpty(mobile)) {
                    Toast.makeText(this, "请输入手机号码", Toast.LENGTH_SHORT).show();
                    return;
                }

                String smsCode = checkCodeView.getText().toString();
                if (TextUtils.isEmpty(smsCode)) {
                    Toast.makeText(this, "请输入校验码", Toast.LENGTH_SHORT).show();
                    return;
                }


                LoginManager.bindClientMobile(id, mobile, smsCode).startUI(new ApiCallback<Boolean>() {
                    @Override
                    public void onError(int code, String errorInfo) {
                        Toast.makeText(MobileBindActivity.this, errorInfo, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess(Boolean aBoolean) {
                        Toast.makeText(MobileBindActivity.this, "绑定成功", Toast.LENGTH_SHORT).show();
                        MobileBindActivity.this.finish();
                    }

                    @Override
                    public void onProgress(int progress) {

                    }
                });
                break;
            case R.id.check_code_button:
                String mobile1 = mobileView.getText().toString();
                if (TextUtils.isEmpty(mobile1)) {
                    Toast.makeText(this, "请输入手机号码", Toast.LENGTH_SHORT).show();
                    return;
                }
                LoginManager.sendSms(mobile1).startUI(new ApiCallback<Long>() {
                    @Override
                    public void onError(int code, String errorInfo) {
                        Toast.makeText(MobileBindActivity.this, errorInfo, Toast.LENGTH_SHORT).show();
                        checkCodeButton.setEnabled(true);
                    }

                    @Override
                    public void onSuccess(Long s) {
                        checkCodeButton.setEnabled(false);
                        checkCodeButton.setText("60秒后重新获取");
                        doUpdateTime(60);
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
}
