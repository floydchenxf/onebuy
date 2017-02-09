package com.yyg365.interestbar.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.yyg365.interestbar.aync.ApiCallback;
import com.yyg365.interestbar.biz.constants.APIConstants;
import com.yyg365.interestbar.biz.manager.LoginManager;
import com.yyg365.interestbar.biz.vo.json.CommissionItemVO;
import com.yyg365.interestbar.biz.vo.json.MsgItemVO;
import com.yyg365.interestbar.ui.R;
import com.yyg365.interestbar.ui.activity.H5Activity;
import com.yyg365.interestbar.ui.adapter.MsgBoxAdapter;
import com.yyg365.interestbar.ui.loading.DataLoadingView;
import com.yyg365.interestbar.ui.loading.DefaultDataLoadingView;
import com.yyg365.pullrefresh.widget.PullToRefreshBase;
import com.yyg365.pullrefresh.widget.PullToRefreshListView;

import java.util.ArrayList;
import java.util.List;

public class MsgBoxFragment extends Fragment {
    private static final String USER_ID = "USER_ID";
    private static final String TYPE_ID = "TYPE_ID";
    private static final int PAGE_SIZE = 20;
    protected Long userId;
    protected int typeId;

    protected DataLoadingView dataLoadingView;
    protected PullToRefreshListView mPullToRefreshListView;

    private ListView mListView;
    protected int pageNo;
    protected boolean isFirst;
    protected boolean needClear;

    private MsgBoxAdapter adapter;

    public MsgBoxFragment() {
    }

    public static MsgBoxFragment newInstance(Long userId, int typeId) {
        MsgBoxFragment fragment = new MsgBoxFragment();
        Bundle args = new Bundle();
        args.putLong(USER_ID, userId);
        args.putInt(TYPE_ID, typeId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userId = getArguments().getLong(USER_ID, 0l);
            typeId = getArguments().getInt(TYPE_ID, 0);
        }

        isFirst = true;
        pageNo = 1;
        needClear = true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_common, container, false);

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

        adapter = new MsgBoxAdapter(getActivity(), new ArrayList<MsgItemVO>());

        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MsgItemVO vo  = adapter.getItem(position - 1);
                Intent detailIntent = new Intent(getActivity(), H5Activity.class);
                H5Activity.H5Data h5Data = new H5Activity.H5Data();
                h5Data.dataType = H5Activity.H5Data.H5_DATA_TYPE_URL;
                h5Data.data = APIConstants.HOST + "app/messageinfo.aspx?id=" + vo.ID + "&cid=" + vo.ClientID;
                h5Data.showProcess = true;
                h5Data.showNav = true;
                h5Data.title = "消息详情";
                detailIntent.putExtra(H5Activity.H5Data.H5_DATA, h5Data);
                startActivity(detailIntent);
            }
        });
        loadData();
        return v;
    }

    private void loadData() {
        if (isFirst) {
            dataLoadingView.startLoading();
        }

        LoginManager.fetchUserMsgs(userId, typeId, pageNo, PAGE_SIZE).startUI(new ApiCallback<List<MsgItemVO>>() {
            @Override
            public void onError(int code, String errorInfo) {
                if (isFirst) {
                    dataLoadingView.loadFail();
                }
            }

            @Override
            public void onSuccess(List<MsgItemVO> msgItemVOs) {
                if (isFirst) {
                    dataLoadingView.loadSuccess();
                }

                if (msgItemVOs == null) {
                    msgItemVOs = new ArrayList<MsgItemVO>();
                }
                adapter.addAll(msgItemVOs, needClear);
            }

            @Override
            public void onProgress(int progress) {

            }
        });
    }

}
