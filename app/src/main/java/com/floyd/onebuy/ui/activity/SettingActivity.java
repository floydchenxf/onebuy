package com.floyd.onebuy.ui.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.floyd.onebuy.aync.ApiCallback;
import com.floyd.onebuy.aync.AsyncJob;
import com.floyd.onebuy.aync.Func;
import com.floyd.onebuy.aync.JobFactory;
import com.floyd.onebuy.biz.constants.EnvConstants;
import com.floyd.onebuy.biz.tools.FileUtils;
import com.floyd.onebuy.ui.DialogCreator;
import com.floyd.onebuy.ui.R;
import com.floyd.onebuy.view.UIAlertDialog;

import java.io.File;

public class SettingActivity extends Activity implements View.OnClickListener {

    private TextView noticeView; //最新公告
    private TextView faqView; //常见问题
    private TextView aboutUsView; //关于我们
    private View contactUsLayout; //联系我们
    private TextView phoneNumView; //客服电话
    private TextView suggestView; //意见反馈
    private Switch msgSwitch; //消息设置
    private View clearLayout; //清除
    private TextView fileSizeView; //文本大小

    private Dialog dataloadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        findViewById(R.id.title_back).setOnClickListener(this);
        TextView titleNameView = (TextView) findViewById(R.id.title_name);
        titleNameView.setVisibility(View.VISIBLE);
        titleNameView.setText("设置");

        dataloadingDialog = DialogCreator.createDataLoadingDialog(this);

        noticeView = (TextView) findViewById(R.id.notice_view);
        faqView = (TextView) findViewById(R.id.faq_view);
        aboutUsView = (TextView) findViewById(R.id.about_us_view);
        contactUsLayout = findViewById(R.id.contact_us_layout);
        phoneNumView = (TextView) findViewById(R.id.phoneNum);
        suggestView = (TextView) findViewById(R.id.suggestion_view);
        msgSwitch = (Switch) findViewById(R.id.msg_switch_view);
        clearLayout = findViewById(R.id.clear_cache_layout);
        fileSizeView = (TextView) findViewById(R.id.file_size_view);

        noticeView.setOnClickListener(this);
        faqView.setOnClickListener(this);
        aboutUsView.setOnClickListener(this);
        contactUsLayout.setOnClickListener(this);
        suggestView.setOnClickListener(this);
        msgSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            }
        });

        clearLayout.setOnClickListener(this);

        AsyncJob<String> job = new AsyncJob<String>() {
            @Override
            public void start(ApiCallback<String> callback) {
                File file = new File(EnvConstants.imageRootPath);
                if (!file.exists()) {
                    callback.onError(1, "not exists");
                    return;
                }

                long size = 0;
                try {
                    size = FileUtils.getFileSize(file);
                } catch (Exception e) {
                    callback.onError(2, e.getMessage());
                    return;
                }
                float a = size / (1024 * 1024);
                callback.onSuccess(a + "M");
            }
        };

        job.threadOn().startUI(new ApiCallback<String>() {
            @Override
            public void onError(int code, String errorInfo) {
                Toast.makeText(SettingActivity.this, errorInfo, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(String s) {
                fileSizeView.setText(s);
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
            case R.id.notice_view:
                break;
            case R.id.faq_view:
                break;
            case R.id.about_us_view:
                break;
            case R.id.contact_us_layout:
                break;
            case R.id.suggestion_view:
                Intent suggestIntent = new Intent(this, FeedbackActivity.class);
                startActivity(suggestIntent);
                break;
            case R.id.clear_cache_layout:
                UIAlertDialog.Builder clearBuilder = new UIAlertDialog.Builder(this);
                SpannableString message = new SpannableString("亲！您确认清除缓存？");
                message.setSpan(new RelativeSizeSpan(2), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                message.setSpan(new ForegroundColorSpan(Color.parseColor("#d4377e")), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                clearBuilder.setMessage(message)
                        .setCancelable(true)
                        .setPositiveButton("清除",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        dialog.dismiss();
                                        JobFactory.createJob(EnvConstants.imageRootPath).map(new Func<String, File>() {
                                            @Override
                                            public File call(String s) {
                                                File f = new File(s);
                                                FileUtils.deleteFile(f);
                                                f.mkdir();
                                                return f;
                                            }
                                        }).threadOn().startUI(new ApiCallback<File>() {
                                            @Override
                                            public void onError(int code, String errorInfo) {

                                            }

                                            @Override
                                            public void onSuccess(File file) {
                                                fileSizeView.setText("0.0M");
                                            }

                                            @Override
                                            public void onProgress(int progress) {

                                            }
                                        });
                                    }
                                })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                AlertDialog dialog2 = clearBuilder.create();
                dialog2.show();
                break;
        }

    }
}