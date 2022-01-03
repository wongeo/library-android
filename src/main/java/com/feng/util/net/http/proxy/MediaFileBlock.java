package com.feng.util.net.http.proxy;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;

import android.os.Environment;
import android.util.Log;

import com.feng.util.MD5;
import com.feng.util.io.FileUtils;

/**
 * 数据块
 * 
 * @author WangJing
 * 
 */
public class MediaFileBlock {
	/** 连接超时 */
	private final int OPT_CONNECT_TIMEOUT = 15000;

	/** 传输超时 */
	private final int OPT_READ_TIMEOUT = 15000;

	/** 每一存储块的大小 */
	private final int BUFFER_LENGTH = 1024 * 1024;

	/** 数据块开始索引 */
	private long mBegin;

	/** 数据块结束索引 */
	private long mEnd;

	/** 距离数据块头的偏移量,(0~BUFFER_LENGTH) */
	private long mBlockOffset;

	/** 至于为什么把它设置为成员变量，是为了使用abort中断 */
	private HttpGet mHttpGet;

	/** 此控制变量用于中断操作 */
	private boolean mIsAvailable;

	/**
	 * 对数据快进行切割，分块输出
	 * 
	 * @param range
	 * @param total
	 * @param url
	 * @param out
	 * @throws Exception
	 */
	public synchronized void write(long range, long total, String url, OutputStream out) throws Exception {

		mIsAvailable = true;

		initRange(range, total);

		while (mBegin < total) {
			if (!mIsAvailable) {
				return;
			}

			String dirStr = Environment.getExternalStorageDirectory() + "/cache/" + MD5.getMD5(url);
			File dir = new File(dirStr);
			if (!dir.exists() || !dir.isDirectory()) {
				dir.mkdirs();
			}

			String path = dirStr + "/" + mBegin + "_" + mEnd + "_" + total;

			writeBlock(range, total, path, url, out);

			// 处理完一块后，将开始索引移向下一块
			mBegin += BUFFER_LENGTH;

			mEnd = mBegin + BUFFER_LENGTH - 1;

			if (mEnd >= total) {
				mEnd = total - 1;
			}

			mBlockOffset = 0;
		}
	}

	/**
	 * 获取一块数据，需要的数据是从数据块中间取，那么不会存储
	 * 
	 * @param offset
	 * @param total
	 * @param path
	 * @param url
	 * @param out
	 * @throws Exception
	 */
	private void writeBlock(long offset, long total, String path, String url, OutputStream out) throws Exception {

		File file = new File(path);
		if (hasFile(file)) {
			RandomAccessFile reader = new RandomAccessFile(file, "rw");
			reader.seek(mBlockOffset);
			byte[] buffer = new byte[1024];
			int len;
			while ((len = reader.read(buffer)) != -1) {
				out.write(buffer, 0, len);
			}
			reader.close();
			return;
		}

		HttpResponse response = getNetBlock(url, offset);
		InputStream is = response.getEntity().getContent();

		byte[] buffer = new byte[1024];
		int len;

		RandomAccessFile writer = new RandomAccessFile(file, "rw");

		while ((len = is.read(buffer)) != -1) {
			if (!mIsAvailable) {
				break;
			}
			out.write(buffer, 0, len);
			if (mBlockOffset == 0) {
				writer.write(buffer, 0, len);
			}
		}
		writer.close();
		is.close();
	}

	/**
	 * 初始化，寻找第一块,初始化begin,end
	 * 
	 * @param offset
	 * @param total
	 */
	private void initRange(long offset, long total) {
		mBegin = offset / BUFFER_LENGTH * BUFFER_LENGTH;

		mBlockOffset = offset - mBegin;

		mEnd = mBegin + BUFFER_LENGTH - 1;

		if (mEnd >= total) {
			mEnd = total - 1;
		}
	}

