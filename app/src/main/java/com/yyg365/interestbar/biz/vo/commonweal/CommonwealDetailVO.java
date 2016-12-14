package com.yyg365.interestbar.biz.vo.commonweal;

import com.yyg365.interestbar.biz.vo.AdvVO;

import java.util.List;

/**
 * Created by floyd on 16-7-30.
 */
public class CommonwealDetailVO {
    public List<CommonwealHelperVO> PersonList;
    public long ProductLssueID;
    public String Percent;
    public String TotalMoney;//目标金额
    public String RaiseMoney;//已筹金额
    public int RaiseCount;//捐款人数
    public String ProName;//公益名称
    public String Content;//公益介绍
    public int Status;//状态
    public List<AdvVO> advVOList;
    public int percentNum;
    public CommonwealDetailJsonVO.JoinInfo JoinInfo;
}
