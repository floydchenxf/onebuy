package com.yyg365.interestbar.ui.activity;

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

import com.yyg365.interestbar.aync.ApiCallback;
import com.yyg365.interestbar.biz.constants.APIConstants;
import com.yyg365.interestbar.biz.manager.ProductManager;
import com.yyg365.interestbar.biz.vo.json.HistoryPrizeListVO;
import com.yyg365.interestbar.biz.vo.json.HistoryPrizeVO;
import com.yyg365.interestbar.ui.R;
import com.yyg365.interestbar.ui.adapter.HistoryPrizeAdapter;
import com.yyg365.interestbar.ui.loading.DataLoadingView;
import com.yyg365.interestbar.ui.loading.DefaultDataLoadingView;
import com.yyg365.pullrefresh.widget.PullToRefreshBase;
import com.yyg365.pullrefresh.widget.PullToRefreshListView;

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
