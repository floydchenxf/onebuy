package com.floyd.onebuy.ui.fragment;

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
import com.floyd.onebuy.aync.ApiCallback;
import com.floyd.onebuy.biz.manager.ProductManager;
import com.floyd.onebuy.biz.vo.commonweal.TypeVO;
import com.floyd.onebuy.biz.vo.fund.FundJsonVO;
import com.floyd.onebuy.biz.vo.json.ProductLssueVO;
import com.floyd.onebuy.event.FundTypeEvent;
import com.floyd.onebuy.ui.ImageLoaderFactory;
import com.floyd.onebuy.ui.R;
import com.floyd.onebuy.ui.activity.WinningDetailActivity;
import com.floyd.onebuy.ui.adapter.FundAdapter;
import com.floyd.onebuy.ui.loading.DataLoadingView;
import com.floyd.onebuy.ui.loading.DefaultDataLoadingView;
import com.floyd.pullrefresh.widget.PullToRefreshBase;
import com.floyd.pullrefresh.widget.PullToRefreshListView;

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

    public FundFragment() {
    }

    public static FundFragment newInstance(String param1, String param2) {
        FundFragment fragment = new FundFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
                ProductLssueVO lssueVo = fundAdapter.getItem(position - 2);
                Intent it = new Intent(getActivity(), WinningDetailActivity.class);
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
        mListView.addHeaderView(v);
    }

    private void loadData(final boolean isFirst) {
        if (isFirst) {
            dataLoadingView.startLoading();
        }
        ProductManager.fetchFundData(PAGE_SIZE, pageNo, typeId, null).startUI(new ApiCallback<FundJsonVO>() {
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
