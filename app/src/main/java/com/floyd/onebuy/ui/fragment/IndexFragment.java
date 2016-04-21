package com.floyd.onebuy.ui.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.CheckedTextView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.android.volley.toolbox.ImageLoader;
import com.floyd.onebuy.ui.R;
import com.floyd.onebuy.aync.ApiCallback;
import com.floyd.onebuy.biz.manager.IndexManager;
import com.floyd.onebuy.biz.vo.AdvVO;
import com.floyd.onebuy.biz.vo.IndexVO;
import com.floyd.onebuy.biz.vo.mote.MoteInfoVO;
import com.floyd.onebuy.biz.vo.product.WinningInfo;
import com.floyd.onebuy.ui.ImageLoaderFactory;
import com.floyd.onebuy.ui.adapter.BannerImageAdapter;
import com.floyd.onebuy.ui.adapter.IndexProductAdapter;
import com.floyd.onebuy.ui.loading.DataLoadingView;
import com.floyd.onebuy.ui.loading.DefaultDataLoadingView;
import com.floyd.onebuy.ui.pageindicator.CircleLoopPageIndicator;
import com.floyd.onebuy.utils.CommonUtil;
import com.floyd.onebuy.view.LoopViewPager;
import com.floyd.pullrefresh.widget.PullToRefreshBase;
import com.floyd.pullrefresh.widget.PullToRefreshListView;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link IndexFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class IndexFragment extends BackHandledFragment implements AbsListView.OnScrollListener, View.OnClickListener {

    private static final String TAG = "IndexFragment";
    public static final int MIN_JULI = 600;
    public static final int MIN_VELOCITY_X = 130;

    public static final int CHANGE_BANNER_HANDLER_MSG_WHAT = 51;

    private PullToRefreshListView mPullToRefreshListView;
    private View mTitleLayout;
    private ListView mListView;

    private View mHeaderView;
    private View mViewPagerContainer;//整个广告
    private LoopViewPager mHeaderViewPager;//广告
    private CircleLoopPageIndicator mHeaderViewIndicator;//广告条索引
    private LinearLayout mNavigationContainer;//导航栏
    private List<AdvVO> mTopBannerList;//最上部分左右循环广告条

    private BannerImageAdapter mBannerImageAdapter;

    private boolean isScrollToUp = false; //ListView滚动的方向

    private boolean isShowBanner;

    private DataLoadingView dataLoadingView;

//    private IndexMoteAdapter indexMoteAdapter;
    private IndexProductAdapter indexProductAdapter;

//    private Dialog loadDialog;

    private int moteType = 1;
    private int pageNo = 1;
    private int PAGE_SIZE = 18;

    private boolean needClear;

    private CheckedTextView  femaleview2;
    private CheckedTextView  maleView2;
    private CheckedTextView  babyView2;

    private GestureDetector listViewGestureDetector;

    private View shuaixuan;//筛选模特
    private LinearLayout guide;//操作指引

    private ImageLoader mImageLoader;

    private ViewFlipper mFlipper;

    private ArrayList<String> mDataList = new ArrayList<String>();

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


    public static IndexFragment newInstance(String param1, String param2) {
        IndexFragment fragment = new IndexFragment();
        return fragment;
    }

    public IndexFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mImageLoader = ImageLoaderFactory.createImageLoader();
//        EventBus.getDefault().register(this);
        mTopBannerList = new ArrayList<AdvVO>();
//        loadDialog = DialogCreator.createDataLoadingDialog(this.getActivity());
    }

    private void checkMoteType(int moteType) {
        if (moteType == 1) {
            femaleview2.setChecked(true);
            maleView2.setChecked(false);
            babyView2.setChecked(false);
            this.moteType = moteType;
            this.pageNo = 1;
        } else if (moteType == 2) {
            femaleview2.setChecked(false);
            maleView2.setChecked(true);
            babyView2.setChecked(false);
            this.moteType = moteType;
            this.pageNo = 1;
        } else if (moteType == 3) {
            femaleview2.setChecked(false);
            maleView2.setChecked(false);
            babyView2.setChecked(true);
            this.moteType = moteType;
            this.pageNo = 1;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_index, container, false);

        dataLoadingView = new DefaultDataLoadingView();
        dataLoadingView.initView(view, this);

        mTitleLayout = view.findViewById(R.id.title);
        mPullToRefreshListView = (PullToRefreshListView) view.findViewById(R.id.pic_list);
        mListView = mPullToRefreshListView.getRefreshableView();
        mListView.setOnScrollListener(this);

        initListViewHeader();

        initButton();

        loadData(true);

        init(view);

        mListView.addHeaderView(mHeaderView);

        indexProductAdapter = new IndexProductAdapter(this.getActivity(), new ArrayList<WinningInfo>());

//        indexMoteAdapter = new IndexMoteAdapter(this.getActivity(), new ArrayList<MoteInfoVO>());
        mListView.setAdapter(indexProductAdapter);

        mPullToRefreshListView.setMode(PullToRefreshBase.Mode.PULL_UP_TO_REFRESH);
        mPullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2() {
            @Override
            public void onPullDownToRefresh() {
            }

            @Override
            public void onPullUpToRefresh() {
                needClear = false;
                loadMoteInfo(false);
                mPullToRefreshListView.onRefreshComplete(false, true);
            }
        });

        return view;
    }

    public void init(View view) {
        guide = ((LinearLayout) view.findViewById(R.id.guide));
        shuaixuan =  view.findViewById(R.id.right_layout);
        //跳转到操作指引界面
//        guide.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(getActivity(), GuideActivity.class));
//            }
//        });

        //跳转到筛选模特界面
//        shuaixuan.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                startActivity(new Intent(getActivity(), HomeChooseActivity1.class));
//            }
//        });
    }

    private void initButton() {

//        femaleView1 = (CheckedTextView) mFakeNavigationContainer.findViewById(R.id.female_colther_new);
        femaleview2 = (CheckedTextView) mNavigationContainer.findViewById(R.id.female_colther);
//        maleView1 = (CheckedTextView) mFakeNavigationContainer.findViewById(R.id.male_colther_new);
        maleView2 = (CheckedTextView) mNavigationContainer.findViewById(R.id.male_colther);
//        babyView1 = (CheckedTextView) mFakeNavigationContainer.findViewById(R.id.baby_colther_new);
        babyView2 = (CheckedTextView) mNavigationContainer.findViewById(R.id.baby_colther);

        femaleview2.setOnClickListener(this);
        maleView2.setOnClickListener(this);
        babyView2.setOnClickListener(this);
        femaleview2.setChecked(true);
    }

    public void loadMoteInfo(final boolean needDialog) {
        if (needDialog) {
//            loadDialog.show();
        }
        IndexManager.fetchMoteList(moteType, pageNo, PAGE_SIZE).startUI(new ApiCallback<List<MoteInfoVO>>() {
            @Override
            public void onError(int code, String errorInfo) {
                if (needDialog) {
//                    loadDialog.dismiss();
                }
                Toast.makeText(IndexFragment.this.getActivity(), errorInfo, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(List<MoteInfoVO> moteInfoVOs) {
                if (needDialog) {
//                    loadDialog.dismiss();
                }
                ++pageNo;
                List<WinningInfo> winningRecordVOs = new ArrayList<WinningInfo>();
                for(int i = 0; i < 20; i++) {
                    WinningInfo vo  = new WinningInfo();
                    vo.productUrl = "http://qmmt2015.b0.upaiyun.com/2016/4/12/70242b33-34df-4db5-a334-46000335e8f4.png";
                    vo.left=i+ 1;
                    vo.id = i;
                    vo.processPrecent=50+i;
                    vo.title = "小米手机５｜｜精彩开奖就送苹果";
                    winningRecordVOs.add(vo);
                }
                indexProductAdapter.addAll(winningRecordVOs, needClear);
                dataLoadingView.loadSuccess();
            }

            @Override
            public void onProgress(int progress) {

            }
        });

    }

    private void loadData(final boolean isFirst) {
        if (isFirst) {
            dataLoadingView.startLoading();
        } else {
//            loadDialog.show();
        }
        IndexManager.getIndexInfoJob().startUI(new ApiCallback<IndexVO>() {
            @Override
            public void onError(int code, String errorInfo) {
                if (isFirst) {
                    dataLoadingView.loadFail();
                } else {
//                    loadDialog.dismiss();
                }
            }

            @Override
            public void onSuccess(IndexVO indexVO) {
                if (isFirst) {
                    dataLoadingView.loadSuccess();
                } else {
//                    loadDialog.dismiss();
                }

                mDataList.add("test1");
                mDataList.add("test2");
                mDataList.add("test3");
                mDataList.add("test4");
                mDataList.add("test5");
                mDataList.add("test6");
                mDataList.add("test7");
                mDataList.add("test8");
                for (String s:mDataList) {
                    TextView tv = new TextView(IndexFragment.this.getActivity());
                    tv.setTextSize(16);
                    tv.setPadding(10, 10, 10, 10);
                    tv.setTextColor(Color.RED);
                    tv.setText(s);
                    mFlipper.addView(tv);
                }
                mFlipper.setAutoStart(true);
                mFlipper.startFlipping();
                mViewPagerContainer.setVisibility(View.VISIBLE);
                mTopBannerList.clear();
                List<AdvVO> advVOs = indexVO.advertList;
                mTopBannerList.addAll(advVOs);
                mBannerImageAdapter.addItems(mTopBannerList);

                if (mTopBannerList != null && mTopBannerList.size() > 0) {
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
//                    if (needClear) {
//                        gotoTop();
//                    }
                } else {
                    isShowBanner = false;
                    mHeaderView.findViewById(R.id.pager_layout).setVisibility(View.GONE);
                }

                mNavigationContainer.setVisibility(View.VISIBLE);

//                List<MoteInfoVO> moteInfoVOs = indexVO.moteVOs;
                List<WinningInfo> winningRecordVOs = new ArrayList<WinningInfo>();
                for(int i = 0; i < 20; i++) {
                    WinningInfo vo  = new WinningInfo();
                    vo.productUrl = "http://qmmt2015.b0.upaiyun.com/2016/4/12/70242b33-34df-4db5-a334-46000335e8f4.png";
                    vo.left=i+ 1;
                    vo.id = i;
                    vo.processPrecent=50+i;
                    vo.title = "小米手机５｜｜精彩开奖就送苹果";
                    winningRecordVOs.add(vo);
                }

                ++pageNo;

                indexProductAdapter.addAll(winningRecordVOs, needClear);
            }

            @Override
            public void onProgress(int progress) {

            }
        });
    }

    private void initListViewHeader() {
        mHeaderView = LayoutInflater.from(this.getActivity()).inflate(R.layout.new_head, mListView, false);
        mViewPagerContainer = mHeaderView.findViewById(R.id.pager_layout);
        mViewPagerContainer.setVisibility(View.GONE);
        mFlipper = (ViewFlipper) mHeaderView.findViewById(R.id.viewflipper);
        mFlipper.setFlipInterval(1500);
        mFlipper.setInAnimation(this.getActivity(), R.anim.public_slide_up);
        mFlipper.setOutAnimation(this.getActivity(), R.anim.public_slide_up2);
        ViewGroup.LayoutParams mViewPagerContainerLayoutParams = mViewPagerContainer.getLayoutParams();
        mViewPagerContainerLayoutParams.height = CommonUtil.dip2px(this.getActivity(), BannerImageAdapter.BANNER_HEIGHT_IN_DP) - 200;
        mHeaderViewPager = (LoopViewPager) mHeaderView.findViewById(R.id.loopViewPager);
        mHeaderViewIndicator = (CircleLoopPageIndicator) mHeaderView.findViewById(R.id.indicator);

        mNavigationContainer = (LinearLayout) mHeaderView.findViewById(R.id.navigation_container);

        mBannerImageAdapter = new BannerImageAdapter(this.getActivity().getSupportFragmentManager(), null, null);
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
    public boolean onBackPressed() {
        return false;
    }

//    @Subscribe
//    public void onEventMainThread(LoginEvent event) {
//        if (!IndexFragment.this.getActivity().isFinishing()) {
//            Log.i(TAG, "unReadMsg when login");
//            loadUnReadMsgs(true);
//        }
//    }

    public void onDestroy() {
        super.onDestroy();
//        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (isShowBanner && firstVisibleItem >= mListView.getHeaderViewsCount()) {
            stopBannerAutoLoop();
            if (mFlipper !=null) {
                mFlipper.stopFlipping();
            }
        } else {
            startBannerAutoLoop();
            if (mFlipper != null) {
                mFlipper.startFlipping();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.female_colther:
                femaleview2.setChecked(true);
                maleView2.setChecked(false);
                babyView2.setChecked(false);
                moteType = 1;
                pageNo = 1;
                needClear = true;
                loadMoteInfo(true);
                break;

            case R.id.male_colther:
                femaleview2.setChecked(false);
                maleView2.setChecked(true);
                babyView2.setChecked(false);
                moteType = 2;
                pageNo = 1;
                needClear = true;
                loadMoteInfo(true);
                break;

            case R.id.baby_colther:
                femaleview2.setChecked(false);
                maleView2.setChecked(false);
                babyView2.setChecked(true);
                moteType = 3;
                pageNo = 1;
                needClear = true;
                loadMoteInfo(true);
                break;
            case R.id.act_ls_fail_layout:
                moteType = 1;
                pageNo = 1;
                needClear = true;
                loadData(true);
                break;
        }

    }

    /**
     * 跳转到ListView的最上方
     */
    public void gotoTop() {
        if (mListView != null) {
            mListView.setSelection(0);
        }
    }

}
