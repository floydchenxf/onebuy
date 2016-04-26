package com.floyd.onebuy.ui.adapter;

import android.content.Context;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.floyd.onebuy.ui.R;
import com.floyd.onebuy.biz.vo.model.WinningInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by floyd on 16-4-18.
 */
public class BuyCarAdapter extends BaseAdapter {

    private Context mContext;
    private ImageLoader mImageLoader;
    private List<WinningInfo> records = new ArrayList<WinningInfo>();

    private BuyClickListener buyClickListener;



    public BuyCarAdapter(Context context, List<WinningInfo> args, ImageLoader imageLoader, BuyClickListener buyClickListener) {
        this.mContext = context;
        if (args != null && !args.isEmpty()) {
            this.records.addAll(args);
        }
        this.mImageLoader = imageLoader;
        this.buyClickListener = buyClickListener;
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
        return this.records.size();
    }

    @Override
    public WinningInfo getItem(int position) {
        return this.records.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.buy_car_item, null);
            holder = new ViewHolder();
            holder.proudctImageView = (NetworkImageView) convertView.findViewById(R.id.product_image_view);
            holder.totalLeftView = (TextView) convertView.findViewById(R.id.time_info_view);
            holder.subView = (TextView) convertView.findViewById(R.id.sub_view);
            holder.addView = (TextView) convertView.findViewById(R.id.add_view);
            holder.numberView = (EditText) convertView.findViewById(R.id.number_view);
            holder.buyLeftView = (CheckedTextView) convertView.findViewById(R.id.buy_left_view);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final WinningInfo info = getItem(position);
        holder.proudctImageView.setDefaultImageResId(R.drawable.default_image);
        holder.proudctImageView.setImageUrl(info.productUrl, mImageLoader);
        holder.totalLeftView.setText(Html.fromHtml("总需" + info.totalCount + "次, 剩余<font color=\"#ffaa66\">" + (info.totalCount - info.joinedCount) + "</font>次"));
        holder.numberView.setText(info.buyCount + "");
        holder.subView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int num = 0;
                String numStr = holder.numberView.getText().toString();
                if (!TextUtils.isEmpty(numStr)) {
                    num = Integer.parseInt(holder.numberView.getText().toString());
                }
                if (num <= 0) {
                    holder.numberView.setText("0");
                    info.buyCount = 0;
                    return;
                }

                int nn = --num;
                info.buyCount = nn;
                holder.numberView.setText(nn + "");
                if (buyClickListener != null) {
                    buyClickListener.onClick(holder.subView);
                }
            }
        });

        holder.addView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int num = 0;
                String numStr = holder.numberView.getText().toString();
                if (!TextUtils.isEmpty(numStr)) {
                    num = Integer.parseInt(holder.numberView.getText().toString());
                }
                int left = info.totalCount - info.totalCount;
                if (num > left) {
                    Toast.makeText(mContext, "数字大于尾数!", Toast.LENGTH_SHORT).show();
                    return;
                }

                int nn = ++num;
                info.buyCount = nn;
                holder.numberView.setText(nn + "");
                if (buyClickListener != null) {
                    buyClickListener.onClick(holder.addView);
                }
            }
        });


        if (info.buyCount >= (info.totalCount - info.joinedCount)) {
            holder.buyLeftView.setChecked(false);
        } else {
            holder.buyLeftView.setChecked(true);
        }

        holder.buyLeftView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int left = info.totalCount - info.joinedCount;
                info.buyCount = left;
                holder.numberView.setText(info.buyCount+ "");
                if (info.buyCount >= left) {
                    holder.buyLeftView.setChecked(false);
                } else {
                    holder.buyLeftView.setChecked(true);
                }
                if (buyClickListener != null) {
                    buyClickListener.onClick(holder.buyLeftView);
                }
            }
        });
        return convertView;
    }

    public List<WinningInfo> getRecords() {
        return this.records;
    }

    public static class ViewHolder {
        public NetworkImageView proudctImageView;
        public TextView totalLeftView;
        public TextView subView;
        public TextView addView;
        public EditText numberView;
        public CheckedTextView buyLeftView;

    }

    public interface BuyClickListener {
        void onClick(View v);
    }
}
