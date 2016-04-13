package com.floyd.onebuy.ui.multiimage;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;

import java.lang.ref.WeakReference;

/**
 * An indicator of progress, similar to Android's ProgressBar. Can be used in
 * 'spin mode' or 'increment mode'
 * 
 * @author Todd Davies
 *
 *         Licensed under the Creative Commons Attribution 3.0 license see:
 *         http://creativecommons.org/licenses/by/3.0/
 */
public class ProgressWheel extends View {

	// Sizes (with defaults)
	private int fullRadius = 100;
	private int circleRadius = 80;
	private int barLength = 60;
	private int barWidth = 20;
	private int rimWidth = 20;
	private int textSize = 20;

	// Padding (with defaults)
	private int paddingTop = 0;
	private int paddingBottom = 0;
	private int paddingLeft = 0;
	private int paddingRight = 0;

	// Colors (with defaults)
	private int barColor = 0xAA000000;
	private int circleColor = 0x00000000;
	private int rimColor = 0xAADDDDDD;
	private int textColor = 0xFF000000;

	// Paints
	private Paint barPaint = new Paint();
	private Paint circlePaint = new Paint();
	private Paint rimPaint = new Paint();
	private Paint textPaint = new Paint();

	private String length;
	// Rectangles
	@SuppressWarnings("unused")
	private RectF rectBounds = new RectF();
	private RectF circleBounds = new RectF();

	// Animation
	// The amount of pixels to move the bar by on each draw
	private int spinSpeed = 2;
	// The number of milliseconds to wait inbetween each draw
	private int delayMillis = 0;

	private Handler spinHandler = new MyHandler(this);

	private static class MyHandler extends Handler {
		private final WeakReference<ProgressWheel> mReference;

		public MyHandler(ProgressWheel activity) {
			mReference = new WeakReference<ProgressWheel>(activity);
		}

		@Override
		public void handleMessage(android.os.Message msg) {
			ProgressWheel proWheel = mReference.get();
			if (proWheel == null) {
				return;
			}
			proWheel.invalidate();
			if (proWheel.isSpinning) {
				proWheel.progress += proWheel.spinSpeed;
				if (proWheel.progress > 360) {
					proWheel.progress = 0;
				}
				proWheel.spinHandler.sendEmptyMessageDelayed(0,
						proWheel.delayMillis);
			}
		}
	}

	int progress = 0;
	boolean isSpinning = false;

	// Other
	// private String text = "";
	// private String[] splitText = {};

	/**
	 * The constructor for the ProgressWheel
	 * 
	 * @param context
	 * @param attrs
	 */
	public ProgressWheel(Context context, AttributeSet attrs) {
		super(context, attrs);

//		parseAttributes();
	}

	// ----------------------------------
	// Setting up stuff
	// ----------------------------------

	/**
	 * Now we know the dimensions of the view, setup the bounds and paints
	 */
	@Override
	public void onAttachedToWindow() {
		super.onAttachedToWindow();
		setupBounds();
		setupPaints();
		invalidate();
	}

	/**
	 * Set the properties of the paints we're using to draw the progress wheel
	 */
	private void setupPaints() {
		barPaint.setColor(barColor);
		barPaint.setAntiAlias(true);
		barPaint.setStyle(Style.STROKE);
		barPaint.setStrokeWidth(barWidth);

		rimPaint.setColor(rimColor);
		rimPaint.setAntiAlias(true);
		rimPaint.setStyle(Style.STROKE);
		rimPaint.setStrokeWidth(rimWidth);

		circlePaint.setColor(circleColor);
		circlePaint.setAntiAlias(true);
		circlePaint.setStyle(Style.FILL);

		textPaint.setColor(textColor);
		textPaint.setStyle(Style.FILL);
		textPaint.setAntiAlias(true);
		textPaint.setTextSize(textSize);
	}

