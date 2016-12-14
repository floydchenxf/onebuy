package com.yyg365.interestbar.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.toolbox.ImageLoader;
import com.yyg365.interestbar.aync.ApiCallback;
import com.yyg365.interestbar.biz.manager.ProductManager;
import com.yyg365.interestbar.biz.vo.json.LuckRecordVO;
import com.yyg365.interestbar.biz.vo.json.ProductLssueWithWinnerVO;
import com.yyg365.interestbar.ui.ImageLoaderFactory;
import com.yyg365.interestbar.ui.R;
import com.yyg365.interestbar.ui.adapter.LuckRecordAdapter;
import com.yyg365.interestbar.ui.loading.DataLoadingView;
import com.yyg365.interestbar.ui.loading.DefaultDataLoadingView;
import com.yyg365.pullrefresh.widget.PullToRefreshBase;
import com.yyg365.pullrefresh.widget.PullToRefreshListView;

import java.util.ArrayList;
import java.util.List;

public class MyFundFragment extends Fragment {
    private static final String USER_ID = "USER_ID";

    private Long userId;

    public static int PAGE_SIZE = 12;
    private PullToRefreshListView mPullToRefreshListView;
    private int pageNo = 1;
    private boolean needClear;
    private boolean isFirst;
    private DataLoadingView dataLoadingView;
    private LuckRecordAdapter luckRecordAdapter;
    private ImageLoader mImageLoader;

    public MyFundFragment() {
    }

    public static MyFundFragment newInstance(Long userId) {
        MyFundFragment fragment = new MyFundFragment();
        Bundle args = new Bundle();
        args.putLong(USER_ID, userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userId = getArguments().getLong(USER_ID);
        }

        pageNo = 1;
        isFirst = true;
        needClear = true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_my_fund, container, false);

        mImageLoader = ImageLoaderFactory.createImageLoader();

        dataLoadingView = new DefaultDataLoadingView();
        dataLoadingView.initView(v, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pageNo = 1;
                needClear = true;
                isFirst = true;
                loadData();
            }
        });

        mPullToRefreshListView = (PullToRefreshListView) v.findViewById(R.id.common_list);

        View emptyView = View.inflate(getActivity(), R.layout.empty_item, null);
        mPullToRefreshListView.setEmptyView(emptyView);
        mPullToRefreshListView.setMode(PullToRefreshBase.Mode.PULL_UP_TO_REFRESH);
        mPullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2() {
            @Override
            public void onPullDownToRefresh() {
            }

            @Override
            public void onPullUpToRefresh() {
                pageNo++;
                needClear = false;
                isFirst = false;
                loadData();
                mPullToRefreshListView.onRefreshComplete(false, true);

            }
        });
        luckRecordAdapter = new LuckRecordAdapter(getActivity(), new ArrayList<ProductLssueWithWinnerVO>(), true, mImageLoader);
        mPullToRefreshListView.getRefreshableView().setAdapter(luckRecordAdapter);

        loadData();
        return v;
    }

    private void loadData() {
        if (isFirst) {
            dataLoadingView.startLoading();
        }
        ProductManager.fetchMyLuckRecords(userId, 3, pageNo, PAGE_SIZE).startUI(new ApiCallback<LuckRecordVO>() {
            @Override
            public void onError(int code, String errorInfo) {
                if (isFirst) {
                    dataLoadingView.loadFail();
                }
            }

            @Override
            public void onSuccess(LuckRecordVO recordVO) {
                if (isFirst) {
                    dataLoadingView.loadSuccess();
                }

                List<ProductLssueWithWinnerVO> productLssueWithWinnerVOs = recordVO.proLssueList;
                luckRecordAdapter.addAll(productLssueWithWinnerVOs, needClear);
            }

            @Override
            public void onProgress(int progress) {

            }
        });
    }


}
