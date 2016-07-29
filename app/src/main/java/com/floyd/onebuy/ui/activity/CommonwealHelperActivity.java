package com.floyd.onebuy.ui.activity;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.android.volley.toolbox.ImageLoader;
import com.floyd.onebuy.biz.manager.CommonwealManager;
import com.floyd.onebuy.ui.ImageLoaderFactory;
import com.floyd.onebuy.ui.R;
import com.floyd.onebuy.ui.adapter.CommonwealAdapter;
import com.floyd.onebuy.ui.loading.DataLoadingView;
import com.floyd.onebuy.ui.loading.DefaultDataLoadingView;
import com.floyd.pullrefresh.widget.PullToRefreshBase;
import com.floyd.pullrefresh.widget.PullToRefreshListView;

public class CommonwealHelperActivity extends Activity implements View.OnClickListener {

    private ImageLoader mImageLoader;
    private PullToRefreshListView mPullToRefreshListView;
    private ListView mListView;

    private DataLoadingView dataLoadingView;
    private int pageNo;
    private boolean needClear;
    private boolean isFirst;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commonweal_helper);
        mImageLoader = ImageLoaderFactory.createImageLoader();
        dataLoadingView = new DefaultDataLoadingView();
        dataLoadingView.initView(findViewById(R.id.act_ls_fail_layout), this);
        pageNo = 1;

        mPullToRefreshListView = (PullToRefreshListView) findViewById(R.id.common_list);
        mPullToRefreshListView.setMode(PullToRefreshBase.Mode.BOTH);
        mPullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2() {
            @Override
            public void onPullDownToRefresh() {
                needClear = true;
                pageNo = 1;
                isFirst = true;
                loadData();
                mPullToRefreshListView.onRefreshComplete(false, true);
            }

            @Override
            public void onPullUpToRefresh() {
                needClear = false;
                pageNo++;
                isFirst = false;
                loadData();
                mPullToRefreshListView.onRefreshComplete(false, true);
            }
        });

        View emptyView = View.inflate(this, R.layout.empty_item, null);
        mPullToRefreshListView.setEmptyView(emptyView);
        mListView = mPullToRefreshListView.getRefreshableView();
        initHeader();
    }

    private void loadData() {
//        if (isFirst) {
//            dataLoadingView.startLoading();
//        }

    }

    private void initHeader() {
    }

    @Override
    public void onClick(View v) {

    }
}
