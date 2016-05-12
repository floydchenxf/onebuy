package com.floyd.onebuy.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.floyd.onebuy.aync.ApiCallback;
import com.floyd.onebuy.biz.constants.APIConstants;
import com.floyd.onebuy.biz.manager.AddressManager;
import com.floyd.onebuy.biz.manager.LoginManager;
import com.floyd.onebuy.biz.vo.json.GoodsAddressVO;
import com.floyd.onebuy.event.AddressModifiedEvent;
import com.floyd.onebuy.ui.R;
import com.floyd.onebuy.ui.adapter.GoodsAddressAdapter;
import com.floyd.onebuy.ui.loading.DataLoadingView;
import com.floyd.onebuy.ui.loading.DefaultDataLoadingView;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;

public class AddressManagerActivity extends Activity implements View.OnClickListener {
    private GoodsAddressAdapter addressAdapter;
    private ListView mListView;
    private View emptyView;
    private DataLoadingView dataLoadingView;
    private static final int PAGE_SIZE = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_manager);
        EventBus.getDefault().register(this);
        dataLoadingView = new DefaultDataLoadingView();
        dataLoadingView.initView(findViewById(R.id.act_lsloading), this);
        mListView = (ListView) findViewById(R.id.address_list);
        emptyView = findViewById(R.id.empty_layout);
        findViewById(R.id.title_back).setOnClickListener(this);
        TextView titleName = (TextView) findViewById(R.id.title_name);
        titleName.setText("收货地址管理");
        titleName.setVisibility(View.VISIBLE);
        TextView rightView = (TextView)findViewById(R.id.right);
        rightView.setText("添加");
        rightView.setVisibility(View.VISIBLE);
        rightView.setOnClickListener(this);

        addressAdapter = new GoodsAddressAdapter(this, new ArrayList<GoodsAddressVO>());
        mListView.setAdapter(addressAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GoodsAddressVO goodsAddressVO = addressAdapter.getItem(position);
                Intent addAddressIntent = new Intent(AddressManagerActivity.this, AddressModifyActivity.class);
                addAddressIntent.putExtra(APIConstants.ADDRESS_MANAGEMENT_VO, goodsAddressVO);
                startActivity(addAddressIntent);
            }
        });
        loadData();
    }

    private void loadData() {
        dataLoadingView.startLoading();
        long userId = LoginManager.getLoginInfo(this).ID;
        AddressManager.getMyAddressList(userId, PAGE_SIZE, 1).startUI(new ApiCallback<List<GoodsAddressVO>>() {
            @Override
            public void onError(int code, String errorInfo) {
                dataLoadingView.loadFail();
            }

            @Override
            public void onSuccess(List<GoodsAddressVO> goodsAddressVOs) {
                dataLoadingView.loadSuccess();
                addressAdapter.addAll(goodsAddressVOs, true);
                if (addressAdapter.getRecords().isEmpty()) {
                    mListView.setVisibility(View.GONE);
                    emptyView.setVisibility(View.VISIBLE);
                } else {
                    mListView.setVisibility(View.VISIBLE);
                    emptyView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onProgress(int progress) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_back:
                this.finish();
                break;
            case R.id.act_ls_fail_layout:
                loadData();
                break;
            case R.id.right:
                Intent addAddressIntent = new Intent(this, AddressModifyActivity.class);
                startActivity(addAddressIntent);
                break;
        }
    }

    @Subscribe
    public void onEventMainThread(AddressModifiedEvent event) {
        if (!this.isFinishing()) {
            loadData();
        }
    }

    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

}
