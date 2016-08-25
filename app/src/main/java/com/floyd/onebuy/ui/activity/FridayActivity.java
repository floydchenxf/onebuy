package com.floyd.onebuy.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.floyd.onebuy.aync.ApiCallback;
import com.floyd.onebuy.biz.manager.ProductManager;
import com.floyd.onebuy.biz.vo.model.WinningInfo;
import com.floyd.onebuy.ui.ImageLoaderFactory;
import com.floyd.onebuy.ui.R;
import com.floyd.onebuy.ui.adapter.FridayAdapter;
import com.floyd.onebuy.ui.loading.DataLoadingView;
import com.floyd.onebuy.ui.loading.DefaultDataLoadingView;
import com.floyd.pullrefresh.widget.PullToRefreshBase;
import com.floyd.pullrefresh.widget.PullToRefreshListView;

import java.util.ArrayList;
import java.util.List;


public class FridayActivity extends Activity implements View.OnClickListener {

    private DataLoadingView dataLoadingView;
    private ImageLoader mImageLoader;
    private PullToRefreshListView mPullToRefreshListView;
    private ListView mListView;
    private FridayAdapter fridayAdapter;

    private int PAGE_SIZE = 10;
    private int pageNo = 1;
    private boolean needClear = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friday);
        dataLoadingView = new DefaultDataLoadingView();
        dataLoadingView.initView(findViewById(R.id.act_lsloading), this);
        mImageLoader = ImageLoaderFactory.createImageLoader();
        findViewById(R.id.title_back).setOnClickListener(this);
        TextView titleName = (TextView) findViewById(R.id.title_name);
        titleName.setVisibility(View.VISIBLE);
        titleName.setText("快乐星期五");

        mPullToRefreshListView = (PullToRefreshListView) findViewById(R.id.friday_list);
        mPullToRefreshListView.setMode(PullToRefreshBase.Mode.PULL_UP_TO_REFRESH);
        mPullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2() {
            @Override
            public void onPullDownToRefresh() {

            }

            @Override
            public void onPullUpToRefresh() {
                needClear = false;
                pageNo++;
                loadData(false);
            }
        });
        View emptyView = View.inflate(this, R.layout.empty_item, null);
        mPullToRefreshListView.setEmptyView(emptyView);
        mListView = mPullToRefreshListView.getRefreshableView();
        fridayAdapter = new FridayAdapter(this, new ArrayList<WinningInfo>(), mImageLoader);
        mListView.setAdapter(fridayAdapter);
        loadData(true);
    }

    private void loadData(final boolean isFirst) {
        if (isFirst) {
            dataLoadingView.startLoading();
        }

        ProductManager.fetchFridayProduct(pageNo, PAGE_SIZE, null).startUI(new ApiCallback<List<WinningInfo>>() {
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
                fridayAdapter.addAll(winningInfos, needClear);
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
