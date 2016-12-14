package com.yyg365.interestbar.ui.multiimage;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.ImageView;

@TargetApi(Build.VERSION_CODES.FROYO)
public class TouchImageView extends ImageView {

	Matrix matrix;

	static final int NONE = 0;
	static final int DRAG = 1;
	static final int ZOOM = 2;
	int mode = NONE;

	PointF last = new PointF();
	PointF start = new PointF();
	float minScale = 1f;
	float maxScale = 3f;
	float[] m;

	int viewWidth, viewHeight;
	static final int CLICK = 3;
	float saveScale = 1f;
	protected float origWidth, origHeight;
	int oldMeasuredWidth, oldMeasuredHeight;

	ScaleGestureDetector mScaleDetector;

	Context context;

	private GestureDetector gestureScanner;
	private OnImageTouchListener mCallback;

	public TouchImageView(Context context) {
		super(context);
		sharedConstructing(context);
	}

	public TouchImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		sharedConstructing(context);
	}
	
	@TargetApi(Build.VERSION_CODES.FROYO)
	private void sharedConstructing(Context context) {
		super.setClickable(true);
		this.context = context;
		mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
		gestureScanner = new GestureDetector(context , new MySimpleGesture());
		gestureScanner.setIsLongpressEnabled(true);
		matrix = new Matrix();
		m = new float[9];
		setImageMatrix(matrix);
		setScaleType(ScaleType.MATRIX);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		gestureScanner.onTouchEvent(event);
		mScaleDetector.onTouchEvent(event);
		
		PointF curr = new PointF(event.getX(), event.getY());

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			last.set(curr);
			start.set(last);
			mode = DRAG;
			break;

		case MotionEvent.ACTION_MOVE:
			if (mode == DRAG) {
				float deltaX = curr.x - last.x;
				float deltaY = curr.y - last.y;
				float fixTransX = getFixDragTrans(deltaX, viewWidth,
						origWidth * saveScale);
				float fixTransY = getFixDragTrans(deltaY, viewHeight,
						origHeight * saveScale);
				matrix.postTranslate(fixTransX, fixTransY);
				fixTrans();
				last.set(curr.x, curr.y);
			}
			break;

		case MotionEvent.ACTION_UP:
			mode = NONE;
			int xDiff = (int) Math.abs(curr.x - start.x);
			int yDiff = (int) Math.abs(curr.y - start.y);
			if (xDiff < CLICK && yDiff < CLICK)
				performClick();
			break;

		case MotionEvent.ACTION_POINTER_UP:
			mode = NONE;
			break;
		}

		setImageMatrix(matrix);
		invalidate();

		float left, right;
		if ((int) origWidth * saveScale <= viewWidth
				&& (int) origHeight * saveScale <= viewHeight) {
			getParent().requestDisallowInterceptTouchEvent(false);
		} else {
			left = m[Matrix.MTRANS_X];
			right = left + origWidth * saveScale;
			if (left > 0 || right < viewWidth) {
				getParent().requestDisallowInterceptTouchEvent(false);
			} else {
				getParent().requestDisallowInterceptTouchEvent(true);
			}
		}
		return true; 
	}

	public void setMaxZoom(float x) {
		maxScale = x;
	}

	private class ScaleListener extends
			ScaleGestureDetector.SimpleOnScaleGestureListener {
		@Override
		public boolean onScaleBegin(ScaleGestureDetector detector) {
			if(mCallback != null){
				mCallback.onScaleBegin();
			}
			mode = ZOOM;
			return true;
		}

		@Override
		public boolean onScale(ScaleGestureDetector detector) {
			float mScaleFactor = detector.getScaleFactor();
			float origScale = saveScale;
			saveScale *= mScaleFactor;
			if (saveScale > maxScale) {
				saveScale = maxScale;
				mScaleFactor = maxScale / origScale;
			} else if (saveScale < minScale) {
				saveScale = minScale;
				mScaleFactor = minScale / origScale;
			}

			if (origWidth * saveScale <= viewWidth
					|| origHeight * saveScale <= viewHeight)
				matrix.postScale(mScaleFactor, mScaleFactor, viewWidth / 2,
						viewHeight / 2);
			else
				matrix.postScale(mScaleFactor, mScaleFactor,
						detector.getFocusX(), detector.getFocusY());

			fixTrans();
			return true;
		}
	}

	void fixTrans() {
		matrix.getValues(m);
		float transX = m[Matrix.MTRANS_X];
		float transY = m[Matrix.MTRANS_Y];

		float fixTransX = getFixTrans(transX, viewWidth, origWidth * saveScale);
		float fixTransY = getFixTrans(transY, viewHeight, origHeight * saveScale);

		if (fixTransX != 0 || fixTransY != 0)
			matrix.postTranslate(fixTransX, fixTransY);
	}

	float getFixTrans(float trans, float viewSize, float contentSize) {
		float minTrans, maxTrans;

		if (contentSize <= viewSize) {
			minTrans = 0;
			maxTrans = viewSize - contentSize;
		} else {
			minTrans = viewSize - contentSize;
			maxTrans = 0;
		}

		if (trans < minTrans)
			return -trans + minTrans;
		if (trans > maxTrans)
			return -trans + maxTrans;
		return 0;
	}

	float getFixDragTrans(float delta, float viewSize, float contentSize) {
		if (contentSize <= viewSize) {
			return 0;
		}
		return delta;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		viewWidth = MeasureSpec.getSize(widthMeasureSpec);
		viewHeight = MeasureSpec.getSize(heightMeasureSpec);
		if (oldMeasuredHeight == viewWidth && oldMeasuredHeight == viewHeight
				|| viewWidth == 0 || viewHeight == 0)
			return;
		oldMeasuredHeight = viewHeight;
		oldMeasuredWidth = viewWidth;

		if (saveScale == 1f) {
			ScaleImage();
		}
		fixTrans();
	}

	private class MySimpleGesture extends SimpleOnGestureListener {

		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			if (mCallback != null && !mScaleDetector.isInProgress()) {
				mCallback.onSingleTouch();
			}
			return true;
		}

		@Override
		public boolean onDoubleTap(MotionEvent e) {
			if(!mScaleDetector.isInProgress()){
				if(saveScale != 1f){
					ScaleImage();
				}else{
					saveScale = 1.5f;
					float scale;
					Drawable drawable = getDrawable();
					if (drawable == null || drawable.getIntrinsicWidth() == 0
							|| drawable.getIntrinsicHeight() == 0)
						return false;
					int bmWidth = drawable.getIntrinsicWidth();
					int bmHeight = drawable.getIntrinsicHeight();

					float scaleX = (float)saveScale* viewWidth / (float) bmWidth;
					float scaleY = (float)saveScale* viewHeight / (float) bmHeight;
					scale = Math.min(scaleX, scaleY);
					matrix.setScale(scale, scale);

					// Center the image
					float redundantYSpace = (float) viewHeight
							- (scale * (float) bmHeight);
					float redundantXSpace = (float) viewWidth
							- (scale * (float) bmWidth);
					redundantYSpace /= (float) 2;
					redundantXSpace /= (float) 2;
					matrix.postTranslate(redundantXSpace, redundantYSpace);
					setImageMatrix(matrix);
					fixTrans();
				}
				if(mCallback != null){
					mCallback.onDoubleTap();
				}
			}
			return true;
		}

		@Override
		public void onLongPress(MotionEvent e) {
//			if(IMChannel.DEBUG){
//				WxLog.d(TouchImageView.class.getSimpleName(),  "TouchImageView ===" +"onLongPress ===" + e.getX() + "==" + e.getY());
//			}
			if (mCallback != null && !mScaleDetector.isInProgress()) {
				mCallback.onLongTouch();
			}
		}
	}

	public interface OnImageTouchListener {
		void onSingleTouch();
		void onLongTouch();
		void onScaleBegin();
		void onDoubleTap();
	}

	public void setOnImageTouchListener(OnImageTouchListener listener) {
		this.mCallback = listener;
	}
	
	public void ScaleImage() {
		// Fit to screen.
		float scale;
		saveScale = 1f;
		Drawable drawable = getDrawable();
		if (drawable == null || drawable.getIntrinsicWidth() == 0
				|| drawable.getIntrinsicHeight() == 0)
			return;
		int bmWidth = drawable.getIntrinsicWidth();
		int bmHeight = drawable.getIntrinsicHeight();
		
		float scaleX = (float) viewWidth / (float) bmWidth;
		float scaleY = (float) viewHeight / (float) bmHeight;
		scale = Math.min(scaleX, scaleY);
		matrix.setScale(scale, scale);

		// Center the image
		float redundantYSpace = (float) viewHeight
				- (scale * (float) bmHeight);
		float redundantXSpace = (float) viewWidth
				- (scale * (float) bmWidth);
		redundantYSpace /= (float) 2;
		redundantXSpace /= (float) 2;

		matrix.postTranslate(redundantXSpace, redundantYSpace);

		origWidth = viewWidth - 2 * redundantXSpace;
		origHeight = viewHeight - 2 * redundantYSpace;
		setImageMatrix(matrix);
		fixTrans();
	}
	
	public boolean isZOOMMode(){
		return saveScale != 1f;
	}
}