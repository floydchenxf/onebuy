package com.floyd.onebuy.ui.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.android.volley.toolbox.ImageLoader;
import com.floyd.onebuy.aync.ApiCallback;
import com.floyd.onebuy.biz.manager.ProductManager;
import com.floyd.onebuy.biz.vo.json.PrizeShowListVO;
import com.floyd.onebuy.biz.vo.json.PrizeShowVO;
import com.floyd.onebuy.ui.ImageLoaderFactory;
import com.floyd.onebuy.ui.R;
import com.floyd.onebuy.ui.adapter.ProfileShowShareAdapter;
import com.floyd.onebuy.ui.loading.DataLoadingView;
import com.floyd.onebuy.ui.loading.DefaultDataLoadingView;
import com.floyd.pullrefresh.widget.PullToRefreshBase;
import com.floyd.pullrefresh.widget.PullToRefreshListView;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProfileShowShareFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProfileShowShareFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileShowShareFragment extends Fragment implements View.OnClickListener {
    private static final String USER_ID = "USER_ID";
    private static final int PAGE_SIZE = 20;
    private Long userId;

    private OnFragmentInteractionListener mListener;

    private DataLoadingView dataLoadingView;
    private ImageLoader mImageLoader;
    private PullToRefreshListView mPullToRefreshListView;
    private ListView mListView;

    private int pageNo;
    private boolean isFirst;
    private boolean needClear;

    private ProfileShowShareAdapter adapter;


    public ProfileShowShareFragment() {
    }

    public static ProfileShowShareFragment newInstance(Long userId) {
        ProfileShowShareFragment fragment = new ProfileShowShareFragment();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile_show_share, container, false);

        dataLoadingView = new DefaultDataLoadingView();
        dataLoadingView.initView(v, this);
        mImageLoader = ImageLoaderFactory.createImageLoader();

        mPullToRefreshListView = (PullToRefreshListView) v.findViewById(R.id.common_list);
        mPullToRefreshListView.setMode(PullToRefreshBase.Mode.PULL_UP_TO_REFRESH);
        mPullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2() {
            @Override
            public void onPullDownToRefresh() {
                //do nothing;
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

        View emptyView = View.inflate(getActivity(), R.layout.empty_item, null);
        mPullToRefreshListView.setEmptyView(emptyView);
        mListView = mPullToRefreshListView.getRefreshableView();

        adapter = new ProfileShowShareAdapter(getActivity(), new ArrayList<PrizeShowVO>(), mImageLoader);
        mListView.setAdapter(adapter);
        loadData();
        return v;
    }

    private void loadData() {
        if (isFirst) {
            dataLoadingView.startLoading();
        }
        ProductManager.getMyPrizeShow(userId, -1, PAGE_SIZE, pageNo).startUI(new ApiCallback<PrizeShowListVO>() {
            @Override
            public void onError(int code, String errorInfo) {
                if (isFirst) {
                    dataLoadingView.loadFail();
                }
            }

            @Override
            public void onSuccess(PrizeShowListVO prizeShowListVO) {
                if (isFirst) {
                    dataLoadingView.loadSuccess();
                }
                List<PrizeShowVO> list = prizeShowListVO.ClientPrizeShowList;
                adapter.addAll(list, needClear);
            }

            @Override
            public void onProgress(int progress) {

            }
        });
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.act_ls_fail_layout:
                pageNo = 1;
                needClear = true;
                isFirst = true;
                loadData();
                break;
        }
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
