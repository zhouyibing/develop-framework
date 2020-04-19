package com.yipeng.framework.common.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.db.sql.Direction;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.yipeng.framework.common.constants.Constants;
import com.yipeng.framework.common.service.converter.ModelResultConverter;
import com.yipeng.framework.common.dao.BaseDao;
import com.yipeng.framework.common.exception.ErrorCode;
import com.yipeng.framework.common.exception.ExceptionUtil;
import com.yipeng.framework.common.model.BaseModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import tk.mybatis.mapper.entity.Example;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * service封装抽象
 * @author: yibingzhou
 */
@Slf4j
public class AbstractBaseService <R,M extends BaseModel,P,T extends BaseDao> implements IBaseService<R,P>{
    protected T dao;
    protected ModelResultConverter modelResultConverter;
    protected Class<R> resultClass;
    protected Class<M> modelClass;


    public AbstractBaseService(T dao, ModelResultConverter modelResultConverter, Class<R> resultClass, Class<M> modelClass) {
        this.dao = dao;
        this.modelResultConverter = modelResultConverter;
        this.resultClass = resultClass;
        this.modelClass = modelClass;
    }

    @Override
    public <D extends BaseDao> D getDao() {
        return (D) dao;
    }

    public R queryById(Long id) {
        BaseModel result = dao.queryById(id);
        if(null == result) return null;
        return convert((M) result);
    }

    public List<R> queryByIds(List<Long> ids) {
        Example example = new Example(modelClass);
        example.createCriteria().andIn("id", ids);
        return queryByExample(example);
    }

    /**
     * 根据id逻辑删除记录
     * @param id
     * @return
     */
    public boolean logicDeleteById(Long id) {
        M model = newModelInstance();
        model.setId(id);
        return Constants.DEFAULT_AFFECT_ROWS == dao.logicDeleteByPk(model);
    }

    @Override
    public Integer logicDeleteById(List<Long> ids) {
        Example example = new Example(modelClass);
        example.createCriteria().andIn("id", ids);
        return dao.logicDeleteByExample(newModelInstance(), example);
    }

    /**
     * 根据id删除记录
     * @param id
     * @return
     */
    public boolean deleteById(Long id) {
        return Constants.DEFAULT_AFFECT_ROWS == dao.deleteByPk(id);
    }

    @Override
    public Integer deleteById(List<Long> ids) {
        Example example = new Example(modelClass);
        example.createCriteria().andIn("id", ids);
        return dao.deleteByExample(example);
    }

    @Override
    public boolean create(P param) {
        M model = toModel(param);
        Boolean selective = model.getSelective();
        if(selective != null && selective) {
            return Constants.DEFAULT_AFFECT_ROWS == dao.insertSelective(model);
        } else {
            return Constants.DEFAULT_AFFECT_ROWS == dao.insert(model);
        }
    }

    @Override
    public Integer create(List list) {
        if(CollectionUtil.isEmpty(list)) return Constants.NO_AFFECT_ROWS;
        List<M> models = Lists.newArrayList();
        list.forEach(item -> {
            M model = toModel(item);
            models.add(model);
        });
        return dao.insertList(list);
    }

    @Override
    public boolean createIfAbsent(P query, P create) throws ExceptionUtil.BizException {
        if(existAllMatch(query)){
            throw ExceptionUtil.doThrow(ErrorCode.RECORD_EXISTED);
        } else {
            return create(create);
        }
    }

    @Override
    public Integer createIfAbsent(Example example, List create) throws ExceptionUtil.BizException {
        if(!existByExample(example)) {
            return create(create);
        }
        return Constants.NO_AFFECT_ROWS;
    }

    @Override
    public boolean save(P param) {
        M model = toModel(param);
        Boolean selective = model.getSelective();
        if (model.getId() == null) {
            if(selective != null && selective){
                return Constants.DEFAULT_AFFECT_ROWS == dao.insertSelective(model);
            }
            return Constants.DEFAULT_AFFECT_ROWS == dao.insert(model);
        }
        if(selective != null && selective){
            return Constants.DEFAULT_AFFECT_ROWS ==  dao.updateByPkSelective(model);
        }
        return Constants.DEFAULT_AFFECT_ROWS == dao.updateByPk(model);
    }

    @Override
    public Integer save(List param) {
        if(CollectionUtil.isEmpty(param)) return Constants.NO_AFFECT_ROWS;
        List<M> noIdModels = Lists.newArrayList();
        AtomicInteger affectRows = new AtomicInteger(Constants.NO_AFFECT_ROWS);
        param.forEach(item -> {
            M model = toModel(item);
            if(model.getId() == null){
                Boolean selective = model.getSelective();
                if(selective != null && selective){
                    affectRows.addAndGet(dao.insertSelective(model));
                }else {
                    affectRows.addAndGet(dao.insert(model));
                }
            }else {
                noIdModels.add(model);
            }
        });
        if(noIdModels.size() >0 ){
            affectRows.addAndGet(dao.insertList(noIdModels));
        }
        return affectRows.get();
    }

