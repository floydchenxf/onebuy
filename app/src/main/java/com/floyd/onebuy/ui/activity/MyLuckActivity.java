package com.floyd.onebuy.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.floyd.onebuy.aync.ApiCallback;
import com.floyd.onebuy.biz.manager.LoginManager;
import com.floyd.onebuy.biz.manager.ProductManager;
import com.floyd.onebuy.biz.vo.json.LuckRecordVO;
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
    public static final String IS_SELF = "is_self";
    public static final String LUCK_TYPE = "LUCK_TYPE";
    private static final String TAG = "MyLuckActivity";

    private static int PAGE_SIZE = 12;
    private PullToRefreshListView mPullToRefreshListView;
    private int pageNo = 1;
    private boolean needClear;
    private boolean isFirst;
    private ListView mListView;
    private DataLoadingView dataLoadingView;
    private LuckRecordAdapter luckRecordAdapter;
    private ImageLoader mImageLoader;
    private View emptyView;
    private TextView gotoIndexView;
    private boolean isSelf;

    private LinearLayout defaultAddressLayout;
    private TextView buyView;
    private TextView nameView;
    private TextView phoneView;
    private TextView addressView;
    private TextView emptyAddressView;
    private int type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_luck);
        isSelf = getIntent().getBooleanExtra(IS_SELF, true);
        findViewById(R.id.title_back).setOnClickListener(this);
        TextView titleNameView = (TextView)findViewById(R.id.title_name);
        titleNameView.setText("中奖记录");
        titleNameView.setVisibility(View.VISIBLE);
        pageNo = 1;
        isFirst = true;
        needClear = true;
        type = getIntent().getIntExtra(LUCK_TYPE, 1);

        mImageLoader = ImageLoaderFactory.createImageLoader();

        dataLoadingView = new DefaultDataLoadingView();
        dataLoadingView.initView(findViewById(R.id.act_lsloading), this);

        mPullToRefreshListView = (PullToRefreshListView) findViewById(R.id.common_list);
        mPullToRefreshListView.setMode(PullToRefreshBase.Mode.PULL_UP_TO_REFRESH);
        mPullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2() {
            @Override
            public void onPullDownToRefresh() {
            }

            @Override
            public void onPullUpToRefresh() {
                pageNo++;
                needClear = false;
                isFirst = false;
                loadData();
                mPullToRefreshListView.onRefreshComplete(false, true);

            }
        });
        mListView = mPullToRefreshListView.getRefreshableView();
        luckRecordAdapter = new LuckRecordAdapter(this, new ArrayList<ProductLssueWithWinnerVO>(), isSelf, mImageLoader);
        View addressHeadview = View.inflate(MyLuckActivity.this, R.layout.luck_address_layout, null);
        addressHeadview.setOnClickListener(this);
        defaultAddressLayout = (LinearLayout) addressHeadview.findViewById(R.id.default_address_layout);
        nameView = (TextView)addressHeadview.findViewById(R.id.name_view);
        phoneView = (TextView) addressHeadview.findViewById(R.id.phone_view);
        addressView = (TextView) addressHeadview.findViewById(R.id.address_view);
        emptyAddressView = (TextView) addressHeadview.findViewById(R.id.empty_address_view);
        mListView.addHeaderView(addressHeadview);
        mListView.setAdapter(luckRecordAdapter);
        emptyView = findViewById(R.id.empty_view);
        gotoIndexView = (TextView) emptyView.findViewById(R.id.goto_index);
        gotoIndexView.setOnClickListener(this);
        buyView = (TextView) findViewById(R.id.buy_view);
        buyView.setOnClickListener(this);
    }

    protected void onResume() {
        super.onResume();
        loadData();
    }

    private void loadData() {
        long uid = LoginManager.getLoginInfo(this).ID;
        if (isFirst) {
            dataLoadingView.startLoading();
        }
        ProductManager.fetchMyLuckRecords(uid, type, pageNo, PAGE_SIZE).startUI(new ApiCallback<LuckRecordVO>() {
            @Override
            public void onError(int code, String errorInfo) {
                if (isFirst) {
                    dataLoadingView.loadFail();
                }
            }

            @Override
            public void onSuccess(LuckRecordVO recordVO) {
                if (isFirst) {
                    dataLoadingView.loadSuccess();
                }

                LuckRecordVO.LuckAddressVO luckAddressVO = recordVO.clientAddr;
                if (luckAddressVO == null || luckAddressVO.ID == 0) {
                    emptyAddressView.setVisibility(View.VISIBLE);
                    defaultAddressLayout.setVisibility(View.GONE);
                    luckRecordAdapter.setHasSetAddress(false);
                } else {
                    emptyAddressView.setVisibility(View.GONE);
                    defaultAddressLayout.setVisibility(View.VISIBLE);
                    nameView.setText(luckAddressVO.Name);
                    addressView.setText(luckAddressVO.Address);
                    phoneView.setText(luckAddressVO.Phone);
                    luckRecordAdapter.setHasSetAddress(true);
                }

                List<ProductLssueWithWinnerVO> productLssueWithWinnerVOs = recordVO.proLssueList;
                luckRecordAdapter.addAll(productLssueWithWinnerVOs, needClear);
                if (luckRecordAdapter.getRecords().isEmpty()) {
                    mListView.setVisibility(View.GONE);
                    emptyView.setVisibility(View.VISIBLE);
                    buyView.setVisibility(View.GONE);
                } else {
                    mListView.setVisibility(View.VISIBLE);
                    emptyView.setVisibility(View.GONE);
                    buyView.setVisibility(View.VISIBLE);
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
                isFirst = true;
                loadData();
                break;
            case R.id.goto_index:
            case R.id.buy_view:
                EventBus.getDefault().post(new TabSwitchEvent(R.id.tab_index_page, new HashMap<String, Object>()));
                this.finish();
                break;
            case R.id.address_layout:
                Intent addressIntent = new Intent(this, AddressManagerActivity.class);
                startActivity(addressIntent);
                break;
        }

    }
}
