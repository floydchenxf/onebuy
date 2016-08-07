package com.floyd.onebuy.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.TextView;

import com.floyd.onebuy.aync.ApiCallback;
import com.floyd.onebuy.biz.constants.APIConstants;
import com.floyd.onebuy.biz.manager.LoginManager;
import com.floyd.onebuy.biz.manager.ProductManager;
import com.floyd.onebuy.biz.vo.model.WinningInfo;
import com.floyd.onebuy.event.TabSwitchEvent;
import com.floyd.onebuy.ui.R;
import com.floyd.onebuy.ui.adapter.JoinedNumAdapter;
import com.floyd.onebuy.ui.adapter.PayResultAdapter;
import com.floyd.onebuy.ui.loading.DataLoadingView;
import com.floyd.onebuy.ui.loading.DefaultDataLoadingView;
import com.floyd.onebuy.view.LeftDownPopupWindow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.greenrobot.event.EventBus;

public class PayResultActivity extends Activity implements View.OnClickListener {

    private DataLoadingView dataLoadingView;
    private PayResultAdapter payResultAdapter;
    private ListView mListView;
    private String orderNo;
    private CheckedTextView viewBuyRecordView;
    private CheckedTextView gotoBuyView;
    private LeftDownPopupWindow joinedPopupWindow;

    private TextView popProductCodeView;
    private TextView popProductTitleView;
    private TextView popJoinedCountView;
    private ListView popJoinNumListView;
    private JoinedNumAdapter joinedNumAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_result);
        findViewById(R.id.title_back).setOnClickListener(this);
        TextView titleName = (TextView) findViewById(R.id.title_name);
        titleName.setText("付款成功");
        titleName.setVisibility(View.VISIBLE);
        orderNo = getIntent().getStringExtra(APIConstants.PAY_ORDER_NO);
        dataLoadingView = new DefaultDataLoadingView();
        dataLoadingView.initView(findViewById(R.id.act_lsloading), this);
        mListView = (ListView) findViewById(R.id.buy_list);
        payResultAdapter = new PayResultAdapter(this, new ArrayList<WinningInfo>());
        payResultAdapter.setMoreClickListener(new PayResultAdapter.MoreClickListener() {
            @Override
            public void onClick(WinningInfo winningInfo) {
                popProductCodeView.setText("第" + winningInfo.code + "期");
                popProductTitleView.setText(winningInfo.getTitle());
                popJoinedCountView.setText(winningInfo.joinedCount + "人次");
                List<String> list = winningInfo.myPrizeCodes;
                joinedNumAdapter.addAll(list, true);
                joinedPopupWindow.showPopUpWindow();
            }
        });
        mListView.setAdapter(payResultAdapter);
        viewBuyRecordView = (CheckedTextView) findViewById(R.id.view_buy_view);
        gotoBuyView = (CheckedTextView) findViewById(R.id.goto_buy_view);
        joinedPopupWindow = new LeftDownPopupWindow(this);
        joinedPopupWindow.initView(R.layout.pop_join_num, new LeftDownPopupWindow.ViewInit() {
            @Override
            public void initView(View v) {
                popProductCodeView = (TextView) v.findViewById(R.id.pop_product_code_view);
                popProductTitleView = (TextView) v.findViewById(R.id.pop_product_title_view);
                popJoinedCountView = (TextView) v.findViewById(R.id.pop_joined_count_view);
                popJoinNumListView = (ListView) v.findViewById(R.id.pop_joined_num_listview);
                joinedNumAdapter = new JoinedNumAdapter(PayResultActivity.this, new ArrayList<String>());
                popJoinNumListView.setAdapter(joinedNumAdapter);
            }
        });
        viewBuyRecordView.setOnClickListener(this);
        gotoBuyView.setOnClickListener(this);
        loadData();
    }

    private void loadData() {
        dataLoadingView.startLoading();
        Long userId = LoginManager.getLoginInfo(this).ID;
        ProductManager.getWinningInfosByOrderNo(userId, orderNo).startUI(new ApiCallback<List<WinningInfo>>() {
            @Override
            public void onError(int code, String errorInfo) {
                dataLoadingView.loadFail();
            }

            @Override
            public void onSuccess(List<WinningInfo> winningInfos) {
                dataLoadingView.loadSuccess();
                payResultAdapter.addAll(winningInfos, true);
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
            case R.id.goto_buy_view:
                this.finish();
                EventBus.getDefault().post(new TabSwitchEvent(R.id.tab_index_page, new HashMap<String, Object>()));
                break;
            case R.id.view_buy_view:
                Intent winnerRecordIntent = new Intent(this, WinningRecordActivity.class);
                startActivity(winnerRecordIntent);
                break;
        }
    }


}