package com.feng.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 时间工具类（用于转换和格式化各种时间）
 * 
 * @author WangJing
 * 
 */
public class TimeUtils {
	private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	private static final DateFormat dateFormatLong = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	private static final DateFormat dateFormatShort2 = new SimpleDateFormat("MM月dd日 - HH:mm:ss");
	private static final DateFormat fileDateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");

	/**
	 * 媒体时间转换为long类型（12:10 -> 4820938402348）
	 * 
	 * @param timeStr
	 * @return
	 */
	public static long mediaStringToTime(String timeStr) {
		String[] parts = timeStr.split(":");
		if (parts.length == 2) {
			return Integer.parseInt(parts[0]) * 1000 * 60 + Integer.parseInt(parts[1]) * 1000;
		} else if (parts.length == 3) {
			return Long.parseLong(parts[0]) * 1000 * 60 * 60 + Long.parseLong(parts[1]) * 1000 * 60 + Long.parseLong(parts[2]) * 1000;
		} else {
			return 0;
		}
	}

	/**
	 * long类型转换为媒体事件（8094820948->70:32）
	 * 
	 * @param time
	 * @return
	 */
	public static String timeToMediaString(long time) {
		int hour = (int) (time / (1000 * 60 * 60));
		int minute = (int) ((time - hour * (1000 * 60 * 60)) / (1000 * 60));
		int second = (int) ((time - hour * (1000 * 60 * 60) - minute * (1000 * 60)) / 1000);
		if (hour == 0) {
			return numTo2Str(minute) + ":" + numTo2Str(second);
		} else {
			return numTo2Str(hour * 60 + minute) + ":" + numTo2Str(second);
		}
	}

	/**
	 * long类型转换为媒体事件（8094820948->1:10:32）
	 * 
	 * @param time
	 * @return
	 */
	public static String timeToMediaString2(long time) {
		int hour = (int) (time / (1000 * 60 * 60));
		int minute = (int) ((time - hour * (1000 * 60 * 60)) / (1000 * 60));
		int second = (int) ((time - hour * (1000 * 60 * 60) - minute * (1000 * 60)) / 1000);
		if (hour == 0) {
			return numTo2Str(minute) + ":" + numTo2Str(second);
		} else {
			return numTo2Str(hour) + ":" + numTo2Str(minute) + ":" + numTo2Str(second);
		}
	}

	private static String numTo2Str(int n) {
		if (n >= 10) {
			return n + "";
		}
		return "0" + n;
	}

	public static String getDateStrNow() {
		Date date = Calendar.getInstance().getTime();
		return dateFormat.format(date);
	}

	public static String getDateLongStr(long time) {
		Date date = new Date(time);
		return dateFormatLong.format(date);
	}

	public static String getDateShort2(long time) {
		Date date = new Date(time);
		return dateFormatShort2.format(date);
	}

	public static String getDateLongStrNow() {
		Date date = Calendar.getInstance().getTime();
		return dateFormatLong.format(date);
	}

	public static String getFileDateStrNow() {
		Date date = Calendar.getInstance().getTime();
		return fileDateFormat.format(date);
	}

	public static long getTimesFromFileDateStr(String s) {
		try {
			return fileDateFormat.parse(s).getTime();
		} catch (Exception ex) {
			return 0L;
		}
	}

	public static long getTimesFromDateStr(String s) {
		try {
			return dateFormat.parse(s).getTime();
		} catch (Exception ex) {
			return 0L;
		}
	}

	public static String getTimeSpanString(long interval) {
		int days = (int) (interval / (1000 * 60 * 60 * 24));
		int hour = (int) (interval / (1000 * 60 * 60)) - days * 24;
		int minute = (int) ((interval - hour * (1000 * 60 * 60)) / (1000 * 60));
		if (days > 0) {
			return days + "天";
		}
		if (hour > 0) {
			return hour + "小时";
		}
		if (minute < 1) {
			minute = 1;
		}
		return minute + "分钟";
	}
}
