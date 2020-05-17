package com.yipeng.framework.core.mapper;

import tk.mybatis.mapper.common.ConditionMapper;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

/**
 * @author: yibingzhou
 */
public interface BaseMapper<T> extends Mapper<T>, ConditionMapper<T>, MySqlMapper<T> {

}
