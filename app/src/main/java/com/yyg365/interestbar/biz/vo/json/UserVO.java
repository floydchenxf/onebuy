package com.yyg365.interestbar.biz.vo.json;

import android.text.TextUtils;

import com.yyg365.interestbar.biz.constants.APIConstants;
import com.yyg365.interestbar.biz.constants.ClientLevelEnum;

import java.io.File;

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
    public int JiFen; //积分
    public double Amount;//余额
    public int ClientLevel; //等级
    public Double Commission; //佣金
    public String Token; //Token
    public String InviterCode;//邀请code
    public String ClientLevelName;
    public int UnreadMessageNum;

    public String getFullPic() {
        if (Pic != null && Pic.startsWith(File.separator)) {
            return APIConstants.HOST + Pic.substring(1);
        } else {
            return APIConstants.HOST + Pic;
        }
    }

    public String getUserName() {
        if (!TextUtils.isEmpty(this.Name)) {
            return this.Name;
        }

        return this.Mobile;
    }

    public String getLevel() {
        if (this.ClientLevel < 1 || this.ClientLevel > 8) {
            return "";
        }

        ClientLevelEnum level = ClientLevelEnum.values()[this.ClientLevel - 1];
        return level.getDesc();
    }
}
