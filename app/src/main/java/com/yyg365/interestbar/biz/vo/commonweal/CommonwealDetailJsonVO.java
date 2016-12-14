package com.yyg365.interestbar.biz.vo.commonweal;

import java.util.List;

/**
 * Created by floyd on 16-7-30.
 */
public class CommonwealDetailJsonVO {
    public List<CommonwealHelperVO> PersonList;
    public FoundationInfo FoundationInfo;
    public JoinInfo JoinInfo;


    public static class FoundationInfo {
        public long FoundationID;
        public String Code;
        public String Percent;
        public String TotalMoney;//目标金额
        public String RaiseMoney;//已筹金额
        public int RaiseCount;//捐款人数
        public String FoundationName;//公益名称
        public String Brief;//公益介绍
        public String Description;
        public String Pictures;//公益图片 多图用|分隔
        public Integer Status;//状态
    }

    public static class JoinInfo {
        public String TotalMoney; //总价格
        public int TotalCount;//参与次数
    }
}
