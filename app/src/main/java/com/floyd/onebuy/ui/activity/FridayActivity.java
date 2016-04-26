package com.floyd.onebuy.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.floyd.onebuy.biz.vo.product.WinningInfo;
import com.floyd.onebuy.ui.ImageLoaderFactory;
import com.floyd.onebuy.ui.R;
import com.floyd.onebuy.ui.adapter.FridayAdapter;
import com.floyd.onebuy.ui.loading.DataLoadingView;
import com.floyd.onebuy.ui.loading.DefaultDataLoadingView;
import com.floyd.pullrefresh.widget.PullToRefreshListView;

import java.util.ArrayList;
import java.util.List;


public class FridayActivity extends Activity implements View.OnClickListener {

    private DataLoadingView dataLoadingView;
    private ImageLoader mImageLoader;
    private int pageNo = 1;
    private boolean needClear;
    private PullToRefreshListView mPullToRefreshListView;
    private ListView mListView;
    private FridayAdapter fridayAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friday);
        dataLoadingView = new DefaultDataLoadingView();
        dataLoadingView.initView(findViewById(R.id.act_lsloading), this);
        mImageLoader = ImageLoaderFactory.createImageLoader();
        findViewById(R.id.title_back).setOnClickListener(this);
        TextView titleName = (TextView) findViewById(R.id.title_name);
        titleName.setVisibility(View.VISIBLE);
        titleName.setText("快乐星期五");

        mPullToRefreshListView = (PullToRefreshListView) findViewById(R.id.friday_list);
        mListView = mPullToRefreshListView.getRefreshableView();
        fridayAdapter = new FridayAdapter(this, new ArrayList<WinningInfo>(), mImageLoader);
        mListView.setAdapter(fridayAdapter);
        loadData(true);
    }

    private void loadData(boolean isFirst) {
        List<WinningInfo> winningRecordVOs = new ArrayList<WinningInfo>();
        for(int i = 0; i < 20; i++) {
            WinningInfo vo  = new WinningInfo();
            vo.productUrl = "http://qmmt2015.b0.upaiyun.com/2016/4/12/70242b33-34df-4db5-a334-46000335e8f4.png";
            vo.left=i+ 1;
            vo.id = i;
            vo.processPrecent=50+i;
            vo.title = "小米手机５｜｜精彩开奖就送苹果";
            winningRecordVOs.add(vo);
        }
        fridayAdapter.addAll(winningRecordVOs, needClear);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_back:
                this.finish();
                break;
            case R.id.act_ls_fail_layout:
                pageNo = 1;
                needClear = true;
                loadData(true);
                break;
        }

    }
}
