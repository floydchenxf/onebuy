package com.floyd.onebuy.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.android.volley.toolbox.ImageLoader;
import com.floyd.onebuy.R;
import com.floyd.onebuy.aync.ApiCallback;
import com.floyd.onebuy.biz.manager.WinningManager;
import com.floyd.onebuy.biz.vo.winning.WinningInfo;
import com.floyd.onebuy.ui.ImageLoaderFactory;
import com.floyd.onebuy.ui.adapter.BuyCarAdapter;
import com.floyd.onebuy.ui.loading.DataLoadingView;
import com.floyd.onebuy.ui.loading.DefaultDataLoadingView;
import com.floyd.pullrefresh.widget.PullToRefreshBase;
import com.floyd.pullrefresh.widget.PullToRefreshListView;

import java.util.List;

/**
 * Created by floyd on 16-4-13.
 */
public class BuyCarFragment extends BackHandledFragment implements View.OnClickListener {

    private DataLoadingView dataLoadingView;
    private PullToRefreshListView mPullToRefreshListView;
    private ListView mListView;
    private ImageLoader mImageLoader;
    private BuyCarAdapter mBuyCarAdapter;
    private int pageNo;
    private boolean needClear;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mImageLoader = ImageLoaderFactory.createImageLoader();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_buy_car, null);
        dataLoadingView = new DefaultDataLoadingView();
        dataLoadingView.initView(view, this);
        mPullToRefreshListView = (PullToRefreshListView) view.findViewById(R.id.buy_car_list);
        mPullToRefreshListView.setMode(PullToRefreshBase.Mode.PULL_UP_TO_REFRESH);
        mPullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2() {
            @Override
            public void onPullDownToRefresh() {
            }

            @Override
            public void onPullUpToRefresh() {
                needClear = false;
                pageNo++;
                loadData(false);
                mPullToRefreshListView.onRefreshComplete(false, true);
            }
        });
        mListView = mPullToRefreshListView.getRefreshableView();
        mBuyCarAdapter = new BuyCarAdapter(this.getActivity(), null, mImageLoader);
        mListView.setAdapter(mBuyCarAdapter);
        loadData(true);
        return view;
    }

    private void loadData(final boolean isFirst) {
        if (isFirst) {
            dataLoadingView.startLoading();
        }

        WinningManager.fetchBuyCar(0l, 0l).startUI(new ApiCallback<List<WinningInfo>>() {
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
                mBuyCarAdapter.addAll(winningInfos, needClear);
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
