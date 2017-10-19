package com.tsoft.core.database.utils;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * 日期/时间工具类
 *
 */
public class UncDate {

	private final static String[] CHINESE_NUMBER = { "○", "一", "二", "三", "四",
			"五", "六", "七", "八", "九", "十", "十一", "十二", "十三", "十四", "十五", "十六",
			"十七", "十八", "十九", "二十", "二十一", "二十二", "二十三", "二十四", "二十五", "二十六",
			"二十七", "二十八", "二十九", "三十", "三十一" };
	
	/**
	 * 返回短日期格式化字符串。格式:yyyy-MM-dd
	 * @param date date实例
	 * @return 短日期
	 */
	public static String shortDate(Date date) {
		return formatDateTime(date, "yyyy-MM-dd");
	}

	/**
	 * 返回长日期格式化字符串。格式:yyyy年MM月dd日
	 * @param date date实例
	 * @return 长日期
	 */
	public static String longDate(Date date) {
		return formatDateTime(date, "yyyy年MM月dd日");
	}

	/**
	 * 返回全中文格式的长日期格式化字符串。示例: 二oo九年一月二十三日
	 * @param date date示例
	 * @return 全中文格式的长日期格式化字符串
	 */
	public static String longDateForChn(Date date) {
		if (date == null)
			return "";

		String s = "";
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		String sYear = String.valueOf(c.get(Calendar.YEAR));
		int month = c.get(Calendar.MONTH) + 1;
		int day = c.get(Calendar.DAY_OF_MONTH);

		for (int i = 0; i < sYear.length(); i++) {
			int y = UncString.toInt(String.valueOf(sYear.charAt(i)));
			s += CHINESE_NUMBER[y];
		}

		s += "年" + CHINESE_NUMBER[month] + "月" + CHINESE_NUMBER[day] + "日";
		return s;
	}

	/**
	 * 返回短时间格式的字符串。同shortDate函数。格式:yyyy-MM-dd
	 * @param dateTime dateTime实例
	 * @return 短时间格式的字符串
	 */
	public static String shortTime(Date dateTime) {
		return shortDate(dateTime);
	}

	/**
	 * 返回普通长度格式的日期时间字符串。格式:yyyy-MM-dd HH:mm
	 * @param dateTime dateTime实例
	 * @return 普通长度格式的日期时间字符串
	 */
	public static String middleTime(Date dateTime) {
		return formatDateTime(dateTime, "yyyy-MM-dd HH:mm");
	}

	/**
	 * 返回长时间格式的日期时间字符串。格式:yyyy-MM-dd HH:mm:ss
	 * @param dateTime dateTime实例
	 * @return 长时间格式的日期时间字符串
	 */
	public static String longTime(Date dateTime) {
		return formatDateTime(dateTime, "yyyy-MM-dd HH:mm:ss");
	}

	/**
	 * 指定格式及时区格式化日期时间
	 * @param dateTime dateTime实例
	 * @param pattern 日期时间格式
	 * @param timeZone 时区
	 * @return 日期时间字符串
	 */
	public static String formatDateTime(Date dateTime, String pattern, TimeZone timeZone) {
		if (dateTime == null)
			return "";

		if(timeZone == null){
			timeZone = TimeZone.getDefault();
		}
		
		if (pattern == null || pattern.equals("")) {
			// Default
			pattern = "yyyy-MM-dd HH:mm:ss";
		}
		SimpleDateFormat dateFmt = new SimpleDateFormat(pattern);
		dateFmt.setTimeZone(timeZone);
		return dateFmt.format(dateTime);
	}
	
	/**
	 * 使用服务器当前时区格式化日期时间
	 * @param dateTime dateTime实例
	 * @param pattern 日期时间格式
	 * @return 日期时间字符串
	 */
	public static String formatDateTime(Date dateTime, String pattern) {
		return formatDateTime(dateTime, pattern, TimeZone.getDefault());
	}
	
