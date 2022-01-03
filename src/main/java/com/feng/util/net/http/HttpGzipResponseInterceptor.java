package com.feng.util.net.http;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;

/**
 * HTTP 响应拦截器（如果返回数据为gzip，则解压并重新赋值）
 * 
 * @author WangJing
 * 
 */
public class HttpGzipResponseInterceptor implements HttpResponseInterceptor {

	@Override
	public void process(HttpResponse response, HttpContext httpContext) throws HttpException, IOException {
		HttpEntity entity = response.getEntity();
		Header ceheader = entity.getContentEncoding();
		if (ceheader != null) {
			for (HeaderElement element : ceheader.getElements()) {
				if (element.getName().equalsIgnoreCase("gzip")) {
					response.setEntity(new HttpGzipEntity(response.getEntity()));
				}
			}
		}
	}
}
