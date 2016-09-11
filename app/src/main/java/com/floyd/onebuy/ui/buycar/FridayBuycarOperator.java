package com.floyd.onebuy.ui.buycar;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.floyd.onebuy.aync.ApiCallback;
import com.floyd.onebuy.biz.constants.BuyCarType;
import com.floyd.onebuy.biz.manager.CarManager;
import com.floyd.onebuy.biz.manager.LoginManager;
import com.floyd.onebuy.ui.MainActivity;
import com.floyd.onebuy.ui.R;
import com.floyd.onebuy.ui.activity.FridayBuyCarActivity;

/**
 * Created by chenxiaofeng on 16/8/14.
 */
public class FridayBuycarOperator implements BuycarOperator {

    private Context mContext;
    private long id;
    private long productId;

    private BuyCarType type = BuyCarType.FRI;
    public FridayBuycarOperator(Context context, long id, long  productId) {
        this.mContext = context;
        this.id = id;
        this.productId = productId;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public void buyAtOnce() {
        Toast.makeText(mContext, "星期五立即购买", Toast.LENGTH_SHORT).show();
        if (LoginManager.isLogin(mContext)) {
            long userId = LoginManager.getLoginInfo(mContext).ID;
            CarManager.addCar(type, id, userId, 1).startUI(new ApiCallback<Boolean>() {
                @Override
                public void onError(int code, String errorInfo) {
                    Toast.makeText(mContext, errorInfo, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSuccess(Boolean s) {
                    Intent it = new Intent(mContext, FridayBuyCarActivity.class);
                    it.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    mContext.startActivity(it);
                }

                @Override
                public void onProgress(int progress) {

                }
            });
        }
    }

    public void addBuyCar() {
        if (LoginManager.isLogin(mContext)) {
            long userId = LoginManager.getLoginInfo(mContext).ID;
            CarManager.addCar(type, id, userId, 1).startUI(new ApiCallback<Boolean>() {
                @Override
                public void onError(int code, String errorInfo) {
                    Toast.makeText(mContext, errorInfo, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSuccess(Boolean s) {
                    Toast.makeText(mContext, "添加星期五购物车成功", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onProgress(int progress) {

                }
            });
        }

    }

    public void viewBuyCar() {
        Intent viewBuyCar = new Intent(mContext, FridayBuyCarActivity.class);
        viewBuyCar.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        mContext.startActivity(viewBuyCar);
    }
}
