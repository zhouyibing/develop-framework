package com.yipeng.framework.common.web.controller;

import cn.hutool.core.collection.CollectionUtil;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Sets;
import com.yipeng.framework.common.api.BaseApi;
import com.yipeng.framework.common.exception.ErrorCode;
import com.yipeng.framework.common.exception.ExceptionUtil;
import com.yipeng.framework.common.model.BaseParam;
import com.yipeng.framework.common.model.ResultOverview;
import com.yipeng.framework.common.model.ValidList;
import com.yipeng.framework.common.param.PageParam;
import com.yipeng.framework.common.result.Result;
import com.yipeng.framework.common.model.Intensifier;
import com.yipeng.framework.common.service.BaseService;
import com.yipeng.framework.common.utils.Precondition;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.validation.Valid;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

/**
 * @author: yibingzhou
 */
public abstract class BaseController<P extends BaseParam, S extends BaseService> implements BaseApi<P> {
    private final static String HEADER_TOKEN = "token";
    protected final static String ALL = "*";

    @Autowired
    protected S service;

    protected Map<String, Set<Intensifier>> intensifierMap = new HashMap<>();
    private Comparator<Intensifier> intensifierComparable = (o1, o2) -> {
        if(o1.getPriority() == o2.getPriority()) return 0;
        return o1.getPriority() > o2.getPriority() ? -1 : 1;
    };

    protected void addIntensifier(Intensifier intensifier) {
        if(intensifier == null) return;
        Set<Intensifier> intensifiers = intensifierMap.get(intensifier.getName());
        if(null == intensifiers) {
            intensifiers = Sets.newTreeSet(intensifierComparable);
            intensifierMap.put(intensifier.getName(), intensifiers);
        }
        intensifiers.add(intensifier);
    }

    protected abstract Class defaultResultClass();
    /**
     * 从header里获取token
     * @return
     */
    public String getAccessToken() {
        String token = getHeader(HEADER_TOKEN);
        if (StringUtils.isEmpty(token)) {
            ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            token = sra.getRequest().getParameter(HEADER_TOKEN);
        }
        return token;
    }

    /**
     * 从请求里获取指定名称的header
     * @return
     */
    public String getHeader(String name) {
        ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        String value = sra.getRequest().getHeader(name);
        return value;
    }

    @Override
    @PostMapping("/createIfAbsent")
    @ApiOperation("不存在时创建")
    public Result createIfAbsent(@Valid @RequestBody P param) {
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        return enhance(methodName, param, (p) -> service.createIfAbsent(p,p));
    }

    @Override
    @PostMapping("/create")
    @ApiOperation("创建")
    public Result create(@Valid @RequestBody P param) {
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        return enhance(methodName, param, (p) -> service.create(p));
    }

    @Override
    @PostMapping("/save")
    @ApiOperation("保存")
    public Result save(@Valid @RequestBody P param) {
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        return enhance(methodName, param, (p) -> service.save(p));
    }

    protected  <T, R> Result enhance(String intensifierName, T param, Function<T, R> main) {
        Object ret = enhanceNoResultWrapped(intensifierName, param, main);
        return Result.success(ret);
    }
    protected <T,R> Object enhanceNoResultWrapped(String intensifierName, T param, Function<T, R> main) {
        //核心逻辑调用前的处理
        Set<Intensifier> myIntensifiers = intensifierMap.get(intensifierName);
        Set<Intensifier> allIntensifiers = intensifierMap.get(ALL);
        Set<Intensifier> intensifiers = Sets.newTreeSet(intensifierComparable);
        if(allIntensifiers == null) intensifiers.addAll(allIntensifiers);
        if(myIntensifiers != null) intensifiers.addAll(myIntensifiers);
        if(CollectionUtil.isNotEmpty(intensifiers)) {
            for(Intensifier intensifier: intensifiers) {
                Object ret = intensifier.getBefore() == null ? null : intensifier.getBefore().apply(param);
                if(ret != null && intensifier.isUseBeforeEnhanceResult()) {
                    if(!param.getClass().isAssignableFrom(ret.getClass())) throw ExceptionUtil.doThrow(ErrorCode.PARAM_TYPE_NOT_MATCH.msg("前置处理返回结果类型必须是传入参数类型同类或子类"));
                    param = (T) ret;
                }
            }
        }
        R res = main.apply(param);
        //核心逻辑调用后的处理
        if(CollectionUtil.isNotEmpty(allIntensifiers)) {
            for(Intensifier intensifier: allIntensifiers) {
                Object ret = intensifier.getAfter() == null ? null : intensifier.getAfter().apply(param);
                if(ret != null && intensifier.isUseBeforeEnhanceResult()) {
                    param = (T) ret;
                }
            }
        }
        return res;
    }

