package com.floyd.onebuy.ui.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.android.volley.toolbox.ImageLoader;
import com.floyd.onebuy.R;
import com.floyd.onebuy.ui.DialogCreator;
import com.floyd.onebuy.ui.ImageLoaderFactory;
import com.floyd.onebuy.ui.loading.DataLoadingView;
import com.floyd.onebuy.ui.loading.DefaultDataLoadingView;
import com.floyd.pullrefresh.widget.PullToRefreshBase;
import com.floyd.pullrefresh.widget.PullToRefreshListView;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link IndexFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class IndexFragment extends BackHandledFragment implements View.OnClickListener {

    private static final String TAG = "IndexFragment";
    public static final int MIN_JULI = 600;
    public static final int MIN_VELOCITY_X = 130;

    private static int BANNER_HEIGHT_IN_DP = 300;
    public static final int CHANGE_BANNER_HANDLER_MSG_WHAT = 51;

    private PullToRefreshListView mPullToRefreshListView;
    private View mTitleLayout;
    private ListView mListView;


    private boolean isShowBanner;

    private DataLoadingView dataLoadingView;

    private Dialog loadDialog;

    private int moteType = 1;
    private int pageNo = 1;
    private int PAGE_SIZE = 18;

    private boolean needClear;

    private ImageLoader mImageLoader;


    public static IndexFragment newInstance(String param1, String param2) {
        IndexFragment fragment = new IndexFragment();
        return fragment;
    }

    public IndexFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mImageLoader = ImageLoaderFactory.createImageLoader();

//        EventBus.getDefault().register(this);
        loadDialog = DialogCreator.createDataLoadingDialog(this.getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_index, container, false);

        dataLoadingView = new DefaultDataLoadingView();
        dataLoadingView.initView(view, this);

        mTitleLayout = view.findViewById(R.id.title);
        mPullToRefreshListView = (PullToRefreshListView) view.findViewById(R.id.pic_list);
        mListView = mPullToRefreshListView.getRefreshableView();

        initListViewHeader();

        initButton();

        loadData(true);

        init(view);

        mPullToRefreshListView.setMode(PullToRefreshBase.Mode.PULL_UP_TO_REFRESH);
        mPullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2() {
            @Override
            public void onPullDownToRefresh() {
                needClear = false;
                mPullToRefreshListView.onRefreshComplete(false, true);
            }

            @Override
            public void onPullUpToRefresh() {
                needClear = false;
                mPullToRefreshListView.onRefreshComplete(false, true);
            }
        });

        return view;
    }

    public void init(View view) {
    }

    private void initButton() {
    }


    private void loadData(final boolean isFirst) {
//        if (isFirst) {
//            dataLoadingView.startLoading();
//        } else {
//            loadDialog.show();
//        }
    }


    private void initListViewHeader() {


    }


    @Override
    public boolean onBackPressed() {
        return false;
    }

    public void onDestroy() {
        super.onDestroy();
//        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onClick(View v) {

    }
}
