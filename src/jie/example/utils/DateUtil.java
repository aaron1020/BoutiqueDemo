package jie.example.utils;

import android.annotation.SuppressLint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import jie.example.constant.Constant;

@SuppressLint("SimpleDateFormat")
public class DateUtil {

	private static SimpleDateFormat sdf = new SimpleDateFormat(
			Constant.DATE_FORMAT_SHORT_LINE);

	/**
	 * 
	 * @param originalDateString
	 *            被比较的时间
	 * @param comparedDateString
	 *            比较的时间
	 * @return int 比较结果：小于返回-1，等于返回0，大于返回1。
	 * @throws Exception
	 */
	public static int dateCompare(String originalDateString,
			String comparedDateString) throws Exception {
		Date originalDate = sdf.parse(originalDateString.trim());
		int compareResult = originalDate.compareTo(sdf.parse(comparedDateString
				.trim()));
		return compareResult;
	}

	/**
	 * 
	 * @param originalDateString
	 *            被比较的时间
	 * @param comparedDateString
	 *            比较的时间
	 * @return 如果originalDateString不小于comparedDateString，返回false;否则，返回true。
	 * @throws Exception
	 */
	public static boolean dateTimeCompare(String originalDateString,
			String comparedDateString) throws Exception {
		Date origianlDate = sdf.parse(originalDateString.trim());
		Date comparedDate = sdf.parse(comparedDateString.trim());
		long originalDateLong = origianlDate.getTime();
		long comparedDateLong = comparedDate.getTime();
		return originalDateLong < comparedDateLong;
	}

	/**
	 * 往前或往后修改日期
	 * 
	 * @param originalDateString
	 *            需要修改的日期
	 * @param changeRange
	 *            修改的幅度：往前添加多少天则传入正的多少天，往后减少多少天则传入负的多少天。
	 * @return 修改后的日期
	 * @throws ParseException
	 */
	public static String addDate(String originalDateString, int changeRange)
			throws ParseException {
		Date date = sdf.parse(originalDateString.trim());
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DATE, changeRange);
		date = calendar.getTime();
		return sdf.format(date);
	}

	/**
	 * 计算两个时间段相差多少个小时
	 */
	public static float calculateTimeIntervalHours(String oneDateString,
			String anotherDateString) throws Exception {
		Date onelDate = sdf.parse(oneDateString.trim());
		Date anotherDate = sdf.parse(anotherDateString.trim());
		long oneDateLong = onelDate.getTime();
		long anotherDateLong = anotherDate.getTime();
		long intervalMilliseconds = anotherDateLong - oneDateLong;// 相差多少毫秒
		float intervalHours = intervalMilliseconds * 1.0f / (60 * 60 * 1000);
		return intervalHours;
	}
	
}
