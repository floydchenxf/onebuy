package com.floyd.onebuy.biz.constants;

/**
 * Created by chenxiaofeng on 16/10/30.
 */
public enum ClientLevelEnum {

    LEVEL1(1, "幼儿园"), LEVEL2(2, "小学生"), LEVEL3(3, "初中生"), LEVEL4(4, "高中生"),
    LEVEL5(5, "大学生"), LEVEL6(6, "研究生"), LEVEL7(7, "博士生"), LEVEL8(8, "博士后");

    private int code;
    private String desc;

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getCode() {

        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }


    ClientLevelEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }


}
