package com.floyd.onebuy.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.floyd.onebuy.ui.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by floyd on 16-5-14.
 */
public class JoinedNumAdapter extends BaseAdapter {

    private List<String> records = new ArrayList<String>();

    private Context mContext;

    public JoinedNumAdapter(Context context, List<String> records) {
        this.mContext = context;
        this.records.clear();
        this.records.addAll(records);
    }


    public void addAll(List<String> records, boolean needClear) {
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
            convertView = View.inflate(mContext, R.layout.joined_num_item, null);
            holder = new ViewHolder();
            holder.textView1 = (TextView) convertView.findViewById(R.id.join_number_1);
            holder.textView2 = (TextView) convertView.findViewById(R.id.join_number_2);
            holder.textView3 = (TextView) convertView.findViewById(R.id.join_number_3);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.textView1.setVisibility(View.INVISIBLE);
        holder.textView2.setVisibility(View.INVISIBLE);
        holder.textView3.setVisibility(View.INVISIBLE);

        int start = position * 3;
        int end = position * 3 + 2;


        if (records.size() - 1 >= end) {
            String text1 = records.get(start);
            String text2 = records.get(start + 1);
            String text3 = records.get(start + 2);
            holder.textView1.setText(text1);
            holder.textView2.setText(text2);
            holder.textView3.setText(text3);
            holder.textView1.setVisibility(View.VISIBLE);
            holder.textView2.setVisibility(View.VISIBLE);
            holder.textView3.setVisibility(View.VISIBLE);
        } else {

            String text1 = records.get(start);
            holder.textView1.setText(text1);
            holder.textView1.setVisibility(View.VISIBLE);

            if (records.size() - 1 >= start + 1) {
                String text2 = records.get(start + 1);
                holder.textView2.setText(text2);
                holder.textView2.setVisibility(View.VISIBLE);
            }
        }

        return convertView;
    }

    private static class ViewHolder {
        public TextView textView1;
        public TextView textView2;
        public TextView textView3;
    }
}