    @Override
    public Integer updateAllMatch(P query, P update) {
        Example example = buildAllMatch(query);
        return updateByExample(update, example);
    }

    @Override
    public Integer updateAnyMatch(P query, P update) {
        Example example = buildAnyMatch(query);
        return updateByExample(update, example);
    }

    @Override
    public Integer updateByExample(P update, Example example) {
        if(example == null) {
            return Constants.NO_AFFECT_ROWS;
        }
        M model = toModel(update);
        Boolean selective = model.getSelective();
        if(selective != null && selective){
            model.setLogicDelete(null);//不更新逻辑删除字段。
            return dao.updateByExampleSelective(model, example);
        }
        return dao.updateByExample(model, example);
    }

    @Override
    public boolean update(Long id, P update) {
        M model = toModel(update);
        Boolean selective = model.getSelective();
        model.setId(id);
        if(selective != null && selective){
            model.setLogicDelete(null);//不更新逻辑删除字段。
            return Constants.DEFAULT_AFFECT_ROWS == dao.updateByPkSelective(model);
        }
        return Constants.DEFAULT_AFFECT_ROWS == dao.updateByPk(model);
    }

    @Override
    public List<R> queryAllMatch(P param) {
        Example example = buildAllMatch(param);
        return queryByExample(example);
    }

    @Override
    public List<R> queryAnyMatch(P param) {
        Example example = buildAnyMatch(param);
        return queryByExample(example);
    }

    public Example buildAllMatch(P param) {
        M model = toModel(param);
        return buildAllMatch(model);
    }

    public Example buildAnyMatch(P param) {
        M model = toModel(param);
        return buildAnyMatch(model);
    }

    @Override
    public Integer deleteAllMatch(P param) {
        Example example = buildAllMatch(param);
        return deleteByExample(example);
    }

    @Override
    public Integer deleteAnyMatch(P param) {
        Example example = buildAnyMatch(param);
        return deleteByExample(example);
    }

    @Override
    public boolean existAllMatch(P param) {
        Example example = buildAllMatch(param);
        return existByExample(example);
    }

    @Override
    public boolean existAnyMatch(P param) {
        Example example = buildAnyMatch(param);
        return existByExample(example);
    }

    @Override
    public PageInfo<R> pageAllMatch(P param, Integer pageNum, Integer pageSize) {
        Example example = buildAllMatch(param);
        return pageByExample(example, pageNum, pageSize);
    }

    @Override
    public PageInfo<R> pageAllMatch(P param, String orderField, Integer pageNum, Integer pageSize) {
        return pageAllMatch(param, orderField, Direction.ASC, pageNum, pageSize);
    }

    @Override
    public PageInfo<R> pageAllMatch(P param, String orderField, Direction direction, Integer pageNum, Integer pageSize) {
        Example example = buildAllMatch(param);
        if(null == example) {
            return null;
        }

        Example.OrderBy orderBy = example.orderBy(orderField);
        if(direction == Direction.DESC){
            orderBy.desc();
        } else if(direction == Direction.ASC) {
            orderBy.asc();
        }
        return pageByExample(example, pageNum, pageSize);
    }

    @Override
    public PageInfo<R> pageAnyMatch(P param, Integer pageNum, Integer pageSize) {
        Example example = buildAnyMatch(param);
        return pageByExample(example, pageNum, pageSize);
    }

    @Override
    public PageInfo<R> pageAnyMatch(P param, String orderField, Integer pageNum, Integer pageSize) {
        return pageAnyMatch(param, orderField, Direction.ASC, pageNum, pageSize);
    }

    @Override
    public PageInfo<R> pageAnyMatch(P param, String orderField, Direction direction, Integer pageNum, Integer pageSize) {
        Example example = buildAnyMatch(param);
        if(null == example) {
            return null;
        }

        Example.OrderBy orderBy = example.orderBy(orderField);
        if(direction == Direction.DESC){
            orderBy.desc();
        } else if(direction == Direction.ASC) {
            orderBy.asc();
        }
        return pageByExample(example, pageNum, pageSize);
    }

    private <K> M toModel(K param) {
        if(param instanceof BaseModel){
            return (M)param;
        }
        M model = newModelInstance();
        if(param instanceof Object[]) {
            modelResultConverter.toDbModel((Object[])param, model);
        } else if(param instanceof Map) {
            modelResultConverter.toDbModel((Map)param, model);
        } else {
            modelResultConverter.toDbModel(param, model);
        }
        return model;
    }

