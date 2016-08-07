package com.floyd.onebuy.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.floyd.onebuy.aync.ApiCallback;
import com.floyd.onebuy.biz.manager.LoginManager;
import com.floyd.onebuy.biz.manager.ProductManager;
import com.floyd.onebuy.biz.vo.model.WinningInfo;
import com.floyd.onebuy.ui.ImageLoaderFactory;
import com.floyd.onebuy.ui.R;
import com.floyd.onebuy.ui.adapter.JoinedNumAdapter;
import com.floyd.onebuy.ui.adapter.WinningRecordAdapter;
import com.floyd.onebuy.ui.loading.DataLoadingView;
import com.floyd.onebuy.ui.loading.DefaultDataLoadingView;
import com.floyd.onebuy.view.LeftDownPopupWindow;
import com.floyd.pullrefresh.widget.PullToRefreshBase;
import com.floyd.pullrefresh.widget.PullToRefreshListView;

import java.util.ArrayList;
import java.util.List;

public class WinningRecordActivity extends Activity implements View.OnClickListener {
    private DataLoadingView dataLoadingView;
    private PullToRefreshListView mPullToRefreshListView;
    private ImageLoader mImageLoader;
    private ListView mListView;
    private WinningRecordAdapter adapter;
    private int pageNo = 1;
    private int PAGE_SIZE = 10;
    private boolean needClear = false;
    private TextView titleView;
    private float oneDp;

    private CheckedTextView allStatusView;
    private CheckedTextView doingStatusView;
    private CheckedTextView lotestStatusView;
    private CheckedTextView doneStatusView;

    private CheckedTextView[] statusChecks;

    private int taskStatus = 3;

    private LeftDownPopupWindow joinedPopupWindow;

    private TextView popProductCodeView;
    private TextView popProductTitleView;
    private TextView popJoinedCountView;
    private ListView popJoinNumListView;
    private JoinedNumAdapter joinedNumAdapter;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_winning_record);
        oneDp = this.getResources().getDimension(R.dimen.one_dp);

        findViewById(R.id.title_back).setOnClickListener(this);
        titleView = (TextView) findViewById(R.id.title_name);
        titleView.setText("夺宝记录");
        titleView.setVisibility(View.VISIBLE);

        dataLoadingView = new DefaultDataLoadingView();
        dataLoadingView.initView(findViewById(R.id.act_lsloading), this);
        mImageLoader = ImageLoaderFactory.createImageLoader();

        mPullToRefreshListView = (PullToRefreshListView) findViewById(R.id.winning_record_list);
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

        View emptyView = View.inflate(this, R.layout.empty_item, null);
        mPullToRefreshListView.setEmptyView(emptyView);


        mListView = mPullToRefreshListView.getRefreshableView();
        adapter = new WinningRecordAdapter(this, mImageLoader, new ArrayList<WinningInfo>());
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                WinningInfo info = adapter.getItem(position-1);
                Intent it = new Intent(WinningRecordActivity.this, WinningDetailActivity.class);
                if (info.status == WinningInfo.STATUS_CHOOSE ) {
                    it.putExtra(WinningDetailActivity.LASTEST, true);
                } else {
                    it.putExtra(WinningDetailActivity.LASTEST, false);
                }
                it.putExtra(WinningDetailActivity.PRODUCT_ID, info.productId);
                it.putExtra(WinningDetailActivity.LSSUE_ID, info.id);
                startActivity(it);
            }
        });

        adapter.setViewJoinNumberClickListener(new WinningRecordAdapter.ViewJoinNumberClickListener() {
            @Override
            public void onClick(WinningInfo winningInfo) {
                popProductCodeView.setText("第" + winningInfo.code + "期");
                popProductTitleView.setText(winningInfo.getTitle());
                int num = winningInfo.myPrizeCodes == null ? 0 : winningInfo.myPrizeCodes.size();
                popJoinedCountView.setText(Html.fromHtml("<font color=\"red\">" + num + "</font>人次"));
                List<String> list = winningInfo.myPrizeCodes;
                joinedNumAdapter.addAll(list, true);
                joinedPopupWindow.showPopUpWindow();
            }
        });
        allStatusView = (CheckedTextView) findViewById(R.id.all_status);
        doingStatusView = (CheckedTextView) findViewById(R.id.doing_status);
        lotestStatusView = (CheckedTextView) findViewById(R.id.lottest_status);
        doneStatusView = (CheckedTextView) findViewById(R.id.done_status);

        statusChecks = new CheckedTextView[]{doingStatusView, lotestStatusView, doneStatusView, allStatusView};

        allStatusView.setOnClickListener(this);
        doingStatusView.setOnClickListener(this);
        lotestStatusView.setOnClickListener(this);
        doneStatusView.setOnClickListener(this);

        joinedPopupWindow = new LeftDownPopupWindow(this);
        joinedPopupWindow.initView(R.layout.pop_join_num, new LeftDownPopupWindow.ViewInit() {
            @Override
            public void initView(View v) {
                popProductCodeView = (TextView) v.findViewById(R.id.pop_product_code_view);
                popProductTitleView = (TextView) v.findViewById(R.id.pop_product_title_view);
                popJoinedCountView = (TextView) v.findViewById(R.id.pop_joined_count_view);
                popJoinNumListView = (ListView) v.findViewById(R.id.pop_joined_num_listview);
                joinedNumAdapter = new JoinedNumAdapter(WinningRecordActivity.this, new ArrayList<String>());
                popJoinNumListView.setAdapter(joinedNumAdapter);
            }
        });


        loadData(true);

    }

    private void loadData(final boolean isFirst) {
        if (isFirst) {
            dataLoadingView.startLoading();
        }

        long userId = LoginManager.getLoginInfo(this).ID;
        ProductManager.getPrizeHistory(userId, PAGE_SIZE, pageNo, taskStatus).startUI(new ApiCallback<List<WinningInfo>>() {
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

                adapter.addAll(winningInfos, needClear);
                pageNo++;
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
            case R.id.doing_status:
                taskStatus = 0;
                checkStatus(taskStatus);
                pageNo = 1;
                needClear = true;
                loadData(false);
                break;
            case R.id.lottest_status:
                taskStatus = 1;
                checkStatus(taskStatus);
                pageNo = 1;
                needClear = true;
                loadData(false);
                break;
            case R.id.done_status:
                taskStatus = 2;
                pageNo = 1;
                needClear = true;
                checkStatus(taskStatus);
                loadData(false);
                break;
            case R.id.all_status:
                taskStatus = 3;
                pageNo = 1;
                needClear = true;
                checkStatus(taskStatus);
                loadData(false);
                break;
        }
    }

    private void checkStatus(int type) {
        for (int i = 0; i < statusChecks.length; i++) {
            if (i == type) {
                statusChecks[i].setChecked(true);
            } else {
                statusChecks[i].setChecked(false);
            }
        }
    }
}
