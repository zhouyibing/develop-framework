package com.yipeng.framework.common.service;

import com.yipeng.framework.common.service.converter.ModelResultConverter;
import com.yipeng.framework.common.dao.BaseDao;
import com.yipeng.framework.common.model.BaseModel;

import java.util.Map;

/**
 * @author: yibingzhou
 */
public class MapParamService<R,M extends BaseModel,T extends BaseDao> extends AbstractBaseService<R, M, Map<String,Object>, T>{
    public MapParamService(T dao, ModelResultConverter modelResultConverter, Class<R> resultClass, Class<M> modelClass) {
        super(dao, modelResultConverter, resultClass, modelClass);
    }
}
