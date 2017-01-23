package com.floyd.onebuy.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.wangyin.payment.jdpaysdk.JDPay;
import com.wangyin.payment.jdpaysdk.JDPaySetting;

/**
 * Created by chenxiaofeng on 2017/1/22.
 */
public class BaseJDPayActivity extends Activity {

    protected void onCreate(Bundle saveInstance) {
        super.onCreate(saveInstance);
        JDPaySetting.init(this, "");
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Object dataObj = data.getExtras().get(JDPay.JDPAY_RESULT);
        Toast.makeText(getApplicationContext(), dataObj.toString(), Toast.LENGTH_SHORT).show();
    }
}
