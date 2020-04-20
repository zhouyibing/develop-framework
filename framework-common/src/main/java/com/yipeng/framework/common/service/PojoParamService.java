package com.yipeng.framework.common.service;

import com.yipeng.framework.common.service.converter.ModelResultConverter;
import com.yipeng.framework.common.dao.BaseDao;
import com.yipeng.framework.common.model.AccessObject;
import com.yipeng.framework.common.model.BaseModel;

public class PojoParamService <M extends BaseModel, T extends BaseDao> extends AbstractBaseService<M, AccessObject, T> {
    public PojoParamService(T dao, ModelResultConverter modelResultConverter, Class<M> modelClass) {
        super(dao, modelResultConverter, modelClass);
    }
}
