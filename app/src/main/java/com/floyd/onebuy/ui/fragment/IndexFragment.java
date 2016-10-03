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
import android.text.TextUtils;
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
import com.floyd.onebuy.biz.constants.APIConstants;
import com.floyd.onebuy.biz.constants.BuyCarType;
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
import com.floyd.onebuy.event.TabSwitchEvent;
import com.floyd.onebuy.ui.ImageLoaderFactory;
import com.floyd.onebuy.ui.R;
import com.floyd.onebuy.ui.activity.CommonwealBakActivity;
import com.floyd.onebuy.ui.activity.FridayActivity;
import com.floyd.onebuy.ui.activity.H5Activity;
import com.floyd.onebuy.ui.activity.SearchActivity;
import com.floyd.onebuy.ui.activity.ShowShareActivity;
import com.floyd.onebuy.ui.adapter.BannerImageAdapter;
import com.floyd.onebuy.ui.adapter.ProductLssueAdapter;
import com.floyd.onebuy.ui.loading.DataLoadingView;
import com.floyd.onebuy.ui.loading.DefaultDataLoadingView;
import com.floyd.onebuy.ui.pageindicator.CircleLoopPageIndicator;
import com.floyd.onebuy.utils.WXUtil;
import com.floyd.onebuy.view.LoopViewPager;
import com.floyd.onebuy.view.UIAlertDialog;
import com.floyd.pullrefresh.widget.PullToRefreshBase;
import com.floyd.pullrefresh.widget.PullToRefreshListView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private ProductLssueAdapter indexProductAdapter;
    private int sortType = 1;
    private int pageNo = 1;
    private int PAGE_SIZE = 12;
    private boolean needClear;

    private CheckedTextView lastestView;
    private CheckedTextView hottestView;
    private CheckedTextView fastestView;
    private CheckedTextView priceView;

    private CheckedTextView[] checkedTextViews;

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
    private LinearLayout searchView;//操作指引

    private ImageLoader mImageLoader;

    private ViewFlipper mFlipper;

    private Long[] typeCodes = new Long[]{21l,22l};
    private int[] defaultImages = new int[]{R.drawable.ten_index, R.drawable.hun_index, R.drawable.gongyi_index,R.drawable.shandan_index, R.drawable.fri_index};
    private String[] defaultTexts = new String[]{"十元区", "百元区", "公益", "晒单", "快乐星期五"};


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

    private View.OnClickListener[] listeners = null;

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
        for (int i = 0; i < checkedTextViews.length; i++) {
            CheckedTextView checkedTextView = checkedTextViews[i];
            if (i == type - 1) {
                checkedTextView.setChecked(true);
            } else {
                checkedTextView.setChecked(false);
            }
        }
        this.sortType = type;
        this.pageNo = 1;
        this.needClear = true;
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
        indexProductAdapter = new ProductLssueAdapter(BuyCarType.NORMAL, this.getActivity(), new ArrayList<WinningInfo>(), mImageLoader, null);
        mListView.setAdapter(indexProductAdapter);
        mPullToRefreshListView.setMode(PullToRefreshBase.Mode.BOTH);
        View emptyView = inflater.inflate(R.layout.empty_item, container, false);
        mPullToRefreshListView.setEmptyView(emptyView);
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
        searchView = ((LinearLayout) view.findViewById(R.id.right_layout));
        searchView.setOnClickListener(this);
        searchView.setVisibility(View.VISIBLE);
        qiandaoView = view.findViewById(R.id.left_layout);
        qiandaoView.setOnClickListener(this);
    }

    private void initButton() {

        lastestView = (CheckedTextView) mNavigationContainer.findViewById(R.id.lastest_view);
        hottestView = (CheckedTextView) mNavigationContainer.findViewById(R.id.hottest_view);
        fastestView = (CheckedTextView) mNavigationContainer.findViewById(R.id.fastest_view);
        priceView = (CheckedTextView) mNavigationContainer.findViewById(R.id.price_view);

        checkedTextViews = new CheckedTextView[]{
                lastestView, hottestView, fastestView, priceView
        };

        lastestView.setChecked(true);
        lastestView.setOnClickListener(this);
        hottestView.setOnClickListener(this);
        fastestView.setOnClickListener(this);
        priceView.setOnClickListener(this);

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

                categoryLayout.setVisibility(View.VISIBLE);
                TypeClickListener tenTypeClickListener = new TypeClickListener(typeCodes[0]);
                TypeClickListener hundeTypeClickListener = new TypeClickListener(typeCodes[1]);
                listeners = new View.OnClickListener[]{tenTypeClickListener, hundeTypeClickListener, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent it = new Intent(getActivity(), CommonwealBakActivity.class);
                        getActivity().startActivity(it);
                    }
                },new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent it = new Intent(getActivity(), ShowShareActivity.class);
                        getActivity().startActivity(it);
                    }
                },new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
