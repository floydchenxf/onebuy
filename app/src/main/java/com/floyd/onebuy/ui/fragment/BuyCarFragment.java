package com.floyd.onebuy.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.floyd.onebuy.aync.ApiCallback;
import com.floyd.onebuy.biz.constants.APIConstants;
import com.floyd.onebuy.biz.constants.BuyCarType;
import com.floyd.onebuy.biz.manager.AddressManager;
import com.floyd.onebuy.biz.manager.CarManager;
import com.floyd.onebuy.biz.manager.DBManager;
import com.floyd.onebuy.biz.manager.LoginManager;
import com.floyd.onebuy.biz.manager.OrderManager;
import com.floyd.onebuy.biz.vo.json.GoodsAddressVO;
import com.floyd.onebuy.biz.vo.json.OrderPayVO;
import com.floyd.onebuy.biz.vo.json.UserVO;
import com.floyd.onebuy.biz.vo.model.WinningInfo;
import com.floyd.onebuy.event.TabSwitchEvent;
import com.floyd.onebuy.ui.ImageLoaderFactory;
import com.floyd.onebuy.ui.R;
import com.floyd.onebuy.ui.activity.PayResultActivity;
import com.floyd.onebuy.ui.adapter.BuyCarAdapter;
import com.floyd.onebuy.ui.loading.DataLoadingView;
import com.floyd.onebuy.ui.loading.DefaultDataLoadingView;
import com.floyd.pullrefresh.widget.PullToRefreshBase;
import com.floyd.pullrefresh.widget.PullToRefreshListView;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.greenrobot.event.EventBus;

/**
 * Created by floyd on 16-4-13.
 */
