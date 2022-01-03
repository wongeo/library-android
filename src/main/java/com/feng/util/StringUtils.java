package com.feng.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 *
 * 
 * @author WangJing
 * 
 */
public class StringUtils {

	private static final int BUFFER_LENGTH = 1024 * 10;

	public static void stirngToByte() {

	}

	public static void inputStreamToString() {

	}

	public static byte[] inputStreamToByte(InputStream is) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte[] buffer = new byte[BUFFER_LENGTH];
		int len = 0;

		while ((len = is.read(buffer)) > 0) {
			bos.write(buffer, 0, len);
		}
		byte[] data = bos.toByteArray();
		is.close();
		bos.close();
		return data;
	}
	public static String parserUrl(String url) {
		int index = url.lastIndexOf("?");
		if (index < 0) {
			return url;
		}
		return url.substring(0, index);
	}

	public static String parserUrl(String url, String parm) {
		int index = url.lastIndexOf("?");
		if (index >= 0) {

			String parms = url.substring(index + 1);
			String[] keyValues = parms.split("&");
			for (int i = 0; i < keyValues.length; i++) {
				if (keyValues[i].startsWith(parm + "=")) {
					return keyValues[i].substring(parm.length() + 1);
				}
			}
		}
		return "";
	}

	public static String cutFromParm(String url, String parm) {
		int index = url.lastIndexOf("?");
		if (index >= 0) {
			String parms = url.substring(index + 1);
			String[] keyValues = parms.split("&");
			for (int i = 0; i < keyValues.length; i++) {
				if (keyValues[i].startsWith(parm + "=")) {
					return url.substring(0, url.lastIndexOf(parm) + keyValues[i].length());
				}
			}
		}
		return url;
	}
}
