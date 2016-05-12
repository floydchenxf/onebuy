package com.floyd.onebuy.ui.adapter;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.floyd.onebuy.biz.vo.model.WinningInfo;
import com.floyd.onebuy.ui.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by floyd on 16-5-10.
 */
public class PayResultAdapter extends BaseDataAdapter<WinningInfo> {
    public PayResultAdapter(Context context, List<WinningInfo> records) {
        super(context, records);
    }

    @Override
    View getLayoutView() {
        return View.inflate(mContext, R.layout.pay_result_item, null);
    }

    @Override
    int[] cacheViews() {
        return new int[]{R.id.product_code_view, R.id.product_title_view, R.id.joined_count_view, R.id.joined_num_layout};
    }

    @Override
    void processHolder(Map<Integer, View> holder, WinningInfo winningInfo) {
        TextView productCodeView = (TextView) holder.get(R.id.product_code_view);
        TextView productTitleView = (TextView) holder.get(R.id.product_title_view);
        TextView joinedCountView = (TextView) holder.get(R.id.joined_count_view);
        LinearLayout joinedNumLayout = (LinearLayout) holder.get(R.id.joined_num_layout);
        productCodeView.setText("第" + winningInfo.code + "期");
        productTitleView.setText(winningInfo.getTitle());
        joinedCountView.setText(winningInfo.joinedCount + "人次");

        joinedNumLayout.removeAllViews();
        List<String> joinedNums = winningInfo.myPrizeCodes;
        if (joinedNums == null || joinedNums.isEmpty()) {
            joinedNumLayout.setVisibility(View.GONE);
        } else {
            joinedNumLayout.setVisibility(View.VISIBLE);

            List<String> tmpList = new ArrayList<String>();
            if (joinedNums.size() >= 6) {
                for(int i=0; i<5; i++) {
                    tmpList.add(joinedNums.get(i));
                }
                tmpList.add("更多");
            } else {
                tmpList.addAll(joinedNums);
            }

            int lines = tmpList.size() % 3 == 0 ? tmpList.size() / 3 : tmpList.size() / 3 + 1;
            for (int i = 0; i < lines; i++) {
                LinearLayout layout = (LinearLayout) View.inflate(mContext, R.layout.joined_num_item, null);
                joinedNumLayout.addView(layout);
                TextView text1 = (TextView) layout.findViewById(R.id.join_number_1);
                TextView text2 = (TextView) layout.findViewById(R.id.join_number_2);
                TextView text3 = (TextView) layout.findViewById(R.id.join_number_3);
                int k = i * 3;
                if (k < tmpList.size()) {
                    text1.setText(tmpList.get(k));
                    text1.setVisibility(View.VISIBLE);
                } else {
                    text1.setVisibility(View.GONE);
                }

                if (k + 1 < tmpList.size()) {
                    text2.setText(tmpList.get(k + 1));
                    text2.setVisibility(View.VISIBLE);
                } else {
                    text2.setVisibility(View.GONE);
                }

                if (k + 2 < tmpList.size()) {
                    text3.setText(tmpList.get(k + 2));
                    text3.setVisibility(View.VISIBLE);
                } else {
                    text3.setVisibility(View.GONE);
                }
            }
        }


    }
}
