package com.fido.framework.core.service;

import com.fido.framework.core.service.converter.ModelResultConverter;
import com.fido.framework.core.dao.BaseDao;
import com.fido.framework.core.model.db.BaseModel;

import java.util.Map;

/**
 * @author: yibingzhou
 */
public class MapParamService<M extends BaseModel,T extends BaseDao> extends AbstractBaseService<M, Map<String,Object>, T>{
    public MapParamService(T dao, ModelResultConverter modelResultConverter, Class<M> modelClass) {
        super(dao, modelResultConverter, modelClass);
    }
}
