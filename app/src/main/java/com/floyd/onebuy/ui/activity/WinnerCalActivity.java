package com.floyd.onebuy.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.floyd.onebuy.aync.ApiCallback;
import com.floyd.onebuy.biz.manager.ProductManager;
import com.floyd.onebuy.biz.vo.json.CalItemVO;
import com.floyd.onebuy.biz.vo.json.CalRecordsVO;
import com.floyd.onebuy.ui.MainActivity;
import com.floyd.onebuy.ui.R;
import com.floyd.onebuy.ui.adapter.CalRecordAdapter;
import com.floyd.onebuy.ui.loading.DataLoadingView;
import com.floyd.onebuy.ui.loading.DefaultDataLoadingView;

import java.util.ArrayList;
import java.util.List;

public class WinnerCalActivity extends Activity {

    public static final String LSSUE_ID = "LSSUE_ID";
    private ListView mListView;
    private LinearLayout descLayout;

    private CalRecordAdapter mAdapter;

    private DataLoadingView dataLoadingView;

    private long lssueId;

    private TextView step1View, step2View, step3View, step4View, resultView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_winner_cal);
        lssueId = getIntent().getLongExtra(LSSUE_ID, 0);
        dataLoadingView = new DefaultDataLoadingView();
        dataLoadingView.initView(findViewById(R.id.act_lsloading), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadData();
            }
        });
        findViewById(R.id.title_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WinnerCalActivity.this.finish();
            }
        });

        findViewById(R.id.index_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gotoIndex = new Intent(WinnerCalActivity.this, MainActivity.class);
                gotoIndex.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                gotoIndex.putExtra(MainActivity.TAB_INDEX, R.id.tab_index_page);
                startActivity(gotoIndex);
            }
        });

        TextView titleNameView = (TextView) findViewById(R.id.title_name);
        titleNameView.setText("计算结果");
        titleNameView.setVisibility(View.VISIBLE);
        mListView = (ListView) findViewById(R.id.common_list);
        descLayout = (LinearLayout) findViewById(R.id.desc_layout);

        step1View = (TextView) findViewById(R.id.step1_view);
        step2View = (TextView) findViewById(R.id.step2_view);
        step3View = (TextView) findViewById(R.id.step3_view);
        step4View = (TextView) findViewById(R.id.step4_view);
        resultView = (TextView) findViewById(R.id.result_view);

        View emptyView = View.inflate(this, R.layout.empty_item, null);
        mListView.setEmptyView(emptyView);
        initHeader(mListView);
        loadData();
    }

    private void loadData() {
        dataLoadingView.startLoading();
        ProductManager.getCalRecords(lssueId).startUI(new ApiCallback<CalRecordsVO>() {
            @Override
            public void onError(int code, String errorInfo) {
                dataLoadingView.loadFail();
            }

            @Override
            public void onSuccess(CalRecordsVO vo) {
                dataLoadingView.loadSuccess();
                List<CalItemVO> items = vo.Records;

                if (items == null || items.isEmpty()) {
                    descLayout.setVisibility(View.GONE);
                } else {
                    descLayout.setVisibility(View.VISIBLE);
                    mAdapter.addAll(items, true);

                    resultView.setText(vo.Result);

                    StringBuilder sb1 = new StringBuilder("1. 求和: ")
                            .append(vo.ResultSum)
                            .append("(上面")
                            .append(vo.ResultCount)
                            .append("条记录时间因子相加之和)");
                    step1View.setText(sb1.toString());

                    StringBuilder sb2 = new StringBuilder("2. 最近下一期(20")
                            .append(vo.Phase)
                            .append("期)\"时时彩\"开奖号码 ")
                            .append(vo.LotteryResult);
                    step2View.setText(sb2.toString());

                    StringBuilder sb3 = new StringBuilder("3. 求余: ")
                            .append(vo.Formula1);
                    step3View.setText(sb3.toString());

                    StringBuilder sb4 = new StringBuilder("4. 结果: ")
                            .append(vo.Formula2);
                    step4View.setText(sb4.toString());
                }
            }

            @Override
            public void onProgress(int progress) {

            }
        });

    }

    private void initHeader(ListView mListView) {
        View header = View.inflate(this, R.layout.cal_record_head, null);
        mListView.addHeaderView(header);
        mAdapter = new CalRecordAdapter(this, new ArrayList<CalItemVO>());
        mListView.setAdapter(mAdapter);
    }

}
