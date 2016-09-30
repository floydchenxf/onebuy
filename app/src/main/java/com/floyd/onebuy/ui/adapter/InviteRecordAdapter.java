package com.floyd.onebuy.ui.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.floyd.onebuy.biz.tools.DateUtil;
import com.floyd.onebuy.biz.vo.json.InviteFriendRecordVO;
import com.floyd.onebuy.ui.R;

import java.util.List;
import java.util.Map;

/**
 * Created by chenxiaofeng on 16/9/30.
 */
public class InviteRecordAdapter extends BaseDataAdapter<InviteFriendRecordVO> {

    public InviteRecordAdapter(Context context, List<InviteFriendRecordVO> records) {
        super(context, records);
    }

    @Override
    View getLayoutView() {
        return View.inflate(mContext, R.layout.invite_record_item, null);
    }

    @Override
    int[] cacheViews() {
        return new int[]{R.id.phone_view, R.id.client_name_view, R.id.record_status_view, R.id.record_status_time_view, R.id.record_time_view};
    }

    @Override
    void processHolder(Map<Integer, View> holder, InviteFriendRecordVO inviteFriendRecordVO) {
        TextView phoneView = (TextView) holder.get(R.id.phone_view);
        TextView clientNameView = (TextView) holder.get(R.id.client_name_view);
        TextView recordStatusView = (TextView) holder.get(R.id.record_status_view);
        TextView recordTimeView = (TextView) holder.get(R.id.record_time_view);
        TextView recordStatusTimeView = (TextView) holder.get(R.id.record_status_time_view);

        phoneView.setText(inviteFriendRecordVO.Phone);
        clientNameView.setText(inviteFriendRecordVO.ClientName);
        String status = "未注册";
        if (inviteFriendRecordVO.Status == 1) {
            status = "已注册";
        }
        recordStatusView.setText(status);

        String recordTime = DateUtil.getDateTime(inviteFriendRecordVO.getRecordTime());
        recordTimeView.setText(recordTime);

        String statusTime = DateUtil.getDateTime(inviteFriendRecordVO.getStatusTime());
        recordStatusTimeView.setText(statusTime);
    }
}
