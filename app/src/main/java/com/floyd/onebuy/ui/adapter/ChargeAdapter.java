package com.floyd.onebuy.ui.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.floyd.onebuy.biz.tools.DateUtil;
import com.floyd.onebuy.biz.vo.json.ChargeOrderVO;
import com.floyd.onebuy.biz.vo.json.ChargeVO;
import com.floyd.onebuy.ui.R;

import java.util.List;
import java.util.Map;

/**
 * Created by chenxiaofeng on 16/8/13.
 */
public class ChargeAdapter extends BaseDataAdapter<ChargeVO> {

    public ChargeAdapter(Context context, List<ChargeVO> records) {
        super(context, records);
    }

    @Override
    View getLayoutView() {
        return View.inflate(mContext, R.layout.charge_item, null);
    }

    @Override
    int[] cacheViews() {
        return new int[]{R.id.charge_type_view, R.id.pay_time_view, R.id.money_view};
    }

    @Override
    void processHolder(Map<Integer, View> holder, ChargeVO chargeVO) {
        TextView chargeTypeView = (TextView) holder.get(R.id.charge_type_view);
        TextView payTimeView = (TextView) holder.get(R.id.pay_time_view);
        TextView moneyView = (TextView) holder.get(R.id.money_view);
        if (chargeVO.ChargeType == 0) {
            chargeTypeView.setText("支付宝充值");
        } else if (chargeVO.ChargeType == 1) {
            chargeTypeView.setText("微信充值");
        }

        String dateTimeStr = DateUtil.getDateTime(chargeVO.getPayTime());
        payTimeView.setText(dateTimeStr);
        moneyView.setText(chargeVO.Money + "元");
    }
}
