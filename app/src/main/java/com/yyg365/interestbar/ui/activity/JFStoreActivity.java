package com.yyg365.interestbar.ui.activity;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.yyg365.interestbar.aync.ApiCallback;
import com.yyg365.interestbar.biz.manager.JiFengManager;
import com.yyg365.interestbar.biz.vo.json.JFGoodsVO;
import com.yyg365.interestbar.ui.ImageLoaderFactory;
import com.yyg365.interestbar.ui.R;
import com.yyg365.interestbar.ui.adapter.JFStoreAdapter;
import com.yyg365.interestbar.ui.loading.DataLoadingView;
import com.yyg365.interestbar.ui.loading.DefaultDataLoadingView;
import com.yyg365.interestbar.view.BasePopupWindow;
import com.yyg365.interestbar.view.RightTopPopupWindow;
import com.yyg365.pullrefresh.widget.PullToRefreshBase;
import com.yyg365.pullrefresh.widget.PullToRefreshListView;

import java.util.ArrayList;
import java.util.List;

public class JFStoreActivity extends Activity implements View.OnClickListener {

    private static final int PAGE_SIZE = 10;
    public static final String SEARCH_KEY = "SEARCH_KEY";
    private DataLoadingView dataLoadingView;
    private PullToRefreshListView mPullToRefreshListView;
    private ListView mListView;
    private JFStoreAdapter mAdapter;

    private ImageLoader mImageLoader;
    private int sort; //排序类型 0 最新  1 最热  2 积分增  3积分减
    private int proType; //商品类型 1表示实体  2表示虚拟 0 or -1 全部
    private int pageNo;
    private String jfdown;
    private String jfup;
    private String key;
    private boolean isFrist;
    private boolean needClear;

    private int priceStatus; // 0:未选中 1:up 2:down

    private CheckedTextView lastestView;
    private CheckedTextView hottestView;
    private CheckedTextView priceView;
    private CheckedTextView filterView;
    private ImageView priceStatusView;
    private CheckedTextView[] checkedTextViews;
    private RightTopPopupWindow rightTopPopupWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jfstore);

        key = getIntent().getStringExtra(SEARCH_KEY);
        if (key == null) {
            key = "";
        }
        mImageLoader = ImageLoaderFactory.createImageLoader();
        rightTopPopupWindow = new RightTopPopupWindow(this);
        rightTopPopupWindow.initView(R.layout.popup_filter_content, new BasePopupWindow.ViewInit() {
            @Override
            public void initView(View v) {

            }
        });
        initData();
        findViewById(R.id.title_back).setOnClickListener(this);
        TextView titleNameView = (TextView) findViewById(R.id.title_name);
        titleNameView.setText("积分商城");
        titleNameView.setVisibility(View.VISIBLE);

        findViewById(R.id.right).setOnClickListener(this);

        dataLoadingView = new DefaultDataLoadingView();
        dataLoadingView.initView(findViewById(R.id.act_lsloading), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initData();
                loadData();
            }
        });

        mPullToRefreshListView = (PullToRefreshListView) findViewById(R.id.common_list);
        mPullToRefreshListView.setMode(PullToRefreshBase.Mode.PULL_UP_TO_REFRESH);
        mPullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2() {
            @Override
            public void onPullDownToRefresh() {

            }

            @Override
            public void onPullUpToRefresh() {
                pageNo++;
                isFrist = false;
                needClear = false;
                loadData();
                mPullToRefreshListView.onRefreshComplete(false, true);
            }
        });

        mListView = mPullToRefreshListView.getRefreshableView();

        View headerView = View.inflate(this, R.layout.jfstore_head, null);
        initHeader(headerView);

        mListView.addHeaderView(headerView);
        mAdapter = new JFStoreAdapter(this, new ArrayList<JFGoodsVO>(), mImageLoader);
        mListView.setAdapter(mAdapter);
        loadData();
    }

    private void initHeader(View headerView) {
        hottestView = (CheckedTextView) headerView.findViewById(R.id.hottest_view);
        lastestView = (CheckedTextView) headerView.findViewById(R.id.lastest_view);
        priceView = (CheckedTextView) headerView.findViewById(R.id.price_view);
        filterView = (CheckedTextView) headerView.findViewById(R.id.filter_view);
        priceStatusView = (ImageView)headerView.findViewById(R.id.price_status_view);
        lastestView.setChecked(true);

        checkedTextViews = new CheckedTextView[] {lastestView, hottestView, priceView, priceView};
        lastestView.setOnClickListener(this);
        hottestView.setOnClickListener(this);
        priceView.setOnClickListener(this);
        filterView.setOnClickListener(this);
    }

    private void initData() {
        this.sort = 0;
        this.proType = 0;
        this.pageNo = 1;
        this.jfdown = "";
        this.jfup = "";
        this.isFrist = true;
        this.needClear = true;
        this.priceStatus = 0;
    }

    private void loadData() {
        if (isFrist) {
            dataLoadingView.startLoading();
        }
        JiFengManager.fetchJFGoodList(null, proType, jfdown, jfup, sort, key, pageNo, PAGE_SIZE).startUI(new ApiCallback<List<JFGoodsVO>>() {
            @Override
            public void onError(int code, String errorInfo) {
                if (isFrist) {
                    dataLoadingView.loadFail();
                }
            }

            @Override
            public void onSuccess(List<JFGoodsVO> jfGoodsVOs) {
                if (isFrist) {
                    dataLoadingView.loadSuccess();
                }
                isFrist = false;
                mAdapter.addAll(jfGoodsVOs, needClear);
            }

            @Override
            public void onProgress(int progress) {

            }
        });
    }

    private void checkSortType(int type) {
        for (int i = 0; i < checkedTextViews.length; i++) {
            CheckedTextView checkedTextView = checkedTextViews[i];
            if (i == type) {
                checkedTextView.setChecked(true);
            } else {
                checkedTextView.setChecked(false);
            }

            if (type == 2) {
                priceStatus = 1;
                priceStatusView.setImageResource(R.drawable.price_high);
                checkedTextViews[2].setChecked(true);
            } else if (type == 3) {
                priceStatus = 2;
                priceStatusView.setImageResource(R.drawable.price_low);
                checkedTextViews[2].setChecked(true);
            } else {
                priceStatus = 0;
                priceStatusView.setImageResource(R.drawable.price_normal);
            }
        }
        this.sort = type;
        this.pageNo = 1;
        this.needClear = true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_back:
                this.finish();
                break;
            case R.id.right:
                //TODO 跳转到搜索
                break;
            case R.id.lastest_view:
                checkSortType(0);
                loadData();
                break;
            case R.id.hottest_view:
                checkSortType(1);
                loadData();
                break;
            case R.id.price_view:
                if (priceStatus == 0  || priceStatus == 2) {
                    checkSortType(2);
                } else if (priceStatus == 1) {
                    checkSortType(3);
                }
                loadData();
                break;
            case R.id.filter_view:
                rightTopPopupWindow.showPopUpWindow();
                break;
        }

    }
}
