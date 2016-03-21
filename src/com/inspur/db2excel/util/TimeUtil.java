package com.inspur.db2excel.util;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimeUtil
{

	/**
	 * 将时间转换为"yyyy-MM-dd HH:mm:ss"格式的字符串
	 * 
	 * @return String
	 */
	public static String date2str(long time)
	{
		return date2str(new Date(time));
	}

	/**
	 * 将时间转换为"yyyy-MM-dd HH:mm:ss"格式的字符串
	 * 
	 * @return String
	 */
	public static String date2str(long time, String format)
	{
		return date2str(new Date(time), format);
	}

	/**
	 * 将时间转换为"yyyy-MM-dd HH:mm:ss"格式的字符串
	 * 
	 * @return String
	 */
	public static String date2str(Date date)
	{
		return date2str(date, "yyyy-MM-dd HH:mm:ss");
	}

	/**
	 * 将时间转换为"yyyy-MM-dd HH:mm:ss"格式的字符串
	 * 
	 * @return String
	 */
	public static String date2str(Date date, String format)
	{
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		String str = sdf.format(date);
		return str;
	}

	/**
	 * 将当前时间转换为"yyyy-MM-dd HH:mm:ss"格式的字符串
	 * 
	 * @return String
	 */
	public static String nowTime2str()
	{
		return nowTime2str("yyyy-MM-dd HH:mm:ss");
	}

	/**
	 * 将当前时间转换为"yyyy-MM-dd HH:mm:ss"格式的字符串
	 * 
	 * @return String
	 */
	public static String nowTime2str(String format)
	{
		Date now = new Date();
		return date2str(now, format);
	}

	/**
	 * 使用默认格式将字符串转换为Date 默认格式为yyyy-MM-dd HH:mm:ss
	 * 
	 * @param timeStr
	 *            String
	 * @return Date
	 * @throws ParseException
	 */
	public static Date str2date(String timeStr) throws ParseException
	{
		return str2date(timeStr, "yyyy-MM-dd HH:mm:ss");
	}

	/**
	 * 使用指定格式将字符串转换为Date
	 * 
	 * @param timeStr
	 *            String
	 * @return Date
	 * @throws ParseException
	 */
	public static Date str2date(String timeStr, String format) throws ParseException
	{
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		Date dt = sdf.parse(timeStr);
		return dt;
	}

	/**
	 * 将时间字符串转换为TimeStamp
	 * 
	 * @param timeStr
	 *            String
	 * @param format
	 *            String
	 * @return Timestamp
	 * @throws ParseException
	 */
	public static Timestamp getTimeStamp(String timeStr, String format) throws ParseException
	{
		if (timeStr == null || "".equals(timeStr) || format == null || "".equals(format))
		{
			return null;
		}
		return new Timestamp(str2date(timeStr, format).getTime());
	}

	/**
	 * 将时间字符串转换为TimeStamp 时间字符串格式为 "yyyy-MM-dd HH:mm:ss"
	 * 
	 * @param timeStr
	 *            String
	 * @return Timestamp
	 * @throws ParseException
	 */
	public static Timestamp getTimeStamp(String timeStr) throws ParseException
	{
		return getTimeStamp(timeStr, "yyyy-MM-dd HH:mm:ss");
	}

	/**
	 * 判断时间是否在指定时间范围内
	 * 
	 * @param time
	 *            String
	 * @param fromTime
	 *            String
	 * @param toTime
	 *            String
	 * @return boolean
	 * @throws ParseException
	 */
	public static boolean timeIn(String time, String fromTime, String toTime) throws ParseException
	{
		Date date = str2date(time);
		Date fromDate = str2date(fromTime);
		Date toDate = str2date(toTime);

		return date.after(fromDate) && date.before(toDate);
	}

	public static long getToday()
	{
		Calendar cal = Calendar.getInstance();
		cal.set(cal.HOUR_OF_DAY, 0);
		cal.set(cal.MINUTE, 0);
		cal.set(cal.SECOND, 0);
		cal.set(cal.MILLISECOND, 0);

		return cal.getTimeInMillis();
	}

	public static long getYesterday()
	{
		return getToday() - 1000 * 3600 * 24;
	}

	public static long getDayBefore(int interval)
	{
		return getToday() - 1000 * 3600 * 24 * interval;
	}

	public static String computeDate(String condition)
	{
		return date2str(computeDateTOLong(condition));
	}

	public static String computeDate(String condition, String format)
	{
		return date2str(computeDateTOLong(condition), format);
	}

	private static long computeDateTOLong(String condition)
	{
		// -1D2H 一天两小时前
		// -H2 两小时前
		// 1D2H 一天两小时后
		// H2 两小时后

		int day = 0;
		int hour = 0;
		if (condition.indexOf("D") > 0)
		{
			String d = condition.substring(0, condition.indexOf("D"));
			day = Integer.parseInt(d);
			if (condition.indexOf("H") > 0)
			{
				String h = condition.substring(condition.indexOf("D") + 1, condition.indexOf("H"));
				hour = Integer.parseInt(h);
				if (condition.indexOf("-") == 0)
				{
					hour = -hour;
				}
			}
		} else if (condition.indexOf("H") > 0)
		{
			String h = condition.substring(0, condition.indexOf("H"));
			hour = Integer.parseInt(h);
		}

		Calendar cal = Calendar.getInstance();
		cal.add(cal.DAY_OF_MONTH, day);
		cal.add(cal.HOUR_OF_DAY, hour);
		cal.set(cal.MINUTE, 0);
		cal.set(cal.SECOND, 0);
		return cal.getTimeInMillis();
	}

	public static void main(String[] args)
	{
		// System.out.println(new Timestamp(TimeUtil.getDayBefore(2)));
		// System.out.println(date2str(getToday()));
		// System.out.println(TimeUtil.date2str(new
		// java.util.Date(TimeUtil.getYesterday())));
		// System.out.println(TimeUtil.date2str(new
		// java.util.Date(TimeUtil.getDayBefore(2))));

		System.out.println(computeDate("1D3H"));
	}
}
