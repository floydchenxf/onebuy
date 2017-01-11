package com.yyg365.interestbar.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.yyg365.interestbar.biz.vo.model.WinningInfo;
import com.yyg365.interestbar.ui.ImageLoaderFactory;
import com.yyg365.interestbar.ui.R;
import com.yyg365.interestbar.ui.activity.WinningDetailActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by floyd on 16-5-2.
 */
public class ProductAdapter extends BaseAdapter {

    private Context mContext;

    private List<WinningInfo> mList = new ArrayList<WinningInfo>();

    private ImageLoader imageLoader;

    public ProductAdapter(Context context, List<WinningInfo> args) {
        this.imageLoader = ImageLoaderFactory.createImageLoader();
        mList.addAll(args);
        mContext = context;
    }

    public void add(WinningInfo k) {
        mList.add(k);
        this.notifyDataSetChanged();
    }

    public void addAll(List<WinningInfo> motes, boolean needClear) {
        if (needClear) {
            mList.clear();
        }
        mList.addAll(motes);
        this.notifyDataSetChanged();
    }

    public List<WinningInfo> getProductList() {
        return mList;
    }

    @Override
    public int getCount() {
        return mList.size() % 2 == 0 ? mList.size() / 2 : mList.size() / 2 + 1;
    }

    @Override
    public WinningInfo getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.product_item, null);
            holder = new ViewHolder();
            holder.productImage1 = (NetworkImageView) convertView.findViewById(R.id.productImage1);
            holder.productImage2 = (NetworkImageView) convertView.findViewById(R.id.productImage2);

            holder.layout1 = convertView.findViewById(R.id.productLayout1);
            holder.layout2 = convertView.findViewById(R.id.productLayout2);

            holder.productNameView1 = (TextView) convertView.findViewById(R.id.productNameView1);
            holder.productNameView2 = (TextView) convertView.findViewById(R.id.productNameView2);

            holder.line1 = convertView.findViewById(R.id.line1);

            holder.typeIconView1 = (ImageView) convertView.findViewById(R.id.type_icon_view1);
            holder.typeIconView2 = (ImageView) convertView.findViewById(R.id.type_icon_view2);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.productImage1.setDefaultImageResId(R.drawable.tupian);
        holder.productImage2.setDefaultImageResId(R.drawable.tupian);

        int start = position * 2;
        int end = position * 2 + 1;


        if (mList.size() - 1 >= end) {
            final WinningInfo info1 = mList.get(start);
            holder.productImage1.setImageUrl(info1.productUrl, imageLoader);
            holder.productNameView1.setText(info1.title);
            holder.layout1.setVisibility(View.VISIBLE);
            holder.layout1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent it = new Intent(mContext, WinningDetailActivity.class);
                    it.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    it.putExtra(WinningDetailActivity.PRODUCT_ID, info1.productId);
                    mContext.startActivity(it);
                }
            });
            holder.line1.setVisibility(View.VISIBLE);

            holder.typeIconView1.setVisibility(View.GONE);
            if (info1.productType == 1) {
                holder.typeIconView1.setVisibility(View.VISIBLE);
                holder.typeIconView1.setImageResource(R.drawable.ten_icon);
            } else if (info1.productType == 2) {
                holder.typeIconView1.setVisibility(View.VISIBLE);
                holder.typeIconView1.setImageResource(R.drawable.hun_icon);
            }

            final WinningInfo info2 = mList.get(start + 1);
            holder.productImage2.setImageUrl(info2.productUrl, imageLoader);
            holder.productNameView2.setText(info2.title);
            holder.layout2.setVisibility(View.VISIBLE);
            holder.layout2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent it = new Intent(mContext, WinningDetailActivity.class);
                    it.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    it.putExtra(WinningDetailActivity.PRODUCT_ID, info2.productId);
                    mContext.startActivity(it);
                }
            });

            holder.typeIconView2.setVisibility(View.GONE);
            if (info2.productType == 1) {
                holder.typeIconView2.setVisibility(View.VISIBLE);
                holder.typeIconView2.setImageResource(R.drawable.ten_icon);
            } else if (info2.productType == 2) {
                holder.typeIconView2.setVisibility(View.VISIBLE);
                holder.typeIconView2.setImageResource(R.drawable.hun_icon);
            }

        } else {
            final WinningInfo info1 = mList.get(start);
            holder.productImage1.setImageUrl(info1.getProductUrl(), imageLoader);
            holder.productNameView1.setText(info1.title);
            holder.layout1.setVisibility(View.VISIBLE);
            holder.layout1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent it = new Intent(mContext, WinningDetailActivity.class);
                    it.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    it.putExtra(WinningDetailActivity.PRODUCT_ID, info1.productId);
                    mContext.startActivity(it);
                }
            });

            holder.line1.setVisibility(View.VISIBLE);

            holder.typeIconView1.setVisibility(View.GONE);
            if (info1.productType == 1) {
                holder.typeIconView1.setVisibility(View.VISIBLE);
                holder.typeIconView1.setImageResource(R.drawable.ten_icon);
            } else if (info1.productType == 2) {
                holder.typeIconView1.setVisibility(View.VISIBLE);
                holder.typeIconView1.setImageResource(R.drawable.hun_icon);
            }

            holder.layout2.setVisibility(View.INVISIBLE);
            holder.layout2.setOnClickListener(null);
            holder.typeIconView2.setVisibility(View.GONE);
        }

        return convertView;
    }

    public static class ViewHolder {
        private NetworkImageView productImage1;
        private NetworkImageView productImage2;

        private TextView productNameView1;
        private TextView productNameView2;

        private View layout1;
        private View layout2;

        private View line1;

        private ImageView typeIconView1;
        private ImageView typeIconView2;


    }
}
