package com.yipeng.framework.common.service;

import com.yipeng.framework.common.service.converter.ModelResultConverter;
import com.yipeng.framework.common.dao.BaseDao;
import com.yipeng.framework.common.model.BaseModel;

public class ObjectArrParamService<M extends BaseModel,T extends BaseDao> extends AbstractBaseService<M, Object[], T>{
    public ObjectArrParamService(T dao, ModelResultConverter modelResultConverter, Class<M> modelClass) {
        super(dao, modelResultConverter, modelClass);
    }
}
