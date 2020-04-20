package com.yipeng.framework.common.service.converter;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ReflectUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.yipeng.framework.common.constants.CaseRule;
import com.yipeng.framework.common.constants.Direction;
import com.yipeng.framework.common.constants.annotation.ConvertExclude;
import com.yipeng.framework.common.constants.annotation.ConvertInclude;
import com.yipeng.framework.common.constants.annotation.FieldMapping;
import com.yipeng.framework.common.exception.ErrorCode;
import com.yipeng.framework.common.exception.ExceptionUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 数据库对象与目标结果对象之间的转换
 * @author: yibingzhou
 */
@Slf4j
public class ModelResultConverter {
    private Map<Class, Set<String>> includeMap = new ConcurrentHashMap<>();
    private Map<Class, Set<String>> excludeMap = new ConcurrentHashMap<>();
    private Map<Class, List<Field>> fieldsMap = new ConcurrentHashMap<>();
    private Map<Class, Set<String>> fieldNamesMap = new ConcurrentHashMap<>();
    private Map<Class, Converter> convertersMap = new ConcurrentHashMap<>();

    /**
     * 提取class里的元数据
     * @param clazz
     */
    public void fetchMeta(Class clazz) {
        excludeMap.computeIfAbsent(clazz, (clz) -> {
            Annotation convertExclude =  clz.getAnnotation(ConvertExclude.class);
            Annotation convertInclude =  clz.getAnnotation(ConvertInclude.class);
            String[] includes = null;
            String[] excludes = null;
            if(convertInclude != null) {
                includes = ((ConvertInclude)convertInclude).value();
            }
            if(convertExclude != null) {
                excludes = ((ConvertExclude)convertExclude).value();
            }
            Set<String> excludeSet = Sets.newHashSet();
            Set<String> includeSet = Sets.newHashSet();
            if(includes !=null && includes.length>0) {
                includeSet = Sets.newHashSet(includes);
                includeMap.put(clz, includeSet);
            } else {
                includeMap.put(clz,includeSet);
            }
            if(excludes != null && excludes.length >0) {
                excludeSet = Sets.newHashSet(excludes);
                if(Sets.intersection(excludeSet, includeSet).size()>0)
                    throw ExceptionUtil.doThrow(ErrorCode.ILLEGAL_ARGUMENT.msg("需要转换字段和忽略字段列表中不能有重复:exclude="+excludeSet+",include="+includeSet));
            }
            return excludeSet;
        });
        fieldsMap.computeIfAbsent(clazz, (clz) -> {
            Field[] allFields = ReflectUtil.getFields(clz);
            Set<String> fieldNames = Sets.newHashSetWithExpectedSize(allFields.length);
            List<Field> fields = Lists.newArrayListWithExpectedSize(allFields.length);
            for(Field f: allFields) {
                //过滤掉静态字段，final字段,常量
                if(Modifier.isFinal(f.getModifiers()) || Modifier.isStatic(f.getModifiers())){
                    continue;
                }
                f.setAccessible(true);//预先设置允许访问
                fieldNames.add(f.getName());
                fields.add(f);
            }
            fieldNamesMap.put(clz, fieldNames);
            return fields;
        });
    }

    /**
     * 获取class的所有字段名（除去fianl,static修饰的字段）
     * @param clazz
     * @return
     */
    public Set<String> getFieldNames(Class clazz) {
        Set<String> names = fieldNamesMap.get(clazz);
        if(null == names) {
            fetchMeta(clazz);
            names = fieldNamesMap.get(clazz);
        }
        return names;
    }

    public <T> Map<String, Object> fieldValueMap(T o) {
        Field[] allFields = ReflectUtil.getFields(o.getClass());
        Map<String, Object> ret = Maps.newHashMap();
        try {
            for (Field f : allFields) {
                //过滤掉静态字段，final字段,常量
                if (Modifier.isFinal(f.getModifiers()) || Modifier.isStatic(f.getModifiers())) {
                    continue;
                }
                ret.put(f.getName(), f.get(o));
            }
        } catch (Exception e) {

        }
        return ret;
    }

    /**
     * 获取class的所有字段（除去fianl,static修饰的字段）
     * @param clazz
     * @return
     */
    public List<Field> getFields(Class clazz) {
        List<Field> fields = fieldsMap.get(clazz);
        if( fields == null) {
            fetchMeta(clazz);
            fields = fieldsMap.get(clazz);
        }
        return fields;
    }

    public <T> void convert(Object[] kvs, T dbModel) {
        convert(kvs, dbModel, false, null);
    }

