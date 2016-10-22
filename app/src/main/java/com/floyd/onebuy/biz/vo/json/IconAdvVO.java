package com.floyd.onebuy.biz.vo.json;

import com.floyd.onebuy.utils.CommonUtil;

import java.io.Serializable;

/**
 * Created by chenxiaofeng on 16/10/22.
 * <p>
 * 首页广告
 */
public class IconAdvVO implements Serializable {
    public String Pic;//广告图片
    public String Url;//跳转url
    public long ProID;//产品ID

    public String getPic() {
        return CommonUtil.getImageUrl(Pic);
    }
}
