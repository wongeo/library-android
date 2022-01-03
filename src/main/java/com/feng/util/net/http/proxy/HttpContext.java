package com.feng.util.net.http.proxy;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import android.util.Log;

public class HttpContext {
	private Socket underlySocket;
	private RequestParams requestParams;
	private HttpHeadersCollection requestHeaders;
	private HttpHeadersCollection responseHeaders;
	private InputStream bodyStream;
	private OutputStream outputStream;
	private boolean isBindToAsynchronizeProcessor;

	public HttpContext() {

	}

	public Socket getUnderlySocket() {
		return underlySocket;
	}

	public void setUnderlySocket(Socket underlySocket) {
		this.underlySocket = underlySocket;
	}

	protected RequestParams getRequestParams() {
		return requestParams;
	}

	protected void setRequestParams(RequestParams requestParams) {
		this.requestParams = requestParams;
	}

	public HttpHeadersCollection getRequestHeaders() {
		return requestHeaders;
	}

	protected void setRequestHeaders(HttpHeadersCollection requestHeaders) {
		this.requestHeaders = requestHeaders;
	}

	protected HttpHeadersCollection getResponseHeaders() {
		return responseHeaders;
	}

	protected void setResponseHeaders(HttpHeadersCollection responseHeaders) {
		this.responseHeaders = responseHeaders;
	}

	public InputStream getBodyStream() {
		return bodyStream;
	}

	public void setBodyStream(InputStream bodyStream) {
		this.bodyStream = bodyStream;
	}

	public OutputStream getOutputStream() {
		return outputStream;
	}

	public void setOutputStream(OutputStream outputStream) {
		this.outputStream = outputStream;
	}

	public boolean isPeerRequestShutdown() {
		if (underlySocket == null) {
			return true;
		}
		return !underlySocket.isConnected() || underlySocket.isClosed() || underlySocket.isInputShutdown() || underlySocket.isOutputShutdown();
	}

	public boolean isBindToAsynchronizeProcessor() {
		return isBindToAsynchronizeProcessor;
	}

	public void setBindToAsynchronizeProcessor(boolean isBindToAsynchronizeProcessor) {
		this.isBindToAsynchronizeProcessor = isBindToAsynchronizeProcessor;
	}

	public void shutdown() {
		try {
			if (bodyStream != null) {
				bodyStream.close();
				bodyStream = null;
			}
		} catch (IOException ex) {
			Log.e("tr", ex.toString());
		}
		try {
			if (outputStream != null) {
				outputStream.close();
				outputStream = null;
			}
		} catch (IOException ex) {
			Log.e("tr", ex.toString());
		}
		try {
			if (underlySocket != null) {
				underlySocket.close();
				underlySocket = null;
			}
		} catch (IOException ex) {
			Log.e("tr", ex.toString());
		}
	}

	public boolean hasCanceled() {
		return isPeerRequestShutdown();
	}
}