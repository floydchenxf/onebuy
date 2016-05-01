package com.floyd.onebuy.biz.vo.json;

import android.text.TextUtils;

import com.floyd.onebuy.biz.constants.APIConstants;

/**
 * Created by floyd on 16-4-21.
 */
public class UserVO {
    public long ID; //用户id
    public String Mobile; //手机号码
    public String Name; //用户名称
    public long RegTime;//注册时间
    public String Pic;//头像
    public long LastTime;//上次访问时间


    public String getFullPic() {
        return APIConstants.HOST + Pic;
    }

    public String getUserName() {
        if (!TextUtils.isEmpty(this.Name)) {
            return this.Name;
        }

        return this.Mobile;
    }
}
