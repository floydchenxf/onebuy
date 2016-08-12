package com.floyd.onebuy.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.floyd.onebuy.aync.ApiCallback;
import com.floyd.onebuy.biz.manager.LoginManager;
import com.floyd.onebuy.biz.manager.OrderManager;
import com.floyd.onebuy.biz.vo.json.ChargeVO;
import com.floyd.onebuy.ui.R;
import com.floyd.onebuy.ui.adapter.ChargeAdapter;
import com.floyd.onebuy.ui.loading.DataLoadingView;
import com.floyd.onebuy.ui.loading.DefaultDataLoadingView;
import com.floyd.pullrefresh.widget.PullToRefreshBase;
import com.floyd.pullrefresh.widget.PullToRefreshListView;

import java.util.ArrayList;
import java.util.List;

public class ChargeListActivity extends Activity implements View.OnClickListener {

    private PullToRefreshListView mPullToRefreshListView;
    private DataLoadingView dataLoadingView;

    private ListView mListView;
    private ChargeAdapter mAdapter;
    private int pageNo = 1;
    private int PAGE_SIZE = 10;
    private boolean needClear = false;
    private TextView payAtOnceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charge_list);
        findViewById(R.id.title_back).setOnClickListener(this);

        TextView titleNameView = (TextView) findViewById(R.id.title_name);
        titleNameView.setText("充值记录");
        titleNameView.setVisibility(View.VISIBLE);

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
        mAdapter = new ChargeAdapter(this, new ArrayList<ChargeVO>());
        mListView.setAdapter(mAdapter);

        payAtOnceView = (TextView) findViewById(R.id.pay_at_once_view);
        payAtOnceView.setOnClickListener(this);
        loadData(true);
    }

    private void loadData(final boolean firstLoad) {
        if (firstLoad) {
            dataLoadingView.startLoading();
        }

        Long userId = LoginManager.getLoginInfo(this).ID;
        OrderManager.getRecharge(userId, pageNo, PAGE_SIZE).startUI(new ApiCallback<List<ChargeVO>>() {
            @Override
            public void onError(int code, String errorInfo) {
                if (firstLoad) {
                    dataLoadingView.loadFail();
                }

            }

            @Override
            public void onSuccess(List<ChargeVO> chargeVOs) {
                if (firstLoad) {
                    dataLoadingView.loadSuccess();
                }

                mAdapter.addAll(chargeVOs, needClear);
            }

            @Override
            public void onProgress(int progress) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.act_ls_fail_layout:
                loadData(true);
                break;
            case R.id.title_back:
                this.finish();
                break;
            case R.id.pay_at_once_view:
                Intent it = new Intent(this, PayChargeActivity.class);
                startActivity(it);
                this.finish();
                break;
        }


    }
}
