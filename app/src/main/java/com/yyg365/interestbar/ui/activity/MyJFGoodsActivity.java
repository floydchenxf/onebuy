package com.yyg365.interestbar.ui.activity;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.yyg365.interestbar.aync.ApiCallback;
import com.yyg365.interestbar.biz.manager.JiFenManager;
import com.yyg365.interestbar.biz.manager.LoginManager;
import com.yyg365.interestbar.biz.vo.json.DangPuItemVO;
import com.yyg365.interestbar.biz.vo.json.JFGoodsDetailVO;
import com.yyg365.interestbar.biz.vo.json.JFGoodsVO;
import com.yyg365.interestbar.ui.ImageLoaderFactory;
import com.yyg365.interestbar.ui.R;
import com.yyg365.interestbar.ui.adapter.MyJFGoodsAdapter;
import com.yyg365.interestbar.ui.adapter.MyPawnAdapter;
import com.yyg365.interestbar.ui.loading.DataLoadingView;
import com.yyg365.interestbar.ui.loading.DefaultDataLoadingView;
import com.yyg365.pullrefresh.widget.PullToRefreshBase;
import com.yyg365.pullrefresh.widget.PullToRefreshListView;

import java.util.ArrayList;
import java.util.List;

public class MyJFGoodsActivity extends Activity {

    private static final int PAGE_SIZE = 20;

    private ImageLoader mImageLoader;

    private DataLoadingView dataLoadingView;

    private PullToRefreshListView mPullToRefreshListView;

    private ListView mRefreshListView;

    private MyJFGoodsAdapter mAdapter;

    private int pageNo;

    private Long userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common);

        findViewById(R.id.title_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!MyJFGoodsActivity.this.isFinishing()) {
                    MyJFGoodsActivity.this.finish();
                }
            }
        });
        TextView titleView = (TextView) findViewById(R.id.title_name);
        titleView.setText("我的积分商品");
        titleView.setVisibility(View.VISIBLE);

        pageNo = 1;
        mImageLoader = ImageLoaderFactory.createImageLoader();

        userId = LoginManager.getLoginInfo(this).ID;

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

        mAdapter = new MyJFGoodsAdapter(this, new ArrayList<JFGoodsVO>(), mImageLoader);
        mRefreshListView.setAdapter(mAdapter);

        mPullToRefreshListView.setMode(PullToRefreshBase.Mode.PULL_UP_TO_REFRESH);
        mPullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2() {
            @Override
            public void onPullDownToRefresh() {
            }

            @Override
            public void onPullUpToRefresh() {
                pageNo++;
                loadData(false);
                mPullToRefreshListView.onRefreshComplete(false, true);
            }
        });
        View emptyView = View.inflate(this, R.layout.empty_item, null);
        mPullToRefreshListView.setEmptyView(emptyView);
        loadData(true);
    }

    private void loadData(final boolean isFirst) {
        if (isFirst) {
            dataLoadingView.startLoading();
        }
        JiFenManager.fetchMyJFGoods(userId, pageNo, PAGE_SIZE).startUI(new ApiCallback<List<JFGoodsVO>>() {
            @Override
            public void onError(int code, String errorInfo) {
                if (isFirst) {
                    dataLoadingView.loadFail();
                }
            }

            @Override
            public void onSuccess(List<JFGoodsVO> jfGoodsVOs) {
                if (isFirst) {
                    dataLoadingView.loadSuccess();
                }

                if (jfGoodsVOs != null && !jfGoodsVOs.isEmpty()) {
                    mAdapter.addAll(jfGoodsVOs, isFirst);
                }
            }

            @Override
            public void onProgress(int progress) {

            }
        });
    }
}
