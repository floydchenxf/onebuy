package com.floyd.onebuy.biz.vo.json;

import android.text.TextUtils;

import com.floyd.onebuy.biz.constants.APIConstants;

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
