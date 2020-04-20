package com.yipeng.framework.common.service;

import cn.hutool.db.sql.Direction;
import com.github.pagehelper.PageInfo;
import com.yipeng.framework.common.exception.ExceptionUtil;
import com.yipeng.framework.common.model.AccessObject;
import com.yipeng.framework.common.service.converter.ModelResultConverter;
import com.yipeng.framework.common.dao.BaseDao;
import com.yipeng.framework.common.model.BaseModel;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.PostConstruct;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BaseService <M extends BaseModel,T extends BaseDao>{

    @Autowired
    protected T dao;
    protected ModelResultConverter modelResultConverter = new ModelResultConverter();
    private Class<M> modelClass;

    public <D extends BaseDao> D getDao() {
        return pojoParamService.getDao();
    }

    public <N extends Number,R> R queryById(N id, Class<R> resultClass) {
        return pojoParamService.queryById(id, resultClass);
    }

    public <N extends Number,R> List<R> queryByIds(List<N> ids, Class<R> resultClass) {
        return pojoParamService.queryByIds(ids, resultClass);
    }

    public <N extends Number> boolean logicDeleteById(N id) {
        return pojoParamService.logicDeleteById(id);
    }

    public <N extends Number> Integer logicDeleteByIds(List<N> ids) {
        return pojoParamService.logicDeleteByIds(ids);
    }

    public <N extends Number> boolean deleteById(N id) {
        return pojoParamService.deleteById(id);
    }

    public <N extends Number> Integer deleteByIds(List<N> ids) {
        return pojoParamService.deleteByIds(ids);
    }

    public boolean create(AccessObject param) {
        return pojoParamService.create(param);
    }

    public Integer create(List list) {
        return pojoParamService.create(list);
    }

    public boolean createIfAbsent(AccessObject query, AccessObject create) throws ExceptionUtil.BizException {
        return pojoParamService.createIfAbsent(query, create);
    }

    public Integer createIfAbsent(Example example, List create) throws ExceptionUtil.BizException {
        return pojoParamService.createIfAbsent(example, create);
    }

    public boolean save(AccessObject param) {
        return pojoParamService.save(param);
    }

    public Integer save(List param) {
        return pojoParamService.save(param);
    }

    public Integer updateAllMatch(AccessObject query, AccessObject update) {
        return pojoParamService.updateAllMatch(query, update);
    }

    public Integer updateAnyMatch(AccessObject query, AccessObject update) {
        return pojoParamService.updateAnyMatch(query, update);
    }

    public Integer updateByExample(AccessObject update, Example example) {
        return pojoParamService.updateByExample(update, example);
    }

    public <N extends Number> boolean update(N id, AccessObject update) {
        return pojoParamService.update(id, update);
    }

    public <R> List<R> queryAllMatch(AccessObject param, Class<R> resultClass) {
        return pojoParamService.queryAllMatch(param, resultClass);
    }

    public <R> List<R> queryAnyMatch(AccessObject param, Class<R> resultClass) {
        return pojoParamService.queryAnyMatch(param, resultClass);
    }

    public Example buildAllMatch(AccessObject param) {
        return pojoParamService.buildAllMatch(param);
    }

    public Example buildAnyMatch(AccessObject param) {
        return pojoParamService.buildAnyMatch(param);
    }

    public Integer deleteAllMatch(AccessObject param) {
        return pojoParamService.deleteAllMatch(param);
    }

    public Integer deleteAnyMatch(AccessObject param) {
        return pojoParamService.deleteAnyMatch(param);
    }

    public boolean existAllMatch(AccessObject param) {
        return pojoParamService.existAllMatch(param);
    }

    public boolean existAnyMatch(AccessObject param) {
        return pojoParamService.existAnyMatch(param);
    }

    public <R> PageInfo<R> pageAllMatch(AccessObject param, Integer pageNum, Integer pageSize, Class<R> resultClass) {
        return pojoParamService.pageAllMatch(param, pageNum, pageSize, resultClass);
    }

    public <R> PageInfo<R> pageAllMatch(AccessObject param, String orderField, Integer pageNum, Integer pageSize, Class<R> resultClass) {
        return pojoParamService.pageAllMatch(param, orderField, pageNum, pageSize, resultClass);
    }

    public <R> PageInfo<R> pageAllMatch(AccessObject param, String orderField, Direction direction, Integer pageNum, Integer pageSize, Class<R> resultClass) {
        return pojoParamService.pageAllMatch(param, orderField, direction, pageNum, pageSize, resultClass);
    }

    public <R> PageInfo<R> pageAnyMatch(AccessObject param, Integer pageNum, Integer pageSize, Class<R> resultClass) {
        return pojoParamService.pageAnyMatch(param, pageNum, pageSize, resultClass);
    }

    public <R> PageInfo<R> pageAnyMatch(AccessObject param, String orderField, Integer pageNum, Integer pageSize, Class<R> resultClass) {
        return pojoParamService.pageAnyMatch(param, orderField, pageNum, pageSize, resultClass);
    }

    public <R> PageInfo<R> pageAnyMatch(AccessObject param, String orderField, Direction direction, Integer pageNum, Integer pageSize, Class<R> resultClass) {
        return pojoParamService.pageAnyMatch(param, orderField, direction, pageNum, pageSize, resultClass);
    }

    public <R> List<R> queryByExample(Example example, Class<R> resultClass) {
        return pojoParamService.queryByExample(example, resultClass);
    }

    public Integer deleteByExample(Example example) {
        return pojoParamService.deleteByExample(example);
    }

    public Integer logicDeleteAllMatch(AccessObject param) {
        return pojoParamService.logicDeleteAllMatch(param);
    }

    public Integer logicDeleteAnyMatch(AccessObject param) {
        return pojoParamService.logicDeleteAnyMatch(param);
    }

    public Integer logicDeleteByExample(Example example) {
        return pojoParamService.logicDeleteByExample(example);
    }

    public boolean existByExample(Example example) {
        return pojoParamService.existByExample(example);
    }

    public <R> PageInfo<R> pageByExample(Example example, Integer pageNum, Integer pageSize, Class<R> resultClass) {
        return pojoParamService.pageByExample(example, pageNum, pageSize, resultClass);
    }

    public boolean needConvert() {
        return pojoParamService.needConvert();
    }

    public Set<String> extraIgnoreFields() {
        return pojoParamService.extraIgnoreFields();
    }

    public Set<String> notIgnoreFields() {
        return pojoParamService.notIgnoreFields();
    }

    public <R> R convert(M baseModel, Class<R> resultClass) {
        return pojoParamService.convert(baseModel, resultClass);
    }

    public boolean create(Map<String, Object> param) {
        return mapParamService.create(param);
    }

    public boolean createIfAbsent(Map<String, Object> query, Map<String, Object> create) throws ExceptionUtil.BizException {
        return mapParamService.createIfAbsent(query, create);
    }

    public boolean save(Map<String, Object> param) {
        return mapParamService.save(param);
    }

    public Integer updateAllMatch(Map<String, Object> query, Map<String, Object> update) {
        return mapParamService.updateAllMatch(query, update);
    }

    public Integer updateAnyMatch(Map<String, Object> query, Map<String, Object> update) {
        return mapParamService.updateAnyMatch(query, update);
    }

    public Integer updateByExample(Map<String, Object> update, Example example) {
        return mapParamService.updateByExample(update, example);
    }

    public <N extends Number> boolean update(N id, Map<String, Object> update) {
        return mapParamService.update(id, update);
    }

    public <R> List<R> queryAllMatch(Map<String, Object> param, Class<R> resultClass) {
        return mapParamService.queryAllMatch(param, resultClass);
    }

    public <R> List<R> queryAnyMatch(Map<String, Object> param, Class<R> resultClass) {
        return mapParamService.queryAnyMatch(param, resultClass);
    }

    public Example buildAllMatch(Map<String, Object> param) {
        return mapParamService.buildAllMatch(param);
    }

    public Example buildAnyMatch(Map<String, Object> param) {
        return mapParamService.buildAnyMatch(param);
    }

    public Integer deleteAllMatch(Map<String, Object> param) {
        return mapParamService.deleteAllMatch(param);
    }

    public Integer deleteAnyMatch(Map<String, Object> param) {
        return mapParamService.deleteAnyMatch(param);
    }

    public boolean existAllMatch(Map<String, Object> param) {
        return mapParamService.existAllMatch(param);
    }

    public boolean existAnyMatch(Map<String, Object> param) {
        return mapParamService.existAnyMatch(param);
    }

    public <R> PageInfo<R> pageAllMatch(Map<String, Object> param, Integer pageNum, Integer pageSize, Class<R> resultClass) {
        return mapParamService.pageAllMatch(param, pageNum, pageSize, resultClass);
    }

    public <R> PageInfo<R> pageAllMatch(Map<String, Object> param, String orderField, Integer pageNum, Integer pageSize, Class<R> resultClass) {
        return mapParamService.pageAllMatch(param, orderField, pageNum, pageSize, resultClass);
    }

    public <R> PageInfo<R> pageAllMatch(Map<String, Object> param, String orderField, Direction direction, Integer pageNum, Integer pageSize, Class<R> resultClass) {
        return mapParamService.pageAllMatch(param, orderField, direction, pageNum, pageSize, resultClass);
    }

    public <R> PageInfo<R> pageAnyMatch(Map<String, Object> param, Integer pageNum, Integer pageSize, Class<R> resultClass) {
        return mapParamService.pageAnyMatch(param, pageNum, pageSize, resultClass);
    }

    public <R> PageInfo<R> pageAnyMatch(Map<String, Object> param, String orderField, Integer pageNum, Integer pageSize, Class<R> resultClass) {
        return mapParamService.pageAnyMatch(param, orderField, pageNum, pageSize, resultClass);
    }

    public <R> PageInfo<R> pageAnyMatch(Map<String, Object> param, String orderField, Direction direction, Integer pageNum, Integer pageSize, Class<R> resultClass) {
        return mapParamService.pageAnyMatch(param, orderField, direction, pageNum, pageSize, resultClass);
    }

    public Integer logicDeleteAllMatch(Map<String, Object> param) {
        return mapParamService.logicDeleteAllMatch(param);
    }

    public Integer logicDeleteAnyMatch(Map<String, Object> param) {
        return mapParamService.logicDeleteAnyMatch(param);
    }

    public boolean create(Object[] param) {
        return objectArrParamService.create(param);
    }

    public boolean createIfAbsent(Object[] query, Object[] create) throws ExceptionUtil.BizException {
        return objectArrParamService.createIfAbsent(query, create);
    }

    public boolean save(Object[] param) {
        return objectArrParamService.save(param);
    }

    public Integer updateAllMatch(Object[] query, Object[] update) {
        return objectArrParamService.updateAllMatch(query, update);
    }

    public Integer updateAnyMatch(Object[] query, Object[] update) {
        return objectArrParamService.updateAnyMatch(query, update);
    }

    public Integer updateByExample(Object[] update, Example example) {
        return objectArrParamService.updateByExample(update, example);
    }

    public <N extends Number> boolean update(N id, Object[] update) {
        return objectArrParamService.update(id, update);
    }

    public <R> List<R> queryAllMatch(Object[] param, Class<R> resultClass) {
        return objectArrParamService.queryAllMatch(param, resultClass);
    }

    public <R> List<R> queryAnyMatch(Object[] param, Class<R> resultClass) {
        return objectArrParamService.queryAnyMatch(param, resultClass);
    }

    public Example buildAllMatch(Object[] param) {
        return objectArrParamService.buildAllMatch(param);
    }

    public Example buildAnyMatch(Object[] param) {
        return objectArrParamService.buildAnyMatch(param);
    }

    public Integer deleteAllMatch(Object[] param) {
        return objectArrParamService.deleteAllMatch(param);
    }

    public Integer deleteAnyMatch(Object[] param) {
        return objectArrParamService.deleteAnyMatch(param);
    }

    public boolean existAllMatch(Object[] param) {
        return objectArrParamService.existAllMatch(param);
    }

    public boolean existAnyMatch(Object[] param) {
        return objectArrParamService.existAnyMatch(param);
    }

    public <R> PageInfo<R> pageAllMatch(Object[] param, Integer pageNum, Integer pageSize, Class<R> resultClass) {
        return objectArrParamService.pageAllMatch(param, pageNum, pageSize, resultClass);
    }

    public <R> PageInfo<R> pageAllMatch(Object[] param, String orderField, Integer pageNum, Integer pageSize, Class<R> resultClass) {
        return objectArrParamService.pageAllMatch(param, orderField, pageNum, pageSize, resultClass);
    }

    public <R> PageInfo<R> pageAllMatch(Object[] param, String orderField, Direction direction, Integer pageNum, Integer pageSize, Class<R> resultClass) {
        return objectArrParamService.pageAllMatch(param, orderField, direction, pageNum, pageSize, resultClass);
    }

    public <R> PageInfo<R> pageAnyMatch(Object[] param, Integer pageNum, Integer pageSize, Class<R> resultClass) {
        return objectArrParamService.pageAnyMatch(param, pageNum, pageSize, resultClass);
    }

    public <R> PageInfo<R> pageAnyMatch(Object[] param, String orderField, Integer pageNum, Integer pageSize, Class<R> resultClass) {
        return objectArrParamService.pageAnyMatch(param, orderField, pageNum, pageSize, resultClass);
    }

    public <R> PageInfo<R> pageAnyMatch(Object[] param, String orderField, Direction direction, Integer pageNum, Integer pageSize, Class<R> resultClass) {
        return objectArrParamService.pageAnyMatch(param, orderField, direction, pageNum, pageSize, resultClass);
    }

    public Integer logicDeleteAllMatch(Object[] param) {
        return objectArrParamService.logicDeleteAllMatch(param);
    }

    public Integer logicDeleteAnyMatch(Object[] param) {
        return objectArrParamService.logicDeleteAnyMatch(param);
    }

    private ObjectArrParamService<M,T> objectArrParamService;
    private MapParamService<M,T> mapParamService;
    private PojoParamService<M,T> pojoParamService;
    
    @PostConstruct
    public void init() {
        pojoParamService = new PojoParamService(dao, modelResultConverter, getModelClass());
        mapParamService = new MapParamService(dao, modelResultConverter, getModelClass());
        objectArrParamService = new ObjectArrParamService(dao, modelResultConverter, getModelClass());
    }

    /**
     * 获得model的class类型
     * @return
     */
    private synchronized Class<M> getModelClass() {
        if(modelClass != null ) return modelClass;
        try {
            ParameterizedType pt = (ParameterizedType) this.getClass().getGenericSuperclass();
            modelClass = (Class<M>) pt.getActualTypeArguments()[0];
            return modelClass;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