    private Example buildAllMatch(M model) {
        if(model == null) return null;
        List<Field> fields = modelResultConverter.getFields(modelClass);
        Example example = new Example(modelClass);
        Example.Criteria criteria = example.createCriteria();
        Boolean selective = model.getSelective();
        fields.forEach(field -> {
            if(Constants.SELECTIVE.equals(field.getName())) return;//过滤selective字段
            Object value = null;
            try {
                value = field.get(model);
            } catch (IllegalAccessException e) {
                throw ExceptionUtil.doThrow(ErrorCode.SERVER_INTERNAL_ERROR.msg(e.getMessage()));
            }
            if((selective == null || !selective.booleanValue()) && value == null) {
                criteria.andEqualTo(field.getName(), value);
            }
            if (null != value) {
                criteria.andEqualTo(field.getName(), value);
            }
        });
        return example;
    }

    private Example buildAnyMatch(M model) {
        if(model == null) return null;
        List<Field> fields = modelResultConverter.getFields(modelClass);
        Example example = new Example(modelClass);
        Example.Criteria criteria = example.createCriteria();
        Boolean selective = model.getSelective();
        fields.forEach(field -> {
            if(Constants.SELECTIVE.equals(field.getName())) return;//过滤selective字段
            Object value = null;
            try {
                value = field.get(model);
            } catch (IllegalAccessException e) {
                throw ExceptionUtil.doThrow(ErrorCode.SERVER_INTERNAL_ERROR.msg(e.getMessage()));
            }
            if((selective == null || !selective.booleanValue()) && value == null) {
                criteria.andEqualTo(field.getName(), value);
            }
            if (null != value) {
                criteria.orEqualTo(field.getName(), value);
            }
        });
        return example;
    }

    public List<R> queryByExample(Example example) {
        if(example == null) return Collections.emptyList();
        return convert(dao.queryByExample(example));
    }

    public Integer deleteByExample(Example example) {
        if(example == null) return Constants.NO_AFFECT_ROWS;
        return dao.deleteByExample(example);
    }

    @Override
    public Integer logicDeleteAllMatch(P param) {
        Example example = buildAllMatch(param);
        return logicDeleteByExample(example);
    }

    @Override
    public Integer logicDeleteAnyMatch(P param) {
        Example example = buildAnyMatch(param);
        return logicDeleteByExample(example);
    }

    @Override
    public Integer logicDeleteByExample(Example example) {
        if(example == null) return Constants.NO_AFFECT_ROWS;
        return dao.logicDeleteByExample(newModelInstance(), example);
    }

    public boolean existByExample(Example example) {
        if(null == example) throw ExceptionUtil.doThrow(ErrorCode.QUERY_PARAMS_IS_NULL);
        return dao.exitWithExample(example);
    }

    public PageInfo<R> pageByExample(Example example, Integer pageNum, Integer pageSize) {
        if(example == null) return null;
        PageInfo<M> result = dao.queryPageByExample(example, pageNum, pageSize);
        List<R> convertResult = convert(result.getList());
        PageInfo<R> ret = new PageInfo<>();
        result.setList(null);
        BeanUtils.copyProperties(result, ret);
        ret.setList(convertResult);
        return ret;
    }

    /**
     * 是否需要进行数据库实体对象转换
     * @return
     */
    protected boolean needConvert() {
        return true;
    }


    /**
     * 增加额外的转换忽略字段，默认的忽略字段定义在数据库实体类@Convert，@ConvertIgnore注解里
     * 把这个结果缓存起来
     * @return
     */
    protected Set<String> extraIgnoreFields() {
        // 如果不需要转换，则该方法不允许调用。
        if(!needConvert()) {
            throw new UnsupportedOperationException("if not need convert,don't invoke this.");
        }
        return null;
    }

    /**
     * 移除数据实体类被标识(@ConvertIgnore和@Convert定义的ignores)的忽略字段
     * 把这个结果缓存起来
     * @return
     */
    protected Set<String> notIgnoreFields() {
        // 如果不需要转换，则该方法不允许调用。
        if(!needConvert()) {
            throw new UnsupportedOperationException("if not need convert,don't invoke this.");
        }
        return null;
    }

    private List<R> convert(List<M> list) {
        if(CollectionUtil.isEmpty(list)) return Collections.emptyList();
        List<R> ret = Lists.newArrayList();
        for(M m : list) {
            R r = convert(m);
            ret.add(r);
        }
        return ret;
    }

    /**
     * 将model转换为需要的result
     * @param baseModel
     * @return
     */
    protected R convert(M baseModel) {
        if(!needConvert() && resultClass != modelClass){
            throw ExceptionUtil.doThrow(ErrorCode.ILLEGAL_ARGUMENT.msg("result class must the same as model class if not need convert"));
        }
        if(!needConvert()) {
            return (R)baseModel;
        } else {
            return toResult(baseModel);
        }
    }

    private R toResult(M baseModel) {
        R result = newResultInstance();
        modelResultConverter.toResult(baseModel, result, extraIgnoreFields(), notIgnoreFields());
        return result;
    }

    private M newModelInstance(){
        try {
            return modelClass.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private R newResultInstance(){
        try {
            return resultClass.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
