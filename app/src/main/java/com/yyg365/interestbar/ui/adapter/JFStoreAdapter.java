package com.yyg365.interestbar.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.yyg365.interestbar.biz.vo.json.JFGoodsVO;
import com.yyg365.interestbar.ui.R;
import com.yyg365.interestbar.ui.activity.JFDetailGoodsActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenxiaofeng on 2017/1/11.
 */
public class JFStoreAdapter extends BaseAdapter {

    private ImageLoader mImageLoader;

    protected List<JFGoodsVO> mList = new ArrayList<JFGoodsVO>();

    private Context mContext;


    public JFStoreAdapter(Context context, List<JFGoodsVO> records, ImageLoader imageLoader) {
        this.mContext = context;
        this.mImageLoader = imageLoader;
        mList.clear();
        mList.addAll(records);
    }

    public void add(JFGoodsVO k) {
        mList.add(k);
        this.notifyDataSetChanged();
    }

    public void addAll(List<JFGoodsVO> records, boolean needClear) {
        if (needClear) {
            this.mList.clear();
        }
        this.mList.addAll(records);
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mList.size() % 2 == 0 ? mList.size() / 2 : mList.size() / 2 + 1;
    }

    @Override
    public JFGoodsVO getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.jf_goods_item, null);
            holder = new ViewHolder();
            holder.productImage1 = (NetworkImageView) convertView.findViewById(R.id.productImage1);
            holder.productImage2 = (NetworkImageView) convertView.findViewById(R.id.productImage2);

            holder.productLayout1 = convertView.findViewById(R.id.productLayout1);
            holder.productLayout2 = convertView.findViewById(R.id.productLayout2);

            holder.productNameView1 = (TextView) convertView.findViewById(R.id.productNameView1);
            holder.productNameView2 = (TextView) convertView.findViewById(R.id.productNameView2);

            holder.scoreView1 = (TextView) convertView.findViewById(R.id.jf_score1);
            holder.scoreView2 = (TextView) convertView.findViewById(R.id.jf_score2);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.productImage1.setDefaultImageResId(R.drawable.tupian);
        holder.productImage2.setDefaultImageResId(R.drawable.tupian);

        int start = position * 2;
        int end = position * 2 + 1;


        if (mList.size() - 1 >= end) {
            final JFGoodsVO info1 = mList.get(start);
            holder.productImage1.setImageUrl(info1.getPicUrl(), mImageLoader);
            holder.productNameView1.setText(info1.Name);
            holder.productLayout1.setVisibility(View.VISIBLE);
            holder.productLayout1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent detailIntent = new Intent(mContext, JFDetailGoodsActivity.class);
                    detailIntent.putExtra(JFDetailGoodsActivity.JF_DETAIL_GOODS_ID, info1.ID);
                    mContext.startActivity(detailIntent);
                }
            });

            Spanned score1 = Html.fromHtml("<font color=\"red\">" + info1.JiFen + "</font>&nbsp;积分");
            holder.scoreView1.setText(score1);

            final JFGoodsVO info2 = mList.get(start + 1);
            holder.productImage2.setImageUrl(info2.getPicUrl(), mImageLoader);
            holder.productNameView2.setText(info2.Name);
            holder.productLayout2.setVisibility(View.VISIBLE);
            holder.productLayout2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent detailIntent = new Intent(mContext, JFDetailGoodsActivity.class);
                    detailIntent.putExtra(JFDetailGoodsActivity.JF_DETAIL_GOODS_ID, info2.ID);
                    mContext.startActivity(detailIntent);
                }
            });

            Spanned score2 = Html.fromHtml("<font color=\"red\">" + info2.JiFen + "</font>&nbsp;积分");
            holder.scoreView2.setText(score2);
        } else {
            final JFGoodsVO info1 = mList.get(start);
            holder.productImage1.setImageUrl(info1.getPicUrl(), mImageLoader);
            holder.productNameView1.setText(info1.Name);
            holder.productLayout1.setVisibility(View.VISIBLE);
            holder.productLayout1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent detailIntent = new Intent(mContext, JFDetailGoodsActivity.class);
                    detailIntent.putExtra(JFDetailGoodsActivity.JF_DETAIL_GOODS_ID, info1.ID);
                    mContext.startActivity(detailIntent);
                }
            });

            Spanned score1 = Html.fromHtml("<font color=\"red\">" + info1.JiFen + "</font>&nbsp;积分");
            holder.scoreView1.setText(score1);

            holder.productLayout2.setVisibility(View.INVISIBLE);
            holder.productLayout2.setOnClickListener(null);
        }

        return convertView;
    }

    public List<JFGoodsVO> getRecords() {
        return this.mList;
    }

    public static class ViewHolder {
        public View productLayout1;
        public View productLayout2;

        private NetworkImageView productImage1;
        private NetworkImageView productImage2;

        private TextView productNameView1;
        private TextView productNameView2;

        private TextView scoreView1;
        private TextView scoreView2;
    }

}




