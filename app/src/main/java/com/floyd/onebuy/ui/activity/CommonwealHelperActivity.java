package com.floyd.onebuy.ui.activity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.floyd.onebuy.aync.ApiCallback;
import com.floyd.onebuy.biz.manager.CommonwealManager;
import com.floyd.onebuy.biz.vo.commonweal.CommonwealHelperList;
import com.floyd.onebuy.biz.vo.commonweal.CommonwealHelperVO;
import com.floyd.onebuy.ui.ImageLoaderFactory;
import com.floyd.onebuy.ui.R;
import com.floyd.onebuy.ui.adapter.CommonwealHelperAdapter;
import com.floyd.onebuy.ui.loading.DataLoadingView;
import com.floyd.onebuy.ui.loading.DefaultDataLoadingView;
import com.floyd.pullrefresh.widget.PullToRefreshBase;
import com.floyd.pullrefresh.widget.PullToRefreshListView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CommonwealHelperActivity extends Activity implements View.OnClickListener {

    private static final int PAGE_SIZE = 10;
    public static final String PRODUCT_ID = "PRODUCT_ID";
    private ImageLoader mImageLoader;
    private PullToRefreshListView mPullToRefreshListView;

    private ListView mListView;
    private DataLoadingView dataLoadingView;
    private CommonwealHelperAdapter commonwealHelperAdapter;
    private int pageNo;
    private boolean needClear;
    private boolean isFirst;
    private TextView totalNumView;
    private float dp;

    private long pid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commonweal_helper);
        pid = getIntent().getLongExtra(PRODUCT_ID, 0);
        mImageLoader = ImageLoaderFactory.createImageLoader();
        dataLoadingView = new DefaultDataLoadingView();
        dataLoadingView.initView(findViewById(R.id.act_lsloading), this);
        pageNo = 1;
        isFirst = true;
        dp = getResources().getDimension(R.dimen.one_dp);

        findViewById(R.id.title_back).setOnClickListener(this);
        TextView titleNameView = (TextView) findViewById(R.id.title_name);
        titleNameView.setText("基金池");
        titleNameView.setVisibility(View.VISIBLE);

        mPullToRefreshListView = (PullToRefreshListView) findViewById(R.id.common_list);
        mPullToRefreshListView.setMode(PullToRefreshBase.Mode.PULL_UP_TO_REFRESH);
        mPullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2() {
            @Override
            public void onPullDownToRefresh() {
//                needClear = true;
//                pageNo = 1;
//                isFirst = true;
//                loadData();
//                mPullToRefreshListView.onRefreshComplete(false, true);
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
        commonwealHelperAdapter = new CommonwealHelperAdapter(this, new ArrayList<CommonwealHelperVO>(), mImageLoader);
        mListView.setAdapter(commonwealHelperAdapter);
        initHeader();
        loadData();
    }

    private void loadData() {
        if (isFirst) {
            dataLoadingView.startLoading();
        }
        CommonwealManager.fetchHelpPersonList(pid, pageNo, PAGE_SIZE).startUI(new ApiCallback<CommonwealHelperList>() {
            @Override
            public void onError(int code, String errorInfo) {
                if (isFirst) {
                    dataLoadingView.loadFail();
                }
            }

            @Override
            public void onSuccess(CommonwealHelperList commonwealHelperList) {
                if (isFirst) {
                    dataLoadingView.loadSuccess();
                    totalNumView.setText("感谢"+commonwealHelperList.PersonCount+"位爱心朋友");
                }

                List<CommonwealHelperVO> personList = commonwealHelperList.PersonList;
                commonwealHelperAdapter.addAll(personList, needClear);
            }

            @Override
            public void onProgress(int progress) {

            }
        });

    }

    private void initHeader() {
        LinearLayout totalNumLayout = new LinearLayout(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        totalNumLayout.setLayoutParams(params);
        totalNumView = new TextView(this);
        totalNumView.setTextSize(14);
        totalNumView.setTextColor(Color.parseColor("#666666"));
        totalNumView.setPadding((int)(14*dp), (int)(10*dp), (int)(14*dp), (int)(10*dp));
        totalNumLayout.addView(totalNumView);
        mListView.addHeaderView(totalNumLayout);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.act_ls_fail_layout:
                pageNo = 1;
                needClear = true;
                isFirst = true;
                loadData();
                break;
            case R.id.title_back:
                this.finish();
                break;
        }

    }
}
