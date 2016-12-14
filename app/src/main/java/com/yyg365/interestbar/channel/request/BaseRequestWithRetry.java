package com.yyg365.interestbar.channel.request;

import java.net.SocketTimeoutException;
import java.util.Map;

public class BaseRequestWithRetry extends BaseRequest {

	public BaseRequestWithRetry(String url, Map<String, String> parameters,
			HttpMethod httpMethod) {
		super(url, parameters, httpMethod);
	}

	public BaseRequestWithRetry(String url, Map<String, String> parameters,
			HttpMethod httpMethod, RequestCallback callback) {
		super(url, parameters, httpMethod, callback);
	}

	public BaseRequestWithRetry(String url, Map<String, String> parameters,
			Map<String, FileItem> attachments, HttpMethod httpMethod) {
		super(url, parameters, attachments, httpMethod);
	}

	public BaseRequestWithRetry(String url, Map<String, String> parameters,
			Map<String, FileItem> attachments, HttpMethod httpMethod,
			RequestCallback callback) {
		super(url, parameters, attachments, httpMethod, callback);
	}

	public BaseRequestWithRetry(String url, Map<String, String> headers,
			Map<String, String> parameters, Map<String, String> fileMap,
			HttpMethod httpMethod) {
		super(url, headers, parameters, fileMap, httpMethod, null);
	}

	public BaseRequestWithRetry(String url, Map<String, String> headers,
			Map<String, String> parameters, Map<String, String> fileMap,
			HttpMethod httpMethod, RequestCallback callback) {
		super(url, headers, parameters, fileMap, httpMethod, callback);
	}

	@Override
	protected boolean isRetryException(Exception e) {
		boolean isRetry = e instanceof SocketTimeoutException;
		return isRetry;
	}
}
