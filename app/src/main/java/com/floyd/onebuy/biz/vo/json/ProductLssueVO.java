package com.floyd.onebuy.biz.vo.json;

import android.text.TextUtils;

import com.floyd.onebuy.biz.constants.APIConstants;

/**
 * Created by floyd on 16-4-30.
 */
public class ProductLssueVO extends  ProductBaseLssueVO {
    public int Code;
    public int Status;

    public String getPicUrl() {
        if (TextUtils.isEmpty(Pictures)) {
            return null;
        }

        if (Pictures.startsWith("http")) {
            return Pictures;
        }
        return APIConstants.HOST + Pictures;
    }

}
