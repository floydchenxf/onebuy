package com.floyd.onebuy.ui.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Network;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.floyd.onebuy.aync.ApiCallback;
import com.floyd.onebuy.biz.constants.APIConstants;
import com.floyd.onebuy.biz.constants.BuyCarType;
import com.floyd.onebuy.biz.manager.CarManager;
import com.floyd.onebuy.biz.manager.LoginManager;
import com.floyd.onebuy.biz.manager.OrderManager;
import com.floyd.onebuy.biz.vo.json.CarItemVO;
import com.floyd.onebuy.biz.vo.json.CarListVO;
import com.floyd.onebuy.biz.vo.json.CarPayChannel;
import com.floyd.onebuy.biz.vo.json.OrderPayVO;
import com.floyd.onebuy.biz.vo.json.OrderVO;
import com.floyd.onebuy.biz.vo.json.UserVO;
import com.floyd.onebuy.event.AddressModifiedEvent;
import com.floyd.onebuy.event.PaySuccessEvent;
import com.floyd.onebuy.event.TabSwitchEvent;
import com.floyd.onebuy.ui.ImageLoaderFactory;
import com.floyd.onebuy.ui.R;
import com.floyd.onebuy.ui.activity.PayResultActivity;
import com.floyd.onebuy.ui.adapter.BuyCarAdapter;
import com.floyd.onebuy.ui.loading.DataLoadingView;
import com.floyd.onebuy.ui.loading.DefaultDataLoadingView;
import com.floyd.onebuy.ui.multiimage.common.OnCheckChangedListener;
import com.unionpay.UPPayAssistEx;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;

/**
 * Created by floyd on 16-4-13.
 */
public class BuyCarFragment extends BackHandledFragment implements View.OnClickListener {

    private DataLoadingView dataLoadingView;
    private ListView mListView;
    private ImageLoader mImageLoader;
    private BuyCarAdapter mBuyCarAdapter;
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

    private LinearLayout payTypeLayout;

    private int payType = 1;
    private long userId = 0l;
    private boolean needClear;

    private BuyCarType buyCarType;
    private boolean initedFooter;

