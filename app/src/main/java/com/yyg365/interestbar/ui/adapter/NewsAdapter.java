package com.yyg365.interestbar.ui.adapter;


import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.yyg365.interestbar.biz.tools.DateUtil;
import com.yyg365.interestbar.biz.vo.json.NewsVO;
import com.yyg365.interestbar.ui.R;

import java.util.List;
import java.util.Map;

/**
 * Created by chenxiaofeng on 16/9/24.
 */
public class NewsAdapter extends BaseDataAdapter<NewsVO> {
    public NewsAdapter(Context context, List<NewsVO> records) {
        super(context, records);
    }

    @Override
    View getLayoutView() {
        return View.inflate(mContext, R.layout.news_item, null);
    }

    @Override
    int[] cacheViews() {
        return new int[]{R.id.news_title_view, R.id.publish_time_view};
    }

    @Override
    void processHolder(Map<Integer, View> holder, NewsVO newsVO) {
        TextView titleView = (TextView) holder.get(R.id.news_title_view);
        TextView publishTimeView = (TextView) holder.get(R.id.publish_time_view);
        titleView.setText(newsVO.Title);
        String dateTime = DateUtil.getDateTime(newsVO.getPublishTime());
        publishTimeView.setText(dateTime);
    }
}
