package com.floyd.onebuy.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.TextView;

import com.floyd.onebuy.aync.ApiCallback;
import com.floyd.onebuy.biz.manager.CommonwealManager;
import com.floyd.onebuy.biz.vo.AdvVO;
import com.floyd.onebuy.biz.vo.commonweal.CommonwealHomeVO;
import com.floyd.onebuy.ui.R;
import com.floyd.onebuy.ui.activity.CommonwealPoolPersionActivity;
import com.floyd.onebuy.ui.activity.PayPoolActivity;
import com.floyd.onebuy.ui.adapter.BannerImageAdapter;
import com.floyd.onebuy.ui.loading.DataLoadingView;
import com.floyd.onebuy.ui.loading.DefaultDataLoadingView;
import com.floyd.onebuy.ui.pageindicator.CircleLoopPageIndicator;
import com.floyd.onebuy.view.LoopViewPager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link CommonwealNewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CommonwealNewFragment extends CommonwealBaseFragment implements View.OnClickListener, AbsListView.OnScrollListener {

    public static final int CHANGE_BANNER_HANDLER_MSG_WHAT = 51;
    private static int PAGE_SIZE = 10;

    private DataLoadingView dataLoadingView;
    private View mViewPagerContainer;//整个广告
    private LoopViewPager mHeaderViewPager;//广告
    private CircleLoopPageIndicator mHeaderViewIndicator;//广告条索引
    private List<AdvVO> mTopBannerList;//最上部分左右循环广告条

    private BannerImageAdapter mBannerImageAdapter;

    private boolean isShowBanner;

    private TextView commonwealFeeView;
    private View commonwealFeeLayout;

    private TextView commonwealDescView;
    private TextView juankuanView;
    private Long id;

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

    public CommonwealNewFragment() {
    }

    public static CommonwealNewFragment newInstance(Long userId) {
        CommonwealNewFragment fragment = new CommonwealNewFragment();
        Bundle args = new Bundle();
        args.putLong(USER_ID, userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTopBannerList = new ArrayList<AdvVO>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.commonweal_new_layout, container, false);
        dataLoadingView = new DefaultDataLoadingView();
        dataLoadingView.initView(view, this);

        mViewPagerContainer = view.findViewById(R.id.pager_layout);
        mHeaderViewPager = (LoopViewPager) view.findViewById(R.id.loopViewPager);
        mHeaderViewIndicator = (CircleLoopPageIndicator) view.findViewById(R.id.indicator);
        commonwealFeeView = (TextView) view.findViewById(R.id.commonweal_fee_textview);
        commonwealFeeLayout = view.findViewById(R.id.commonweal_fee_view);
        commonwealFeeLayout.setOnClickListener(this);

        commonwealDescView = (TextView) view.findViewById(R.id.commonweal_desc_view);

        mBannerImageAdapter = new BannerImageAdapter(getActivity().getSupportFragmentManager(), null, null, BannerFragment.SCALE_FIT_XY);
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
        juankuanView = (TextView) view.findViewById(R.id.juanku_view);
        juankuanView.setOnClickListener(this);
        loadData(true);
        return view;
    }

    private void loadData(final boolean isFirst) {
        if (isFirst) {
            dataLoadingView.startLoading();
        }

        CommonwealManager.fetchCommonwealHomeData(PAGE_SIZE).startUI(new ApiCallback<CommonwealHomeVO>() {
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

                commonwealFeeLayout.setVisibility(View.VISIBLE);
                commonwealFeeView.setText(commonwealHomeVO.TotalMoney + "");

                commonwealDescView.setText(Html.fromHtml(commonwealHomeVO.FoundationHtml));
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
        if (isShowBanner /*&& firstVisibleItem >= mListView.getHeaderViewsCount()*/) {
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
            case R.id.act_ls_fail_layout:
                loadData(true);
                break;
            case R.id.commonweal_fee_view:
                Intent it = new Intent(getActivity(), CommonwealPoolPersionActivity.class);
                startActivity(it);
                break;
            case R.id.juanku_view:
                Intent payPoolIntent = new Intent(getActivity(), PayPoolActivity.class);
                startActivity(payPoolIntent);
                break;
        }

    }

    @Override
    Map<String, Object> getSwitchData() {
        return null;
    }
}
