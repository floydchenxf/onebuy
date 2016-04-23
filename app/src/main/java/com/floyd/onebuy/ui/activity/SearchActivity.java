package com.floyd.onebuy.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.floyd.onebuy.aync.ApiCallback;
import com.floyd.onebuy.aync.AsyncJob;
import com.floyd.onebuy.aync.JobFactory;
import com.floyd.onebuy.biz.manager.SearchManager;
import com.floyd.onebuy.dao.Search;
import com.floyd.onebuy.ui.R;

import java.util.ArrayList;
import java.util.List;


public class SearchActivity extends Activity implements View.OnClickListener {

    private ListView searchContentListView;
    private EditText searchContentView;
    private TextView searchButton;
    private ArrayAdapter searchAdapter;
    private List<String> searchList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        findViewById(R.id.title_back).setOnClickListener(this);
        searchContentView = (EditText) findViewById(R.id.search_content_view);
        searchButton = (TextView) findViewById(R.id.search_button);
        searchContentListView = (ListView) findViewById(R.id.search_list_view);
        searchAdapter = new ArrayAdapter(this, R.layout.simple_list_item, searchList);
        searchContentListView.setAdapter(searchAdapter);
        searchButton.setOnClickListener(this);
        initData();
    }

    private void initData() {
        JobFactory.createAsyncJob(new AsyncJob<List<String>>() {
            @Override
            public void start(ApiCallback<List<String>> callback) {
                List<String> result = new ArrayList<String>();
                List<Search> list = SearchManager.queryAllSearchRecords(SearchActivity.this);
                if (list == null) {
                    callback.onSuccess(result);
                    return;
                }

                for (Search s : list) {
                    result.add(s.getContent());
                }

                callback.onSuccess(result);
            }
        }).startUI(new ApiCallback<List<String>>() {
            @Override
            public void onError(int code, String errorInfo) {

            }

            @Override
            public void onSuccess(List<String> strings) {
                searchList.clear();
                searchList.addAll(strings);
                searchAdapter.notifyDataSetChanged();
            }

            @Override
            public void onProgress(int progress) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.search_button:
                final String content = searchContentView.getText().toString();
                if (TextUtils.isEmpty(content)) {
                    Toast.makeText(this, "请输入查询关键字", Toast.LENGTH_SHORT).show();
                    return;
                }

                JobFactory.createAsyncJob(new AsyncJob<Boolean>() {
                    @Override
                    public void start(ApiCallback<Boolean> callback) {
                        long id = SearchManager.addSearchRecord(SearchActivity.this, content);
                        if (id > 0) {
                            callback.onSuccess(true);
                        } else {
                            callback.onError(-1, "");
                        }
                    }
                }).startUI(new ApiCallback<Boolean>() {
                    @Override
                    public void onError(int code, String errorInfo) {
                        Toast.makeText(SearchActivity.this, "查询插入失败", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess(Boolean aBoolean) {
                        Toast.makeText(SearchActivity.this, "查询插入成功", Toast.LENGTH_SHORT).show();
                        SearchActivity.this.initData();
                    }

                    @Override
                    public void onProgress(int progress) {

                    }
                });

                break;
            case R.id.title_back:
                this.finish();
                break;
        }

    }
}
