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
                System.arraycopy(joinedNums, 0, tmpList, 0, 5);
                tmpList.add("更多");
            } else {
                System.arraycopy(joinedNums, 0, tmpList, 0, joinedNums.size());
            }

            int lines = tmpList.size() % 3 == 0 ? tmpList.size() / 3 : tmpList.size() / 3 + 1;
            for (int i = 0; i < lines; i++) {
                LinearLayout layout = (LinearLayout) View.inflate(mContext, R.layout.joined_num_item, null);
                TextView text1 = (TextView) layout.findViewById(R.id.join_number_1);
                TextView text2 = (TextView) layout.findViewById(R.id.join_number_2);
                TextView text3 = (TextView) layout.findViewById(R.id.join_number_3);
                int k = i * 3;
                if (k < joinedNums.size()) {
                    text1.setText(joinedNums.get(k));
                }

                if (k + 1 < joinedNums.size()) {
                    text2.setText(joinedNums.get(k + 1));
                } else {
                    text2.setVisibility(View.GONE);
                }

                if (k + 2 < joinedNums.size()) {
                    text3.setText(joinedNums.get(k + 2));
                } else {
                    text3.setVisibility(View.GONE);
                }
            }
        }


    }
}
