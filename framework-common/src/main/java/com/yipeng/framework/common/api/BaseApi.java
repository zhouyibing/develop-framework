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
public interface BaseApi<P extends BaseParam>{

    /**
     *  不存在则插入
     * @param param
     * @return
     */
    Result creatIfAbsent(P param);
    Result creat(P param);

    Result save(P param);
    Result saveList(ValidList<P> params);

    Result delete(P param);
    Result deleteById(Long id);
    Result deleteByIdList(List<Long> ids);

    Result logicDelete(P param);
    Result logicDeleteById(Long id);
    Result logicDeleteByIdList(List<Long> ids);

    Result update(P param);

    /**
     * 批量更新记录
     * 底层只能一次更新一条记录
     * @param param
     * @return
     */
    Result updateList(ValidList<P> param);

    Result getById(Long id);

    Result getByIds(List<Long> id);

    Result get(P param);

    PageInfo page(PageParam<P> pageParam);
}
