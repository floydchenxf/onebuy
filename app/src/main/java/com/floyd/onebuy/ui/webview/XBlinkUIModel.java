package com.floyd.onebuy.ui.webview;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.RelativeLayout;

/**
 * 一些简易的UI组件封装
 *
 */
public class XBlinkUIModel {

    private View loadingView;// loading view
    private View errorView;// error page view
    private AbstractNaviBar naviBar = null;

    private View mView;
    private Context mContext;

    private boolean showLoading = false;// show loading before page display

    public XBlinkUIModel(Context context, View view){
        this.mContext = context;
        this.mView = view;
    }

    public void enableShowLoading(){
        showLoading = true;
    }
    public void disableShowLoading(){
        showLoading = false;
    }

    /**
     * loading view
     */
    public void showLoadingView(){
        if(showLoading){
            if(loadingView == null){
                loadingView = new WebWaitingView(mContext);
                setLoadingView(loadingView);
            }
            loadingView.bringToFront();
            if(loadingView.getVisibility() != View.VISIBLE){
                loadingView.setVisibility(View.VISIBLE);
            }
        }
    }

    public void hideLoadingView(){
        if(showLoading){
            if(loadingView != null && loadingView.getVisibility() != View.GONE){
                loadingView.setVisibility(View.GONE);
            }
        }
    }

    public void setLoadingView(View view){
        if(view != null){
            loadingView = view;
            loadingView.setVisibility(View.GONE);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
            params.addRule(RelativeLayout.CENTER_HORIZONTAL, 1);
            ViewParent parent = mView.getParent();
            if( parent != null){
                ((ViewGroup) parent).addView(loadingView, params);
            }
        }
    }

    public void setLoadingView(View view,RelativeLayout.LayoutParams params){
        if(view != null){
            loadingView = view;
            loadingView.setVisibility(View.GONE);
            
            ViewParent parent = mView.getParent();
            if( parent != null){
                ((ViewGroup) parent).addView(loadingView, params);
            }
        }
    }
    /**
     * error view
     */
    public void loadErrorPage(){
        if(errorView == null){
            errorView = new WebErrorView(mContext);
            setErrorView(errorView);
        }
        errorView.bringToFront();
        if(errorView.getVisibility() != View.VISIBLE){
            errorView.setVisibility(View.VISIBLE);
        }
    }

    public void hideErrorPage(){
        if(errorView != null && errorView.getVisibility() != View.GONE){
            errorView.setVisibility(View.GONE);
        }
    }

    public void setErrorView(View view){
        if(view != null){
            errorView = view;
            errorView.setVisibility(View.GONE);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
            params.addRule(RelativeLayout.CENTER_HORIZONTAL, 1);
            ViewParent parent = mView.getParent();
            if(parent != null){
                ((ViewGroup) parent).addView(errorView, params);
            }
        }
    }

    public View getErrorView(){
        if(errorView == null){
            setErrorView(new WebErrorView(mContext));
        }
        return errorView;
    }

    /**
     * navi bar
     * @param view
     */
    public void setNaviBar(AbstractNaviBar view){
        if(naviBar != null){
            naviBar.setVisibility(View.GONE);
            naviBar = null;
        }
        if(view != null){
            naviBar = view;
        }
    }
    public void resetNaviBar(){
        if(naviBar != null){
            naviBar.resetState();
        }
    }
    public void hideNaviBar(){
        if(naviBar != null && naviBar.getVisibility() != View.GONE){
            naviBar.setVisibility(View.GONE);
        }
    }

    /**
     * 切换状态，1开始加载，处于加载中状态；0停止加载
     * @param start
     */
    public void switchNaviBar(int start){
        if(naviBar != null && start == 1){
            naviBar.startLoading();
        }
    }

}
