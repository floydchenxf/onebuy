package com.yyg365.interestbar.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Network;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.yyg365.interestbar.aync.ApiCallback;
import com.yyg365.interestbar.biz.constants.BuyCarType;
import com.yyg365.interestbar.biz.manager.SpecialSubjectManager;
import com.yyg365.interestbar.biz.vo.json.SubjectInfoVO;
import com.yyg365.interestbar.biz.vo.json.SubjectPageDataVO;
import com.yyg365.interestbar.biz.vo.model.WinningInfo;
import com.yyg365.interestbar.ui.ImageLoaderFactory;
import com.yyg365.interestbar.ui.R;
import com.yyg365.interestbar.ui.adapter.ProductLssueAdapter;
import com.yyg365.interestbar.ui.adapter.SubjectProductAdapter;
import com.yyg365.interestbar.ui.loading.DataLoadingView;
import com.yyg365.interestbar.ui.loading.DefaultDataLoadingView;

import java.util.ArrayList;
import java.util.List;

public class SubjectInfoActivity extends Activity {

    public static final String SUBJECT_ID = "SUBJECT_ID";

    private Long id;

    private DataLoadingView dataLoadingView;
    private ListView mListView;
    private ImageLoader mImageLoader;
    private SubjectProductAdapter productAdapter;
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
        findViewById(R.id.title_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SubjectInfoActivity.this.finish();
            }
        });

        backgroudView = (NetworkImageView) findViewById(R.id.background_view);
        backgroudView.setDefaultImageResId(R.drawable.tupian);
        mListView = (ListView) findViewById(R.id.common_list);
        productAdapter = new SubjectProductAdapter(BuyCarType.NORMAL, this, new ArrayList<WinningInfo>(), mImageLoader);
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
