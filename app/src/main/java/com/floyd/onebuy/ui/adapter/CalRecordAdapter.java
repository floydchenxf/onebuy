package com.floyd.onebuy.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.floyd.onebuy.biz.vo.json.CalItemVO;
import com.floyd.onebuy.ui.R;

import java.util.List;
import java.util.Map;

/**
 * Created by chenxiaofeng on 16/10/22.
 */
public class CalRecordAdapter extends BaseDataAdapter<CalItemVO> {

    public CalRecordAdapter(Context context, List<CalItemVO> records) {
        super(context, records);
    }

    @Override
    View getLayoutView() {
        return View.inflate(mContext, R.layout.cal_record_item, null);
    }

    @Override
    int[] cacheViews() {
        return new int[]{R.id.day_time_view, R.id.hour_time_view, R.id.time_factor_view, R.id.client_name_view};
    }

    @Override
    void processHolder(Map<Integer, View> holder, CalItemVO vo) {

        TextView dayTimeView = (TextView) holder.get(R.id.day_time_view);
        TextView hourTimeView = (TextView) holder.get(R.id.hour_time_view);
        TextView timeFactorView = (TextView) holder.get(R.id.time_factor_view);
        TextView clientNameView = (TextView) holder.get(R.id.client_name_view);

        dayTimeView.setText(vo.DayTime);
        hourTimeView.setText(vo.HourTime);
        timeFactorView.setText(vo.TimeFactor);
        clientNameView.setText(vo.ClientName);
    }
}
