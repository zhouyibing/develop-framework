package com.fido.framework.core.utils;

import org.apache.commons.lang3.time.DateFormatUtils;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * 日期工具类, 继承org.apache.commons.lang.time.DateUtils类
 *
 * @filename: DateUtils.java
 * @version: 1.0
 * @Date: 2015年7月31日 下午12:23:21
 * @see jeesite DateUtils
 *
 */
public class DateUtils extends org.apache.commons.lang3.time.DateUtils {

	public static final String PATTERN_STANDARD = "yyyy-MM-dd HH:mm:ss";

	private static String[] parsePatterns = { "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm:ss SSS",
			"yyyy-MM-dd HH:mm", "yyyy-MM", "yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm", "yyyy/MM",
			"yyyy.MM.dd", "yyyy.MM.dd HH:mm:ss", "yyyy.MM.dd HH:mm", "yyyy.MM", "yyyyMMddHHmmss" };

	/**
	 * 得到当前日期字符串 格式（yyyy-MM-dd）
	 */
	public static String getDate() {
		return getDate("yyyy-MM-dd");
	}

	/**
	 * 得到当前日期字符串 格式（yyyy-MM-dd） pattern可以为："yyyy-MM-dd" "HH:mm:ss" "E"
	 */
	public static String getDate(String pattern) {
		return DateFormatUtils.format(new Date(), pattern);
	}

	public static Date addDaysDayEnd(Date date, int amount) {
		date = addDays(date, amount);
		return setDateDayEnd(date);
	}

	public static Date setDateDayEnd(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		return calendar.getTime();
	}

	public static boolean gt(Date date1, Date date2) {
		return date1.compareTo(date2) > 0;
	}

	public static boolean lt(Date date1, Date date2) {
		return date1.compareTo(date2) < 0;
	}

	/**
	 * 得到日期字符串 默认格式（yyyy-MM-dd） pattern可以为："yyyy-MM-dd" "HH:mm:ss" "E"
	 */
	public static String formatDate(Date date, String pattern) {
		String formatDate = null;
		if (pattern != null) {
			formatDate = DateFormatUtils.format(date, pattern);
		} else {
			formatDate = DateFormatUtils.format(date, parsePatterns[0]);
		}
		return formatDate;
	}

	/**
	 * 得到日期时间字符串，转换格式（yyyy-MM-dd HH:mm:ss）
	 */
	public static String formatDateTime(Date date) {
		return formatDate(date, PATTERN_STANDARD);
	}

	/**
	 * 得到当前时间字符串 格式（HH:mm:ss）
	 */
	public static String getTime() {
		return formatDate(new Date(), "HH:mm:ss");
	}

	/**
	 * 得到当前时分
	 *
	 * @return
	 */
	public static String getHHmm() {
		return formatDate(new Date(), "HH:mm");
	}

	/**
	 * 得到当前日期和时间字符串 格式（yyyy-MM-dd HH:mm:ss）
	 */
	public static String getDateTime() {
		return formatDate(new Date(), "yyyy-MM-dd HH:mm:ss");
	}

	/**
	 * 得到当前年份字符串 格式（yyyy）
	 */
	public static String getYear() {
		return formatDate(new Date(), "yyyy");
	}

	/**
	 * 得到当前月份字符串 格式（MM）
	 */
	public static String getMonth() {
		return formatDate(new Date(), "MM");
	}

	/**
	 * 得到当天字符串 格式（dd）
	 */
	public static String getDay() {
		return formatDate(new Date(), "dd");
	}

	/**
	 * 得到当前星期字符串 格式（E）星期几
	 */
	public static String getWeek() {
		return formatDate(new Date(), "E");
	}

	/**
	 * 日期型字符串转化为日期 格式 { "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm",
	 * "yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm", "yyyy.MM.dd",
	 * "yyyy.MM.dd HH:mm:ss", "yyyy.MM.dd HH:mm" }
	 */
	public static Date parseDate(String dateStr) {
		if (dateStr == null) {
			return null;
		}
		try {
			return parseDate(dateStr, parsePatterns);
		} catch (ParseException e) {
			return null;
		}
	}

	/**
	 * 获取过去的天数
	 *
	 * @param date
	 * @return
	 */
	public static long pastDays(Date date) {
		long t = System.currentTimeMillis() - date.getTime();
		return t / (24 * 60 * 60 * 1000);
	}

	/**
	 * 获取过去的小时
	 *
	 * @param date
	 * @return
	 */
	public static long pastHour(Date date) {
		long t = System.currentTimeMillis() - date.getTime();
		return t / (60 * 60 * 1000);
	}

