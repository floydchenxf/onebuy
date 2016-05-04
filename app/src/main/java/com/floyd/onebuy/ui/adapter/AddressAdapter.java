package com.floyd.onebuy.ui.adapter;

import android.content.Context;
import android.view.View;

import com.floyd.onebuy.biz.vo.json.AddressVO;

import java.util.List;
import java.util.Map;

/**
 * Created by floyd on 16-5-8.
 */
public class AddressAdapter extends BaseDataAdapter<AddressVO> {
    public AddressAdapter(Context context, List<AddressVO> records) {
        super(context, records);
    }

    @Override
    View getLayoutView() {
        return null;
    }

    @Override
    int[] cacheViews() {
        return new int[0];
    }

    @Override
    void processHolder(Map<Integer, View> holder, AddressVO addressVO) {

    }
}
