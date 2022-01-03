package com.feng.util.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import android.os.Environment;

/**
 * 内容参考博客 http://blog.csdn.net/com314159/article/details/22859059
 * 
 * @author WangJing
 * 
 */
public class StorageUtils {
	/**
	 * 返回值不带File seperater "/",如果没有外置第二个sd卡,返回null
	 * 
	 * @return
	 */
	public static String getSecondExterPath() {
		List<String> paths = getAllExterSdcardPath();

		if (paths.size() == 2) {

			for (String path : paths) {
				if (path != null && !path.equals(getFirstExterPath())) {
					return path;
				}
			}

			return null;

		} else {
			return null;
		}
	}

	public static boolean isFirstSdcardMounted() {
		if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			return false;
		}
		return true;
	}

	public static boolean isSecondSDcardMounted() {
		String sd2 = getSecondExterPath();
		if (sd2 == null) {
			return false;
		}
		return checkFsWritable(sd2 + File.separator);

	}

	/**
	 * 测试外置sd卡是否卸载，不能直接判断外置sd卡是否为null，因为当外置sd卡拔出时，仍然能得到外置sd卡路径。
	 * 我这种方法是按照android谷歌测试DICM的方法， 创建一个文件，然后立即删除，看是否卸载外置sd卡
	 * 注意这里有一个小bug，即使外置sd卡没有卸载，但是存储空间不够大，或者文件数已至最大数，此时，也不能创建新文件。此时，统一提示用户清理sd卡吧
	 * 
	 * @param dir
	 * @return
	 */
	private static boolean checkFsWritable(String dir) {

		if (dir == null)
			return false;

		File directory = new File(dir);

		if (!directory.isDirectory()) {
			if (!directory.mkdirs()) {
				return false;
			}
		}

		File f = new File(directory, ".keysharetestgzc");
		try {
			if (f.exists()) {
				f.delete();
			}
			if (!f.createNewFile()) {
				return false;
			}
			f.delete();
			return true;

		} catch (Exception e) {
		}
		return false;

	}

	/**
	 * 获取第一存储路径
	 * @return
	 */
	public static String getFirstExterPath() {
		return Environment.getExternalStorageDirectory().getPath();
	}

	/**
	 * 获取所有存储路径
	 * 
	 * @return
	 */
	public static List<String> getAllExterSdcardPath() {
		List<String> list = new ArrayList<String>();

		String firstPath = getFirstExterPath();

		// 得到路径
		try {
			Runtime runtime = Runtime.getRuntime();
			Process proc = runtime.exec("mount");
			InputStream is = proc.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);
			String line;
			BufferedReader br = new BufferedReader(isr);
			while ((line = br.readLine()) != null) {
				// 将常见的linux分区过滤掉
				if (line.contains("secure"))
					continue;
				if (line.contains("asec"))
					continue;
				if (line.contains("media"))
					continue;
				if (line.contains("system") || line.contains("cache") || line.contains("sys") || line.contains("data") || line.contains("tmpfs") || line.contains("shell") || line.contains("root") || line.contains("acct") || line.contains("proc") || line.contains("misc") || line.contains("obb")) {
					continue;
				}

				if (line.contains("fat") || line.contains("fuse") || (line.contains("ntfs"))) {

					String columns[] = line.split(" ");
					if (columns != null && columns.length > 1) {
						String path = columns[1];
						if (path != null && !list.contains(path) && path.contains("sd"))
							list.add(columns[1]);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (!list.contains(firstPath)) {
			list.add(firstPath);
		}
		return list;
	}
}
