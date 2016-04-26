package com.floyd.onebuy.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.android.volley.toolbox.ImageLoader;
import com.floyd.onebuy.biz.vo.model.WinningInfo;
import com.floyd.onebuy.ui.ImageLoaderFactory;
import com.floyd.onebuy.ui.R;
import com.floyd.onebuy.ui.adapter.ProductAdapter;
import com.floyd.onebuy.ui.loading.DataLoadingView;
import com.floyd.onebuy.ui.loading.DefaultDataLoadingView;
import com.floyd.pullrefresh.widget.PullToRefreshBase;
import com.floyd.pullrefresh.widget.PullToRefreshListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by floyd on 16-4-13.
 * 开奖页面
 */
public class NewOwnerFragment extends BackHandledFragment implements View.OnClickListener {

    private PullToRefreshListView mPullToRefreshListView;
    private ListView mListView;
    private DataLoadingView dataLoadingView;
    private ProductAdapter productAdapter;
    private ImageLoader mImageLoader;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_new_owner, container, false);
        dataLoadingView = new DefaultDataLoadingView();
        dataLoadingView.initView(view, this);
        mPullToRefreshListView = (PullToRefreshListView) view.findViewById(R.id.product_list);
        mListView = mPullToRefreshListView.getRefreshableView();
        mPullToRefreshListView.setMode(PullToRefreshBase.Mode.PULL_UP_TO_REFRESH);
        mPullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2() {
            @Override
            public void onPullDownToRefresh() {
            }

            @Override
            public void onPullUpToRefresh() {
                mPullToRefreshListView.onRefreshComplete(false, true);
            }
        });
        productAdapter = new ProductAdapter(this.getActivity(), new ArrayList<WinningInfo>(), mImageLoader);
        mListView.setAdapter(productAdapter);
        loadData();
        return view;
    }

    private void loadData() {
        List<WinningInfo> winningRecordVOs = new ArrayList<WinningInfo>();
        for(int i = 0; i < 1; i++) {
            WinningInfo vo  = new WinningInfo();
            vo.productUrl = "http://qmmt2015.b0.upaiyun.com/2016/4/12/70242b33-34df-4db5-a334-46000335e8f4.png";
            vo.joinedCount=i+ 1;
            vo.totalCount = 100;
            vo.id = i+1;
            vo.processPrecent=(50+i)+"";
            vo.title = "小米手机５｜｜精彩开奖就送苹果";
            vo.status = 2;
            vo.lotteryTime = System.currentTimeMillis() + 100000;
            winningRecordVOs.add(vo);
        }
        productAdapter.addAll(winningRecordVOs, true);
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    public void onClick(View v) {

    }
}
