package com.feng.util.io;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.Locale;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;

/**
 * 执行文件的操作控制类 <br>
 * 注意：一定不能与UI有任何关系</br>
 * 
 * @author WangJing
 * 
 */
public class FileUtils {
	public static boolean exists(String path) {
		File file = new File(path);
		if (file.exists()) {
			return true;
		}
		return false;
	}

	/**
	 * 写文件
	 * 
	 * @param path
	 * @param buffer
	 */
	public static boolean write(String path, byte[] buffer) {
		File file = new File(path);
		OutputStream os = null;
		try {
			os = new FileOutputStream(file);
			os.write(buffer);
			return true;
		} catch (java.io.IOException e) {
			file.delete();
			e.printStackTrace();
		} finally {
			try {
				if (os != null) {
					os.flush();
					os.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	public static boolean write(String path, Bitmap bitmap, CompressFormat format, int quality) {
		File file = new File(path);
		OutputStream os = null;
		try {
			os = new FileOutputStream(file);
			bitmap.compress(format, quality, os);
			return true;
		} catch (Exception e) {
			file.delete();
			e.printStackTrace();
		} finally {
			try {
				if (os != null) {
					os.flush();
					os.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	/**
	 * 读取文件
	 * 
	 * @param path
	 * @return
	 */
	public static byte[] read(String path) {
		File file = new File(path);
		InputStream is = null;
		try {
			is = new FileInputStream(file);
			int length = is.available();
			byte[] buffer = new byte[length];
			is.read(buffer);
			return buffer;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (is != null) {
					is.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * 删除文件
	 * 
	 * @param path
	 */
	public static void delete(String path) {
		if (path == null) {
			return;
		}
		delete(new File(path));
	}

	/**
	 * 删除文件
	 * 
	 * @param file
	 */
	public static void delete(File file) {
		if (file == null) {
			return;
		}
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			delete(files);
		}
		file.delete();
	}

	/**
	 * 删除文件
	 * 
	 * @param files
	 */
	public static void delete(File[] files) {
		if (files == null) {
			return;
		}
		for (File file : files) {
			delete(file);
		}
	}

	/**
	 * 转换文件大小
	 * 
	 * @param fileSize
	 * @return
	 */
	public static String longToMediaString(long fileSize) {
		if (fileSize == 0) {
			return "0MB";
		}
		float kbSize = fileSize / 1024f;
		if (kbSize < 1024) {
			return (int) kbSize + "KB";
		}
		float MBSize = kbSize / 1024f;
		if (MBSize < 1024) {
			return (int) MBSize + "MB";
		}
		return (int) (MBSize / 1024f) + "GB";
	}

	/**
	 * 拷贝文件
	 * 
	 * @param iis
	 * @param destPath
	 * @throws IOException
	 */
	public static void copyUnSafe(InputStream iis, String destPath) throws IOException {
		try {
			copy(iis, destPath);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static void copy(File srcFile, File descFile) {
		BufferedInputStream inBuff = null;
		BufferedOutputStream outBuff = null;
		try {
			// 新建文件输入流并对它进行缓冲
			inBuff = new BufferedInputStream(new FileInputStream(srcFile));

			// 新建文件输出流并对它进行缓冲
			outBuff = new BufferedOutputStream(new FileOutputStream(descFile));

			// 缓冲数组
			byte[] b = new byte[1024 * 5];
			int len;
			while ((len = inBuff.read(b)) != -1) {
				outBuff.write(b, 0, len);
			}
			// 刷新此缓冲的输出流
			outBuff.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (inBuff != null)
					inBuff.close();
				if (outBuff != null)
					outBuff.close();
			} catch (Exception e2) {
			}

		}
	}

	public static int copy(String srcPath, String descPath) {
		try {
			InputStream from = new FileInputStream(srcPath);
			return copy(from, descPath);
		} catch (FileNotFoundException e) {
			return -1;
		}
	}

	public static int copy(InputStream from, String descPath) {
		try {
			delete(descPath);
			OutputStream to = new FileOutputStream(descPath);
			byte buf[] = new byte[1024];
			int c;
			while ((c = from.read(buf)) > 0) {
				to.write(buf, 0, c);
			}
			from.close();
			to.close();
			return 0;
		} catch (Exception ex) {
			ex.printStackTrace();
			return -1;
		}
	}

	public static boolean isDirectoryAccessiable(String dir) {
		File dirF = new File(dir);
		return dirF.isDirectory() && dirF.canRead();
	}

	public static boolean isDirectoryCanWrite(String dir) {
		File dirF = new File(dir);
		return dirF.isDirectory() && dirF.canWrite();
	}

	public static void creatDir(String dir) {
		if (dir == null) {
			return;
		}
		File dirF = new File(dir);
		if (!dirF.exists()) {
			dirF.mkdirs();
		}
	}

	/**
	 * 获取文件扩展名
	 * 
	 * @param filePathName
	 * @return
	 */
	public static String getExtension(String filePathName) {
		if (filePathName == null)
			return null;
		final int index = filePathName.lastIndexOf('.');
		return (index != -1) ? filePathName.substring(index + 1).toLowerCase(Locale.getDefault()).intern() : "";
	}

	/**
	 * 
	 * @param obj
	 * @param path
	 * @throws Exception
	 */
	public static <X> void saveObject(X obj, String path) throws Exception {
		FileOutputStream fos = new FileOutputStream(path);
		ObjectOutputStream dos = null;
		try {
			dos = new ObjectOutputStream(fos);
			dos.writeObject(obj);
		} finally {
			if (dos != null) {
				dos.close();
			}
			if (fos != null) {
				fos.close();
			}
		}
	}

	/**
	 * 
	 * @param clazz
	 * @param path
	 * @return
	 * @throws Exception
	 */
	public static <X> X readObject(Class<X> clazz, String path) throws Exception {
		FileInputStream fis = new FileInputStream(path);
		return readObject(clazz, fis);
	}

	/**
	 * 
	 * @param clazz
	 * @param iis
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static <X> X readObject(Class<X> clazz, InputStream iis) throws Exception {
		ObjectInputStream ois = null;
		try {
			ois = new ObjectInputStream(iis);
			return (X) ois.readObject();
		} finally {
			try {
				if (ois != null) {
					ois.close();
				}
				if (iis != null) {
					iis.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}
}