	/**
	 * 返回当前日期时间
	 * @param pattern 日期时间格式
	 * @param timeZone 时区
	 * @return
	 */
	public static String timestamp(String pattern, TimeZone timeZone) {
		return formatDateTime(new Date(), pattern, timeZone);
	}

	/**
	 * 将含有日期的字符串转化为Date型。
	 * 
	 * @param dateString
	 *            含有日期的字符串。格式为：yyyy-MM-dd
	 * @return
	 */
	public static Date parseDate(String dateString) {
		return parseDate(dateString, '-');
	}

	/**
	 * 将含有日期的字符串转化为Date型。
	 * 
	 * @param dateString
	 *            含有日期的字符串。使用separator作为分隔符。
	 * @param separator
	 *            年月日间分隔符
	 * @return
	 */

	public static Date parseDate(String dateString, char separator) {
		Date date = null;

		if (dateString == null || dateString.length() == 0)
			return null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy" + separator + "MM"
				+ separator + "dd");
		try {
			date = sdf.parse(dateString);
		} catch (ParseException ex) {

		} finally {

		}
		return date;
	}

	public static Timestamp parseTimestamp(String timeString){
		return parseTimestamp(timeString, null);
	}
	
	/**
	 * 将含有日期时间的字符串转化为Timestamp型。
	 * 
	 * @param timeString
	 *            含有日期时间的字符串。格式为：yyyy-MM-dd HH:mm:ss
	 * @return
	 */
	public static Timestamp parseTimestamp(String timeString, TimeZone timeZone) {
		Timestamp time = null;
		String format1 = "yyyy-MM-dd HH:mm:ss";
		String format2 = "yyyy-MM-dd HH:mm";
		String format3 = "yyyy-MM-dd";

		try {
			time = parseFormattedTimestamp(timeString, format1, timeZone);
		} catch (ParseException ex) {
			try {
				time = parseFormattedTimestamp(timeString, format2, timeZone);
			} catch (ParseException ex2) {
				try {
					time = parseFormattedTimestamp(timeString, format3, timeZone);
				} catch (ParseException ex3) {
					//
				}

			}
		}

		return time;
	}

	private static Timestamp parseFormattedTimestamp(String timeString,
			String format, TimeZone timeZone) throws ParseException {
		Date date = null;

		if (timeString == null)
			return null;
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		if(timeZone != null){
			sdf.setTimeZone(timeZone);
		}
		
		date = sdf.parse(timeString);
		return (date == null) ? null : (new Timestamp(date.getTime()));
	}

	/**
	 * 设置时间
	 * 
	 * @param dateTime
	 *            需要设置的日期类
	 * @param year
	 *            年
	 * @param month
	 *            月（1-12）
	 * @param day
	 *            日（1-31）
	 * @param hour
	 *            小时（0-23）
	 * @param minute
	 *            分（0-59）
	 * @param second
	 *            秒（0-59）
	 */
	public static void setTime(Calendar dateTime, int year, int month, int day,
			int hour, int minute, int second) {
		dateTime.set(Calendar.YEAR, year);
		dateTime.set(Calendar.MONTH, month - 1);
		dateTime.set(Calendar.DAY_OF_MONTH, day);
		dateTime.set(Calendar.HOUR_OF_DAY, hour);
		dateTime.set(Calendar.MINUTE, minute);
		dateTime.set(Calendar.SECOND, second);
		dateTime.set(Calendar.MILLISECOND, 0);
	}

	/**
	 * 设置时间
	 * 
	 * @param dateTime
	 *            需要设置的日期类
	 * @param hour
	 *            小时（0-23）
	 * @param minute
	 *            分（0-59）
	 * @param second
	 *            秒（0-59）
	 * @param millisecond
	 *            毫秒（0-999）
	 */
	public static void setTime(Calendar dateTime, int hour, int minute,
			int second, int millisecond) {
		if (dateTime != null) {
			dateTime.set(Calendar.HOUR_OF_DAY, hour);
			dateTime.set(Calendar.MINUTE, minute);
			dateTime.set(Calendar.SECOND, second);
			dateTime.set(Calendar.MILLISECOND, millisecond);
		}
	}

	/**
	 * 截掉日期的小时分秒部分
	 * 
	 * @param dateTime
	 */
	public static void trim(Calendar dateTime) {
		if (dateTime != null) {
			dateTime.set(Calendar.HOUR_OF_DAY, 0);
			dateTime.set(Calendar.MINUTE, 0);
			dateTime.set(Calendar.SECOND, 0);
			dateTime.set(Calendar.MILLISECOND, 0);
		}
	}
	
	/**
	 * 给日期类型做加减运算
	 * @param dateTime 日期对象
	 * @param amount 增量
	 * @param field 增加的单位.Calendar.DAY_OF_MONTH, Calendar.HOUR, Calendar.MINUTE, Calendar.SECOND等
	 * @return 运算结果日期
	 */
	public static Date add(Date dateTime, int amount, int field){
		if(dateTime == null){
			throw new NullPointerException("parameter dateTime can't be null");
		}
		
		Calendar c = Calendar.getInstance();
		c.setTime(dateTime);
		c.add(field, amount);
		return c.getTime();
	}
	/**
	 * 给日期类型对象增加相应的天数
	 * @param dateTime 日期对象
	 * @param amount 增量(以天为单位)
	 * @return
	 */
	public static Date add(Date dateTime, int amount){
		return add(dateTime, amount, Calendar.DAY_OF_MONTH);
	}
	
	/**
	 * 给日期类型对象增加相应的月数
	 * @param dateTime 日期对象
	 * @param amount 增量(以月为单位)
	 * @return
	 */
	public static Date addMonth(Date dateTime, int amount) {
		return add(dateTime, amount, Calendar.MONTH);
	}
	
	/**
	 * 给日期类型对象增加相应的周数
	 * @param dateTime 日期对象
	 * @param amount 增量(以周为单位)
	 * @return
	 */
	public static Date addWeek(Date dateTime, int amount) {
		return add(dateTime, amount, Calendar.WEEK_OF_YEAR);
	}
	
	/**
	 * 给日期类型对象增加相应的年数
	 * @param dateTime 日期对象
	 * @param amount 增量(以年为单位)
	 * @return
	 */
	public static Date addYear(Date dateTime, int amount) {
		return add(dateTime, amount, Calendar.YEAR);
	}
	
	/**
	 * 给日期类型对象增加相应的小时数
	 * @param dateTime 日期对象
	 * @param amount 增量(以小时为单位)
	 * @return
	 */
	public static Date addHour(Date dateTime, int amount) {
		return add(dateTime, amount, Calendar.HOUR_OF_DAY);
	}
	
	/**
	 * 给日期类型对象增加相应的分钟数
	 * @param dateTime 日期对象
	 * @param amount 增量(以分钟为单位)
	 * @return
	 */
	public static Date addMinute(Date dateTime, int amount) {
		return add(dateTime, amount, Calendar.MINUTE);
	}
	
	/**
	 * 给日期类型对象增加相应的秒数
	 * @param dateTime 日期对象
	 * @param amount 增量(以秒为单位)
	 * @return
	 */
	public static Date addSecond(Date dateTime, int amount) {
		return add(dateTime, amount, Calendar.SECOND);
	}

	/**
	 * 以友好的方式显示时间
	 * @param time
	 * @return
	 */
	public static String friendlyTime( Date time) {
		if(time == null) return "未知";

		int ct = (int)((System.currentTimeMillis() - time.getTime())/1000);
		if(ct < 3600)
			return Math.max(ct / 60,1)+"分钟前";
		if(ct >= 3600 && ct < 86400)
			return (ct / 3600)+"小时前";
		if(ct >= 86400 && ct < 2592000){ //86400 * 30
			int day = ct / 86400 ;
			return day+"天前";
		}
		if(ct >= 2592000 && ct < 31104000) //86400 * 30
			return ct / 2592000+"月前";
		return ct / 31104000+"年前";
	}

}
