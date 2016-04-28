package com.floyd.onebuy.ui.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
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

import com.android.volley.toolbox.BitmapProcessor;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.floyd.onebuy.aync.ApiCallback;
import com.floyd.onebuy.biz.constants.EnvConstants;
import com.floyd.onebuy.biz.manager.JiFengManager;
import com.floyd.onebuy.biz.manager.LoginManager;
import com.floyd.onebuy.biz.manager.ProductManager;
import com.floyd.onebuy.biz.tools.FileTools;
import com.floyd.onebuy.biz.vo.AdvVO;
import com.floyd.onebuy.biz.vo.json.SignInVO;
import com.floyd.onebuy.biz.vo.json.UserVO;
import com.floyd.onebuy.biz.vo.json.WordNewsVO;
import com.floyd.onebuy.biz.vo.model.NewIndexVO;
import com.floyd.onebuy.biz.vo.model.WinningInfo;
import com.floyd.onebuy.biz.vo.product.ProductTypeVO;
import com.floyd.onebuy.channel.threadpool.WxDefaultExecutor;
import com.floyd.onebuy.ui.ImageLoaderFactory;
import com.floyd.onebuy.ui.R;
import com.floyd.onebuy.ui.activity.H5Activity;
import com.floyd.onebuy.ui.activity.SearchActivity;
import com.floyd.onebuy.ui.adapter.BannerImageAdapter;
import com.floyd.onebuy.ui.adapter.ProductAdapter;
import com.floyd.onebuy.ui.loading.DataLoadingView;
import com.floyd.onebuy.ui.loading.DefaultDataLoadingView;
import com.floyd.onebuy.ui.pageindicator.CircleLoopPageIndicator;
import com.floyd.onebuy.utils.CommonUtil;
import com.floyd.onebuy.utils.WXUtil;
import com.floyd.onebuy.view.LoopViewPager;
import com.floyd.onebuy.view.UIAlertDialog;
import com.floyd.pullrefresh.widget.PullToRefreshBase;
import com.floyd.pullrefresh.widget.PullToRefreshListView;