public class BuyCarFragment extends BackHandledFragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private static final int PAGE_SIZE = 10;
    private DataLoadingView dataLoadingView;
    private PullToRefreshListView mPullToRefreshListView;
    private ListView mListView;
    private ImageLoader mImageLoader;
    private BuyCarAdapter mBuyCarAdapter;
    private int pageNo;
    private boolean needClear;
    private TextView titleNameView;
    private TextView editView;

    private boolean isEdit = false;

    private TextView totalProductView;//总计view
    private TextView payView;
    private View payLayout;

    private View deleteLayout;
    private TextView deleteDescView;
    private TextView deleteButtonView;

    private TextView gotoIndexView;

    private View emptyLayout;
    private View bottomLayout;

    private View weixinLayout;
    private View alipayLayout;
    private View jiefengLayout;

    private RadioButton weixinButton;
    private RadioButton alipayButton;
    private RadioButton jifengButton;

    private RadioButton[] radioButtons;

    private int payType = 1;

    private BuyCarType buyCarType;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mImageLoader = ImageLoaderFactory.createImageLoader();
        pageNo = 1;
        needClear = true;
        buyCarType = BuyCarType.NORMAL;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_buy_car, null);
        dataLoadingView = new DefaultDataLoadingView();
        dataLoadingView.initView(view, this);
        emptyLayout = view.findViewById(R.id.empty_layout);
        bottomLayout = view.findViewById(R.id.bottom_layout);

        mPullToRefreshListView = (PullToRefreshListView) view.findViewById(R.id.buy_car_list);
        mPullToRefreshListView.setMode(PullToRefreshBase.Mode.BOTH);
        mPullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2() {
            @Override
            public void onPullDownToRefresh() {
                pageNo = 1;
                needClear = true;
                loadData(false);
                mPullToRefreshListView.onRefreshComplete(false, true);
            }

            @Override
            public void onPullUpToRefresh() {
                pageNo++;
                needClear = false;
                loadData(false);
                mPullToRefreshListView.onRefreshComplete(false, true);
            }
        });
        mListView = mPullToRefreshListView.getRefreshableView();
        mBuyCarAdapter = new BuyCarAdapter(this.getActivity(), null, mImageLoader, new BuyCarAdapter.BuyClickListener() {
            @Override
            public void onClick(View v, final long lssueId, final int buyNumber) {
                UserVO vo = LoginManager.getLoginInfo(BuyCarFragment.this.getActivity());
                if (vo == null) {
                    return;
                }
                long userId = vo.ID;
                DBManager.updateBuyCarNumber(buyCarType, BuyCarFragment.this.getActivity(), userId, lssueId, buyNumber);
                int productNum = mBuyCarAdapter.getRecords().size();
                int totalPrice = 0;
                for (WinningInfo info : mBuyCarAdapter.getRecords()) {
                    totalPrice += info.buyCount;
                }

                totalProductView.setText(Html.fromHtml("共" + productNum + "件商品,总计：<font color=\"red\">" + totalPrice + "</font>夺宝币"));
            }
        }, new BuyCarAdapter.CheckedListener() {
            @Override
            public void onChecked(View v, boolean isChecked) {
                Set<Long> kk = mBuyCarAdapter.getDeleteList();
                deleteDescView.setText(Html.fromHtml("共删除<font color=\"#ff0000\">" + kk.size() + "</font>件商品"));
            }
        });
        mListView.setAdapter(mBuyCarAdapter);

        initFooter();
        titleNameView = (TextView) view.findViewById(R.id.title_name);
        titleNameView.setText("购物车");
        titleNameView.setVisibility(View.VISIBLE);
        payLayout = view.findViewById(R.id.pay_layout);
        payLayout.setVisibility(View.GONE);
        totalProductView = (TextView) view.findViewById(R.id.total_product_view);
        payView = (TextView) view.findViewById(R.id.pay_view);
        payView.setOnClickListener(this);

        editView = (TextView) view.findViewById(R.id.right);
        editView.setVisibility(View.VISIBLE);
        editView.setOnClickListener(this);

        gotoIndexView = (TextView) emptyLayout.findViewById(R.id.goto_index);

        gotoIndexView.setOnClickListener(this);

        deleteLayout = view.findViewById(R.id.delete_layout);
        deleteLayout.setVisibility(View.GONE);
        deleteDescView = (TextView) view.findViewById(R.id.delete_desc_view);
        deleteButtonView = (TextView) view.findViewById(R.id.delete_button_view);
        deleteButtonView.setOnClickListener(this);

        mBuyCarAdapter.showRadiio(isEdit);
        view.findViewById(R.id.title_back).setVisibility(View.GONE);
        return view;
    }

    private void initFooter() {
        View footer = View.inflate(getActivity(), R.layout.buycar_footer, null);
        alipayLayout = footer.findViewById(R.id.alipay_layout);
        weixinLayout = footer.findViewById(R.id.weixin_layout);
        jiefengLayout = footer.findViewById(R.id.jifeng_layout);
        alipayButton = (RadioButton) footer.findViewById(R.id.alipay_radio);
        jifengButton = (RadioButton) footer.findViewById(R.id.jifeng_radio);
        weixinButton = (RadioButton) footer.findViewById(R.id.wx_radio);
        alipayButton.setOnCheckedChangeListener(this);
        weixinButton.setOnCheckedChangeListener(this);
        jifengButton.setOnCheckedChangeListener(this);

        alipayLayout.setOnClickListener(this);
        weixinLayout.setOnClickListener(this);
        jiefengLayout.setOnClickListener(this);

        radioButtons = new RadioButton[]{weixinButton, jifengButton, alipayButton};
        mListView.addFooterView(footer);
    }

    public void onResume() {
        super.onResume();
        loadData(true);
    }

    private void loadData(final boolean isFirst) {
        UserVO userVO = LoginManager.getLoginInfo(this.getActivity());
        if (userVO == null) {
            showNoDataLayout();
            return;
        }
        if (isFirst) {
            dataLoadingView.startLoading();
        }

        long userId = userVO.ID;
        CarManager.fetchBuyCarList(buyCarType, getActivity(), userId, pageNo, PAGE_SIZE).startUI(new ApiCallback<List<WinningInfo>>() {
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
                mBuyCarAdapter.addAll(winningInfos, needClear);

                int productNum = winningInfos.size();
                int totalPrice = 0;
                for (WinningInfo info : mBuyCarAdapter.getRecords()) {
                    totalPrice += info.buyCount;
                }
                if (totalPrice <= 0) {
                    showNoDataLayout();
                    return;
                }

                hiddenNoDataLayout();
                if (isEdit) {
                    deleteLayout.setVisibility(View.VISIBLE);
                    payLayout.setVisibility(View.GONE);
                    editView.setText("完成");
                } else {
                    payLayout.setVisibility(View.VISIBLE);
                    deleteLayout.setVisibility(View.GONE);
                    editView.setText("编辑");
                    totalProductView.setText(Html.fromHtml("共" + productNum + "件商品,总计：<font color=\"red\">" + totalPrice + "</font>夺宝币"));
                }
            }

            @Override
            public void onProgress(int progress) {

            }
        });

    }

    private void showNoDataLayout() {
        editView.setVisibility(View.GONE);
        mPullToRefreshListView.setVisibility(View.GONE);
        emptyLayout.setVisibility(View.VISIBLE);
        bottomLayout.setVisibility(View.GONE);
    }

    private void hiddenNoDataLayout() {
        editView.setVisibility(View.VISIBLE);
        mPullToRefreshListView.setVisibility(View.VISIBLE);
        emptyLayout.setVisibility(View.GONE);
        bottomLayout.setVisibility(View.VISIBLE);
    }


    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.act_ls_fail_layout:
                pageNo = 1;
                needClear = true;
                loadData(true);
                break;
            case R.id.pay_view:
                UserVO vo = LoginManager.getLoginInfo(getActivity());
                if (vo == null) {
                    Toast.makeText(getActivity(), "请先登录用户!", Toast.LENGTH_SHORT).show();
                    return;
                }

                StringBuilder productLssueDetail = new StringBuilder();
                final Set<Long> delCarIds = new HashSet<Long>();
                for (WinningInfo info : mBuyCarAdapter.getRecords()) {
                    delCarIds.add(info.id);
                    productLssueDetail.append(info.lssueId).append("|").append(info.buyCount).append(",");
                }

                GoodsAddressVO goodsAddressVO = AddressManager.getDefaultAddressInfo(getActivity());
                String address = "";
                if (goodsAddressVO != null) {
                    address = goodsAddressVO.getFullAddress();
                }

                OrderManager.createAndPayOrder(BuyCarType.NORMAL, vo.ID, productLssueDetail.substring(0, productLssueDetail.toString().length() - 1), vo.Name, vo.Mobile, address, "").startUI(new ApiCallback<OrderPayVO>() {
                    @Override
                    public void onError(int code, String errorInfo) {
                        Toast.makeText(getActivity(), errorInfo, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess(OrderPayVO orderVO) {
                        CarManager.delCar(buyCarType, delCarIds).startUI(new ApiCallback<Boolean>() {
                            @Override
                            public void onError(int code, String errorInfo) {
                                Toast.makeText(getActivity(), errorInfo, Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onSuccess(Boolean s) {
                                isEdit = false;
                                mBuyCarAdapter.remove(delCarIds);
                                mBuyCarAdapter.showRadiio(isEdit);
                                pageNo = 1;
                                needClear = true;
//                                loadData(false);
                            }

                            @Override
                            public void onProgress(int progress) {

                            }
                        });
                        Intent intent = new Intent(getActivity(), PayResultActivity.class);
                        intent.putExtra(APIConstants.PAY_ORDER_NO, orderVO.orderNum);
                        startActivity(intent);
                    }

                    @Override
                    public void onProgress(int progress) {

                    }
                });
                break;
            case R.id.right:
                isEdit = !isEdit;
                mBuyCarAdapter.showRadiio(isEdit);

                UserVO userVO = LoginManager.getLoginInfo(this.getActivity());
                if (userVO != null) {
                    if (isEdit) {
                        deleteLayout.setVisibility(View.VISIBLE);
                        Set<Long> kk = mBuyCarAdapter.getDeleteList();
                        deleteDescView.setText(Html.fromHtml("共删除<font color=\"#ff0000\">" + kk.size() + "</font>件商品"));
                        payLayout.setVisibility(View.GONE);
                        editView.setText("完成");
                    } else {
                        payLayout.setVisibility(View.VISIBLE);
                        deleteLayout.setVisibility(View.GONE);
                        editView.setText("编辑");
                    }
                }

                break;
            case R.id.delete_button_view:
                final Set<Long> carIds = mBuyCarAdapter.getDeleteList();
                if (carIds == null || carIds.isEmpty()) {
                    Toast.makeText(getActivity(), "请选择删除记录", Toast.LENGTH_SHORT).show();
                    return;
                }
                CarManager.delCar(buyCarType, carIds).startUI(new ApiCallback<Boolean>() {
                    @Override
                    public void onError(int code, String errorInfo) {
                        Toast.makeText(getActivity(), errorInfo, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess(Boolean s) {
                        isEdit = false;
                        mBuyCarAdapter.remove(carIds);
                        mBuyCarAdapter.showRadiio(isEdit);
                        pageNo = 1;
                        needClear = true;
                        loadData(false);
                    }

                    @Override
                    public void onProgress(int progress) {

                    }
                });
                break;
            case R.id.weixin_layout:
                checkType(1);
                break;
            case R.id.jifeng_layout:
                checkType(2);
                break;
            case R.id.alipay_layout:
                checkType(3);
                break;
            case R.id.goto_index:
                EventBus.getDefault().post(new TabSwitchEvent(R.id.tab_index_page, new HashMap<String, Object>()));
                break;
        }
    }


    private void checkType(int type) {
        payType = type;
        int idx = 0;
        for (RadioButton rb : radioButtons) {
            if (++idx == type) {
                rb.setChecked(true);
            } else {
                rb.setChecked(false);
            }
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (!isChecked) {
            return;
        }
        switch (buttonView.getId()) {
            case R.id.wx_radio:
                checkType(1);
                break;
            case R.id.alipay_radio:
                checkType(3);
                break;
            case R.id.jifeng_radio:
                checkType(2);
                break;
        }

    }
}
