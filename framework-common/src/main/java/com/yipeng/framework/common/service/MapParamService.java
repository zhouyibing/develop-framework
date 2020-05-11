package com.yipeng.framework.common.service;

import com.yipeng.framework.common.service.converter.ModelResultConverter;
import com.yipeng.framework.common.dao.BaseDao;
import com.yipeng.framework.common.model.db.BaseModel;

import java.util.Map;

/**
 * @author: yibingzhou
 */
public class MapParamService<M extends BaseModel,T extends BaseDao> extends AbstractBaseService<M, Map<String,Object>, T>{
    public MapParamService(T dao, ModelResultConverter modelResultConverter,Class<M> modelClass) {
        super(dao, modelResultConverter, modelClass);
    }
}
