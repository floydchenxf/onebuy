package com.floyd.onebuy.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.floyd.onebuy.aync.ApiCallback;
import com.floyd.onebuy.biz.manager.LoginManager;
import com.floyd.onebuy.biz.manager.ProductManager;
import com.floyd.onebuy.biz.vo.json.ProductLssueWithWinnerVO;
import com.floyd.onebuy.event.TabSwitchEvent;
import com.floyd.onebuy.ui.ImageLoaderFactory;
import com.floyd.onebuy.ui.R;
import com.floyd.onebuy.ui.adapter.LuckRecordAdapter;
import com.floyd.onebuy.ui.loading.DataLoadingView;
import com.floyd.onebuy.ui.loading.DefaultDataLoadingView;
import com.floyd.pullrefresh.widget.PullToRefreshBase;
import com.floyd.pullrefresh.widget.PullToRefreshListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.greenrobot.event.EventBus;

public class MyLuckActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "MyLuckActivity";
    private static int PAGE_SIZE = 12;
    private PullToRefreshListView mPullToRefreshListView;
    private int pageNo = 1;
    private boolean needClear;
    private ListView mListView;
    private DataLoadingView dataLoadingView;
    private LuckRecordAdapter luckRecordAdapter;
    private ImageLoader mImageLoader;
    private View emptyView;
    private TextView gotoIndexView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_luck);
        findViewById(R.id.title_back).setOnClickListener(this);
        TextView titleNameView = (TextView)findViewById(R.id.title_name);
        titleNameView.setText("中奖记录");
        titleNameView.setVisibility(View.VISIBLE);

        mImageLoader = ImageLoaderFactory.createImageLoader();

        dataLoadingView = new DefaultDataLoadingView();
        dataLoadingView.initView(findViewById(R.id.act_lsloading), this);

        mPullToRefreshListView = (PullToRefreshListView) findViewById(R.id.common_list);
        mPullToRefreshListView.setMode(PullToRefreshBase.Mode.BOTH);
        mPullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2() {
            @Override
            public void onPullDownToRefresh() {
                pageNo = 1;
                needClear = true;
                loadData();
                mPullToRefreshListView.onRefreshComplete(false, true);
            }

            @Override
            public void onPullUpToRefresh() {
                pageNo++;
                needClear = false;
                loadData();
                mPullToRefreshListView.onRefreshComplete(false, true);

            }
        });
        mListView = mPullToRefreshListView.getRefreshableView();
        luckRecordAdapter = new LuckRecordAdapter(this, new ArrayList<ProductLssueWithWinnerVO>(), mImageLoader);
        mListView.setAdapter(luckRecordAdapter);
        emptyView = findViewById(R.id.empty_view);
        gotoIndexView = (TextView) emptyView.findViewById(R.id.goto_index);
        gotoIndexView.setOnClickListener(this);
        loadData();
    }

    private void loadData() {
        long uid = LoginManager.getLoginInfo(this).ID;
        ProductManager.fetchMyLuckRecords(uid, pageNo, PAGE_SIZE).startUI(new ApiCallback<List<ProductLssueWithWinnerVO>>() {
            @Override
            public void onError(int code, String errorInfo) {
                Toast.makeText(MyLuckActivity.this, errorInfo, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(List<ProductLssueWithWinnerVO> productLssueWithWinnerVOs) {
                luckRecordAdapter.addAll(productLssueWithWinnerVOs, needClear);
                if (luckRecordAdapter.getRecords().isEmpty()) {
                    mListView.setVisibility(View.GONE);
                    emptyView.setVisibility(View.VISIBLE);
                } else {
                    mListView.setVisibility(View.VISIBLE);
                    emptyView.setVisibility(View.GONE);
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
                loadData();
                break;
            case R.id.goto_index:
                EventBus.getDefault().post(new TabSwitchEvent(R.id.tab_index_page, new HashMap<String, Object>()));
                break;
        }

    }
}
