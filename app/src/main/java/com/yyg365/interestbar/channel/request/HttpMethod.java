package com.yyg365.interestbar.channel.request;

/**
 * Http请求的方式
 * @author floydchenxf
 */
public enum HttpMethod {
    GET,
    POST;
    
    /**
     * @param name
     * @param defaultMethod 默认请求方式
     * @return
     */
    public static HttpMethod valueOf(String name,HttpMethod defaultMethod){
    	if(name != null){
    		if(name.equalsIgnoreCase(GET.name())){
    			return GET;
    		}
    		if(name.equalsIgnoreCase(POST.name())){
    			return POST;
    		}
    	}
    	return defaultMethod;
    }
    
}