package com.yipeng.framework.core.utils;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * lang工具类
 * @filename: LangUtils.java
 * @description: 
 * @author:   yongdongliang
 * @version:  1.0  
 * @Date:     2017年5月26日 下午3:20:15 
 * @copyright (C) 长沙亿朋信息科技有限公司
 *
 */
public abstract class LangUtils {
	
	/**
	 * 判断Boolean包装对象是否为true
	 * @param booleanObj
	 * @return
	 */
	public static boolean isTrue(Boolean booleanObj) {
		return (null != booleanObj && booleanObj) ? true : false;
	}
	
	
	/**
	 * 判断是否都相等
	 * @param data1
	 * @param data2
	 * @return
	 */
	public static boolean gt(Number data1, Number data2) {
		if (null != data1 && null != data2) {
			return new BigDecimal(data1.toString()).compareTo(new BigDecimal(data2.toString())) > 0;
		}
		return false;
//		return !lteq(data1, data2);//数据为null时这里会返回true
	}

	public static boolean lt(Number data1, Number data2) {
		if (null != data1 && null != data2) {
			return new BigDecimal(data1.toString()).compareTo(new BigDecimal(data2.toString())) < 0;
		}
		return false;
//		return !gteq(data1, data2);
	}

	public static boolean lteq(Number data1, Number data2) {
		if (null != data1 && null != data2) {
			return new BigDecimal(data1.toString()).compareTo(new BigDecimal(data2.toString())) <= 0;
		}
		return false;
	}

	public static boolean gteq(Number data1, Number data2) {
		if (null != data1 && null != data2) {
			return new BigDecimal(data1.toString()).compareTo(new BigDecimal(data2.toString())) >= 0;
		}
		return false;
	}

	/**
	 * 判断是否都相等
	 * @param data1
	 * @param data2
	 * @return
	 */
	public static boolean equals(Long data1, Long data2) {
		if (null != data1 && null != data2) {
			return data1.longValue() == data2.longValue();
		} 
		return false;
	}
	
	/**
	 * 判断两个Integer对象是否相等
	 * @param data1
	 * @param data2
	 * @return
	 */
	public static boolean equals(Integer data1, Integer data2) {
		if (null != data1 && null != data2) {
			return data1.intValue() == data2.intValue();
		} 
		return false;
	}
	
	
	/**
	 * 判断是否都相等
	 * @param data1
	 * @param data2
	 * @return
	 */
	public static boolean equals(String data1, String data2) {
		if (null != data1 && null != data2) {
			return data1.equals(data2);
		} 
		return false;
	}
	
	public static boolean eq(String data1, String data2) {
		return equals(data1, data2);
	}
	
	/**
	 * 判断是有效的ID
	 * @param id
	 * @return
	 */
	public static boolean isValidID(Long id) {
		return (null != id && id > 0) ? true : false;
	}
	
	/**
	 * 判断是无效的ID
	 * @param id
	 * @return
	 */
	public static boolean isNotValidID(Long id) {
		return !isValidID(id);
	}
	
	/**
	 * 将字符串转换成unicode编码
	 * @param string
	 * @return
	 */
	public static String string2Unicode(String string) {
	        StringBuffer unicode = new StringBuffer();
	        for (int i = 0; i < string.length(); i++) {
	            // 取出每一个字符
	            char c = string.charAt(i);
	            // 转换为unicode
	            unicode.append("\\u" + Integer.toHexString(c));
	        }
	        return unicode.toString();
    }
	
	/**
	 * 判断是否是中文
	 * @param str
	 * @return
	 */
	public static boolean isChineseString(String str) {
		String regEx = "[\u4e00-\u9fa5]";
		Pattern pat = Pattern.compile(regEx);
		Matcher matcher = pat.matcher(str);
		if (matcher.find()) {
			return true;
		}
		return false;
	}
}
