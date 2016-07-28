package com.floyd.onebuy.ui.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.floyd.onebuy.aync.ApiCallback;
import com.floyd.onebuy.biz.manager.CommonwealManager;
import com.floyd.onebuy.biz.vo.AdvVO;
import com.floyd.onebuy.biz.vo.commonweal.CommonwealHomeVO;
import com.floyd.onebuy.biz.vo.commonweal.CommonwealVO;
import com.floyd.onebuy.ui.ImageLoaderFactory;
import com.floyd.onebuy.ui.R;
import com.floyd.onebuy.ui.adapter.BannerImageAdapter;
import com.floyd.onebuy.ui.adapter.CommonwealAdapter;
import com.floyd.onebuy.ui.fragment.BannerFragment;
import com.floyd.onebuy.ui.loading.DataLoadingView;
import com.floyd.onebuy.ui.loading.DefaultDataLoadingView;
import com.floyd.onebuy.ui.pageindicator.CircleLoopPageIndicator;
import com.floyd.onebuy.view.LoopViewPager;
import com.floyd.pullrefresh.widget.PullToRefreshBase;
import com.floyd.pullrefresh.widget.PullToRefreshListView;

import java.util.ArrayList;
import java.util.List;

public class CommonwealActivity extends FragmentActivity implements AbsListView.OnScrollListener, View.OnClickListener {

    public static final int CHANGE_BANNER_HANDLER_MSG_WHAT = 51;
    private static int PAGE_SIZE = 10;

    private PullToRefreshListView mPullToRefreshListView;
    private ListView mListView;
    private CommonwealAdapter mAdapter;

    private DataLoadingView dataLoadingView;
    private View mViewPagerContainer;//整个广告
    private LoopViewPager mHeaderViewPager;//广告
    private CircleLoopPageIndicator mHeaderViewIndicator;//广告条索引
    private List<AdvVO> mTopBannerList;//最上部分左右循环广告条

    private BannerImageAdapter mBannerImageAdapter;
    private boolean needClear;
    private int pageNo;

    private boolean isShowBanner;

    private TextView commonwealFeeView;

