package com.floyd.onebuy.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.floyd.onebuy.biz.vo.json.AddressVO;
import com.floyd.onebuy.ui.R;
import com.floyd.onebuy.ui.adapter.AddressAdapter;
import com.floyd.onebuy.ui.loading.DataLoadingView;
import com.floyd.onebuy.ui.loading.DefaultDataLoadingView;

import java.util.ArrayList;

public class AddressManagerActivity extends Activity implements View.OnClickListener {
    private AddressAdapter addressAdapter;
    private ListView mListView;
    private DataLoadingView dataLoadingView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_manager);
        dataLoadingView = new DefaultDataLoadingView();
        dataLoadingView.initView(findViewById(R.id.act_lsloading), this);
        mListView = (ListView) findViewById(R.id.address_list);
        addressAdapter = new AddressAdapter(this, new ArrayList<AddressVO>());
        mListView.setAdapter(addressAdapter);
        loadData();
    }

    private void loadData() {
        dataLoadingView.startLoading();
        //TODO
        dataLoadingView.loadSuccess();
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
        }
    }

}
