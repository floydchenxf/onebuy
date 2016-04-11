package com.floyd.onebuy.channel.request;

public enum SystemError {

	Error(0, ""), ERROR_NETWORK_NULL(1, "当前没网络"), 
	ERROR_INVALID_LOGINSTATE(2, "当前用户未登陆"), ERROR_INVALID_CMDID(3, "当前请求cmdid无效"), 
	ERROR_REQ_NOT_ALLOWED(4, "当前请求不被服务端允许，需要开启相关设置"), ERROR_VALID_FAIL(5,"当前请求的认证校验失效，需重新设置"), 
	ERROR_INVALID_PARAMS(6, "当前请求的参数有误，需要重新设置参数"), ERROR_TOKEN_UNAVAIL(7, "当前请求token获取失败"), 
	ERROR_NETWORK_ERROR(8, "http异常"), ERROR_TIME_OUT(9, "请求超时"), ERROR_TOKEN_SAME(10, "多次请求的token一致，一般是账号前缀问题"), 
	ERROR_PARAM_ERR(11, "本地数据解析解析异常"), ERROR_OOM(12, "内存空间不足"), ERROR_POOL_FULL(13, "当前请求过多，线程池已满，请稍后再试"), 
	ERROR_UNPACK_ERR(254, "服务器数据解析异常"), ERROR_SERVER_ERR(255, "服务器内部错误"), ERROR_PARSER_JSON_ERR(14, "Json解析出错");

	private int code;
	private String info;

	SystemError(int code, String info) {
		this.code = code;
		this.info = info;
	}

	public int getCode() {
		return code;
	}

	public String getInfo() {
		return info;
	}

}
