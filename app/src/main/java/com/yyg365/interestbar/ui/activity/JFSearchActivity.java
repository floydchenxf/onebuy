package com.yyg365.interestbar.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.yyg365.interestbar.aync.ApiCallback;
import com.yyg365.interestbar.aync.AsyncJob;
import com.yyg365.interestbar.aync.JobFactory;
import com.yyg365.interestbar.biz.manager.DBManager;
import com.yyg365.interestbar.dao.JFSearch;
import com.yyg365.interestbar.dao.Search;
import com.yyg365.interestbar.ui.R;

import java.util.ArrayList;
import java.util.List;


public class JFSearchActivity extends Activity implements View.OnClickListener {

    public static final String SEARCH_WORD = "search_word";

    private ListView searchContentListView;
    private EditText searchContentView;
    private TextView searchButton;
    private ArrayAdapter searchAdapter;
    private List<String> searchList = new ArrayList<String>();
    private TextView clearButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jfsearch);
        findViewById(R.id.title_back).setOnClickListener(this);

        TextView titleName = (TextView)findViewById(R.id.title_name);
        titleName.setText("积分商城");
        titleName.setVisibility(View.VISIBLE);

        searchContentView = (EditText) findViewById(R.id.search_content_view);
        searchButton = (TextView) findViewById(R.id.search_button);
        searchContentListView = (ListView) findViewById(R.id.search_list_view);
        searchAdapter = new ArrayAdapter(this, R.layout.simple_list_item, searchList);
        searchContentListView.setAdapter(searchAdapter);

        searchContentListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position < searchList.size()) {
                    String k = searchList.get(position);
                    Intent it = new Intent(JFSearchActivity.this, JFStoreActivity.class);
                    it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    it.putExtra(JFStoreActivity.SEARCH_KEY, k);
                    startActivity(it);
                }
            }
        });
        searchButton.setOnClickListener(this);
        clearButton = (TextView) findViewById(R.id.clear_search_view);
        clearButton.setOnClickListener(this);
        initData();
    }

    private void initData() {
        JobFactory.createAsyncJob(new AsyncJob<List<String>>() {
            @Override
            public void start(ApiCallback<List<String>> callback) {
                List<String> result = new ArrayList<String>();
                List<JFSearch> list = DBManager.queryAllJFSearchRecords(JFSearchActivity.this);
                if (list == null) {
                    callback.onSuccess(result);
                    return;
                }

                for (JFSearch s : list) {
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

                JobFactory.createAsyncJob(new AsyncJob<Integer>() {
                    @Override
                    public void start(ApiCallback<Integer> callback) {
                        boolean isExists = DBManager.isJFExists(JFSearchActivity.this, content);
                        if (isExists) {
                            callback.onSuccess(1);
                            return;
                        }
                        DBManager.addJFSearchRecord(JFSearchActivity.this, content);
                        callback.onSuccess(2);
                    }
                }).startUI(new ApiCallback<Integer>() {
                    @Override
                    public void onError(int code, String errorInfo) {
                        Toast.makeText(JFSearchActivity.this, "查询插入失败", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess(Integer a) {
                        Intent it = new Intent(JFSearchActivity.this, JFStoreActivity.class);
                        it.putExtra(JFStoreActivity.SEARCH_KEY, content);
                        it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(it);
                        searchContentView.setText("");
                        if (a == 2) {
                            searchList.add(content);
                            searchAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onProgress(int progress) {

                    }
                });

                break;
            case R.id.title_back:
                this.finish();
                break;
            case R.id.clear_search_view:
                DBManager.deleteJFSearchRecords(JFSearchActivity.this);
                searchList.clear();
                searchAdapter.notifyDataSetChanged();
                break;
        }

    }
}
