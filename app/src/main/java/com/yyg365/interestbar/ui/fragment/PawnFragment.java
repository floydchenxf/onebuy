package com.yyg365.interestbar.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.yyg365.interestbar.aync.ApiCallback;
import com.yyg365.interestbar.biz.manager.PawnManager;
import com.yyg365.interestbar.biz.vo.json.DangPuItemVO;
import com.yyg365.interestbar.ui.ImageLoaderFactory;
import com.yyg365.interestbar.ui.R;
import com.yyg365.interestbar.ui.adapter.PawnAdapter;
import com.yyg365.interestbar.ui.adapter.TypeAdapter;
import com.yyg365.interestbar.ui.loading.DataLoadingView;
import com.yyg365.interestbar.ui.loading.DefaultDataLoadingView;
import com.yyg365.pullrefresh.widget.PullToRefreshBase;
import com.yyg365.pullrefresh.widget.PullToRefreshListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenxiaofeng on 17/1/4.
 */
public class PawnFragment extends BackHandledFragment {


    private static final int PAGE_SIZE = 20;

    private ImageLoader mImageLoader;

    private DataLoadingView dataLoadingView;

    private PullToRefreshListView mPullToRefreshListView;

    private ListView mRefreshListView;

    private PawnAdapter mAdapter;

    private int pageNo;

    public static PawnFragment newInstance(String param1, String param2) {
        PawnFragment fragment = new PawnFragment();
        return fragment;
    }

    public PawnFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mImageLoader = ImageLoaderFactory.createImageLoader();
        pageNo = 1;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.activity_common, container, false);
        view.findViewById(R.id.title_back).setVisibility(View.GONE);
        TextView titleView = (TextView) view.findViewById(R.id.title_name);
        titleView.setText("典當");
        titleView.setVisibility(View.VISIBLE);

        dataLoadingView = new DefaultDataLoadingView();
        dataLoadingView.initView(view, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pageNo = 1;
                loadData(true);
            }
        });

        mPullToRefreshListView = (PullToRefreshListView) view.findViewById(R.id.common_list);
        mRefreshListView = mPullToRefreshListView.getRefreshableView();

        mAdapter = new PawnAdapter(getActivity(), new ArrayList<DangPuItemVO>(), mImageLoader);
        mRefreshListView.setAdapter(mAdapter);

        mPullToRefreshListView.setMode(PullToRefreshBase.Mode.PULL_UP_TO_REFRESH);
        mPullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2() {
            @Override
            public void onPullDownToRefresh() {
            }

            @Override
            public void onPullUpToRefresh() {
                loadData(false);
                mPullToRefreshListView.onRefreshComplete(false, true);
            }
        });
        View emptyView = inflater.inflate(R.layout.empty_item, container, false);
        mPullToRefreshListView.setEmptyView(emptyView);

        loadData(true);

        return view;
    }

    private void loadData(final boolean isFirst) {
        if (isFirst) {
            dataLoadingView.startLoading();
        }

        PawnManager.fetchPawnProducts(0l, pageNo, PAGE_SIZE).startUI(new ApiCallback<List<DangPuItemVO>>() {
            @Override
            public void onError(int code, String errorInfo) {
                if (isFirst) {
                    dataLoadingView.loadFail();
                }
            }

            @Override
            public void onSuccess(List<DangPuItemVO> dangPuItemVOs) {
                if (isFirst) {
                    dataLoadingView.loadSuccess();
                }
                pageNo++;
                mAdapter.addAll(dangPuItemVOs, isFirst);
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

}