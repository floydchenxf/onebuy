package com.floyd.onebuy.ui.adapter;

import android.content.Context;
import android.view.View;

import com.floyd.onebuy.biz.vo.json.ProductLssueWithWinnerVO;
import com.floyd.onebuy.ui.R;

import java.util.List;
import java.util.Map;

/**
 * Created by floyd on 16-7-23.
 */
public class LuckRecordAdapter extends BaseDataAdapter<ProductLssueWithWinnerVO> {

    public LuckRecordAdapter(Context context, List<ProductLssueWithWinnerVO> records) {
        super(context, records);
    }

    @Override
    View getLayoutView() {
        return View.inflate(mContext, R.layout.luck_record_item, null);
    }

    @Override
    int[] cacheViews() {
        return new int[]{};
    }

    @Override
    void processHolder(Map<Integer, View> holder, ProductLssueWithWinnerVO productLssueWithWinnerVO) {

    }
}
