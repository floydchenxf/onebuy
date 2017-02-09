package com.yyg365.interestbar.ui.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.yyg365.interestbar.biz.tools.DateUtil;
import com.yyg365.interestbar.biz.vo.json.MsgItemVO;
import com.yyg365.interestbar.ui.R;

import java.util.List;
import java.util.Map;

/**
 * Created by chenxiaofeng on 16/10/27.
 */
public class MsgBoxAdapter extends BaseDataAdapter<MsgItemVO> {

    public MsgBoxAdapter(Context context, List<MsgItemVO> records) {
        super(context, records);
    }

    @Override
    View getLayoutView() {
        return View.inflate(mContext, R.layout.msg_item, null);
    }

    @Override
    int[] cacheViews() {
        return new int[]{R.id.msg_title_view, R.id.send_time_view};
    }

    @Override
    void processHolder(Map<Integer, View> holder, MsgItemVO vo) {
        TextView msgTitleView = (TextView) holder.get(R.id.msg_title_view);
        msgTitleView.setText(vo.Title);
        TextView sendTimeView = (TextView) holder.get(R.id.send_time_view);
        String time = DateUtil.getDateTime(vo.getSendTime());
        sendTimeView.setText(time);
    }
}
