package com.floyd.onebuy.ui.buycar;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by chenxiaofeng on 16/8/14.
 */
public class CommonwealBuycarOperator implements BuycarOperator {

    private Context mContext;
    private long id;
    private long productId;
    public CommonwealBuycarOperator(Context context, long id, long  productId) {
        this.mContext = context;
        this.id = id;
        this.productId = productId;
    }

    public void buyAtOnce() {
        Toast.makeText(mContext, "基金立即购买", Toast.LENGTH_SHORT).show();

    }

    public void addBuyCar() {
        Toast.makeText(mContext, "基金加入购物车", Toast.LENGTH_SHORT).show();
    }

    public void viewBuyCar() {
        Toast.makeText(mContext, "查看基金购物车", Toast.LENGTH_SHORT).show();
    }
}
