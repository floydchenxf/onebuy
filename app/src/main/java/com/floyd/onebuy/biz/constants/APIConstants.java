package com.floyd.onebuy.biz.constants;

/**
 * Created by floyd on 15-11-28.
 */
public class APIConstants {
//    public static final String HOST = "http://120.27.132.126:9999/mote/";
    public static final String HOST ="http://139.196.34.98:8888/mote/";
    public static final String API_VERIFY_CODE = "api/user/sendVerifyCode";//发送验证码
    public static final String API_CHANGE_PASSWORD_VERIFY_CODE = "api/user/changePasswordByVerifyCode";//修改密码的验证码
    public static final String API_USER_LOGIN = "api/user/login";//登录
    public static final String API_REG_USER = "api/user/register";//注册
    public static final String API_MY_MOTE_INFO = "api/user/myMoteInfo";//模特信息
    public static final String API_MY_SELLER_INFO = "api/user/mySellerInfo";//卖家信息
    public static final String API_UPDATE_MONTE_AVART = "api/user/updateMoteAvart";//更新头像
    public static final String API_GET_ADVERT_LIST = "api/index/getAdvertList";//获取广告
    public static final String API_GET_MOTE_LIST = "api/index/getMoteList"; //获取首页模特信息
    public static final String API_ADD_FOLLOW = "api/follow/addFollow"; //关注模特
    public static final String API_CANCEL_FOLLOW = "api/follow/cancelFollow"; //取消关注模特
    public static final String API_ADV_DETAIL_INFO = "api/index/getAdvertDetail";//广告详情
    public static final String API_MOTE_DETAIL_INFO = "api/user/getMoteInfo";//模特详情
    public static final String API_MOTE_TASK_PICS = "api/taskPic/getMoteTaskPics";
    public static final String API_MOTE_TASK_SEARCH = "api/task/search";//任务查询
    public static final String API_NEW_MOTE_TASK = "api/task/newMoteTask"; //模特接单
    public static final String API_MOTE_MY_TASK = "api/motetask/getMyTaskList";//模特我的任务
    public static final String API_INDEX_INFO = "api/index/getIndexInfo";//获取首页导航栏目
    public static final String API_ADD_ORDER_NO = "api/task/addOrderNo"; //模特添加订单
    public static final String API_FINISH_SHOW_PIC = "api/task/finishShowPic";//模特儿完成晒图
    public static final String API_SELF_BUY = "api/task/selfBuy";//模特儿自购商品
    public static final String API_RETURN_ITEM = "api/task/returnItem";//模特儿退回商品
    public static final String API_UPLOAD_IMAGE = "api/taskPic/uploadImage"; //模特儿上传照片
    public static final String API_REMOVE_IMAGE_URL = "api/taskPic/removeImageUrl";//模特儿删除图片
    public static final String MYCARE = "api/follow/getFollowList";//我的关注
    public static final String API_TASK_PROCESS = "api/task/getMoteTaskProcess";//模特任务进程
    public static final String API_GIVE_UP_TASK = "api/task/giveUpTask";//放弃已接任务
    public static final String API_GIVE_UP_UNACCEPT_TASK = "api/task/giveUpUnAcceptTask"; //放弃未接的任务
    public static final String API_GIVE_UP_MOTE_TASK = "api/motetask/giveUpMoteTask"; //放弃模特任务
    public static final String API_AREA_LEST_BY_PID = "api/common/getAreaListByPid";//获取地址
    public static final String API_UPDATE_MOTE = "api/user/updateMote"; //更新mote信息
    public static final String API_PIC_UPVOTE = "api/taskPic/picUpVote"; //图片点赞
    public static final String API_CANCEL_PIC_UPVOTE = "api/taskPic/cancelPicUpVote"; //取消图片点赞
    public static final String API_UPDATE_MOTE_AUTHEN_INFO = "api/user/updateMoteAuthenInfo"; //更信模特验证信息
    public static final String API_COMMON_UPLOAD = "api/common/upload"; //通用图片上传
    public static final String API_MOTE_WALLET = "api/user/getMyMoteWallet"; //获取模特儿钱包
    public static final String API_REDUCE_CASH_APPLY = "api/cash/reduceCashApply";//申请提现
    public static final String API_QUERY_APPLY_LIST = "api/cash/queryApplyList"; //查询提现记录
    public static final String API_GET_DETAIL_BY_TASKID = "api/task/getDetailByTaskId";
    public static final String API_GET_MOTE_FILTER = "api/user/getMoteFilter";//获取保存的筛选条件
    public static final String API_SAVE_MOTE_FILTER = "api/user/saveMoteFilter"  ;//保存筛选条件
    public static final String CHOOSEMOTE = "api/index/filterMote";//筛选模特
    public static final String API_GET_USER_INFO= "api/user/getUserInfo";
    public static final String API_TASK_PIC_DETAIL = "api/taskPic/getTaskPicDetail"; //任务图片详情
    public static final String API_UNREAD_MSG_NUM = "api/index/getUnReadMsgNum"; //未读消息接口
    public static final String API_HAS_READ_MSG = "api/index/hasReadMsg";//已经读取

    public static final String API_KUAIDI_LIST = "api/common/getKuaidiList";//快递公司
    public static final String API_GET_EXPRESS_INFO = "api/motetask/getExpressInfo";
    //----------------------------------------------------------卖家------------------------------------------------
    public static final String API_SELLER_TASK_LIST = "api/task/getSellerTaskList"; //我的任务
    public static final String API_MOTE_TASK_APPROVE_PIC = "api/motetask/approvePic"; //商家评价图片
    public static final String API_SELLER_TASK_PICS = "api/taskPic/getSellerTaskPics"; //商家我的图库
    public static final String API_SELLER_TASK_LIST_DETAIL = "api/task/getMoteList4APP"; //我的任务详情
    public static final String API_SLLER_FINISH_AND_APPROVE_TASK = "api/task/finishAndApproveMoteTask";//确认并评价任务

    public static final String API_UPDATE_SELLER_INFO = "api/user/updateSeller";//更新卖家信息
    public static final String API_GET_SELLER_WALLET = "api/user/getMySellerWallet"; //获取商家钱包
    public static final String API_USER_SUGGESTION = "api/user/suggestion";//提建议
    public static final String API_GET_FEE_CHANGE_FLOW = "api/user/getFeeChangeFlow"; //获取费用变更记录
}
