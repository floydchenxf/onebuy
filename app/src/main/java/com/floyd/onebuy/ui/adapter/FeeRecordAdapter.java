package com.floyd.onebuy.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.floyd.onebuy.R;
import com.floyd.onebuy.biz.tools.DateUtil;
import com.floyd.onebuy.biz.vo.fee.FeeRecordVO;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by floyd on 16-4-14.
 */
public class FeeRecordAdapter extends BaseAdapter {

    private List<FeeRecordVO> feeRecordVOList = new ArrayList<FeeRecordVO>();
    private Context mContext;
    private LayoutInflater infalter;

    public FeeRecordAdapter(Context context, List<FeeRecordVO> args) {
        infalter = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        feeRecordVOList.addAll(args);
        mContext = context;
    }

    public void add(FeeRecordVO k) {
        feeRecordVOList.add(k);
        this.notifyDataSetChanged();
    }

    public void addAll(List<FeeRecordVO> fees, boolean needClear) {
        if (needClear) {
            feeRecordVOList.clear();
        }
        feeRecordVOList.addAll(fees);
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return feeRecordVOList.size();
    }

    @Override
    public FeeRecordVO getItem(int position) {
        return feeRecordVOList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public List<FeeRecordVO> getFeeRecords() {
        return this.feeRecordVOList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = infalter.inflate(R.layout.fee_record_adapter, null);
            holder = new ViewHolder();
            holder.feeView = (TextView)convertView.findViewById(R.id.fee_view);
            holder.orderNoView = (TextView) convertView.findViewById(R.id.order_no_view);
            holder.sourceView = (TextView) convertView.findViewById(R.id.source_view);
            holder.dateView = (TextView) convertView.findViewById(R.id.date_view);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        FeeRecordVO vo  = getItem(position);
        holder.feeView.setText(vo.fee);
        holder.orderNoView.setText(vo.orderNo);
        holder.sourceView.setText(vo.source);
        String dateStr = DateUtil.getDateTime(vo.time);
        holder.dateView.setText(dateStr);
        return convertView;
    }

    public static class ViewHolder {

        public TextView feeView;
        public TextView orderNoView;
        public TextView sourceView;
        public TextView dateView;

    }
}
