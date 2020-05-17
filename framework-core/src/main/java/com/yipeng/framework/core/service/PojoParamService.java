package com.yipeng.framework.core.service;

import com.yipeng.framework.core.service.converter.ModelResultConverter;
import com.yipeng.framework.core.dao.BaseDao;
import com.yipeng.framework.core.model.db.AccessObject;
import com.yipeng.framework.core.model.db.BaseModel;

public class PojoParamService <M extends BaseModel, T extends BaseDao> extends AbstractBaseService<M, AccessObject, T> {
    public PojoParamService(T dao, ModelResultConverter modelResultConverter, Class<M> modelClass) {
        super(dao, modelResultConverter, modelClass);
    }
}
