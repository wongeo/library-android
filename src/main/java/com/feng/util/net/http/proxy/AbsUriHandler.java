package com.feng.util.net.http.proxy;

import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import com.feng.util.StringUtils;

/**
 * HTTP响应的相关处理的封装
 * 
 * @author WangJing
 * 
 */
public abstract class AbsUriHandler {

	/**
	 * 是否接受
	 * 
	 * @param uri
	 * @return
	 */
	protected abstract boolean isAccept(String uri);

	/**
	 * 响应
	 * 
	 * @param uri
	 * @param httpContext
	 * @throws Exception
	 */
	protected abstract void handle(String uri, HttpContext httpContext) throws Exception;

	/**
	 * 输出206 数据块
	 * 
	 * @param printer
	 * @param offset
	 * @param total
	 */
	protected void headResponse206(PrintStream printer, int offset, int total, String contentType) {
		int length = total - offset;
		Calendar cd = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("EEE d MMM yyyy HH:mm:ss 'GMT'", Locale.US);
		sdf.setTimeZone(TimeZone.getTimeZone("GMT")); // 设置时区为GMT
		String gmtTime = sdf.format(cd.getTime());
		printer.print("HTTP/1.1 206 Partial Content\r\n");
		printer.print("Date: " + gmtTime + "\r\n");
		printer.print("Accept-Ranges: bytes\r\n");
		printer.print("Content-Length: " + length + "\r\n");
		printer.print("Content-Range: " + "bytes " + offset + "-" + (total - 1) + "/" + total + "\r\n");
		printer.print("Content-Type: " + contentType + "\r\n");
		printer.print("Connection: Keep-Alive\r\n");
		printer.print("Server: nginx\r\n");
		printer.print("\r\n");
	}

	/**
	 * 输出200全部数据
	 * 
	 * @param printer
	 * @param length
	 */
	protected void headResponse200(PrintStream printer, int length, String contentType) {
		Calendar cd = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("EEE d MMM yyyy HH:mm:ss 'GMT'", Locale.US);
		sdf.setTimeZone(TimeZone.getTimeZone("GMT")); // 设置时区为GMT
		String gmtTime = sdf.format(cd.getTime());

		printer.print("HTTP/1.1 200 OK\r\n");
		printer.print("Date: " + gmtTime + "\r\n");
		printer.print("Accept-Ranges: bytes\r\n");
		printer.print("Content-Length: " + length + "\r\n");
		printer.print("Content-Type: " + contentType + "\r\n");
		printer.print("Connection: Keep-Alive\r\n");
		printer.print("Server: nginx\r\n");
		printer.print("\r\n");
	}

	/**
	 * 根据URL获取响应的类型
	 * 
	 * @param url
	 * @return
	 */
	protected String getContentType(String url) {
		try {
			String urlWithoutParm = StringUtils.parserUrl(url).toLowerCase(Locale.getDefault());
			if (urlWithoutParm.endsWith("m4a") || urlWithoutParm.endsWith("abk")) {
				return "audio/mp4";
			} else if (urlWithoutParm.endsWith("mp3")) {
				return "audio/mpeg";
			} else {
				return "application/octet-stream";
			}

		} catch (Exception e) {
			return "application/octet-stream";
		}
	}
}
