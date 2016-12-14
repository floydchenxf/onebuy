package com.yyg365.interestbar.ui.multiimage;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.yyg365.interestbar.ui.multiimage.gif.GifFrame;

import java.util.List;

public class MutliImageGifView extends ImageView {
	
	private List<GifFrame> frames;
	private Handler handler = new Handler();
	private int index;
	private int size;
	private boolean isGif;
	private boolean isFirst;
	
	public MutliImageGifView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MutliImageGifView(Context context) {
		super(context);
	}
	
	public MutliImageGifView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	public void setFrames(List<GifFrame> frames) {
		if (frames != null) {
			size = frames.size();
		}
		if (frames != null && !frames.equals(this.frames)) {
			index = 0;
		}
		this.frames = frames;
	}

	public void startPlay() {
		if (frames != null && frames.size() > 0) {
			handler.removeCallbacks(myRunnable);
			if (index >= size) {
				index = 0;
			}
			GifFrame frame = frames.get(index);
			if (frame != null) {
				isGif = false;
				isFirst = true;
				Bitmap bitmap = frame.getImage();
				setImageBitmap(bitmap);
				isFirst = false;
			}
			if (size > 1) {	// 只有一帧的gif没必要播放
				handler.post(myRunnable);
			}
		}

	}
	
	private Runnable myRunnable = new Runnable() {

		@Override
		public void run() {
			if (size > 0 && frames != null && size == frames.size()) {
				index = index % size;
				GifFrame frame = frames.get(index);
				if (frame != null) {
					isGif = true;
					setImageBitmap(frame.getImage());
					handler.postDelayed(myRunnable, frame.getDelay());
					index++;
				}
			}else if(size == 1){
				GifFrame frame = frames.get(0);
				if (frame != null) {
					isGif = true;
					setImageBitmap(frame.getImage());
				}
			}

		}
	};
	
	final private boolean clearGif(){
		if(isGif){
			isGif = false;
			return false;
		}else{
			if(!isFirst){
				frames = null;
				index = 0;
			}
			return true;
		}
	}

	@Override
	public void setImageBitmap(Bitmap bm) {
		clearGif();
		super.setImageBitmap(bm);
	}

	public void recycle() {
		setImageBitmap(null);
		if(frames != null){
			for(GifFrame frame : frames){
				Bitmap bmp = frame.getImage();
				if(bmp != null && !bmp.isRecycled()){
					bmp.recycle();
				}
			}
		}
		clearGif();
	}
	
	public void stopPlay(){
		if(handler != null){
			handler.removeCallbacks(myRunnable);
		}
	}
}
