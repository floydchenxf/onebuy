package com.floyd.onebuy.ui.fragment;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;
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
import com.floyd.onebuy.R;
import com.floyd.onebuy.aync.ApiCallback;
import com.floyd.onebuy.biz.manager.IndexManager;
import com.floyd.onebuy.biz.vo.AdvVO;
import com.floyd.onebuy.biz.vo.IndexVO;
import com.floyd.onebuy.biz.vo.mote.MoteInfoVO;
import com.floyd.onebuy.ui.DialogCreator;
import com.floyd.onebuy.ui.ImageLoaderFactory;
import com.floyd.onebuy.ui.adapter.IndexMoteAdapter;
import com.floyd.onebuy.ui.loading.DataLoadingView;
import com.floyd.onebuy.ui.loading.DefaultDataLoadingView;
import com.floyd.onebuy.ui.pageindicator.CircleLoopPageIndicator;
import com.floyd.onebuy.utils.CommonUtil;
import com.floyd.onebuy.view.LoopViewPager;
import com.floyd.pullrefresh.widget.PullToRefreshBase;
import com.floyd.pullrefresh.widget.PullToRefreshListView;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

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

    private static int BANNER_HEIGHT_IN_DP = 300;
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

    private boolean isShowBanner;

    private boolean isScrollToUp = false; //ListView滚动的方向

    private DataLoadingView dataLoadingView;

    private IndexMoteAdapter indexMoteAdapter;

    private Dialog loadDialog;

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

    private float keydownX1;

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
        loadDialog = DialogCreator.createDataLoadingDialog(this.getActivity());
        listViewGestureDetector = new GestureDetector(this.getActivity(), new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                return false;
            }

            @Override
            public void onShowPress(MotionEvent e) {

            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                Log.i(TAG, "onScroll------------vx:" + distanceX + "----vy:" + distanceY);
                return false;
            }

            @Override
            public void onLongPress(MotionEvent e) {

            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                float x1 = keydownX1;
                Log.i(TAG, "-------x1:" + x1 + "-------x2:" + e2.getX() + "---vx:" + velocityX);
                if (x1 - e2.getX() > MIN_JULI && Math.abs(velocityX) > MIN_VELOCITY_X) {
                    pageNo = 1;
                    needClear = true;
                    int k = (++moteType - 1) % 3 + 1;
                    checkMoteType(k);
                    loadMoteInfo(true);
                } else if (e2.getX() - x1 > MIN_JULI && Math.abs(velocityX) > MIN_VELOCITY_X) {

                    int k = 1;
                    if (moteType > 1) {
                        k = moteType - 1;
                    } else {
                        k = 4-moteType;
                    }

                    pageNo = 1;
                    needClear = true;
                    checkMoteType(k);
                    loadMoteInfo(true);
                }
                return false;
            }
        });
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

        indexMoteAdapter = new IndexMoteAdapter(this.getActivity(), new ArrayList<MoteInfoVO>());
        mListView.setAdapter(indexMoteAdapter);

        mListView.setOnTouchListener(new View.OnTouchListener() {

            float y1, y2;
            float x1, x2;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        y1 = event.getRawY();
                        x1 = event.getRawX();
                        Log.i(TAG, "===========x1:" + event.getX());
                        break;
                    case MotionEvent.ACTION_MOVE:
                        y2 = event.getRawY();
                        x2 = event.getRawX();
                        if (y2 - y1 > 30) {
                            isScrollToUp = true;
                        } else if (y2 - y1 < -60) {
                            isScrollToUp = false;
                        }

                        break;
                }

                listViewGestureDetector.onTouchEvent(event);
                return false;
            }
        });
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

        mPullToRefreshListView.setOnTouchListener(new View.OnTouchListener() {

            float y1 = 0, y2 = 0;
            float x1 = 0, x2 = 0;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        break;
                    case MotionEvent.ACTION_MOVE:
                        break;
                    case MotionEvent.ACTION_UP:
                        break;
                }

                return false;
            }
        });

        mPullToRefreshListView.setKeydownCallback(new PullToRefreshListView.KeydownCallback() {
            @Override
            public void keydown(MotionEvent event) {
                keydownX1 = event.getX();
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
            loadDialog.show();
        }
        IndexManager.fetchMoteList(moteType, pageNo, PAGE_SIZE).startUI(new ApiCallback<List<MoteInfoVO>>() {
            @Override
            public void onError(int code, String errorInfo) {
                if (needDialog) {
                    loadDialog.dismiss();
                }
                Toast.makeText(IndexFragment.this.getActivity(), errorInfo, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(List<MoteInfoVO> moteInfoVOs) {
                if (needDialog) {
                    loadDialog.dismiss();
                }
                ++pageNo;
                indexMoteAdapter.addAll(moteInfoVOs, needClear);
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
            loadDialog.show();
        }
        IndexManager.getIndexInfoJob().startUI(new ApiCallback<IndexVO>() {
            @Override
            public void onError(int code, String errorInfo) {
                if (isFirst) {
                    dataLoadingView.loadFail();
                } else {
                    loadDialog.dismiss();
                }
            }

            @Override
            public void onSuccess(IndexVO indexVO) {
                if (isFirst) {
                    dataLoadingView.loadSuccess();
                } else {
                    loadDialog.dismiss();
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
                    tv.setTextSize(22);
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
                    if (needClear) {
                        gotoTop();
                    }
                } else {
                    isShowBanner = false;
                    mHeaderView.findViewById(R.id.pager_layout).setVisibility(View.GONE);

                }

                mNavigationContainer.setVisibility(View.VISIBLE);

                List<MoteInfoVO> moteInfoVOs = indexVO.moteVOs;
                ++pageNo;
                indexMoteAdapter.addAll(moteInfoVOs, needClear);
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
        mFlipper.setInAnimation(this.getActivity(), R.anim.abc_slide_in_bottom);
        mFlipper.setOutAnimation(this.getActivity(), R.anim.abc_slide_out_top);
        ViewGroup.LayoutParams mViewPagerContainerLayoutParams = mViewPagerContainer.getLayoutParams();
        mViewPagerContainerLayoutParams.height = CommonUtil.dip2px(this.getActivity(), BANNER_HEIGHT_IN_DP)-200;
//        mViewPagerContainerLayoutParams.height =480;
                mHeaderViewPager = (LoopViewPager) mHeaderView.findViewById(R.id.loopViewPager);
        mHeaderViewIndicator = (CircleLoopPageIndicator) mHeaderView.findViewById(R.id.indicator);

        mNavigationContainer = (LinearLayout) mHeaderView.findViewById(R.id.navigation_container);

        mBannerImageAdapter = new BannerImageAdapter(this.getActivity().getSupportFragmentManager(), null);
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
        EventBus.getDefault().unregister(this);
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

    public class BannerImageAdapter extends FragmentPagerAdapter {

        List<AdvVO> dataLists = new ArrayList<AdvVO>();

        public BannerImageAdapter(FragmentManager fm, List<AdvVO> dataList) {
            super(fm);
            if (dataList != null && !dataList.isEmpty()) {
                this.dataLists.addAll(dataList);
            }
        }

        public void addItems(List<AdvVO> dataList) {
            this.dataLists.clear();
            this.dataLists.addAll(dataList);
            this.notifyDataSetChanged();
        }

        @Override
        public Fragment getItem(int position) {
            Log.d("test", "BannerImageAdapter getItem");
            Bundle args = new Bundle();
            args.putParcelable(BannerFragment.Banner, dataLists.get(position));
            args.putInt(BannerFragment.Position, position);
            args.putInt(BannerFragment.Height, BANNER_HEIGHT_IN_DP);
            return BannerFragment.newInstance(args);
        }

        @Override
        public int getCount() {
            if (dataLists != null) {
                return dataLists.size();
            }
            return 0;
        }

        public int getItemPosition(Object object) {
            return super.getItemPosition(object);
        }

        public long getItemId(int position) {
            if (position >= 0 && position < dataLists.size()) {
                AdvVO dataList = dataLists.get(position);
                if (dataList != null) {
                    long id = dataList.id;
                    return id;
                }
            }
            return (long) position;
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
