/**
 *
 */
package com.floyd.onebuy.channel.request;

/**
 * 错误信息Bean
 *
 * @author floydchenxf
 */
public class ApiErrorInfo {

    private Integer errorCode;
    private String errorDesc;

    private String subCode;
    private String subDesc;

    /**
     *
     */
    public ApiErrorInfo() {
    }

    /**
     * @param errorCode
     */
    public ApiErrorInfo(int errorCode) {
        this.errorCode = errorCode;
    }

    /**
     * @param errorDesc
     */
    public ApiErrorInfo(String errorDesc) {
        super();
        this.errorDesc = errorDesc;
    }

    /**
     * @param errorCode
     * @param errorDesc
     */
    public ApiErrorInfo(int errorCode, String errorDesc) {
        this.errorCode = errorCode;
        this.errorDesc = errorDesc;
    }

    /**
     * @param requestError
     */
    public ApiErrorInfo(RequestError requestError) {
        if (requestError != null) {
            ApiErrorInfo info = requestError.getErrorInfo();
            if (info != null) {
                this.errorCode = info.errorCode;
                this.errorDesc = info.errorDesc;
            }
        }
    }

    /**
     * @return the errorCode
     */
    public Integer getErrorCode() {
        return errorCode == null ? 0 : errorCode;
    }

    /**
     * @param errorCode the errorCode to set
     */
    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }

    /**
     * @return the errorDesc
     */
    public String getErrorDesc() {
        return errorDesc;
    }

    /**
     * @param errorDesc the errorDesc to set
     */
    public void setErrorDesc(String errorDesc) {
        this.errorDesc = errorDesc;
    }

    public String getSubCode() {
        return subCode;
    }

    public void setSubCode(String subCode) {
        this.subCode = subCode;
    }

    public String getSubDesc() {
        return subDesc;
    }

    public void setSubDesc(String subDesc) {
        this.subDesc = subDesc;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("errorCode:").append(this.errorCode).append(" msg:")
                .append(this.errorDesc).append(" subCode:").append(subCode)
                .append(" subMsg:").append(this.subDesc);
        return builder.toString();
    }

}
