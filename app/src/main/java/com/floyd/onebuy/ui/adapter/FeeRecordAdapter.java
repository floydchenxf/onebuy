package com.floyd.onebuy.ui.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.floyd.onebuy.biz.tools.DateUtil;
import com.floyd.onebuy.biz.vo.fee.FeeRecordVO;
import com.floyd.onebuy.ui.R;

import java.util.List;
import java.util.Map;

/**
 * Created by floyd on 16-4-14.
 */
public class FeeRecordAdapter extends BaseDataAdapter<FeeRecordVO> {
    public FeeRecordAdapter(Context context, List<FeeRecordVO> records) {
        super(context, records);
    }

    @Override
    View getLayoutView() {
        return View.inflate(mContext, R.layout.fee_record_adapter, null);
    }

    @Override
    int[] cacheViews() {
        return new int[]{R.id.fee_view, R.id.order_no_view, R.id.source_view, R.id.date_view};
    }

    @Override
    void processHolder(Map holder, FeeRecordVO vo) {
        TextView feeView = (TextView) holder.get(R.id.fee_view);
        TextView orderNoView = (TextView) holder.get(R.id.order_no_view);
        TextView sourceView = (TextView) holder.get(R.id.source_view);
        TextView dateView = (TextView) holder.get(R.id.date_view);

        feeView.setText(vo.fee);
        orderNoView.setText(vo.orderNo);
        sourceView.setText(vo.source);
        String dateStr = DateUtil.getDateTime(vo.time);
        dateView.setText(dateStr);
    }

}
