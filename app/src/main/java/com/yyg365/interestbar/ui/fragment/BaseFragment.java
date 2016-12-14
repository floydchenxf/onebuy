package com.yyg365.interestbar.ui.fragment;

import android.annotation.TargetApi;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;

public abstract class BaseFragment extends Fragment {
	
	protected String mPageName;
	
	protected void createPage(String pageName) {
		this.mPageName = pageName;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	protected DisplayMetrics getDisplayMetrics(){
		DisplayMetrics dm = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
		return dm;
	}
	
	/**
	 * 设置标题栏返回按钮的默认功能 默认为finish当前activity
	 */
	protected void setBackListener(View button) {
		if (button != null) {
			button.setVisibility(View.VISIBLE);
			button.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					hideKeyBoard();
					getActivity().finish();
				}
			});
		}
	}
	
	protected void hideKeyBoard() {
		View view = getActivity().getCurrentFocus();
		if (view != null) {
			((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE))
					.hideSoftInputFromWindow(view.getWindowToken(), 0);
		}
	}
	
	/**
	 * 设置点击标题栏自动返回ListView顶部的默认功能
	 */
	protected void setBackToListTop(final AbsListView aListView ,final int viewId) {
		final View view = getView().findViewById(viewId);
		if (view != null) {
			view.setOnClickListener(new View.OnClickListener() {

				@SuppressWarnings("deprecation")
				@TargetApi(11)
				@Override
				public void onClick(View v) {
					if(aListView != null && aListView.getAdapter() != null){
						if(aListView != null && aListView.getAdapter() != null){
							aListView.setSelection(0);
						}
					}
				}
			});
		}
	}
	
	public abstract boolean onBackPressed();
	
	public abstract void onShow();

	public abstract void clearGestureLayout();
}
