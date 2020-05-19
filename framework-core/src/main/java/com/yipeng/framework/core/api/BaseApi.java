package com.yipeng.framework.core.api;

import com.github.pagehelper.PageInfo;
import com.yipeng.framework.core.model.biz.BaseParam;
import com.yipeng.framework.core.model.biz.ResultOverview;
import com.yipeng.framework.core.model.biz.ValidList;
import com.yipeng.framework.core.param.PageParam;
import com.yipeng.framework.core.result.Result;
import io.swagger.models.auth.In;

import java.util.List;

/**
 * 基本接口
 * CRUD
 * @author: yibingzhou
 */
public interface BaseApi<P extends BaseParam, R>{

    /**
     *  不存在则插入
     * @param param
     * @return
     */
    Result<Boolean> createIfAbsent(P param);
    Result<Boolean> create(P param);

    Result<Boolean> save(P param);
    Result<Integer> saveList(ValidList<P> params);

    Result<Integer> delete(P param);
    <K extends Comparable> Result<Boolean> deleteByPk(K param);
    <K extends Comparable> Result<Integer> deleteByPkList(List<K> param);

    Result<Integer> logicDelete(P param);
    <K extends Comparable> Result<Boolean> logicDeleteByPk(K param);
    <K extends Comparable> Result<Integer> logicDeleteByPkList(List<K> param);

    Result<Boolean> update(P param);

    /**
     * 批量更新记录
     * 底层只能一次更新一条记录
     * @param param
     * @return
     */
    Result<ResultOverview> updateList(ValidList<P> param);

    <K extends Comparable> Result<R> getByPk(K pk);

    <K extends Comparable> Result<R> getByPkList(List<K> pks);

    Result<R> get(P param);

    PageInfo<R> page(PageParam<P> pageParam);
}
