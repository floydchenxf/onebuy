package com.yyg365.interestbar.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.yyg365.interestbar.biz.vo.NavigationVO;
import com.yyg365.interestbar.ui.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by floyd on 16-5-17.
 */
public class NavigationAdapter extends BaseAdapter {

    protected Context mContext;

    protected List<NavigationVO> records = new ArrayList<NavigationVO>();

    public NavigationAdapter(Context context, List<NavigationVO> records) {
        this.mContext = context;
        this.records.clear();
        this.records.addAll(records);
    }

    public void add(NavigationVO k) {
        records.add(k);
        this.notifyDataSetChanged();
    }

    public void addAll(List<NavigationVO> records, boolean needClear) {
        if (needClear) {
            this.records.clear();
        }
        this.records.addAll(records);
        this.notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        return records.size() % 3 == 0 ? records.size() / 3 : records.size() / 3 + 1;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.night_item, null);
            holder = new ViewHolder();
            holder.layout1 = convertView.findViewById(R.id.layout1);
            holder.layout2 = convertView.findViewById(R.id.layout2);
            holder.layout3 = convertView.findViewById(R.id.layout3);
            holder.imageView1 = (ImageView) convertView.findViewById(R.id.ItemImage1);
            holder.imageView2 = (ImageView) convertView.findViewById(R.id.ItemImage2);
            holder.imageView3 = (ImageView) convertView.findViewById(R.id.ItemImage3);
            holder.textView1 = (TextView) convertView.findViewById(R.id.ItemText1);
            holder.textView2 = (TextView) convertView.findViewById(R.id.ItemText2);
            holder.textView3 = (TextView) convertView.findViewById(R.id.ItemText3);
            holder.line1 = convertView.findViewById(R.id.split_line1);
            holder.line2 = convertView.findViewById(R.id.split_line2);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.layout1.setVisibility(View.GONE);
        holder.layout2.setVisibility(View.GONE);
        holder.layout3.setVisibility(View.GONE);
        holder.line1.setVisibility(View.GONE);
        holder.line2.setVisibility(View.GONE);
        holder.layout1.setOnClickListener(null);
        holder.layout2.setOnClickListener(null);
        holder.layout3.setOnClickListener(null);

        int start = position * 3;
        int end = position * 3 + 2;

        if (records.size() - 1 >= end) {
            final NavigationVO info1 = records.get(start);
            final NavigationVO info2 = records.get(start + 1);
            final NavigationVO info3 = records.get(start + 2);

            holder.imageView1.setImageResource(info1.drawIcon);
            holder.imageView2.setImageResource(info2.drawIcon);
            holder.imageView3.setImageResource(info3.drawIcon);

            holder.textView1.setText(info1.navigationName);
            holder.textView2.setText(info2.navigationName);
            holder.textView3.setText(info3.navigationName);

            holder.layout1.setOnClickListener(info1.onClickListener);
            holder.layout2.setOnClickListener(info2.onClickListener);
            holder.layout3.setOnClickListener(info3.onClickListener);

            holder.layout1.setVisibility(View.VISIBLE);
            holder.layout2.setVisibility(View.VISIBLE);
            holder.layout3.setVisibility(View.VISIBLE);
            holder.line1.setVisibility(View.VISIBLE);
            holder.line2.setVisibility(View.VISIBLE);
        } else {
            final NavigationVO info1 = records.get(start);
            holder.imageView1.setImageResource(info1.drawIcon);
            holder.textView1.setText(info1.navigationName);
            holder.layout1.setOnClickListener(info1.onClickListener);
            holder.layout1.setVisibility(View.VISIBLE);
            holder.line1.setVisibility(View.VISIBLE);

            if (records.size() - 1 >= start + 1) {
                final NavigationVO info2 = records.get(start + 1);
                holder.imageView2.setImageResource(info2.drawIcon);
                holder.textView2.setText(info2.navigationName);
                holder.layout2.setOnClickListener(info2.onClickListener);
                holder.layout2.setVisibility(View.VISIBLE);
                holder.line2.setVisibility(View.VISIBLE);
            }
        }

        return convertView;
    }

    public static class ViewHolder {
        public View layout1;
        public View layout2;
        public View layout3;
        public View line1;
        public View line2;
        public ImageView imageView1;
        public ImageView imageView2;
        public ImageView imageView3;
        public TextView textView1;
        public TextView textView2;
        public TextView textView3;
    }
}
