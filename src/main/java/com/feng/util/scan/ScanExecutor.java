package com.feng.util.scan;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import android.content.Context;

import com.feng.util.io.StorageUtils;

/**
 * 本地扫描文件夹
 * 
 * @author WangJing
 * 
 */
public class ScanExecutor {

	private static ScanExecutor mInstance = new ScanExecutor();

	public static ScanExecutor getInstance() {
		return mInstance;
	}

	private ScanExecutor() {

	}

	private List<File> mFolders;

	private int mLevel;

	private FileFilter mFileFilter;

	public void startScan(Context context, Callable<Boolean> accept) {
		mFolders = new ArrayList<File>();
		List<String> storagepaths = StorageUtils.getAllExterSdcardPath();
		if (storagepaths == null) {
			return;
		}
		List<File> rootFolders = new ArrayList<File>();
		for (String storagepath : storagepaths) {
			File folder = new File(storagepath);
			if (!folder.isDirectory() || !folder.canRead() || folder.isHidden()) {
				continue;
			}
			rootFolders.add(folder);
		}
		for (File folder : rootFolders) {
			scanFolder(folder, accept);
		}
	}

	public void startScan(Context context, FileFilter fileFilter, Callable<Boolean> accept) {
		mFileFilter = fileFilter;
		startScan(context, accept);
	}

	/**
	 * 检测此文件夹是否合格
	 * 
	 * @param folder
	 */
	private void scanFolder(File folder, Callable<Boolean> accept) {
		if (!folder.isDirectory() || !folder.canRead() || folder.isHidden()) {
			return;
		}

		File[] files = folder.listFiles();
		if (files == null || files.length == 0) {
			return;
		}
		for (File file : files) {
			if (!accept(accept)) {
				break;
			}
			if (mFileFilter.accept(file)) {
				mFolders.add(folder);
				break;
			}
		}
		scanFolder(files, accept);
	}

	private void scanFolder(File[] files, Callable<Boolean> accept) {
		if (mLevel > 3) {
			return;
		}
		mLevel++;
		for (File file : files) {
			if (!accept(accept)) {
				break;
			}
			scanFolder(file, accept);
		}
		mLevel--;
	}

	public List<File> getFolder() {
		if (mFolders == null) {
			mFolders = new ArrayList<File>();
		}
		return mFolders;
	}

	private boolean accept(Callable<Boolean> accept) {
		if (accept == null) {
			return true;
		}
		try {
			return accept.call();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

}