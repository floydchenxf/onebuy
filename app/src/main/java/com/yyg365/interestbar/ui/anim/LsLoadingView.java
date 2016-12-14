/**
 * @author xianzhen<wujun.wuj@taobao.com>
 */

package com.yyg365.interestbar.ui.anim;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.graphics.drawable.shapes.Shape;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;

import com.yyg365.interestbar.utils.Tools;

import java.util.ArrayList;

public class LsLoadingView extends View implements
		ValueAnimator.AnimatorUpdateListener {

	private static final int BALLOFFSET = 9;
	
	private static final int ANGLE_OFFSET = 40;
	
	private static final int BALL_BETWEEN = 8; //dip
	private static final int BALL_SIZE = 5; //dip
	
	private static final int DEFAULT_HEIGHT = 50; //dp

	public final ArrayList<ShapeHolder> balls = new ArrayList<ShapeHolder>();
	Animator animation = null;
	
    public LsLoadingView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        initBalls();
    }

	public LsLoadingView(Context context) {
		super(context);
		initBalls();
	}
	
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
         
        int height = measureDimension(Tools.dip2px(getContext(), DEFAULT_HEIGHT), heightMeasureSpec);
         
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), height);
    }
    
    protected int measureDimension( int defaultSize, int measureSpec ) {
        
        int result = defaultSize;
         
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
                 
        //1. layout给出了确定的值，比如：100dp
        //2. layout使用的是match_parent，但父控件的size已经可以确定了，比如设置的是具体的值或者match_parent
        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize; //建议：result直接使用确定值
        } 
        //1. layout使用的是wrap_content
        //2. layout使用的是match_parent,但父控件使用的是确定的值或者wrap_content
        else if (specMode == MeasureSpec.AT_MOST) {
            result = Math.min(defaultSize, specSize); //建议：result不能大于specSize
        } 
        //UNSPECIFIED,没有任何限制，所以可以设置任何大小
        //多半出现在自定义的父控件的情况下，期望由自控件自行决定大小
        else {      
            result = defaultSize; 
        }
         
        return result;
    }

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		//super.onLayout(changed, left, top, right, bottom);
		ShapeHolder ball0 = balls.get(0);
		ShapeHolder ball1 = balls.get(1);
		ShapeHolder ball2 = balls.get(2);
		ShapeHolder ball3 = balls.get(3);
		ShapeHolder ball4 = balls.get(4);
		
		int between = Tools.dip2px(getContext(), BALL_BETWEEN);
