package com.yyg365.interestbar.ui;

import android.content.Context;
import android.util.Log;

import com.yyg365.interestbar.biz.vo.IKeepClassForProguard;
import com.tencent.android.tpush.XGPushBaseReceiver;
import com.tencent.android.tpush.XGPushClickedResult;
import com.tencent.android.tpush.XGPushRegisterResult;
import com.tencent.android.tpush.XGPushShowedResult;
import com.tencent.android.tpush.XGPushTextMessage;

import java.util.Map;

/**
 * Created by floyd on 16-1-10.
 */
public class MessageReceiver extends XGPushBaseReceiver {
    private static final String TAG = "MessageReceiver";
    @Override
    public void onRegisterResult(Context context, int i, XGPushRegisterResult xgPushRegisterResult) {

        Log.i(TAG, "--------------onRegisterResult");
    }

    @Override
    public void onUnregisterResult(Context context, int i) {
        Log.i(TAG, "--------------onUnregisterResult");
    }

    @Override
    public void onSetTagResult(Context context, int i, String s) {
        Log.i(TAG, "--------------onSetTagResult");
    }

    @Override
    public void onDeleteTagResult(Context context, int i, String s) {
        Log.i(TAG, "--------------onDeleteTagResult");
    }

    @Override
    public void onTextMessage(Context context, XGPushTextMessage xgPushTextMessage) {
        Log.i(TAG, "-----------receive msg "+xgPushTextMessage);
        String title = xgPushTextMessage.getTitle();
        String content = xgPushTextMessage.getContent();
        String customContent = xgPushTextMessage.getCustomContent();
//        NoticeVO noticeVO = new NoticeVO();
//        noticeVO.vibrate = true;
//        noticeVO.tip = true;
//        noticeVO.title = title;
//        noticeVO.content = content;
//        OpenAppVO openAppVO = new OpenAppVO();
//        PushMsgVO pushMsgVO = new Gson().fromJson(customContent, PushMsgVO.class);
//        openAppVO.notifyType = pushMsgVO.msgType;
//        openAppVO.params = pushMsgVO.ct;
//        noticeVO.openAppVO = openAppVO;
//        PushNotificationManager.getInstance(context).showNotification(noticeVO);
    }

    @Override
    public void onNotifactionClickedResult(Context context, XGPushClickedResult xgPushClickedResult) {
        Log.i(TAG, "--------------onNotifactionClickedResult");
    }

    @Override
    public void onNotifactionShowedResult(Context context, XGPushShowedResult notifiShowedRlt) {

        if (context == null || notifiShowedRlt == null) {
            return;
        }


        notifiShowedRlt.getContent();
        notifiShowedRlt.getCustomContent();
    }

    public class PushMsgVO implements IKeepClassForProguard {
        public long td;
        public int msgType;
        public Map<String, String> ct;
    }
}
