package com.floyd.onebuy.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.ListView;

import com.floyd.onebuy.aync.ApiCallback;
import com.floyd.onebuy.biz.constants.APIConstants;
import com.floyd.onebuy.biz.manager.LoginManager;
import com.floyd.onebuy.biz.manager.ProductManager;
import com.floyd.onebuy.biz.vo.model.WinningInfo;
import com.floyd.onebuy.ui.R;
import com.floyd.onebuy.ui.adapter.PayResultAdapter;
import com.floyd.onebuy.ui.loading.DataLoadingView;
import com.floyd.onebuy.ui.loading.DefaultDataLoadingView;

import java.util.ArrayList;
import java.util.List;

public class PayResultActivity extends Activity implements View.OnClickListener {

    private DataLoadingView dataLoadingView;
    PayResultAdapter payResultAdapter;
    private ListView mListView;
    private String orderNo;
    private CheckedTextView viewBuyRecordView;
    private CheckedTextView gotoBuyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_result);
        orderNo = getIntent().getStringExtra(APIConstants.PAY_ORDER_NO);
        dataLoadingView = new DefaultDataLoadingView();
        dataLoadingView.initView(findViewById(R.id.act_lsloading), this);
        mListView = (ListView) findViewById(R.id.buy_list);
        payResultAdapter = new PayResultAdapter(this, new ArrayList<WinningInfo>());
        mListView.setAdapter(payResultAdapter);
        viewBuyRecordView = (CheckedTextView) findViewById(R.id.view_buy_view);
        gotoBuyView = (CheckedTextView) findViewById(R.id.goto_buy_view);
        viewBuyRecordView.setOnClickListener(this);
        gotoBuyView.setOnClickListener(this);
        loadData();
    }

    private void loadData() {
        dataLoadingView.startLoading();
        Long userId = LoginManager.getLoginInfo(this).ID;
        ProductManager.getWinningInfosByOrderNo(userId, orderNo).startUI(new ApiCallback<List<WinningInfo>>() {
            @Override
            public void onError(int code, String errorInfo) {
                dataLoadingView.loadFail();
            }

            @Override
            public void onSuccess(List<WinningInfo> winningInfos) {
                dataLoadingView.loadSuccess();
                payResultAdapter.addAll(winningInfos, true);
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
                loadData();
                break;
            case R.id.goto_buy_view:
                break;
            case R.id.view_buy_view:
                break;
        }
    }


}