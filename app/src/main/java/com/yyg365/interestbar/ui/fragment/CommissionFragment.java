package com.yyg365.interestbar.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.yyg365.interestbar.aync.ApiCallback;
import com.yyg365.interestbar.biz.manager.LoginManager;
import com.yyg365.interestbar.biz.vo.json.CommissionItemVO;
import com.yyg365.interestbar.biz.vo.json.CommissionVO;
import com.yyg365.interestbar.biz.vo.json.UserCommissionVO;
import com.yyg365.interestbar.ui.R;
import com.yyg365.interestbar.ui.adapter.CommissionAdapter;
import com.yyg365.interestbar.ui.loading.DataLoadingView;
import com.yyg365.interestbar.ui.loading.DefaultDataLoadingView;
import com.yyg365.pullrefresh.widget.PullToRefreshBase;
import com.yyg365.pullrefresh.widget.PullToRefreshListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommissionFragment extends Fragment {
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

    private ImageView levelView;
    private TextView levelNameView;
    private TextView commissionRateView;

    private CommissionAdapter adapter;

    private static final Map<Integer, Integer> iconMap = new HashMap<Integer, Integer>();

    static {
        iconMap.put(1, R.drawable.icon_commissionlevel_1);
        iconMap.put(2, R.drawable.icon_commissionlevel_2);
        iconMap.put(3, R.drawable.icon_commissionlevel_3);
        iconMap.put(4, R.drawable.icon_commissionlevel_4);
    }


    public CommissionFragment() {
    }

    public static CommissionFragment newInstance(Long userId, int typeId) {
        CommissionFragment fragment = new CommissionFragment();
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
        View v = inflater.inflate(R.layout.fragment_commission, container, false);

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

        initHeader(mListView);
        adapter = new CommissionAdapter(getActivity(), new ArrayList<CommissionItemVO>());

        mListView.setAdapter(adapter);
        loadData();
        return v;
    }

    private void initHeader(ListView mListView) {
        View header = View.inflate(getActivity(), R.layout.commission_head, null);
        levelNameView = (TextView) header.findViewById(R.id.level_name_view);
        levelView = (ImageView) header.findViewById(R.id.level_view);
        commissionRateView = (TextView) header.findViewById(R.id.commission_rate_view);
        mListView.addHeaderView(header);
    }

    private void loadData() {
        if (isFirst) {
            dataLoadingView.startLoading();
        }

        LoginManager.fetchUserCommission(userId, typeId, pageNo, PAGE_SIZE).startUI(new ApiCallback<CommissionVO>() {
            @Override
            public void onError(int code, String errorInfo) {
                if (isFirst) {
                    dataLoadingView.loadFail();
                }
            }

            @Override
            public void onSuccess(CommissionVO commissionVO) {
                if (isFirst) {
                    dataLoadingView.loadSuccess();
                }

                UserCommissionVO userCommissionVO = commissionVO.UserInfo;
                int level = userCommissionVO.CommissionLevel;
                Integer icon = iconMap.get(level);
                if (icon != null) {
                    levelView.setImageResource(icon);
                }

                StringBuilder sb = new StringBuilder(userCommissionVO.CommissionLevelName);
                sb.append("  (").append("提成:<font color=\"red\">").append(100 * userCommissionVO.CommissionRatio).append("%</font>)");
                levelNameView.setText(Html.fromHtml(sb.toString()));
                commissionRateView.setText(Html.fromHtml("佣金:<font color=\"red\">" + userCommissionVO.CommissionRatio + "</font>"));
                List<CommissionItemVO> list = commissionVO.list;
                if (list == null) {
                    list = new ArrayList<CommissionItemVO>();
                }
                adapter.addAll(list, needClear);
            }

            @Override
            public void onProgress(int progress) {

            }
        });

    }

}
