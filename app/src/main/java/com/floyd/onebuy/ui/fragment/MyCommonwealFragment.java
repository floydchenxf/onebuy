package com.floyd.onebuy.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.floyd.onebuy.aync.ApiCallback;
import com.floyd.onebuy.aync.AsyncJob;
import com.floyd.onebuy.biz.manager.CommonwealManager;
import com.floyd.onebuy.biz.vo.commonweal.CommonwealHomeVO;
import com.floyd.onebuy.biz.vo.commonweal.CommonwealVO;
import com.floyd.onebuy.ui.ImageLoaderFactory;
import com.floyd.onebuy.ui.R;
import com.floyd.onebuy.ui.activity.CommonwealDetailActivity;
import com.floyd.onebuy.ui.adapter.CommonwealAdapter;
import com.floyd.onebuy.ui.loading.DataLoadingView;
import com.floyd.onebuy.ui.loading.DefaultDataLoadingView;
import com.floyd.pullrefresh.widget.PullToRefreshBase;
import com.floyd.pullrefresh.widget.PullToRefreshListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenxiaofeng on 16/10/5.
 */
public class MyCommonwealFragment extends Fragment implements View.OnClickListener {
    public static final String USER_ID = "USER_ID";

    private static final int PAGE_SIZE = 20;
    private Long userId;
    private int pageNo;
    private boolean needClear;
    private ImageLoader mImageLoader;

    private PullToRefreshListView mPullToRefreshListView;
    private ListView mListView;
    private CommonwealAdapter mAdapter;
    private DataLoadingView dataLoadingView;

    public MyCommonwealFragment() {
    }


    public static MyCommonwealFragment newInstance(Long userId) {
        MyCommonwealFragment fragment = new MyCommonwealFragment();
        Bundle args = new Bundle();
        args.putLong(USER_ID, userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userId = getArguments().getLong(USER_ID, 0l);
        }

        pageNo = 1;
        needClear = true;
        mImageLoader = ImageLoaderFactory.createImageLoader();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_commonweal, container, false);
        dataLoadingView = new DefaultDataLoadingView();
        dataLoadingView.initView(view, this);
        mPullToRefreshListView = (PullToRefreshListView) view.findViewById(R.id.common_list);
        mPullToRefreshListView.setMode(PullToRefreshBase.Mode.PULL_UP_TO_REFRESH);
        mPullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2() {
            @Override
            public void onPullDownToRefresh() {
            }

            @Override
            public void onPullUpToRefresh() {
                needClear = false;
                pageNo++;
                loadPageData();
                mPullToRefreshListView.onRefreshComplete(false, true);
            }
        });

        View emptyView = inflater.inflate(R.layout.empty_item, container, false);
        mPullToRefreshListView.setEmptyView(emptyView);
        mListView = mPullToRefreshListView.getRefreshableView();
        mAdapter = new CommonwealAdapter(getActivity(), new ArrayList<CommonwealVO>(), mImageLoader);
        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position < 1 || position > mAdapter.getRecords().size()) {
                    return;
                }
                CommonwealVO vo = mAdapter.getItem(position - 1);
                Intent it = new Intent(getActivity(), CommonwealDetailActivity.class);
                it.putExtra(CommonwealDetailActivity.PRODUCT_ID, vo.FoundationID);
                startActivity(it);
            }
        });
        loadData(true);
        return view;
    }

    private void loadData(final boolean isFirst) {
        if (isFirst) {
            dataLoadingView.startLoading();
        }

        CommonwealManager.fetchMyFirstCommonweal(userId, pageNo, PAGE_SIZE).startUI(new ApiCallback<CommonwealHomeVO>() {
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

                List<CommonwealVO> commonwealVO = commonwealHomeVO.FoundationList;
                mAdapter.addAll(commonwealVO, needClear);
            }

            @Override
            public void onProgress(int progress) {

            }
        });
    }

    private void loadPageData() {
        CommonwealManager.fetchMyCommonwealList(userId, pageNo, PAGE_SIZE).startUI(new ApiCallback<List<CommonwealVO>>() {
            @Override
            public void onError(int code, String errorInfo) {
                Toast.makeText(getActivity(), errorInfo, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(List<CommonwealVO> commonwealVOs) {
                mAdapter.addAll(commonwealVOs, needClear);
            }

            @Override
            public void onProgress(int progress) {

            }
        });
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
