package com.yyg365.interestbar.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.yyg365.interestbar.aync.ApiCallback;
import com.yyg365.interestbar.biz.manager.LoginManager;
import com.yyg365.interestbar.biz.manager.PawnManager;
import com.yyg365.interestbar.biz.vo.json.DangPuItemVO;
import com.yyg365.interestbar.ui.ImageLoaderFactory;
import com.yyg365.interestbar.ui.R;
import com.yyg365.interestbar.ui.adapter.MyPawnAdapter;
import com.yyg365.interestbar.ui.adapter.PawnAdapter;
import com.yyg365.interestbar.ui.loading.DataLoadingView;
import com.yyg365.interestbar.ui.loading.DefaultDataLoadingView;
import com.yyg365.pullrefresh.widget.PullToRefreshBase;
import com.yyg365.pullrefresh.widget.PullToRefreshListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenxiaofeng on 2017/1/5.
 */
public class MyPawnActivity extends Activity {

    private static final int PAGE_SIZE = 20;

    private ImageLoader mImageLoader;

    private DataLoadingView dataLoadingView;

    private PullToRefreshListView mPullToRefreshListView;

    private ListView mRefreshListView;

    private MyPawnAdapter mAdapter;

    private int pageNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common);
        findViewById(R.id.title_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!MyPawnActivity.this.isFinishing()) {
                    MyPawnActivity.this.finish();
                }
            }
        });
        TextView titleView = (TextView) findViewById(R.id.title_name);
        titleView.setText("我的典當");
        titleView.setVisibility(View.VISIBLE);

        pageNo = 1;
        mImageLoader = ImageLoaderFactory.createImageLoader();

        dataLoadingView = new DefaultDataLoadingView();
        dataLoadingView.initView(findViewById(R.id.act_lsloading), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pageNo = 1;
                loadData(true);
            }
        });

        mPullToRefreshListView = (PullToRefreshListView) findViewById(R.id.common_list);
        mRefreshListView = mPullToRefreshListView.getRefreshableView();

        mAdapter = new MyPawnAdapter(this, new ArrayList<DangPuItemVO>(), mImageLoader);
        mRefreshListView.setAdapter(mAdapter);

        mPullToRefreshListView.setMode(PullToRefreshBase.Mode.PULL_UP_TO_REFRESH);
        mPullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2() {
            @Override
            public void onPullDownToRefresh() {
            }

            @Override
            public void onPullUpToRefresh() {
                loadData(false);
                mPullToRefreshListView.onRefreshComplete(false, true);
            }
        });
        View emptyView = View.inflate(this, R.layout.empty_item, null);
        mPullToRefreshListView.setEmptyView(emptyView);
    }

    protected void onResume() {
        super.onResume();
        pageNo = 1;
        loadData(true);
    }



    private void loadData(final boolean isFirst) {
        if (isFirst) {
            dataLoadingView.startLoading();
        }

        Long id = LoginManager.getLoginInfo(this).ID;
        PawnManager.fetchPawnProducts(id, pageNo, PAGE_SIZE).startUI(new ApiCallback<List<DangPuItemVO>>() {
            @Override
            public void onError(int code, String errorInfo) {
                if (isFirst) {
                    dataLoadingView.loadFail();
                }
            }

            @Override
            public void onSuccess(List<DangPuItemVO> dangPuItemVOs) {
                if (isFirst) {
                    dataLoadingView.loadSuccess();
                }
                pageNo++;
                mAdapter.addAll(dangPuItemVOs, isFirst);
            }

            @Override
            public void onProgress(int progress) {

            }
        });


    }
}
