package com.yyg365.interestbar.ui.activity;

import android.graphics.Color;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.yyg365.interestbar.aync.ApiCallback;
import com.yyg365.interestbar.biz.manager.CommonwealManager;
import com.yyg365.interestbar.biz.vo.commonweal.CommonwealPoolList;
import com.yyg365.interestbar.biz.vo.commonweal.CommonwealPoolVO;
import com.yyg365.interestbar.ui.ImageLoaderFactory;
import com.yyg365.interestbar.ui.R;
import com.yyg365.interestbar.ui.adapter.CommonwealPoolPersionAdapter;

import java.util.ArrayList;
import java.util.List;

public class CommonwealPoolPersionActivity extends CommonActivity implements View.OnClickListener {

    private CommonwealPoolPersionAdapter adapter;

    private ImageLoader mImageLoader;

    private TextView totalNumView;
    private float dp;

    protected void initData() {
        mImageLoader = ImageLoaderFactory.createImageLoader();
        dp = getResources().getDimension(R.dimen.one_dp);
        adapter = new CommonwealPoolPersionAdapter(this, new ArrayList<CommonwealPoolVO>(), mImageLoader);
    }

    @Override
    protected String fillTitleName() {
        return "捐赠记录";
    }

    @Override
    protected void initListView(ListView mListView) {
        LinearLayout totalNumLayout = new LinearLayout(this);
        AbsListView.LayoutParams params = new AbsListView.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        totalNumLayout.setLayoutParams(params);
        totalNumView = new TextView(this);
        totalNumView.setTextSize(14);
        totalNumView.setTextColor(Color.parseColor("#666666"));
        totalNumView.setPadding((int) (14 * dp), (int) (10 * dp), (int) (14 * dp), (int) (10 * dp));
        totalNumLayout.addView(totalNumView);
        mListView.addHeaderView(totalNumLayout);
        mListView.setAdapter(adapter);

    }

    @Override
    protected void loadIndexData() {
        loadData();
    }

    @Override
    protected void loadNextPageData() {
        loadData();
    }


    private void loadData() {
        if (isFirst) {
            dataLoadingView.startLoading();
        }

        CommonwealManager.fetchPoolPersonList(pageNo, PAGE_SIZE).startUI(new ApiCallback<CommonwealPoolList>() {
            @Override
            public void onError(int code, String errorInfo) {
                if (isFirst) {
                    dataLoadingView.loadFail();
                }
            }

            @Override
            public void onSuccess(CommonwealPoolList commonwealPoolList) {
                if (isFirst) {
                    dataLoadingView.loadSuccess();
                    totalNumView.setText(Html.fromHtml("感谢<font color=\"red\">" + commonwealPoolList.PersonCount + "</font>位爱心朋友"));
                }

                List<CommonwealPoolVO> personList = commonwealPoolList.OrderPersonList;
                adapter.addAll(personList, needClear);
            }

            @Override
            public void onProgress(int progress) {

            }
        });
    }


    @Override
    public void onClick(View view) {

    }
}
