package com.yyg365.pullrefresh.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ListView;

public class DummyHeadListView extends ListView {

	private View mDumyGroupView;
	private boolean mHeaderVisible = false;

	public DummyHeadListView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public DummyHeadListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void setDumyGroupView(View view) {
		mDumyGroupView = view;

		if (mDumyGroupView != null) {
			mDumyGroupView.setVisibility(View.GONE);
			setFadingEdgeLength(0);
		}
		requestLayout();
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);
		// 由于HeaderView并没有添加到ExpandableListView的子控件中，所以要draw他

//		if (mHeaderVisible) {
//			drawChild(canvas, mDumyGroupView, getDrawingTime());
//		} else {
//			if (mDumyGroupView != null) {
//				mDumyGroupView.setVisibility(View.GONE);
//			}
//		}
	}

	public void setHeaderVisible(boolean mHeaderVisible) {
		this.mHeaderVisible = mHeaderVisible;
		if (mHeaderVisible) {
			if(mDumyGroupView!=null)
			mDumyGroupView.setVisibility(View.VISIBLE);
		} else {
			if(mDumyGroupView!=null)
			mDumyGroupView.setVisibility(View.GONE);
		}
	}

	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//		if (mDumyGroupView != null) {
//			measureChild(mDumyGroupView, widthMeasureSpec, heightMeasureSpec);
//		}
	}

}
