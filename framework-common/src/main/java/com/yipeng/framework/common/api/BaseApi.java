package com.yipeng.framework.common.api;

import com.github.pagehelper.PageInfo;
import com.yipeng.framework.common.model.BaseParam;
import com.yipeng.framework.common.model.ValidList;
import com.yipeng.framework.common.param.PageParam;
import com.yipeng.framework.common.result.Result;
import java.util.List;

/**
 * 基本接口
 * CRUD
 * @author: yibingzhou
 */
public interface BaseApi{

    /**
     *  不存在则插入
     * @param param
     * @return
     */
    <P extends BaseParam> Result createIfAbsent(P param);
    <P extends BaseParam> Result create(P param);

    <P extends BaseParam> Result save(P param);
    <P extends BaseParam> Result saveList(ValidList<P> params);

    <P extends BaseParam> Result delete(P param);
    <P extends BaseParam> Result deleteByPk(P param);
    <P extends BaseParam> Result deleteByPkList(List<P> param);

    <P extends BaseParam> Result logicDelete(P param);
    <P extends BaseParam> Result logicDeleteByPk(P param);
    <P extends BaseParam> Result logicDeleteByPkList(List<P> param);

    <P extends BaseParam> Result update(P param);

    /**
     * 批量更新记录
     * 底层只能一次更新一条记录
     * @param param
     * @return
     */
    <P extends BaseParam> Result updateList(ValidList<P> param);

    <P extends BaseParam> Result getByPk(P param);

    <P extends BaseParam> Result getByPkList(List<P> id);

    <P extends BaseParam> Result get(P param);

    <P extends BaseParam> PageInfo page(PageParam<P> pageParam);
}
