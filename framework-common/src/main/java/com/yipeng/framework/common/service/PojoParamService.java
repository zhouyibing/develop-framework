package com.yipeng.framework.common.service;

import com.yipeng.framework.common.service.converter.ModelResultConverter;
import com.yipeng.framework.common.dao.BaseDao;
import com.yipeng.framework.common.model.AccessObject;
import com.yipeng.framework.common.model.BaseModel;

/**
 * @author: yibingzhou
 */
public class PojoParamService <R, M extends BaseModel, T extends BaseDao> extends AbstractBaseService<R, M, AccessObject, T> {
    public PojoParamService(T dao, ModelResultConverter modelResultConverter, Class<R> resultClass, Class<M> modelClass) {
        super(dao, modelResultConverter, resultClass, modelClass);
    }
}
