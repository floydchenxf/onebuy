package com.floyd.onebuy.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;

import com.floyd.onebuy.biz.vo.product.ProductTypeVO;
import com.floyd.onebuy.ui.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by floyd on 16-5-2.
 */
public class TypeAdapter extends BaseAdapter {

    private List<ProductTypeVO> typeList = new ArrayList<ProductTypeVO>();
    private Context mContext;
    private int checkedIndex;

    public TypeAdapter(Context context) {
        this.mContext = context;
    }

    public void addAll(List<ProductTypeVO> typeList) {
        this.typeList.clear();
        this.typeList.addAll(typeList);
        this.notifyDataSetChanged();
    }

    public void setCheckedIndex(int index) {
        this.checkedIndex = index;
        this.notifyDataSetChanged();
    }

    public List<ProductTypeVO> getTypeList() {
        return this.typeList;
    }

    @Override
    public int getCount() {
        return typeList.size();
    }

    @Override
    public ProductTypeVO getItem(int position) {
        return typeList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.type_item, null);
            holder = new ViewHolder();
            holder.typeNameView = (CheckedTextView)convertView.findViewById(R.id.type_name_view);
            convertView.setTag(holder);
        }

        holder = (ViewHolder)convertView.getTag();
        ProductTypeVO typeVO = getItem(position);
        holder.typeNameView.setText(typeVO.CodeName);
        if (checkedIndex == position) {
            holder.typeNameView.setChecked(true);
        } else {
            holder.typeNameView.setChecked(false);
        }
        return convertView;
    }

    private static class ViewHolder {
        public CheckedTextView typeNameView;
    }
}
