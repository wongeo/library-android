package com.feng.util.net.http.proxy;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class HttpHeadersCollection {

	private String head;

	private Map<String, String> headersData;

	public HttpHeadersCollection() {
		headersData = new HashMap<String, String>();
	}

	public void addHeader(String headerName, String headerValue) {
		headersData.put(headerName, headerValue);
	}

	public boolean hasHeaderExist(String headerName) {
		return headersData.containsKey(headerName);
	}

	public String getHeaderValue(String headerName) {
		return headersData.get(headerName);
	}

	public Collection<Entry<String, String>> getHeaderPairs() {
		return headersData.entrySet();
	}

	public Collection<String> getAllHeaderKeys() {
		return headersData.keySet();
	}

	public Collection<String> getAllHeaderValues() {
		return headersData.values();
	}

	public void setHeader(String str) {
		head = str;
	}

	public String getHead() {
		return head;
	}

	@Override
	public String toString() {
		String result = head + "\n";
		Iterator<Entry<String, String>> iterator = headersData.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<String, String> pair = iterator.next();
			result += pair.getKey() + ": " + pair.getValue() + "\n";
		}
		return result;
	}
}
