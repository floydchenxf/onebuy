package com.yyg365.interestbar.view;

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
public class LeftDownPopupWindow extends BasePopupWindow {

    public LeftDownPopupWindow(Activity context) {
        super(context);
    }


    @Override
    void setLocation(PopupWindow popupWindow) {
        popupWindow.showAtLocation(this.locationView, Gravity.LEFT | Gravity.BOTTOM, 0, 0);
    }

    @Override
    Animation getShowAnimation() {
        Animation a = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF,
                0, Animation.RELATIVE_TO_SELF, 1,
                Animation.RELATIVE_TO_SELF, 0);

        a.setDuration(200);
        a.setInterpolator(new AccelerateDecelerateInterpolator());
        return a;
    }

    @Override
    Animation getHiddenAnimation() {
        Animation hiddenAnimation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 1);
        hiddenAnimation.setDuration(200);
        hiddenAnimation.setInterpolator(new AccelerateInterpolator());
        return hiddenAnimation;
    }
}
