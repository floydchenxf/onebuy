package com.floyd.onebuy.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.floyd.onebuy.biz.manager.GsonHelper;
import com.google.gson.reflect.TypeToken;
import com.wangyin.payment.jdpaysdk.JDPay;
import com.wangyin.payment.jdpaysdk.JDPaySetting;
import com.wangyin.payment.jdpaysdk.front.common.Constant;

import java.lang.reflect.Type;
import java.util.Map;

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
        if (resultCode == Constant.PAY_RESPONSE_CODE) {
            String json = data.getStringExtra(JDPay.JDPAY_RESULT);
            Type type = new TypeToken<Map<String, String>>(){}.getType();
            Map<String, String> kk = GsonHelper.getGson().fromJson(json, type);
            String payStatus = kk.get("payStatus");
            if ("JDP_PAY_FAIL".equals(payStatus)) {
                Toast.makeText(this, "支付失败", Toast.LENGTH_SHORT).show();
            } else if ("JDP_PAY_SUCCESS".equals(payStatus)) {
                Toast.makeText(this, "支付成功", Toast.LENGTH_SHORT).show();
                this.finish();
            } else {
                Toast.makeText(this, "用户取消了支付", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
