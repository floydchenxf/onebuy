package com.yyg365.interestbar.ui.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.yyg365.interestbar.event.WxPayEvent;
import com.yyg365.interestbar.ui.R;
import com.yyg365.interestbar.ui.share.ShareConstants;

import de.greenrobot.event.EventBus;

/**
 * Created by chenxiaofeng on 2017/1/17.
 */
public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {

    private IWXAPI api;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pay_result);

        api = WXAPIFactory.createWXAPI(this, null);
        api.registerApp(ShareConstants.WX_APP_ID);
        api.handleIntent(getIntent(), this);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 1500);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Log.e("onNewIntent", "onNewIntent");
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
        finish();
    }

    @Override
    public void onReq(BaseReq req) {
        Log.e("onReq", "onReq");
        Toast.makeText(WXPayEntryActivity.this, "支付失败，请重新支付！", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void onResp(BaseResp resp) {

        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
            if (!this.isFinishing()) {
                EventBus.getDefault().post(new WxPayEvent(resp.errCode));
            }
            finish();
        }

    }
}
