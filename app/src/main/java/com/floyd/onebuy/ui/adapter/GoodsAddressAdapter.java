package com.floyd.onebuy.ui.adapter;

import android.content.Context;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.floyd.onebuy.biz.vo.json.GoodsAddressVO;
import com.floyd.onebuy.ui.R;

import java.util.List;
import java.util.Map;

/**
 * Created by floyd on 16-5-8.
 */
public class GoodsAddressAdapter extends BaseDataAdapter<GoodsAddressVO> {

    public GoodsAddressAdapter(Context context, List<GoodsAddressVO> records) {
        super(context, records);
    }

    @Override
    View getLayoutView() {
        return View.inflate(mContext, R.layout.address_item, null);
    }

    @Override
    int[] cacheViews() {
        return new int[]{R.id.name_view, R.id.mobile_view, R.id.address_detail_view};
    }

    @Override
    void processHolder(Map<Integer, View> holder, final GoodsAddressVO addressVO) {
        TextView nameView = (TextView) holder.get(R.id.name_view);
        TextView mobileView = (TextView) holder.get(R.id.mobile_view);
        TextView addressDetailView = (TextView) holder.get(R.id.address_detail_view);

        nameView.setText(addressVO.linkName);
        mobileView.setText(addressVO.mobile);
        StringBuilder detailStr = new StringBuilder();
        if (addressVO.isDefault > 0) {
            detailStr.append("<font color=\"red\">默认</font>");
        }
        detailStr.append(addressVO.getFullAddress());
        addressDetailView.setText(Html.fromHtml(detailStr.toString()));
    }

}
