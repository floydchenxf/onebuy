package com.yyg365.interestbar.biz.vo.commonweal;

import android.text.TextUtils;

import com.yyg365.interestbar.biz.constants.APIConstants;

/**
 * Created by floyd on 16-7-30.
 */
public class CommonwealHelperVO {
    public Long ClientID; //用户id
    public String ClientName; //用户名称
    public String ClientPic; //用户头像
    public String Money;

    public String getClientPic() {
        if (TextUtils.isEmpty(ClientPic)) {
            return null;
        }

        if (ClientPic.startsWith("http")) {
            return ClientPic;
        }

        return APIConstants.HOST + ClientPic;
    }
}