    public <T> void convert(Object[] kvs, T dbModel, boolean ignoreCase, Set<String> ignoreFields) {
        if(kvs == null || kvs.length == 0 || kvs.length%2 !=0) {
            throw ExceptionUtil.doThrow(ErrorCode.ILLEGAL_ARGUMENT.msg("名值对数组必须成对"));
        }
        List<Field> fields = getFields(dbModel.getClass());
        if(CollectionUtil.isEmpty(fields)) return;
        Map<String, Field> nameFieldMap = fields.stream().collect(Collectors.toMap(field -> {
            if(ignoreCase) {
                return field.getName().toLowerCase();
            } else {
                return field.getName();
            }
        }, field -> field));
        try {
            for(int i=0;i<kvs.length;i+=2) {
                if(ignoreFields == null || !ignoreFields.contains(kvs[i])) {
                    Field field = nameFieldMap.get(ignoreCase ? ((String)kvs[i]).toLowerCase() : (String)kvs[i]);
                    if(field != null) {
                        try {
                            field.set(dbModel, kvs[i+1]);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("convert failed", e);
            throw ExceptionUtil.doThrow(ErrorCode.SERVER_INTERNAL_ERROR.msg(e.getMessage()));
        }
    }

    /**
     * 从map里提取字段并设置到dbModel对象里
     * @param params 字段名-字段值参数map
     * @param dbModel 目标对象
     * @param <T>
     */
    public <T> void convert(Map<String, Object> params, T dbModel) {
        convert(params, dbModel, false, null);
    }

    /**
     * 从map里提取字段并设置到target对象里
     * @param params 字段名-字段值参数map
     * @param dbModel 目标对象
     * @param ignoreCase 是否忽略大小写
     * @param ignoreFields 忽略的字段列表
     * @param <T>
     */
    public <T> void convert(Map<String, Object> params, T dbModel, boolean ignoreCase, Set<String> ignoreFields) {
        if(CollectionUtil.isEmpty(params) || dbModel == null) return;
        List<Field> fields = getFields(dbModel.getClass());
        if(CollectionUtil.isEmpty(fields)) return;
        Map<String, Field> nameFieldMap = fields.stream().collect(Collectors.toMap(field -> {
            if(ignoreCase) {
                return field.getName().toLowerCase();
            } else {
                return field.getName();
            }
        }, field -> field));
        try {
            params.forEach((k, v) -> {
                if(ignoreFields ==null || !ignoreFields.contains(k)) {
                    Field field = nameFieldMap.get(ignoreCase ? k.toLowerCase() : k);
                    if(field != null) {
                        try {
                            field.set(dbModel, v);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            });
        } catch (Exception e) {
            log.error("convert failed", e);
            throw ExceptionUtil.doThrow(ErrorCode.SERVER_INTERNAL_ERROR.msg(e.getMessage()));
        }
    }
    public <S> void convert(S source, Map<String, Object> target, Set<String> extraIgnoreFields, Set<String> notIgnoreFields) {
        try {
            target.putAll(getFieldMap(source, extraIgnoreFields, notIgnoreFields));
        } catch (Exception e) {
            log.error("convert failed", e);
            throw ExceptionUtil.doThrow(ErrorCode.SERVER_INTERNAL_ERROR.msg(e.getMessage()));
        }
    }

    public <S,T> void convert(S source, T target) {
        convert(source, target, null, null);
    }

    /**
     * 将参数对象转换给target对象
     * @param source 源对象
     * @param target 目标对象
     * @param extraIgnoreFields 额外忽略字段列表
     * @param notIgnoreFields 不需要忽略的字段列表
     */
    public <S,T> void convert(S source, T target, Set<String> extraIgnoreFields, Set<String> notIgnoreFields) {
        Class targetClass = target.getClass();
        fetchMeta(targetClass);
        List<Field> targetClassFields = fieldsMap.get(targetClass);
        try {
            Map<String,Object> paramFieldMap = getFieldMap(source, extraIgnoreFields, notIgnoreFields);
            //将param拷贝给target
            for(Field f : targetClassFields) {
                List<FieldMappingResult> fieldMappingResults = getFieldMappingResult(f, Direction.IN);
                Converter converter = null;
                Object value = null;
                for(FieldMappingResult fieldIn : fieldMappingResults) {
                    if(CaseRule.IGNORE_CASE == fieldIn.caseRule) {
                        value = paramFieldMap.get(f.getName().toLowerCase());
                    } else {
                        value = paramFieldMap.get(f.getName());
                    }
                    if(null == value) continue;
                    converter = getConverter(fieldIn);
                    break;//选择第一个
                }

                value = null == value ? paramFieldMap.get(f.getName()) : value;
                setFieldValue(f, target, value, converter);
            }

        }catch (Exception e) {
            log.error("convert failed", e);
            throw ExceptionUtil.doThrow(ErrorCode.SERVER_INTERNAL_ERROR.msg(e.getMessage()));
        }
    }

    private <S> Map<String,Object> getFieldMap(S param, Set<String> extraIgnoreFields, Set<String> notIgnoreFields) throws IllegalAccessException {
        Class sourceClass = param.getClass();
        fetchMeta(sourceClass);
        Set<String> excludeList = calcExcludeFields(sourceClass, extraIgnoreFields);
        Set<String> includeList = calIncludeFields(sourceClass,notIgnoreFields);
        List<Field> sourceClassFields = fieldsMap.get(sourceClass);
        Map<String, Object> paramFieldMap = Maps.newHashMapWithExpectedSize(sourceClassFields.size());
        for (Field f : sourceClassFields) {
            ConvertExclude convertExclude = f.getAnnotation(ConvertExclude.class);
            //过滤掉不需要转换字段
            if ((convertExclude == null && !excludeList.contains(f.getName())) || includeList.contains(f.getName())) {
                List<FieldMappingResult> fieldMappingResults = getFieldMappingResult(f, Direction.OUT);
                boolean mappedSelf = false;
                for(FieldMappingResult fieldOut : fieldMappingResults) {
                    Converter converter = getConverter(fieldOut);
                    Object value = f.get(param);
                    if(fieldOut.fieldName.equals(f.getName())) {
                        mappedSelf = true;
                    }
                    if(converter != null) {
                        value = convertValue(f, value, converter);
                    }
                    if(CaseRule.IGNORE_CASE == fieldOut.caseRule) {
                        paramFieldMap.put(fieldOut.fieldName.toLowerCase(), value);
                    } else {
                        paramFieldMap.put(fieldOut.fieldName, value);
                    }
                }
                //字段原始名需要加入
                if(!mappedSelf) {
                    paramFieldMap.put(f.getName(), f.get(param));
                }
            }
        }
        return paramFieldMap;
    }

    private Set<String> calcExcludeFields(Class clazz, Set<String> extraIgnoreFields) {
        Set<String> excludeList = excludeMap.get(clazz);
        if(null != extraIgnoreFields) {
            excludeList.addAll(extraIgnoreFields);
        }
        return excludeList;
    }

    private Set<String> calIncludeFields(Class clazz,Set<String> includeFields) {
        Set<String> includeList = includeMap.get(clazz);
        if(null != includeFields) {
            includeList.addAll(includeFields);
        }
        return includeList;
    }

    private List<FieldMappingResult> getFieldMappingResult(Field field, Direction direction) {
        FieldMapping[] fieldMappings = field.getAnnotationsByType(FieldMapping.class);
        if(null == fieldMappings) return Lists.newArrayList();
        List<FieldMappingResult> fieldMappingResults = Lists.newArrayListWithExpectedSize(fieldMappings.length);
        for(FieldMapping fieldMapping : fieldMappings) {
            String fieldName = fieldMapping.name();
            Class converterClass = fieldMapping.converter();
            if(StringUtils.EMPTY.equals(fieldName)) {
                fieldName = field.getName();
            }
            boolean isConverter = converterClass == Converter.class ? false : Converter.class.isAssignableFrom(converterClass);
            if(direction == null || Direction.BOTH == direction || fieldMapping.direction() == direction) {
                fieldMappingResults.add(new FieldMappingResult(fieldName, converterClass, isConverter, fieldMapping.caseRule(), fieldMapping.direction()));
            }
        }

        return fieldMappingResults;
    }

    private Object convertValue(Field field, Object value, Converter converter) {
        if(converter != null) {
            if(field.getType() == converter.targetClass()) {
                return converter.reverse(value);
            } else if( field.getType() == converter.sourceClass()){
                return converter.convert(value);
            }
        }
        return value;
    }

    private <T,V> void setFieldValue(Field field, T target, V value, Converter converter) throws IllegalAccessException {
        if(value == null) return;
        if(converter != null) {
            if(field.getType() == converter.targetClass()) {
                field.set(target, converter.convert(value));
            } else if( field.getType() == converter.sourceClass()){
                field.set(target, converter.reverse(value));
            }
        } else if(field.getType() == value.getClass()){
            field.set(target, value);
        } else {
            //尝试转换类型
            Object targetValue = cn.hutool.core.convert.Convert.convert(field.getType() , value);
            field.set(target, targetValue);
        }
    }

    private Converter getConverter(FieldMappingResult fieldMappingResult) {
        if(fieldMappingResult.converterClass != Converter.class && fieldMappingResult.legalConverter) {
            //忽略默认转换器
            Converter converter = convertersMap.computeIfAbsent(fieldMappingResult.converterClass, (clz) -> {
                try {
                    return (Converter) clz.newInstance();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
            return converter;
        }
        return null;
    }


    @AllArgsConstructor
    class FieldMappingResult{
        private String fieldName;
        private Class converterClass;
        private boolean legalConverter;
        private CaseRule caseRule;
        private Direction direction;
    }
}
