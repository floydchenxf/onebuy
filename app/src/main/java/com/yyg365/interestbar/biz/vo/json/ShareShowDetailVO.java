package com.yyg365.interestbar.biz.vo.json;

import android.text.TextUtils;

import com.yyg365.interestbar.biz.constants.APIConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenxiaofeng on 16/9/3.
 */
public class ShareShowDetailVO {

    public Long ProductLssueID;
    public String Pictures;
    public String GuestTitle;
    public String GuestContent;
    public Long ProID;
    public String ProPics;
    public String GuestName;
    public String GuestPic;
    public Long GuestTime;
    public String GuestID;
    public int isVerify;//状态
    public int TypeBook;
    public String MediaUrl;//地址
    public int CommentNum;
    public List<ShowCommentVO> ShowComment;

    public List<String> getMediaUrls() {
        List<String> result = new ArrayList<String>();
        if (!TextUtils.isEmpty(MediaUrl)) {
            String[] urls = MediaUrl.split("\\|");
            for (String s : urls) {
                result.add(APIConstants.HOST + s);
            }
        }
        return result;
    }

    public boolean isPic() {
        return this.TypeBook == APIConstants.SHARE_SHOW_PIC_TYPE;
    }
}
