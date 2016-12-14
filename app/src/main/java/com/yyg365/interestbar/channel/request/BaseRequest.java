package com.yyg365.interestbar.channel.request;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;

public class BaseRequest extends AbstractRequest {

	private static final String USER_AGENT = "wangxin_default";

	@Override
	protected String getUserAgent() {
		return USER_AGENT;
	}

	public BaseRequest(String url, Map<String, String> parameters,
			HttpMethod httpMethod) {
		super(url, null, parameters, null, httpMethod, null, null);
	}

	public BaseRequest(String url, Map<String, String> parameters,
			HttpMethod httpMethod, RequestCallback callback) {
		super(url, null, parameters, null, httpMethod, new ProgressCallback(
				callback), null);
	}

	public BaseRequest(String url, Map<String, String> parameters,
			Map<String, FileItem> attachments, HttpMethod httpMethod) {
		super(url, null, parameters, attachments, httpMethod, null, null);
	}

	public BaseRequest(String url, Map<String, String> parameters,
			Map<String, FileItem> attachments, HttpMethod httpMethod,
			RequestCallback callback) {
		super(url, null, parameters, attachments, httpMethod,
				new ProgressCallback(callback), null);
	}

	public BaseRequest(String url, Map<String, String> headers,
			Map<String, String> parameters, Map<String, String> fileMap,
			HttpMethod httpMethod, RequestCallback callback) {
		Map<String, FileItem> attachments = new HashMap<String, FileItem>();
		if (fileMap != null && !fileMap.isEmpty()) {
			for (Map.Entry<String, String> ent : fileMap.entrySet()) {
				String key = ent.getKey();
				String value = ent.getValue();
				attachments.put(key, new FileItem(value));
			}
		}

		this.url = url;
		this.headers = headers;
		this.parameters = parameters;
		this.attachments = attachments;
		this.httpMethod = httpMethod;
		this.callback = new ProgressCallback(callback);
	}
	
	
	@Override
	protected Response processExceptionResponse(Response resp, Exception e1) {
		if (resp == null) {
			if (e1 instanceof SocketTimeoutException) {
				resp = new Response();
				ApiErrorInfo errorInfo = new ApiErrorInfo();
				errorInfo.setErrorCode(SystemError.ERROR_TIME_OUT.getCode());
				errorInfo.setErrorDesc(SystemError.ERROR_TIME_OUT.getInfo());
				resp.setRequestError(new RequestError(null, errorInfo));
			} else if (e1 instanceof IOException) {
				resp = new Response();
				ApiErrorInfo errorInfo = new ApiErrorInfo();
				errorInfo.setErrorCode(SystemError.ERROR_NETWORK_ERROR
						.getCode());
				errorInfo.setErrorDesc(SystemError.ERROR_NETWORK_ERROR
						.getInfo());
				resp.setRequestError(new RequestError(null, errorInfo));
			} else if (e1 instanceof IllegalArgumentException) {
				resp = new Response();
				ApiErrorInfo errorInfo = new ApiErrorInfo();
				errorInfo.setErrorCode(SystemError.ERROR_INVALID_PARAMS
						.getCode());
				errorInfo.setErrorDesc(SystemError.ERROR_INVALID_PARAMS
						.getInfo());
				resp.setRequestError(new RequestError(null, errorInfo));
			} else if (e1 instanceof ClientProtocolException) {
				resp = new Response();
				ApiErrorInfo errorInfo = new ApiErrorInfo();
				errorInfo.setErrorCode(SystemError.ERROR_NETWORK_ERROR
						.getCode());
				errorInfo.setErrorDesc(SystemError.ERROR_NETWORK_ERROR
						.getInfo());
				resp.setRequestError(new RequestError(null, errorInfo));
			} else {
				resp = Response.fromError(e1, null);
			}
		} else {
			resp.setRequestError(new RequestError(e1, null));
		}

		return resp;
	}

}
