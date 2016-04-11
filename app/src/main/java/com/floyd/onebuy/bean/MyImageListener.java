package com.floyd.onebuy.bean;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;
import android.widget.ImageView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;

/**
 * Created by Administrator on 2015/12/5.
 */
public class MyImageListener implements ImageLoader.ImageListener {
    private View view;
    private Context context;

    public MyImageListener(View view, Context context) {
        this.view = view;
        this.context = context;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
        Bitmap bitmap = response.getBitmap();

        if (view != null && view instanceof ImageView) {
            ((ImageView) view).setImageBitmap(bitmap);
        } else {
            Drawable drawable = new BitmapDrawable(context.getResources(), bitmap);
            view.setBackground(drawable);
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {

    }

}
