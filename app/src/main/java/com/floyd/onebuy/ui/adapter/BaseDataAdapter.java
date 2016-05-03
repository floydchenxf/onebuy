package com.floyd.onebuy.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by floyd on 16-5-4.
 */
public abstract class BaseDataAdapter<T> extends BaseAdapter {

    protected Context mContext;

    protected List<T> records = new ArrayList<T>();

    public BaseDataAdapter(Context context, List<T> records) {
        this.mContext = context;
        this.records.clear();
        this.records.addAll(records);
    }

    public void add(T k) {
        records.add(k);
        this.notifyDataSetChanged();
    }

    public void addAll(List<T> records, boolean needClear) {
        if (needClear) {
            records.clear();
        }
        records.addAll(records);
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return records.size();
    }

    @Override
    public T getItem(int position) {
        return records.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public List<T> getRecords() {
        return this.records;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Map<Integer, View> holder;
        if (convertView == null) {
            convertView = getLayoutView();
            holder = new HashMap<Integer, View>();
            initHolder(convertView, holder);
            convertView.setTag(holder);
        } else {
            holder = (Map<Integer, View>) convertView.getTag();
        }

        T t = getItem(position);
        processHolder(holder, t);
        return convertView;
    }

    /**
     * 设置布局
     *
     * @return
     */
    abstract View getLayoutView();

    /**
     * 设置holder
     *
     * @param view
     * @param holder
     */
    abstract void initHolder(View view, Map<Integer, View> holder);

    /**
     * 处理holder
     *
     * @param holder
     * @param t
     */
    abstract void processHolder(Map<Integer, View> holder, T t);

}
