package com.yyg365.interestbar.biz.manager;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.yyg365.interestbar.aync.ApiCallback;
import com.yyg365.interestbar.aync.AsyncJob;
import com.yyg365.interestbar.aync.Func;
import com.yyg365.interestbar.aync.HttpJobFactory;
import com.yyg365.interestbar.aync.Processor;
import com.yyg365.interestbar.bean.MD5Util;
import com.yyg365.interestbar.biz.constants.APIConstants;
import com.yyg365.interestbar.biz.func.AbstractJsonApiCallback;
import com.yyg365.interestbar.biz.func.StringFunc;
import com.yyg365.interestbar.biz.tools.PrefsTools;
import com.yyg365.interestbar.biz.vo.json.CommissionVO;
import com.yyg365.interestbar.biz.vo.json.InviteFriendRecordVO;
import com.yyg365.interestbar.biz.vo.json.InviteVO;
import com.yyg365.interestbar.biz.vo.json.MsgItemVO;
import com.yyg365.interestbar.biz.vo.json.ProductLssueVO;
import com.yyg365.interestbar.biz.vo.json.UserVO;
import com.yyg365.interestbar.channel.request.FileItem;
import com.yyg365.interestbar.channel.request.HttpMethod;
import com.yyg365.interestbar.ui.activity.LoginActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;

import java.io.File;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by floyd on 16-4-28.
 */
public class LoginManager {

