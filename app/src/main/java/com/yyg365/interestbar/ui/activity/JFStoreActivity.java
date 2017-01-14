package com.yyg365.interestbar.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.yyg365.interestbar.aync.ApiCallback;
import com.yyg365.interestbar.biz.manager.JiFenManager;
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
    private int proType; //商品类型 1表示实体  2表示虚拟 0: 全部
    private int pageNo;

    private int jfScore; //选中的积分范围。默认 -1,表示未选中

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

    private CheckedTextView proTypeView1, proTypeView2;
    private CheckedTextView jfView1, jfView2, jfView3, jfView4, jfView5;
    private TextView cancelButton, submitButton;

    private CheckedTextView[] proTypeArray = new CheckedTextView[2];
    private CheckedTextView[] jfViewArray = new CheckedTextView[5];

    private ScoreRange r1 = new ScoreRange(-1, -1);
    private ScoreRange r2 = new ScoreRange(0, 10000);
    private ScoreRange r3 = new ScoreRange(10001, 1000000);
    private ScoreRange r4 = new ScoreRange(1000001, 10000000);
    private ScoreRange r5 = new ScoreRange(10000001, 1000000000);
    private ScoreRange[] rangeArray = new ScoreRange[]{r1, r2, r3, r4, r5};

    private int tmpProType, tmpJfScore;


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
                proTypeView1 = (CheckedTextView) v.findViewById(R.id.protype_view1);
                proTypeView2 = (CheckedTextView) v.findViewById(R.id.protype_view2);
                proTypeArray[0] = proTypeView1;
                proTypeArray[1] = proTypeView2;
                jfView1 = (CheckedTextView) v.findViewById(R.id.score_view1);
                jfView2 = (CheckedTextView) v.findViewById(R.id.score_view2);
                jfView3 = (CheckedTextView) v.findViewById(R.id.score_view3);
                jfView4 = (CheckedTextView) v.findViewById(R.id.score_view4);
                jfView5 = (CheckedTextView) v.findViewById(R.id.score_view5);
                jfViewArray[0] = jfView1;
                jfViewArray[1] = jfView2;
                jfViewArray[2] = jfView3;
                jfViewArray[3] = jfView4;
                jfViewArray[4] = jfView5;

                cancelButton = (TextView) v.findViewById(R.id.cancel_button);
                submitButton = (TextView) v.findViewById(R.id.submit_button);

                proTypeView1.setOnClickListener(JFStoreActivity.this);
                proTypeView2.setOnClickListener(JFStoreActivity.this);

                jfView1.setOnClickListener(JFStoreActivity.this);
                jfView2.setOnClickListener(JFStoreActivity.this);
                jfView3.setOnClickListener(JFStoreActivity.this);
                jfView4.setOnClickListener(JFStoreActivity.this);
                jfView5.setOnClickListener(JFStoreActivity.this);

                cancelButton.setOnClickListener(JFStoreActivity.this);
                submitButton.setOnClickListener(JFStoreActivity.this);

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
        View emptyView  = View.inflate(this, R.layout.empty_item, null);
        mPullToRefreshListView.setEmptyView(emptyView);
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

        initHeader();

        mAdapter = new JFStoreAdapter(this, new ArrayList<JFGoodsVO>(), mImageLoader);
        mListView.setAdapter(mAdapter);
        loadData();
    }

    private void initHeader() {
        hottestView = (CheckedTextView) findViewById(R.id.hottest_view);
        lastestView = (CheckedTextView) findViewById(R.id.lastest_view);
        priceView = (CheckedTextView) findViewById(R.id.price_view);
        filterView = (CheckedTextView) findViewById(R.id.filter_view);
        priceStatusView = (ImageView) findViewById(R.id.price_status_view);
        lastestView.setChecked(true);

        checkedTextViews = new CheckedTextView[]{lastestView, hottestView, priceView, priceView};
        lastestView.setOnClickListener(this);
        hottestView.setOnClickListener(this);
        priceView.setOnClickListener(this);
        filterView.setOnClickListener(this);
    }

    private void initData() {
        this.sort = 0;
        this.pageNo = 1;
        this.isFrist = true;
        this.needClear = true;
        this.priceStatus = 0;
        initFilter();
    }

    private void initFilter() {
        this.proType = 0;
        this.jfScore = -1;
    }

    private void loadData() {
        if (isFrist) {
            dataLoadingView.startLoading();
        }

        String jfdown, jfup;
        if (jfScore == -1) {
            jfdown = "";
            jfup = "";
        } else {
            ScoreRange sr = rangeArray[jfScore];
            jfdown = sr.start + "";
            jfup = sr.end + "";
        }

        JiFenManager.fetchJFGoodList(null, proType, jfdown, jfup, sort, key, pageNo, PAGE_SIZE).startUI(new ApiCallback<List<JFGoodsVO>>() {
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

    private void checkProType(int idx) {
        for (int i = 0; i < proTypeArray.length; i++) {
            if (i == idx) {
                if (idx == tmpProType - 1) {
                    proTypeArray[i].setChecked(false);
                    tmpProType = 0;
                } else {
                    proTypeArray[i].setChecked(true);
                    tmpProType = idx + 1;
                }
            } else {
                proTypeArray[i].setChecked(false);
            }
        }
    }

    private void checkJfScore(int idx) {
        for (int i = 0; i < jfViewArray.length; i++) {
            if (idx == i) {
                if (tmpJfScore == idx) {
                    jfViewArray[i].setChecked(false);
                    tmpJfScore = -1;
                } else {
                    jfViewArray[i].setChecked(true);
                    tmpJfScore = idx;
                }
            } else {
                jfViewArray[i].setChecked(false);
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_back:
                this.finish();
                break;
            case R.id.right:
                Intent it = new Intent(this, JFSearchActivity.class);
                startActivity(it);
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
                if (priceStatus == 0 || priceStatus == 2) {
                    checkSortType(2);
                } else if (priceStatus == 1) {
                    checkSortType(3);
                }
                loadData();
                break;
            case R.id.filter_view:
                rightTopPopupWindow.showPopUpWindow();
                tmpProType = proType;
                tmpJfScore = jfScore;
                for (int i = 0; i < proTypeArray.length; i++) {
                    if (tmpProType - 1 == i) {
                        proTypeArray[i].setChecked(true);
                    } else {
                        proTypeArray[i].setChecked(false);
                    }
                }

                for (int i = 0; i < jfViewArray.length; i++) {
                    if (tmpJfScore == i) {
                        jfViewArray[i].setChecked(true);
                    } else {
                        jfViewArray[i].setChecked(false);
                    }
                }

                break;
            case R.id.cancel_button:
                rightTopPopupWindow.hidePopUpWindow();
                break;
            case R.id.submit_button:
                proType = tmpProType;
                jfScore = tmpJfScore;
                rightTopPopupWindow.hidePopUpWindow();
                needClear = true;
                loadData();
                break;
            case R.id.protype_view1:
                checkProType(0);
                break;
            case R.id.protype_view2:
                checkProType(1);
                break;
            case R.id.score_view1:
                checkJfScore(0);
                break;
            case R.id.score_view2:
                checkJfScore(1);
                break;
            case R.id.score_view3:
                checkJfScore(2);
                break;
            case R.id.score_view4:
                checkJfScore(3);
                break;
            case R.id.score_view5:
                checkJfScore(4);
                break;
        }

    }

    public static class ScoreRange {
        public int start;
        public int end;

        public ScoreRange(int start, int end) {
            this.start = start;
            this.end = end;
        }
    }
}
