package com.floyd.onebuy.view;

import android.app.Activity;
import android.view.Gravity;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.PopupWindow;

/**
 * Created by floyd on 16-8-6.
 */
public class RightTopPopupWindow extends BasePopupWindow {

    public RightTopPopupWindow(Activity context) {
        super(context);
    }

    @Override
    void setLocation(PopupWindow popupWindow) {
        popupWindow.showAtLocation(this.locationView, Gravity.RIGHT | Gravity.TOP, 0, 0);
    }

    @Override
    Animation getShowAnimation() {
        Animation a = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 1, Animation.RELATIVE_TO_SELF,
                0, Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 0);

        a.setDuration(200);
        a.setInterpolator(new AccelerateDecelerateInterpolator());
        return a;
    }

    @Override
    Animation getHiddenAnimation() {
        Animation hiddenAnimation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 1,
                Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);
        hiddenAnimation.setDuration(200);
        hiddenAnimation.setInterpolator(new AccelerateInterpolator());
        return hiddenAnimation;
    }
}