	/**
	 * 获取过去的分钟
	 *
	 * @param date
	 * @return
	 */
	public static long pastMinutes(Date date) {
		long t = System.currentTimeMillis() - date.getTime();
		return t / (60 * 1000);
	}

	/**
	 * 转换为时间（天,时:分:秒.毫秒）
	 *
	 * @param timeMillis
	 * @return
	 */
	public static String formatDateTime(long timeMillis) {
		long day = timeMillis / (24 * 60 * 60 * 1000);
		long hour = (timeMillis / (60 * 60 * 1000) - day * 24);
		long min = ((timeMillis / (60 * 1000)) - day * 24 * 60 - hour * 60);
		long s = (timeMillis / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
		long sss = (timeMillis - day * 24 * 60 * 60 * 1000 - hour * 60 * 60 * 1000 - min * 60 * 1000 - s * 1000);
		return (day > 0 ? day + "," : "") + hour + ":" + min + ":" + s + "." + sss;
	}

	/**
	 * 获取两个日期之间的天数
	 *
	 * @param before
	 * @param after
	 * @return
	 */
	public static double getDistanceOfTwoDate(Date before, Date after) {
		if (after != null && before != null) {
			long beforeTime = before.getTime();
			long afterTime = after.getTime();
			return (afterTime - beforeTime) / (1000 * 60 * 60 * 24);
		} else {
			return 0;
		}
	}

	/**
	 * 计算两个日期之间相差的天数
	 *
	 * @param smdate 较小的时间
	 * @param bdate  较大的时间
	 * @return 相差天数
	 * @throws ParseException
	 */
	public static int daysBetween(Date smdate, Date bdate) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			smdate = sdf.parse(sdf.format(smdate));
			bdate = sdf.parse(sdf.format(bdate));
			Calendar cal = Calendar.getInstance();
			cal.setTime(smdate);
			long time1 = cal.getTimeInMillis();
			cal.setTime(bdate);
			long time2 = cal.getTimeInMillis();
			long betweenDays = (time2 - time1) / (1000 * 3600 * 24);
			return Integer.parseInt(String.valueOf(betweenDays));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * 字符串的日期格式的计算
	 */
	public static int daysBetween(String smdate, String bdate) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Calendar cal = Calendar.getInstance();
			cal.setTime(sdf.parse(smdate));
			long time1 = cal.getTimeInMillis();
			cal.setTime(sdf.parse(bdate));
			long time2 = cal.getTimeInMillis();
			long betweenDays = (time2 - time1) / (1000 * 3600 * 24);
			return Integer.parseInt(String.valueOf(betweenDays));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * 获取两个日期之间的天数
	 *
	 * @param before
	 * @param after
	 * @return
	 */
	public static int getDistanceDays(Date before, Date after) {
		if (after != null && before != null) {

			long beforeTime = before.getTime();
			long afterTime = after.getTime();
			if (afterTime > beforeTime) {

				Calendar calendarbefore = Calendar.getInstance();
				calendarbefore.setTime(before);

				Calendar calendarafter = Calendar.getInstance();
				calendarafter.setTime(after);
				int days = 0;
				// 如是跨了年
				if (calendarbefore.get(Calendar.YEAR) != calendarafter.get(Calendar.YEAR)) {

					int beforedays = calendarbefore.get(Calendar.DAY_OF_YEAR);

					int afterdays = calendarbefore.getActualMaximum(Calendar.DAY_OF_YEAR)
							+ calendarafter.get(Calendar.DAY_OF_YEAR);

					days = afterdays - beforedays;
				} else {
					days = calendarafter.get(Calendar.DAY_OF_YEAR) - calendarbefore.get(Calendar.DAY_OF_YEAR);
				}
				return days;// +(days==0?1:0);

			} else {// 已过期
				return -1;
			}
		} else {
			return 0;
		}
	}

	/**
	 * 指定毫秒数的时间戳
	 *
	 * @param millis 毫秒数
	 * @return 指定毫秒数的时间戳
	 */
	public static Timestamp getTimestamp(long millis) {
		return new Timestamp(millis);
	}

	/**
	 * 以字符形式表示的时间戳
	 *
	 * @param time 毫秒数
	 * @return 以字符形式表示的时间戳
	 */
	public static Timestamp getTimestamp(String time) {
		return new Timestamp(Long.parseLong(time));
	}

	/**
	 * 系统当前的时间戳
	 *
	 * @return 系统当前的时间戳
	 */
	public static Timestamp getTimestamp() {
		return new Timestamp(System.currentTimeMillis());
	}

	/**
	 * 指定日期的时间戳
	 *
	 * @param date 指定日期
	 * @return 指定日期的时间戳
	 */
	public static Timestamp getTimestamp(Date date) {
		return new Timestamp(date.getTime());
	}

	/**
	 * 系统时间的毫秒数
	 *
	 * @return 系统时间的毫秒数
	 */
	public static long getMillis() {
		return System.currentTimeMillis();
	}

	/**
	 * 指定日历的毫秒数
	 *
	 * @param cal 指定日历
	 * @return 指定日历的毫秒数
	 */
	public static long getMillis(Calendar cal) {
		return cal.getTime().getTime();
	}

	/**
	 * 指定日期的毫秒数
	 *
	 * @param date 指定日期
	 * @return 指定日期的毫秒数
	 */
	public static long getMillis(Date date) {
		return date.getTime();
	}

	/**
	 * 指定时间戳的毫秒数
	 *
	 * @param ts 指定时间戳
	 * @return 指定时间戳的毫秒数
	 */
	public static long getMillis(Timestamp ts) {
		return ts.getTime();
	}

	/**
	 * @param args
	 * @throws ParseException
	 */
	public static void main(String[] args) throws ParseException {
		// System.out.println(formatDate(parseDate("2010/3/6")));
		// System.out.println(getDate("yyyy年MM月dd日 E"));
		// long time = new Date().getTime()-parseDate("2012-11-19").getTime();
		// System.out.println(time/(24*60*60*1000));
//		Calendar calendar = Calendar.getInstance();
//		System.out.println(calendar.getActualMaximum(Calendar.DAY_OF_YEAR));
		// double a11= new BigDecimal(11111).divide(new BigDecimal(1000 * 60 * 60 * 24),
		// 2, BigDecimal.ROUND_HALF_EVEN).doubleValue();
//		System.out.println(calendar.get(Calendar.DAY_OF_YEAR));
		double day1 = DateUtils.getDistanceOfTwoDate(new SimpleDateFormat("yyyy-MM-dd").parse("1900-03-18"),
				new Date());
		int day = getDistanceDays(new SimpleDateFormat("yyyy-MM-dd HH:mm").parse("2016-03-18 19:00"), new Date());

		// getAgeByBirthday(parseDate("2001-06-21"))
		System.out.println(day1);
		System.out.println(day);
	}

	/**
	 * 根据用户生日计算年龄
	 */
	public static int getAgeByBirthday(Date birthday) {
		if (birthday == null) {
			return 0;
		}
		Calendar cal = Calendar.getInstance();

		if (cal.before(birthday)) {
			throw new IllegalArgumentException("The birthDay is before Now.It's unbelievable!");
		}

		int yearNow = cal.get(Calendar.YEAR);
		int monthNow = cal.get(Calendar.MONTH) + 1;
		int dayOfMonthNow = cal.get(Calendar.DAY_OF_MONTH);

		cal.setTime(birthday);
		int yearBirth = cal.get(Calendar.YEAR);
		int monthBirth = cal.get(Calendar.MONTH) + 1;
		int dayOfMonthBirth = cal.get(Calendar.DAY_OF_MONTH);

		int age = yearNow - yearBirth;

		if (monthNow <= monthBirth) {
			if (monthNow == monthBirth) {
				// monthNow==monthBirth
				if (dayOfMonthNow < dayOfMonthBirth) {
					age--;
				}
			} else {
				// monthNow>monthBirth
				age--;
			}
		}
		return age;
	}

	/**
	 * 获取最后一次位置时间描述
	 *
	 * @return
	 */
	public static String getLastloctimelabel(Date lastlocationtime) {
		String desc = "";
		if (null != lastlocationtime) {
			Date now = new Date();
			long between = (now.getTime() - lastlocationtime.getTime()) / 1000;// 除以1000是为了转换成秒

			long days = between / (24 * 3600);
			long hours = between % (24 * 3600) / 3600;
			long minutes = between % 3600 / 60;
			if (days < 3) {
				if (days < 1) {
					if (hours < 2) {
						if (minutes < 30) {
							desc = "刚刚";
						} else {
							desc = "1小时";
						}
					} else {
						desc = hours + "小时";
					}
				} else if (days > 1 && days < 2) {
					// desc = "1天前";
					desc = "昨天";

				} else {
					// desc = "2天前";
					desc = "前天";
				}
			} else {
				desc = days + "天前";
			}
		}
		return desc;
	}

	/****
	 * 比较两个时间
	 *
	 * @param firstDate
	 * @param secondDate
	 * @return
	 */
	public static boolean compareDate(Date firstDate, Date secondDate) {
		if (firstDate == null || secondDate == null) {
			throw new RuntimeException();
		}

		String strFirstDate = formatDateTime(firstDate);
		String strSecondDate = formatDateTime(secondDate);
		if (strFirstDate.compareTo(strSecondDate) > 0) {
			return true;
		}
		return false;
	}

	/***
	 * 比较时间大小（long getTime）
	 *
	 * @param firstDate
	 * @param secondDate
	 * @return
	 */
	public static boolean compareDate(long firstDate, long secondDate) {
		if (firstDate > secondDate) {
			return true;
		}
		return false;
	}

	/**
	 * 获取指定时间距当前时间的描述
	 *
	 * @return
	 */
	public static String getTimeDesc(Date date) {
		long second = 1000;
		long minute = second * 60;
		long hour = minute * 60;
		long day = hour * 24;
		long month = day * 30;
		long year = month * 12;
		long diffValue = System.currentTimeMillis() - date.getTime();
		if (diffValue < 0) {
			return null;
		}
		long yearC = diffValue / year;
		long monthC = diffValue / month;
		long weekC = diffValue / (7 * day);
		long dayC = diffValue / day;
		long hourC = diffValue / hour;
		long minC = diffValue / minute;
		long secondC = diffValue / second;
		String result = "";
		if (yearC > 0) {
			result = yearC + "年前";
		} else if (monthC >= 1) {
			result = monthC + "月前";
		} else if (weekC >= 1) {
			result = weekC + "周前";
		} else if (dayC >= 1) {
			result = dayC + "天前";
		} else if (hourC >= 1) {
			result = hourC + "小时前";
		} else if (minC >= 1) {
			result = minC + "分钟前";
		} else if (secondC > 0) {
			result = secondC + "秒前";
		} else {
			result = "刚刚";
		}
		return result;
	}

	/***
	 * 计算宝宝年龄
	 *
	 * @param date
	 * @return
	 */
	public static String getAge(String date) {
		String[] data = date.split("-");

		if (data.length < 3) {
			return "";
		}

		Calendar birthday = new GregorianCalendar(Integer.valueOf(data[0]), Integer.valueOf(data[1]),
				Integer.valueOf(data[2]));

		Calendar now = Calendar.getInstance();

		int day = now.get(Calendar.DAY_OF_MONTH) - birthday.get(Calendar.DAY_OF_MONTH);
		// 月份从0开始计算，所以需要+1
		int month = now.get(Calendar.MONTH) + 1 - birthday.get(Calendar.MONTH);

		int year = now.get(Calendar.YEAR) - birthday.get(Calendar.YEAR);

		// 按照减法原理，先day相减，不够向month借；然后month相减，不够向year借；最后year相减。

		if (day < 0) {

			month -= 1;

			now.add(Calendar.MONTH, -1);// 得到上一个月，用来得到上个月的天数。

			day = day + now.getActualMaximum(Calendar.DAY_OF_MONTH);

		}

		if (month < 0) {

			month = (month + 12) % 12;

			year--;

		}

		System.out.println("年龄：" + year + "岁" + month + "月" + day + "天");

		StringBuffer tag = new StringBuffer();

		if (year > 0) {

			tag.append(year + "岁");

		}

		if (month > 0) {

			tag.append(month + "个月");

		}

		if (day > 0) {

			tag.append(day + "天");

		}

		if (year == 0 && month == 0 && day == 0) {

			tag.append("今日出生");

		}

		return String.valueOf(tag);
	}

	public static String getAfterMonth(String inputDate, int number) {
		Calendar c = Calendar.getInstance();// 获得一个日历的实例
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date date = null;
		try {
			date = sdf.parse(inputDate);// 初始日期
		} catch (Exception e) {

		}
		c.setTime(date);// 设置日历时间
		c.add(Calendar.MONTH, number);// 在日历的月份上增加6个月
		String strDate = sdf.format(c.getTime());// 的到你想要得6个月后的日期
		return strDate;
	}

	/** 转化为可读的持续时间格式 */
	public static String prettyTime(Number detTime) {
		if (detTime == null) {
			return null;
		}

		long time = detTime.longValue();
		StringBuilder buf = new StringBuilder(128);
		int tmp = (int) (time / (24 * 3600 * 1000));
		if (tmp > 0) {
			buf.append(tmp).append('天');
		}
		time %= (24 * 3600 * 1000);
		if (time == 0) {
			return buf.length() == 0 ? "0秒" : buf.toString();
		}

		tmp = (int) (time / (3600 * 1000));
		if (tmp > 0) {
			buf.append(tmp).append('时');
		}
		time %= (3600 * 1000);
		if (time == 0) {
			return buf.length() == 0 ? "0秒" : buf.toString();
		}

		tmp = (int) (time / (60 * 1000));
		if (tmp > 0) {
			buf.append(tmp).append('分');
		}
		time %= (60 * 1000);
		if (time == 0) {
			return buf.length() == 0 ? "0秒" : buf.toString();
		}

		buf.append(time / 1000f).append('秒');
		return buf.toString();
	}
}
