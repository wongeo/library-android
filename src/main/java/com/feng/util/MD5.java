package com.feng.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5 {
	/**
	 * 获取字符串MD5值，32字节字符串
	 * @param val
	 * @return
	 */
	public static String getMD5(String val) {
		if (val == null) return "";
		MessageDigest md5;
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			return "";
		}
		md5.update(val.getBytes());
		byte[] m = md5.digest();// 加密
		return getString(m);
	}
	
	public static String getMD5(byte[] val){
		if (val == null) return null;
		MessageDigest md5;
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			return "";
		}
		md5.update(val);
		byte[] m = md5.digest();// 加密
		return getString(m);
	}
	
	/** 获取字符串的MD5值，字节数组
	 * @param val
	 * @return 返回字节数组
	 */
	public static byte[] getMD5Bytes(String val){
		if (val == null) return null;
		MessageDigest md5;
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			return null;
		}
		md5.update(val.getBytes());
		return md5.digest();// 加密
	}

	public static String getString(byte[] b) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < b.length; i++) {
			sb.append(String.format("%02x", b[i]));
		}
		return sb.toString();
	}

	public static byte[] md5(File file) {
		FileInputStream fis = null;
		try {
			MessageDigest md5 = MessageDigest.getInstance("md5");
			fis = new FileInputStream(file);
			byte[] buffer = new byte[1024];
			int readCount;
			while ((readCount = fis.read(buffer)) > -1) {
				md5.update(buffer, 0, readCount);
			}
			return md5.digest();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}
	
	public static byte[] getByte(String b) {
		
		StringBuffer sb = new StringBuffer(b);
		byte by[]=new byte[sb.length()/2];
		for (int i = 0; i < sb.length(); i=i+2) {
			BigInteger big= new BigInteger(sb.substring(i, i+2), 16);
			by[i/2]=big.byteValue();
		}
		return by;
	}

}
