package com.floyd.onebuy.biz.manager;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.floyd.onebuy.aync.ApiCallback;
import com.floyd.onebuy.aync.AsyncJob;
import com.floyd.onebuy.aync.HttpJobFactory;
import com.floyd.onebuy.biz.constants.APIConstants;
import com.floyd.onebuy.biz.func.AbstractJsonApiCallback;
import com.floyd.onebuy.biz.func.StringFunc;
import com.floyd.onebuy.biz.tools.PrefsTools;
import com.floyd.onebuy.biz.vo.json.UserVO;
import com.floyd.onebuy.channel.request.FileItem;
import com.floyd.onebuy.channel.request.HttpMethod;
import com.floyd.onebuy.ui.activity.LoginActivity;
import com.google.gson.Gson;

import org.json.JSONException;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by floyd on 16-4-28.
 */
public class LoginManager {

    public static final String LOGIN_INFO = "LOGIN_INFO";

    public static UserVO getLoginInfo(Context context) {
        String data = PrefsTools.getStringPrefs(context, LOGIN_INFO, "");
        if (TextUtils.isEmpty(data)) {
            return null;
        }

        UserVO loginVO = new UserVO();
        Gson gson = new Gson();
        loginVO = gson.fromJson(data, UserVO.class);
        return loginVO;
    }

    public static boolean isLogin(Context context) {
        UserVO loginVO = LoginManager.getLoginInfo(context);
        if (loginVO == null) {
            Intent it = new Intent(context, LoginActivity.class);
            context.startActivity(it);
            return false;
        }

        return true;
    }

    public static void saveLoginInfo(Context context, UserVO vo) {
        Gson gson = new Gson();
        String data = gson.toJson(vo);
        PrefsTools.setStringPrefs(context, LOGIN_INFO, data);
    }

    public static AsyncJob<UserVO> regUserJob(String phoneNum, String password, String code) {
        String url = APIConstants.HOST_API_PATH + APIConstants.USER_MODULE;
        Map<String, String> params = new HashMap<String, String>();
        params.put("pageType", "register");
        params.put("mobile", phoneNum);
        params.put("password", password);
        params.put("code", code);
        return JsonHttpJobFactory.getJsonAsyncJob(url, params, HttpMethod.POST, UserVO.class);
    }

    /**
     * 用户登录
     *
     * @param mobile   手机号
     * @param password 　密码
     * @return
     */
    public static AsyncJob<UserVO> login(final Context context, String mobile, String password) {
        String url = APIConstants.HOST_API_PATH + APIConstants.USER_MODULE;
        Map<String, String> params = new HashMap<String, String>();
        params.put("pageType", "login");
        params.put("mobile", mobile);
        params.put("password", password);

        final AsyncJob<String> httpJob = HttpJobFactory.createHttpJob(url, params, HttpMethod.GET).map(new StringFunc());

        AsyncJob<UserVO> targetJob = new AsyncJob<UserVO>() {
            @Override
            public void start(final ApiCallback<UserVO> callback) {

                httpJob.start(new AbstractJsonApiCallback<UserVO>(callback) {
                    @Override
                    protected UserVO convert2Obj(String data) throws JSONException {
                        Gson gson = new Gson();
                        UserVO loginVO = gson.fromJson(data, UserVO.class);
                        saveLoginInfo(context, loginVO);
                        return loginVO;
                    }
                });

            }
        };
        return targetJob;
    }

    /**
     * 修改密码
     *
     * @param mobile  　手机号码
     * @param oldPass 老密码
     * @param newPass 　新密码
     * @return
     */
    public static AsyncJob<Boolean> modifyPassword(String mobile, String oldPass, String newPass) {
        String url = APIConstants.HOST_API_PATH + APIConstants.USER_MODULE;
        Map<String, String> params = new HashMap<String, String>();
        params.put("pageType", "modifyUserPassword");
        params.put("mobile", mobile);
        params.put("oldPassword", oldPass);
        params.put("newPassword", newPass);
        return JsonHttpJobFactory.getJsonAsyncJob(url, params, HttpMethod.POST, Boolean.class);
    }

    public static AsyncJob<String> forgetPassword(String mobile, String smsCode, String newPassword) {
        String url = APIConstants.HOST_API_PATH + APIConstants.USER_MODULE;
        Map<String, String> params = new HashMap<String, String>();
        params.put("pageType", "forgetPassword");
        params.put("mobile", mobile);
        params.put("newPassword", newPassword);
        params.put("code", smsCode);
        return JsonHttpJobFactory.getJsonAsyncJob(url, params, HttpMethod.POST, String.class);
    }

    /**
     * 发送短信
     *
     * @param mobile
     * @return map key:code
     */
    public static AsyncJob<Map<String, String>> sendSms(String mobile) {
        String url = APIConstants.HOST_API_PATH + APIConstants.USER_MODULE;
        Map<String, String> params = new HashMap<String, String>();
        params.put("pageType", "sendVerifyCode");
        params.put("mobile", mobile);
        return JsonHttpJobFactory.getJsonAsyncJob(url, params, HttpMethod.POST, Map.class);
    }


    /**
     * 修改头像
     *
     * @param userId
     * @param file
     * @return
     */
    public static AsyncJob<Map<String, String>> modifyHead(long userId, File file) {
        String url = APIConstants.HOST_API_PATH + APIConstants.USER_MODULE;
        Map<String, String> params = new HashMap<String, String>();
        params.put("pageType", "upLoadUserLogo");
        params.put("userId", userId + "");
        Map<String, FileItem> files = new HashMap<String, FileItem>();
        files.put("File", new FileItem(file));
        return JsonHttpJobFactory.getJsonAsyncJob(url, params, files, HttpMethod.POST, Map.class);
    }

    /**
     * 修改个人信息
     *
     * @param userId
     * @param nickName
     * @return
     */
    public static AsyncJob<Boolean> modifyUserInfo(long userId, String nickName) {
        String url = APIConstants.HOST_API_PATH + APIConstants.USER_MODULE;
        Map<String, String> params = new HashMap<String, String>();
        params.put("pageType", "modifyUserInfo");
        params.put("nickName", nickName);
        params.put("userId", userId+"");
        return JsonHttpJobFactory.getJsonAsyncJob(url, params, HttpMethod.POST, Boolean.class);
    }

    public static AsyncJob<Boolean> checkUserMobile(String mobile, String smsCode) {
        String url = APIConstants.HOST_API_PATH + APIConstants.USER_MODULE;
        Map<String, String> params = new HashMap<String, String>();
        params.put("pageType", "checkUserMobile");
        params.put("mobile", mobile);
        return JsonHttpJobFactory.getJsonAsyncJob(url, params, HttpMethod.POST, Boolean.class);
    }
}
