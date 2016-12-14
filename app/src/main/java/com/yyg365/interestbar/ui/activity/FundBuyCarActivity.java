package com.yyg365.interestbar.ui.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
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
import com.yyg365.interestbar.aync.ApiCallback;
import com.yyg365.interestbar.biz.constants.APIConstants;
import com.yyg365.interestbar.biz.constants.BuyCarType;
import com.yyg365.interestbar.biz.manager.AddressManager;
import com.yyg365.interestbar.biz.manager.CarManager;
import com.yyg365.interestbar.biz.manager.DBManager;
import com.yyg365.interestbar.biz.manager.LoginManager;
import com.yyg365.interestbar.biz.manager.OrderManager;
import com.yyg365.interestbar.biz.vo.json.CarItemVO;
import com.yyg365.interestbar.biz.vo.json.CarListVO;
import com.yyg365.interestbar.biz.vo.json.CarPayChannel;
import com.yyg365.interestbar.biz.vo.json.GoodsAddressVO;
import com.yyg365.interestbar.biz.vo.json.OrderPayVO;
import com.yyg365.interestbar.biz.vo.json.OrderVO;
import com.yyg365.interestbar.biz.vo.json.UserVO;
import com.yyg365.interestbar.biz.vo.model.WinningInfo;
import com.yyg365.interestbar.event.PayFirdaySuccessEvent;
import com.yyg365.interestbar.event.PayFundSuccessEvent;
import com.yyg365.interestbar.event.PaySuccessEvent;
import com.yyg365.interestbar.event.TabSwitchEvent;
import com.yyg365.interestbar.ui.ImageLoaderFactory;
import com.yyg365.interestbar.ui.MainActivity;
import com.yyg365.interestbar.ui.R;
import com.yyg365.interestbar.ui.adapter.BuyCarAdapter;
import com.yyg365.interestbar.ui.loading.DataLoadingView;
import com.yyg365.interestbar.ui.loading.DefaultDataLoadingView;
import com.yyg365.pullrefresh.widget.PullToRefreshBase;
import com.yyg365.pullrefresh.widget.PullToRefreshListView;
import com.unionpay.UPPayAssistEx;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;

public class FundBuyCarActivity extends Activity implements View.OnClickListener {

    public static final String PAY_RESULT = "pay_result";

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
    private String orderNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_buy_car);
        EventBus.getDefault().register(this);
        userId = LoginManager.getLoginInfo(this).ID;
        initedFooter = false;
        mImageLoader = ImageLoaderFactory.createImageLoader();
        buyCarType = BuyCarType.FUND;

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
                        Toast.makeText(FundBuyCarActivity.this, errorInfo, Toast.LENGTH_SHORT).show();
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
        titleNameView.setText("基金购物车");
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
        findViewById(R.id.title_back).setOnClickListener(this);
    }

    private void initFooter() {
        View footer = View.inflate(FundBuyCarActivity.this, R.layout.buycar_footer, null);
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
                            View v = View.inflate(FundBuyCarActivity.this, R.layout.pay_type_item, null);
                            v.setTag(channel.ID);
                            NetworkImageView imageView = (NetworkImageView) v.findViewById(R.id.wx_icon);
                            imageView.setImageUrl(channel.getPicUrl(), mImageLoader);
                            TextView payNameView = (TextView) v.findViewById(R.id.pay_name_view);
                            StringBuilder sb = new StringBuilder(channel.Name);
                            if (channel.ID == 1) {
                                sb.append(" (￥").append(carListVO.UserInfo.Amount).append(")");
                            } else if (channel.ID == 2) {
                                sb.append(" (").append(carListVO.UserInfo.JiFen).append(")");
                            }
                            payNameView.setText(sb.toString());
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
                            idx++;
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
                    productLssueDetail.append(info.ProductLssueID).append("_").append(info.CarCount).append("|");
                }

                OrderManager.createOrder(BuyCarType.FRI, vo.ID, productLssueDetail.substring(0, productLssueDetail.toString().length() - 1), payType).startUI(new ApiCallback<OrderVO>() {
                    @Override
                    public void onError(int code, String errorInfo) {
                        Toast.makeText(FundBuyCarActivity.this, errorInfo, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess(OrderVO orderVO) {
                        if (payType == 6) {
                            FundBuyCarActivity.this.orderNum = orderVO.orderNum;
                            UPPayAssistEx.startPay(FundBuyCarActivity.this, null, null, orderVO.tn, APIConstants.PAY_MODE);
                        } else {
                            FundBuyCarActivity.this.orderNum = orderVO.orderNum;
                            buySuccessCall();
                        }
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
                        Toast.makeText(FundBuyCarActivity.this, errorInfo, Toast.LENGTH_SHORT).show();
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
                Intent it = new Intent(this, MainActivity.class);
                it.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                this.startActivity(it);
                break;
            case R.id.title_back:
                this.finish();
                break;
        }
    }

    private void buySuccessCall() {
        if (this.isFinishing()) {
            return;
        }


        final Set<Long> delCarIds = new HashSet<Long>();
        for (CarItemVO info : mBuyCarAdapter.getRecords()) {
            delCarIds.add(info.CarID);
        }

        isEdit = false;
        mBuyCarAdapter.remove(delCarIds);
        mBuyCarAdapter.showRadiio(isEdit);

        Intent intent = new Intent(this, PayResultActivity.class);
        intent.putExtra(APIConstants.PAY_ORDER_NO, orderNum);
        startActivity(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        String msg = "";
        String str = data.getExtras().getString(PAY_RESULT);
        if (str.equalsIgnoreCase("success")) {
            EventBus.getDefault().post(new PaySuccessEvent());
            return;
        }

        if (str.equalsIgnoreCase("fail")) {
            msg = "支付失败！";
        } else if (str.equalsIgnoreCase("cancel")) {
            msg = "用户取消了支付";
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("支付结果通知");
        builder.setMessage(msg);
        builder.setInverseBackgroundForced(true);
        builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    @Subscribe
    public void onEventMainThread(PayFundSuccessEvent event) {
        buySuccessCall();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
