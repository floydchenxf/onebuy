package com.yyg365.interestbar.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.yyg365.interestbar.aync.ApiCallback;
import com.yyg365.interestbar.bean.MD5Util;
import com.yyg365.interestbar.biz.manager.LoginManager;
import com.yyg365.interestbar.ui.R;

public class ForgotPassword2Activity extends Activity implements View.OnClickListener {

    private String mobileNum;
    private Long userId;

    private EditText newPasswordView;
    private TextView showPasswordButton;

    private boolean mbDisplayFlg = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password2);
        mobileNum = getIntent().getExtras().getString(ForgotPasswordActivity.MOBILE_NUMBER);
        userId = getIntent().getExtras().getLong(ForgotPasswordActivity.USER_ID, 0l);
        findViewById(R.id.title_back).setOnClickListener(this);
        TextView titleNameView = (TextView) findViewById(R.id.title_name);
        titleNameView.setText("找回密码");
        titleNameView.setVisibility(View.VISIBLE);
        newPasswordView = (EditText) findViewById(R.id.new_password_view);
        showPasswordButton = (TextView) findViewById(R.id.show_password_button);
        showPasswordButton.setOnClickListener(this);
        findViewById(R.id.new_password_button).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_back:
                this.finish();
                break;

            case R.id.show_password_button:
                if (!mbDisplayFlg) {
                    // display password text, for example "123456"
                    newPasswordView.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    // hide password, display "."
                    newPasswordView.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
                mbDisplayFlg = !mbDisplayFlg;
                newPasswordView.postInvalidate();
                break;
            case R.id.new_password_button:
                String newPassword = newPasswordView.getText().toString();
                if (TextUtils.isEmpty(newPassword)) {
                    Toast.makeText(this, "请输入新密码", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (newPassword.length() < 6) {
                    Toast.makeText(this, "您输入的密码过短，密码不能小于6个字符", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (newPassword.length() > 20) {
                    Toast.makeText(this, "您输入的密码过长，密码不能大于20个字符", Toast.LENGTH_SHORT).show();
                    return;
                }

                LoginManager.forgetPaswordStep2(userId, newPassword).startUI(new ApiCallback<Boolean>() {
                    @Override
                    public void onError(int code, String errorInfo) {
                        Toast.makeText(ForgotPassword2Activity.this, errorInfo, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess(Boolean aLong) {
                        if (aLong) {
                            ForgotPassword2Activity.this.finish();
                            Toast.makeText(ForgotPassword2Activity.this, "找回密码成功", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onProgress(int progress) {

                    }
                });

//                LoginManager.forgetPassword(mobileNum, smsCode, newPassword).startUI(new ApiCallback<String>() {
//                    @Override
//                    public void onError(int code, String errorInfo) {
//                        Toast.makeText(ForgotPassword2Activity.this, errorInfo, Toast.LENGTH_SHORT).show();
//                    }
//
//                    @Override
//                    public void onSuccess(String s) {
//                        ForgotPassword2Activity.this.finish();
//                        Toast.makeText(ForgotPassword2Activity.this, "找回密码成功", Toast.LENGTH_SHORT).show();
//
//                    }
//
//                    @Override
//                    public void onProgress(int progress) {
//
//                    }
//                });

                break;
        }

    }
}