//		WxLog.d("animation", "ypos:"+getHeight()/2);
		
		int pos0 = (right - left - 4*between)/2;
		ball0.setX(pos0);
		ball1.setX(pos0 + between);
		ball2.setX(pos0 + 2*between);
		ball3.setX(pos0 + 3*between);
		ball4.setX(pos0 + 4*between);
	}

	private void initBalls(){
		int balloffset = Tools.dip2px(getContext(), BALL_BETWEEN);
		
		addBall(0, 0, 0);
		addBall(balloffset, 0, ANGLE_OFFSET);
		addBall(2*balloffset, 0, ANGLE_OFFSET*2);
		addBall(3*balloffset, 0, ANGLE_OFFSET*3);
		addBall(4*balloffset, 0, ANGLE_OFFSET*4);
	}

	private void createAnimation() {
		if (animation == null) {
			ValueAnimator anim = ValueAnimator.ofInt(0, 360 + ANGLE_OFFSET * 4 + 60);
			anim.setInterpolator(new LinearInterpolator());
			anim.setDuration(2000);
			anim.setRepeatCount(Animation.INFINITE);
			anim.addUpdateListener(this);

			animation = anim;
		}
	}
	
	public void stopAnimation(){
		if(animation != null && animation.isRunning()){
			animation.cancel();
		}
	}

	public void startAnimation() {
		createAnimation();
		animation.start();
	}

	private ShapeHolder createBall(float x, float y) {
		OvalShape circle = new OvalShape();
		
		float size = Tools.dip2px(getContext(), BALL_SIZE);
		circle.resize(size, size);
		ShapeDrawable drawable = new ShapeDrawable(circle);
		ShapeHolder shapeHolder = new ShapeHolder(drawable);
		shapeHolder.setX(x);
		shapeHolder.setY(y);
		return shapeHolder;
	}

	private void addBall(float x, float y, int angle) {
		ShapeHolder shapeHolder = createBall(x, y);
		shapeHolder.setAngle(angle);
		shapeHolder.setColor(0x33000000);
		balls.add(shapeHolder);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		for (ShapeHolder ball : balls) {
			canvas.translate(ball.getX(), ball.getY());
			ball.getShape().setAlpha((int) (255*0.8*ball.getAlpha() + 255*0.2));
			ball.getShape().draw(canvas);
			canvas.translate(-ball.getX(), -ball.getY());
		}
	}
	
	private float getAlpha(int angle){
		if(angle < 90 && angle >= 0){
			return (float)(angle)/90;
		}else if(angle >= 270 && angle < 360){
			return (float)(360-angle)/90;
		}else if(angle >= 360){
			return 0f;
		}else{
			return 1f;
		}
	}

	public void onAnimationUpdate(ValueAnimator animation) {

		invalidate();
		ShapeHolder ball0 = balls.get(0);
		ShapeHolder ball1 = balls.get(1);
		ShapeHolder ball2 = balls.get(2);
		ShapeHolder ball3 = balls.get(3);
		ShapeHolder ball4 = balls.get(4);

		int offsety = getHeight()/2;
		
		int balloffset = Tools.dip2px(getContext(), BALLOFFSET);

		int value = (Integer) animation.getAnimatedValue();
		
		int angle = value>360?360:value;
		
		int dy0 = (int) (balloffset * Math.sin(angle * Math.PI / 180));
		ball0.setY(dy0+offsety);
		ball0.setAlpha(getAlpha(angle));

		angle = (int) (value - ball1.getAngle() > 0 ? Math.min(value - ball1.getAngle(), 360) : 0);

		int dy1 = (int) (balloffset * Math.sin(angle * Math.PI / 180));
		ball1.setY(dy1+offsety);
		ball1.setAlpha(getAlpha(angle));
//		WxLog.d("animation", "ball1 y:"+dy1+ball0.getY()+ " dy1:"+dy1+" base:"+ball1.getY());

		angle = (int) (value - ball2.getAngle() > 0 ? Math.min(value - ball2.getAngle(), 360) : 0);

		int dy2 = (int) (balloffset * Math.sin(angle * Math.PI / 180));
		ball2.setY(dy2+offsety);
		ball2.setAlpha(getAlpha(angle));

		
		angle = (int) (value - ball3.getAngle() > 0 ? Math.min(value - ball3.getAngle(), 360) : 0);
		int dy3 = (int) (balloffset * Math.sin(angle * Math.PI / 180));
		ball3.setY(dy3+offsety);
		ball3.setAlpha(getAlpha(angle));

		angle = (int) (value - ball4.getAngle() > 0 ? Math.min(value - ball4.getAngle(), 360) : 0);
		int dy4 = (int) (balloffset * Math.sin(angle * Math.PI / 180));
		ball4.setY(dy4+offsety);
		ball4.setAlpha(getAlpha(angle));
	}
	
	class ShapeHolder {
	    private float x = 0, y = 0;
	    private ShapeDrawable shape;
	    private int color;
	    private Paint paint;
	    private float alpha = 1f;
	    private int angle  = 0;

	    public void setPaint(Paint value) {
	        paint = value;
	    }
	    public Paint getPaint() {
	        return paint;
	    }

	    public void setX(float value) {
	        x = value;
	    }
	    public float getX() {
	        return x;
	    }
	    public void setY(float value) {
	        y = value;
	    }
	    public float getY() {
	        return y;
	    }
	    public void setShape(ShapeDrawable value) {
	        shape = value;
	    }
	    public ShapeDrawable getShape() {
	        return shape;
	    }
	    public int getColor() {
	        return color;
	    }
	    public void setColor(int value) {
	        shape.getPaint().setColor(value);
	        color = value;
	    }

	    public float getWidth() {
	        return shape.getShape().getWidth();
	    }
	    public void setWidth(float width) {
	        Shape s = shape.getShape();
	        s.resize(width, s.getHeight());
	    }

	    public float getHeight() {
	        return shape.getShape().getHeight();
	    }
	    public void setHeight(float height) {
	        Shape s = shape.getShape();
	        s.resize(s.getWidth(), height);
	    }

	    public ShapeHolder(ShapeDrawable s) {
	        shape = s;
	    }
		public double getAngle() {
			return angle;
		}
		public void setAngle(int angle) {
			this.angle = angle;
		}
		public float getAlpha() {
			return alpha;
		}
		public void setAlpha(float alpha) {
			this.alpha = alpha;
		}
	}
}