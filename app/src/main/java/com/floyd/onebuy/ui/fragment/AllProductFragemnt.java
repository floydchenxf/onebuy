package com.floyd.onebuy.ui.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.android.volley.toolbox.ImageLoader;
import com.floyd.onebuy.ui.ImageLoaderFactory;
import com.floyd.onebuy.ui.adapter.ProductAdapter;
import com.floyd.onebuy.ui.loading.DataLoadingView;
import com.floyd.pullrefresh.widget.PullToRefreshListView;

/**
 * Created by floyd on 16-4-12.
 */
public class AllProductFragemnt extends BackHandledFragment implements View.OnClickListener {

    private PullToRefreshListView mPullToRefreshListView;
    private ListView mListView;
    private DataLoadingView dataLoadingView;
    private ProductAdapter productAdapter;
    private ImageLoader mImageLoader;

    public static AllProductFragemnt newInstance(String param1, String param2) {
        AllProductFragemnt fragment = new AllProductFragemnt();
        return fragment;
    }

    public AllProductFragemnt() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mImageLoader = ImageLoaderFactory.createImageLoader();
    }

//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        final View view = inflater.inflate(R.layout.fragment_index, container, false);
//        return view;
//    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    public void onClick(View v) {

    }
}