	/**
	 * 获取网络数据块
	 * 
	 * @param url
	 * @return
	 * @throws Exception
	 */
	private HttpResponse getNetBlock(String url, long offset) throws Exception {
		String range = "bytes=" + mBegin + "-" + mEnd;

		if (mBlockOffset != 0) {
			range = "bytes=" + offset + "-" + mEnd;
		}
		Log.d("ssm", range);

		mHttpGet = new HttpGet(url);
		mHttpGet.addHeader("Range", range);
		BasicHttpParams params = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(params, OPT_CONNECT_TIMEOUT);
		HttpConnectionParams.setSoTimeout(params, OPT_READ_TIMEOUT);
		HttpClient hc = new DefaultHttpClient(params);
		HttpResponse response = hc.execute(mHttpGet);

		int httpCode = response.getStatusLine().getStatusCode();

		if (httpCode != 200 && httpCode != 206) {
			throw new Exception("下载失败");
		}

		return response;
	}

	/**
	 * 分解是否合法，（如果文件不足一块大小，则废弃掉）
	 * 
	 * @param file
	 * @return
	 */
	private boolean hasFile(File file) {
		if (file.exists() && file.isFile() && file.length() == BUFFER_LENGTH) {
			return true;
		}
		if (file.exists() && file.isFile() && file.length() < BUFFER_LENGTH) {
			String[] strs = file.getName().split("_");
			int i0 = Integer.parseInt(strs[0]);
			int i1 = Integer.parseInt(strs[1]);
			if (i1 - i0 + 1 == file.length()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 中断操作
	 */
	public void interrupt() {
		if (mHttpGet != null && !mHttpGet.isAborted()) {
			mHttpGet.abort();
		}
		mIsAvailable = false;
	}

	/**
	 * 获取mp3的总长度，并记录在缓存目录的根目录
	 * 
	 * @param url
	 * @return
	 * @throws Exception
	 */
	public long getTotal(String url) throws Exception {

		long total = -1;
		File dir = createCacheDirectory(url);

		String path = dir.getAbsolutePath() + "/total_length";
		try {
			byte[] data = FileUtils.read(path);
			if (data != null) {
				String strLength = new String(data);
				total = Long.parseLong(strLength);
				return total;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		BasicHttpParams params = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(params, OPT_CONNECT_TIMEOUT);
		HttpConnectionParams.setSoTimeout(params, OPT_READ_TIMEOUT);
		HttpHead head = new HttpHead(url);
		HttpClient client = new DefaultHttpClient(params);
		HttpResponse response = client.execute(head);
		int httpCode = response.getStatusLine().getStatusCode();

		checkHttpCode(httpCode);

		total = Long.parseLong(response.getFirstHeader("Content-Length").getValue());

		FileUtils.write(path, (total + "").getBytes());

		return total;
	}

	/**
	 * 创建缓存目录
	 * 
	 * @param url
	 * @return
	 */
	private File createCacheDirectory(String url) {
		String dirStr = Environment.getExternalStorageDirectory() + "/cache/" + MD5.getMD5(url);
		File dir = new File(dirStr);
		if (!dir.exists() || !dir.isDirectory()) {
			dir.mkdirs();
		}
		return dir;
	}

	/**
	 * 检查httpCode
	 * 
	 * @param code
	 * @throws Exception
	 */
	private void checkHttpCode(int code) throws Exception {
		if (code == 404) {
			throw new Exception("not find 404");
		}
		if (code != 200) {
			throw new Exception("请求失败");
		}
	}

	/**
	 * 下载数据第一块
	 * 
	 * @param url
	 * @param total
	 * @throws Exception
	 */
	public void downloadFirstBlock(String url, long total) throws Exception {
		initRange(0, total);

		File dir = createCacheDirectory(url);

		String path = dir.getAbsolutePath() + "/" + mBegin + "_" + mEnd + "_" + total;

		File file = new File(path);
		if (hasFile(file)) {
			return;
		}

		HttpResponse response = getNetBlock(url, 0);
		InputStream is = response.getEntity().getContent();

		byte[] buffer = new byte[1024];
		int len;

		RandomAccessFile writer = new RandomAccessFile(file, "rw");

		while ((len = is.read(buffer)) != -1) {
			if (mBlockOffset == 0) {
				writer.write(buffer, 0, len);
			}
		}
		writer.close();
		is.close();
	}
}
