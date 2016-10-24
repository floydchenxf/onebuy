package com.floyd.onebuy.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.floyd.onebuy.aync.ApiCallback;
import com.floyd.onebuy.biz.constants.BuyCarType;
import com.floyd.onebuy.biz.manager.ProductManager;
import com.floyd.onebuy.biz.vo.model.WinningInfo;
import com.floyd.onebuy.ui.ImageLoaderFactory;
import com.floyd.onebuy.ui.PrizeListener;
import com.floyd.onebuy.ui.R;
import com.floyd.onebuy.ui.adapter.ProductLssueAdapter;
import com.floyd.onebuy.ui.loading.DataLoadingView;
import com.floyd.onebuy.ui.loading.DefaultDataLoadingView;
import com.floyd.onebuy.view.BasePopupWindow;
import com.floyd.onebuy.view.CenterPopupWindow;
import com.floyd.pullrefresh.widget.PullToRefreshBase;
import com.floyd.pullrefresh.widget.PullToRefreshListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by floyd on 16-4-13.
 * 开奖页面
 */
public class NewOwnerFragment extends BackHandledFragment implements View.OnClickListener {

    private static final int PAGE_SIZE = 10;

    private int pageNo = 1;
    private boolean needClear = true;
    private PullToRefreshListView mPullToRefreshListView;
    private ListView mListView;
    private DataLoadingView dataLoadingView;
    private ProductLssueAdapter productLssueAdapter;
    private ImageLoader mImageLoader;

    private boolean isBottom;

    public static NewOwnerFragment newInstance(String param1, String param2) {
        NewOwnerFragment fragment = new NewOwnerFragment();
        return fragment;
    }

    public NewOwnerFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mImageLoader = ImageLoaderFactory.createImageLoader();

    }

    @Override
    public void onResume() {
        super.onResume();
        loadData(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_new_owner, container, false);
        dataLoadingView = new DefaultDataLoadingView();
        dataLoadingView.initView(view, this);
        view.findViewById(R.id.title_back).setVisibility(View.GONE);
        TextView titleNameView = (TextView) view.findViewById(R.id.title_name);
        titleNameView.setText("最新揭晓");
        titleNameView.setVisibility(View.VISIBLE);
        mPullToRefreshListView = (PullToRefreshListView) view.findViewById(R.id.product_list);
        mListView = mPullToRefreshListView.getRefreshableView();
        mPullToRefreshListView.setMode(PullToRefreshBase.Mode.BOTH);
        View emptyView = inflater.inflate(R.layout.empty_item, container, false);
        mPullToRefreshListView.setEmptyView(emptyView);
        mPullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2() {
            @Override
            public void onPullDownToRefresh() {
                pageNo = 1;
                needClear = true;
                loadData(false);
                mPullToRefreshListView.onRefreshComplete(false, true);
            }

            @Override
            public void onPullUpToRefresh() {
                pageNo++;
                needClear = false;
                mPullToRefreshListView.onRefreshComplete(false, true);
            }
        });
        productLssueAdapter = new ProductLssueAdapter(BuyCarType.NORMAL, this.getActivity(), new ArrayList<WinningInfo>(), mImageLoader, new PrizeListener() {
            @Override
            public void prizeCallback(String title, String prizeCode, String productUrl) {

            }
        });

        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (visibleItemCount + firstVisibleItem == totalItemCount - 1) {
                    isBottom = true;
                } else {
                    isBottom = false;
                }
            }

            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
                switch (i) {
                    case SCROLL_STATE_TOUCH_SCROLL:
                        if (isBottom) {
                            productLssueAdapter.setScroll(true);
                        } else {
                            productLssueAdapter.setScroll(false);
                        }
                        break;
                    case SCROLL_STATE_IDLE:
                        productLssueAdapter.setScroll(false);
                        break;
                }
            }
        });
        mListView.setAdapter(productLssueAdapter);
        return view;
    }


    private void loadData(final boolean isFirst) {
        if (isFirst) {
            dataLoadingView.startLoading();
        }

        ProductManager.getNewestProductLssues(PAGE_SIZE, pageNo).startUI(new ApiCallback<List<WinningInfo>>() {
            @Override
            public void onError(int code, String errorInfo) {
                if (isFirst) {
                    dataLoadingView.loadFail();
                }
            }

            @Override
            public void onSuccess(List<WinningInfo> winningInfos) {
                if (isFirst) {
                    dataLoadingView.loadSuccess();
                }
                productLssueAdapter.addAll(winningInfos, needClear);
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
                pageNo = 1;
                needClear = true;
                loadData(true);
                break;
        }

    }
}
