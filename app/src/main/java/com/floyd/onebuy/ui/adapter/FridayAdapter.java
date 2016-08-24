package com.floyd.onebuy.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.floyd.onebuy.biz.vo.model.WinningInfo;
import com.floyd.onebuy.ui.R;
import com.floyd.onebuy.ui.activity.WinningDetailActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by floyd on 16-4-24.
 */
public class FridayAdapter extends BaseAdapter {
    private Context mContext;
    private ImageLoader mImageLoader;
    private List<WinningInfo> records = new ArrayList<WinningInfo>();


    public FridayAdapter(Context context, List<WinningInfo> args, ImageLoader imageLoader) {
        this.mContext = context;
        if (args != null && !args.isEmpty()) {
            this.records.addAll(args);
        }
        this.mImageLoader = imageLoader;
    }

    public void addAll(List<WinningInfo> records, boolean needClear) {
        if (needClear) {
            this.records.clear();
        }
        this.records.addAll(records);
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return records.size() % 2 == 0 ? records.size() / 2 : records.size() / 2 + 1;
    }

    @Override
    public WinningInfo getItem(int position) {
        return records.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.friday_item, null);
            viewHolder = new ViewHolder();
            viewHolder.layout1 = convertView.findViewById(R.id.product_item_1);
            viewHolder.layout2 = convertView.findViewById(R.id.product_item_2);
            viewHolder.addBuyListView1 = (TextView) convertView.findViewById(R.id.add_buy_list_view_1);
            viewHolder.addBuyListView2 = (TextView) convertView.findViewById(R.id.add_buy_list_view_2);
            viewHolder.productImageView1 = (NetworkImageView) convertView.findViewById(R.id.product_pic_1);
            viewHolder.productImageView2 = (NetworkImageView) convertView.findViewById(R.id.product_pic_2);
            viewHolder.productTitleView1 = (TextView) convertView.findViewById(R.id.product_title_view_1);
            viewHolder.productTitleView2 = (TextView) convertView.findViewById(R.id.product_title_view_2);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.productImageView1.setDefaultImageResId(R.drawable.tuqian);
        viewHolder.productImageView2.setDefaultImageResId(R.drawable.tuqian);
        int start = position * 2;
        int end = position * 2 + 1;
        if (records.size() - 1 >= end) {
            final WinningInfo winningInfo = records.get(start);
            final WinningInfo winningInfo2 = records.get(start + 1);
            viewHolder.layout1.setVisibility(View.VISIBLE);
            viewHolder.layout2.setVisibility(View.VISIBLE);
            viewHolder.productImageView1.setImageUrl(winningInfo.productUrl, mImageLoader);
            viewHolder.productImageView2.setImageUrl(winningInfo2.productUrl, mImageLoader);
            viewHolder.productTitleView1.setText(winningInfo.title);
            viewHolder.productTitleView2.setText(winningInfo2.title);
            viewHolder.layout1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent it = new Intent(mContext, WinningDetailActivity.class);
                    it.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    it.putExtra(WinningDetailActivity.LSSUE_ID, winningInfo.id);
                    it.putExtra(WinningDetailActivity.PRODUCT_ID, winningInfo.productId);
                    it.putExtra(WinningDetailActivity.DETAIL_TYPE, WinningDetailActivity.DETAIL_TYPE_FRI);
                    it.putExtra(WinningDetailActivity.LASTEST, true);
                    mContext.startActivity(it);
                }
            });

            viewHolder.layout2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent it = new Intent(mContext, WinningDetailActivity.class);
                    it.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    it.putExtra(WinningDetailActivity.LSSUE_ID, winningInfo2.id);
                    it.putExtra(WinningDetailActivity.PRODUCT_ID, winningInfo2.productId);
                    it.putExtra(WinningDetailActivity.DETAIL_TYPE, WinningDetailActivity.DETAIL_TYPE_FRI);
                    it.putExtra(WinningDetailActivity.LASTEST, true);
                    mContext.startActivity(it);
                }
            });
        } else {
            final WinningInfo winningInfo = records.get(start);
            viewHolder.layout1.setVisibility(View.VISIBLE);
            viewHolder.layout2.setVisibility(View.INVISIBLE);
            viewHolder.productImageView1.setImageUrl(winningInfo.productUrl, mImageLoader);
            viewHolder.productTitleView1.setText(winningInfo.title);
            viewHolder.layout1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent it = new Intent(mContext, WinningDetailActivity.class);
                    it.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    it.putExtra(WinningDetailActivity.LSSUE_ID, winningInfo.id);
                    it.putExtra(WinningDetailActivity.PRODUCT_ID, winningInfo.productId);
                    it.putExtra(WinningDetailActivity.DETAIL_TYPE, WinningDetailActivity.DETAIL_TYPE_FRI);
                    it.putExtra(WinningDetailActivity.LASTEST, true);
                    mContext.startActivity(it);
                }
            });

            viewHolder.layout2.setOnClickListener(null);
        }

        return convertView;
    }


    static class ViewHolder {
        public NetworkImageView productImageView1;
        public TextView productTitleView1;
        public TextView addBuyListView1;
        public View layout1;

        public NetworkImageView productImageView2;
        public TextView productTitleView2;
        public TextView addBuyListView2;
        public View layout2;
    }
}
