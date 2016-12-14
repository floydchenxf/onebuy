package com.yyg365.interestbar.ui.adapter;

import android.content.Context;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.widget.TextView;

import com.yyg365.interestbar.biz.tools.DateUtil;
import com.yyg365.interestbar.biz.vo.json.ChargeOrderVO;
import com.yyg365.interestbar.biz.vo.json.ChargeVO;
import com.yyg365.interestbar.ui.R;

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
        return new int[]{R.id.charge_type_view, R.id.pay_time_view, R.id.money_pay_status_view, R.id.order_no_view};
    }

    @Override
    void processHolder(Map<Integer, View> holder, ChargeVO chargeVO) {
        TextView chargeTypeView = (TextView) holder.get(R.id.charge_type_view);
        TextView payTimeView = (TextView) holder.get(R.id.pay_time_view);
        TextView moneyView = (TextView) holder.get(R.id.money_pay_status_view);
        TextView orderNoView = (TextView) holder.get(R.id.order_no_view);
        orderNoView.setText(chargeVO.OrderNum);
        chargeTypeView.setText(chargeVO.ChargeTypeTitle);

        int chargeStatus = chargeVO.ChargeState;
        StringBuilder moneyPayStatus = new StringBuilder();
        moneyPayStatus.append("<font color=\"red\">").append(chargeVO.Money).append("</font>").append("/");
        if (chargeStatus == 0) {
            moneyPayStatus.append("未支付");
        } else {
            moneyPayStatus.append("已支付");
        }

        Spanned s = Html.fromHtml(moneyPayStatus.toString());
        moneyView.setText(s);
        String dateTimeStr = DateUtil.getDateTime("yy/MM/dd HH:mm", chargeVO.getPayTime());
        payTimeView.setText(dateTimeStr);
    }
}
