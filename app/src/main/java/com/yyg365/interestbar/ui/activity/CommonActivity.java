package com.yyg365.interestbar.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.yyg365.interestbar.ui.R;
import com.yyg365.interestbar.ui.adapter.HistoryPrizeAdapter;
import com.yyg365.interestbar.ui.loading.DataLoadingView;
import com.yyg365.interestbar.ui.loading.DefaultDataLoadingView;
import com.yyg365.pullrefresh.widget.PullToRefreshBase;
import com.yyg365.pullrefresh.widget.PullToRefreshListView;

/**
 * Created by chenxiaofeng on 16/9/5.
 */
public abstract class CommonActivity extends Activity {

    protected static final int PAGE_SIZE = 10;
    protected DataLoadingView dataLoadingView;
    protected PullToRefreshListView mPullToRefreshListView;
    protected ListView mListView;
    protected int pageNo;
    protected boolean needClear;
    protected boolean isFirst;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common);
        pageNo = 1;
        needClear = true;
        isFirst = true;
        initData();

        TextView titleView = (TextView) findViewById(R.id.title_name);
        String titleName = fillTitleName();
        if (!TextUtils.isEmpty(titleName)) {
            titleView.setText(titleName);
            titleView.setVisibility(View.VISIBLE);
        }

        findViewById(R.id.title_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CommonActivity.this.finish();
            }
        });

        dataLoadingView = new DefaultDataLoadingView();
        dataLoadingView.initView(findViewById(R.id.act_lsloading), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pageNo = 1;
                needClear = true;
                isFirst = true;
                loadIndexData();
            }
        });

        mPullToRefreshListView = (PullToRefreshListView) findViewById(R.id.common_list);
        mPullToRefreshListView.setMode(PullToRefreshBase.Mode.PULL_UP_TO_REFRESH);
        mPullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2() {
            @Override
            public void onPullDownToRefresh() {

            }

            @Override
            public void onPullUpToRefresh() {
                pageNo++;
                needClear = false;
                isFirst = false;
                loadNextPageData();
                mPullToRefreshListView.onRefreshComplete(false, true);
            }
        });

        View emptyView = View.inflate(this, R.layout.empty_item, null);
        mPullToRefreshListView.setEmptyView(emptyView);
        mListView = mPullToRefreshListView.getRefreshableView();

        initView();
        //初始化头
        initListViewHeader(mListView);

        initListView(mListView);
        //初始化尾
        initListViewFooter(mListView);

        loadIndexData();
    }

    protected void initData() {
    }

    protected void initView() {
    }

    protected abstract String fillTitleName();

    /**
     * 初始化listview
     *
     * @param mListView
     */
    protected abstract void initListView(ListView mListView);

    /**
     * 初始化listview头
     *
     * @param mListView
     */
    protected void initListViewHeader(ListView mListView) {
    }


    /**
     * 初始化listview尾
     *
     * @param mListView
     */
    protected void initListViewFooter(ListView mListView) {
    }

    /**
     * 获取进入页面的首要数据
     */
    protected abstract void loadIndexData();

    /**
     * 获取下一页数据
     */
    protected abstract void loadNextPageData();
}