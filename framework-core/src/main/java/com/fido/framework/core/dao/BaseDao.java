package com.fido.framework.core.dao;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.page.PageMethod;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.fido.framework.core.constants.BooleanEnum;
import com.fido.framework.core.constants.Constants;
import com.fido.framework.core.mapper.BaseMapper;
import com.fido.framework.core.model.db.BaseModel;
import com.fido.framework.core.utils.LangUtils;
import com.fido.framework.core.utils.ModelUtil;
import lombok.Getter;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Example;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import static com.fido.framework.core.constants.Constants.*;

/**
 * @author: yibingzhou
 */
public class BaseDao<T extends BaseModel,M extends BaseMapper<T>> {

    @Autowired
    @Getter
    @JsonIgnore
    protected M baseMapper;

    public T queryByPk(Object pk) {
        return baseMapper.selectByPrimaryKey(pk);
    }

    public List<T> queryByExample(Example example) {
        return baseMapper.selectByExample(example);
    }

    public int logicDeleteByExample(T param, Example example) {
        param.setLogicDelete(BooleanEnum.TRUE.getCode());
        return baseMapper.updateByExampleSelective(param,example);
    }

    public int deleteByPk(Object pk) {
        return baseMapper.deleteByPrimaryKey(pk);
    }

    public boolean exitWithPk(Object pk) {
        return baseMapper.existsWithPrimaryKey(pk);
    }

    public boolean exitWithExample(Example example) {
        return Constants.NO_AFFECT_ROWS != baseMapper.selectCountByExample(example);
    }

    public int deleteByExample(Example example) {
        return baseMapper.deleteByExample(example);
    }

    public T queryOne(T param) {
        return baseMapper.selectOne(param);
    }

    public List<T> queryList(T param) {
        return baseMapper.select(param);
    }

    public PageInfo<T> queryPage(T param, Integer pageNum, Integer pageSize) {
        pageNum = (pageNum == null || pageNum.intValue() < 0) ? START_PAGE_NUM : pageNum;
        pageSize = (pageSize == null || pageSize.intValue() < 0) ? DEFAULT_PAGE_SIZE : pageSize;
        PageMethod.startPage(pageNum, pageSize, true, false, null);
        List<T> results = baseMapper.select(param);
        return new PageInfo<>(results);
    }

    public PageInfo<T> queryPageByExample(Example example, Integer pageNum, Integer pageSize) {
        pageNum = (pageNum == null || pageNum.intValue() < 0) ? START_PAGE_NUM : pageNum;
        pageSize = (pageSize == null || pageSize.intValue() < 0) ? DEFAULT_PAGE_SIZE : pageSize;
        PageMethod.startPage(pageNum, pageSize, true, false, null);
        List<T> results = baseMapper.selectByExample(example);
        return new PageInfo<>(results);
    }

    public Integer updateByExample(T param, Example example) {
        return baseMapper.updateByExample(param, example);
    }

    public Integer updateByExampleSelective(T param, Example example) {
        return baseMapper.updateByExampleSelective(param, example);
    }

    public Integer insertSelective(T param) {
        return baseMapper.insertSelective(param);
    }

    public Integer insertList(List<T> params) {
        return baseMapper.insertList(params);
    }

    public Integer insert(T param) {
        return baseMapper.insert(param);
    }

    /**
     * 查询指定字段
     * @param record 参数
     * @param properties 字段属性
     * @return
     */
    public List<T> queryPropList(T record, String... properties) {
        Example example = new Example(record.getClass());
        example.selectProperties(properties);

        Map<String, Object> paramMap = Maps.newHashMap();
        BeanUtil.beanToMap(record, paramMap, false, true);
        Example.Criteria criteria = example.createCriteria();
        for (Map.Entry<String, Object> entry : paramMap.entrySet()) {
            criteria.andEqualTo(entry.getKey(), entry.getValue());
        }
        List<T> list = baseMapper.selectByExample(example);
        return list;
    }

    /**
     * 查询指定字段，返回字段类型集合
     * @param record 参数
     * @param property 字段属性
     * @param clazz 字段属性类型
     * @return
     */
    @SuppressWarnings("unchecked")
    public <P> List<P> queryPropList(T record, String property,Class<P> clazz) {
        List<T> list = queryPropList(record, property);
        List<P> fieldList = Lists.newArrayList();
        for (T t : list) {
            P value = (P)BeanUtil.getProperty(t, property);
            fieldList.add(value);
        }
        return fieldList;
    }

    /**
     * 查询公共字段属性(属性名与类型一致)，返回指定类型集合
     * @param record 参数
     * @param clazz 返回类型，与参数相同的字段属性将被赋值
     * @return
     */
    public <P> List<P> queryPropList(T record, Class<P> clazz) {
        List<Field> fieldList1 = ModelUtil.listValidModelFields(record.getClass());
        List<Field> fieldList2 = ModelUtil.listValidModelFields(clazz);
        List<String> propList = Lists.newArrayList();
        for (Field field1 : fieldList1) {
            for (Field field2 : fieldList2) {
                if (LangUtils.eq(field1.getName(), field2.getName()) && field1.getType() == field2.getType()) {
                    propList.add(field1.getName());
                    break;
                }
            }
        }
        String[] properties = new String[propList.size()];
        propList.toArray(properties);

        List<T> list = queryPropList(record, properties);
        List<P> retList = Lists.newArrayList();
        for (T obj : list) {
            P p = BeanUtils.instantiateClass(clazz);
            BeanUtil.copyProperties(obj, p, CopyOptions.create().setIgnoreNullValue(true));
            retList.add(p);
        }
        return retList;
    }

    /**
     * 查询指定字段
     * @param example
     * @param properties
     * @return
     */
    public List<T> queryPropByExample(Example example, String... properties) {
        example.selectProperties(properties);
        List<T> list = baseMapper.selectByExample(example);
        return list;
    }

    /**
     * 查询指定字段，返回字段类型集合
     * @param example
     * @param property 字段属性
     * @param clazz 字段属性类型
     * @return
     */
    @SuppressWarnings("unchecked")
    public <P> List<P> queryPropByExample(Example example, String property,Class<P> clazz) {
        List<T> list = queryPropByExample(example, property);
        List<P> fieldList = Lists.newArrayList();
        for (T t : list) {
            P value = (P)BeanUtil.getProperty(t, property);
            fieldList.add(value);
        }
        return fieldList;
    }
}
