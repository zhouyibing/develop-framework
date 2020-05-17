package com.yipeng.framework.core.service;

import com.yipeng.framework.core.service.converter.ModelResultConverter;
import com.yipeng.framework.core.dao.BaseDao;
import com.yipeng.framework.core.model.db.BaseModel;

import java.util.Map;

/**
 * @author: yibingzhou
 */
public class MapParamService<M extends BaseModel,T extends BaseDao> extends AbstractBaseService<M, Map<String,Object>, T>{
    public MapParamService(T dao, ModelResultConverter modelResultConverter, Class<M> modelClass) {
        super(dao, modelResultConverter, modelClass);
    }
}
