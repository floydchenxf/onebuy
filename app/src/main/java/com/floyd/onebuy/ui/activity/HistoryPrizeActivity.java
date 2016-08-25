package com.floyd.onebuy.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.floyd.onebuy.aync.ApiCallback;
import com.floyd.onebuy.biz.constants.APIConstants;
import com.floyd.onebuy.biz.manager.ProductManager;
import com.floyd.onebuy.biz.vo.json.HistoryPrizeListVO;
import com.floyd.onebuy.biz.vo.json.HistoryPrizeVO;
import com.floyd.onebuy.ui.R;
import com.floyd.onebuy.ui.adapter.HistoryPrizeAdapter;
import com.floyd.onebuy.ui.loading.DataLoadingView;
import com.floyd.onebuy.ui.loading.DefaultDataLoadingView;
import com.floyd.pullrefresh.widget.PullToRefreshBase;
import com.floyd.pullrefresh.widget.PullToRefreshListView;

import java.util.ArrayList;
import java.util.List;

public class HistoryPrizeActivity extends Activity implements View.OnClickListener {

    private static final int PAGE_SIZE = 10;
    private DataLoadingView dataLoadingView;
    private PullToRefreshListView mPullToRefreshListView;
    private ListView mListView;
    private HistoryPrizeAdapter adapter;
    private int pageNo = 1;
    private boolean needClear = true;
    private TextView titleView;
    private float oneDp;
    private long proId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common);

        proId = getIntent().getLongExtra(APIConstants.PRO_ID, 0l);

        oneDp = this.getResources().getDimension(R.dimen.one_dp);

        findViewById(R.id.title_back).setOnClickListener(this);
        titleView = (TextView) findViewById(R.id.title_name);
        titleView.setText("往期揭晓");
        titleView.setVisibility(View.VISIBLE);

        dataLoadingView = new DefaultDataLoadingView();
        dataLoadingView.initView(findViewById(R.id.act_lsloading), this);

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
                loadData(false);
                mPullToRefreshListView.onRefreshComplete(false, true);

            }
        });
        View emptyView = View.inflate(this, R.layout.empty_item, null);
        mPullToRefreshListView.setEmptyView(emptyView);
        mListView = mPullToRefreshListView.getRefreshableView();
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                HistoryPrizeVO vo = adapter.getItem(i-1);
                Intent it = new Intent(HistoryPrizeActivity.this, PersionProfileActivity.class);
                it.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                it.putExtra(PersionProfileActivity.CURRENT_USER_ID, vo.ClientID);
                startActivity(it);
            }
        });
        adapter = new HistoryPrizeAdapter(this, new ArrayList<HistoryPrizeVO>(), proId);
        mListView.setAdapter(adapter);
        loadData(true);
    }

    private void loadData(final boolean isFirst) {
        if (isFirst) {
            dataLoadingView.startLoading();
        }

        ProductManager.getHistoryPrizes(PAGE_SIZE, pageNo, proId).startUI(new ApiCallback<HistoryPrizeListVO>() {
            @Override
            public void onError(int code, String errorInfo) {
                if (isFirst) {
                    dataLoadingView.loadFail();
                }
            }

            @Override
            public void onSuccess(HistoryPrizeListVO prizeListVO) {
                if (isFirst) {
                    dataLoadingView.loadSuccess();
                }

                List<HistoryPrizeVO> prizeVOs = prizeListVO.HistoryPrizeList;
                adapter.addAll(prizeVOs, needClear);
                pageNo++;
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
