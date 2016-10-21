package com.floyd.onebuy.biz.vo.json;

import java.util.List;

/**
 * Created by chenxiaofeng on 16/10/21.
 */
public class CalRecordsVO {
    public List<CalItemVO> Records;
    public String ResultSum;//求和总数
    public int ResultCount;//Records总数
    public String Phase;//期数
    public String LotteryResult;//开奖号码
    public String Mod;//余数
    public String Result;//计算结果
    public String Formula1;
    public String Formula2;
}
