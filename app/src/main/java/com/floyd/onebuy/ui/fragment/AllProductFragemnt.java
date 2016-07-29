package com.floyd.onebuy.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckedTextView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.android.volley.toolbox.ImageLoader;
import com.floyd.onebuy.aync.ApiCallback;
import com.floyd.onebuy.biz.manager.ProductManager;
import com.floyd.onebuy.biz.vo.AdvVO;
import com.floyd.onebuy.biz.vo.model.NewIndexVO;
import com.floyd.onebuy.biz.vo.model.WinningInfo;
import com.floyd.onebuy.biz.vo.product.ProductTypeVO;
import com.floyd.onebuy.ui.ImageLoaderFactory;
import com.floyd.onebuy.ui.R;
import com.floyd.onebuy.ui.adapter.ProductAdapter;
import com.floyd.onebuy.ui.adapter.ProductBannerImageAdapter;
import com.floyd.onebuy.ui.adapter.TypeAdapter;
import com.floyd.onebuy.ui.loading.DataLoadingView;
import com.floyd.onebuy.ui.loading.DefaultDataLoadingView;
import com.floyd.onebuy.ui.pageindicator.CircleLoopPageIndicator;
import com.floyd.onebuy.view.LoopViewPager;
import com.floyd.pullrefresh.widget.PullToRefreshBase;
import com.floyd.pullrefresh.widget.PullToRefreshListView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Created by floyd on 16-4-12.
 */
public class AllProductFragemnt extends BackHandledFragment implements View.OnClickListener {

    public static final int CHANGE_PRODUCT_BANNER_HANDLER_MSG_WHAT = 52;
    private static final int PAGE_SIZE = 12;
    private PullToRefreshListView mPullToRefreshListView;

    private ListView mRefreshListView;
    private ProductAdapter productAdapter;
    private View mHeaderView;
    private View mViewPagerContainer;//整个广告
    private LoopViewPager mHeaderViewPager;//广告

    private CircleLoopPageIndicator mHeaderViewIndicator;//广告条索引
    private ProductBannerImageAdapter mBannerImageAdapter;

    private ListView typeListView;

    private TypeAdapter typeAdapter;
    private DataLoadingView dataLoadingView;

    private ImageLoader mImageLoader;
    private long typeId = 0;
    private int sortType = 1;
    private int pageNo = 1;
    private boolean needClear;

    private LinearLayout banneLayout;

    private CheckedTextView lastestView; //按照最新排序
    private CheckedTextView hottestView; //按照最热排序
    private CheckedTextView fastestView; //按照最快排序
    private CheckedTextView priceView; //按照价格排序

    private CheckedTextView[] checkedTextViews;

    private CountDownLatch countDownLatch = new CountDownLatch(1);

    public static AllProductFragemnt newInstance(String param1, String param2) {
        AllProductFragemnt fragment = new AllProductFragemnt();
        return fragment;
    }

