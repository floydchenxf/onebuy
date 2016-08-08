package com.floyd.onebuy.ui.activity;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.floyd.onebuy.aync.ApiCallback;
import com.floyd.onebuy.biz.manager.AppHandleManager;
import com.floyd.onebuy.biz.manager.LoginManager;
import com.floyd.onebuy.ui.DialogCreator;
import com.floyd.onebuy.ui.R;

public class FeedbackActivity extends Activity implements View.OnClickListener {

    private EditText contentView;
    private TextView feedbackView;
    private TextView titleNameView;
    private Dialog dataLoadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        findViewById(R.id.title_back).setOnClickListener(this);
        dataLoadingDialog = DialogCreator.createDataLoadingDialog(this);
        contentView = (EditText) findViewById(R.id.content);
        feedbackView = (TextView) findViewById(R.id.feedback_button);
        titleNameView = (TextView) findViewById(R.id.title_name);
        titleNameView.setText(R.string.title_feedback);
        titleNameView.setVisibility(View.VISIBLE);
        feedbackView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_back:
                this.finish();
                break;
            case R.id.feedback_button:
                final String content = contentView.getText().toString();
                if (TextUtils.isEmpty(content)) {
                    Toast.makeText(this, "请输入反馈内容!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (LoginManager.isLogin(this)) {
                    dataLoadingDialog.show();
                    AppHandleManager.commitSugguest(content, LoginManager.getLoginInfo(this)).startUI(new ApiCallback<Boolean>() {
                        @Override
                        public void onError(int code, String errorInfo) {
                            dataLoadingDialog.dismiss();
                            Toast.makeText(FeedbackActivity.this, errorInfo, Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onSuccess(Boolean aBoolean) {
                            dataLoadingDialog.dismiss();
                            contentView.setText("");
                            Toast.makeText(FeedbackActivity.this, "反馈建议成功", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onProgress(int progress) {

                        }
                    });
                }

                break;
        }

    }
}
