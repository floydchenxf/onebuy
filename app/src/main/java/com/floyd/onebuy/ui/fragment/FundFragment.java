package com.floyd.onebuy.ui.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.android.volley.toolbox.ImageLoader;
import com.floyd.onebuy.aync.ApiCallback;
import com.floyd.onebuy.biz.manager.ProductManager;
import com.floyd.onebuy.biz.vo.commonweal.CommonwealVO;
import com.floyd.onebuy.biz.vo.fund.FundJsonVO;
import com.floyd.onebuy.biz.vo.json.ProductLssueVO;
import com.floyd.onebuy.ui.ImageLoaderFactory;
import com.floyd.onebuy.ui.R;
import com.floyd.onebuy.ui.adapter.CommonwealAdapter;
import com.floyd.onebuy.ui.adapter.FundAdapter;
import com.floyd.onebuy.ui.loading.DataLoadingView;
import com.floyd.onebuy.ui.loading.DefaultDataLoadingView;
import com.floyd.pullrefresh.widget.PullToRefreshBase;
import com.floyd.pullrefresh.widget.PullToRefreshListView;

import java.util.ArrayList;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link FundFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FundFragment extends CommonwealBaseFragment implements View.OnClickListener {

    private static final String TAG = "FundFragment";

    private static final int PAGE_SIZE = 18;
    private PullToRefreshListView mPullToRefreshListView;

    private ListView mListView;
    private DataLoadingView dataLoadingView;
    private ImageLoader mImageLoader;
    private boolean needClear = false;
    private int pageNo = 1;
    private FundAdapter fundAdapter;

    public FundFragment() {
    }

    public static FundFragment newInstance(String param1, String param2) {
        FundFragment fragment = new FundFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mImageLoader = ImageLoaderFactory.createImageLoader();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fund, container, false);
        dataLoadingView = new DefaultDataLoadingView();
        dataLoadingView.initView(view, this);
        mPullToRefreshListView = (PullToRefreshListView) view.findViewById(R.id.common_list);
        mPullToRefreshListView.setMode(PullToRefreshBase.Mode.BOTH);
        mPullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2() {
            @Override
            public void onPullDownToRefresh() {
                needClear = true;
                pageNo = 1;
                loadData(true);
                mPullToRefreshListView.onRefreshComplete(false, true);
            }

            @Override
            public void onPullUpToRefresh() {
                needClear = false;
                pageNo++;
                loadData(false);
                mPullToRefreshListView.onRefreshComplete(false, true);
            }
        });

        View emptyView = inflater.inflate(R.layout.empty_item, container, false);
        mPullToRefreshListView.setEmptyView(emptyView);
        mListView = mPullToRefreshListView.getRefreshableView();
        fundAdapter = new FundAdapter(getActivity(), new ArrayList<ProductLssueVO>(), mImageLoader);
        mListView.setAdapter(fundAdapter);

        loadData(true);
        return view;

    }

    private void loadData(final boolean isFirst) {
        if (isFirst) {
            dataLoadingView.loadSuccess();
        }
        ProductManager.fetchFundData(PAGE_SIZE, pageNo).startUI(new ApiCallback<FundJsonVO>() {
            @Override
            public void onError(int code, String errorInfo) {
                if (isFirst) {
                    dataLoadingView.loadFail();
                }
            }

            @Override
            public void onSuccess(FundJsonVO fundJsonVO) {
                if (isFirst) {
                    dataLoadingView.loadSuccess();
                }

                fundAdapter.addAll(fundJsonVO.ProductLssueList, needClear);
            }

            @Override
            public void onProgress(int progress) {

            }
        });
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    Map<String, Object> getSwitchData() {
        return null;
    }
}
