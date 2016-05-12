package com.floyd.onebuy.ui.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.floyd.onebuy.biz.vo.json.AddressVO;
import com.floyd.onebuy.ui.R;

import java.util.List;
import java.util.Map;

/**
 * Created by floyd on 16-5-11.
 */
public class AddressAdapter extends BaseDataAdapter<AddressVO> {
    public AddressAdapter(Context context, List<AddressVO> records) {
        super(context, records);
    }

    @Override
    View getLayoutView() {
        return View.inflate(mContext, R.layout.simple_address_item, null);
    }

    @Override
    int[] cacheViews() {
        return new int[]{R.id.address_name_view};
    }

    @Override
    void processHolder(Map<Integer, View> holder, AddressVO addressVO) {
        TextView addressNameView = (TextView) holder.get(R.id.address_name_view);
        addressNameView.setText(addressVO.CodeName);
    }
}
