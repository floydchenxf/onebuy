package com.floyd.onebuy.ui.adapter;

import android.content.Context;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.floyd.onebuy.R;
import com.floyd.onebuy.biz.vo.fee.WinningRecordVO;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by floyd on 16-4-14.
 */
public class WinningRecordAdapter extends BaseAdapter {

    private List<WinningRecordVO> winningRecordList = new ArrayList<WinningRecordVO>();
    private Context mContext;
    private ImageLoader mImageLoader;

    public WinningRecordAdapter(Context context, ImageLoader imageLoader, List<WinningRecordVO> args) {
        this.winningRecordList.addAll(args);
        this.mContext = context;
        this.mImageLoader = imageLoader;
    }

    public void add(WinningRecordVO k) {
        this.winningRecordList.add(k);
        this.notifyDataSetChanged();
    }

    public void addAll(List<WinningRecordVO> fees, boolean needClear) {
        if (needClear) {
            this.winningRecordList.clear();
        }
        this.winningRecordList.addAll(fees);
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return this.winningRecordList.size();
    }

    @Override
    public WinningRecordVO getItem(int position) {
        return this.winningRecordList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public List<WinningRecordVO> getFeeRecords() {
        return this.winningRecordList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.winning_record_adapter, null);
            holder = new ViewHolder();
            holder.productImageView = (NetworkImageView) convertView.findViewById(R.id.product_image_view);
            holder.leftTipView = (TextView) convertView.findViewById(R.id.left_tip_view);
            holder.titleView = (TextView) convertView.findViewById(R.id.product_title_view);
            holder.currentTipView = (TextView) convertView.findViewById(R.id.current_tip_view);
            holder.totalTipView = (TextView) convertView.findViewById(R.id.needd_tip_view);
            holder.addButtonView = (CheckedTextView) convertView.findViewById(R.id.add_button);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        WinningRecordVO vo = getItem(position);
        holder.productImageView.setImageUrl(vo.productImage, mImageLoader);
        holder.currentTipView.setText(Html.fromHtml("本期夺宝：<font color=\"red\">" + vo.currentTips + "</font>"));
        holder.totalTipView.setText("总需" + vo.needTips + "人次");
        holder.titleView.setText(vo.productTitle);
        holder.leftTipView.setText("剩余" + vo.leftTips);
        return convertView;
    }

    public static class ViewHolder {
        public NetworkImageView productImageView;
        public TextView titleView;
        public TextView currentTipView;
        public TextView totalTipView;
        public TextView leftTipView;
        public CheckedTextView addButtonView;
    }
}
