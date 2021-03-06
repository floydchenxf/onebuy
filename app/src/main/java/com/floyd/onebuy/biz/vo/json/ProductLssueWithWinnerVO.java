package com.floyd.onebuy.biz.vo.json;

import android.text.TextUtils;

/**
 * Created by floyd on 16-7-23.
 */
public class ProductLssueWithWinnerVO extends ProductBaseLssueVO {
    public String ProductLssueCode;
    public String PrizeCode;
    public Long PriceTime;
    public int JonidedCount;
    public int IsSend; //收货按钮显示
    public int IsShow;//晒单按钮显示

    public Long OrderID;//订单id

    public String getFullTitle() {
        StringBuilder sb = new StringBuilder();
        if (!TextUtils.isEmpty(this.ProductLssueCode)) {
            sb.append("(").append("第").append(this.ProductLssueCode).append("期").append(") ");
        }
        sb.append(this.ProName);
        return sb.toString();
    }
}
