package com.floyd.onebuy.ui.multiimage;

import android.graphics.Bitmap;

import com.floyd.onebuy.ui.multiimage.gif.GifFrame;

import java.util.List;

/**
 * 大图加载接口类
 * @author wb-jiangxiang
 *
 */
public interface ILoadBigImageView {

	/**
	 * 加载出普通图片
	 * @param bitmap
	 */
	public void onLoadImage(Bitmap bitmap, String url, int type);
	
	/**
	 * 加载出gif图片
	 * @param gifs
	 * @param data 需要用于保存的图片流
	 */
	public void onLoadGif(List<GifFrame> gifs, byte[] data, String url, int type);
	
	/**
	 * 通知当前加载进度
	 * @param progress
	 */
	public void notfiyProgress(int progress, String url);
	
	/**
	 * 通知图片加载异常
	 */
	public void notifyError(String url, int type);
}
