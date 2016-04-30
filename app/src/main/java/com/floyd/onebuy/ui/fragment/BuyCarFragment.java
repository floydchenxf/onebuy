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
import com.floyd.onebuy.aync.ApiCallback;
import com.floyd.onebuy.biz.manager.CarManager;
import com.floyd.onebuy.biz.manager.DBManager;
import com.floyd.onebuy.biz.manager.LoginManager;
import com.floyd.onebuy.biz.vo.json.UserVO;
import com.floyd.onebuy.biz.vo.model.WinningInfo;
import com.floyd.onebuy.ui.ImageLoaderFactory;
import com.floyd.onebuy.ui.R;
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

    private static final int PAGE_SIZE = 10;
    private DataLoadingView dataLoadingView;
    private PullToRefreshListView mPullToRefreshListView;
    private ListView mListView;
    private ImageLoader mImageLoader;
    private BuyCarAdapter mBuyCarAdapter;
    private int pageNo;
    private boolean needClear;
    private TextView titleNameView;
    private TextView editView;

    private boolean isEdit = false;

    private TextView totalProductView;//总计view
    private TextView payView;
    private View payLayout;

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
        mPullToRefreshListView.setMode(PullToRefreshBase.Mode.BOTH);
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
                loadData(false);
                mPullToRefreshListView.onRefreshComplete(false, true);
            }
        });
        mListView = mPullToRefreshListView.getRefreshableView();
        mBuyCarAdapter = new BuyCarAdapter(this.getActivity(), null, mImageLoader, new BuyCarAdapter.BuyClickListener() {
            @Override
            public void onClick(View v, final long lssueId, final int buyNumber) {
                long userId = LoginManager.getLoginInfo(BuyCarFragment.this.getActivity()).ID;
                DBManager.updateBuyCarNumber(BuyCarFragment.this.getActivity(), userId, lssueId, buyNumber);
                int productNum = mBuyCarAdapter.getRecords().size();
                int totalPrice = 0;
                for (WinningInfo info : mBuyCarAdapter.getRecords()) {
                    totalPrice += info.buyCount;
                }

                totalProductView.setText(Html.fromHtml("共" + productNum + "件商品,总计：<font color=\"red\">" + totalPrice + "</font>夺宝币"));
            }
        });
        mListView.setAdapter(mBuyCarAdapter);
        payLayout = view.findViewById(R.id.pay_layout);
        payLayout.setVisibility(View.GONE);
        totalProductView = (TextView) view.findViewById(R.id.total_product_view);
        payView = (TextView) view.findViewById(R.id.pay_view);
        payView.setOnClickListener(this);
        titleNameView = (TextView) view.findViewById(R.id.title_name);
        titleNameView.setText("购物车");
        titleNameView.setVisibility(View.VISIBLE);

        editView = (TextView) view.findViewById(R.id.right);
        editView.setVisibility(View.VISIBLE);
        editView.setOnClickListener(this);

        if (isEdit) {
            editView.setText("完成");
        } else {
            editView.setText("编辑");
        }

        mBuyCarAdapter.showRadiio(isEdit);

        view.findViewById(R.id.title_back).setVisibility(View.GONE);

        return view;
    }

    public void onResume() {
        super.onResume();
        loadData(true);
    }

    private void loadData(final boolean isFirst) {
        UserVO userVO = LoginManager.getLoginInfo(this.getActivity());
        if (userVO == null) {
            return;
        }
        if (isFirst) {
            dataLoadingView.startLoading();
        }

        long userId = userVO.ID;
        CarManager.fetchBuyCarList(getActivity(), userId, pageNo, PAGE_SIZE).startUI(new ApiCallback<List<WinningInfo>>() {
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
                for (WinningInfo info : winningInfos) {
                    totalPrice += info.buyCount;
                }

                payLayout.setVisibility(View.VISIBLE);
                totalProductView.setText(Html.fromHtml("共" + productNum + "件商品,总计：<font color=\"red\">" + totalPrice + "</font>夺宝币"));
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
            case R.id.right:
                isEdit = !isEdit;
                mBuyCarAdapter.showRadiio(isEdit);
                break;
        }

    }
}