    private Handler mChangeViewPagerHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case CHANGE_BANNER_HANDLER_MSG_WHAT:
                    if (mTopBannerList != null) {
                        if (mTopBannerList != null && mTopBannerList.size() > 0 && mHeaderViewPager != null) {
                            int totalcount = mTopBannerList.size();//autoChangeViewPager.getChildCount();
                            int currentItem = mHeaderViewPager.getCurrentItem();
                            int toItem = currentItem + 1 == totalcount ? 0 : currentItem + 1;
                            mHeaderViewPager.setCurrentItem(toItem, true);
                            //每3秒钟发送一个message，用于切换viewPager中的图片
                            this.sendEmptyMessageDelayed(CHANGE_BANNER_HANDLER_MSG_WHAT, 3000);
                        }
                    }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commonweal);
        ImageLoader mImageLoader = ImageLoaderFactory.createImageLoader();
        mTopBannerList = new ArrayList<AdvVO>();
        pageNo = 1;

        findViewById(R.id.title_back).setOnClickListener(this);
        TextView titleNameView = (TextView) findViewById(R.id.title_name);
        titleNameView.setText("公益");
        titleNameView.setVisibility(View.VISIBLE);

        dataLoadingView = new DefaultDataLoadingView();
        dataLoadingView.initView(findViewById(R.id.act_lsloading), this);
        mPullToRefreshListView = (PullToRefreshListView) findViewById(R.id.common_list);
        mPullToRefreshListView.setMode(PullToRefreshBase.Mode.BOTH);
        mPullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2() {
            @Override
            public void onPullDownToRefresh() {
                needClear = true;
                pageNo = 1;
                loadData(false);
                mPullToRefreshListView.onRefreshComplete(false, true);
            }

            @Override
            public void onPullUpToRefresh() {
                needClear = false;
                pageNo++;
                loadPageData();
                mPullToRefreshListView.onRefreshComplete(false, true);
            }
        });
        mListView = mPullToRefreshListView.getRefreshableView();
        initHeader();
        mAdapter = new CommonwealAdapter(this, new ArrayList<CommonwealVO>(), mImageLoader);
        mListView.setAdapter(mAdapter);
        loadData(true);
    }

    private void initHeader() {
        View header = View.inflate(this, R.layout.commonweal_head, null);
        mViewPagerContainer = header.findViewById(R.id.pager_layout);
        mHeaderViewPager = (LoopViewPager) header.findViewById(R.id.loopViewPager);
        mHeaderViewIndicator = (CircleLoopPageIndicator) header.findViewById(R.id.indicator);
        commonwealFeeView = (TextView)header.findViewById(R.id.commonweal_fee_textview);
        mListView.addHeaderView(header);

        mBannerImageAdapter = new BannerImageAdapter(this.getSupportFragmentManager(), null, null, BannerFragment.SCALE_CENTER_CROP);
        mHeaderViewPager.setAdapter(mBannerImageAdapter);
        mHeaderViewPager.setOnPageChangeListener(new LoopViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int index) {
                mHeaderViewIndicator.setIndex(index);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });

        mHeaderViewPager.setDispatchTouchEventListener(new LoopViewPager.DispatchTouchEventListener() {
            @Override
            public void dispatchTouchEvent(MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    stopBannerAutoLoop();
                } else if (event.getAction() == MotionEvent.ACTION_CANCEL || event.getAction() == MotionEvent.ACTION_OUTSIDE
                        || event.getAction() == MotionEvent.ACTION_UP) {
                    startBannerAutoLoop();
                }
            }
        });
        mViewPagerContainer.setVisibility(View.GONE);
    }

    private void loadData(final boolean isFirst) {
        if (isFirst) {
            dataLoadingView.startLoading();
        }
        CommonwealManager.fetchCommonwealHomeData(10).startUI(new ApiCallback<CommonwealHomeVO>() {
            @Override
            public void onError(int code, String errorInfo) {
                if (isFirst) {
                    dataLoadingView.loadFail();
                }
            }

            @Override
            public void onSuccess(CommonwealHomeVO commonwealHomeVO) {
                if (isFirst) {
                    dataLoadingView.loadSuccess();
                }

                mTopBannerList.clear();

                List<AdvVO> advVOs = commonwealHomeVO.Advertis;
                mTopBannerList.addAll(advVOs);
                mBannerImageAdapter.addItems(mTopBannerList);
                if (mTopBannerList != null && mTopBannerList.size() > 0) {
                    mViewPagerContainer.setVisibility(View.VISIBLE);
                    isShowBanner = true;
                    int count = mBannerImageAdapter.getCount();
                    mHeaderViewIndicator.setTotal(count);
                    mHeaderViewIndicator.setIndex(0);
                    mHeaderViewPager.setAdapter(mBannerImageAdapter);
                    mBannerImageAdapter.notifyDataSetChanged();
                    if (mTopBannerList.size() == 1) {
                        mHeaderViewIndicator.setVisibility(View.GONE);
                    } else {
                        mHeaderViewIndicator.setVisibility(View.VISIBLE);
                        stopBannerAutoLoop();
                        startBannerAutoLoop();
                    }
                } else {
                    isShowBanner = false;
                    mViewPagerContainer.setVisibility(View.GONE);
                }

                commonwealFeeView.setText(commonwealHomeVO.TotalMoney + "");

                List<CommonwealVO> commonwealVO = commonwealHomeVO.FoundationList;
                mAdapter.addAll(commonwealVO, needClear);
            }

            @Override
            public void onProgress(int progress) {

            }
        });
    }

    private void loadPageData() {
        CommonwealManager.fetchCommonwealList(pageNo, PAGE_SIZE).startUI(new ApiCallback<List<CommonwealVO>>() {
            @Override
            public void onError(int code, String errorInfo) {
                Toast.makeText(CommonwealActivity.this, errorInfo, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(List<CommonwealVO> commonwealVOs) {
                mAdapter.addAll(commonwealVOs, needClear);
            }

            @Override
            public void onProgress(int progress) {

            }
        });
    }


    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (isShowBanner && firstVisibleItem >= mListView.getHeaderViewsCount()) {
            stopBannerAutoLoop();
        } else {
            startBannerAutoLoop();
        }
    }


    public void stopBannerAutoLoop() {
        if (mChangeViewPagerHandler != null) {
            mChangeViewPagerHandler.removeCallbacksAndMessages(null);
        }
    }

    public void startBannerAutoLoop() {
        if (mChangeViewPagerHandler != null && !mChangeViewPagerHandler.hasMessages(CHANGE_BANNER_HANDLER_MSG_WHAT)) {
            mChangeViewPagerHandler.sendEmptyMessageDelayed(CHANGE_BANNER_HANDLER_MSG_WHAT, 5000);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_back:
                this.finish();
                break;
            case R.id.act_lsloading:
                loadData(true);
                break;
        }

    }
}
