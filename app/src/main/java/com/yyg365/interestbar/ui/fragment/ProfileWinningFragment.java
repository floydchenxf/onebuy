package com.yyg365.interestbar.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.yyg365.interestbar.aync.ApiCallback;
import com.yyg365.interestbar.biz.manager.LoginManager;
import com.yyg365.interestbar.biz.manager.ProductManager;
import com.yyg365.interestbar.biz.vo.model.WinningInfo;
import com.yyg365.interestbar.ui.ImageLoaderFactory;
import com.yyg365.interestbar.ui.R;
import com.yyg365.interestbar.ui.activity.WinningDetailActivity;
import com.yyg365.interestbar.ui.adapter.JoinedNumAdapter;
import com.yyg365.interestbar.ui.adapter.WinningRecordAdapter;
import com.yyg365.interestbar.ui.loading.DataLoadingView;
import com.yyg365.interestbar.ui.loading.DefaultDataLoadingView;
import com.yyg365.interestbar.view.LeftDownPopupWindow;
import com.yyg365.pullrefresh.widget.PullToRefreshBase;
import com.yyg365.pullrefresh.widget.PullToRefreshListView;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProfileWinningFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProfileWinningFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileWinningFragment extends Fragment implements View.OnClickListener {
    private static final String USER_ID = "USER_ID";

    private Long userId;

    private DataLoadingView dataLoadingView;
    private PullToRefreshListView mPullToRefreshListView;
    private ImageLoader mImageLoader;
    private ListView mListView;
    private WinningRecordAdapter adapter;
    private int pageNo = 1;
    private int PAGE_SIZE = 10;
    private boolean needClear = false;
    private boolean isFirst = true;
    private LeftDownPopupWindow joinedPopupWindow;

    private TextView popProductCodeView;
    private TextView popProductTitleView;
    private TextView popJoinedCountView;
    private ListView popJoinNumListView;
    private JoinedNumAdapter joinedNumAdapter;


    private OnFragmentInteractionListener mListener;

    public ProfileWinningFragment() {
    }

    public static ProfileWinningFragment newInstance(Long userId) {
        ProfileWinningFragment fragment = new ProfileWinningFragment();
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
        isFirst = true;



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.fragment_profile_winning, container, false);

        dataLoadingView = new DefaultDataLoadingView();
        dataLoadingView.initView(v, this);
        mImageLoader = ImageLoaderFactory.createImageLoader();

        mPullToRefreshListView = (PullToRefreshListView) v.findViewById(R.id.common_list);
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

        View emptyView = View.inflate(getActivity(), R.layout.empty_item, null);
        mPullToRefreshListView.setEmptyView(emptyView);


        mListView = mPullToRefreshListView.getRefreshableView();
        adapter = new WinningRecordAdapter(getActivity(), mImageLoader, new ArrayList<WinningInfo>(), false);
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                WinningInfo info = adapter.getItem(position-1);
                Intent it = new Intent(getActivity(), WinningDetailActivity.class);
                it.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                if (info.status == WinningInfo.STATUS_CHOOSE ) {
                    it.putExtra(WinningDetailActivity.LASTEST, true);
                } else {
                    it.putExtra(WinningDetailActivity.LASTEST, false);
                }
                it.putExtra(WinningDetailActivity.PRODUCT_ID, info.productId);
                it.putExtra(WinningDetailActivity.LSSUE_ID, info.id);
                startActivity(it);
            }
        });

        adapter.setViewJoinNumberClickListener(new WinningRecordAdapter.ViewJoinNumberClickListener() {
            @Override
            public void onClick(WinningInfo winningInfo) {
                popProductCodeView.setText("第" + winningInfo.code + "期");
                popProductTitleView.setText(winningInfo.getTitle());
                int num = winningInfo.myPrizeCodes == null ? 0 : winningInfo.myPrizeCodes.size();
                popJoinedCountView.setText(Html.fromHtml("<font color=\"red\">" + num + "</font>人次"));
                List<String> list = winningInfo.myPrizeCodes;
                joinedNumAdapter.addAll(list, true);
                joinedPopupWindow.showPopUpWindow();
            }
        });

        joinedPopupWindow = new LeftDownPopupWindow(getActivity());
        joinedPopupWindow.initView(R.layout.pop_join_num, new LeftDownPopupWindow.ViewInit() {
            @Override
            public void initView(View v) {
                popProductCodeView = (TextView) v.findViewById(R.id.pop_product_code_view);
                popProductTitleView = (TextView) v.findViewById(R.id.pop_product_title_view);
                popJoinedCountView = (TextView) v.findViewById(R.id.pop_joined_count_view);
                popJoinNumListView = (ListView) v.findViewById(R.id.pop_joined_num_listview);
                joinedNumAdapter = new JoinedNumAdapter(getActivity(), new ArrayList<String>());
                popJoinNumListView.setAdapter(joinedNumAdapter);
            }
        });


        loadData();
        return v;
    }

    private void loadData() {

        if (isFirst) {
            dataLoadingView.startLoading();
        }

        ProductManager.getPrizeHistory(userId, PAGE_SIZE, pageNo, 3).startUI(new ApiCallback<List<WinningInfo>>() {
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

                adapter.addAll(winningInfos, needClear);
                pageNo++;
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
