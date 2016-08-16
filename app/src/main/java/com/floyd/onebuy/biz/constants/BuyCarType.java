package com.floyd.onebuy.biz.constants;

/**
 * Created by chenxiaofeng on 16/8/17.
 */
public enum BuyCarType {

    NORMAL(1, "普通商品购物车"), FRI(2, "星期五购物车"), FUND(3, "基金购物车");

    private int code;
    private String desc;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    BuyCarType(int code, String desc) {
        this.code = code;
        this.desc = desc;

    }


}
