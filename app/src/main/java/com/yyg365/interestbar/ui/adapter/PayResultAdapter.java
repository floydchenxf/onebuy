package com.yyg365.interestbar.ui.adapter;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yyg365.interestbar.biz.vo.model.WinningInfo;
import com.yyg365.interestbar.ui.R;

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

    private MoreClickListener moreClickListener;

    public MoreClickListener getMoreClickListener() {
        return moreClickListener;
    }

    public void setMoreClickListener(MoreClickListener moreClickListener) {
        this.moreClickListener = moreClickListener;
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
    void processHolder(Map<Integer, View> holder, final WinningInfo winningInfo) {
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
            boolean hasMore = false;
            if (joinedNums.size() >= 6) {
                for(int i=0; i<5; i++) {
                    tmpList.add(joinedNums.get(i));
                }
                tmpList.add("查看更多");
                hasMore = true;
            } else {
                tmpList.addAll(joinedNums);
                hasMore = false;
            }

            int lines = tmpList.size() % 3 == 0 ? tmpList.size() / 3 : tmpList.size() / 3 + 1;
            for (int i = 0; i < lines; i++) {
                LinearLayout layout = (LinearLayout) View.inflate(mContext, R.layout.joined_num_item, null);
                joinedNumLayout.addView(layout);
                TextView text1 = (TextView) layout.findViewById(R.id.join_number_1);
                TextView text2 = (TextView) layout.findViewById(R.id.join_number_2);
                TextView text3 = (TextView) layout.findViewById(R.id.join_number_3);
                text3.setOnClickListener(null);
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
                if (i==1 && hasMore) {
                    text3.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (moreClickListener != null) {
                                moreClickListener.onClick(winningInfo);
                            }
                        }
                    });
                } else {
                    text3.setOnClickListener(null);
                }
            }
        }


    }

    public static interface MoreClickListener {
        void onClick(WinningInfo winningInfo);
    }
}
