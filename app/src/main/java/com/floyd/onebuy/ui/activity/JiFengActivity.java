package com.floyd.onebuy.ui.activity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.floyd.onebuy.aync.ApiCallback;
import com.floyd.onebuy.biz.manager.JiFengManager;
import com.floyd.onebuy.biz.manager.LoginManager;
import com.floyd.onebuy.biz.vo.json.JiFengVO;
import com.floyd.onebuy.ui.R;
import com.floyd.onebuy.ui.adapter.JiFengAdapter;
import com.floyd.onebuy.ui.loading.DataLoadingView;
import com.floyd.onebuy.ui.loading.DefaultDataLoadingView;
import com.floyd.pullrefresh.widget.PullToRefreshBase;
import com.floyd.pullrefresh.widget.PullToRefreshListView;

import java.util.ArrayList;
import java.util.List;

public class JiFengActivity extends Activity implements View.OnClickListener {

    private DataLoadingView dataLoadingView;
    private PullToRefreshListView mPullToRefreshListView;
    private ListView mListView;
    private JiFengAdapter adapter;
    private int pageNo = 1;
    private int PAGE_SIZE = 10;
    private boolean needClear = false;
    private TextView titleView;
    private float oneDp;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fee_record);

        oneDp = this.getResources().getDimension(R.dimen.one_dp);

        findViewById(R.id.title_back).setOnClickListener(this);
        titleView = (TextView) findViewById(R.id.title_name);
        titleView.setText("积分记录");
        titleView.setVisibility(View.VISIBLE);

        dataLoadingView = new DefaultDataLoadingView();
        dataLoadingView.initView(findViewById(R.id.act_lsloading), this);

        mPullToRefreshListView = (PullToRefreshListView) findViewById(R.id.fee_list);
        mPullToRefreshListView.setMode(PullToRefreshBase.Mode.PULL_UP_TO_REFRESH);
        mPullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2() {
            @Override
            public void onPullDownToRefresh() {

            }

            @Override
            public void onPullUpToRefresh() {
                pageNo++;
                needClear = false;
                loadData(false);
                mPullToRefreshListView.onRefreshComplete(false, true);

            }
        });
        mListView = mPullToRefreshListView.getRefreshableView();
        adapter = new JiFengAdapter(this, new ArrayList<JiFengVO>());
        mListView.setAdapter(adapter);
        loadData(true);

    }

    private void loadData(final boolean isFirst) {
        if (isFirst) {
            dataLoadingView.startLoading();
        }

        long userId = LoginManager.getLoginInfo(this).ID;

        JiFengManager.fetchJiFengList(userId, PAGE_SIZE, pageNo, 1).startUI(new ApiCallback<List<JiFengVO>>() {
            @Override
            public void onError(int code, String errorInfo) {
                if (isFirst) {
                    dataLoadingView.loadFail();
                }
            }

            @Override
            public void onSuccess(List<JiFengVO> jiFengVOs) {
                if (isFirst) {
                    dataLoadingView.loadSuccess();
                }
                adapter.addAll(jiFengVOs, needClear);
                pageNo++;
                if (adapter.getRecords() == null || adapter.getRecords().isEmpty()) {
                    TextView emptyView = new TextView(JiFengActivity.this);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) (40 * oneDp));
                    emptyView.setGravity(Gravity.CENTER);
                    emptyView.setLayoutParams(params);
                    emptyView.setText("暂无数据");
                    emptyView.setTextColor(Color.BLACK);
                    mPullToRefreshListView.setEmptyView(emptyView);
                } else {
                    View head = View.inflate(JiFengActivity.this, R.layout.jifeng_head, null);
                    mListView.addHeaderView(head);
                }
            }

            @Override
            public void onProgress(int progress) {

            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_back:
                this.finish();
                break;
            case R.id.act_ls_fail_layout:
                pageNo = 1;
                needClear = true;
                loadData(true);
                break;
        }
    }
}
