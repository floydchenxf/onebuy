package com.yyg365.interestbar.biz.vo.json;

/**
 * Created by chenxiaofeng on 16/9/24.
 */
public class NewsVO {

    public Long ID;
    public String Title; //标题
    public Long PublishTime; //发布时间

    public Long getPublishTime() {
        return this.PublishTime * 1000;
    }
}
