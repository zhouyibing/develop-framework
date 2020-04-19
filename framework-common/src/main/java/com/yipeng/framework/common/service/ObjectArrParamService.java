package com.yipeng.framework.common.service;

import com.yipeng.framework.common.service.converter.ModelResultConverter;
import com.yipeng.framework.common.dao.BaseDao;
import com.yipeng.framework.common.model.BaseModel;

/**
 * @author: yibingzhou
 */
public class ObjectArrParamService<R,M extends BaseModel,T extends BaseDao> extends AbstractBaseService<R, M, Object[], T>{
    public ObjectArrParamService(T dao, ModelResultConverter modelResultConverter, Class<R> resultClass, Class<M> modelClass) {
        super(dao, modelResultConverter, resultClass, modelClass);
    }
}
