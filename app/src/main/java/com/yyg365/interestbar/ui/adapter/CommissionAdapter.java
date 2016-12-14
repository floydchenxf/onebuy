package com.yyg365.interestbar.ui.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.yyg365.interestbar.biz.tools.DateUtil;
import com.yyg365.interestbar.biz.vo.json.CommissionItemVO;
import com.yyg365.interestbar.ui.R;

import java.util.List;
import java.util.Map;

/**
 * Created by chenxiaofeng on 16/10/27.
 */
public class CommissionAdapter extends BaseDataAdapter<CommissionItemVO> {

    public CommissionAdapter(Context context, List<CommissionItemVO> records) {
        super(context, records);
    }

    @Override
    View getLayoutView() {
        return View.inflate(mContext, R.layout.commission_item, null);
    }

    @Override
    int[] cacheViews() {
        return new int[]{R.id.remark_view, R.id.record_time_view, R.id.commission_rate_view};
    }

    @Override
    void processHolder(Map<Integer, View> holder, CommissionItemVO vo) {
        TextView remarkView = (TextView) holder.get(R.id.remark_view);
        TextView recordTimeView = (TextView) holder.get(R.id.record_time_view);
        TextView commissionRateView = (TextView) holder.get(R.id.commission_rate_view);

        remarkView.setText(vo.Remark);
        String time = DateUtil.getDateTime(vo.getRecordTime());
        recordTimeView.setText(time);
        commissionRateView.setText(vo.Commission + "");

    }
}
