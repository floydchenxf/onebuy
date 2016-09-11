package com.floyd.onebuy.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.floyd.onebuy.aync.ApiCallback;
import com.floyd.onebuy.biz.constants.APIConstants;
import com.floyd.onebuy.biz.constants.BuyCarType;
import com.floyd.onebuy.biz.manager.AddressManager;
import com.floyd.onebuy.biz.manager.CarManager;
import com.floyd.onebuy.biz.manager.DBManager;
import com.floyd.onebuy.biz.manager.LoginManager;
import com.floyd.onebuy.biz.manager.OrderManager;
import com.floyd.onebuy.biz.vo.json.CarItemVO;
import com.floyd.onebuy.biz.vo.json.CarListVO;
import com.floyd.onebuy.biz.vo.json.CarPayChannel;
import com.floyd.onebuy.biz.vo.json.GoodsAddressVO;
import com.floyd.onebuy.biz.vo.json.OrderPayVO;
import com.floyd.onebuy.biz.vo.json.UserVO;
import com.floyd.onebuy.biz.vo.model.WinningInfo;
import com.floyd.onebuy.event.TabSwitchEvent;
import com.floyd.onebuy.ui.ImageLoaderFactory;
import com.floyd.onebuy.ui.R;
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

public class FridayBuyCarActivity extends Activity implements View.OnClickListener {

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

    private BuyCarType buyCarType;
    private Long userId;
    private boolean initedFooter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_buy_car);

        userId = LoginManager.getLoginInfo(this).ID;
        initedFooter = false;
        mImageLoader = ImageLoaderFactory.createImageLoader();
        buyCarType = BuyCarType.FRI;

        dataLoadingView = new DefaultDataLoadingView();
        dataLoadingView.initView(findViewById(R.id.act_lsloading), this);
        emptyLayout = findViewById(R.id.empty_layout);
        bottomLayout = findViewById(R.id.bottom_layout);

        mListView = (ListView) findViewById(R.id.buy_car_list);
        mBuyCarAdapter = new BuyCarAdapter(this, null, mImageLoader, new BuyCarAdapter.BuyClickListener() {
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
                        Toast.makeText(FridayBuyCarActivity.this, errorInfo, Toast.LENGTH_SHORT).show();
                        numberView.setText(buyNumber + "");
                    }

                    @Override
                    public void onSuccess(Boolean aBoolean) {
                        if (aBoolean) {
                            CarItemVO cv = (CarItemVO) (v.getTag());
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
        titleNameView = (TextView) findViewById(R.id.title_name);
        titleNameView.setText("星期五购物车");
        titleNameView.setVisibility(View.VISIBLE);
        payLayout = findViewById(R.id.pay_layout);
        payLayout.setVisibility(View.GONE);
        totalProductView = (TextView) findViewById(R.id.total_product_view);
        payView = (TextView) findViewById(R.id.pay_view);
        payView.setOnClickListener(this);

        editView = (TextView) findViewById(R.id.right);
        editView.setVisibility(View.VISIBLE);
        editView.setOnClickListener(this);

        gotoIndexView = (TextView) emptyLayout.findViewById(R.id.goto_index);

        gotoIndexView.setOnClickListener(this);

        deleteLayout = findViewById(R.id.delete_layout);
        deleteLayout.setVisibility(View.GONE);
        deleteDescView = (TextView) findViewById(R.id.delete_desc_view);
        deleteButtonView = (TextView) findViewById(R.id.delete_button_view);
        deleteButtonView.setOnClickListener(this);

        mBuyCarAdapter.showRadiio(isEdit);
        findViewById(R.id.title_back).setVisibility(View.GONE);
    }

    private void initFooter() {
        View footer = View.inflate(FridayBuyCarActivity.this, R.layout.buycar_footer, null);
        mListView.removeFooterView(footer);
        payTypeLayout = (LinearLayout) footer.findViewById(R.id.pay_type_layout);
        mListView.addFooterView(footer);
    }

    public void onResume() {
        super.onResume();
        loadData(true);
    }

    private void loadData(final boolean isFirst) {
        UserVO userVO = LoginManager.getLoginInfo(this);
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
                mBuyCarAdapter.addAll(list, true);

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
                            View v = View.inflate(FridayBuyCarActivity.this, R.layout.pay_type_item, null);
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.act_ls_fail_layout:
                loadData(true);
                break;
            case R.id.pay_view:
                UserVO vo = LoginManager.getLoginInfo(this);
                if (vo == null) {
                    Toast.makeText(this, "请先登录用户!", Toast.LENGTH_SHORT).show();
                    return;
                }

                StringBuilder productLssueDetail = new StringBuilder();
                final Set<Long> delCarIds = new HashSet<Long>();
                for (CarItemVO info : mBuyCarAdapter.getRecords()) {
                    delCarIds.add(info.CarID);
                    productLssueDetail.append(info.ProductLssueID).append("|").append(info.CarCount).append(",");
                }

                GoodsAddressVO goodsAddressVO = AddressManager.getDefaultAddressInfo(this);
                String address = "";
                if (goodsAddressVO != null) {
                    address = goodsAddressVO.getFullAddress();
                }

                OrderManager.createAndPayOrder(BuyCarType.FRI, vo.ID, productLssueDetail.substring(0, productLssueDetail.toString().length() - 1), payType).startUI(new ApiCallback<OrderPayVO>() {
                    @Override
                    public void onError(int code, String errorInfo) {
                        Toast.makeText(FridayBuyCarActivity.this, errorInfo, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess(OrderPayVO orderVO) {
                        CarManager.delCar(buyCarType, delCarIds).startUI(new ApiCallback<Boolean>() {
                            @Override
                            public void onError(int code, String errorInfo) {
                                Toast.makeText(FridayBuyCarActivity.this, errorInfo, Toast.LENGTH_SHORT).show();
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
                        Intent intent = new Intent(FridayBuyCarActivity.this, PayResultActivity.class);
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

                UserVO userVO = LoginManager.getLoginInfo(this);
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
                    Toast.makeText(this, "请选择删除记录", Toast.LENGTH_SHORT).show();
                    return;
                }
                CarManager.delCar(buyCarType, carIds).startUI(new ApiCallback<Boolean>() {
                    @Override
                    public void onError(int code, String errorInfo) {
                        Toast.makeText(FridayBuyCarActivity.this, errorInfo, Toast.LENGTH_SHORT).show();
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