    @Override
    @PostMapping("/saveList")
    @ApiOperation("批量保存")
    public Result saveList(@Valid @RequestBody ValidList<P> params) {
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        return enhance(methodName, params, (p) -> service.save(p));
    }

    @Override
    @PostMapping("/delete")
    @ApiOperation("删除")
    public Result delete(@Valid @RequestBody P param) {
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        return enhance(methodName, param, (p) -> service.deleteAllMatch(p));
    }

    @Override
    @GetMapping("/deleteById")
    @ApiOperation("根据id删除")
    public Result deleteById(@ApiParam(name = "id", value = "id", example = "1", required = true) @RequestParam Long id) {
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        return enhance(methodName, id, (p) -> service.deleteById(p));
    }

    @Override
    @PostMapping("/deleteByIdList")
    @ApiOperation("批量根据id删除")
    public Result deleteByIdList(@ApiParam(name = "id", value = "id列表", required = true) @RequestBody List<Long> ids) {
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        return enhance(methodName, ids, (p) -> service.deleteByIds(p));
    }

    @Override
    @PostMapping("/logicDelete")
    @ApiOperation("逻辑删除")
    public Result logicDelete(@RequestBody P param) {
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        return enhance(methodName, param, (p) -> service.logicDeleteAllMatch(p));
    }

    @Override
    @GetMapping("/logicDeleteById")
    @ApiOperation("根据id逻辑删除")
    public Result logicDeleteById(@ApiParam(name = "id", value = "id", example = "1", required = true) @RequestParam Long id) {
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        return enhance(methodName, id, (p) -> service.logicDeleteById(p));
    }

    @Override
    @PostMapping("/logicDeleteByIdList")
    @ApiOperation("批量根据id逻辑删除")
    public Result logicDeleteByIdList(@ApiParam(name = "id", value = "id列表", required = true) @RequestBody List<Long> ids) {
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        return enhance(methodName, ids, (p) -> service.logicDeleteByIds(p));
    }

    @Override
    @PostMapping("/update")
    @ApiOperation("更新")
    public Result update(@Valid @RequestBody P param) {
        Precondition.checkNotNull(param.getId(),"id不能为空");
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        return enhance(methodName, param, (p) -> service.update(p.getId(), p));
    }

    @Override
    @PostMapping("/updateList")
    @ApiOperation("批量更新")
    public Result updateList(@Valid @RequestBody ValidList<P> param) {
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        return enhance(methodName, param, (p) -> {
            AtomicInteger failed = new AtomicInteger(0);
            AtomicInteger success = new AtomicInteger(0);
            p.forEach( item -> {
                Precondition.checkNotNull(item.getId(),"id不能为空");
                if(service.update(item.getId(), item)){
                    success.incrementAndGet();
                } else {
                    failed.incrementAndGet();
                }
            });
            ResultOverview resultOverview = new ResultOverview();
            resultOverview.setFail(failed.get());
            resultOverview.setSuccess(success.get());
            return resultOverview;
        });
    }

    @Override
    @GetMapping("/getById")
    @ApiOperation("根据id查询")
    public Result getById(@ApiParam(name = "id", value = "id",example = "123",required = true) @RequestParam Long id) {
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        return enhance(methodName, id, (p) -> service.queryById(p, defaultResultClass()));
    }

    @Override
    @PostMapping("/getByIds")
    @ApiOperation("批量根据id查询")
    public Result getByIds(@ApiParam(name = "id", value = "id列表", required = true) @RequestBody List<Long> ids) {
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        return enhance(methodName, ids, (p) -> service.queryByIds(p,defaultResultClass()));
    }

    @Override
    @PostMapping("/get")
    @ApiOperation("条件查询")
    public Result get(@Valid @RequestBody P param) {
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        return enhance(methodName, param, (p) -> service.queryAllMatch(p,defaultResultClass()));
    }

    @Override
    @PostMapping("/page")
    @ApiOperation("分页查询")
    public PageInfo page(@Valid @RequestBody PageParam<P> pageParam) {
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        return (PageInfo) enhanceNoResultWrapped(methodName, pageParam, (p) -> service.pageAllMatch(p.getParams(),p.getOrderBy(), p.getOrderType(), p.getCurrent(), p.getPageSize(),defaultResultClass()));
    }
}
