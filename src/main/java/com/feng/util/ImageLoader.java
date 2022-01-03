package com.feng.util;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.feng.ref.RelaySoftReference;
import com.feng.util.io.FileUtils;
import com.feng.util.net.http.HttpUtils;

/**
 * 图片加载器
 * 
 * @author WangJing
 * 
 */
public class ImageLoader {

	/**
	 * 单例类实例
	 */
	private static ImageLoader mInstance = new ImageLoader();
	/**
	 * 图片软引用集合
	 */
	private Map<String, RelaySoftReference<Bitmap>> mImages = new HashMap<String, RelaySoftReference<Bitmap>>();
	/**
	 * 下载图片线程池
	 */
	private ExecutorService mDownImagePool = Executors.newFixedThreadPool(5);

	private ImageLoader() {

	}

	public static ImageLoader getInstance() {
		return mInstance;
	}

	/**
	 * 加载在线或者缓存内图片
	 * 
	 * @param url
	 * @param path
	 * @param onCompleted
	 */
	public void getRemoteImageOrCacheAsync(final String url, final String path, final SingleArgsAction<Bitmap> onCompleted) {
		mDownImagePool.submit(new Runnable() {

			@Override
			public void run() {
				synchronized (url) {
					Bitmap bitmap = getFileImageOrCache(path);
					if (bitmap != null) {
						onCompleted.execute(bitmap);
						return;
					}
					
					Bitmap bm = null;
					try {
						byte[] raw = HttpUtils.getData(url);
						FileUtils.write(path, raw);
						bm = BitmapFactory.decodeByteArray(raw, 0, raw.length);
					} catch (Exception e) {
						e.printStackTrace();
					}
					onCompleted.execute(bm);
				}
			}
		});
	}

	/**
	 * 加载缓存内图片
	 * 
	 * @param path
	 * @return
	 */
	public Bitmap getFileImageOrCache(final String path) {
		if (!new File(path).exists()) {
			return null;
		}
		RelaySoftReference<Bitmap> refObj = mImages.get(path);
		if (refObj != null) {
			Bitmap cachedImage = refObj.getObj();
			if (cachedImage != null) {
				return cachedImage;
			} else {
				mImages.remove(path);
			}
		}

		Callable<Bitmap> getAction = new Callable<Bitmap>() {

			@Override
			public Bitmap call() throws Exception {
				Bitmap bm = BitmapFactory.decodeFile(path);
				return bm;
			}
		};

		Bitmap img = null;
		try {
			img = getAction.call();
		} catch (Exception e) {
			e.printStackTrace();
		}
		mImages.put(path, new RelaySoftReference<Bitmap>(img, getAction));
		return img;
	}
}
