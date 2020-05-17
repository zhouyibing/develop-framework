package com.yipeng.framework.core.utils;
/**
 * @author lyf
 * @version 1.0
 * @Date 2019年4月19日 下午11:39:53
 * @copyright (c) 长沙亿朋信息科技有限公司
 */

import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.Transient;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;

public class ModelUtil {

	public static String methodGet(String field) {
		if (StringUtils.isBlank(field)) {
			return "";
		}
		return "get" + (field.charAt(0) + "").toUpperCase() + field.substring(1);
	}

	public static String methodSet(String field) {
		if (StringUtils.isBlank(field)) {
			return "";
		}
		return "set" + (field.charAt(0) + "").toUpperCase() + field.substring(1);
	}

	/**
	 * 获取所有属性，包括所有超类
	 * 
	 * @param clazz
	 * @return
	 */
	public static List<Field> listAllFields(Class<?> clazz) {
		List<Field> fieldList = Lists.newArrayList();
		while (true) {
			Field[] fields = clazz.getDeclaredFields();
			for (int i = 0; i < fields.length; i++) {
				fieldList.add(fields[i]);
			}
			if (clazz.getSuperclass() != null) {
				clazz = clazz.getSuperclass();
			} else {
				break;
			}
		}
		return fieldList;
	}

	/**
	 * 获取符合实体标准的属性（有set方法，无static，final修饰，没有Transient注解），包括所有超类
	 * 
	 * @param clazz
	 * @return
	 */
	public static List<Field> listValidModelFields(Class<?> clazz) {
		List<Field> fieldList = listAllFields(clazz);
		List<Field> validList = Lists.newArrayList();
		for (int i = 0; i < fieldList.size(); i++) {
			Field field = fieldList.get(i);
			int mod = field.getModifiers();
			try {
				if (!Modifier.isStatic(mod) && !Modifier.isFinal(mod) && field.getAnnotation(Transient.class) == null
						&& clazz.getMethod(methodSet(field.getName()), field.getType()) != null) {
					validList.add(field);
				}
			} catch (Exception e) {
				// 没有set方法，忽略字段
				continue;
			}
		}
		return validList;
	}

}
