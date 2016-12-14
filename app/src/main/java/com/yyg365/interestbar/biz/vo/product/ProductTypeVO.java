package com.yyg365.interestbar.biz.vo.product;

import com.yyg365.interestbar.biz.constants.APIConstants;

/**
 * Created by floyd on 16-4-21.
 * 产品分类
 */
public class ProductTypeVO {
    public Long CodeID;
    public String TypePic;//分类图片
    public String CodeName;//品类名称

    public String getTypePic() {
        return APIConstants.HOST + TypePic;
    }
}
