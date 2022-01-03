package com.feng.util.net.http.proxy;

import java.io.OutputStream;
import java.io.PrintStream;

/**
 * 第三版流媒体代理
 * 
 * @author WangJing
 * 
 */
public class MediaProxyHandler extends AbsUriHandler {

	/** 文件块对象，用于往解码器中写入文件块 */
	private MediaFileBlock mFileBlock;

	public MediaProxyHandler() {
		mFileBlock = new MediaFileBlock();
	}

	@Override
	public boolean isAccept(String uri) {
		return true;
	}

	@Override
	public void handle(String uri, HttpContext httpContext) {
		try {
			String url = uri.substring(1);
			OutputStream out = httpContext.getOutputStream();
			PrintStream printer = new PrintStream(out);

			String contentType = getContentType(url);

			long total = mFileBlock.getTotal(url);

			HttpHeadersCollection headers = httpContext.getRequestHeaders();
			boolean hasRange = headers.hasHeaderExist("Range");
			int range = 0;
			if (hasRange) {
				String rangeStr = headers.getHeaderValue("Range");
				rangeStr = rangeStr.substring(6, rangeStr.lastIndexOf("-"));
				range = Integer.parseInt(rangeStr);
				headResponse206(printer, range, (int) total, contentType);
			} else {
				headResponse200(printer, (int) total, contentType);
			}

			mFileBlock.interrupt();
			write(range, total, url, out);
			out.flush();

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * 输出媒体数据块
	 * 
	 * @param range
	 * @param total
	 * @param url
	 * @param out
	 * @throws Exception
	 */
	private synchronized void write(int range, long total, String url, OutputStream out) throws Exception {
		mFileBlock.write(range, total, url, out);
	}
}