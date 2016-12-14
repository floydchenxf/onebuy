package com.yyg365.interestbar.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yyg365.interestbar.aync.ApiCallback;
import com.yyg365.interestbar.biz.manager.LoginManager;
import com.yyg365.interestbar.biz.vo.json.UserVO;
import com.yyg365.interestbar.event.LoginEvent;
import com.yyg365.interestbar.ui.R;

import de.greenrobot.event.EventBus;

public class LoginActivity extends Activity implements View.OnClickListener {

    private static final String TAG = "LoginActivity";

    private EditText userNameView;
    private EditText passwordView;
    private TextView loginView;
    private LinearLayout backView;
    private TextView regView;
    private TextView forgetPasswordView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        backView = (LinearLayout) findViewById(R.id.title_back);
        TextView titleNameView = (TextView)findViewById(R.id.title_name);
        titleNameView.setText("登录");
        titleNameView.setVisibility(View.VISIBLE);
        regView = (TextView) findViewById(R.id.regButton);
        userNameView = (EditText) findViewById(R.id.user_name);
        passwordView = (EditText) findViewById(R.id.password);
        forgetPasswordView = (TextView) findViewById(R.id.forgot_password_view);
        backView.setOnClickListener(this);
        regView.setOnClickListener(this);
        loginView = (TextView) findViewById(R.id.login);
        loginView.setOnClickListener(this);
        forgetPasswordView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login:
                String userName = userNameView.getText().toString();
                String password = passwordView.getText().toString();
                if (TextUtils.isEmpty(userName) || TextUtils.isEmpty(password)) {
                    Toast.makeText(LoginActivity.this, "请输入用户名或密码", Toast.LENGTH_SHORT).show();
                    return;
                }

                LoginManager.login(this, userName, password).startUI(new ApiCallback<UserVO>() {
                    @Override
                    public void onError(int code, String errorInfo) {
                        Toast.makeText(LoginActivity.this, errorInfo, Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "----" + errorInfo);
                    }

                    @Override
                    public void onSuccess(UserVO userVO) {
                        Toast.makeText(LoginActivity.this, "登录成功!", Toast.LENGTH_SHORT).show();
                        LoginEvent loginEvent = new LoginEvent();
                        loginEvent.id = userVO.ID;
                        loginEvent.mobile = userVO.Mobile;
                        EventBus.getDefault().post(loginEvent);
                        finish();
                    }

                    @Override
                    public void onProgress(int progress) {

                    }
                });

                break;
            case R.id.title_back:
                this.finish();
                break;
            case R.id.regButton:
                Intent it = new Intent(LoginActivity.this, RegActivity.class);
                startActivity(it);
                this.finish();
                break;
            case R.id.forgot_password_view:
                Intent forgotIntent = new Intent(this, ForgotPasswordActivity.class);
                startActivity(forgotIntent);
                this.finish();
                break;
            default:
        }

    }

}