	/**
	 * Set the bounds of the component
	 */
	private void setupBounds() {
		paddingTop = this.getPaddingTop();
		paddingBottom = this.getPaddingBottom();
		paddingLeft = this.getPaddingLeft();
		paddingRight = this.getPaddingRight();

		rectBounds = new RectF(paddingLeft, paddingTop,
				this.getLayoutParams().width - paddingRight,
				this.getLayoutParams().height - paddingBottom);

		circleBounds = new RectF(paddingLeft + barWidth, paddingTop + barWidth,
				this.getLayoutParams().width - paddingRight - barWidth,
				this.getLayoutParams().height - paddingBottom - barWidth);

		fullRadius = (this.getLayoutParams().width - paddingRight - barWidth) / 2;
		circleRadius = (fullRadius - barWidth) + 1;
	}

	// ----------------------------------
	// Animation stuff
	// ----------------------------------

	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		// Draw the rim
		canvas.drawArc(circleBounds, 360, 360, false, rimPaint);
		// Draw the bar
		if (isSpinning) {
			canvas.drawArc(circleBounds, progress - 90, barLength, false,
					barPaint);
		} else {
			canvas.drawArc(circleBounds, -90, progress, false, barPaint);
		}
	}


	/**
	 * Set the progress to a specific value
	 */
	public void setProgress(int i) {
		isSpinning = false;
		progress = (int) (3.6 * i);
		spinHandler.sendEmptyMessage(0);
	}

	// ----------------------------------
	// Getters + setters
	// ----------------------------------

	/**
	 * Set the text in the progress bar Doesn't invalidate the view
	 * 
	 * @param text
	 *            the text to show ('\n' constitutes a new line)
	 */
	public void setText(String text) {
		// this.text = text;
		// splitText = this.text.split("\n");
	}

	public int getCircleRadius() {
		return circleRadius;
	}

	public void setCircleRadius(int circleRadius) {
		this.circleRadius = circleRadius;
	}

	public int getBarLength() {
		return barLength;
	}

	public void setBarLength(int barLength) {
		this.barLength = barLength;
	}

	public int getBarWidth() {
		return barWidth;
	}

	public void setBarWidth(int barWidth) {
		this.barWidth = barWidth;
	}

	public int getTextSize() {
		return textSize;
	}

	public void setTextSize(int textSize) {
		this.textSize = textSize;
	}

	public int getPaddingTop() {
		return paddingTop;
	}

	public void setPaddingTop(int paddingTop) {
		this.paddingTop = paddingTop;
	}

	public int getPaddingBottom() {
		return paddingBottom;
	}

	public void setPaddingBottom(int paddingBottom) {
		this.paddingBottom = paddingBottom;
	}

	public int getPaddingLeft() {
		return paddingLeft;
	}

	public void setPaddingLeft(int paddingLeft) {
		this.paddingLeft = paddingLeft;
	}

	public int getPaddingRight() {
		return paddingRight;
	}

	public void setPaddingRight(int paddingRight) {
		this.paddingRight = paddingRight;
	}

	public int getBarColor() {
		return barColor;
	}

	public void setBarColor(int barColor) {
		this.barColor = barColor;
	}

	public int getCircleColor() {
		return circleColor;
	}

	public void setCircleColor(int circleColor) {
		this.circleColor = circleColor;
	}

	public int getRimColor() {
		return rimColor;
	}

	public void setRimColor(int rimColor) {
		this.rimColor = rimColor;
	}

	public Shader getRimShader() {
		return rimPaint.getShader();
	}

	public void setRimShader(Shader shader) {
		this.rimPaint.setShader(shader);
	}

	public int getTextColor() {
		return textColor;
	}

	public void setTextColor(int textColor) {
		this.textColor = textColor;
	}

	public int getSpinSpeed() {
		return spinSpeed;
	}

	public void setSpinSpeed(int spinSpeed) {
		this.spinSpeed = spinSpeed;
	}

	public int getRimWidth() {
		return rimWidth;
	}

	public void setRimWidth(int rimWidth) {
		this.rimWidth = rimWidth;
	}

	public int getDelayMillis() {
		return delayMillis;
	}

	public void setDelayMillis(int delayMillis) {
		this.delayMillis = delayMillis;
	}

	public String getLength() {
		return length;
	}

	public void setLength(String length) {
		this.length = length;
	}
}
