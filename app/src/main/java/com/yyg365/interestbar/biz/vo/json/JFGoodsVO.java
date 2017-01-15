package com.yyg365.interestbar.biz.vo.json;

import com.yyg365.interestbar.utils.CommonUtil;

/**
 * Created by chenxiaofeng on 2017/1/11.
 */
public class JFGoodsVO {
    public Long ID;
    public String Name;
    public String SmallTitle;
    public String FirstPicture;
    public Integer JiFen;
    public Long PublishTime;

    public Long PayJiFen;
    public Long OrderTime;
    public int IsSend;//是否发送

    public String getPicUrl() {
        return CommonUtil.getImageUrl(this.FirstPicture);
    }

}
