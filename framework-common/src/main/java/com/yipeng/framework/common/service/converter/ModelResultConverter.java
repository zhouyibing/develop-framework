package com.yipeng.framework.common.service.converter;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ReflectUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.yipeng.framework.common.constants.ConvertRule;
import com.yipeng.framework.common.constants.annotation.Convert;
import com.yipeng.framework.common.constants.annotation.ConvertIgnore;
import com.yipeng.framework.common.constants.annotation.FieldMapping;
import com.yipeng.framework.common.exception.ErrorCode;
import com.yipeng.framework.common.exception.ExceptionUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 数据库对象与目标结果对象之间的转换
 * @author: yibingzhou
 */
@Slf4j
public class ModelResultConverter {
    private Map<Class, Set<String>> ignoresMap = new ConcurrentHashMap<>();
    private Map<Class, ConvertRule> convertRuleMap = new ConcurrentHashMap<>();
    private Map<Class, List<Field>> fieldsMap = new ConcurrentHashMap<>();
    private Map<Class, Set<String>> fieldNamesMap = new ConcurrentHashMap<>();
    private Map<Class, Converter> convertersMap = new ConcurrentHashMap<>();

    /**
     * 提取class里的元数据
     * @param clazz
     */
    public void fetchMeta(Class clazz) {
        ignoresMap.computeIfAbsent(clazz, (clz) -> {
            Annotation convert =  clz.getAnnotation(Convert.class);
            if(convert != null) {
                String[] ignores = ((Convert)convert).ignores();
                if(ignores != null && ignores.length >0) {
                    return Sets.newHashSet(ignores);
                }
            }
            return Sets.newHashSet();
        });
        convertRuleMap.computeIfAbsent(clazz, (clz) -> {
            Annotation convert =  clz.getAnnotation(Convert.class);
            if(convert != null) {
                ConvertRule rule = ((Convert)convert).rule();
                if(rule != null) {
                    return rule;
                }
            }
            return ConvertRule.FULL_NAME;
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

    public <T> void toDbModel(Object[] kvs, T dbModel) {
        toDbModel(kvs, dbModel, false, null);
    }

    public <T> void toDbModel(Object[] kvs, T dbModel, boolean ignoreCase, Set<String> ignoreFields) {
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
    public <T> void toDbModel(Map<String, Object> params, T dbModel) {
        toDbModel(params, dbModel, false, null);
    }

    /**
     * 从map里提取字段并设置到dbModel对象里
     * @param params 字段名-字段值参数map
     * @param dbModel 目标对象
     * @param ignoreCase 是否忽略大小写
     * @param ignoreFields 忽略的字段列表
     * @param <T>
     */
    public <T> void toDbModel(Map<String, Object> params, T dbModel, boolean ignoreCase, Set<String> ignoreFields) {
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

    public <M,R> void toDbModel(R param, M dbModel) {
        toDbModel(param, dbModel, null, null);
    }

    /**
     * 将参数对象转换给dbModel对象
     * @param param 参数对象
     * @param dbModel 数据库对象
     * @param extraIgnoreFields 额外忽略字段列表
     * @param notIgnoreFields 不需要忽略的字段列表
     */
    public <M,R> void toDbModel(R param, M dbModel, Set<String> extraIgnoreFields, Set<String> notIgnoreFields) {
        Class paramClass = param.getClass();
        Class dbModelClass = dbModel.getClass();
        fetchMeta(paramClass);
        fetchMeta(dbModelClass);
        Set<String> ignoreList = calcIgnoreFields(paramClass, extraIgnoreFields, notIgnoreFields);
        List<Field> paramClassFields = fieldsMap.get(paramClass);
        List<Field> dbModelClassFields = fieldsMap.get(dbModelClass);
        ConvertRule convertRule = convertRuleMap.get(paramClass);
        try {
            Map<String, Object> paramFieldMap = Maps.newHashMapWithExpectedSize(paramClassFields.size());
            Map<String, Converter> paramFieldConverterMap = Maps.newHashMapWithExpectedSize(paramClassFields.size());
            for (Field f : paramClassFields) {
                ConvertIgnore convertIgnore = f.getAnnotation(ConvertIgnore.class);
                FieldMappingResult fieldMappingResult = getFieldMappingResult(f);
                //过滤掉不需要转换字段
                if ((convertIgnore == null && !ignoreList.contains(f.getName())) ||
                        (null != notIgnoreFields && notIgnoreFields.contains(f.getName()))) {
                    String fieldInMapping = StringUtils.isNotBlank(fieldMappingResult.fieldName) ? fieldMappingResult.fieldName : null;
                    String fieldName = fieldInMapping !=null ? fieldInMapping : f.getName();
                    if (convertRule == ConvertRule.IGNORE_CASE) {
                        fieldName = fieldInMapping != null ? fieldName.toLowerCase() : f.getName().toLowerCase();
                    }
                    paramFieldMap.put(fieldName, f.get(param));
                    Converter converter = getConverter(fieldMappingResult);
                    if(converter != null) {
                        paramFieldConverterMap.put(fieldName, converter);
                    }
                }
            }

            //将param拷贝给dbmodel
            for(Field f : dbModelClassFields) {
                Object value = null;
                Converter converter = null;
                if(convertRule == ConvertRule.IGNORE_CASE) {
                    String lowCaseFieldName = f.getName().toLowerCase();
                    value = paramFieldMap.get(lowCaseFieldName);
                    converter = paramFieldConverterMap.get(lowCaseFieldName);
                } else {
                    value = paramFieldMap.get(f.getName());
                    converter = paramFieldConverterMap.get(f.getName().toLowerCase());
                }

                if(null == value) {
                    continue;
                }
                setFieldValue(f, dbModel, value, converter);
            }

        }catch (Exception e) {
            log.error("convert failed", e);
            throw ExceptionUtil.doThrow(ErrorCode.SERVER_INTERNAL_ERROR.msg(e.getMessage()));
        }
    }
    public <M,R> void toResult(M dbModel, R result) {
        toResult(dbModel, result, null, null);
    }

    private Set<String> calcIgnoreFields(Class clazz, Set<String> extraIgnoreFields,Set<String> notIgnoreFields) {
        Set<String> ignoreList = ignoresMap.get(clazz);
        if(null != extraIgnoreFields) {
            ignoreList.addAll(extraIgnoreFields);
        }
        if(null != notIgnoreFields) {
            notIgnoreFields.forEach(fieldName -> {
                ignoreList.remove(fieldName);
            });
        }
        return ignoreList;
    }

    private FieldMappingResult getFieldMappingResult(Field field) {
        FieldMapping fieldMapping = field.getAnnotation(FieldMapping.class);
        String fieldName = null;
        Class converterClass = null;
        boolean isConverter = false;
        if(fieldMapping != null) {
            fieldName = fieldMapping.value();
            converterClass = fieldMapping.converter();
            isConverter = Converter.class.isAssignableFrom(converterClass);
            if(!isConverter) {
                log.warn("'{}' is not a converter of 'com.yipeng.framework.common.service.converter.Converter'", converterClass.getName());
            }
        }
        return new FieldMappingResult(fieldName, converterClass, isConverter);
    }

    private <T,V> void setFieldValue(Field field, T target, V value, Converter converter) throws IllegalAccessException {
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

    /**
     * 将数据库对象转换为目标结果对象
     * @param dbModel 数据库对象
     * @param result 结果对象
     * @param extraIgnoreFields 额外忽略字段列表
     * @param notIgnoreFields 不需要忽略的字段列表
     * @param <M>
     * @param <R>
     */
    public <M,R> void toResult(M dbModel, R result, Set<String> extraIgnoreFields, Set<String> notIgnoreFields) {
        Class dbModelClass = dbModel.getClass();
        Class resultClass = result.getClass();
        fetchMeta(dbModelClass);
        fetchMeta(resultClass);
        Set<String> ignoreList = calcIgnoreFields(dbModelClass, extraIgnoreFields, notIgnoreFields);

        List<Field> dbmodelClassFields = fieldsMap.get(dbModelClass);
        ConvertRule convertRule = convertRuleMap.get(dbModelClass);
        List<Field> resultClassFields = fieldsMap.get(resultClass);
        try {
            Map<String, Object> dbModelFieldMap = Maps.newHashMapWithExpectedSize(dbmodelClassFields.size());
            for (Field f : dbmodelClassFields) {
                ConvertIgnore convertIgnore = f.getAnnotation(ConvertIgnore.class);

                //过滤掉不需要转换字段
                if ((convertIgnore == null && !ignoreList.contains(f.getName())) ||
                        (null != notIgnoreFields && notIgnoreFields.contains(f.getName()))) {
                    if (convertRule == ConvertRule.FULL_NAME) {
                        dbModelFieldMap.put(f.getName(), f.get(dbModel));
                    } else if(convertRule == ConvertRule.IGNORE_CASE) {
                        dbModelFieldMap.put(f.getName().toLowerCase(), f.get(dbModel));
                    }
                }
            }
            //将source拷贝给target
            for(Field f : resultClassFields) {
                FieldMappingResult fieldMappingResult = getFieldMappingResult(f);
                String fieldInMapping = StringUtils.isNotBlank(fieldMappingResult.fieldName) ? fieldMappingResult.fieldName : null;
                String fieldName = fieldInMapping !=null ? fieldInMapping : f.getName();
                if (convertRule == ConvertRule.IGNORE_CASE) {
                    fieldName = fieldInMapping != null ? fieldName.toLowerCase() : f.getName().toLowerCase();
                }

                Object value = dbModelFieldMap.get(fieldName);

                if(null == value) {
                    continue;
                }
                Converter converter = getConverter(fieldMappingResult);
                setFieldValue(f, result, value, converter);
            }
            //clear map,help gc
            dbModelFieldMap.clear();
            ignoreList.clear();
        }catch (Exception e) {
            log.error("convert failed", e);
            throw ExceptionUtil.doThrow(ErrorCode.SERVER_INTERNAL_ERROR.msg(e.getMessage()));
        }
    }

    @AllArgsConstructor
    class FieldMappingResult{
        private String fieldName;
        private Class converterClass;
        private boolean legalConverter;
    }
}
