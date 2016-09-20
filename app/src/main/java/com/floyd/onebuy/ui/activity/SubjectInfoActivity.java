package com.floyd.onebuy.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.android.volley.Network;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.floyd.onebuy.aync.ApiCallback;
import com.floyd.onebuy.biz.constants.BuyCarType;
import com.floyd.onebuy.biz.manager.SpecialSubjectManager;
import com.floyd.onebuy.biz.vo.json.SubjectInfoVO;
import com.floyd.onebuy.biz.vo.json.SubjectPageDataVO;
import com.floyd.onebuy.biz.vo.model.WinningInfo;
import com.floyd.onebuy.ui.ImageLoaderFactory;
import com.floyd.onebuy.ui.R;
import com.floyd.onebuy.ui.adapter.ProductLssueAdapter;
import com.floyd.onebuy.ui.loading.DataLoadingView;
import com.floyd.onebuy.ui.loading.DefaultDataLoadingView;

import java.util.ArrayList;
import java.util.List;

public class SubjectInfoActivity extends Activity {

    public static final String SUBJECT_ID = "SUBJECT_ID";

    private Long id;

    private DataLoadingView dataLoadingView;
    private ListView mListView;
    private ImageLoader mImageLoader;
    private ProductLssueAdapter productAdapter;
    private NetworkImageView backgroudView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_info);
        id = getIntent().getLongExtra(SUBJECT_ID, 0l);

        mImageLoader = ImageLoaderFactory.createImageLoader();
        dataLoadingView = new DefaultDataLoadingView();
        dataLoadingView.initView(findViewById(R.id.act_lsloading), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadData();
            }
        });

        backgroudView = (NetworkImageView) findViewById(R.id.background_view);
        backgroudView.setDefaultImageResId(R.drawable.tupian);
        mListView = (ListView) findViewById(R.id.common_list);
        productAdapter = new ProductLssueAdapter(BuyCarType.NORMAL, this, new ArrayList<WinningInfo>(), mImageLoader);
        mListView.setAdapter(productAdapter);
        loadData();
    }

    private void loadData() {
        dataLoadingView.startLoading();
        SpecialSubjectManager.fetchSubjectInfoById(id).startUI(new ApiCallback<SubjectPageDataVO>() {
            @Override
            public void onError(int code, String errorInfo) {
                dataLoadingView.loadFail();
            }

            @Override
            public void onSuccess(SubjectPageDataVO subjectPageDataVO) {
                dataLoadingView.loadSuccess();
                SubjectInfoVO infoVO = subjectPageDataVO.SubjectInfo;
                backgroudView.setImageUrl(infoVO.getBackGround(), mImageLoader);

                List<WinningInfo> rr = subjectPageDataVO.ProductList;
                productAdapter.addAll(rr, true);
            }

            @Override
            public void onProgress(int progress) {

            }
        });
    }
}
