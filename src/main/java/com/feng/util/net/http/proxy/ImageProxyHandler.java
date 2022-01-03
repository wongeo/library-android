package com.feng.util.net.http.proxy;

import java.io.OutputStream;
import java.io.PrintStream;

import android.os.Environment;

import com.feng.util.io.FileUtils;

public class ImageProxyHandler extends AbsUriHandler {

	public static String protocolPrefix = "/cacheproxy/";

	public ImageProxyHandler() {
	}

	@Override
	public boolean isAccept(String uri) {
		return uri.contains(".png");
	}

	@Override
	public void handle(String uri, HttpContext httpContext) {
		try {
			final OutputStream out = httpContext.getOutputStream();
			byte[] raw = FileUtils.read(Environment.getExternalStorageDirectory() + "/" + uri);
			PrintStream printer = new PrintStream(out);
			printer.print("HTTP/1.1 200 OK\r\n");
			printer.print("Accept-Ranges:bytes\r\n");
			printer.print("Connection:Keep-Alive\r\n");
			printer.print("Content-Length:" + raw.length + "\r\n");
			printer.print("Content-Type:image/png\r\n");
			printer.print("Server:com.sopaco.serverlite\r\n");
			printer.print("\r\n");
			printer.write(raw);

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
