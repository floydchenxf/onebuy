package com.floyd.onebuy.ui.buycar;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.floyd.onebuy.aync.ApiCallback;
import com.floyd.onebuy.biz.manager.CarManager;
import com.floyd.onebuy.biz.manager.LoginManager;
import com.floyd.onebuy.ui.MainActivity;
import com.floyd.onebuy.ui.R;

/**
 * Created by chenxiaofeng on 16/8/14.
 */
public class NormalProductBuycarOperator implements BuycarOperator {
    private long id;
    private long productId;
    private Context mContext;

    public NormalProductBuycarOperator(Context context, long id, long productId) {
        this.id = id;
        this.productId = productId;
        this.mContext = context;
    }
    @Override
    public void buyAtOnce() {
        if (LoginManager.isLogin(mContext)) {
            long userId = LoginManager.getLoginInfo(mContext).ID;
            CarManager.addCar(id, userId, 1).startUI(new ApiCallback<Boolean>() {
                @Override
                public void onError(int code, String errorInfo) {
                    Toast.makeText(mContext, errorInfo, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSuccess(Boolean s) {
                    Intent it = new Intent(mContext, MainActivity.class);
                    it.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    it.putExtra(MainActivity.TAB_INDEX, R.id.tab_buy_car);
                    mContext.startActivity(it);
                }

                @Override
                public void onProgress(int progress) {

                }
            });
        }
    }

    @Override
    public void addBuyCar() {
        if (LoginManager.isLogin(mContext)) {
            long userId = LoginManager.getLoginInfo(mContext).ID;
            CarManager.addCar(id, userId, 1).startUI(new ApiCallback<Boolean>() {
                @Override
                public void onError(int code, String errorInfo) {
                    Toast.makeText(mContext, errorInfo, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSuccess(Boolean s) {
                    Toast.makeText(mContext, "添加购物车成功", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onProgress(int progress) {

                }
            });
        }
    }

    @Override
    public void viewBuyCar() {
        Intent viewBuyCar = new Intent(mContext, MainActivity.class);
        viewBuyCar.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        viewBuyCar.putExtra(MainActivity.TAB_INDEX, R.id.tab_buy_car);
        mContext.startActivity(viewBuyCar);
    }
}
