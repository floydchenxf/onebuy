package com.floyd.onebuy.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.floyd.onebuy.aync.ApiCallback;
import com.floyd.onebuy.biz.manager.LoginManager;
import com.floyd.onebuy.biz.vo.json.UserVO;
import com.floyd.onebuy.ui.R;

public class RegActivity extends Activity implements View.OnClickListener {

    private static final String TAG = "RegActivity";

    private View backView;
    private EditText checkCodeView;
    private TextView checkCodeButtonView;
    private EditText userNickView;
    private EditText passwordView;
    private TextView regButton;
    private EditText phoneNumView;
    private int time = 0;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private int checkType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg);
        backView = findViewById(R.id.title_back);
        checkCodeView = (EditText) findViewById(R.id.check_code);
        checkCodeButtonView = (TextView) findViewById(R.id.check_code_button);
        userNickView = (EditText) findViewById(R.id.user_nick);
        passwordView = (EditText) findViewById(R.id.password);
        regButton = (TextView) findViewById(R.id.next_step);
        backView.setOnClickListener(this);
        regButton.setOnClickListener(this);
        checkCodeButtonView.setOnClickListener(this);
        regButton.setOnClickListener(this);

    }


    private void doUpdateTime(final int time) {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                int k = time;
                if (k <= 0) {
                    checkCodeButtonView.setEnabled(true);
                    checkCodeButtonView.setBackgroundResource(R.drawable.common_round_blue_bg);
                    checkCodeButtonView.setText("获取验证码");
                    return;
                }
                k--;
                checkCodeButtonView.setText(k + "秒后重新获取");
                doUpdateTime(k);
            }
        }, 1000);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                this.finish();
                break;
            case R.id.check_code_button:
                checkCodeButtonView.setEnabled(false);
                String usernick2 = userNickView.getText().toString();
                LoginManager.fetchVerifyCodeJob(usernick2).startUI(new ApiCallback<Boolean>() {
                    @Override
                    public void onError(int code, String errorInfo) {
                        Toast.makeText(RegActivity.this, errorInfo, Toast.LENGTH_SHORT).show();
                        checkCodeButtonView.setEnabled(true);
                    }

                    @Override
                    public void onSuccess(Boolean s) {
                        if (s) {
                            checkCodeButtonView.setEnabled(false);
                            checkCodeButtonView.setBackgroundResource(R.drawable.common_round_bg);
                            checkCodeButtonView.setText("60秒后重新获取");
                            doUpdateTime(60);
                        }
                    }

                    @Override
                    public void onProgress(int progress) {

                    }
                });
                break;
            case R.id.next_step:
                String usernick = userNickView.getText().toString();
                if (TextUtils.isEmpty(usernick)) {
                    Toast.makeText(this, "请输入别名", Toast.LENGTH_SHORT).show();
                    return;
                }

                String password = passwordView.getText().toString();
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show();
                    return;
                }

                String smsCode = checkCodeView.getText().toString();
                if (TextUtils.isEmpty(smsCode)) {
                    Toast.makeText(this, "请输入校验码", Toast.LENGTH_SHORT).show();
                    return;
                }

                LoginManager.regUserJob(usernick, password, smsCode).startUI(new ApiCallback<UserVO>() {
                    @Override
                    public void onError(int code, String errorInfo) {
                        Toast.makeText(RegActivity.this, errorInfo, Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "--------" + code + "----" + errorInfo);
                    }

                    @Override
                    public void onSuccess(UserVO s) {
                        userNickView.setText("");
                        passwordView.setText("");
                        checkCodeView.setText("");
                        Toast.makeText(RegActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onProgress(int progress) {

                    }
                });
                break;
        }
    }
}