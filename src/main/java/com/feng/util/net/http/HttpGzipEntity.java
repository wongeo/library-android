package com.feng.util.net.http;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

import org.apache.http.HttpEntity;
import org.apache.http.entity.HttpEntityWrapper;

/**
 * GZIP压缩实体（目前长度，不知道如何计算）
 * 
 * @author WangJing
 * 
 */
public class HttpGzipEntity extends HttpEntityWrapper {

	public HttpGzipEntity(HttpEntity wrapped) {
		super(wrapped);
	}

	@Override
	public InputStream getContent() throws IOException {
		return new GZIPInputStream(super.getContent());
	}
}
