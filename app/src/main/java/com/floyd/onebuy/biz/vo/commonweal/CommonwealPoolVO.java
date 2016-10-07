package com.floyd.onebuy.biz.vo.commonweal;

import android.text.TextUtils;

import com.floyd.onebuy.biz.constants.APIConstants;
import com.floyd.onebuy.biz.tools.DateUtil;

/**
 * Created by chenxiaofeng on 16/10/7.
 */
public class CommonwealPoolVO {

    public Long ClientID; //用户id
    public String ClientName; //用户名称
    public String ClientPic; //用户头像
    public String OrderNum;
    public String ClientIP;
    public String Money;
    public long PayTime;//支付时间
    public String Remark;//评论

    public String getClientPic() {
        if (TextUtils.isEmpty(ClientPic)) {
            return null;
        }

        if (ClientPic.startsWith("http")) {
            return ClientPic;
        }

        return APIConstants.HOST + ClientPic;
    }

    public String getPayTime() {
        String dateString = DateUtil.getDateTime(this.PayTime * 1000);
        return dateString;
    }
}
