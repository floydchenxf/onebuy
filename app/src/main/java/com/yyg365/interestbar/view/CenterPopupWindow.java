package com.yyg365.interestbar.view;

import android.app.Activity;
import android.view.Gravity;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.PopupWindow;

/**
 * Created by chenxiaofeng on 16/10/3.
 */
public class CenterPopupWindow extends BasePopupWindow {

    public CenterPopupWindow(Activity context) {
        super(context);
    }

    @Override
    void setLocation(PopupWindow popupWindow) {
        popupWindow.showAtLocation(this.locationView, Gravity.CENTER, 0, 0);
    }

    @Override
    Animation getShowAnimation() {
        Animation a = new ScaleAnimation(0, 1, 0, 1);
        a.setDuration(200);
        a.setInterpolator(new AccelerateDecelerateInterpolator());
        return a;
    }

    @Override
    Animation getHiddenAnimation() {
        Animation hiddenAnimation = new ScaleAnimation(
                1, 0, 1, 0);
        hiddenAnimation.setDuration(200);
        hiddenAnimation.setInterpolator(new AccelerateInterpolator());
        return hiddenAnimation;
    }
}
