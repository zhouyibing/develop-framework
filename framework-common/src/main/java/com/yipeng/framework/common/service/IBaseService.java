package com.yipeng.framework.common.service;

import cn.hutool.db.sql.Direction;
import com.github.pagehelper.PageInfo;
import com.yipeng.framework.common.dao.BaseDao;
import com.yipeng.framework.common.exception.ExceptionUtil;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * @author: yibingzhou
 * 方法返回规则：如果确定仅影响一条记录，方法返回boolean,否则返回影响行数
 * 查询是否存在返回boolean
 */
public interface IBaseService<R, T> {

    <D extends BaseDao> D getDao();

    R queryById(Long id);
    List<R> queryByIds(List<Long> ids);
    boolean logicDeleteById(Long id);
    Integer logicDeleteById(List<Long> ids);
    boolean deleteById(Long id);
    Integer deleteById(List<Long> ids);

    /**
     * 创建记录
     * @param create
     */
    boolean create(T create);

    /**
     * 批量创建记录，不做selective判断,交于baseMapper执行
     * @param list
     * @return
     */
    Integer create(List list);

    /**
     * 不存在时创建记录
     * @param query 查询参数
     * @param create 待创建记录
     */
    boolean createIfAbsent(T query, T create) throws ExceptionUtil.BizException;

    /**
     * 不存在时批量创建记录，不做selective判断,交于baseMapper执行
     * @param example
     * @param create
     * @return
     * @throws ExceptionUtil.BizException
     */
    Integer createIfAbsent(Example example, List create) throws ExceptionUtil.BizException;

    /**
     * 根据id保存/更新记录
     * @param param
     */
    boolean save(T param);
    Integer save(List param);
    /**
     * 更新记录
     * @param query 查询参数
     * @param update 更新参数
     * @return
     */
    Integer updateAllMatch(T query, T update);
    Integer updateAnyMatch(T query, T update);
    Integer updateByExample(T update, Example example);

    /**
     * 根据id更新记录
     * @param update
     * @return
     */
    boolean update(Long id, T update);

    List<R> queryAllMatch(T param);
    List<R> queryAnyMatch(T param);
    List<R> queryByExample(Example example);

    Example buildAllMatch(T param);
    Example buildAnyMatch(T param);

    Integer deleteAllMatch(T param);
    Integer deleteAnyMatch(T param);
    Integer deleteByExample(Example example);

    Integer logicDeleteAllMatch(T param);
    Integer logicDeleteAnyMatch(T param);
    Integer logicDeleteByExample(Example example);


    boolean existAllMatch(T param);
    boolean existAnyMatch(T param);
    boolean existByExample(Example example);

    PageInfo<R> pageAllMatch(T param, Integer pageNum, Integer pageSize);
    PageInfo<R> pageAllMatch(T param, String orderField, Integer pageNum, Integer pageSize);
    PageInfo<R> pageAllMatch(T param, String orderField, Direction direction, Integer pageNum, Integer pageSize);
    PageInfo<R> pageAnyMatch(T param, Integer pageNum, Integer pageSize);
    PageInfo<R> pageAnyMatch(T param, String orderField, Integer pageNum, Integer pageSize);
    PageInfo<R> pageAnyMatch(T param, String orderField, Direction direction, Integer pageNum, Integer pageSize);
    PageInfo<R> pageByExample(Example example, Integer pageNum, Integer pageSize);
}
