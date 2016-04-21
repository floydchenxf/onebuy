package com.floyd.onebuy.biz.manager;

import com.floyd.onebuy.aync.AsyncJob;
import com.floyd.onebuy.biz.constants.APIConstants;
import com.floyd.onebuy.biz.vo.user.RegUserVO;
import com.floyd.onebuy.biz.vo.user.UserVO;
import com.floyd.onebuy.channel.request.HttpMethod;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by floyd on 16-4-21.
 */
public class UserManager {

    /**
     * 注册用户
     *
     * @param mobile   手机号码
     * @param password 　密码
     * @param userType 　用户类型 1 app 2　微信
     * @return
     */
    public static AsyncJob<RegUserVO> regUser(String mobile, String password, int userType) {
        String url = APIConstants.HOST + APIConstants.USER_MODULE;
        Map<String, String> params = new HashMap<String, String>();
        params.put("pageType", "register");
        params.put("mobile", mobile);
        params.put("password", password);
        params.put("userType", userType + "");
        return JsonHttpJobFactory.getJsonAsyncJob(url, params, HttpMethod.POST, RegUserVO.class);
    }

    /**
     * 用户登录
     *
     * @param mobile   手机号
     * @param password 　密码
     * @return
     */
    public static AsyncJob<UserVO> login(String mobile, String password) {
        String url = APIConstants.HOST + APIConstants.USER_MODULE;
        Map<String, String> params = new HashMap<String, String>();
        params.put("pageType", "login");
        params.put("mobile", mobile);
        params.put("password", password);
        return JsonHttpJobFactory.getJsonAsyncJob(url, params, HttpMethod.POST, UserVO.class);
    }

    /**
     * 修改密码
     *
     * @param mobile  　手机号码
     * @param oldPass 老密码
     * @param newPass 　新密码
     * @return
     */
    public AsyncJob<Boolean> modifyPassword(String mobile, String oldPass, String newPass) {
        String url = APIConstants.HOST + APIConstants.USER_MODULE;
        Map<String, String> params = new HashMap<String, String>();
        params.put("pageType", "modifyUserPassword");
        params.put("mobile", mobile);
        params.put("oldPassword", oldPass);
        params.put("newPassword", newPass);
        return JsonHttpJobFactory.getJsonAsyncJob(url, params, HttpMethod.POST, Boolean.class);
    }

    public AsyncJob<Boolean> forgetPassword(String mobile, String smsCode, String newPassword) {
        return null;
    }


}
