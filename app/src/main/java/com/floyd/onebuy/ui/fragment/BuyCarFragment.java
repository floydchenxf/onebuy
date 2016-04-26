package com.floyd.onebuy.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.floyd.onebuy.ui.R;
import com.floyd.onebuy.aync.ApiCallback;
import com.floyd.onebuy.biz.manager.WinningManager;
import com.floyd.onebuy.biz.vo.model.WinningInfo;
import com.floyd.onebuy.ui.ImageLoaderFactory;
import com.floyd.onebuy.ui.activity.PayActivity;
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
    private TextView titleNameView;


    private TextView totalProductView;//总计view
    private TextView payView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mImageLoader = ImageLoaderFactory.createImageLoader();
        needClear = true;
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
//                needClear = false;
//                pageNo++;
//                loadData(false);
                mPullToRefreshListView.onRefreshComplete(false, true);
            }
        });
        mListView = mPullToRefreshListView.getRefreshableView();
        mBuyCarAdapter = new BuyCarAdapter(this.getActivity(), null, mImageLoader, new BuyCarAdapter.BuyClickListener() {
            @Override
            public void onClick(View v) {
                int productNum = mBuyCarAdapter.getRecords().size();
                int totalPrice = 0;
                for (WinningInfo info:mBuyCarAdapter.getRecords()) {
                    totalPrice += info.buyCount;
                }

                totalProductView.setText(Html.fromHtml("共"+productNum+"件商品,总计：<font color=\"red\">"+ totalPrice+ "</font>夺宝币"));

            }
        });
        mListView.setAdapter(mBuyCarAdapter);
        totalProductView = (TextView) view.findViewById(R.id.total_product_view);
        payView = (TextView) view.findViewById(R.id.pay_view);
        payView.setOnClickListener(this);
        titleNameView = (TextView)view.findViewById(R.id.title_name);
        titleNameView.setText("购物车");
        titleNameView.setVisibility(View.VISIBLE);
        view.findViewById(R.id.title_back).setVisibility(View.GONE);
        return view;
    }

    public void onResume() {
        super.onResume();
        loadData(true);
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

                int productNum = winningInfos.size();
                int totalPrice = 0;
                for (WinningInfo info:winningInfos) {
                    totalPrice += info.buyCount;
                }

                totalProductView.setText(Html.fromHtml("共"+productNum+"件商品,总计：<font color=\"red\">"+ totalPrice+ "</font>夺宝币"));
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
            case R.id.pay_view:
                Intent it = new Intent(this.getActivity(), PayActivity.class);
                startActivity(it);
                break;
        }

    }
}
