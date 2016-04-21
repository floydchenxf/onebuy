package com.floyd.onebuy.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.floyd.onebuy.ui.R;
import com.floyd.onebuy.biz.vo.product.WinningInfo;
import com.floyd.onebuy.ui.ImageLoaderFactory;
import com.floyd.onebuy.ui.activity.WinningDetailActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by floyd on 16-4-16.
 */
public class IndexProductAdapter extends BaseAdapter {

    List<WinningInfo> winningInfos = new ArrayList<WinningInfo>();
    private ImageLoader imageLoader;
    private Context mContext;

    public IndexProductAdapter(Context context, List<WinningInfo> args) {
        this.imageLoader = ImageLoaderFactory.createImageLoader();
        winningInfos.addAll(args);
        mContext = context;
    }

    public void addAll(List<WinningInfo> products, boolean needClear) {
        if (needClear) {
            winningInfos.clear();
        }
        winningInfos.addAll(products);
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return winningInfos.size() % 2 == 0 ? winningInfos.size() / 2 : winningInfos.size() / 2 + 1;
    }

    @Override
    public WinningInfo getItem(int position) {
        return winningInfos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.winning_product_item, null);
            viewHolder = new ViewHolder();
            viewHolder.layout1 = convertView.findViewById(R.id.product_item_1);
            viewHolder.layout2 = convertView.findViewById(R.id.product_item_2);
            viewHolder.addBuyListView1 = (TextView) convertView.findViewById(R.id.add_buy_list_view_1);
            viewHolder.addBuyListView2 = (TextView) convertView.findViewById(R.id.add_buy_list_view_2);
            viewHolder.processPrecentDescView1 = (TextView) convertView.findViewById(R.id.progress_precent_desc_1);
            viewHolder.processPrecentDescView2 = (TextView) convertView.findViewById(R.id.progress_precent_desc_2);
            viewHolder.productImageView1 = (NetworkImageView) convertView.findViewById(R.id.product_pic_1);
            viewHolder.productImageView2 = (NetworkImageView) convertView.findViewById(R.id.product_pic_2);
            viewHolder.productTitleView1 = (TextView) convertView.findViewById(R.id.product_title_view_1);
            viewHolder.productTitleView2 = (TextView) convertView.findViewById(R.id.product_title_view_2);
            viewHolder.progressBarView1 = (ProgressBar) convertView.findViewById(R.id.progress_present_1);
            viewHolder.progressBarView2 = (ProgressBar) convertView.findViewById(R.id.progress_present_2);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.productImageView1.setDefaultImageResId(R.drawable.tuqian);
        viewHolder.productImageView2.setDefaultImageResId(R.drawable.tuqian);
        int start = position * 2;
        int end = position * 2 + 1;
        if (winningInfos.size() - 1 >= end) {
            final WinningInfo winningInfo = winningInfos.get(start);
            final WinningInfo winningInfo2 = winningInfos.get(start + 1);
            viewHolder.layout1.setVisibility(View.VISIBLE);
            viewHolder.layout2.setVisibility(View.VISIBLE);
            viewHolder.productImageView1.setImageUrl(winningInfo.productUrl, imageLoader);
            viewHolder.productImageView2.setImageUrl(winningInfo2.productUrl, imageLoader);
            viewHolder.progressBarView1.setProgress(winningInfo.processPrecent);
            viewHolder.progressBarView2.setProgress(winningInfo2.processPrecent);
            viewHolder.productTitleView1.setText(winningInfo.title);
            viewHolder.productTitleView2.setText(winningInfo2.title);
            viewHolder.processPrecentDescView1.setText(Html.fromHtml("夺宝进度:<font color=\"blue\">" + winningInfo.processPrecent + "</font>"));
            viewHolder.processPrecentDescView2.setText(Html.fromHtml("夺宝进度:<font color=\"blue\">" + winningInfo2.processPrecent + "</font>"));

            viewHolder.layout1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent it = new Intent(mContext, WinningDetailActivity.class);
                    it.putExtra("id", winningInfo.id);
                    mContext.startActivity(it);
                }
            });

            viewHolder.layout2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent it = new Intent(mContext, WinningDetailActivity.class);
                    it.putExtra("id", winningInfo2.id);
                    mContext.startActivity(it);
                }
            });
        } else {
            final WinningInfo winningInfo = winningInfos.get(start);
            viewHolder.layout1.setVisibility(View.VISIBLE);
            viewHolder.layout2.setVisibility(View.INVISIBLE);
            viewHolder.productImageView1.setImageUrl(winningInfo.productUrl, imageLoader);
            viewHolder.progressBarView1.setProgress(winningInfo.processPrecent);
            viewHolder.productTitleView1.setText(winningInfo.title);
            viewHolder.processPrecentDescView1.setText(Html.fromHtml("夺宝进度:<font color=\"blue\">" + winningInfo.processPrecent + "</font>"));

            viewHolder.layout1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent it = new Intent(mContext, WinningDetailActivity.class);
                    it.putExtra("id", winningInfo.id);
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
        public TextView processPrecentDescView1;
        public ProgressBar progressBarView1;
        public TextView addBuyListView1;
        public View layout1;

        public NetworkImageView productImageView2;
        public TextView productTitleView2;
        public TextView processPrecentDescView2;
        public ProgressBar progressBarView2;
        public TextView addBuyListView2;
        public View layout2;
    }
}