import java.io.File;
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
    private boolean isShowBanner;
    private DataLoadingView dataLoadingView;
    private ProductAdapter indexProductAdapter;
    private int sortType = 1;
    private int pageNo = 1;
    private int PAGE_SIZE = 10;
    private boolean needClear;

    private CheckedTextView lastestView;
    private CheckedTextView hottestView;
    private CheckedTextView fastestView;

    private View categoryLayout;
    private NetworkImageView typeImageView1;
    private NetworkImageView typeImageView2;
    private NetworkImageView typeImageView3;
    private NetworkImageView typeImageView4;
    private NetworkImageView typeImageView5;

    private TextView typeTextView1;
    private TextView typeTextView2;
    private TextView typeTextView3;
    private TextView typeTextView4;
    private TextView typeTextView5;

    private NetworkImageView[] imageViews;
    private TextView[] typeDeses;

    private NetworkImageView newsImageView;

    private View qiandaoView;//每日签到
    private LinearLayout guide;//操作指引

    private ImageLoader mImageLoader;

    private ViewFlipper mFlipper;

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
        mTopBannerList = new ArrayList<AdvVO>();
    }

    private void checkSortType(int type) {
        if (type == 1) {
            lastestView.setChecked(true);
            hottestView.setChecked(false);
            fastestView.setChecked(false);
            this.sortType = type;
            this.pageNo = 1;
        } else if (type == 2) {
            lastestView.setChecked(false);
            hottestView.setChecked(true);
            fastestView.setChecked(false);
            this.sortType = type;
            this.pageNo = 1;
        } else if (type == 3) {
            lastestView.setChecked(false);
            hottestView.setChecked(false);
            fastestView.setChecked(true);
            this.sortType = type;
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
        indexProductAdapter = new ProductAdapter(this.getActivity(), new ArrayList<WinningInfo>(), mImageLoader);
        mListView.setAdapter(indexProductAdapter);
        mPullToRefreshListView.setMode(PullToRefreshBase.Mode.BOTH);
        mPullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2() {
            @Override
            public void onPullDownToRefresh() {
                needClear = true;
                loadData(false);
                pageNo = 1;
                sortType = 1;
                checkSortType(1);
                mPullToRefreshListView.onRefreshComplete(false, true);
            }

            @Override
            public void onPullUpToRefresh() {
                needClear = false;
                loadProductLssueVO();
                mPullToRefreshListView.onRefreshComplete(false, true);
            }
        });

        return view;
    }

    public void init(View view) {
        guide = ((LinearLayout) view.findViewById(R.id.guide));
        qiandaoView = view.findViewById(R.id.right_layout);
        guide.setOnClickListener(this);

        //跳转到操作指引界面
//        guide.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(getActivity(), GuideActivity.class));
//            }
//        });

        //跳转到筛选模特界面
        qiandaoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (LoginManager.isLogin(IndexFragment.this.getActivity())){
                    UserVO vo = LoginManager.getLoginInfo(IndexFragment.this.getActivity());
                    JiFengManager.dailySignIn(vo.ID).startUI(new ApiCallback<SignInVO>() {
                        @Override
                        public void onError(int code, String errorInfo) {
                            Toast.makeText(IndexFragment.this.getActivity(), errorInfo, Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onSuccess(SignInVO signInVO) {
                            UIAlertDialog.Builder clearBuilder = new UIAlertDialog.Builder(IndexFragment.this.getActivity());
                            SpannableString message = new SpannableString("亲！您签到成功，奖励" + signInVO.AddJF + "积分");
                            message.setSpan(new RelativeSizeSpan(2), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            message.setSpan(new ForegroundColorSpan(Color.parseColor("#d4377e")), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            clearBuilder.setMessage(message)
                                    .setCancelable(true)
                                    .setNegativeButton("确认", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                            AlertDialog dialog2 = clearBuilder.create();
                            dialog2.show();
                        }

                        @Override
                        public void onProgress(int progress) {

                        }
                    });
                }
            }
        });
    }

    private void initButton() {

        lastestView = (CheckedTextView) mNavigationContainer.findViewById(R.id.lastest_view);
        hottestView = (CheckedTextView) mNavigationContainer.findViewById(R.id.hottest_view);
        fastestView = (CheckedTextView) mNavigationContainer.findViewById(R.id.fastest_view);

        lastestView.setOnClickListener(this);
        hottestView.setOnClickListener(this);
        fastestView.setOnClickListener(this);
        lastestView.setChecked(true);
    }

    public void loadProductLssueVO() {
        ProductManager.fetchProductLssueVOs(PAGE_SIZE, pageNo, 0, sortType).startUI(new ApiCallback<List<WinningInfo>>() {
            @Override
            public void onError(int code, String errorInfo) {

            }

            @Override
            public void onSuccess(List<WinningInfo> winningRecordVOs) {
                indexProductAdapter.addAll(winningRecordVOs, needClear);
                ++pageNo;
            }

            @Override
            public void onProgress(int progress) {

            }
        });
    }

    private void loadData(final boolean isFirst) {
        if (isFirst) {
            dataLoadingView.startLoading();
        }

        ProductManager.fetchIndexData().startUI(new ApiCallback<NewIndexVO>() {
            @Override
            public void onError(int code, String errorInfo) {
                if (isFirst) {
                    dataLoadingView.loadFail();
                }
            }

            @Override
            public void onSuccess(NewIndexVO indexVO) {
                if (isFirst) {
                    dataLoadingView.loadSuccess();
                }

                List<WordNewsVO> wordList = indexVO.wordList;
                if (wordList != null) {
                    for (final WordNewsVO vo : wordList) {
                        TextView tv = new TextView(IndexFragment.this.getActivity());
                        tv.setTextSize(16);
                        tv.setPadding(10, 10, 10, 10);
                        tv.setTextColor(Color.RED);
                        tv.setText(vo.Title);
                        tv.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent h5Activity = new Intent(IndexFragment.this.getActivity(), H5Activity.class);
                                H5Activity.H5Data h5Data = new H5Activity.H5Data();
                                h5Data.dataType = H5Activity.H5Data.H5_DATA_TYPE_URL;
                                h5Data.data = vo.Url;
                                h5Data.showProcess = true;
                                h5Data.showNav = true;
                                h5Data.title = "公告";
                                h5Activity.putExtra(H5Activity.H5Data.H5_DATA, h5Data);
                                startActivity(h5Activity);
                            }
                        });
                        mFlipper.addView(tv);
                    }
                }

                mFlipper.setAutoStart(true);
                mFlipper.startFlipping();
                mViewPagerContainer.setVisibility(View.VISIBLE);

                mTopBannerList.clear();

                List<AdvVO> advVOs = indexVO.advertisList;
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
                } else {
                    isShowBanner = false;
                    mHeaderView.findViewById(R.id.pager_layout).setVisibility(View.GONE);
                }

                mNavigationContainer.setVisibility(View.VISIBLE);

                List<ProductTypeVO> typeList = indexVO.typeList;
                if (typeList == null || typeList.isEmpty()) {
                    categoryLayout.setVisibility(View.GONE);
                } else {
                    categoryLayout.setVisibility(View.VISIBLE);
                    for (int i = 0; i < 5; i++) {
                        ProductTypeVO typeVO = typeList.get(i);
                        imageViews[i].setImageUrl(typeVO.getTypePic(), mImageLoader);
                        typeDeses[i].setText(typeVO.CodeName);
                    }
                }

                List<WinningInfo> winningRecordVOs = indexVO.theNewList;
                indexProductAdapter.addAll(winningRecordVOs, needClear);

                final String newsImageUrl = indexVO.newsImageUrl;
                newsImageView.setImageUrl(newsImageUrl, mImageLoader, new BitmapProcessor() {
                    @Override
                    public Bitmap processBitmpa(final Bitmap bitmap) {

                        WxDefaultExecutor.getInstance().submitHighPriority(new Runnable() {
                            @Override
                            public void run() {
                                final String md5Name = WXUtil.getMD5FileName(newsImageUrl);
                                FileTools.writeBitmap(EnvConstants.imageRootPath + File.separator + md5Name, bitmap, 100);
                            }
                        });
                        return bitmap;
                    }
                });
                ++pageNo;
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

//        mHeaderView.findViewById(R.id.fri_area).setOnClickListener(this);

        categoryLayout = mHeaderView.findViewById(R.id.category);
        typeImageView1 = (NetworkImageView) mHeaderView.findViewById(R.id.type1_imageView);
        typeImageView2 = (NetworkImageView) mHeaderView.findViewById(R.id.type2_imageView);
        typeImageView3 = (NetworkImageView) mHeaderView.findViewById(R.id.type3_imageView);
        typeImageView4 = (NetworkImageView) mHeaderView.findViewById(R.id.type4_imageView);
        typeImageView5 = (NetworkImageView) mHeaderView.findViewById(R.id.type5_imageView);

        typeTextView1 = (TextView) mHeaderView.findViewById(R.id.type1_textView);
        typeTextView2 = (TextView) mHeaderView.findViewById(R.id.type2_textView);
        typeTextView3 = (TextView) mHeaderView.findViewById(R.id.type3_textView);
        typeTextView4 = (TextView) mHeaderView.findViewById(R.id.type4_textView);
        typeTextView5 = (TextView) mHeaderView.findViewById(R.id.type5_textView);

        imageViews = new NetworkImageView[]{typeImageView1, typeImageView2, typeImageView3, typeImageView4, typeImageView5};
        typeDeses = new TextView[]{typeTextView1, typeTextView2, typeTextView3, typeTextView4, typeTextView5};

        newsImageView = (NetworkImageView) mHeaderView.findViewById(R.id.news_pic_view);
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
            if (mFlipper != null) {
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
            case R.id.lastest_view:
                lastestView.setChecked(true);
                hottestView.setChecked(false);
                fastestView.setChecked(false);
                sortType = 1;
                pageNo = 1;
                needClear = true;
                loadProductLssueVO();
                break;

            case R.id.hottest_view:
                lastestView.setChecked(false);
                hottestView.setChecked(true);
                fastestView.setChecked(false);
                sortType = 2;
                pageNo = 1;
                needClear = true;
                loadProductLssueVO();
                break;

            case R.id.fastest_view:
                lastestView.setChecked(false);
                hottestView.setChecked(false);
                fastestView.setChecked(true);
                sortType = 3;
                pageNo = 1;
                needClear = true;
                loadProductLssueVO();
                break;
            case R.id.act_ls_fail_layout:
                sortType = 1;
                pageNo = 1;
                needClear = true;
                loadData(true);
                break;
            case R.id.guide:
                Intent it = new Intent(this.getActivity(), SearchActivity.class);
                startActivity(it);
                break;
//            case R.id.fri_area:
//                Intent fridayIntent = new Intent(this.getActivity(), FridayActivity.class);
//                startActivity(fridayIntent);
//                break;
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