    private String orderNum;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mImageLoader = ImageLoaderFactory.createImageLoader();
        buyCarType = BuyCarType.NORMAL;
        userId = LoginManager.getLoginInfo(getActivity()).ID;
        needClear = true;
        initedFooter = false;
        EventBus.getDefault().register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_buy_car, null);
        dataLoadingView = new DefaultDataLoadingView();
        dataLoadingView.initView(view, this);
        emptyLayout = view.findViewById(R.id.empty_layout);
        bottomLayout = view.findViewById(R.id.bottom_layout);

        mListView = (ListView) view.findViewById(R.id.buy_car_list);
        mBuyCarAdapter = new BuyCarAdapter(this.getActivity(), null, mImageLoader, new BuyCarAdapter.BuyClickListener() {
            @Override
            public void onClick(final View v, final EditText numberView, final long lssueId, final int currentNum, final int buyNumber) {
                int productNum = mBuyCarAdapter.getRecords().size();
                int totalPrice = 0;
                for (CarItemVO info : mBuyCarAdapter.getRecords()) {
                    totalPrice += info.CarCount * info.SinglePrice;
                }

                totalProductView.setText(Html.fromHtml("共" + productNum + "件商品,总计：<font color=\"red\">" + totalPrice + "</font>夺宝币"));

                CarManager.addCar(buyCarType, lssueId, userId, currentNum - buyNumber).startUI(new ApiCallback<Boolean>() {
                    @Override
                    public void onError(int code, String errorInfo) {
                        Toast.makeText(getActivity(), errorInfo, Toast.LENGTH_SHORT).show();
                        numberView.setText(buyNumber+"");
                    }

                    @Override
                    public void onSuccess(Boolean aBoolean) {
                        if (aBoolean) {
                            CarItemVO cv = (CarItemVO)(v.getTag());
                            cv.CarCount = currentNum;
                            int productNum = mBuyCarAdapter.getRecords().size();
                            int totalPrice = 0;
                            for (CarItemVO info : mBuyCarAdapter.getRecords()) {
                                totalPrice += info.CarCount * info.SinglePrice;
                            }

                            totalProductView.setText(Html.fromHtml("共" + productNum + "件商品,总计：<font color=\"red\">" + totalPrice + "</font>夺宝币"));
                        }

                    }

                    @Override
                    public void onProgress(int progress) {

                    }
                });


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
        mListView.removeFooterView(footer);
        payTypeLayout = (LinearLayout) footer.findViewById(R.id.pay_type_layout);
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
        CarManager.fetchCarList(buyCarType, userId).startUI(new ApiCallback<CarListVO>() {
            @Override
            public void onError(int code, String errorInfo) {
                if (isFirst) {
                    dataLoadingView.loadFail();
                }
            }

            @Override
            public void onSuccess(CarListVO carListVO) {
                if (isFirst) {
                    dataLoadingView.loadSuccess();
                }

                List<CarItemVO> list = carListVO.list;
                mBuyCarAdapter.addAll(list, needClear);

                int productNum = list.size();
                int totalPrice = 0;
                for (CarItemVO info : mBuyCarAdapter.getRecords()) {
                    totalPrice += info.CarCount * info.SinglePrice;
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

                if (!initedFooter) {
                    List<CarPayChannel> payChannels = carListVO.PayChannel;
                    if (payChannels != null && !payChannels.isEmpty()) {
                        final RadioButton[] rbArray = new RadioButton[payChannels.size()];
                        View.OnClickListener l = new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Long id = (Long) view.getTag();
                                for (RadioButton trb : rbArray) {
                                    Long rbId = (Long) trb.getTag();
                                    if (id.equals(rbId)) {
                                        trb.setChecked(true);
                                        payType = id.intValue();
                                    } else {
                                        trb.setChecked(false);
                                    }
                                }
                            }
                        };

                        CompoundButton.OnCheckedChangeListener checkChangedListener = new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                                if (!isChecked) {
                                    compoundButton.setChecked(false);
                                    return;
                                }
                                Long id = (Long) compoundButton.getTag();
                                for (RadioButton trb : rbArray) {
                                    Long rbId = (Long) trb.getTag();
                                    if (id.equals(rbId)) {
                                        trb.setChecked(true);
                                        payType = id.intValue();
                                    } else {
                                        trb.setChecked(false);
                                    }
                                }
                            }
                        };
                        int idx = 0;
                        for (CarPayChannel channel : payChannels) {
                            View v = View.inflate(getActivity(), R.layout.pay_type_item, null);
                            v.setTag(channel.ID);
                            NetworkImageView imageView = (NetworkImageView) v.findViewById(R.id.wx_icon);
                            imageView.setImageUrl(channel.getPicUrl(), mImageLoader);
                            TextView payNameView = (TextView) v.findViewById(R.id.pay_name_view);
                            payNameView.setText(channel.Name);
                            RadioButton rb = (RadioButton) v.findViewById(R.id.wx_radio);
                            rb.setTag(channel.ID);
                            rbArray[idx] = rb;
                            if (idx == 0) {
                                rb.setChecked(true);
                                payType = channel.ID.intValue();
                            } else {
                                rb.setChecked(false);
                            }

                            rb.setOnCheckedChangeListener(checkChangedListener);
                            v.setOnClickListener(l);
                            payTypeLayout.addView(v);
                            idx ++;
                        }
                    }

                    initedFooter = true;
                }
            }

            @Override
            public void onProgress(int progress) {

            }
        });

    }

    private void showNoDataLayout() {
        editView.setVisibility(View.GONE);
        mListView.setVisibility(View.GONE);
        emptyLayout.setVisibility(View.VISIBLE);
        bottomLayout.setVisibility(View.GONE);
    }

    private void hiddenNoDataLayout() {
        editView.setVisibility(View.VISIBLE);
        mListView.setVisibility(View.VISIBLE);
        emptyLayout.setVisibility(View.GONE);
        bottomLayout.setVisibility(View.VISIBLE);
    }


    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onEventMainThread(PaySuccessEvent event) {
        if (getActivity().isFinishing()) {
            return;
        }


        final Set<Long> delCarIds = new HashSet<Long>();
        for (CarItemVO info : mBuyCarAdapter.getRecords()) {
            delCarIds.add(info.CarID);
        }

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
            }

            @Override
            public void onProgress(int progress) {
            }
        });
        Intent intent = new Intent(getActivity(), PayResultActivity.class);
        intent.putExtra(APIConstants.PAY_ORDER_NO, orderNum);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.act_ls_fail_layout:
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
                for (CarItemVO info : mBuyCarAdapter.getRecords()) {
                    delCarIds.add(info.CarID);
                    productLssueDetail.append(info.ProductLssueID).append("|").append(info.CarCount).append(",");
                }

                OrderManager.createOrder(BuyCarType.NORMAL, vo.ID, productLssueDetail.substring(0, productLssueDetail.toString().length() - 1), payType).startUI(new ApiCallback<OrderVO>() {
                    @Override
                    public void onError(int code, String errorInfo) {
                        Toast.makeText(getActivity(), errorInfo, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess(OrderVO orderVO) {
                        BuyCarFragment.this.orderNum = orderVO.orderNum;
                        UPPayAssistEx.startPay(getActivity(), null, null, orderVO.tn, "01");
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
                        loadData(false);
                    }

                    @Override
                    public void onProgress(int progress) {

                    }
                });
                break;
            case R.id.goto_index:
                EventBus.getDefault().post(new TabSwitchEvent(R.id.tab_index_page, new HashMap<String, Object>()));
                break;
        }
    }
}
