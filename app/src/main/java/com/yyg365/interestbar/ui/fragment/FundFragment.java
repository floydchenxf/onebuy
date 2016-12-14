package com.yyg365.interestbar.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.yyg365.interestbar.aync.ApiCallback;
import com.yyg365.interestbar.biz.manager.ProductManager;
import com.yyg365.interestbar.biz.vo.commonweal.TypeVO;
import com.yyg365.interestbar.biz.vo.fund.FundJsonVO;
import com.yyg365.interestbar.biz.vo.json.ProductLssueVO;
import com.yyg365.interestbar.event.FundTypeEvent;
import com.yyg365.interestbar.ui.ImageLoaderFactory;
import com.yyg365.interestbar.ui.R;
import com.yyg365.interestbar.ui.activity.WinningDetailActivity;
import com.yyg365.interestbar.ui.adapter.FundAdapter;
import com.yyg365.interestbar.ui.loading.DataLoadingView;
import com.yyg365.interestbar.ui.loading.DefaultDataLoadingView;
import com.yyg365.pullrefresh.widget.PullToRefreshBase;
import com.yyg365.pullrefresh.widget.PullToRefreshListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link FundFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FundFragment extends CommonwealBaseFragment implements View.OnClickListener {

    private static final String TAG = "FundFragment";

    private static final int PAGE_SIZE = 18;
    private PullToRefreshListView mPullToRefreshListView;

    private ListView mListView;
    private DataLoadingView dataLoadingView;
    private ImageLoader mImageLoader;
    private boolean needClear = false;
    private int pageNo = 1;
    private FundAdapter fundAdapter;
    private TextView fundFeeView;

    private List<TypeVO> typeList;
    private long typeId;

    private Long userId;
    private boolean isPersion;

    public FundFragment() {
    }

    public static FundFragment newInstance(Long userId) {
        FundFragment fragment = new FundFragment();
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

        isPersion = userId != null&&userId > 0;
        mImageLoader = ImageLoaderFactory.createImageLoader();
        EventBus.getDefault().register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fund, container, false);
        dataLoadingView = new DefaultDataLoadingView();
        dataLoadingView.initView(view, this);
        mPullToRefreshListView = (PullToRefreshListView) view.findViewById(R.id.common_list);
        mPullToRefreshListView.setMode(PullToRefreshBase.Mode.BOTH);
        mPullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2() {
            @Override
            public void onPullDownToRefresh() {
                needClear = true;
                pageNo = 1;
                typeId = 0;
                loadData(true);
                mPullToRefreshListView.onRefreshComplete(false, true);
            }

            @Override
            public void onPullUpToRefresh() {
                needClear = false;
                pageNo++;
                loadData(false);
                mPullToRefreshListView.onRefreshComplete(false, true);
            }
        });

        View emptyView = inflater.inflate(R.layout.empty_item, container, false);
        mPullToRefreshListView.setEmptyView(emptyView);
        mListView = mPullToRefreshListView.getRefreshableView();
        initHead();
        fundAdapter = new FundAdapter(getActivity(), new ArrayList<ProductLssueVO>(), mImageLoader);
        mListView.setAdapter(fundAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position < 2 || position - 1 > fundAdapter.getRecords().size()) {
                    return;
                }
                ProductLssueVO lssueVo = fundAdapter.getItem(position - 2);
                Intent it = new Intent(getActivity(), WinningDetailActivity.class);
                it.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                it.putExtra(WinningDetailActivity.LASTEST, true);
                it.putExtra(WinningDetailActivity.PRODUCT_ID, lssueVo.ProID);
                it.putExtra(WinningDetailActivity.LSSUE_ID, lssueVo.ProductLssueID);
                it.putExtra(WinningDetailActivity.DETAIL_TYPE, WinningDetailActivity.DETAIL_TYPE_FUND);
                startActivity(it);
            }
        });

        loadData(true);
        return view;

    }

    private void initHead() {
        View v = View.inflate(getActivity(), R.layout.fund_head, null);
        fundFeeView = (TextView) v.findViewById(R.id.fund_fee_textview);
        if (isPersion) {
            fundFeeView.setVisibility(View.GONE);
        }
        mListView.addHeaderView(v);
    }

    private void loadData(final boolean isFirst) {
        if (isFirst) {
            dataLoadingView.startLoading();
        }
        ProductManager.fetchFundData(PAGE_SIZE, pageNo, typeId, userId).startUI(new ApiCallback<FundJsonVO>() {
            @Override
            public void onError(int code, String errorInfo) {
                if (isFirst) {
                    dataLoadingView.loadFail();
                }
            }

            @Override
            public void onSuccess(FundJsonVO fundJsonVO) {
                if (isFirst) {
                    dataLoadingView.loadSuccess();
                    fundFeeView.setText(fundJsonVO.TotalMoney);
                }

                typeList = fundJsonVO.TypeList;

                fundAdapter.addAll(fundJsonVO.ProductLssueList, needClear);
            }

            @Override
            public void onProgress(int progress) {

            }
        });
    }

    @Subscribe
    public void onEventMainThread(FundTypeEvent event) {
        if (!getActivity().isFinishing()) {
            typeId = event.typeId;
            pageNo = 1;
            needClear = true;
            loadData(false);
        }
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

    @Override
    Map<String, Object> getSwitchData() {
        if (typeList == null || typeList.isEmpty()) {
            return null;
        }

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("result", typeList);
        return params;
    }

    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
