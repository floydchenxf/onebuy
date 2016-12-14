package com.yyg365.interestbar.ui.loading;

import android.view.View;

/**
 * Created by floyd on 15-12-24.
 */
public interface DataLoadingView {

    /**
     * 初始化
     *
     * @param view
     * @param onClickListener
     */
    void initView(View view, View.OnClickListener onClickListener);


    /**
     * load成功
     */
    void loadSuccess();

    /**
     * load失败
     */
    void loadFail();

    /**
     * 开始加载
     */
    void startLoading();

}
