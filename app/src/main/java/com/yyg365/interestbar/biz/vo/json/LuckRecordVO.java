package com.yyg365.interestbar.biz.vo.json;

import java.util.List;

/**
 * Created by chenxiaofeng on 16/9/18.
 */
public class LuckRecordVO {
    public LuckAddressVO clientAddr;
    public List<ProductLssueWithWinnerVO> proLssueList;


    public static class LuckAddressVO {
        public Long ID;
        public String Name;
        public String Phone;
        public String Address;
    }
}
