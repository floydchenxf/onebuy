/**
 * Copyright 2013 Alibaba.com All right reserved.
 * This software is the confidential and proprietary information of Alibaba.com ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement you entered into with Alibaba.com.
 *
 * @(#) AbstractRequest.java
 */
package com.floyd.onebuy.channel.request;

import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.util.Map;


/**
 * 同步的http请求器,如无其他需求，请使用{@link AbstractRequest#execute()} 方法<br>
 *
 * @author floydchenxf
 */
public abstract class AbstractRequest extends AbstractWebUtils {
	
    private static final String sTAG = "AbstractRequest";

    protected static int SO_CONNECT_TIMEOUT = 30000;//10 SECONDS
    protected static int SO_READ_TIMEOUT = 30000;//30 SECONDS

    protected String url;
    protected HttpMethod httpMethod;
    protected Map<String, String> headers;
    protected Map<String, String> parameters;
    protected Map<String, FileItem> attachments;

    protected Handler callbackHandler;
    protected RequestCallback callback;
    protected Object requestFlag;
    
    protected int progressType;
    
    protected int retryNum = 0;
    
    protected AbstractRequest() {

    }

    /**
     * @param url
     * @param parameters
     * @param httpMethod
     */
    public AbstractRequest(String url, Map<String, String> parameters, HttpMethod httpMethod) {
        this(url, null, parameters, null,httpMethod, null, null);
    }

    /**
     * @param url
     * @param headers
     * @param parameters
     * @param httpMethod
     */
    public AbstractRequest(String url, Map<String, String> headers, Map<String, String> parameters,HttpMethod httpMethod) {
        this(url, headers, parameters, null,httpMethod, null, null);
    }

    /**
     * 发起常规请求对象
     *
     * @param url         不能为空
     * @param headers
     * @param parameters
     * @param attachments
     * @param httpMethod
     * @param callback
     * @param requestFlag  透传给{@code callback}的透传参数，当多个request共用一个callback的时候，可以用此来标记不同对象发起的request，可以为null
     */
    public AbstractRequest(String url, Map<String, String> headers, Map<String, String> parameters,Map<String, FileItem> attachments,
    							HttpMethod httpMethod, RequestCallback callback, Object requestFlag) {
        if (TextUtils.isEmpty(url)) {
            throw new IllegalArgumentException("url must not empty");
        }
        this.url = url;
        this.headers = headers;
        this.parameters = parameters;
        this.attachments =attachments;
        this.callback = callback;
        this.requestFlag = requestFlag;
        this.httpMethod = httpMethod;
    }

    /**
     * 如果需要在请求中添加基本参数，子类可以覆写
     *
     * @return
     */
    protected AbstractRequest decorateRequest() {
        return this;
    }
    
    public void setProcessType(int progressType) {
    	this.progressType = progressType;
    }

    /**
     * 发起同步api调用<br/>
     * 否则直接返回{@code Response}对象，Response中对于是否有错误仅判断http response code 是否为200或者请求过程中是否有异常抛出。<br>
     * 这里不包含任何业务判断，仅对请求协议是否正常执行做出判断，如有特殊需求，请在子类中处理<br>
     *
     * @return 返回原始的响应内容和响应头信息,如果包含http错误，则{@link Response#isSuccess()} 为 false
     */
    public Response execute() {
        Response response = null;
        try {
            decorateRequest();
            switch (getHttpMethod()) {
                case POST:
                    response = doPost(url, parameters, headers,attachments,SO_CONNECT_TIMEOUT, SO_READ_TIMEOUT);
                    break;
                case GET:
                    response = doGet(url, parameters, headers, DEFAULT_CHARSET, SO_CONNECT_TIMEOUT, SO_READ_TIMEOUT);
                    break;
                default:
                    return response;
            }
			Log.i(sTAG, "url:" + url + "-------response:" + response + "---params:"+parameters);
            if (response == null) {
                throw new IOException("response is null");
            }

        } catch (Exception e1) {
            String message = e1.getMessage() == null ? "null" : e1.getMessage();
            Log.e(sTAG, message);
			boolean isRetry = isRetryException(e1);
			if (isRetry && retryNum++ < 2) {
				Log.i(sTAG, "url:" + url + "-------retry: " + retryNum);
				response = execute();
				return response;
			} else {
				response = processExceptionResponse(response, e1);
			}
        }

        processResponse(response);
        if (this.callback != null) {
            final Response resp = response;
            final AbstractRequest req = this;
            // do callback
            Handler callbackHandler = req.getCallbackHandler();
            if (callbackHandler == null) {
                // Run on this thread.
            	doCallback(resp);
            } else {
                // Post to the handler.
                callbackHandler.post(new Runnable() {
                    public void run() {
                    	doCallback(resp);
                    }
                });
            }
        }

        return response;
    }
    
    protected void processResponse(Response response) {
		//处理response,用于重载
	}
    
    protected boolean isRetryException(Exception e) {
		return false;
	}
    
    protected Response processExceptionResponse(Response resp, Exception e) {
    	if (resp == null) {
    		resp = Response.fromError(e, null);
    	} else {
    		resp.setRequestError(new RequestError(e, null));
    	}
    	
    	return resp;
    }

	public void doCallback(Response resp) {
    	if (resp.isSuccess()) {
			Log.i(sTAG, "url:" + url + " call success:" + retryNum);
    		byte[] t = resp.getContent();
    		this.getCallback().onSuccess(t);
    	} else {
			Log.i(sTAG, "url:" + url + " call fails:" + retryNum);
    		RequestError requestError = resp.getRequestError();
    		ApiErrorInfo apiErrorInfo = requestError.getErrorInfo();
    		Exception e = requestError.getException();
    		if (apiErrorInfo != null) {
    			int code = apiErrorInfo.getErrorCode();
    			String info = apiErrorInfo.getErrorDesc();
    			this.getCallback().onError(code, info);
    		} else {
    			this.getCallback().onError(400, e.getMessage());
    		}
    	}
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }
    
    /**
	 * @return the attachments
	 */
	public Map<String, FileItem> getAttachments() {
		return attachments;
	}

	/**
	 * @param attachments the attachments to set
	 */
	public void setAttachments(Map<String, FileItem> attachments) {
		this.attachments = attachments;
	}

	public RequestCallback getCallback() {
        return callback;
    }

    public void setCallback(RequestCallback callback) {
        this.callback = callback;
    }

    public Object getRequestFlag() {
        return requestFlag;
    }

    public void setRequestFlag(Object requestFlag) {
        this.requestFlag = requestFlag;
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(HttpMethod httpMethod) {
        this.httpMethod = httpMethod;
    }

    public Handler getCallbackHandler() {
        return callbackHandler;
    }

    public void setCallbackHandler(Handler callbackHandler) {
        this.callbackHandler = callbackHandler;
    }
    
	public void invokeProgress(int progressType, int progress) {
		if (this.callback == null) {
			return;
		}
		
		if (this.progressType == ALL_PROGRESS) {
			invokeProgress(progress);
		} else {
			if (this.progressType == progressType) {
				invokeProgress(progress);
			}
		}
	}
	
	protected void invokeProgress(final int progress) {
		Handler callbackHandler = this.getCallbackHandler();
		if (callbackHandler == null) {
			this.callback.onProgress(progress);
		} else {
			// Post to the handler.
			callbackHandler.post(new Runnable() {
				public void run() {
					AbstractRequest.this.callback.onProgress(progress);
				}
			});
		}
	}
}
