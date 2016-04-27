package com.floyd.onebuy.ui.activity;

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

import com.floyd.onebuy.aync.ApiCallback;
import com.floyd.onebuy.biz.manager.LoginManager;
import com.floyd.onebuy.biz.vo.json.UserVO;
import com.floyd.onebuy.event.LoginEvent;
import com.floyd.onebuy.ui.R;

import de.greenrobot.event.EventBus;

public class LoginActivity extends Activity implements View.OnClickListener {

    private static final String TAG = "LoginActivity";

    private EditText userNameView;
    private EditText passwordView;
    private TextView loginView;
    private LinearLayout backView;
    private TextView regView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        backView = (LinearLayout) findViewById(R.id.title_back);
        regView = (TextView) findViewById(R.id.regButton);
        userNameView = (EditText) findViewById(R.id.user_name);
        passwordView = (EditText) findViewById(R.id.password);
        backView.setOnClickListener(this);
        regView.setOnClickListener(this);
        loginView = (TextView) findViewById(R.id.login);
        loginView.setOnClickListener(this);
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
//                        finish();
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
            default:
        }

    }

}
