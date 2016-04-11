package com.floyd.onebuy.ui.webview;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 页面中间loading 页面
 */
public class WebWaitingView extends RelativeLayout {
    private static final int LOADING_BG_ID = 101;
    private static final int LOADING_PGBAR_ID = 102;

	public WebWaitingView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public WebWaitingView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public WebWaitingView(Context context) {
		super(context);
		init(context);
	}

    @TargetApi(16)
	private void init(Context context) {
        final float scale = context.getResources().getDisplayMetrics().density;
        float cradius = scale * 20;
        View bgView = new View(context);
        bgView.setId(LOADING_BG_ID);
        GradientDrawable gd = new GradientDrawable();
        gd.setCornerRadius(cradius);
        gd.setColor(Color.DKGRAY);
        gd.setAlpha(150);
        if(android.os.Build.VERSION.SDK_INT >= 16){
            bgView.setBackground(gd);
        }else {
            bgView.setBackgroundDrawable(gd);
        }

        int size = (int)(120 * scale);
        LayoutParams param1 = new LayoutParams(size, size);
        param1.addRule(RelativeLayout.CENTER_IN_PARENT);
        this.addView(bgView, param1);
//		this.setClickable(true);

		// 采用原生ProgressBar
		ProgressBar progressBar = new ProgressBar(context);
        progressBar.setId(LOADING_PGBAR_ID);
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        params.addRule(RelativeLayout.ALIGN_TOP, LOADING_BG_ID);
        params.topMargin = 10 + (int)(20 * scale);
		this.addView(progressBar, params);

		TextView txt = new TextView(context);
		txt.setText("正在加载");
		txt.setTextColor(Color.WHITE);
		LayoutParams paramTxt = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        paramTxt.addRule(RelativeLayout.CENTER_HORIZONTAL);
        paramTxt.addRule(RelativeLayout.BELOW, LOADING_PGBAR_ID);
		this.addView(txt, paramTxt);
	}
}
