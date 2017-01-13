package com.yyg365.interestbar.biz.vo.json;

import com.yyg365.interestbar.utils.CommonUtil;

/**
 * Created by chenxiaofeng on 2017/1/13.
 */
public class JFGoodsDetailVO {

    public Long ID;
    public String Name;
    public String SmallTitle;
    public String FirstPicture;
    public String Pictures;//逗号分隔，多图
    public Long JiFen; //积分
    public int ProType; //产品类型
    public int Status;
    public int IsSJ;
    public Long PublishTime;//精确到秒
    public String ContentUrl; //内容url

    public String getFirstPicUrl() {
        return CommonUtil.getImageUrl(this.FirstPicture);
    }

    public String getContentUrl() {
        return CommonUtil.getImageUrl(this.ContentUrl);
    }

}
