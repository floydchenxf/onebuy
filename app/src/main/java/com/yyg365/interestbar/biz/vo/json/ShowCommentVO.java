package com.yyg365.interestbar.biz.vo.json;

import android.text.TextUtils;

import com.yyg365.interestbar.biz.constants.APIConstants;

/**
 * Created by chenxiaofeng on 16/9/3.
 */
public class ShowCommentVO {

    public long CommentID;
    public String Comment;
    public Long ClientID;
    public String ClientName;
    public String ClientPic;
    public Long AddTime;
    public String Ip;

    public String getPicUrl() {
        String result = "";
        if (!TextUtils.isEmpty(this.ClientPic) && !this.ClientPic.startsWith("http")) {
            result = APIConstants.HOST + this.ClientPic;
        }

        return result;
    }
}
