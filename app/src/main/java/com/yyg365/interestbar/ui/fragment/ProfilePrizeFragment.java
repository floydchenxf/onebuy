package com.yyg365.interestbar.ui.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.yyg365.interestbar.aync.ApiCallback;
import com.yyg365.interestbar.biz.manager.LoginManager;
import com.yyg365.interestbar.biz.manager.ProductManager;
import com.yyg365.interestbar.biz.vo.json.LuckRecordVO;
import com.yyg365.interestbar.biz.vo.json.ProductLssueVO;
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

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProfilePrizeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProfilePrizeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfilePrizeFragment extends Fragment implements View.OnClickListener {
    public static final String LUCK_TYPE = "LUCK_TYPE";
    private static final String USER_ID = "USER_ID";
    private static int PAGE_SIZE = 12;
    private Long userId;
    private OnFragmentInteractionListener mListener;
    private PullToRefreshListView mPullToRefreshListView;
    private int pageNo = 1;
    private boolean needClear;
    private boolean isFirst;
    private ListView mListView;
    private DataLoadingView dataLoadingView;
    private LuckRecordAdapter luckRecordAdapter;
    private ImageLoader mImageLoader;

    public ProfilePrizeFragment() {
    }

    public static ProfilePrizeFragment newInstance(Long userId) {
        ProfilePrizeFragment fragment = new ProfilePrizeFragment();
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

        isFirst = true;
        pageNo = 1;
        mImageLoader = ImageLoaderFactory.createImageLoader();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_common_record, container, false);

        dataLoadingView = new DefaultDataLoadingView();
        dataLoadingView.initView(view, this);

        mPullToRefreshListView = (PullToRefreshListView) view.findViewById(R.id.common_list);
        mPullToRefreshListView.setMode(PullToRefreshBase.Mode.PULL_UP_TO_REFRESH);
        mPullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2() {
            @Override
            public void onPullDownToRefresh() {
//                pageNo = 1;
//                needClear = true;
//                isFirst = true;
//                loadData();
//                mPullToRefreshListView.onRefreshComplete(false, true);
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
        mListView = mPullToRefreshListView.getRefreshableView();
        luckRecordAdapter = new LuckRecordAdapter(getActivity(), new ArrayList<ProductLssueWithWinnerVO>(), false, mImageLoader);
        mListView.setAdapter(luckRecordAdapter);
        View emptyView = View.inflate(getActivity(), R.layout.empty_item, null);
        mPullToRefreshListView.setEmptyView(emptyView);
        loadData();

        return view;
    }

    private void loadData() {
        if (isFirst) {
            dataLoadingView.startLoading();
        }
        ProductManager.fetchMyLuckRecords(userId, 1, pageNo, PAGE_SIZE).startUI(new ApiCallback<LuckRecordVO>() {
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
                List<ProductLssueWithWinnerVO> aa = recordVO.proLssueList;
                luckRecordAdapter.addAll(aa, needClear);
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
