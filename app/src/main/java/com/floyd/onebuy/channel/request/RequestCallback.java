package com.floyd.onebuy.channel.request;

public interface RequestCallback {

	void onProgress(int progress);

	/**
	 * 异步调用成功
	 * 
	 * @param result
	 *            结果
	 */
	public <T> void onSuccess(T... result);

	/**
	 * 异步调用失败
	 * 
	 * @param code
	 *            错误值
	 * @param info
	 *            错误信息
	 */
	public void onError(int code, String info);

}
