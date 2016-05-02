package com.floyd.onebuy.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.floyd.onebuy.biz.vo.model.WinningInfo;
import com.floyd.onebuy.ui.ImageLoaderFactory;
import com.floyd.onebuy.ui.R;

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
        return mList.size() % 3 == 0 ? mList.size() / 3 : mList.size() / 3 + 1;
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
            holder.productImage3 = (NetworkImageView) convertView.findViewById(R.id.productImage3);

            holder.layout1 = convertView.findViewById(R.id.productLayout1);
            holder.layout2 = convertView.findViewById(R.id.productLayout2);
            holder.layout3 = convertView.findViewById(R.id.productLayout3);

            holder.productNameView1 = (TextView) convertView.findViewById(R.id.productNameView1);
            holder.productNameView2 = (TextView) convertView.findViewById(R.id.productNameView2);
            holder.productNameView3 = (TextView) convertView.findViewById(R.id.productNameView3);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.productImage1.setDefaultImageResId(R.drawable.tuqian);
        holder.productImage2.setDefaultImageResId(R.drawable.tuqian);
        holder.productImage3.setDefaultImageResId(R.drawable.tuqian);

        int start = position * 3;
        int end = position * 3 + 2;


        if (mList.size() - 1 >= end) {
            final WinningInfo info1 = mList.get(start);
            holder.productImage1.setImageUrl(info1.productUrl, imageLoader);
            holder.productNameView1.setText(info1.title);
            holder.layout1.setVisibility(View.VISIBLE);
            holder.layout1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO
                }
            });
            final WinningInfo info2 = mList.get(start + 1);
            holder.productImage2.setImageUrl(info2.productUrl, imageLoader);
            holder.productNameView2.setText(info2.title);
            holder.layout2.setVisibility(View.VISIBLE);
            holder.layout2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO
                }
            });
            final WinningInfo info3 = mList.get(start + 2);
            holder.productImage3.setImageUrl(info3.productUrl, imageLoader);
            holder.productNameView3.setText(info3.title);
            holder.layout3.setVisibility(View.VISIBLE);
            holder.layout3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO
                }
            });
        } else {
            final WinningInfo info1 = mList.get(start);
            holder.productImage1.setImageUrl(info1.getProductUrl(), imageLoader);
            holder.productNameView1.setText(info1.title);
            holder.layout1.setVisibility(View.VISIBLE);
            holder.layout1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO
                }
            });

            if (mList.size() - 1 < start + 1) {
                holder.layout2.setVisibility(View.INVISIBLE);
                holder.layout3.setVisibility(View.INVISIBLE);
                holder.layout2.setOnClickListener(null);
                holder.layout3.setOnClickListener(null);
            } else {
                final WinningInfo info2 = mList.get(start + 1);
                holder.productImage2.setImageUrl(info2.productUrl, imageLoader);
                holder.productNameView2.setText(info2.title);
                holder.layout2.setVisibility(View.VISIBLE);
                holder.layout2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //TODO
                    }
                });
                holder.layout3.setVisibility(View.INVISIBLE);
                holder.layout3.setOnClickListener(null);
            }
        }

        return convertView;
    }

    public static class ViewHolder {
        private NetworkImageView productImage1;
        private NetworkImageView productImage2;
        private NetworkImageView productImage3;

        private TextView productNameView1;
        private TextView productNameView2;
        private TextView productNameView3;

        private View layout1;
        private View layout2;
        private View layout3;


    }
}
