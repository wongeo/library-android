package com.feng.util.net.http.proxy;

import java.util.Map;

public class RequestParams {
	public static RequestParams parseFromUrl(String url) {
		return null;
	}
	
	private Map<String, String> params;
	
	public Map<String, String> getParams() {
		return params;
	}

	public void setParams(Map<String, String> params) {
		this.params = params;
	}
	
	public void combineWith(RequestParams target) {
		
	}
}