    public AllProductFragemnt() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mImageLoader = ImageLoaderFactory.createImageLoader();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_all_product, container, false);

        dataLoadingView = new DefaultDataLoadingView();
        dataLoadingView.initView(view, this);

        lastestView = (CheckedTextView) view.findViewById(R.id.lastest_view);
        hottestView = (CheckedTextView) view.findViewById(R.id.hottest_view);
        fastestView = (CheckedTextView) view.findViewById(R.id.fastest_view);
        priceView = (CheckedTextView) view.findViewById(R.id.price_view);

        checkedTextViews = new CheckedTextView[]{
                lastestView, hottestView, fastestView, priceView
        };

        lastestView.setOnClickListener(this);
        hottestView.setOnClickListener(this);
        fastestView.setOnClickListener(this);
        priceView.setOnClickListener(this);

        mPullToRefreshListView = (PullToRefreshListView) view.findViewById(R.id.product_list);
        mRefreshListView = mPullToRefreshListView.getRefreshableView();

        initListViewHeader();
        productAdapter = new ProductAdapter(getActivity(), new ArrayList<WinningInfo>());
        mRefreshListView.setAdapter(productAdapter);

        mPullToRefreshListView.setMode(PullToRefreshBase.Mode.BOTH);
        mPullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2() {
            @Override
            public void onPullDownToRefresh() {
                needClear = true;
                pageNo = 1;
                loadPageData();
                mPullToRefreshListView.onRefreshComplete(false, true);
            }

            @Override
            public void onPullUpToRefresh() {
                needClear = false;
                loadPageData();
                mPullToRefreshListView.onRefreshComplete(false, true);
            }
        });
        View emptyView = inflater.inflate(R.layout.empty_item, container,false);
        mPullToRefreshListView.setEmptyView(emptyView);

        typeListView = (ListView) view.findViewById(R.id.type_list_view);
        typeAdapter = new TypeAdapter(getActivity());
        typeListView.setAdapter(typeAdapter);
        typeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                typeId = typeAdapter.getItem(position).CodeID;
                needClear = true;
                pageNo = 1;
                typeAdapter.setCheckedIndex(position);
                loadPageData();
            }
        });
        loadData(true);
        return view;
    }

    private void initListViewHeader() {
        mHeaderView = LayoutInflater.from(this.getActivity()).inflate(R.layout.all_product_head, mRefreshListView, false);
        mViewPagerContainer = mHeaderView.findViewById(R.id.product_pager_layout);

        mHeaderViewPager = (LoopViewPager) mHeaderView.findViewById(R.id.product_loopViewPager);
        mHeaderViewIndicator = (CircleLoopPageIndicator) mHeaderView.findViewById(R.id.product_indicator);
        mBannerImageAdapter = new ProductBannerImageAdapter(this.getActivity().getSupportFragmentManager(), null, null);
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
        mRefreshListView.addHeaderView(mHeaderView);
    }

    /**
     * 外部切换
     * @param typeId
     */
    public void switchType(final long typeId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int position = 0;
                try {
                    countDownLatch.await();
                } catch (InterruptedException e) {
                }
                for (ProductTypeVO vo :typeAdapter.getTypeList()) {
                    if (vo.CodeID == typeId) {
                        break;
                    }
                    position++;
                }
                AllProductFragemnt.this.typeId = typeId;
                needClear = true;
                pageNo = 1;
                typeAdapter.setCheckedIndex(position);
                loadPageData();
            }
        }).start();
    }

    private void checkSortType(int type) {
        this.sortType = type;
        for (int i = 0; i < checkedTextViews.length; i++) {
            CheckedTextView checkedTextView = checkedTextViews[i];
            if (i == type - 1) {
                checkedTextView.setChecked(true);
            } else {
                checkedTextView.setChecked(false);
            }
        }
        pageNo = 1;
        needClear = true;
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

                List<AdvVO> advVOs = indexVO.advertisList;
                if (advVOs == null || advVOs.isEmpty()) {
                    mViewPagerContainer.setVisibility(View.GONE);
                } else {
                    mViewPagerContainer.setVisibility(View.VISIBLE);
                    mBannerImageAdapter.addItems(advVOs);
                    mHeaderViewIndicator.setTotal(mBannerImageAdapter.getCount());
                    mHeaderViewIndicator.setIndex(0);
                    if (advVOs.size() == 1) {
                        mHeaderViewIndicator.setVisibility(View.GONE);
                    } else {
                        mHeaderViewIndicator.setVisibility(View.VISIBLE);
                    }
                }

                List<WinningInfo> records = indexVO.theNewList;
                productAdapter.addAll(records, true);

                List<ProductTypeVO> typeList = indexVO.typeList;
                ProductTypeVO allProductTypeVO = new ProductTypeVO();
                allProductTypeVO.CodeName = "全部商品";
                allProductTypeVO.CodeID = 0l;
                typeList.add(0, allProductTypeVO);
                typeAdapter.addAll(typeList);
                pageNo = 2;
                if (countDownLatch.getCount() > 0) {
                    countDownLatch.countDown();
                }
            }

            @Override
            public void onProgress(int progress) {

            }
        });
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.act_ls_fail_layout:
                sortType = 1;
                pageNo = 1;
                typeId = 0;
                needClear = true;
                loadData(true);
                break;

            case R.id.lastest_view:
                sortType = 1;
                needClear = true;
                pageNo = 1;
                checkSortType(sortType);
                loadPageData();
                break;
            case R.id.hottest_view:
                sortType = 2;
                pageNo = 1;
                needClear = true;
                checkSortType(sortType);
                loadPageData();
                break;
            case R.id.fastest_view:
                sortType = 3;
                needClear = true;
                pageNo = 1;
                checkSortType(sortType);
                loadPageData();
                break;
            case R.id.price_view:
                sortType = 4;
                needClear = true;
                pageNo = 1;
                checkSortType(sortType);
                loadPageData();
                break;
        }


    }

    private void loadPageData() {
        ProductManager.fetchProductLssueVOs(PAGE_SIZE, pageNo, typeId, sortType).startUI(new ApiCallback<List<WinningInfo>>() {
            @Override
            public void onError(int code, String errorInfo) {

            }

            @Override
            public void onSuccess(List<WinningInfo> winningInfos) {
                productAdapter.addAll(winningInfos, needClear);
                ++pageNo;
            }

            @Override
            public void onProgress(int progress) {

            }
        });

    }
}
