package com.yyg365.interestbar.ui.webview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * 抽象的导航 控制类
 */
public abstract class AbstractNaviBar extends RelativeLayout {
    public static final int NAVI_BAR_ID = 110;

    public AbstractNaviBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public AbstractNaviBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AbstractNaviBar(Context context) {
        super(context);
    }

    public abstract void resetState();

    public abstract void startLoading();

    public abstract void stopLoading();

}
