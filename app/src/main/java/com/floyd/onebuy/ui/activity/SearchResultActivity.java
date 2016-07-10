package com.floyd.onebuy.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.floyd.onebuy.aync.ApiCallback;
import com.floyd.onebuy.biz.manager.ProductManager;
import com.floyd.onebuy.biz.vo.model.WinningInfo;
import com.floyd.onebuy.ui.R;
import com.floyd.onebuy.ui.adapter.ProductAdapter;
import com.floyd.onebuy.ui.loading.DataLoadingView;
import com.floyd.onebuy.ui.loading.DefaultDataLoadingView;
import com.floyd.pullrefresh.widget.PullToRefreshBase;
import com.floyd.pullrefresh.widget.PullToRefreshListView;

import java.util.ArrayList;
import java.util.List;

public class SearchResultActivity extends Activity implements View.OnClickListener {

    private static final int PAGE_SIZE = 10;
    private DataLoadingView dataLoadingView;
    private PullToRefreshListView mPullToRefreshListView;
    private ListView mListView;
    private ProductAdapter productAdapter;
    private TextView titleView;
    private View searchContentView;
    private int pageNo = 1;
    private boolean needClear = true;
    private String searchWord;
    private float oneDp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        searchWord = getIntent().getStringExtra(SearchActivity.SEARCH_WORD);
        oneDp = this.getResources().getDimension(R.dimen.one_dp);
        findViewById(R.id.title_back).setOnClickListener(this);

        searchContentView = findViewById(R.id.search_content_view);
        searchContentView.setOnClickListener(this);

        dataLoadingView = new DefaultDataLoadingView();
        dataLoadingView.initView(findViewById(R.id.act_lsloading), this);

        mPullToRefreshListView = (PullToRefreshListView) findViewById(R.id.common_list);
        mPullToRefreshListView.setMode(PullToRefreshBase.Mode.BOTH);
        mPullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2() {
            @Override
            public void onPullDownToRefresh() {
                pageNo = 1;
                needClear = true;
                loadData(false);
                mPullToRefreshListView.onRefreshComplete(false, true);
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
        productAdapter = new ProductAdapter(this, new ArrayList<WinningInfo>());
        mListView.setAdapter(productAdapter);
        loadData(true);
    }

    private void loadData(final boolean isFirst) {

        if (isFirst) {
            dataLoadingView.startLoading();
        }

        ProductManager.searchProduct(searchWord, PAGE_SIZE, pageNo).startUI(new ApiCallback<List<WinningInfo>>() {
            @Override
            public void onError(int code, String errorInfo) {
                if (isFirst) {
                    dataLoadingView.loadFail();
                }
            }

            @Override
            public void onSuccess(List<WinningInfo> winningInfos) {
                if (isFirst) {
                    dataLoadingView.loadSuccess();
                }

                productAdapter.addAll(winningInfos, needClear);
                pageNo++;

                if (productAdapter.getProductList() == null || productAdapter.getProductList().isEmpty()) {
                    TextView emptyView = new TextView(SearchResultActivity.this);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) (40 * oneDp));
                    emptyView.setGravity(Gravity.CENTER);
                    emptyView.setLayoutParams(params);
                    emptyView.setText("暂无数据");
                    emptyView.setTextColor(Color.BLACK);
                    mPullToRefreshListView.setEmptyView(emptyView);
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
            case R.id.search_content_view:
                Intent it = new Intent(this, SearchActivity.class);
                it.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(it);
                break;
        }
    }
}

