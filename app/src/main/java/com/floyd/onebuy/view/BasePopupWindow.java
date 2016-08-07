package com.floyd.onebuy.view;

import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.AbsListView;
import android.widget.PopupWindow;

/**
 * Created by floyd on 16-4-17.
 */
public abstract class BasePopupWindow implements View.OnClickListener {

    private PopupWindow menu;
    protected Activity context;
    private View contentView;
    private View containerView;
    protected boolean isShow;

    private Animation hiddenAnimation;
    private Animation showAnimation;

    protected View locationView;

    public void setLocationView(View locationView) {
        this.locationView = locationView;
    }

    public BasePopupWindow(Activity context) {
        this.context = context;
        this.locationView = context.getWindow().getDecorView();
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

    /**
     * 设置定位
     *
     * @param popupWindow
     */
    abstract void setLocation(PopupWindow popupWindow);

    /**
     * 设置显示动作
     *
     * @return
     */
    abstract Animation getShowAnimation();

    /**
     * 设置隐藏动作
     *
     * @return
     */
    abstract Animation getHiddenAnimation();


    public void showPopUpWindow() {
        if (menu == null) {
            throw new RuntimeException("please init view first!");
        }

        if (showAnimation == null) {
            showAnimation = getShowAnimation();
        }

        if (showAnimation != null) {
            containerView.startAnimation(showAnimation);
        }
        setLocation(menu);
        isShow = true;
    }

    public boolean hidePopUpWindow() {
        if (!context.isFinishing()) {
            if (menu != null) {

                if (hiddenAnimation == null) {
                    hiddenAnimation = getHiddenAnimation();
                }

                if (hiddenAnimation != null) {
                    hiddenAnimation.setAnimationListener(new Animation.AnimationListener() {

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

                    containerView.startAnimation(hiddenAnimation);
                }
            }
            isShow = false;
            return true;
        }
        return false;
    }

    public boolean isShow() {
        return this.isShow;
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
