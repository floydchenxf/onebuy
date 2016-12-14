package com.yyg365.interestbar.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.yyg365.interestbar.aync.ApiCallback;
import com.yyg365.interestbar.biz.constants.APIConstants;
import com.yyg365.interestbar.biz.manager.NewsManager;
import com.yyg365.interestbar.biz.manager.ProductManager;
import com.yyg365.interestbar.biz.vo.json.HistoryPrizeListVO;
import com.yyg365.interestbar.biz.vo.json.HistoryPrizeVO;
import com.yyg365.interestbar.biz.vo.json.NewsVO;
import com.yyg365.interestbar.ui.adapter.HistoryPrizeAdapter;
import com.yyg365.interestbar.ui.adapter.NewsAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenxiaofeng on 16/9/24.
 */
public class NewsActivity extends CommonActivity {


    private NewsAdapter mAdapter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void initData() {
        mAdapter = new NewsAdapter(this, new ArrayList<NewsVO>());
    }

    @Override
    protected String fillTitleName() {
        return "重要公告";
    }

    @Override
    protected void initListView(ListView mListView) {
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                NewsVO vo = mAdapter.getRecords().get(i - 1);
                Intent detailIntent = new Intent(NewsActivity.this, H5Activity.class);
                H5Activity.H5Data h5Data = new H5Activity.H5Data();
                h5Data.dataType = H5Activity.H5Data.H5_DATA_TYPE_URL;
                String detailUrl = String.format(APIConstants.NEWS_DETAIL_URL_FORMAT, vo.ID);
                h5Data.data = detailUrl;
                h5Data.showProcess = true;
                h5Data.showNav = true;
                h5Data.title = "公告详情";
                detailIntent.putExtra(H5Activity.H5Data.H5_DATA, h5Data);
                startActivity(detailIntent);

            }
        });
        mListView.setAdapter(mAdapter);
    }

    @Override
    protected void loadIndexData() {
        loadData();
    }

    private void loadData() {
        if (isFirst) {
            dataLoadingView.startLoading();
        }

        NewsManager.fetchNews(pageNo, PAGE_SIZE).startUI(new ApiCallback<List<NewsVO>>() {
            @Override
            public void onError(int code, String errorInfo) {
                if (isFirst) {
                    dataLoadingView.loadFail();
                }
            }

            @Override
            public void onSuccess(List<NewsVO> newsVOs) {
                if (isFirst) {
                    dataLoadingView.loadSuccess();
                }

                if (newsVOs == null) {
                    newsVOs = new ArrayList<NewsVO>();
                }
                mAdapter.addAll(newsVOs, needClear);
                pageNo++;
            }

            @Override
            public void onProgress(int progress) {

            }
        });
    }

    @Override
    protected void loadNextPageData() {
        loadData();
    }
}
