package com.floyd.onebuy.biz.vo.json;

import com.floyd.onebuy.utils.CommonUtil;

/**
 * Created by chenxiaofeng on 16/9/26.
 */
public class CommonwealAdvVO {
    public String Title;
    public String Pic;
    public Long FoundationID;
    public String Url;

    public String getPic() {
        return CommonUtil.getImageUrl(this.Pic);
    }
}
