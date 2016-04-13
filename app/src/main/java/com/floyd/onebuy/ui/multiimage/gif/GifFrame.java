package com.floyd.onebuy.ui.multiimage.gif;

import android.graphics.Bitmap;

public class GifFrame {
	
	private transient Bitmap image;
	private int delay;

	public GifFrame(Bitmap bitmap, int delay){
		this.image = bitmap;
		
		this.delay = delay;
	}
	 
	public Bitmap getImage() {
		return image;
	}

	public void setImage(Bitmap image) {
		this.image = image;
	}

	public int getDelay() {
		return delay;
	}

	public void setDelay(int delay) {
		this.delay = delay;
	}
}
