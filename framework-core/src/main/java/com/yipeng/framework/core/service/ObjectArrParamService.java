package com.yipeng.framework.core.service;

import com.yipeng.framework.core.service.converter.ModelResultConverter;
import com.yipeng.framework.core.dao.BaseDao;
import com.yipeng.framework.core.model.db.BaseModel;

public class ObjectArrParamService<M extends BaseModel,T extends BaseDao> extends AbstractBaseService<M, Object[], T>{
    public ObjectArrParamService(T dao, ModelResultConverter modelResultConverter, Class<M> modelClass) {
        super(dao, modelResultConverter, modelClass);
    }
}
