package com.floyd.onebuy.ui.loading;

import android.content.Context;
import android.text.Html;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.floyd.onebuy.R;
import com.floyd.onebuy.ui.anim.LsLoadingView;


/**
 * Created by floyd on 15-12-24.
 */
public class DefaultDataLoadingView implements DataLoadingView {

    private TextView mActLsFailTv;

    private View mActLsFailLayoutView;

    private FrameLayout mActLsloading;

    private LsLoadingView mLsLoadingView;

    private View mLoading_container;

    private Context mContext;

    public void initView(View view, View.OnClickListener onClickListener) {

        if (mActLsloading == null) {
            mActLsloading = (FrameLayout) view.findViewById(R.id.act_lsloading);
        }
        //一些错误和空页面
        if (mActLsFailLayoutView == null) {
            mActLsFailLayoutView = view.findViewById(R.id.act_ls_fail_layout);
            mActLsFailLayoutView.setOnClickListener(onClickListener);
            mActLsFailLayoutView.setVisibility(View.GONE);
        }

        if (mActLsFailTv == null) {
            mActLsFailTv = (TextView) view.findViewById(R.id.act_ls_fail_tv);
            mActLsFailTv.setText(Html.fromHtml("页面太调皮，跑丢了...<br>请<font color='#1fb4fc'>刷新</font>再试试吧^^"));
        }


        mLsLoadingView = (LsLoadingView) view.findViewById(R.id.ls_loading_image);
        mLoading_container = view.findViewById(R.id.loading_container);
        if (mLoading_container != null) {
            mLoading_container.setVisibility(View.GONE);
        }

        mActLsloading.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

    }

    /**
     * 数据加载成功
     */
    public void loadSuccess() {
        mActLsloading.setVisibility(View.GONE);
        mActLsFailLayoutView.setVisibility(View.GONE);
        stopLoading();
    }

    /**
     * 数据加载失败
     */
    public void loadFail() {
        mActLsloading.setVisibility(View.VISIBLE);
        mActLsFailLayoutView.setVisibility(View.VISIBLE);
        stopLoading();
    }

    /**
     * 开始显示加载动画
     */
    public void startLoading() {
        if (mActLsloading != null) {
            mActLsloading.setVisibility(View.VISIBLE);
            mLoading_container.setVisibility(View.VISIBLE);
            mLsLoadingView.startAnimation();
            mActLsFailLayoutView.setVisibility(View.GONE);
        }
    }

    /**
     * 结束显示加载动画
     */
    private void stopLoading() {
        if (mActLsloading != null) {
            mLoading_container.setVisibility(View.GONE);
            mLsLoadingView.stopAnimation();
        }
    }

}
