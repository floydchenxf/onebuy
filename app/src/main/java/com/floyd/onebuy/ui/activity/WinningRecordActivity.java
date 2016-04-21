package com.floyd.onebuy.ui.activity;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.floyd.onebuy.ui.R;
import com.floyd.onebuy.aync.ApiCallback;
import com.floyd.onebuy.biz.manager.FeeManager;
import com.floyd.onebuy.biz.vo.fee.WinningRecordVO;
import com.floyd.onebuy.ui.DialogCreator;
import com.floyd.onebuy.ui.ImageLoaderFactory;
import com.floyd.onebuy.ui.adapter.WinningRecordAdapter;
import com.floyd.onebuy.ui.loading.DataLoadingView;
import com.floyd.onebuy.ui.loading.DefaultDataLoadingView;
import com.floyd.pullrefresh.widget.PullToRefreshBase;
import com.floyd.pullrefresh.widget.PullToRefreshListView;

import java.util.ArrayList;
import java.util.List;

public class WinningRecordActivity extends Activity implements View.OnClickListener {
    private Dialog loadingDialog;
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

    private CheckedTextView doingStatusView;
    private CheckedTextView confirmStatusView;
    private CheckedTextView doneStatusView;

    private int taskStatus = 1;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_winning_record);

        oneDp = this.getResources().getDimension(R.dimen.one_dp);

        findViewById(R.id.title_back).setOnClickListener(this);
        titleView = (TextView) findViewById(R.id.title_name);
        titleView.setText("夺宝记录");
        titleView.setVisibility(View.VISIBLE);

        loadingDialog = DialogCreator.createDataLoadingDialog(this);
        dataLoadingView = new DefaultDataLoadingView();
        dataLoadingView.initView(findViewById(R.id.act_lsloading), this);
        mImageLoader = ImageLoaderFactory.createImageLoader();

        mPullToRefreshListView = (PullToRefreshListView) findViewById(R.id.winning_record_list);
        mPullToRefreshListView.setMode(PullToRefreshBase.Mode.PULL_UP_TO_REFRESH);
        mPullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2() {
            @Override
            public void onPullDownToRefresh() {

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
        adapter = new WinningRecordAdapter(this, mImageLoader, new ArrayList<WinningRecordVO>());
        mListView.setAdapter(adapter);

        doingStatusView = (CheckedTextView) findViewById(R.id.doing_status);
        confirmStatusView = (CheckedTextView) findViewById(R.id.confirm_status);
        doneStatusView = (CheckedTextView) findViewById(R.id.done_status);

        doingStatusView.setOnClickListener(this);
        confirmStatusView.setOnClickListener(this);
        doneStatusView.setOnClickListener(this);

        loadData(true);

    }

    private void loadData(final boolean isFirst) {
        if (isFirst) {
            dataLoadingView.startLoading();
        } else {
            loadingDialog.show();
        }

        FeeManager.fetchWinningRecords(0, taskStatus, pageNo, PAGE_SIZE).startUI(new ApiCallback<List<WinningRecordVO>>() {
            @Override
            public void onError(int code, String errorInfo) {
                if (isFirst) {
                    dataLoadingView.loadFail();
                } else {
                    loadingDialog.dismiss();
                }
            }

            @Override
            public void onSuccess(List<WinningRecordVO> winningRecordVOs) {
                if (isFirst) {
                    dataLoadingView.loadSuccess();
                } else {
                    loadingDialog.dismiss();
                }

                adapter.addAll(winningRecordVOs, needClear);
                pageNo++;
                if (adapter.getFeeRecords() == null || adapter.getFeeRecords().isEmpty()) {
                    TextView emptyView = new TextView(WinningRecordActivity.this);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) (10 * oneDp));
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
            case R.id.doing_status:
                taskStatus = 1;
                doingStatusView.setChecked(true);
                confirmStatusView.setChecked(false);
                doneStatusView.setChecked(false);
                pageNo = 1;
                needClear = true;
                loadingDialog.show();
                loadData(false);
                break;
            case R.id.confirm_status:
                taskStatus = 2;
                doingStatusView.setChecked(false);
                confirmStatusView.setChecked(true);
                doneStatusView.setChecked(false);
                pageNo = 1;
                needClear = true;
                loadingDialog.show();
                loadData(false);
                break;
            case R.id.done_status:
                taskStatus = 3;
                doingStatusView.setChecked(false);
                confirmStatusView.setChecked(false);
                doneStatusView.setChecked(true);
                pageNo = 1;
                needClear = true;
                loadingDialog.show();
                loadData(false);
                break;
        }
    }
}
