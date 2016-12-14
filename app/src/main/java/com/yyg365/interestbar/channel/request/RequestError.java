/**
 *
 */
package com.yyg365.interestbar.channel.request;

/**
 * 执行请求的错误包装对象，包含原始Exception和错误描述的{@link ApiErrorInfo}
 * 
 * @author floydchenxf
 */
public class RequestError {

	private Exception exception;
	private ApiErrorInfo errorInfo;

	public RequestError(ApiErrorInfo errorInfo) {
		this.errorInfo = errorInfo;
	}

	public RequestError(Exception exception) {
		this.exception = exception;
	}

	public RequestError(Exception exception, ApiErrorInfo errorInfo) {
		this.exception = exception;
		this.errorInfo = errorInfo;
	}

	public Exception getException() {
		return exception;
	}

	public void setException(Exception exception) {
		this.exception = exception;
	}

	public ApiErrorInfo getErrorInfo() {
		return errorInfo;
	}

	public void setErrorInfo(ApiErrorInfo errorInfo) {
		this.errorInfo = errorInfo;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "RequestError [exception=" + exception + ", errorInfo="
				+ errorInfo + "]";
	}

}
