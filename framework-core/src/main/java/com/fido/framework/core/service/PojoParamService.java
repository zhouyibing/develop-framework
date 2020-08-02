package com.fido.framework.core.service;

import com.fido.framework.core.service.converter.ModelResultConverter;
import com.fido.framework.core.dao.BaseDao;
import com.fido.framework.core.model.db.AccessObject;
import com.fido.framework.core.model.db.BaseModel;

public class PojoParamService <M extends BaseModel, T extends BaseDao> extends AbstractBaseService<M, AccessObject, T> {
    public PojoParamService(T dao, ModelResultConverter modelResultConverter, Class<M> modelClass) {
        super(dao, modelResultConverter, modelClass);
    }
}
