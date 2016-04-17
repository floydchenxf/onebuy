package com.floyd.onebuy.view;

import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.PopupWindow;

/**
 * Created by floyd on 16-4-17.
 */
public class MyPopupWindow implements View.OnClickListener {

    private PopupWindow menu;
    private Activity context;
    private View contentView;
    private View containerView;
    private boolean isShow;

    public MyPopupWindow(Activity context) {
        this.context = context;
    }

    public void initView(int layout, ViewInit viewInit) {

        if (menu == null) {
            contentView = View.inflate(context, layout, null);
            contentView.setOnClickListener(this);
            containerView = ((ViewGroup) contentView).getChildAt(0);
            containerView.setOnClickListener(this);
            if (viewInit != null) {
                viewInit.initView(contentView);
            }
            menu = new PopupWindow(contentView, AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.MATCH_PARENT);
            menu.setBackgroundDrawable(new BitmapDrawable());
            menu.setTouchable(true);
            menu.setFocusable(true);
            menu.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
            menu.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        }
    }


    public void showPopUpWindow() {
        if (menu == null) {
            throw new RuntimeException("please init view first!");
        }

        TranslateAnimation trans = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF,
                0, Animation.RELATIVE_TO_SELF, 1,
                Animation.RELATIVE_TO_SELF, 0);

        trans.setDuration(200);
        trans.setInterpolator(new AccelerateDecelerateInterpolator());
        containerView.startAnimation(trans);
        menu.showAtLocation(context.getWindow().getDecorView(), Gravity.LEFT | Gravity.BOTTOM, 0, 0);
        isShow = true;
    }

    public boolean hidePopUpWindow() {
        if (!context.isFinishing()) {
            if (menu != null) {
                TranslateAnimation trans = new TranslateAnimation(
                        Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0,
                        Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 1);

                trans.setDuration(200);
                trans.setInterpolator(new AccelerateInterpolator());
                trans.setAnimationListener(new Animation.AnimationListener() {

                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {

                        menu.dismiss();
                    }
                });

                containerView.startAnimation(trans);
            }
            isShow = false;
            return true;
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        if (v == contentView) {
            hidePopUpWindow();
        }
    }

    public interface ViewInit {
        void initView(View v);
    }
}
