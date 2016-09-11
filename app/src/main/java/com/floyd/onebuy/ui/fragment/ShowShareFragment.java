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
import com.floyd.onebuy.biz.manager.ShowShareManager;
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
 * {@link ShowShareFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ShowShareFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ShowShareFragment extends Fragment {
    private static final String USER_ID = "USER_ID";
    private static final String IS_SELF = "IS_SELF";
    private static final String TYPE_ID = "TYPE_ID";
    private static final String IS_PRODUCT = "is_product";
    private static final int PAGE_SIZE = 20;
    protected Long userId;
    protected int typeId;

    private OnFragmentInteractionListener mListener;
    protected DataLoadingView dataLoadingView;
    protected ImageLoader mImageLoader;
    protected PullToRefreshListView mPullToRefreshListView;

    private ListView mListView;
    protected int pageNo;
    protected boolean isFirst;
    protected boolean needClear;
    protected boolean isProduct;

    private ProfileShowShareAdapter adapter;


    public ShowShareFragment() {
    }

    public static ShowShareFragment newInstance(Long userId, int typeId, boolean isProduct) {
        ShowShareFragment fragment = new ShowShareFragment();
        Bundle args = new Bundle();
        args.putLong(USER_ID, userId);
        args.putInt(TYPE_ID, typeId);
        args.putBoolean(IS_PRODUCT, isProduct);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userId = getArguments().getLong(USER_ID, 0l);
            typeId = getArguments().getInt(TYPE_ID, 0);
            isProduct = getArguments().getBoolean(IS_PRODUCT, false);
        }

        isFirst = true;
        pageNo = 1;
        needClear = true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile_show_share, container, false);

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

        ShowShareManager.fetchPrizeShowList(getActivity(), userId, typeId, PAGE_SIZE, pageNo, isProduct).startUI(new ApiCallback<PrizeShowListVO>() {
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
                List<PrizeShowVO> list = prizeShowListVO.PrizeShowList;
                if (list == null) {
                    list = new ArrayList<PrizeShowVO>();
                }
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

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
