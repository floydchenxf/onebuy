package com.floyd.onebuy.ui.activity;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.floyd.onebuy.aync.ApiCallback;
import com.floyd.onebuy.biz.manager.LoginManager;
import com.floyd.onebuy.biz.vo.json.UserVO;
import com.floyd.onebuy.ui.DialogCreator;
import com.floyd.onebuy.ui.R;

/**
 * Created by floyd on 16-7-12.
 */
public class ChangeNickActivity extends Activity implements View.OnClickListener {

    private EditText nickEditView;
    private ImageView delImageView;

    private UserVO userVO;
    private Dialog dataloadDialog;

    TextWatcher mTextWatcher = new TextWatcher() {
        private CharSequence temp;
        private int editStart ;
        private int editEnd ;
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            temp = s;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (temp.length() > 0) {
                delImageView.setVisibility(View.VISIBLE);
            } else {
                delImageView.setVisibility(View.GONE);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_nick);
        dataloadDialog = DialogCreator.createDataLoadingDialog(this);
        TextView titleName = (TextView) findViewById(R.id.title_name);
        titleName.setText("修改昵称");
        titleName.setVisibility(View.VISIBLE);
        TextView editView = (TextView) findViewById(R.id.right);
        editView.setText("保存");
        editView.setVisibility(View.VISIBLE);
        editView.setOnClickListener(this);
        findViewById(R.id.title_back).setOnClickListener(this);
        nickEditView = (EditText) findViewById(R.id.nick_name_edit);
        delImageView = (ImageView) findViewById(R.id.del_image);
        userVO = LoginManager.getLoginInfo(this);
        String nick = userVO.getUserName();
        nickEditView.setText(nick);
        nickEditView.setSelection(nick.length());
        delImageView.setOnClickListener(this);
        nickEditView.addTextChangedListener(mTextWatcher);
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_back:
                this.finish();
                break;
            case R.id.del_image:
                nickEditView.setText("");
                nickEditView.setSelection(0);
                break;
            case R.id.right:
                final String nickName = nickEditView.getText().toString();
                if (TextUtils.isEmpty(nickName)) {
                    Toast.makeText(ChangeNickActivity.this, "请输入用户昵称", Toast.LENGTH_SHORT).show();
                    return;
                }

                dataloadDialog.show();
                LoginManager.modifyUserInfo(userVO.ID, nickName).startUI(new ApiCallback<Boolean>() {
                    @Override
                    public void onError(int code, String errorInfo) {
                        dataloadDialog.dismiss();
                        Toast.makeText(ChangeNickActivity.this, errorInfo, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess(Boolean aBoolean) {
                        dataloadDialog.dismiss();
                        if (aBoolean) {
                            userVO.Name = nickName;
                            LoginManager.saveLoginInfo(ChangeNickActivity.this, userVO);
                            Toast.makeText(ChangeNickActivity.this, "修改昵称成功", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }

                    @Override
                    public void onProgress(int progress) {

                    }
                });
                break;
        }

    }
}
