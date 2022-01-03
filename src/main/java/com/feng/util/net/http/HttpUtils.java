package com.feng.util.net.http;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;

/**
 * HTTP 网络数据工具类
 * 
 * @author WangJing
 * 
 */
public class HttpUtils {

	private static final int BUFFER_LENGTH = 1024 * 10;
	private static final int OPT_CONNECT_TIMEOUT = 15000;
	private static final int OPT_READ_TIMEOUT = 30000;

	/**
	 * Get 请求 （数据支持GZIP，请求不支持Range）
	 * 
	 * @param url
	 * @return
	 * @throws Exception
	 */
	public static byte[] getData(String url) throws Exception {
		HttpGet httpGet = new HttpGet(url);
		httpGet.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, OPT_CONNECT_TIMEOUT);
		httpGet.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, OPT_READ_TIMEOUT);
		httpGet.addHeader("accept-encoding", "gzip");

		DefaultHttpClient httpClient = new DefaultHttpClient();
		httpClient.addResponseInterceptor(new HttpGzipResponseInterceptor());
		HttpResponse response = httpClient.execute(httpGet);
		InputStream is = response.getEntity().getContent();
		ByteArrayOutputStream bos = new ByteArrayOutputStream();

		byte[] buffer = new byte[BUFFER_LENGTH];
		int len = 0;

		while ((len = is.read(buffer)) > 0) {
			bos.write(buffer, 0, len);
		}
		return bos.toByteArray();
	}

	/**
	 * Post 请求
	 * 
	 * @param url
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public static byte[] postData(String url, List<NameValuePair> params) throws Exception {

		HttpPost httpPost = new HttpPost(url);
		httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
		httpPost.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, OPT_CONNECT_TIMEOUT);
		httpPost.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, OPT_READ_TIMEOUT);

		HttpClient httpClient = new DefaultHttpClient();
		HttpResponse response = httpClient.execute(httpPost);
		InputStream is = response.getEntity().getContent();
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte[] buffer = new byte[BUFFER_LENGTH];
		int len = 0;

		while ((len = is.read(buffer)) > 0) {
			bos.write(buffer, 0, len);
		}
		return bos.toByteArray();
	}

	private static final String BOUNDARY = "----WebKitFormBoundaryT1HoybnYeFOGFlBR";

	/**
	 * 
	 * @param params
	 *            传递的普通参数
	 * @param uploadFile
	 *            需要上传的文件名
	 * @param fileFormName
	 *            需要上传文件表单中的名字
	 * @param newFileName
	 *            上传的文件名称，不填写将为uploadFile的名称
	 * @param urlStr
	 *            上传的服务器的路径
	 * @throws IOException
	 */
	public static byte[] uploadForm(Map<String, String> params, String fileFormName, File uploadFile, String newFileName, String urlStr) throws IOException {
		if (newFileName == null || newFileName.trim().equals("")) {
			newFileName = uploadFile.getName();
		}

		StringBuilder sb = new StringBuilder();
		/**
		 * 普通的表单数据
		 */
		for (String key : params.keySet()) {
			sb.append("--" + BOUNDARY + "\r\n");
			sb.append("Content-Disposition: form-data; name=\"" + key + "\"" + "\r\n");
			sb.append("\r\n");
			sb.append(params.get(key) + "\r\n");
		}
		/**
		 * 上传文件的头
		 */
		sb.append("--" + BOUNDARY + "\r\n");
		sb.append("Content-Disposition: form-data; name=\"" + fileFormName + "\"; filename=\"" + newFileName + "\"" + "\r\n");
		sb.append("Content-Type: image/jpeg" + "\r\n");// 如果服务器端有文件类型的校验，必须明确指定ContentType
		sb.append("\r\n");

		byte[] headerInfo = sb.toString().getBytes("UTF-8");
		byte[] endInfo = ("\r\n--" + BOUNDARY + "--\r\n").getBytes("UTF-8");
		System.out.println(sb.toString());
		URL url = new URL(urlStr);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);
		conn.setRequestProperty("Content-Length", String.valueOf(headerInfo.length + uploadFile.length() + endInfo.length));
		conn.setDoOutput(true);

		OutputStream out = conn.getOutputStream();
		InputStream in = new FileInputStream(uploadFile);
		out.write(headerInfo);

		byte[] buf = new byte[1024];
		int len;
		while ((len = in.read(buf)) != -1)
			out.write(buf, 0, len);

		out.write(endInfo);
		in.close();
		out.close();
		if (conn.getResponseCode() == 200) {
			System.out.println("上传成功");
		}

		InputStream is = conn.getInputStream();

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		while ((len = is.read(buf)) > 0) {
			bos.write(buf, 0, len);
		}
		return bos.toByteArray();

	}

	/**
	 * 下载（异步）
	 * 
	 * @param url
	 * @param path
	 * @param listener
	 */
	public static void downloadDataAsync(final String url, final String path, final DownloadListener listener) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					downloadData(url, path, listener);
				} catch (Exception e) {
					e.printStackTrace();
					listener.onInterrupted(e);
				}
			}
		}).start();
	}

	/**
	 * 下载
	 * 
	 * @param url
	 * @param path
	 * @param listener
	 * @throws Exception
	 */
	public static void downloadData(String url, String path, DownloadListener listener) throws Exception {
		File file = new File(path);
		if (file.exists()) {
			file.delete();
		}
		File fileTemp = new File(path + ".temp");

		HttpGet httpGet = new HttpGet(url);
		httpGet.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, OPT_CONNECT_TIMEOUT);
		httpGet.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, OPT_READ_TIMEOUT);
		long offset = fileTemp.length();
		httpGet.addHeader("Range", "bytes=" + offset + "-");

		HttpClient httpClient = new DefaultHttpClient();
		HttpResponse response = httpClient.execute(httpGet);
		long totalLength = response.getEntity().getContentLength() + offset;
		if (listener != null) {
			listener.onPrepared(totalLength);
		}
		int httpCode = response.getStatusLine().getStatusCode();
		if (httpCode == 416 || (httpCode >= 400 && httpCode < 500 && httpCode != 404)) {
			if (listener != null) {
				listener.onCompleted();
			}
			return;
		}

		if (totalLength == offset) {
			if (listener != null) {
				listener.onCompleted();
			}
			return;
		}

		InputStream is = response.getEntity().getContent();
		FileOutputStream fos = new FileOutputStream(fileTemp, true);
		byte[] buffer = new byte[BUFFER_LENGTH];
		int len = 0;
		long downloadedSize = offset;
		while ((len = is.read(buffer)) > 0) {
			fos.write(buffer, 0, len);
			downloadedSize += len;
			if (listener != null) {
				listener.onDownloadProgressChanged(downloadedSize, totalLength);
			}
		}
		fos.close();
		is.close();

		fileTemp.renameTo(file);
		if (listener != null) {
			listener.onCompleted();
		}
	}

	public interface DownloadListener {

		void onPrepared(long totalLength);

		void onDownloadProgressChanged(long downloadedSize, long totalLength);

		void onInterrupted(Exception ex);

		void onCompleted();
	}
}
