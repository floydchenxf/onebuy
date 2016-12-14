package com.yyg365.interestbar.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.android.volley.toolbox.ImageLoader;
import com.yyg365.interestbar.aync.ApiCallback;
import com.yyg365.interestbar.biz.manager.LoginManager;
import com.yyg365.interestbar.biz.manager.ProductManager;
import com.yyg365.interestbar.biz.vo.json.InviteFriendRecordVO;
import com.yyg365.interestbar.biz.vo.json.LuckRecordVO;
import com.yyg365.interestbar.biz.vo.json.ProductLssueWithWinnerVO;
import com.yyg365.interestbar.ui.R;
import com.yyg365.interestbar.ui.adapter.InviteRecordAdapter;
import com.yyg365.interestbar.ui.adapter.LuckRecordAdapter;
import com.yyg365.interestbar.ui.loading.DataLoadingView;
import com.yyg365.interestbar.ui.loading.DefaultDataLoadingView;
import com.yyg365.pullrefresh.widget.PullToRefreshBase;
import com.yyg365.pullrefresh.widget.PullToRefreshListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenxiaofeng on 16/9/30.
 */
public class InviteFriendRecordFragment extends Fragment {

    private static int PAGE_SIZE = 12;
    private static final String USER_ID = "USER_ID";
    private Long userId;
    private PullToRefreshListView mPullToRefreshListView;
    private int pageNo = 1;
    private boolean needClear;
    private boolean isFirst;
    private ListView mListView;
    private DataLoadingView dataLoadingView;
    private InviteRecordAdapter inviteRecordRecordAdapter;

    public InviteFriendRecordFragment() {
    }

    public static InviteFriendRecordFragment newInstance(Long userId) {
        InviteFriendRecordFragment fragment = new InviteFriendRecordFragment();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_common_record, container, false);
        dataLoadingView = new DefaultDataLoadingView();
        dataLoadingView.initView(view, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pageNo = 1;
                needClear = true;
                isFirst = true;
                loadData();
            }
        });

        mPullToRefreshListView = (PullToRefreshListView) view.findViewById(R.id.common_list);
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
        mListView = mPullToRefreshListView.getRefreshableView();
        inviteRecordRecordAdapter = new InviteRecordAdapter(getActivity(), new ArrayList<InviteFriendRecordVO>());
        mListView.setAdapter(inviteRecordRecordAdapter);
        View emptyView = View.inflate(getActivity(), R.layout.empty_item, null);
        mPullToRefreshListView.setEmptyView(emptyView);
        loadData();
        return view;
    }

    private void loadData() {

        if (isFirst) {
            dataLoadingView.startLoading();
        }
        LoginManager.getMyInvite(userId, pageNo, PAGE_SIZE).startUI(new ApiCallback<List<InviteFriendRecordVO>>() {
            @Override
            public void onError(int code, String errorInfo) {
                if (isFirst) {
                    dataLoadingView.loadFail();
                }
            }

            @Override
            public void onSuccess(List<InviteFriendRecordVO> list) {
                if (isFirst) {
                    dataLoadingView.loadSuccess();
                }
                inviteRecordRecordAdapter.addAll(list, needClear);
            }

            @Override
            public void onProgress(int progress) {

            }
        });
    }
}