    public static final String LOGIN_INFO = "LOGIN_INFO";
    public static final String DEVICE_TOKEN_INFO = "DEVICE_TOKEN_INFO";
    public static final String LASTTEST_COUNT = "LASTTEST_COUNT";

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
        PrefsTools.setStringPrefs(context, LASTTEST_COUNT, vo.Mobile);
        PrefsTools.setStringPrefs(context, LOGIN_INFO, data);
    }

    public static void saveDeviceId(Context context, String deviceId) {
        PrefsTools.setStringPrefs(context, DEVICE_TOKEN_INFO, deviceId);
    }

    public static String getDeviceId(Context context) {
        return PrefsTools.getStringPrefs(context, DEVICE_TOKEN_INFO, "0000");
    }

    public static String getLasttestCount(Context context) {
        return PrefsTools.getStringPrefs(context, LASTTEST_COUNT);
    }

    /**
     * 注册用户
     *
     * @param phoneNum
     * @param password
     * @param code
     * @return
     */
    public static AsyncJob<UserVO> regUserJob(String phoneNum, String password, String code, String deviceToken) {
        String url = APIConstants.HOST_API_PATH + APIConstants.USER_MODULE;
        Map<String, String> params = new HashMap<String, String>();
        params.put("pageType", "register");
        params.put("mobile", phoneNum);
        String md5Pass = MD5Util.encodeBy32BitMD5(password);
        params.put("password", md5Pass == null ? "" : md5Pass.toLowerCase());
        params.put("code", code);
        params.put("deviceToken", deviceToken);
        return JsonHttpJobFactory.getJsonAsyncJob(url, params, HttpMethod.POST, UserVO.class);
    }

    /**
     * 获取用户基本信息
     *
     * @return
     */
    public static AsyncJob<UserVO> fetchUserInfo(Long userId) {
        String url = APIConstants.HOST_API_PATH + APIConstants.USER_MODULE;
        Map<String, String> params = new HashMap<String, String>();
        params.put("pageType", "getUserInfoByUserID");
        params.put("userId", userId + "");
        AsyncJob<UserVO> result = JsonHttpJobFactory.getJsonAsyncJob(url, params, HttpMethod.POST, UserVO.class);
        return result;
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
        String md5Pass = MD5Util.encodeBy32BitMD5(password);
        params.put("deviceToken", getDeviceId(context));
        params.put("password", md5Pass == null ? "" : md5Pass.toLowerCase());

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
        params.put("oldPassword", MD5Util.encodeBy32BitMD5(oldPass).toLowerCase());
        params.put("newPassword", MD5Util.encodeBy32BitMD5(newPass).toLowerCase());
        return JsonHttpJobFactory.getJsonAsyncJob(url, params, HttpMethod.POST, Boolean.class);
    }

    public static AsyncJob<String> forgetPassword(String mobile, String smsCode, String newPassword) {
        String url = APIConstants.HOST_API_PATH + APIConstants.USER_MODULE;
        Map<String, String> params = new HashMap<String, String>();
        params.put("pageType", "forgetPassword");
        params.put("mobile", mobile);
        params.put("newPassword", MD5Util.encodeBy32BitMD5(newPassword).toLowerCase());
        params.put("code", smsCode);
        return JsonHttpJobFactory.getJsonAsyncJob(url, params, HttpMethod.POST, String.class);
    }

    public static AsyncJob<Long> forgetPaswordStep1(String mobile, String smsCode) {
        String url = APIConstants.HOST_API_PATH + APIConstants.USER_MODULE;
        Map<String, String> params = new HashMap<String, String>();
        params.put("pageType", "forgetPasswordstep1");
        params.put("mobile", mobile);
        params.put("code", smsCode);
        Type type = new TypeToken<Map<String, Long>>() {
        }.getType();
        AsyncJob<Map<String, Long>> job = JsonHttpJobFactory.getJsonAsyncJob(url, params, HttpMethod.POST, type);
        return job.map(new Func<Map<String, Long>, Long>() {
            @Override
            public Long call(Map<String, Long> map) {
                return map.get("ID");
            }
        });
    }

    public static AsyncJob<Boolean> forgetPaswordStep2(Long userId, String newPasword) {
        String url = APIConstants.HOST_API_PATH + APIConstants.USER_MODULE;
        Map<String, String> params = new HashMap<String, String>();
        params.put("pageType", "forgetPasswordstep2");
        params.put("userId", userId + "");
        String md5Pass = MD5Util.encodeBy32BitMD5(newPasword).toLowerCase();
        params.put("newPassword", md5Pass);
        return JsonHttpJobFactory.getJsonAsyncJob(url, params, HttpMethod.GET, Boolean.class);
    }


    /**
     * 发送短信
     *
     * @param mobile
     * @return map key:code
     */
    public static AsyncJob<Long> sendSms(String mobile) {
        String url = APIConstants.HOST_API_PATH + APIConstants.USER_MODULE;
        Map<String, String> params = new HashMap<String, String>();
        params.put("pageType", "sendVerifyCode");
        params.put("mobile", mobile);

        Type type = new TypeToken<Map<String, Long>>() {
        }.getType();

        AsyncJob<Map<String, Long>> job = JsonHttpJobFactory.getJsonAsyncJob(url, params, HttpMethod.POST, type);
        return job.map(new Func<Map<String, Long>, Long>() {
            @Override
            public Long call(Map<String, Long> map) {
                return map.get("Code");
            }
        });
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
        FileItem fileItem = new FileItem(file, "image/jpeg");
        files.put("File", fileItem);
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
        params.put("userId", userId + "");
        return JsonHttpJobFactory.getJsonAsyncJob(url, params, HttpMethod.POST, Boolean.class);
    }


    public static AsyncJob<Boolean> bindClientMobile(Long userId, String mobile, String smsCode) {
        String url = APIConstants.HOST_API_PATH + APIConstants.USER_MODULE;
        Map<String, String> params = new HashMap<String, String>();
        params.put("pageType", "bindClientMobile");
        params.put("userId", userId + "");
        params.put("mobile", mobile);
        params.put("code", smsCode);
        return JsonHttpJobFactory.getJsonAsyncJob(url, params, HttpMethod.POST, Boolean.class);
    }

    /**
     * 获取邀请信息
     *
     * @param userId
     * @return
     */
    public static AsyncJob<InviteVO> fetchInviteInfo(Long userId) {
        String url = APIConstants.HOST_API_PATH + APIConstants.USER_MODULE;
        Map<String, String> params = new HashMap<String, String>();
        params.put("pageType", "genInviteCode");
        params.put("userId", userId + "");
        return JsonHttpJobFactory.getJsonAsyncJob(url, params, HttpMethod.POST, InviteVO.class);

    }

    public static AsyncJob<List<InviteFriendRecordVO>> getMyInvite(Long userId, int pageNo, int pageSize) {
        String url = APIConstants.HOST_API_PATH + APIConstants.USER_MODULE;
        Map<String, String> params = new HashMap<String, String>();
        params.put("pageType", "myInvite");
        params.put("userId", userId + "");
        params.put("pageNum", pageNo + "");
        params.put("pageSize", pageSize + "");
        Type type = new TypeToken<List<InviteFriendRecordVO>>() {
        }.getType();
        return JsonHttpJobFactory.getJsonAsyncJob(url, params, HttpMethod.POST, type);
    }


    /**
     * 获取佣金信息
     *
     * @param userId
     * @param type
     * @param pageNo
     * @param pageSize
     * @return
     */
    public static AsyncJob<CommissionVO> fetchUserCommission(Long userId, int type, int pageNo, int pageSize) {
        String url = APIConstants.HOST_API_PATH + APIConstants.USER_MODULE;
        Map<String, String> params = new HashMap<String, String>();
        params.put("pageType", "myCommissionLog");
        params.put("userId", userId + "");
        params.put("type", type+"");
        params.put("pageNum", pageNo + "");
        params.put("pageSize", pageSize + "");
        return JsonHttpJobFactory.getJsonAsyncJob(url, params, HttpMethod.POST, CommissionVO.class);
    }

    public static AsyncJob<List<MsgItemVO>> fetchUserMsgs(Long userId, int type, int pageNo, int pageSize) {
        String url = APIConstants.HOST_API_PATH + APIConstants.USER_MODULE;
        Map<String, String> params = new HashMap<String, String>();
        params.put("pageType", "messagelist");
        params.put("userId", userId + "");
        params.put("type", type+"");
        params.put("pageNum", pageNo + "");
        params.put("pageSize", pageSize + "");
        Type resultType = new TypeToken<Map<String, Object>>(){}.getType();
        AsyncJob<Map<String, Object>> job = JsonHttpJobFactory.getJsonAsyncJob(url, params, HttpMethod.POST, resultType);

        return job.map(new Func<Map<String, Object>, List<MsgItemVO>>() {
            @Override
            public List<MsgItemVO> call(Map<String, Object> map) {
                Object a =  map.get("list");
                Gson gson = GsonHelper.getGson();
                Type listType = new TypeToken<List<MsgItemVO>>(){}.getType();
                return gson.fromJson(gson.toJson(a), listType);
            }
        });
    }


}
