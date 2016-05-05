package com.floyd.onebuy.ui.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.floyd.onebuy.biz.vo.json.JiFengVO;
import com.floyd.onebuy.ui.R;

import java.util.List;
import java.util.Map;

/**
 * Created by floyd on 16-5-6.
 */
public class JiFengAdapter extends BaseDataAdapter<JiFengVO> {

    public JiFengAdapter(Context context, List<JiFengVO> records) {
        super(context, records);
    }

    @Override
    View getLayoutView() {
        return View.inflate(mContext, R.layout.jifeng_item, null);
    }

    @Override
    int[] cacheViews() {
        return new int[]{R.id.jifeng_num_view, R.id.jifeng_source_view, R.id.in_time_vew};
    }

    @Override
    void processHolder(Map<Integer, View> holder, JiFengVO jiFengVO) {
        TextView jifengNumView = (TextView) holder.get(R.id.jifeng_num_view);
        TextView jifengSourceView = (TextView) holder.get(R.id.jifeng_source_view);
        TextView inTimeView = (TextView) holder.get(R.id.in_time_vew);

        jifengNumView.setText(jiFengVO.BuyJf + "");
        jifengSourceView.setText(jiFengVO.JFSourceInfo);
        inTimeView.setText(jiFengVO.InTime);

    }
}
