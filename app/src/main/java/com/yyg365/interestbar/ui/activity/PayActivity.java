package com.yyg365.interestbar.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;

import com.yyg365.interestbar.ui.R;

public class PayActivity extends Activity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private TextView titleNameVeiw;
    private RadioButton alipayView;
    private RadioButton wxView;
    private int checkType; //1. alipay, 2:weixin

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);
        titleNameVeiw = (TextView) findViewById(R.id.title_name);
        titleNameVeiw.setText("清单");
        titleNameVeiw.setVisibility(View.VISIBLE);
        alipayView = (RadioButton)findViewById(R.id.alipay_radio);
        wxView = (RadioButton)findViewById(R.id.wx_radio);
        wxView.setOnCheckedChangeListener(this);
        alipayView.setOnCheckedChangeListener(this);
        findViewById(R.id.title_back).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_back:
                this.finish();
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.wx_radio:
                if (isChecked) {
                    checkType = 2;
                    buttonView.setChecked(true);
                    alipayView.setChecked(false);
                } else {
                    checkType = 1;
                    buttonView.setChecked(false);
                    alipayView.setChecked(true);
                }
                break;
            case R.id.alipay_radio:
                if (isChecked) {
                    checkType = 1;
                    buttonView.setChecked(true);
                    wxView.setChecked(false);
                } else {
                    checkType = 2;
                    buttonView.setChecked(false);
                    wxView.setChecked(true);
                }
                break;
        }

    }
}
