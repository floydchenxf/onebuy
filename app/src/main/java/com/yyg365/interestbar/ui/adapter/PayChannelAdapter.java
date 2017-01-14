package com.yyg365.interestbar.ui.adapter;

import android.content.Context;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.yyg365.interestbar.biz.vo.json.PayChannelVO;
import com.yyg365.interestbar.ui.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by chenxiaofeng on 2017/1/7.
 */
public class PayChannelAdapter extends BaseDataAdapter<PayChannelVO> {

    private ImageLoader mImageLoader;

    private Map<Long, Boolean> states = new HashMap<Long, Boolean>();

    private Long payId;

    public PayChannelAdapter(Context context, List<PayChannelVO> records, ImageLoader imageLoader) {
        super(context, records);
        this.mImageLoader = imageLoader;
    }

    public void setDefaultChecked(Long payId) {
        this.payId = payId;
        states.put(payId, true);
    }

    public Long getCheckedId() {
        return this.payId;
    }

    @Override
    View getLayoutView() {
        return View.inflate(mContext, R.layout.redeem_pay_item, null);
    }

    @Override
    int[] cacheViews() {
        return new int[]{R.id.pay_image_view, R.id.pay_name_view, R.id.pay_choose};
    }

    @Override
    void processHolder(Map<Integer, View> holder, final PayChannelVO vo) {

        NetworkImageView payImageView = (NetworkImageView) holder.get(R.id.pay_image_view);
        TextView payNameView = (TextView) holder.get(R.id.pay_name_view);
        final RadioButton payChooseView = (RadioButton) holder.get(R.id.pay_choose);

        payImageView.setDefaultImageResId(R.drawable.tupian);
        payImageView.setImageUrl(vo.getPicUrl(), mImageLoader);

        payNameView.setText(vo.Name);

        final Long id = vo.ID;

        payChooseView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (Long key : states.keySet()) {
                    states.put(key, false);
                }

                payId = id;
                states.put(id, payChooseView.isChecked());
                PayChannelAdapter.this.notifyDataSetChanged();
            }
        });

        boolean res = false;
        if (states.get(id) == null
                || !states.get(id)) {
            res = false;
            states.put(id, false);
        } else {
            res = true;
        }

        payChooseView.setChecked(res);
    }
}
