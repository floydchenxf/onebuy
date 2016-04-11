package com.floyd.onebuy.ui.fragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.toolbox.ImageLoader;
import com.floyd.onebuy.R;
import com.floyd.onebuy.ui.DialogCreator;
import com.floyd.onebuy.ui.ImageLoaderFactory;
import com.floyd.onebuy.ui.loading.DataLoadingView;
import com.floyd.onebuy.ui.loading.DefaultDataLoadingView;

public class MyFragment extends BackHandledFragment implements View.OnClickListener {

    private static final String TAG = "MyFragment";

    private static final int CODE_GALLERY_REQUEST = 80;
    private static final int CROP_PICTURE_REQUEST = 81;


    private ProgressDialog avatorDialog;

    private ImageLoader mImageLoader;

    private DataLoadingView dataLoadingView;
    private Dialog dataLoadingDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mImageLoader = ImageLoaderFactory.createImageLoader();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my, container, false);

        dataLoadingView = new DefaultDataLoadingView();
        dataLoadingView.initView(view, this);
        dataLoadingDialog = DialogCreator.createDataLoadingDialog(this.getActivity());

        loadData(true, true);
        return view;
    }

    public void onResume() {
        super.onResume();
        loadData(false, false);
    }

    private void loadData(final boolean needDialog, final boolean isFirst) {
//        if (needDialog) {
//            if (isFirst) {
//                dataLoadingView.startLoading();
//            } else {
//                dataLoadingDialog.show();
//            }
//        }
    }

    @Override
    public boolean onBackPressed() {
        return true;
    }

    @Override
    public void onClick(View v) {

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    }
}
