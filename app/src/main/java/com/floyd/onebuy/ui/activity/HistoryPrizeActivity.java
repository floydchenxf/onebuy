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

public class HistoryPrizeActivity extends CommonActivity {

    private HistoryPrizeAdapter adapter;
    private float oneDp;
    private long proId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void initData() {
        proId = getIntent().getLongExtra(APIConstants.PRO_ID, 0l);
        oneDp = this.getResources().getDimension(R.dimen.one_dp);
        adapter = new HistoryPrizeAdapter(this, new ArrayList<HistoryPrizeVO>(), proId);
    }

    @Override
    protected String fillTitleName() {
        return "往期揭晓";
    }

    @Override
    protected void initListView(ListView mListView) {
        mListView.setAdapter(adapter);
    }

    @Override
    protected void loadIndexData() {
        loadData();
    }

    private void loadData() {
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
    protected void loadNextPageData() {
        loadData();
    }
}
