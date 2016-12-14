package com.yyg365.interestbar.channel.request;


public class ProgressCallback implements RequestCallback {
	
	private RequestCallback callback;
	
	private int lastProgress;
	private int degree;
	
	public ProgressCallback(RequestCallback callback) {
		this.callback = callback;
	}

	@Override
	public void onSuccess(Object... result) {
		if (callback != null) {
			callback.onSuccess(result);
		}
	}

	@Override
	public void onError(int code, String info) {
		if (callback != null) {
			callback.onError(code, info);
		}
	}

	@Override
	public void onProgress(int progress) {
		if (callback != null) {
			degree = progress - lastProgress;
			//最多20次处理完
			if (progress == 0 || degree >= 5 || progress >= 99) {
				callback.onProgress(progress);
				lastProgress = progress;
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
				}
			}
		}
	}

}
