package com.yyg365.interestbar.biz.vo.product;

import android.text.TextUtils;

import com.yyg365.interestbar.biz.constants.APIConstants;

/**
 * Created by floyd on 16-4-17.
 */
public class JoinVO {
    public long ProductLssueID; //期数
    public String ClientIP; //客户端ip
    public String ClientID; //用户id
    public String ClientName; //用户名称
    public String ClientPic; //用户头像
    public int Number; //参与次数
    public String InTime;//加入时间

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