//                        Intent it = new Intent(getActivity(), FridayActivity.class);
//                        getActivity().startActivity(it);
                        UIAlertDialog.Builder noticeBuilder = new UIAlertDialog.Builder(getActivity());
                        SpannableString message = new SpannableString("该板块暂未开发,敬请期待!");
                        noticeBuilder.setMessage(message)
                                .setCancelable(true)
                                .setPositiveButton("确认",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog,
                                                                int id) {
                                                dialog.dismiss();
                                            }
                                        });
                        AlertDialog dialog2 = noticeBuilder.create();
                        dialog2.show();
                    }
                }};
                for (int i=0; i < 5; i++) {
                    imageViews[i].setDefaultImageResId(defaultImages[i]);
                    imageViews[i].setImageUrl(null, mImageLoader);
                    typeDeses[i].setText(defaultTexts[i]);
                    imageViews[i].setOnClickListener(listeners[i]);
                }

//                List<ProductTypeVO> typeList = indexVO.typeList;
//                if (typeList != null && !typeList.isEmpty()) {
//                    Map<Long, ProductTypeVO> productTypeVOMap = new HashMap<Long, ProductTypeVO>();
//                    for (int i = 0; i < typeList.size(); i++) {
//                        ProductTypeVO typeVO = typeList.get(i);
//                        productTypeVOMap.put(typeVO.CodeID, typeVO);
//                    }
//
//                    int typeSize = typeCodes.length > 5 ? 5 : typeCodes.length;
//                    for (int k=0; k < typeSize; k++) {
//                        ProductTypeVO vv = productTypeVOMap.get(typeCodes[k]);
//                        if (vv == null) {
//                            continue;
//                        }
//                        String typePic = vv.TypePic;
//                        imageViews[k].setImageUrl(typePic, mImageLoader);
//                        typeDeses[k].setText(vv.CodeName);
//                        imageViews[k].setOnClickListener(listeners[k]);
//                    }
//                }

                List<WinningInfo> winningRecordVOs = indexVO.theNewList;
                indexProductAdapter.addAll(winningRecordVOs, needClear);

                final String newsImageUrl = indexVO.newsImageUrl;
                newsImageView.setDefaultImageResId(R.drawable.news_pic);
                newsImageView.setImageUrl(newsImageUrl, mImageLoader, new BitmapProcessor() {
                    @Override
                    public Bitmap processBitmap(final Bitmap bitmap) {

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
                pageNo = 2;
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
        mHeaderViewPager = (LoopViewPager) mHeaderView.findViewById(R.id.loopViewPager);
        mHeaderViewIndicator = (CircleLoopPageIndicator) mHeaderView.findViewById(R.id.indicator);

        mNavigationContainer = (LinearLayout) mHeaderView.findViewById(R.id.navigation_container);

        mBannerImageAdapter = new BannerImageAdapter(this.getActivity().getSupportFragmentManager(), null, null, BannerFragment.SCALE_CENTER_CROP);
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
        newsImageView.setOnClickListener(this);
        mListView.addHeaderView(mHeaderView);
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


    public void onDestroy() {
        super.onDestroy();
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
                checkSortType(1);
                loadProductLssueVO();
                break;
            case R.id.hottest_view:
                checkSortType(2);
                loadProductLssueVO();
                break;
            case R.id.fastest_view:
                checkSortType(3);
                loadProductLssueVO();
                break;
            case R.id.price_view:
                checkSortType(4);
                loadProductLssueVO();
                break;
            case R.id.act_ls_fail_layout:
                sortType = 1;
                pageNo = 1;
                needClear = true;
                loadData(true);
                break;
            case R.id.left_layout:
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
                            message.setSpan(new ForegroundColorSpan(Color.parseColor("#ff424c")), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
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
                break;
            case R.id.right_layout:
                Intent it = new Intent(this.getActivity(), SearchActivity.class);
                startActivity(it);
                break;
            case R.id.news_pic_view:
                Intent wealIntent = new Intent(getActivity(), CommonwealBakActivity.class);
                wealIntent.putExtra(CommonwealBakActivity.CURRENT_PAGE_INDEX, 1);
                startActivity(wealIntent);
                break;
        }

    }


    private static class TypeClickListener implements View.OnClickListener {
        private long typeId;
        public TypeClickListener(long typeId) {
            this.typeId = typeId;
        }

        @Override
        public void onClick(View v) {
            Map<String, Object> data = new HashMap<String, Object>();
            data.put(APIConstants.INDEX_PRODUCT_TYPE_ID, typeId);
            EventBus.getDefault().post(new TabSwitchEvent(R.id.tab_all_product, data));
        }
    }

}
