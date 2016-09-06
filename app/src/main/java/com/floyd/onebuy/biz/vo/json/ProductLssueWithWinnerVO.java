package com.floyd.onebuy.biz.vo.json;

import android.text.TextUtils;

/**
 * Created by floyd on 16-7-23.
 */
public class ProductLssueWithWinnerVO extends ProductBaseLssueVO {
    public String ProductLssueCode;
    public String PriceCode;
    public Long PriceTime;

    public String getFullTitle() {
        StringBuilder sb = new StringBuilder();
        if (!TextUtils.isEmpty(this.ProductLssueCode)) {
            sb.append("(").append("第").append(this.ProductLssueCode).append("期").append(") ");
        }
        sb.append(this.ProName);
        return sb.toString();
    }
}
